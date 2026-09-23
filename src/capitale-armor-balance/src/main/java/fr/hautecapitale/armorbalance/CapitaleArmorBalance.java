package fr.hautecapitale.armorbalance;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.fabricmc.api.ModInitializer;

/**
 * First-pass ARMOR40 balancing layer.
 *
 * It doubles only additive generic ARMOR values on modded armor-slot items.
 * Vanilla items, toughness, knockback resistance, max health, spell attributes,
 * accessory/hand bonuses and multiplicative armor modifiers are untouched.
 *
 * Reflection is deliberate here: the module operates at the component boundary
 * and stays isolated from every armor mod's implementation classes.
 */
public final class CapitaleArmorBalance implements ModInitializer {
    public static final double ARMOR_SCALE = 2.0D;
    private static final String LOG = "[capitale_armor_balance] ";
    private static final Set<String> PROCESSED_ITEMS = ConcurrentHashMap.newKeySet();

    @Override
    public void onInitialize() {
        try {
            Class<?> eventsClass = Class.forName("net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents");
            Class<?> callbackClass = Class.forName("net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents$ModifyCallback");
            Object event = publicField(eventsClass, "MODIFY").get(null);

            Object callback = Proxy.newProxyInstance(
                    callbackClass.getClassLoader(),
                    new Class<?>[]{callbackClass},
                    (proxy, method, args) -> {
                        if (method.getDeclaringClass() == Object.class) {
                            return switch (method.getName()) {
                                case "toString" -> "CapitaleArmorBalanceModifyCallback";
                                case "hashCode" -> System.identityHashCode(proxy);
                                case "equals" -> proxy == args[0];
                                default -> null;
                            };
                        }
                        if (method.getName().equals("modify") && args != null && args.length == 1) {
                            installContext(args[0]);
                        }
                        return null;
                    });

            Method register = findMethod(event.getClass(), "register", 1);
            register.invoke(event, callback);
            System.out.println(LOG + "ARMOR40 x2 registered: modded armor ADD_VALUE only; vanilla/toughness/HP unchanged.");
        } catch (Throwable t) {
            System.err.println(LOG + "FAILED to register ARMOR40 x2: " + t);
            t.printStackTrace();
        }
    }

    private static void installContext(Object context) throws Exception {
        Method modify = null;
        for (Method m : context.getClass().getMethods()) {
            if (m.getName().equals("modify") && m.getParameterCount() == 2
                    && Predicate.class.isAssignableFrom(m.getParameterTypes()[0])
                    && BiConsumer.class.isAssignableFrom(m.getParameterTypes()[1])) {
                modify = m;
                break;
            }
        }
        if (modify == null) throw new NoSuchMethodException("DefaultItemComponentEvents.ModifyContext.modify(Predicate,BiConsumer)");
        modify.setAccessible(true);

        Predicate<Object> predicate = item -> {
            try {
                String id = itemId(item);
                return id != null && !id.startsWith("minecraft:");
            } catch (Throwable ignored) {
                return false;
            }
        };
        BiConsumer<Object, Object> consumer = (builder, item) -> {
            try {
                scaleArmorComponent(builder, item);
            } catch (Throwable t) {
                System.err.println(LOG + "Skipping " + safeItemId(item) + " after reflection error: " + t);
            }
        };
        modify.invoke(context, predicate, consumer);
    }

