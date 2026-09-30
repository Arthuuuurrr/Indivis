package fr.hautecapitale.creatures.spawn;

import net.fabricmc.api.ModInitializer;
import java.lang.reflect.*;
import java.util.*;

/**
 * Single-authority marine combat controller for simple aquatic hostiles.
 * Mythical/specialized mobs keep native AI. Kraken receives buoyancy only.
 */
public final class CapitaleCreaturesMarineCombat130 implements ModInitializer {
    private static final Set<String> DIRECT = Set.of(
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
        "cubeanimals:piranha"
    );
    private static final String KRAKEN = "myths_of_the_sea:kraken";
    private static final Map<Object,Object> LAST_TARGET = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<Object,Integer> TARGET_TTL = Collections.synchronizedMap(new WeakHashMap<>());

    private static volatile boolean ready=false, logged=false;
    private static Method worldEntities,getType,typeId,touchingWater,getX,getY,getZ,setVelocityDDD,
            setTarget,getNavigation,navigationStop,isAlive,isSpectator,isCreative,setNoGravity,isOnGround,
            getVelocity,isWater;
    private static Class<?> playerClass,mobClass,livingClass,entityClass;
    private static Constructor<?> blockPosCtor;
    private static Field vecX,vecY,vecZ;

    @Override public void onInitialize() {
        try {
            registerEndWorldTick();
            System.out.println("[Capitale Creatures] 1.2.30 marine AI consolidated: direct chase authority for simple marine hostiles; Myths native AI preserved; Kraken buoyancy assist enabled.");
        } catch(Throwable t) {
            System.err.println("[Capitale Creatures] 1.2.30 marine AI init failure: "+t);
        }
    }

    private static void registerEndWorldTick() throws Exception {
        Class<?> listener=Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndWorldTick");
        Class<?> holder=Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents");
        Class<?> eventCls=Class.forName("net.fabricmc.fabric.api.event.Event");
        Object event=holder.getField("END_WORLD_TICK").get(null);
        Object proxy=Proxy.newProxyInstance(listener.getClassLoader(),new Class<?>[]{listener},(p,m,a)->{
            if(m.getDeclaringClass()==Object.class) {
                return switch(m.getName()) {
                    case "toString" -> "CapitaleCreaturesMarineCombat130";
                    case "hashCode" -> System.identityHashCode(p);
                    case "equals" -> p==(a==null||a.length==0?null:a[0]);
                    default -> null;
                };
            }
            if(a!=null&&a.length>0) tickWorld(a[0]);
            return null;
        });
        eventCls.getMethod("register",Object.class).invoke(event,proxy);
    }

    private static Method methodAny(Class<?> c, String... names) throws Exception {
        for(String n:names) try { return c.getMethod(n); } catch(NoSuchMethodException ignored) {}
        throw new NoSuchMethodException(Arrays.toString(names));
    }

    private static void init() throws Exception {
        if(ready) return;
        synchronized(CapitaleCreaturesMarineCombat130.class) {
            if(ready) return;
            Class<?> serverWorld=Class.forName("net.minecraft.class_3218");
            entityClass=Class.forName("net.minecraft.class_1297");
            Class<?> entityType=Class.forName("net.minecraft.class_1299");
            mobClass=Class.forName("net.minecraft.class_1308");
            livingClass=Class.forName("net.minecraft.class_1309");
            playerClass=Class.forName("net.minecraft.class_1657");
            Class<?> navigation=Class.forName("net.minecraft.class_1408");
            Class<?> blockPos=Class.forName("net.minecraft.class_2338");
            Class<?> vec3=Class.forName("net.minecraft.class_243");

            worldEntities=serverWorld.getMethod("method_27909");
            isWater=serverWorld.getMethod("method_22351",blockPos);
            getType=entityClass.getMethod("method_5864");
            typeId=entityType.getMethod("method_5890",entityType);
            touchingWater=entityClass.getMethod("method_5799");
            getX=entityClass.getMethod("method_23317");
            getY=entityClass.getMethod("method_23318");
            getZ=entityClass.getMethod("method_23321");
            setVelocityDDD=entityClass.getMethod("method_18800",double.class,double.class,double.class);
            getVelocity=entityClass.getMethod("method_18798");
            setNoGravity=entityClass.getMethod("method_5875",boolean.class);
            isOnGround=entityClass.getMethod("method_24828");
            isAlive=entityClass.getMethod("method_5805");
            isSpectator=entityClass.getMethod("method_7325");
            isCreative=methodAny(playerClass,"method_68878","method_7337");
            setTarget=mobClass.getMethod("method_5980",livingClass);
            getNavigation=mobClass.getMethod("method_5942");
            navigationStop=navigation.getMethod("method_6340");
            blockPosCtor=blockPos.getConstructor(int.class,int.class,int.class);
            vecX=vec3.getField("field_1352");
            vecY=vec3.getField("field_1351");
            vecZ=vec3.getField("field_1350");
            ready=true;
        }
    }

