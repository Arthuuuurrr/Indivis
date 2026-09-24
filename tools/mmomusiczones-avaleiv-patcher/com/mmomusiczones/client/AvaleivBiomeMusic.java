package com.mmomusiczones.client;

import com.mmomusiczones.zone.MusicZone;
import java.util.List;

public final class AvaleivBiomeMusic {
    private static final MusicZone DAY = zone("city_avaleiv_day", 5, 9);
    private static final MusicZone NIGHT = zone("city_avaleiv_night", 13);

    private AvaleivBiomeMusic() {}

    public static MusicZone select(String biomeId, boolean night) {
        if (!"indivis:avaleiv".equals(biomeId)) return null;
        return night ? NIGHT : DAY;
    }

    private static MusicZone zone(String name, int... tracks) {
        String[] ids = new String[tracks.length];
        for (int i = 0; i < tracks.length; i++) {
            ids[i] = String.format("mmomusiczones:music.ville_%02d", tracks[i]);
        }
        return new MusicZone(
            name,
            "minecraft:overworld",
            new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE},
            new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE},
            Integer.MAX_VALUE,
            List.of(ids),
            1.0f
        );
    }
}
