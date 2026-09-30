package fr.hautecapitale.creatures.spawn;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

public final class CapitaleCreaturesPatrolCityGuard142 {
    private static final int BUFFER = 16;
    private static final int PURGE_INTERVAL = 20;
    private static final Set<String> CITIES = loadCities();
    private static volatile Method biomeId;
    private static volatile boolean entityReflectionReady;
    private static Method worldEntities, getTags, discard, getX, getY, getZ;
    private static int purgeTicks;
    private static boolean logged;

    private CapitaleCreaturesPatrolCityGuard142() {}

    public static boolean isForbidden(Object world, int x, int y, int z, String currentBiome) {
        try {
            if (isCity(currentBiome)) return true;
            if (cityAt(world, x, y, z)) return true;
            int[][] dirs = {
                { BUFFER, 0}, {-BUFFER, 0}, {0, BUFFER}, {0,-BUFFER},
                { BUFFER, BUFFER}, { BUFFER,-BUFFER}, {-BUFFER, BUFFER}, {-BUFFER,-BUFFER}
            };
            for (int[] d : dirs) if (cityAt(world, x+d[0], y, z+d[1])) return true;
            return false;
        } catch (Throwable t) {
            if (!logged) {
                logged = true;
                System.err.println("[Capitale Creatures] 1.2.42 patrol city guard check failure: " + root(t));
            }
            return true;
        }
    }

    public static void purgeNearCities(Object world) {
        try {
            if (++purgeTicks % PURGE_INTERVAL != 0) return;
            initEntityReflection();
            Object all = worldEntities.invoke(world);
            if (!(all instanceof Iterable<?> entities)) return;
            for (Object e : entities) {
                if (e == null) continue;
                Object rawTags = getTags.invoke(e);
                if (!(rawTags instanceof Set<?> tags) || !tags.contains("indivis_orc_patrol")) continue;
                int x=(int)Math.floor(((Number)getX.invoke(e)).doubleValue());
                int y=(int)Math.floor(((Number)getY.invoke(e)).doubleValue());
                int z=(int)Math.floor(((Number)getZ.invoke(e)).doubleValue());
                if (isForbidden(world,x,y,z,null)) discard.invoke(e);
            }
        } catch (Throwable t) {
            if (!logged) {
                logged = true;
                System.err.println("[Capitale Creatures] 1.2.42 patrol city purge failure: " + root(t));
            }
        }
    }

    private static boolean cityAt(Object world, int x, int y, int z) throws Exception {
        return isCity(biome(world,x,y,z)) || isCity(biome(world,x,y+4,z)) || isCity(biome(world,x,y-4,z));
    }

    private static String biome(Object world, int x, int y, int z) throws Exception {
        Method m=biomeId;
        if(m==null){
            synchronized(CapitaleCreaturesPatrolCityGuard142.class){
                m=biomeId;
                if(m==null){
                    Class<?> patrol=Class.forName("fr.hautecapitale.creatures.spawn.CapitaleCreaturesOrcPatrol1212");
                    m=patrol.getDeclaredMethod("biomeId",Object.class,int.class,int.class,int.class);
                    m.setAccessible(true); biomeId=m;
                }
            }
        }
        Object out=m.invoke(null,world,x,y,z);
        return out==null?null:String.valueOf(out);
    }

    private static boolean isCity(String id){ return id!=null && CITIES.contains(id); }

    private static Set<String> loadCities(){
        LinkedHashSet<String> out=new LinkedHashSet<>();
        Collections.addAll(out,
            "capitale:capitale",
            "indivis:lion_port","indivis:clairval","indivis:haute_rive","indivis:ilystara","indivis:sylvarhen",
            "indivis:avaleiv","indivis:skarnfjord","indivis:durak_vor","indivis:blanche_fleche","indivis:port_levant",
            "indivis:sillons_d_or","indivis:sombrefleche","indivis:clos_des_ormes","indivis:aubecourt",
            "indivis:haut_arsenal","indivis:pointe_rouge","indivis:aurelune","indivis:havre_fort"
        );
        try(InputStream in=CapitaleCreaturesPatrolCityGuard142.class.getResourceAsStream("/data/capitale_creatures/tags/worldgen/biome/city_no_spawn.json")){
            if(in!=null){
                String s=new String(in.readAllBytes(),StandardCharsets.UTF_8);
                Matcher m=Pattern.compile("(?:capitale|indivis):[a-z0-9_]+",Pattern.CASE_INSENSITIVE).matcher(s);
                while(m.find())out.add(m.group());
            }
        }catch(Throwable ignored){}
        return Collections.unmodifiableSet(out);
    }

    private static synchronized void initEntityReflection() throws Exception {
        if(entityReflectionReady)return;
        Class<?> world=Class.forName("net.minecraft.class_3218");
        Class<?> entity=Class.forName("net.minecraft.class_1297");
        worldEntities=world.getMethod("method_27909");
        getTags=entity.getMethod("method_5752");
        discard=entity.getMethod("method_31472");
        getX=entity.getMethod("method_23317");
        getY=entity.getMethod("method_23318");
        getZ=entity.getMethod("method_23321");
        entityReflectionReady=true;
    }

    private static Throwable root(Throwable t){while(t instanceof InvocationTargetException ite&&ite.getCause()!=null)t=ite.getCause();return t;}
}
