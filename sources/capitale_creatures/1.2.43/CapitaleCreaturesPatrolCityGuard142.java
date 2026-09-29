package fr.hautecapitale.creatures.spawn;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

/**
 * 1.2.43 semantics: this guard only validates ambient patrol spawn positions.
 * It deliberately does NOT purge patrol/orc entities after they have spawned,
 * so scripted city assaults and normal movement into cities remain possible.
 */
public final class CapitaleCreaturesPatrolCityGuard142 {
    private static final int BUFFER = 16;
    private static final Set<String> CITIES = loadCities();
    private static volatile Method biomeId;
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
                System.err.println("[Capitale Creatures] 1.2.43 ambient patrol city-spawn guard failure: " + root(t));
            }
            return true;
        }
    }

    /**
     * Kept for binary compatibility with the 1.2.42-patched patrol controller.
     * Intentionally a no-op in 1.2.43: once a patrol/orc exists, city entry is allowed.
     */
    public static void purgeNearCities(Object world) {
        // Intentionally empty. Spawn prevention belongs to the ambient patrol generator only.
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

    private static Throwable root(Throwable t){while(t instanceof InvocationTargetException ite&&ite.getCause()!=null)t=ite.getCause();return t;}
}
