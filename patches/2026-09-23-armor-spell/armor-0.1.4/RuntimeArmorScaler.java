package fr.hautecapitale.armorbalance;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class RuntimeArmorScaler {
    private RuntimeArmorScaler() {}

    private static volatile Reflection R;
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();
    private static final ThreadLocal<Boolean> BYPASS = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public static Object scale(Object component) {
        if (component == null || !"net.minecraft.class_9285".equals(component.getClass().getName())) return component;
        try {
            Reflection r = reflection();
            Object rawEntries = r.entries.invoke(component);
            if (!(rawEntries instanceof List<?> entries) || entries.isEmpty()) return component;
            ArrayList<Object> out = new ArrayList<>(entries.size());
            boolean changed = false;
            for (Object entry : entries) {
                Object attribute = r.entryAttribute.invoke(entry);
                Object modifier = r.entryModifier.invoke(entry);
                Object slot = r.entrySlot.invoke(entry);
                Object display = r.entryDisplay.invoke(entry);
                Object operation = r.modifierOperation.invoke(modifier);
                if (Objects.equals(attribute, r.armorAttribute)
                        && Objects.equals(operation, r.addValueOperation)
                        && r.armorSlots.contains(slot)) {
                    Object id = r.modifierId.invoke(modifier);
                    double oldValue = ((Number) r.modifierValue.invoke(modifier)).doubleValue();
                    Object scaledModifier = r.modifierCtor.newInstance(id, oldValue * 2.0D, operation);
                    out.add(r.entryCtor.newInstance(attribute, scaledModifier, slot, display));
                    changed = true;
                } else out.add(entry);
            }
            return changed ? r.componentCtor.newInstance(List.copyOf(out)) : component;
        } catch (Throwable t) {
            warnOnce("scale", t);
            return component;
        }
    }

    public static boolean routeApply(Object component, String intermediaryMethod, Object slot, Object consumer) {
        if (Boolean.TRUE.equals(BYPASS.get())) return false;
        try {
            Object scaled = scale(component);
            if (scaled == component) return false;
            Reflection r = reflection();
            Method target = switch (intermediaryMethod) {
                case "method_57482" -> r.applyEquipmentBi;
                case "method_60618" -> r.applySlotBi;
                case "method_70727" -> r.applySlotTri;
                default -> null;
            };
            if (target == null) return false;
            BYPASS.set(Boolean.TRUE);
            try { target.invoke(scaled, slot, consumer); }
            finally { BYPASS.set(Boolean.FALSE); }
            return true;
        } catch (Throwable t) {
            BYPASS.set(Boolean.FALSE);
            warnOnce("route:" + intermediaryMethod, t);
            return false;
        }
    }

    private static Reflection reflection() throws Exception {
        Reflection local = R;
        if (local != null) return local;
        synchronized (RuntimeArmorScaler.class) {
            local = R;
            if (local == null) R = local = new Reflection();
            return local;
        }
    }

    private static void warnOnce(String where, Throwable t) {
        Throwable root = t;
        if (t instanceof java.lang.reflect.InvocationTargetException ite && ite.getCause() != null) root = ite.getCause();
        String key = where + ":" + root.getClass().getName() + ":" + String.valueOf(root.getMessage());
        if (WARNED.add(key)) {
            System.err.println("[capitale_armor_balance] Runtime ARMOR scaling fallback (original values kept): " + key);
            root.printStackTrace(System.err);
        }
    }

    private static Method method(Class<?> c, String name) throws Exception {
        Method m = c.getDeclaredMethod(name); m.setAccessible(true); return m;
    }
    private static Field field(Class<?> c, String name) throws Exception {
        Field f = c.getDeclaredField(name); f.setAccessible(true); return f;
    }

    private static final class Reflection {
        final Object armorAttribute, addValueOperation;
        final Set<Object> armorSlots;
        final Method entries, entryAttribute, entryModifier, entrySlot, entryDisplay;
        final Method modifierOperation, modifierId, modifierValue;
        final Constructor<?> modifierCtor, entryCtor, componentCtor;
        final Method applyEquipmentBi, applySlotBi, applySlotTri;

        Reflection() throws Exception {
            Class<?> attributeRegistry = Class.forName("net.minecraft.class_5134");
            Class<?> operationClass = Class.forName("net.minecraft.class_1322$class_1323");
            Class<?> registryEntryClass = Class.forName("net.minecraft.class_6880");
            Class<?> modifierClass = Class.forName("net.minecraft.class_1322");
            Class<?> identifierClass = Class.forName("net.minecraft.class_2960");
            Class<?> slotClass = Class.forName("net.minecraft.class_9274");
            Class<?> equipmentSlotClass = Class.forName("net.minecraft.class_1304");
            Class<?> displayClass = Class.forName("net.minecraft.class_9285$class_11193");
            Class<?> entryClass = Class.forName("net.minecraft.class_9285$class_9287");
            Class<?> componentClass = Class.forName("net.minecraft.class_9285");
            Class<?> triConsumerClass = Class.forName("org.apache.commons.lang3.function.TriConsumer");

            armorAttribute = field(attributeRegistry, "field_23724").get(null);
            addValueOperation = field(operationClass, "field_6328").get(null);
            HashSet<Object> slots = new HashSet<>();
            for (String f : new String[]{"field_49220","field_49221","field_49222","field_49223","field_49224","field_50127"}) slots.add(field(slotClass, f).get(null));
            armorSlots = Set.copyOf(slots);

            entries = method(componentClass, "comp_2393");
            entryAttribute = method(entryClass, "comp_2395");
            entryModifier = method(entryClass, "comp_2396");
            entrySlot = method(entryClass, "comp_2397");
            entryDisplay = method(entryClass, "comp_4036");
            modifierOperation = method(modifierClass, "comp_2450");
            modifierId = method(modifierClass, "comp_2447");
            modifierValue = method(modifierClass, "comp_2449");

            modifierCtor = modifierClass.getDeclaredConstructor(identifierClass, double.class, operationClass);
            modifierCtor.setAccessible(true);
            entryCtor = entryClass.getDeclaredConstructor(registryEntryClass, modifierClass, slotClass, displayClass);
            entryCtor.setAccessible(true);
            componentCtor = componentClass.getDeclaredConstructor(List.class);
            componentCtor.setAccessible(true);

            applyEquipmentBi = componentClass.getDeclaredMethod("method_57482", equipmentSlotClass, java.util.function.BiConsumer.class);
            applySlotBi = componentClass.getDeclaredMethod("method_60618", slotClass, java.util.function.BiConsumer.class);
            applySlotTri = componentClass.getDeclaredMethod("method_70727", slotClass, triConsumerClass);
            applyEquipmentBi.setAccessible(true); applySlotBi.setAccessible(true); applySlotTri.setAccessible(true);
        }
    }
}
