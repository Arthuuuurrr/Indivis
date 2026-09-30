package fr.hautecapitale.creatures.spawn;

import net.fabricmc.api.ModInitializer;
import java.lang.reflect.*;
import java.util.*;

public final class CapitaleCreaturesGluttonDepth141 implements ModInitializer {
    private static final String ID = "chaos_mmo_ai:gluttonfish";
    private static final double ACTIVE_COMBAT_RANGE = 24.0;
    private static volatile boolean ready, logged;

    private static Method worldEntities, getType, typeId, touchingWater, getX, getY, getZ;
    private static Method getVelocity, setVelocityDDD, setNoGravity, getTarget, setTarget, isAlive, isWater;
    private static Method getMaxAir, setAir, getPitch, setPitch;
    private static Class<?> mobClass;
    private static Constructor<?> blockPosCtor;
    private static Field vecX, vecY, vecZ;

    @Override public void onInitialize() {
        try {
            registerWorldTick("START_WORLD_TICK", "net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$StartWorldTick");
            registerWorldTick("END_WORLD_TICK", "net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndWorldTick");
            System.out.println("[Capitale Creatures] 1.2.41 Gluttonfish abyssal authority: hard depth bias outside close underwater combat (start+end world tick).");
        } catch (Throwable t) {
            System.err.println("[Capitale Creatures] 1.2.41 Gluttonfish depth registration failure: " + root(t));
        }
    }

    private static void registerWorldTick(String field, String callbackName) throws Exception {
        Class<?> cb = Class.forName(callbackName);
        Object event = Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents").getField(field).get(null);
        Object proxy = Proxy.newProxyInstance(cb.getClassLoader(), new Class<?>[]{cb}, (p,m,a) -> {
            if (m.getDeclaringClass() == Object.class) return objectMethod(p,m,a);
            if (a != null && a.length == 1) tickWorld(a[0]);
            return null;
        });
        Class.forName("net.fabricmc.fabric.api.event.Event").getMethod("register", Object.class).invoke(event, proxy);
    }

    private static Object objectMethod(Object p, Method m, Object[] a) {
        return switch (m.getName()) {
            case "toString" -> "CapitaleGluttonDepth141";
            case "hashCode" -> System.identityHashCode(p);
            case "equals" -> p == (a == null ? null : a[0]);
            default -> null;
        };
    }

    private static synchronized void init() throws Exception {
        if (ready) return;
        Class<?> world = Class.forName("net.minecraft.class_3218");
        Class<?> entity = Class.forName("net.minecraft.class_1297");
        Class<?> entityType = Class.forName("net.minecraft.class_1299");
        mobClass = Class.forName("net.minecraft.class_1308");
        Class<?> living = Class.forName("net.minecraft.class_1309");
        Class<?> pos = Class.forName("net.minecraft.class_2338");
        Class<?> vec = Class.forName("net.minecraft.class_243");

        worldEntities = world.getMethod("method_27909");
        getType = entity.getMethod("method_5864");
        typeId = entityType.getMethod("method_5890", entityType);
        touchingWater = entity.getMethod("method_5799");
        getX = entity.getMethod("method_23317");
        getY = entity.getMethod("method_23318");
        getZ = entity.getMethod("method_23321");
        getVelocity = entity.getMethod("method_18798");
        setVelocityDDD = entity.getMethod("method_18800", double.class,double.class,double.class);
        setNoGravity = entity.getMethod("method_5875", boolean.class);
        getMaxAir = entity.getMethod("method_5748");
        setAir = entity.getMethod("method_5855", int.class);
        getPitch = entity.getMethod("method_36455");
        setPitch = entity.getMethod("method_36457", float.class);
        isAlive = entity.getMethod("method_5805");
        getTarget = mobClass.getMethod("method_5968");
        setTarget = mobClass.getMethod("method_5980", living);
        isWater = world.getMethod("method_22351", pos);
        blockPosCtor = pos.getConstructor(int.class,int.class,int.class);
        vecX = vec.getField("field_1352");
        vecY = vec.getField("field_1351");
        vecZ = vec.getField("field_1350");
        ready = true;
    }

    private static void tickWorld(Object world) {
        try {
            init();
            Object all = worldEntities.invoke(world);
            if (!(all instanceof Iterable<?> entities)) return;
            for (Object e : entities) {
                if (e == null || !ID.equals(entityId(e))) continue;
                tickGlutton(world, e);
            }
        } catch (Throwable t) {
            if (!logged) {
                logged = true;
                System.err.println("[Capitale Creatures] 1.2.41 Gluttonfish depth runtime failure: " + root(t));
                root(t).printStackTrace();
            }
        }
    }

