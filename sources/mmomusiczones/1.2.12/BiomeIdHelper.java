package com.mmomusiczones.client;

import net.minecraft.class_6880;

public final class BiomeIdHelper {
    private BiomeIdHelper() {}

    public static String getIdAsString(class_6880<?> biome) {
        String id = biome.method_55840();
        if ("indivis:corruption".equals(id)
                || "capitale:corruption".equals(id)
                || "indivis:donjon".equals(id)) {
            return "capitale:donjon";
        }
        return id;
    }
}
