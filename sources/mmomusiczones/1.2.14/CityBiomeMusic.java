package com.mmomusiczones.client;

import com.mmomusiczones.zone.MusicZone;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CityBiomeMusic {
    private static final Map<String, MusicZone> DAY = new HashMap<>();
    private static final Map<String, MusicZone> NIGHT = new HashMap<>();

    private CityBiomeMusic() {}

    public static MusicZone select(String biomeId, boolean night) {
        return (night ? NIGHT : DAY).get(biomeId);
    }

    private static void put(String biomeId, String name, int[] day, int[] night) {
        DAY.put(biomeId, zone(name + "_day", day));
        NIGHT.put(biomeId, zone(name + "_night", night));
    }

    private static MusicZone zone(String name, int[] tracks) {
        String[] sounds = new String[tracks.length];
        for (int i = 0; i < tracks.length; i++) {
            sounds[i] = String.format("mmomusiczones:music.ville_%02d", tracks[i]);
        }
        return new MusicZone(name, "minecraft:overworld",
                new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE},
                new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE},
                Integer.MAX_VALUE, List.of(sounds), 1.0F);
    }

    static {
        put("capitale:capitale", "capitale", new int[]{39}, new int[]{38});
        put("indivis:ilystara", "ilystara", new int[]{40}, new int[]{40, 63, 66});
        put("indivis:lion_port", "lion_port", new int[]{36, 35}, new int[]{27});
        put("indivis:clairval", "clairval", new int[]{37, 46, 48}, new int[]{18, 47, 49, 50});
        put("indivis:haute_rive", "haute_rive", new int[]{41, 15}, new int[]{69, 70});
        put("indivis:sylvarhen", "sylvarhen", new int[]{34, 1}, new int[]{57, 59, 68});
        put("indivis:skarnfjord", "skarnfjord", new int[]{33, 29, 61}, new int[]{24, 23, 67, 25, 26});
        put("indivis:durak_vor", "durak_vor", new int[]{26, 25, 44, 56, 65}, new int[]{22, 23, 51});
        put("indivis:avaleiv", "avaleiv", new int[]{5, 9, 60}, new int[]{13});
        put("indivis:blanche_fleche", "blanche_fleche", new int[]{6, 28}, new int[]{53});
        put("indivis:port_levant", "port_levant", new int[]{16, 17}, new int[]{62, 8, 12});
        put("indivis:sillons_d_or", "sillons_d_or", new int[]{46, 48}, new int[]{47, 49, 50, 18});
        put("indivis:havre_fort", "havre_fort", new int[]{11, 20, 21}, new int[]{52, 55});
        put("indivis:clos_des_ormes", "clos_des_ormes", new int[]{46, 48}, new int[]{47, 49, 50, 18});
        put("indivis:aubecourt", "aubecourt", new int[]{42}, new int[]{45});
        put("indivis:pointe_rouge", "pointe_rouge", new int[]{48, 31}, new int[]{50});
        put("indivis:haut_arsenal", "haut_arsenal", new int[]{43}, new int[]{54, 70});
        // Urzak-Tor volontairement absente : ville pas encore construite.
    }
}