    private static void tickWorld(Object world) {
        try {
            init();
            Iterable<?> entities=(Iterable<?>)worldEntities.invoke(world);
            List<Object> all=new ArrayList<>(), swimmers=new ArrayList<>();
            for(Object e:entities) {
                all.add(e);
                if(playerClass.isInstance(e)&&eligiblePlayer(e)&&water(e)) swimmers.add(e);
            }
            for(Object e:all) {
                String id=id(e);
                if(KRAKEN.equals(id)) { tickKraken(e); continue; }
                if(!DIRECT.contains(id)||!mobClass.isInstance(e)||!water(e)) continue;
                Object target=nearest(e,swimmers,aggroRange(id));
                if(target!=null) remember(e,target);
                else target=remembered(e,id);
                if(target==null) { try { setTarget.invoke(e,new Object[]{null}); } catch(Throwable ignored){} continue; }
                double d=distance(e,target);
                try { navigationStop.invoke(getNavigation.invoke(e)); } catch(Throwable ignored) {}
                double engage=engageRange(id);
                try { setTarget.invoke(e,d<=engage?target:null); } catch(Throwable ignored) {}
                steer(world,e,target,id,d,engage);
            }
        } catch(Throwable t) {
            if(!logged) { logged=true; System.err.println("[Capitale Creatures] 1.2.30 marine AI runtime failure: "+root(t)); }
        }
    }

    private static boolean eligiblePlayer(Object p) throws Exception {
        return Boolean.TRUE.equals(isAlive.invoke(p))
                && !Boolean.TRUE.equals(isSpectator.invoke(p))
                && !Boolean.TRUE.equals(isCreative.invoke(p));
    }

    private static void remember(Object mob,Object p) {
        LAST_TARGET.put(mob,p); TARGET_TTL.put(mob,20);
    }

    private static Object remembered(Object mob,String id) throws Exception {
        Object p=LAST_TARGET.get(mob); Integer ttl=TARGET_TTL.get(mob);
        if(p==null||ttl==null||ttl<=0||!playerClass.isInstance(p)||!eligiblePlayer(p)||distance(mob,p)>aggroRange(id)+8.0) {
            LAST_TARGET.remove(mob); TARGET_TTL.remove(mob); return null;
        }
        TARGET_TTL.put(mob,ttl-1);
        return p;
    }

    private static void steer(Object world,Object e,Object p,String id,double d,double engage) throws Exception {
        double dx=x(p)-x(e), dy=(y(p)+0.30)-(y(e)+0.20), dz=z(p)-z(e);
        double len=Math.sqrt(dx*dx+dy*dy+dz*dz); if(len<1e-5) return;
        double ux=dx/len,uy=dy/len,uz=dz/len;
        double[] dir=waterSafeDirection(world,e,ux,uy,uz);
        double s=chaseSpeed(id);
        if(d<=engage+0.8) s*=0.48;
        if(d<=engage) s*=0.16;
        setVelocityDDD.invoke(e,dir[0]*s,dir[1]*s,dir[2]*s);
    }