    private static String entityId(Object e) throws Exception {
        Object type = getType.invoke(e);
        Object id = typeId.invoke(null, type);
        return id == null ? null : id.toString();
    }

    private static void tickGlutton(Object world, Object e) throws Exception {
        if (!Boolean.TRUE.equals(touchingWater.invoke(e))) return;

        setNoGravity.invoke(e, true);
        setAir.invoke(e, ((Number)getMaxAir.invoke(e)).intValue());

        Object target = mobClass.isInstance(e) ? getTarget.invoke(e) : null;
        boolean closeWaterCombat = validCloseWaterTarget(e, target);
        if (target != null && !closeWaterCombat && mobClass.isInstance(e)) {
            setTarget.invoke(e, new Object[]{null});
            target = null;
        }
        if (closeWaterCombat) return;

        int x = (int)Math.floor(((Number)getX.invoke(e)).doubleValue());
        int y = (int)Math.floor(((Number)getY.invoke(e)).doubleValue());
        int z = (int)Math.floor(((Number)getZ.invoke(e)).doubleValue());
        int[] column = waterColumnAround(world, x, z, y);
        if (column == null) return;

        int bottom = column[0], top = column[1], depth = top - bottom + 1;
        if (depth < 7) return;

        int cruiseY = bottom + Math.max(2, depth / 5);
        int upperBand = bottom + Math.max(4, (depth * 2) / 5);
        if (y <= upperBand) return;

        Object v = getVelocity.invoke(e);
        double vx = vecX.getDouble(v), vy = vecY.getDouble(v), vz = vecZ.getDouble(v);
        double excess = Math.max(1.0, y - cruiseY);
        double forcedDown = -(0.075 + Math.min(0.085, excess * 0.010));
        double finalVy = Math.min(vy, forcedDown);
        setVelocityDDD.invoke(e, vx, finalVy, vz);

        double horizontal = Math.sqrt(vx*vx + vz*vz);
        if (horizontal > 0.01) {
            float desiredPitch = (float)(-Math.toDegrees(Math.atan2(finalVy, horizontal)));
            desiredPitch = Math.max(-35.0f, Math.min(35.0f, desiredPitch));
            float current = ((Number)getPitch.invoke(e)).floatValue();
            float delta = desiredPitch - current;
            if (delta > 6.0f) delta = 6.0f;
            else if (delta < -6.0f) delta = -6.0f;
            setPitch.invoke(e, current + delta);
        }
    }

    private static boolean validCloseWaterTarget(Object e, Object t) throws Exception {
        if (t == null || !Boolean.TRUE.equals(isAlive.invoke(t)) || !Boolean.TRUE.equals(touchingWater.invoke(t))) return false;
        double dx = ((Number)getX.invoke(t)).doubleValue() - ((Number)getX.invoke(e)).doubleValue();
        double dy = ((Number)getY.invoke(t)).doubleValue() - ((Number)getY.invoke(e)).doubleValue();
        double dz = ((Number)getZ.invoke(t)).doubleValue() - ((Number)getZ.invoke(e)).doubleValue();
        return dx*dx + dy*dy + dz*dz <= ACTIVE_COMBAT_RANGE * ACTIVE_COMBAT_RANGE;
    }

    private static int[] waterColumnAround(Object world, int x, int z, int startY) throws Exception {
        int seed = startY;
        if (!water(world,x,seed,z)) {
            boolean found = false;
            for (int d=1; d<=16 && !found; d++) {
                if (water(world,x,startY-d,z)) { seed=startY-d; found=true; }
                else if (water(world,x,startY+d,z)) { seed=startY+d; found=true; }
            }
            if (!found) return null;
        }
        int top=seed, bottom=seed;
        while (top < seed+48 && water(world,x,top+1,z)) top++;
        while (bottom > seed-80 && water(world,x,bottom-1,z)) bottom--;
        return top-bottom+1 >= 3 ? new int[]{bottom,top} : null;
    }

    private static boolean water(Object world,int x,int y,int z) throws Exception {
        return Boolean.TRUE.equals(isWater.invoke(world, blockPosCtor.newInstance(x,y,z)));
    }

    private static Throwable root(Throwable t) {
        while (t instanceof InvocationTargetException ite && ite.getCause()!=null) t=ite.getCause();
        return t;
    }
}
