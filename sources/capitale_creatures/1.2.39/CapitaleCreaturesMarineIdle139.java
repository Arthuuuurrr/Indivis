package fr.hautecapitale.creatures.spawn;

import net.fabricmc.api.ModInitializer;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class CapitaleCreaturesMarineIdle139 implements ModInitializer {
    private static final Set<String> TARGET_IDS = Set.of(
        "dino_mounts_ai:dawn_aegirocassis_green_hostile",
        "dino_mounts_ai:dawn_aegirocassis_red_hostile",
        "dino_mounts_ai:dawn_aegirocassis_spotted_hostile",
        "dino_mounts_ai:dawn_dunkleosteus_female_hostile",
        "dino_mounts_ai:dawn_dunkleosteus_male_hostile",
        "dino_mounts_ai:mystic_mosasaurus_couleur_1_hostile",
        "dino_mounts_ai:mystic_mosasaurus_couleur_2_hostile",
        "dino_mounts_ai:mystic_mosasaurus_couleur_3_hostile",
        "dino_mounts_ai:mystic_mosasaurus_couleur_4_hostile",
        "chaos_mmo_ai:corpsefish",
        "chaos_mmo_ai:gluttonfish"
    );
    private static final Map<Object, SwimTarget> TARGETS = Collections.synchronizedMap(new WeakHashMap<>());
    private static volatile boolean ready, logged;
    private static Method worldEntities, getType, typeId, touchingWater, getX, getY, getZ, getVelocity, setVelocityDDD, setNoGravity;
    private static Method getTarget, setTarget, isAlive, isSpectator, isCreative, isWater;
    private static Method getMaxAir, setAir, getYaw, setYaw, getPitch, setPitch, setBodyYaw, setHeadYaw;
    private static Class<?> mobClass, playerClass;
    private static Constructor<?> blockPosCtor;
    private static Field vecX, vecY, vecZ;

    @Override public void onInitialize() {
        try {
            registerEndWorldTick();
            System.out.println("[Capitale Creatures] 1.2.39 marine locomotion: depth-aware idle + underwater breathing + velocity-facing yaw/pitch for Aegirocassis/Dunkleosteus/Corpsefish/Gluttonfish/Mosasaurus; sharks unchanged.");
        } catch (Throwable t) {
            System.err.println("[Capitale Creatures] 1.2.39 marine locomotion registration failure: " + root(t));
        }
    }

    private static void registerEndWorldTick() throws Exception {
        Class<?> cb = Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndWorldTick");
        Object event = Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents").getField("END_WORLD_TICK").get(null);
        Object proxy = Proxy.newProxyInstance(cb.getClassLoader(), new Class<?>[]{cb}, (p,m,a) -> {
            if (m.getDeclaringClass() == Object.class) return objectMethod(p,m,a);
            if (a != null && a.length == 1) tickWorld(a[0]);
            return null;
        });
        Class.forName("net.fabricmc.fabric.api.event.Event").getMethod("register", Object.class).invoke(event, proxy);
    }
    private static Object objectMethod(Object p, Method m, Object[] a) {
        return switch (m.getName()) {
            case "toString" -> "CapitaleMarineIdle139";
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
        playerClass = Class.forName("net.minecraft.class_1657");
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
        getYaw = entity.getMethod("method_36454");
        setYaw = entity.getMethod("method_36456", float.class);
        getPitch = entity.getMethod("method_36455");
        setPitch = entity.getMethod("method_36457", float.class);
        setBodyYaw = entity.getMethod("method_5636", float.class);
        setHeadYaw = entity.getMethod("method_5847", float.class);
        isAlive = entity.getMethod("method_5805");
        isSpectator = entity.getMethod("method_7325");
        isCreative = methodAny(playerClass, "method_68878", "method_7337");
        getTarget = mobClass.getMethod("method_5968");
        setTarget = mobClass.getMethod("method_5980", living);
        isWater = world.getMethod("method_22351", pos);
        blockPosCtor = pos.getConstructor(int.class,int.class,int.class);
        vecX = vec.getField("field_1352");
        vecY = vec.getField("field_1351");
        vecZ = vec.getField("field_1350");
        ready = true;
    }
    private static Method methodAny(Class<?> c, String... names) throws Exception {
        for (String n:names) try { return c.getMethod(n); } catch (NoSuchMethodException ignored) {}
        throw new NoSuchMethodException(Arrays.toString(names));
    }

    private static void tickWorld(Object world) {
        try {
            init();
            Object all = worldEntities.invoke(world);
            if (!(all instanceof Iterable<?> entities)) return;
            for (Object e: entities) {
                if (e == null) continue;
                String id = entityId(e);
                if (!TARGET_IDS.contains(id)) continue;
                tickOne(world,e,id,entities);
            }
        } catch (Throwable t) {
            if (!logged) {
                logged = true;
                System.err.println("[Capitale Creatures] 1.2.39 marine locomotion runtime failure: " + root(t));
            }
        }
    }

    private static String entityId(Object e) throws Exception {
        Object type = getType.invoke(e);
        Object id = typeId.invoke(null,type);
        return id == null ? null : id.toString();
    }

    private static void tickOne(Object world,Object e,String id,Iterable<?> entities) throws Exception {
        boolean wet = Boolean.TRUE.equals(touchingWater.invoke(e));
        if (!wet) {
            TARGETS.remove(e);
            setNoGravity.invoke(e,false);
            return;
        }
        int maxAir = ((Number)getMaxAir.invoke(e)).intValue();
        setAir.invoke(e,maxAir);
        setNoGravity.invoke(e,true);

        Object combat = mobClass.isInstance(e) ? getTarget.invoke(e) : null;
        if (combat != null) {
            TARGETS.remove(e);
            if (id.contains("mosasaurus")) {
                boolean valid = Boolean.TRUE.equals(isAlive.invoke(combat))
                    && Boolean.TRUE.equals(touchingWater.invoke(combat));
                if (valid) steerToEntity(e,combat,0.305);
                else {
                    setTarget.invoke(e,new Object[]{null});
                    combat=null;
                }
                if (combat != null) {
                    orientFromVelocity(e,id,true);
                    return;
                }
            } else {
                orientFromVelocity(e,id,true);
                return;
            }
        }

        if (id.contains("mosasaurus")) {
            Object player = nearestWaterPlayer(e,entities,28.0);
            if (player != null) {
                if (mobClass.isInstance(e)) setTarget.invoke(e,player);
                steerToEntity(e,player,0.305);
                TARGETS.remove(e);
                orientFromVelocity(e,id,true);
                return;
            }
        }

        int x=(int)Math.floor(((Number)getX.invoke(e)).doubleValue());
        int y=(int)Math.floor(((Number)getY.invoke(e)).doubleValue());
        int z=(int)Math.floor(((Number)getZ.invoke(e)).doubleValue());
        SwimTarget t=TARGETS.get(e);
        if (t != null) t=t.tick();
        if (t == null || t.ttl<=0 || distSq(x,y,z,t.x,t.y,t.z)<4 || !water(world,t.x,t.y,t.z)) t=chooseTarget(world,x,y,z,id);
        if (t != null) TARGETS.put(e,t); else TARGETS.remove(e);
        steer(e,world,x,y,z,t,id);
        orientFromVelocity(e,id,false);
    }

    private static Object nearestWaterPlayer(Object e,Iterable<?> entities,double range) throws Exception {
        double ex=((Number)getX.invoke(e)).doubleValue(), ey=((Number)getY.invoke(e)).doubleValue(), ez=((Number)getZ.invoke(e)).doubleValue();
        double best=range*range; Object out=null;
        for(Object p:entities){
            if(p==null || !playerClass.isInstance(p)) continue;
            if(!Boolean.TRUE.equals(isAlive.invoke(p)) || Boolean.TRUE.equals(isSpectator.invoke(p)) || Boolean.TRUE.equals(isCreative.invoke(p))) continue;
            if(!Boolean.TRUE.equals(touchingWater.invoke(p))) continue;
            double dx=((Number)getX.invoke(p)).doubleValue()-ex, dy=((Number)getY.invoke(p)).doubleValue()-ey, dz=((Number)getZ.invoke(p)).doubleValue()-ez;
            double d=dx*dx+dy*dy+dz*dz;
            if(d<best){best=d;out=p;}
        }
        return out;
    }

    private static void steerToEntity(Object e,Object target,double speed) throws Exception {
        double ex=((Number)getX.invoke(e)).doubleValue(), ey=((Number)getY.invoke(e)).doubleValue(), ez=((Number)getZ.invoke(e)).doubleValue();
        double dx=((Number)getX.invoke(target)).doubleValue()-ex, dy=((Number)getY.invoke(target)).doubleValue()-ey, dz=((Number)getZ.invoke(target)).doubleValue()-ez;
        double d=Math.sqrt(dx*dx+dy*dy+dz*dz); if(d<0.001) return;
        setVelocityDDD.invoke(e, dx/d*speed, dy/d*speed*0.78, dz/d*speed);
    }

    private static SwimTarget chooseTarget(Object world,int x,int y,int z,String id) throws Exception {
        ThreadLocalRandom r=ThreadLocalRandom.current();
        int radius=(id.contains("mosasaurus") || id.equals("chaos_mmo_ai:gluttonfish")) ? 18 : 12;
        for(int a=0;a<28;a++){
            int tx=x+r.nextInt(-radius,radius+1), tz=z+r.nextInt(-radius,radius+1);
            if(Math.abs(tx-x)+Math.abs(tz-z)<4) continue;
            int[] column=waterColumnAround(world,tx,tz,y);
            if(column==null) continue;
            int bottom=column[0], top=column[1], depth=top-bottom+1;
            int ty;
            if(id.equals("chaos_mmo_ai:gluttonfish")) ty=bottom+Math.max(2,depth/5);
            else if(id.contains("mosasaurus")) ty=bottom+Math.max(3,(depth*2)/5);
            else if(id.contains("dunkleosteus")) ty=bottom+Math.max(2,depth/2);
            else ty=bottom+Math.max(2,(depth*3)/5);
            ty=Math.max(bottom+1,Math.min(top-1,ty+r.nextInt(-2,3)));
            if(water(world,tx,ty,tz)) return new SwimTarget(tx,ty,tz,r.nextInt(55,121));
        }
        int[] c=waterColumnAround(world,x,z,y);
        if(c!=null && c[1]-c[0]>=3){
            int ty=id.equals("chaos_mmo_ai:gluttonfish") ? c[0]+Math.max(2,(c[1]-c[0]+1)/5) : c[0]+Math.max(2,(c[1]-c[0]+1)/2);
            ty=Math.max(c[0]+1,Math.min(c[1]-1,ty));
            return new SwimTarget(x,ty,z,40);
        }
        return null;
    }

    private static int[] waterColumnAround(Object world,int x,int z,int startY) throws Exception {
        int seed=startY;
        if(!water(world,x,seed,z)){
            boolean found=false;
            for(int d=1;d<=12 && !found;d++){
                if(water(world,x,startY-d,z)){seed=startY-d;found=true;}
                else if(water(world,x,startY+d,z)){seed=startY+d;found=true;}
            }
            if(!found) return null;
        }
        int top=seed,bottom=seed;
        while(top<seed+40 && water(world,x,top+1,z)) top++;
        while(bottom>seed-64 && water(world,x,bottom-1,z)) bottom--;
        return top-bottom+1>=3 ? new int[]{bottom,top}:null;
    }

    private static void steer(Object e,Object world,int x,int y,int z,SwimTarget t,String id) throws Exception {
        Object v=getVelocity.invoke(e);
        double vx=vecX.getDouble(v), vy=vecY.getDouble(v), vz=vecZ.getDouble(v);
        double speed=swimSpeed(id);
        if(t!=null){
            double dx=t.x+.5-(x+.5), dy=t.y+.5-(y+.5), dz=t.z+.5-(z+.5);
            double d=Math.sqrt(dx*dx+dy*dy+dz*dz);
            if(d>0.001){
                double tx=dx/d*speed, ty=dy/d*speed*.78, tz=dz/d*speed;
                vx=vx*.58+tx*.42; vy=vy*.48+ty*.52; vz=vz*.58+tz*.42;
            }
        } else {
            double angle=ThreadLocalRandom.current().nextDouble(Math.PI*2);
            vx=vx*.65+Math.cos(angle)*speed*.35; vz=vz*.65+Math.sin(angle)*speed*.35; vy*=.5;
        }
        if(!water(world,x,y+1,z) && vy>-.035) vy=-.035;
        if(!water(world,x,y-1,z) && vy<.025) vy=.025;
        double h=Math.sqrt(vx*vx+vz*vz), max=speed*1.35;
        if(h>max && h>1e-4){double k=max/h;vx*=k;vz*=k;}
        vy=Math.max(-speed*.9,Math.min(speed*.9,vy));
        setVelocityDDD.invoke(e,vx,vy,vz);
    }
    private static void orientFromVelocity(Object e,String id,boolean combat) throws Exception {
        Object v=getVelocity.invoke(e);
        double vx=vecX.getDouble(v), vy=vecY.getDouble(v), vz=vecZ.getDouble(v);
        double horizontal=Math.sqrt(vx*vx+vz*vz);
        if(horizontal<0.012 && Math.abs(vy)<0.012) return;

        float desiredYaw=(float)(Math.toDegrees(Math.atan2(vz,vx))-90.0);
        float currentYaw=((Number)getYaw.invoke(e)).floatValue();
        float yaw=approachDegrees(currentYaw,desiredYaw,yawStep(id,combat));
        setYaw.invoke(e,yaw);
        setBodyYaw.invoke(e,yaw);
        setHeadYaw.invoke(e,yaw);

        if(horizontal>0.01){
            float desiredPitch=(float)(-Math.toDegrees(Math.atan2(vy,horizontal)));
            desiredPitch=Math.max(-35.0f,Math.min(35.0f,desiredPitch));
            float currentPitch=((Number)getPitch.invoke(e)).floatValue();
            float pitch=approachLinear(currentPitch,desiredPitch,combat?8.0f:5.0f);
            setPitch.invoke(e,pitch);
        }
    }
    private static float yawStep(String id,boolean combat){
        if(id.contains("mosasaurus")) return combat?10.0f:6.0f;
        if(id.contains("dunkleosteus")) return combat?16.0f:9.0f;
        if(id.contains("aegirocassis")) return combat?14.0f:8.0f;
        if(id.equals("chaos_mmo_ai:gluttonfish")) return combat?18.0f:10.0f;
        return combat?22.0f:12.0f;
    }
    private static float approachDegrees(float current,float target,float maxStep){
        float delta=wrapDegrees(target-current);
        if(delta>maxStep)delta=maxStep; else if(delta<-maxStep)delta=-maxStep;
        return current+delta;
    }
    private static float wrapDegrees(float value){
        value%=360.0f;
        if(value>=180.0f)value-=360.0f;
        if(value<-180.0f)value+=360.0f;
        return value;
    }
    private static float approachLinear(float current,float target,float maxStep){
        float d=target-current;
        if(d>maxStep)d=maxStep; else if(d<-maxStep)d=-maxStep;
        return current+d;
    }

    private static double swimSpeed(String id){
        if(id.contains("mosasaurus")) return .115;
        if(id.contains("dunkleosteus")) return .095;
        if(id.contains("aegirocassis")) return .070;
        if(id.equals("chaos_mmo_ai:gluttonfish")) return .080;
        if(id.equals("chaos_mmo_ai:corpsefish")) return .095;
        return .085;
    }
    private static boolean water(Object world,int x,int y,int z) throws Exception {
        return Boolean.TRUE.equals(isWater.invoke(world,blockPosCtor.newInstance(x,y,z)));
    }
    private static double distSq(int x,int y,int z,int a,int b,int c){double dx=x-a,dy=y-b,dz=z-c;return dx*dx+dy*dy+dz*dz;}
    private static Throwable root(Throwable t){while(t instanceof InvocationTargetException ite && ite.getCause()!=null)t=ite.getCause();return t;}
    private record SwimTarget(int x,int y,int z,int ttl){SwimTarget tick(){return new SwimTarget(x,y,z,ttl-1);}}
}