    private static double[] waterSafeDirection(Object world,Object e,double ux,double uy,double uz) throws Exception {
        if(waterAt(world,x(e)+ux*1.35,y(e)+uy*1.35,z(e)+uz*1.35)) return new double[]{ux,uy,uz};
        double[][] cand={
            {ux,Math.min(0.75,uy+0.45),uz},
            {ux,Math.max(-0.75,uy-0.45),uz},
            {ux-uz*0.55,uy,uz+ux*0.55},
            {ux+uz*0.55,uy,uz-ux*0.55}
        };
        for(double[] c:cand) {
            double l=Math.sqrt(c[0]*c[0]+c[1]*c[1]+c[2]*c[2]);
            c[0]/=l;c[1]/=l;c[2]/=l;
            if(waterAt(world,x(e)+c[0]*1.20,y(e)+c[1]*1.20,z(e)+c[2]*1.20)) return c;
        }
        return new double[]{ux*0.18,Math.max(0.04,uy*0.18),uz*0.18};
    }

    private static boolean waterAt(Object world,double x,double y,double z) throws Exception {
        Object bp=blockPosCtor.newInstance((int)Math.floor(x),(int)Math.floor(y),(int)Math.floor(z));
        return Boolean.TRUE.equals(isWater.invoke(world,bp));
    }

    private static void tickKraken(Object e) throws Exception {
        if(!water(e)) { try { setNoGravity.invoke(e,false); } catch(Throwable ignored){} return; }
        setNoGravity.invoke(e,true);
        Object v=getVelocity.invoke(e);
        double vx=((Number)vecX.get(v)).doubleValue();
        double vy=((Number)vecY.get(v)).doubleValue();
        double vz=((Number)vecZ.get(v)).doubleValue();
        boolean ground=Boolean.TRUE.equals(isOnGround.invoke(e));
        double fixed=vy;
        if(ground) fixed=Math.max(vy,0.055);
        else if(vy < -0.035) fixed=-0.010;
        if(fixed!=vy) setVelocityDDD.invoke(e,vx,fixed,vz);
    }

    private static Object nearest(Object e,List<Object> ps,double max) throws Exception {
        Object best=null; double bd=max;
        for(Object p:ps){ double d=distance(e,p); if(d<bd){bd=d;best=p;} }
        return best;
    }
    private static double aggroRange(String id){
        if(id.equals("chaos_mmo_ai:corpsefish")) return 28.0;
        if(id.equals("chaos_mmo_ai:gluttonfish")) return 30.0;
        if(id.equals("cubeanimals:piranha")) return 18.0;
        if(id.contains("mosasaurus")) return 28.0;
        if(id.contains("dunkleosteus")) return 24.0;
        if(id.contains("aegirocassis")) return 18.0;
        return 22.0;
    }
    private static double engageRange(String id){
        if(id.equals("cubeanimals:piranha")) return 1.10;
        if(id.equals("chaos_mmo_ai:corpsefish")) return 1.50;
        if(id.equals("chaos_mmo_ai:gluttonfish")) return 2.70;
        if(id.contains("aegirocassis")) return 1.60;
        if(id.contains("dunkleosteus")) return 2.10;
        if(id.contains("mosasaurus")) return 3.00;
        return 1.8;
    }
    private static double chaseSpeed(String id){
        if(id.equals("chaos_mmo_ai:corpsefish")) return 0.35;
        if(id.equals("chaos_mmo_ai:gluttonfish")) return 0.29;
        if(id.equals("cubeanimals:piranha")) return 0.315;
        if(id.contains("mosasaurus")) return 0.305;
        if(id.contains("dunkleosteus")) return 0.275;
        if(id.contains("aegirocassis")) return 0.235;
        return 0.27;
    }
    private static boolean water(Object e)throws Exception{return Boolean.TRUE.equals(touchingWater.invoke(e));}
    private static String id(Object e)throws Exception{Object t=getType.invoke(e);return String.valueOf(typeId.invoke(null,t));}
    private static double x(Object e)throws Exception{return ((Number)getX.invoke(e)).doubleValue();}
    private static double y(Object e)throws Exception{return ((Number)getY.invoke(e)).doubleValue();}
    private static double z(Object e)throws Exception{return ((Number)getZ.invoke(e)).doubleValue();}
    private static double distance(Object a,Object b)throws Exception{double dx=x(a)-x(b),dy=y(a)-y(b),dz=z(a)-z(b);return Math.sqrt(dx*dx+dy*dy+dz*dz);}
    private static Throwable root(Throwable t){while(t.getCause()!=null)t=t.getCause();return t;}
}
