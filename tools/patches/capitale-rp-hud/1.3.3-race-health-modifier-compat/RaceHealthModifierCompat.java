package fr.arthur.capitale.rphud;

import java.lang.reflect.*;

/** Keeps race health as the MAX_HEALTH base while preserving equipment/effect modifiers. */
public final class RaceHealthModifierCompat {
    private RaceHealthModifierCompat() {}

    public static boolean applyRaceBase(Object player, int raceId) {
        return setBase(player, expectedBase(raceId));
    }

    public static Float getMaxHealthBase(Object player) {
        try {
            Object instance = attributeInstance(player, maxHealthAttribute());
            if (instance == null) return null;
            Object value = invokeNoArg(instance, "getBaseValue", "method_6201", "m_22115_");
            return value instanceof Number n ? n.floatValue() : null;
        } catch (Throwable ignored) { return null; }
    }

    public static float getMaxHealthTotalOr(Object player, float fallback) {
        try {
            Float v = numberNoArg(player, "getMaxHealth", "method_6063", "m_21233_");
            return v != null && v > 0F ? v : fallback;
        } catch (Throwable ignored) { return fallback; }
    }

    public static void normalizeCurrentHealth(Object player, int expectedRaceBase, int raceId) {
        try {
            Float current = numberNoArg(player, "getHealth", "method_6032");
            Float totalMax = numberNoArg(player, "getMaxHealth", "method_6063");
            if (current == null || totalMax == null || totalMax <= 0F) return;
            float target = current;
            if (target > totalMax) {
                target = totalMax;
            } else if (raceId == 2 && target >= 20F && target < expectedRaceBase) {
                target = Math.min(expectedRaceBase, totalMax);
            }
            if (Math.abs(target - current) > 0.001F) invokeOneNumber(player, target, "setHealth", "method_6033");
        } catch (Throwable ignored) { }
    }

    private static int expectedBase(int raceId) { return raceId == 2 ? 22 : (raceId == 3 ? 18 : 20); }

    private static boolean setBase(Object player, double value) {
        try {
            Object instance = attributeInstance(player, maxHealthAttribute());
            if (instance == null) return false;
            return invokeOneNumber(instance, value, "setBaseValue", "method_6192", "method_6190", "m_22100_");
        } catch (Throwable ignored) { return false; }
    }

    private static Object maxHealthAttribute() throws Exception {
        for (String cn : new String[]{"net.minecraft.entity.attribute.EntityAttributes", "net.minecraft.class_5134"}) {
            try {
                Class<?> c = Class.forName(cn);
                for (String fn : new String[]{"MAX_HEALTH", "field_23716"}) {
                    try { Field f=c.getDeclaredField(fn); f.setAccessible(true); Object v=f.get(null); if(v!=null) return v; }
                    catch (Throwable ignored) { }
                }
            } catch (Throwable ignored) { }
        }
        return null;
    }

    private static Object attributeInstance(Object player, Object attribute) throws Exception {
        if (player == null || attribute == null) return null;
        for (Method m : allMethods(player.getClass())) {
            if (m.getParameterCount()!=1) continue;
            String n=m.getName();
            if (!(n.equals("getAttributeInstance") || n.equals("method_5996") || n.equals("m_21051_"))) continue;
            try { m.setAccessible(true); Object v=m.invoke(player,attribute); if(v!=null) return v; } catch(Throwable ignored) { }
        }
        return null;
    }

    private static Object invokeNoArg(Object target, String... names) throws Exception {
        for (Method m: allMethods(target.getClass())) {
            if(m.getParameterCount()!=0) continue;
            for(String n:names) if(m.getName().equals(n)) {
                try { m.setAccessible(true); return m.invoke(target); } catch(Throwable ignored) { }
            }
        }
        return null;
    }

    private static Float numberNoArg(Object target, String... names) throws Exception {
        Object v=invokeNoArg(target,names); return v instanceof Number n ? n.floatValue() : null;
    }

    private static boolean invokeOneNumber(Object target, double value, String... names) throws Exception {
        for(Method m: allMethods(target.getClass())) {
            if(m.getParameterCount()!=1) continue;
            boolean named=false; for(String n:names) if(m.getName().equals(n)) named=true;
            if(!named) continue;
            Class<?> p=m.getParameterTypes()[0];
            Object arg;
            if(p==float.class || p==Float.class) arg=(float)value;
            else if(p==double.class || p==Double.class) arg=value;
            else continue;
            try { m.setAccessible(true); m.invoke(target,arg); return true; } catch(Throwable ignored) { }
        }
        return false;
    }

    private static Method[] allMethods(Class<?> c) {
        java.util.ArrayList<Method> out=new java.util.ArrayList<>();
        for(Class<?> x=c; x!=null; x=x.getSuperclass()) for(Method m:x.getDeclaredMethods()) out.add(m);
        return out.toArray(Method[]::new);
    }
}
