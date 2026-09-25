package fr.hautecapitale.creatures.spawn;

import net.fabricmc.api.ModInitializer;
import java.lang.reflect.*;
import java.util.*;

/** Runtime density guard for natural/unnamed Autonomous Orc Mobs. */
public final class CapitaleCreaturesOrcDensity131 implements ModInitializer {
    private static final Set<String> ORCS = Set.of(
        "autonomous_orc_mobs:orc_archer",
        "autonomous_orc_mobs:orc_warrior",
        "autonomous_orc_mobs:female_orc_warrior",
        "autonomous_orc_mobs:female_orc_warrior_red",
        "autonomous_orc_mobs:orc_warlock",
        "autonomous_orc_mobs:orc_champion",
        "autonomous_orc_mobs:female_orc_elite",
        "autonomous_orc_mobs:female_orc",
        "autonomous_orc_mobs:orc_chief",
        "autonomous_orc_mobs:orc_ravager",
        "autonomous_orc_mobs:orc_ravager_direwolf"
    );
    private static final int CORRUPTION_CAP = 4;
    private static final int DUNGEON_CAP = 10;
    private static final double RADIUS = 96.0;
    private static long ticks = 0;
    private static volatile boolean ready=false, logged=false;
    private static Method players, entities, biomeAt, registryEntryId, getType, typeId, getX,getY,getZ, discard, hasCustomName;
    private static Constructor<?> blockPosCtor;
    private static Class<?> playerClass;

    @Override public void onInitialize() {
        try {
            register();
            System.out.println("[Capitale Creatures] 1.2.31 orc density guard active: corruption cap=4, dungeon cap=10 within 96 blocks; named/scripted orcs preserved.");
        } catch(Throwable t) { System.err.println("[Capitale Creatures] orc density guard init failed: "+root(t)); }
    }

    private static void register() throws Exception {
        Class<?> listener=Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndWorldTick");
        Class<?> holder=Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents");
        Class<?> eventCls=Class.forName("net.fabricmc.fabric.api.event.Event");
        Object event=holder.getField("END_WORLD_TICK").get(null);
        Object proxy=Proxy.newProxyInstance(listener.getClassLoader(),new Class<?>[]{listener},(p,m,a)->{
            if(m.getDeclaringClass()==Object.class) return switch(m.getName()) {
                case "toString" -> "CapitaleCreaturesOrcDensity131";
                case "hashCode" -> System.identityHashCode(p);
                case "equals" -> p==(a==null||a.length==0?null:a[0]);
                default -> null;
            };
            if(a!=null&&a.length>0&&a[0]!=null) tick(a[0]);
            return null;
        });
        eventCls.getMethod("register",Object.class).invoke(event,proxy);
    }

    private static void init() throws Exception {
        if(ready) return;
        synchronized(CapitaleCreaturesOrcDensity131.class) {
            if(ready) return;
            Class<?> world=Class.forName("net.minecraft.class_3218");
            Class<?> entity=Class.forName("net.minecraft.class_1297");
            Class<?> type=Class.forName("net.minecraft.class_1299");
            Class<?> blockPos=Class.forName("net.minecraft.class_2338");
            Class<?> registryEntry=Class.forName("net.minecraft.class_6880");
            playerClass=Class.forName("net.minecraft.class_3222");
            players=world.getMethod("method_18456");
            entities=world.getMethod("method_27909");
            biomeAt=world.getMethod("method_23753",blockPos);
            registryEntryId=registryEntry.getMethod("method_55840");
            getType=entity.getMethod("method_5864");
            typeId=type.getMethod("method_5890",type);
            getX=entity.getMethod("method_23317"); getY=entity.getMethod("method_23318"); getZ=entity.getMethod("method_23321");
            discard=entity.getMethod("method_31472");
            try { hasCustomName=entity.getMethod("method_16914"); } catch(Throwable ignored) { hasCustomName=null; }
            blockPosCtor=blockPos.getConstructor(int.class,int.class,int.class);
            ready=true;
        }
    }

    private static void tick(Object world) {
        try {
            if((++ticks % 40L)!=0L) return;
            init();
            List<?> ps=(List<?>)players.invoke(world);
            if(ps==null||ps.isEmpty()) return;
            List<Object> all=new ArrayList<>();
            for(Object e:(Iterable<?>)entities.invoke(world)) all.add(e);
            for(Object p:ps) {
                String pb=biome(world,x(p),y(p),z(p));
                if(!"indivis:corruption".equals(pb)&&!"capitale:donjon".equals(pb)) continue;
                int cap="indivis:corruption".equals(pb)?CORRUPTION_CAP:DUNGEON_CAP;
                List<Object> candidates=new ArrayList<>();
                for(Object e:all) {
                    if(!ORCS.contains(id(e))) continue;
                    if(named(e)) continue;
                    double d2=distanceSq(p,e); if(d2>RADIUS*RADIUS) continue;
                    String eb=biome(world,x(e),y(e),z(e));
                    if(!pb.equals(eb)) continue;
                    candidates.add(e);
                }
                if(candidates.size()<=cap) continue;
                candidates.sort((a,b)->Double.compare(safeDistanceSq(p,b),safeDistanceSq(p,a)));
                int remove=candidates.size()-cap;
                for(int i=0;i<remove;i++) {
                    Object e=candidates.get(i);
                    try { discard.invoke(e); } catch(Throwable ignored) {}
                }
            }
        } catch(Throwable t) {
            if(!logged) { logged=true; System.err.println("[Capitale Creatures] orc density guard runtime failure: "+root(t)); }
        }
    }

    private static boolean named(Object e) {
        if(hasCustomName==null) return false;
        try { return Boolean.TRUE.equals(hasCustomName.invoke(e)); } catch(Throwable t) { return false; }
    }
    private static String id(Object e)throws Exception{Object t=getType.invoke(e);return String.valueOf(typeId.invoke(null,t));}
    private static double x(Object e)throws Exception{return ((Number)getX.invoke(e)).doubleValue();}
    private static double y(Object e)throws Exception{return ((Number)getY.invoke(e)).doubleValue();}
    private static double z(Object e)throws Exception{return ((Number)getZ.invoke(e)).doubleValue();}
    private static double distanceSq(Object a,Object b)throws Exception{double dx=x(a)-x(b),dy=y(a)-y(b),dz=z(a)-z(b);return dx*dx+dy*dy+dz*dz;}
    private static double safeDistanceSq(Object a,Object b){try{return distanceSq(a,b);}catch(Throwable t){return 0;}}
    private static String biome(Object world,double xd,double yd,double zd)throws Exception{
        Object pos=blockPosCtor.newInstance((int)Math.floor(xd),(int)Math.floor(yd),(int)Math.floor(zd));
        Object entry=biomeAt.invoke(world,pos); if(entry==null)return "";
        Object key=registryEntryId.invoke(entry); String s=String.valueOf(key);
        for(String target:new String[]{"indivis:corruption","capitale:donjon"}) if(s.contains(target)) return target;
        return s;
    }
    private static Throwable root(Throwable t){while(t.getCause()!=null)t=t.getCause();return t;}
}