    private static void scaleArmorComponent(Object componentBuilder, Object item) throws Exception {
        String itemId = itemId(item);
        if (itemId == null || itemId.startsWith("minecraft:")) return;
        if (!PROCESSED_ITEMS.add(itemId)) return;

        Class<?> dataComponentTypes = Class.forName("net.minecraft.class_9334");
        Object attributeComponentType = declaredField(dataComponentTypes, "field_49636").get(null);

        Method buildMap = findMethod(componentBuilder.getClass(), "method_57838", 0);
        Object componentMap = buildMap.invoke(componentBuilder);
        Method getComponent = findCompatibleMethod(componentMap.getClass(), "method_58694", attributeComponentType);
        Object modifiersComponent = getComponent.invoke(componentMap, attributeComponentType);
        if (modifiersComponent == null) return;

        Method modifiersMethod = findMethod(modifiersComponent.getClass(), "comp_2393", 0);
        Object rawEntries = modifiersMethod.invoke(modifiersComponent);
        if (!(rawEntries instanceof List<?> entries) || entries.isEmpty()) return;

        Object armorAttribute = declaredField(Class.forName("net.minecraft.class_5134"), "field_23724").get(null);
        Object addValueOperation = declaredField(Class.forName("net.minecraft.class_1322$class_1323"), "field_6328").get(null);
        Set<Object> armorSlots = armorSlots();

        Class<?> registryEntryClass = Class.forName("net.minecraft.class_6880");
        Class<?> modifierClass = Class.forName("net.minecraft.class_1322");
        Class<?> modifierOpClass = Class.forName("net.minecraft.class_1322$class_1323");
        Class<?> identifierClass = Class.forName("net.minecraft.class_2960");
        Class<?> slotClass = Class.forName("net.minecraft.class_9274");
        Class<?> displayClass = Class.forName("net.minecraft.class_9285$class_11193");
        Class<?> entryClass = Class.forName("net.minecraft.class_9285$class_9287");
        Class<?> componentClass = Class.forName("net.minecraft.class_9285");

        Constructor<?> modifierCtor = modifierClass.getDeclaredConstructor(identifierClass, double.class, modifierOpClass);
        modifierCtor.setAccessible(true);
        Constructor<?> entryCtor = entryClass.getDeclaredConstructor(registryEntryClass, modifierClass, slotClass, displayClass);
        entryCtor.setAccessible(true);
        Constructor<?> componentCtor = componentClass.getDeclaredConstructor(List.class);
        componentCtor.setAccessible(true);

        ArrayList<Object> rebuilt = new ArrayList<>(entries.size());
        int changed = 0;
        ArrayList<String> changes = new ArrayList<>();
        for (Object entry : entries) {
            Object attribute = findMethod(entry.getClass(), "comp_2395", 0).invoke(entry);
            Object modifier = findMethod(entry.getClass(), "comp_2396", 0).invoke(entry);
            Object slot = findMethod(entry.getClass(), "comp_2397", 0).invoke(entry);
            Object display = findMethod(entry.getClass(), "comp_4036", 0).invoke(entry);
            Object operation = findMethod(modifier.getClass(), "comp_2450", 0).invoke(modifier);

            if (Objects.equals(attribute, armorAttribute)
                    && Objects.equals(operation, addValueOperation)
                    && armorSlots.contains(slot)) {
                Object id = findMethod(modifier.getClass(), "comp_2447", 0).invoke(modifier);
                double oldValue = ((Number) findMethod(modifier.getClass(), "comp_2449", 0).invoke(modifier)).doubleValue();
                double newValue = oldValue * ARMOR_SCALE;
                Object scaledModifier = modifierCtor.newInstance(id, newValue, operation);
                rebuilt.add(entryCtor.newInstance(attribute, scaledModifier, slot, display));
                changed++;
                changes.add(formatNumber(oldValue) + "→" + formatNumber(newValue));
            } else {
                rebuilt.add(entry);
            }
        }

        if (changed == 0) return;
        Object replacement = componentCtor.newInstance(List.copyOf(rebuilt));
        Method add = findCompatibleMethod(componentBuilder.getClass(), "method_57840", attributeComponentType, replacement);
        add.invoke(componentBuilder, attributeComponentType, replacement);
        System.out.println(LOG + "x2 ARMOR " + itemId + " [" + String.join(", ", changes) + "]");
    }

    private static Set<Object> armorSlots() throws Exception {
        Class<?> c = Class.forName("net.minecraft.class_9274");
        LinkedHashSet<Object> result = new LinkedHashSet<>();
        for (String f : new String[]{"field_49220", "field_49221", "field_49222", "field_49223", "field_49224", "field_50127"}) {
            result.add(declaredField(c, f).get(null));
        }
        return result;
    }

    private static String itemId(Object item) throws Exception {
        Object registry = declaredField(Class.forName("net.minecraft.class_7923"), "field_41178").get(null);
        Method getId = findCompatibleMethod(registry.getClass(), "method_10221", item);
        Object id = getId.invoke(registry, item);
        return id == null ? null : id.toString();
    }

    private static String safeItemId(Object item) {
        try { return itemId(item); } catch (Throwable ignored) { return "<unknown item>"; }
    }

    private static String formatNumber(double d) {
        if (d == Math.rint(d)) return Long.toString((long) d);
        return Double.toString(d);
    }

    private static Field publicField(Class<?> c, String name) throws Exception {
        Field f = c.getField(name); f.setAccessible(true); return f;
    }
    private static Field declaredField(Class<?> c, String name) throws Exception {
        Field f = c.getDeclaredField(name); f.setAccessible(true); return f;
    }
    private static Method findMethod(Class<?> c, String name, int parameterCount) throws Exception {
        for (Class<?> x = c; x != null; x = x.getSuperclass()) {
            for (Method m : x.getDeclaredMethods()) {
                if (m.getName().equals(name) && m.getParameterCount() == parameterCount) {
                    m.setAccessible(true); return m;
                }
            }
        }
        for (Method m : c.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == parameterCount) {
                m.setAccessible(true); return m;
            }
        }
        throw new NoSuchMethodException(c.getName() + "." + name + "/" + parameterCount);
    }
    private static Method findCompatibleMethod(Class<?> c, String name, Object... args) throws Exception {
        ArrayList<Method> methods = new ArrayList<>();
        for (Class<?> x = c; x != null; x = x.getSuperclass()) methods.addAll(Arrays.asList(x.getDeclaredMethods()));
        methods.addAll(Arrays.asList(c.getMethods()));
        for (Method m : methods) {
            if (!m.getName().equals(name) || m.getParameterCount() != args.length) continue;
            Class<?>[] p = m.getParameterTypes();
            boolean ok = true;
            for (int i = 0; i < p.length; i++) {
                if (args[i] != null && !p[i].isAssignableFrom(args[i].getClass())) { ok = false; break; }
            }
            if (ok) { m.setAccessible(true); return m; }
        }
        throw new NoSuchMethodException(c.getName() + "." + name + " compatible/" + args.length);
    }
}
