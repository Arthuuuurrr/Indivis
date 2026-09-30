package fr.hautecapitale.creatures.spawn;

import net.fabricmc.api.ModInitializer;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class CapitaleCreaturesDeepOceanBoost138 implements ModInitializer {
    private static final Set<String> DEEP = Set.of(
        "minecraft:deep_ocean","minecraft:deep_lukewarm_ocean","minecraft:deep_cold_ocean","minecraft:deep_frozen_ocean"
    );
    private static final int INTERVAL=40, DEEP_CAP=24, RADIUS=128, ATTEMPTS=24;
    private static int ticks;
    private static volatile boolean ready, logged;
    private static Class<?> aq, specClass, columnClass;
    private static Method aqInit,isOverworld,countNearby,findColumnAt,findWaterColumn,weighted,spawnGroup,typeExists;
    private static Field worldPlayersField,playerXField,playerZField,specsField;
    private static Field colDepth,colBiome,specMinDepth,specBiomes,specExcluded,specEntity,specWeight,specGroupMin,specGroupMax;

    @Override public void onInitialize(){
        try{
            registerEndWorldTick();
            System.out.println("[Capitale Creatures] 1.2.38 deep-ocean boost: cap=24/128, interval=40; normal oceans and rivers untouched.");
        }catch(Throwable t){System.err.println("[Capitale Creatures] 1.2.38 deep-ocean boost registration failure: "+root(t));}
    }
    private static void registerEndWorldTick() throws Exception{
        Class<?> cb=Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndWorldTick");
        Object event=Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents").getField("END_WORLD_TICK").get(null);
        Object proxy=Proxy.newProxyInstance(cb.getClassLoader(),new Class<?>[]{cb},(p,m,a)->{
            if(m.getDeclaringClass()==Object.class)return objectMethod(p,m,a);
            if(a!=null&&a.length==1)tickWorld(a[0]); return null;
        });
        Class.forName("net.fabricmc.fabric.api.event.Event").getMethod("register",Object.class).invoke(event,proxy);
    }
    private static Object objectMethod(Object p,Method m,Object[] a){return switch(m.getName()){case"toString"->"CapitaleDeepOceanBoost138";case"hashCode"->System.identityHashCode(p);case"equals"->p==(a==null?null:a[0]);default->null;};}

    private static synchronized void init() throws Exception{
        if(ready)return;
        aq=Class.forName("fr.hautecapitale.creatures.spawn.CapitaleCreaturesAquaticPopulation129");
        specClass=Class.forName("fr.hautecapitale.creatures.spawn.CapitaleCreaturesAquaticPopulation129$Spec");
        columnClass=Class.forName("fr.hautecapitale.creatures.spawn.CapitaleCreaturesAquaticPopulation129$WaterColumn");
        aqInit=method(aq,"initReflection");
        isOverworld=method(aq,"isOverworld",Object.class);
        countNearby=method(aq,"countNearby",Object.class,Object.class,int.class);
        findColumnAt=method(aq,"findColumnAt",Object.class,int.class,int.class);
        findWaterColumn=method(aq,"findWaterColumn",Object.class,Object.class,ThreadLocalRandom.class);
        weighted=method(aq,"weighted",List.class,int.class,ThreadLocalRandom.class);
        spawnGroup=method(aq,"spawnGroup",Object.class,specClass,columnClass,int.class,ThreadLocalRandom.class);
        typeExists=method(aq,"typeExists",String.class);
        worldPlayersField=field(aq,"worldPlayers"); playerXField=field(aq,"playerBlockX"); playerZField=field(aq,"playerBlockZ"); specsField=field(aq,"specs");
        colDepth=field(columnClass,"depth"); colBiome=field(columnClass,"biome");
        specMinDepth=field(specClass,"minimumWaterDepth"); specBiomes=field(specClass,"biomes"); specExcluded=field(specClass,"excludedBiomes"); specEntity=field(specClass,"entity"); specWeight=field(specClass,"weight"); specGroupMin=field(specClass,"groupMin"); specGroupMax=field(specClass,"groupMax");
        ready=true;
    }
    private static Method method(Class<?> c,String n,Class<?>...p)throws Exception{Method m=c.getDeclaredMethod(n,p);m.setAccessible(true);return m;}
    private static Field field(Class<?> c,String n)throws Exception{Field f=c.getDeclaredField(n);f.setAccessible(true);return f;}

    @SuppressWarnings("unchecked")
    private static void tickWorld(Object world){
        try{
            init(); aqInit.invoke(null);
            if(!Boolean.TRUE.equals(isOverworld.invoke(null,world)))return;
            if(++ticks%INTERVAL!=0)return;
            Method worldPlayers=(Method)worldPlayersField.get(null), px=(Method)playerXField.get(null), pz=(Method)playerZField.get(null);
            List<?> players=(List<?>)worldPlayers.invoke(world); if(players==null||players.isEmpty())return;
            for(Object player:players){
                int x=((Number)px.invoke(player)).intValue(), z=((Number)pz.invoke(player)).intValue();
                Object localColumn=findColumnAt.invoke(null,world,x,z);
                if(localColumn==null || !DEEP.contains(String.valueOf(colBiome.get(localColumn))))continue;
                int nearby=((Number)countNearby.invoke(null,world,player,RADIUS)).intValue();
                int missing=DEEP_CAP-nearby; if(missing<=0)continue;
                spawnOneDeepGroup(world,player,missing);
            }
        }catch(Throwable t){
            if(!logged){logged=true;System.err.println("[Capitale Creatures] 1.2.38 deep-ocean boost runtime failure: "+root(t));root(t).printStackTrace();}
        }
    }

    @SuppressWarnings("unchecked")
    private static void spawnOneDeepGroup(Object world,Object player,int missing)throws Exception{
        ThreadLocalRandom rng=ThreadLocalRandom.current();
        List<?> specs=(List<?>)specsField.get(null); if(specs==null||specs.isEmpty())return;
        for(int attempt=0;attempt<ATTEMPTS;attempt++){
            Object col=invoke(findWaterColumn,null,world,player,rng); if(col==null)continue;
            String biome=String.valueOf(colBiome.get(col)); if(!DEEP.contains(biome))continue;
            int depth=((Number)colDepth.get(col)).intValue();
            ArrayList<Object> candidates=new ArrayList<>(); int total=0;
            for(Object s:specs){
                if(depth<((Number)specMinDepth.get(s)).intValue())continue;
                Set<String> biomes=(Set<String>)specBiomes.get(s), excluded=(Set<String>)specExcluded.get(s);
                if(!biomes.contains(biome)||excluded.contains(biome))continue;
                String entity=String.valueOf(specEntity.get(s));
                if(!Boolean.TRUE.equals(typeExists.invoke(null,entity)))continue;
                candidates.add(s); total+=Math.max(1,((Number)specWeight.get(s)).intValue());
            }
            if(candidates.isEmpty()||total<=0)continue;
            Object chosen=weighted.invoke(null,candidates,total,rng);
            int lo=((Number)specGroupMin.get(chosen)).intValue(), hi=((Number)specGroupMax.get(chosen)).intValue();
            int count=lo+rng.nextInt(Math.max(1,hi-lo+1)); count=Math.min(count,missing);
            int spawned=((Number)spawnGroup.invoke(null,world,chosen,col,count,rng)).intValue();
            if(spawned>0)return;
        }
    }
    private static Object invoke(Method m,Object recv,Object...args)throws Exception{
        try{return m.invoke(recv,args);}catch(InvocationTargetException e){Throwable c=e.getCause();if(c instanceof Exception ex)throw ex;if(c instanceof Error er)throw er;throw e;}
    }
    private static Throwable root(Throwable t){while(t instanceof InvocationTargetException ite&&ite.getCause()!=null)t=ite.getCause();return t;}
}
