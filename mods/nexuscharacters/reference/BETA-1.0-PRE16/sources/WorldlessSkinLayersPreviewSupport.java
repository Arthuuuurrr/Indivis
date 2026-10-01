package net.tompsen.nexuscharacters;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PRE16: applies the real 3D Skin Layers mesh algorithm to the PlayerSkinWidget
 * used by the worldless character-creation preview.
 *
 * The main composed skin is already hair-stripped when the dedicated hair mesh
 * is available. This class restores 3D depth for the remaining vanilla outer
 * layers (hat/jacket/sleeves/pants) without touching their textures.
 */
public final class WorldlessSkinLayersPreviewSupport {
    private static final Map<String, Object[]> MESH_CACHE = new ConcurrentHashMap<>();
    private static String lastReadyKey;
    private static String lastFailure;

    private WorldlessSkinLayersPreviewSupport() {}

    public static void apply(Object widget, Object dto) {
        if (widget == null || dto == null) return;
        String stage = "BEGIN";
        try {
            stage = "MODELS";
            Object wide = fieldValue(widget, "field_59834");
            Object slim = fieldValue(widget, "field_59835");
            if (wide == null || slim == null) {
                throw new IllegalStateException("PlayerSkinWidget models missing");
            }

            // Never leave stale injected meshes after a skin/selection change.
            clearOuter(wide);
            clearOuter(slim);

            stage = "SKIN";
            Object skinTextures = previewSkin(dto);
            Object textureId = textureId(skinTextures);
            if (textureId == null) {
                throw new IllegalStateException("dynamic preview texture id missing");
            }

            String key = String.valueOf(textureId);
            Object[] meshes = MESH_CACHE.get(key);
            if (meshes == null) {
                stage = "TEXTURE";
                Object image = skinImage(textureId);
                if (image == null) {
                    throw new IllegalStateException("Skin Layers cannot resolve " + key);
                }
                stage = "MESH";
                meshes = buildMeshes(image);
                MESH_CACHE.put(key, meshes);
            }

            stage = "INJECT_WIDE";
            injectModel(wide, meshes, false);
            stage = "INJECT_SLIM";
            injectModel(slim, meshes, true);

            if (!key.equals(lastReadyKey)) {
                lastReadyKey = key;
                lastFailure = null;
                System.out.println("[NexusCharacters][SkinLayersPreview] READY texture=" + key
                        + " outer=hat,jacket,sleeves,pants");
            }
        } catch (Throwable throwable) {
            String fp = stage + "|" + throwable.getClass().getName() + "|" + String.valueOf(throwable.getMessage());
            if (!fp.equals(lastFailure)) {
                lastFailure = fp;
                System.err.println("[NexusCharacters][SkinLayersPreview] FAIL stage=" + stage + " : " + throwable);
                throwable.printStackTrace();
            }
        }
    }

    private static Object previewSkin(Object dto) throws Exception {
        Class<?> c = Class.forName("net.tompsen.nexuscharacters.PreviewDummyPlayerManager");
        Method m = findMethod(c, "skin", 1);
        if (m == null) throw new NoSuchMethodException("PreviewDummyPlayerManager.skin");
        return m.invoke(null, dto);
    }

    private static Object textureId(Object skinTextures) throws Exception {
        if (skinTextures == null) return null;
        Object first = invokeNoArg(skinTextures, "comp_1626");
        if (first == null) return null;
        if ("net.minecraft.class_2960".equals(first.getClass().getName())) return first;
        Object id = invokeNoArg(first, "comp_3627");
        if (id != null && "net.minecraft.class_2960".equals(id.getClass().getName())) return id;
        return id;
    }

    private static Object skinImage(Object textureId) throws Exception {
        Class<?> skinUtil = Class.forName("dev.tr7zw.skinlayers.SkinUtil");
        for (Method m : skinUtil.getMethods()) {
            if (!m.getName().equals("getTexture") || m.getParameterCount() != 2) continue;
            if (!"net.minecraft.class_2960".equals(m.getParameterTypes()[0].getName())) continue;
            return m.invoke(null, textureId, null);
        }
        throw new NoSuchMethodException("SkinUtil.getTexture(Identifier, SkullSettings)");
    }

