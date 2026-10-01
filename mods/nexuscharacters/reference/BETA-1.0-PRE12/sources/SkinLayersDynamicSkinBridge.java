package net.tompsen.nexuscharacters;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * BETA 1.0 PRE12 compatibility bridge for 3D Skin Layers / TRansition.
 *
 * 3D Skin Layers asks TRansition's PlayerUtil for the player's skin identifier.
 * NexusCharacters changes the render-state SkinTextures, but does not change the
 * underlying GameProfile skin. Therefore Skin Layers kept voxelising the account
 * skin instead of Nexus' generated appearance.
 *
 * This bridge resolves the Nexus preset tag and returns the already registered
 * dynamic Nexus texture identifier.
 */
public final class SkinLayersDynamicSkinBridge {
    private static final String TAG_PREFIX = "nexuscharacters.skin.";
    private static volatile boolean failureLogged;

    private SkinLayersDynamicSkinBridge() {}

    public static Object resolve(Object player) {
        if (player == null) return null;

        try {
            Method tagsMethod = findNoArg(player.getClass(), "method_5752");
            if (tagsMethod == null) return null;

            Object rawTags = tagsMethod.invoke(player);
            if (!(rawTags instanceof Set<?> tags)) return null;

            String presetId = null;
            for (Object raw : tags) {
                if (!(raw instanceof String tag) || !tag.startsWith(TAG_PREFIX)) continue;
                String candidate = tag.substring(TAG_PREFIX.length());
                if (!candidate.isBlank()) {
                    presetId = candidate;
                    break;
                }
            }
            if (presetId == null) return null;

            // This also guarantees that the NativeImageBackedTexture is
            // registered in Minecraft's TextureManager before Skin Layers reads it.
            Class<?> presetSupport = Class.forName("net.tompsen.nexuscharacters.PresetSkinSupport");
            Method textures = presetSupport.getMethod("textures", String.class);
            Object skinTextures = textures.invoke(null, presetId);
            if (skinTextures == null) return null;

            Method bodyMethod = findNoArg(skinTextures.getClass(), "comp_1626");
            if (bodyMethod == null) return null;
            Object bodyAsset = bodyMethod.invoke(skinTextures);
            if (bodyAsset == null) return null;

            Method texturePathMethod = findNoArg(bodyAsset.getClass(), "comp_3627");
            if (texturePathMethod == null) return null;
            return texturePathMethod.invoke(bodyAsset);
        } catch (Throwable t) {
            if (!failureLogged) {
                failureLogged = true;
                System.err.println("[NexusCharacters] PRE12 Skin Layers dynamic-skin bridge unavailable: " + t);
            }
            return null;
        }
    }

    private static Method findNoArg(Class<?> type, String name) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try {
                Method m = c.getDeclaredMethod(name);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException ignored) {}
        }
        for (Method m : type.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == 0) {
                m.setAccessible(true);
                return m;
            }
        }
        return null;
    }
}
