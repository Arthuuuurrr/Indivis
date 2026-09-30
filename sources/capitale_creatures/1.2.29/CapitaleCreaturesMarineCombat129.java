package fr.hautecapitale.creatures.spawn;

import net.fabricmc.api.ModInitializer;
import java.lang.reflect.*;
import java.util.*;

public final class CapitaleCreaturesMarineCombat129 implements ModInitializer {
    private static final Set<String> STRICT = Set.of(
        "dino_mounts_ai:dawn_aegirocassis_green_hostile",
        "dino_mounts_ai:dawn_aegirocassis_red_hostile",
        "dino_mounts_ai:dawn_aegirocassis_spotted_hostile",
        "dino_mounts_ai:dawn_dunkleosteus_female_hostile",
        "dino_mounts_ai:dawn_dunkleosteus_male_hostile",
        "dino_mounts_ai:mystic_mosasaurus_couleur_1_hostile",
        "dino_mounts_ai:mystic_mosasaurus_couleur_2_hostile",
        "dino_mounts_ai:mystic_mosasaurus_couleur_3_hostile",
        "chaos_mmo_ai:corpsefish",
        "chaos_mmo_ai:gluttonfish",
        "cubeanimals:piranha",
        "myths_of_the_sea:abaia",
        "myths_of_the_sea:bake_kujira",
        "myths_of_the_sea:hippocampus",
        "myths_of_the_sea:kraken",
        "myths_of_the_sea:leviathan"
    );

    private static volatile boolean ready = false, logged = false;
    private static Method worldEntities, getType, typeId, touchingWater, getX, getY, getZ,
            setVelocityDDD, setTarget, getNavigation, navigationStop, discard;
    private static Class<?> playerClass, mobClass, livingClass;

    @Override public void onInitialize() {
        try {
            registerEndWorldTick();
            System.out.println("[Capitale Creatures] 1.2.29 marine combat controller: direct pursuit, bounded melee engagement, extended swimmer aggro, mountain maggot guard.");
        } catch (Throwable t) {
            System.err.println("[Capitale Creatures] marine combat controller init failed: " + t);
        }
    }

    private static void registerEndWorldTick() throws Exception {
        Class<?> listener = Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndWorldTick");
        Class<?> holder = Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents");
        Class<?> eventCls = Class.forName("net.fabricmc.fabric.api.event.Event");
        Object event = holder.getField("END_WORLD_TICK").get(null);
        Object proxy = Proxy.newProxyInstance(listener.getClassLoader(), new Class<?>[]{listener}, (p,m,a) -> {
            if (m.getDeclaringClass() == Object.class) {
                return switch (m.getName()) { case "toString" -> "CapitaleCreaturesMarineCombat129"; case "hashCode" -> System.identityHashCode(p); case "equals" -> p == (a == null || a.length == 0 ? null : a[0]); default -> null; };
            }
            if (a != null && a.length > 0) tickWorld(a[0]);
            return null;
        });
        eventCls.getMethod("register", Object.class).invoke(event, proxy);
    }

    private static void init() throws Exception {
        if (ready) return;
        synchronized (CapitaleCreaturesMarineCombat129.class) {
            if (ready) return;
            Class<?> serverWorld = Class.forName("net.minecraft.class_3218");
            Class<?> entity = Class.forName("net.minecraft.class_1297");
            Class<?> entityType = Class.forName("net.minecraft.class_1299");
            mobClass = Class.forName("net.minecraft.class_1308");
            livingClass = Class.forName("net.minecraft.class_1309");
            playerClass = Class.forName("net.minecraft.class_1657");
            Class<?> navigation = Class.forName("net.minecraft.class_1408");
            worldEntities = serverWorld.getMethod("method_27909");
            getType = entity.getMethod("method_5864");
            typeId = entityType.getMethod("method_5890", entityType);
            touchingWater = entity.getMethod("method_5799");
            getX = entity.getMethod("method_23317");
            getY = entity.getMethod("method_23318");
            getZ = entity.getMethod("method_23321");
            setVelocityDDD = entity.getMethod("method_18800", double.class, double.class, double.class);
            discard = entity.getMethod("method_31472");
            setTarget = mobClass.getMethod("method_5980", livingClass);
            getNavigation = mobClass.getMethod("method_5942");
            navigationStop = navigation.getMethod("method_6340");
            ready = true;
        }
    }

