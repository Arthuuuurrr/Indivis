package net.hazen.hazennstuff.hc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Haute Capitale compatibility rules for HazennStuff armor sets.
 *
 * This class deliberately uses reflection so it can be compiled without
 * bundling Minecraft/Fabric classes and remains a tiny runtime-only helper.
 */
public final class HcSetRules {
    private HcSetRules() {}

    private static final String HEAD = "HEAD";
    private static final String CHEST = "CHEST";
    private static final String LEGS = "LEGS";
    private static final String FEET = "FEET";

    /** Explicit aliases only where the original registration splits one logical set across classes. */
    private static final Map<String, Map<String, Set<String>>> RULES = Map.ofEntries(
        Map.entry("BishopOfDeceitArmorItem", Map.of(
            HEAD, Set.of("BishopOfDeceitArmorItem"),
            CHEST, Set.of("BishopOfDeceitArmorItem")
        )),
        Map.entry("NecromancerArmorItem", Map.of(
            HEAD, Set.of("NecromancerArmorItem"),
            CHEST, Set.of("NecromancerArmorItem"),
            LEGS, Set.of("NecromancerArmorItem")
        )),
        Map.entry("NamelessOneArmorItem", Map.of(
            HEAD, Set.of("NamelessOneArmorItem"),
            CHEST, Set.of("NamelessOneArmorItem"),
            LEGS, Set.of("NamelessOneArmorItem")
        )),
        Map.entry("ChlorophyteHeadgearArmorItem", chlorophyteRule("ChlorophyteHeadgearArmorItem")),
        Map.entry("ChlorophyteHelmetArmorItem", chlorophyteRule("ChlorophyteHelmetArmorItem")),
        Map.entry("ChlorophyteMaskArmorItem", chlorophyteRule("ChlorophyteMaskArmorItem")),
        Map.entry("MothicWitchArmorItem", Map.of(
            HEAD, Set.of("MothicWitchArmorItem"),
            CHEST, Set.of("MothicWitchArmorItem", "NerfedMothicWitchArmorItem"),
            LEGS, Set.of("MothicWitchArmorItem"),
            FEET, Set.of("MothicWitchArmorItem")
        )),
        Map.entry("FireblossomBattlemageCrownedArmorItemLegacy", legacyFireblossomRule(
            "FireblossomBattlemageCrownedArmorItemLegacy", "FireblossomBattlemageArmorItemLegacy")),
        Map.entry("FireblossomBattlemageHelmetArmorItemLegacy", legacyFireblossomRule(
            "FireblossomBattlemageHelmetArmorItemLegacy", "FireblossomBattlemageArmorItemLegacy")),
        Map.entry("GeckolibFireblossomBattlemageCrownedArmorItemLegacy", legacyFireblossomRule(
            "GeckolibFireblossomBattlemageCrownedArmorItemLegacy", "GeckolibFireblossomBattlemageArmorItemLegacy")),
        Map.entry("GeckolibFireblossomBattlemageHelmetArmorItemLegacy", legacyFireblossomRule(
            "GeckolibFireblossomBattlemageHelmetArmorItemLegacy", "GeckolibFireblossomBattlemageArmorItemLegacy"))
    );

    private static Map<String, Set<String>> chlorophyteRule(String headKey) {
        return Map.of(
            HEAD, Set.of(headKey),
            CHEST, Set.of("ChlorophyteArmorItem"),
            LEGS, Set.of("ChlorophyteArmorItem"),
            FEET, Set.of("ChlorophyteArmorItem")
        );
    }

    private static Map<String, Set<String>> legacyFireblossomRule(String headKey, String bodyKey) {
        return Map.of(
            HEAD, Set.of(headKey),
            CHEST, Set.of(bodyKey),
            LEGS, Set.of(bodyKey),
            FEET, Set.of(bodyKey)
        );
    }

    private static final ConcurrentHashMap<Class<?>, Method> SET_METHODS = new ConcurrentHashMap<>();
    private static volatile Access ACCESS;
    private static volatile boolean warned;

    public static boolean isWearingExpectedSet(Object self, Object player) {
        try {
            String selfSet = setKey(self);
            if (selfSet == null) return false;

            Map<String, Set<String>> rule = RULES.get(selfSet);
            if (rule == null) {
                rule = Map.of(
                    HEAD, Set.of(selfSet), CHEST, Set.of(selfSet),
                    LEGS, Set.of(selfSet), FEET, Set.of(selfSet)
                );
            }

            Access a = access(player);
            for (Map.Entry<String, Set<String>> e : rule.entrySet()) {
                Object slot = a.slots.get(e.getKey());
                Object stack = a.getEquipped.invoke(player, slot);
                Object item = a.getItem.invoke(stack);
                String equippedSet = setKey(item);
                if (equippedSet == null || !e.getValue().contains(equippedSet)) return false;
            }
            return true;
        } catch (Throwable t) {
            if (!warned) {
                warned = true;
                System.err.println("[hazennstuff-hc-setfix] set rule failed once: " + t);
            }
            return false;
        }
    }

    private static String setKey(Object item) throws Exception {
        if (item == null) return null;
        Method m = SET_METHODS.computeIfAbsent(item.getClass(), c -> {
            try { return c.getMethod("set"); }
            catch (ReflectiveOperationException ignored) { return null; }
        });
        if (m == null) return null;
        Object v = m.invoke(item);
        return v instanceof String s ? s : null;
    }

    private static Access access(Object player) throws Exception {
        Access a = ACCESS;
        if (a != null) return a;
        synchronized (HcSetRules.class) {
            a = ACCESS;
            if (a != null) return a;
            Class<?> slotClass = Class.forName("net.minecraft.class_1304");
            Class<?> stackClass = Class.forName("net.minecraft.class_1799");
            Method equipped = findMethod(player.getClass(), "method_6118", slotClass);
            Method getItem = findMethod(stackClass, "method_7909");
            Map<String,Object> slots = Map.of(
                HEAD, field(slotClass, "field_6169"),
                CHEST, field(slotClass, "field_6174"),
                LEGS, field(slotClass, "field_6172"),
                FEET, field(slotClass, "field_6166")
            );
            ACCESS = a = new Access(equipped, getItem, slots);
            return a;
        }
    }

    private static Method findMethod(Class<?> start, String name, Class<?>... params) throws Exception {
        Class<?> c = start;
        while (c != null) {
            try {
                Method m = c.getDeclaredMethod(name, params);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException ignored) { c = c.getSuperclass(); }
        }
        throw new NoSuchMethodException(name);
    }

    private static Object field(Class<?> c, String name) throws Exception {
        Field f = c.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(null);
    }

    private record Access(Method getEquipped, Method getItem, Map<String,Object> slots) {}
}