    private static Object[] buildMeshes(Object image) throws Exception {
        Object helper = meshHelper();
        Object[] out = new Object[8];
        out[0] = mesh(helper, image, 8, 8, 8, 32, 0, false, 0.6f);   // hat
        out[1] = mesh(helper, image, 8, 12, 4, 16, 32, true, 0.0f);  // jacket
        out[2] = mesh(helper, image, 4, 12, 4, 0, 48, true, 0.0f);   // left pants
        out[3] = mesh(helper, image, 4, 12, 4, 0, 32, true, 0.0f);   // right pants
        out[4] = mesh(helper, image, 4, 12, 4, 48, 48, true, -2.0f); // left sleeve wide
        out[5] = mesh(helper, image, 4, 12, 4, 40, 32, true, -2.0f); // right sleeve wide
        out[6] = mesh(helper, image, 3, 12, 4, 48, 48, true, -2.0f); // left sleeve slim
        out[7] = mesh(helper, image, 3, 12, 4, 40, 32, true, -2.0f); // right sleeve slim
        for (int i = 0; i < out.length; i++) {
            if (out[i] == null) throw new IllegalStateException("Skin Layers mesh " + i + " is null");
        }
        return out;
    }

    private static Object meshHelper() throws Exception {
        Class<?> api = Class.forName("dev.tr7zw.skinlayers.api.SkinLayersAPI");
        Method m = api.getMethod("getMeshHelper");
        return m.invoke(null);
    }

    private static Object mesh(Object helper, Object image,
                               int w, int h, int d, int u, int v,
                               boolean topPivot, float rotation) throws Exception {
        Class<?> iface = Class.forName("dev.tr7zw.skinlayers.api.MeshHelper");
        for (Method m : iface.getMethods()) {
            if (!m.getName().equals("create3DMesh") || m.getParameterCount() != 8) continue;
            return m.invoke(helper, image, w, h, d, u, v, topPivot, rotation);
        }
        throw new NoSuchMethodException("MeshHelper.create3DMesh/8");
    }

    private static void injectModel(Object model, Object[] meshes, boolean slim) throws Exception {
        Object hat = fieldValue(model, "field_3394");
        Object jacket = fieldValue(model, "field_3483");
        Object leftSleeve = fieldValue(model, "field_3484");
        Object rightSleeve = fieldValue(model, "field_3486");
        Object leftPants = fieldValue(model, "field_3482");
        Object rightPants = fieldValue(model, "field_3479");

        inject(hat, meshes[0], offset("HEAD"));
        inject(jacket, meshes[1], offset("BODY"));
        inject(leftPants, meshes[2], offset("LEFT_LEG"));
        inject(rightPants, meshes[3], offset("RIGHT_LEG"));
        inject(leftSleeve, slim ? meshes[6] : meshes[4], offset(slim ? "LEFT_ARM_SLIM" : "LEFT_ARM"));
        inject(rightSleeve, slim ? meshes[7] : meshes[5], offset(slim ? "RIGHT_ARM_SLIM" : "RIGHT_ARM"));
    }

    private static void clearOuter(Object model) throws Exception {
        for (String field : new String[]{"field_3394","field_3483","field_3484","field_3486","field_3482","field_3479"}) {
            Object part = fieldValue(model, field);
            inject(part, null, null);
        }
    }

    private static Object offset(String name) throws Exception {
        Class<?> c = Class.forName("dev.tr7zw.skinlayers.api.OffsetProvider");
        return c.getField(name).get(null);
    }

    private static void inject(Object part, Object mesh, Object offset) throws Exception {
        if (part == null) throw new IllegalStateException("outer ModelPart missing");
        Class<?> injector = Class.forName("dev.tr7zw.skinlayers.accessor.ModelPartInjector");
        Method m = null;
        for (Method candidate : injector.getMethods()) {
            if (candidate.getName().equals("setInjectedMesh") && candidate.getParameterCount() == 2) {
                m = candidate;
                break;
            }
        }
        if (m == null) throw new NoSuchMethodException("ModelPartInjector.setInjectedMesh");
        m.invoke(part, mesh, offset);
        setBoolean(part, "field_3665", true);
        setBoolean(part, "field_38456", false);
    }

    private static Object fieldValue(Object object, String name) throws Exception {
        Field f = findField(object.getClass(), name);
        if (f == null) throw new NoSuchFieldException(object.getClass().getName() + "." + name);
        return f.get(object);
    }

    private static void setBoolean(Object object, String name, boolean value) throws Exception {
        Field f = findField(object.getClass(), name);
        if (f != null) f.setBoolean(object, value);
    }

    private static Object invokeNoArg(Object object, String name) throws Exception {
        Method m = findMethod(object.getClass(), name, 0);
        if (m == null) throw new NoSuchMethodException(object.getClass().getName() + "." + name);
        return m.invoke(object);
    }

    private static Field findField(Class<?> type, String name) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException ignored) {}
        }
        return null;
    }

    private static Method findMethod(Class<?> type, String name, int count) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            for (Method m : c.getDeclaredMethods()) {
                if (!m.getName().equals(name) || m.getParameterCount() != count) continue;
                m.setAccessible(true);
                return m;
            }
        }
        for (Method m : type.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == count) return m;
        }
        return null;
    }
}