    private static void tickWorld(Object world) {
        try {
            init();
            Iterable<?> entities = (Iterable<?>) worldEntities.invoke(world);
            List<Object> swimmers = new ArrayList<>();
            List<Object> all = new ArrayList<>();
            for (Object e : entities) {
                all.add(e);
                if (playerClass.isInstance(e) && isWater(e)) swimmers.add(e);
            }
            for (Object e : all) {
                String id = id(e);
                if ("chaos_mmo_ai:maggot".equals(id) && y(e) >= 115.0) {
                    discard.invoke(e);
                    continue;
                }
                if (!STRICT.contains(id) || !mobClass.isInstance(e) || !isWater(e)) continue;
                Object p = nearest(e, swimmers, aggroRange(id));
                if (p == null) continue;
                double d = distance(e,p);
                try { navigationStop.invoke(getNavigation.invoke(e)); } catch (Throwable ignored) {}
                double engage = engageRange(id);
                try { setTarget.invoke(e, d <= engage ? p : null); } catch (Throwable ignored) {}
                steerDirect(e,p,id,d,engage);
            }
        } catch (Throwable t) {
            if (!logged) {
                logged = true;
                System.err.println("[Capitale Creatures] marine combat controller runtime failure: " + t);
            }
        }
    }

    private static void steerDirect(Object e, Object p, String id, double d, double engage) throws Exception {
        double dx=x(p)-x(e), dy=(y(p)+0.35)-(y(e)+0.25), dz=z(p)-z(e);
        double len=Math.sqrt(dx*dx+dy*dy+dz*dz);
        if (len < 1.0e-4) return;
        double s=chaseSpeed(id);
        if (d <= engage + 0.7) s *= 0.45;
        if (d <= engage) s *= 0.18;
        setVelocityDDD.invoke(e, dx/len*s, dy/len*s, dz/len*s);
    }

    private static Object nearest(Object e, List<Object> ps, double max) throws Exception {
        Object best=null; double bd=max;
        for (Object p: ps) { double d=distance(e,p); if (d < bd) { bd=d; best=p; } }
        return best;
    }

    private static double aggroRange(String id) {
        if (id.equals("chaos_mmo_ai:corpsefish")) return 28.0;
        if (id.equals("chaos_mmo_ai:gluttonfish")) return 30.0;
        if (id.equals("cubeanimals:piranha")) return 18.0;
        if (id.contains("mosasaurus")) return 28.0;
        if (id.contains("dunkleosteus")) return 24.0;
        if (id.contains("aegirocassis")) return 18.0;
        if (id.contains("leviathan") || id.contains("kraken") || id.contains("bake_kujira")) return 30.0;
        return 24.0;
    }

    private static double engageRange(String id) {
        if (id.equals("cubeanimals:piranha")) return 1.35;
        if (id.equals("chaos_mmo_ai:corpsefish")) return 1.75;
        if (id.equals("chaos_mmo_ai:gluttonfish")) return 3.2;
        if (id.contains("aegirocassis")) return 1.8;
        if (id.contains("dunkleosteus")) return 2.5;
        if (id.contains("mosasaurus")) return 3.6;
        if (id.contains("leviathan") || id.contains("kraken") || id.contains("bake_kujira")) return 3.5;
        return 2.1;
    }

    private static double chaseSpeed(String id) {
        if (id.equals("chaos_mmo_ai:corpsefish")) return 0.34;
        if (id.equals("chaos_mmo_ai:gluttonfish")) return 0.285;
        if (id.equals("cubeanimals:piranha")) return 0.31;
        if (id.contains("mosasaurus")) return 0.30;
        if (id.contains("dunkleosteus")) return 0.27;
        if (id.contains("aegirocassis")) return 0.23;
        if (id.contains("leviathan") || id.contains("kraken")) return 0.30;
        return 0.27;
    }

    private static boolean isWater(Object e) throws Exception { return Boolean.TRUE.equals(touchingWater.invoke(e)); }
    private static String id(Object e) throws Exception { Object t=getType.invoke(e); return String.valueOf(typeId.invoke(null,t)); }
    private static double x(Object e) throws Exception { return ((Number)getX.invoke(e)).doubleValue(); }
    private static double y(Object e) throws Exception { return ((Number)getY.invoke(e)).doubleValue(); }
    private static double z(Object e) throws Exception { return ((Number)getZ.invoke(e)).doubleValue(); }
    private static double distance(Object a,Object b) throws Exception { double dx=x(a)-x(b),dy=y(a)-y(b),dz=z(a)-z(b); return Math.sqrt(dx*dx+dy*dy+dz*dz); }
}
