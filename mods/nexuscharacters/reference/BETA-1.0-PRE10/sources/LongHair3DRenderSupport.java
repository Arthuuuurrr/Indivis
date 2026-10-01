package net.tompsen.nexuscharacters;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BETA 1.0 PRE10.
 *
 * Optional bridge to 3D Skin Layers' public API. Long-hair torso pixels are
 * rendered as their own voxel mesh instead of being baked into the same player
 * jacket layer as clothing.
 *
 * The bridge is reflection-only: NexusCharacters keeps working when
 * skinlayers3d is absent or its API is unavailable.
 */
public final class LongHair3DRenderSupport {
    private static final Pattern HAIR = Pattern.compile("(?:^|_)h(0?[0-9]|1[0-3])(?:_|$)");
    private static final Pattern HAIR_COLOR = Pattern.compile("(?:^|_)hc(0?[0-9]|1[0-2])(?:_|$)");

    private static final Map<Integer, Object> BODY_MESHES = new ConcurrentHashMap<>();

    private static volatile boolean apiChecked;
    private static volatile boolean apiAvailable;
    private static volatile boolean renderFailureLogged;

    // Extra spacing on top of skinlayers3d's BODY offset. Deliberately small:
    // enough to separate hair from jacket voxels without making it float.
    private static final float EXTRA_X = 1.01f;
    private static final float EXTRA_Y = 1.00f;
    private static final float EXTRA_Z = 1.07f;

    private LongHair3DRenderSupport() {}

    /**
     * Replacement for DynamicAppearanceSupport.hairName(int).
     * Short hair and head-only long hair remain unchanged.
     * For long hair 01..05, switch to the head-only texture only after the
     * 3D body mesh has been successfully constructed.
     */
    public static String hairAssetName(int hairIndex) {
        if (hairIndex <= 5) {
            return String.format(java.util.Locale.ROOT, "hair_short_%02d.png", hairIndex);
        }

        int longIndex = hairIndex - 5;
        String original = String.format(java.util.Locale.ROOT, "hair_long_%02d.png", longIndex);

        // Long 06..08 have no torso hair in the current assets.
        if (longIndex < 1 || longIndex > 5) {
            return original;
        }

        return getBodyMesh(longIndex) != null
                ? String.format(java.util.Locale.ROOT, "hair_long_%02d_head3d.png", longIndex)
                : original;
    }

    /**
     * Called from CharacterCosmeticFeatureRenderer at the start of its render
     * method. Every failure is contained here so this optional visual feature
     * cannot take down character rendering.
     */
    public static void render(Object renderer, Object matrices, Object queue, int light, Object state) {
        if (renderer == null || matrices == null || queue == null || state == null) return;

        boolean pushed = false;
        try {
            if (readBooleanField(state, "field_53333", false)) return;
            Object skinSet = readField(state, "field_53520");
            if (skinSet == null) return;

            Object skin = invokeNoArg(skinSet, "comp_1626");
            if (skin == null) return;
            Object textureId = invokeNoArg(skin, "comp_3627");
            if (textureId == null) return;

            String marker = String.valueOf(textureId);
            int hairIndex = extract(marker, HAIR, 0);
            if (hairIndex < 6 || hairIndex > 10) return;

            int longIndex = hairIndex - 5;
            Object mesh = getBodyMesh(longIndex);
            if (mesh == null) return;

            Object model = invokeNoArg(renderer, "method_17165");
            if (model == null) return;

            // field_3483 is PlayerEntityModel.jacket: this is the exact model
            // part skinlayers3d uses for its BODY mesh injection.
            Object jacket = readField(model, "field_3483");
            if (jacket == null) return;

            invokeNoArg(matrices, "method_22903");
            pushed = true;

            invokeOneArg(jacket, "method_22703", matrices);

            Class<?> offsetProviderClass = Class.forName("dev.tr7zw.skinlayers.api.OffsetProvider");
            Object bodyOffset = offsetProviderClass.getField("BODY").get(null);
            Method applyOffset = findMethod(offsetProviderClass, "applyOffset", 2);
            applyOffset.invoke(bodyOffset, matrices, mesh);

            // Separate the dedicated hair shell very slightly from the jacket
            // shell. This is applied after skinlayers3d's own BODY scale.
            invokeFloats(matrices, "method_22905", EXTRA_X, EXTRA_Y, EXTRA_Z);

            int overlay = playerOverlay(state);
            Object hairTexture = resourceId(
                    "nexuscharacters",
                    String.format(java.util.Locale.ROOT,
                            "textures/cosmetic/long_hair_body/hair_long_body_%02d.png", longIndex));
            Object renderLayer = renderLayer(hairTexture);
            int tint = hairTint(marker);

            Method submit = findCompatibleSubmit(queue.getClass(), mesh, matrices, renderLayer);
            submit.invoke(queue, mesh, matrices, renderLayer, light, overlay, null, tint, null);
        } catch (Throwable t) {
            if (!renderFailureLogged) {
                renderFailureLogged = true;
                System.err.println("[NexusCharacters] PRE10 long-hair 3D bridge disabled after render failure: " + t);
            }
        } finally {
            if (pushed) {
                try {
                    invokeNoArg(matrices, "method_22909");
                } catch (Throwable ignored) {}
            }
        }
    }

    private static Object getBodyMesh(int longIndex) {
        if (longIndex < 1 || longIndex > 5) return null;
        Object cached = BODY_MESHES.get(longIndex);
        if (cached != null) return cached;
        if (!ensureApi()) return null;

        synchronized (BODY_MESHES) {
            cached = BODY_MESHES.get(longIndex);
            if (cached != null) return cached;

            Object image = null;
            try (InputStream in = LongHair3DRenderSupport.class.getResourceAsStream(
                    String.format(java.util.Locale.ROOT,
                            "/assets/nexuscharacters/textures/cosmetic/long_hair_body/hair_long_body_%02d.png",
                            longIndex))) {
                if (in == null) return null;

                Class<?> nativeImage = Class.forName("net.minecraft.class_1011");
                Method read = nativeImage.getMethod("method_4309", InputStream.class);
                image = read.invoke(null, in);

                Class<?> api = Class.forName("dev.tr7zw.skinlayers.api.SkinLayersAPI");
                Object meshHelper = api.getMethod("getMeshHelper").invoke(null);
                Method create = findCreate3DMesh(meshHelper.getClass(), nativeImage);

                Object mesh = create.invoke(meshHelper,
                        image,
                        8, 12, 4,
                        16, 32,
                        true,
                        0.0f);

                if (mesh == null) return null;
                BODY_MESHES.put(longIndex, mesh);
                return mesh;
            } catch (Throwable t) {
                apiAvailable = false;
                return null;
            } finally {
                if (image != null) {
                    try {
                        Method close = image.getClass().getMethod("close");
                        close.invoke(image);
                    } catch (Throwable ignored) {}
                }
            }
        }
    }

    private static boolean ensureApi() {
        if (apiChecked) return apiAvailable;
        synchronized (LongHair3DRenderSupport.class) {
            if (apiChecked) return apiAvailable;
            try {
                Class<?> api = Class.forName("dev.tr7zw.skinlayers.api.SkinLayersAPI");
                Class<?> mesh = Class.forName("dev.tr7zw.skinlayers.api.Mesh");
                Class<?> offset = Class.forName("dev.tr7zw.skinlayers.api.OffsetProvider");
                if (api.getMethod("getMeshHelper") == null || mesh == null || offset.getField("BODY") == null) {
                    apiAvailable = false;
                } else {
                    apiAvailable = true;
                }
            } catch (Throwable ignored) {
                apiAvailable = false;
            }
            apiChecked = true;
            return apiAvailable;
        }
    }

    private static int hairTint(String marker) {
        int colorIndex = extract(marker, HAIR_COLOR, 0);
        if (colorIndex <= 0) return -1;
        try {
            Method m = Appearance69Support.class.getMethod("hairColorArgb", int.class);
            return (Integer)m.invoke(null, colorIndex);
        } catch (Throwable ignored) {
            return -1;
        }
    }

    private static int playerOverlay(Object state) throws Exception {
        Class<?> renderer = Class.forName("net.minecraft.class_922");
        for (Method m : renderer.getDeclaredMethods()) {
            if (!m.getName().equals("method_23622") || !Modifier.isStatic(m.getModifiers()) || m.getParameterCount() != 2) {
                continue;
            }
            m.setAccessible(true);
            Object value = m.invoke(null, state, 0.0f);
            if (value instanceof Integer i) return i;
        }
        return 0;
    }

    private static Object resourceId(String namespace, String path) throws Exception {
        Class<?> id = Class.forName("net.minecraft.class_2960");
        Method m = id.getMethod("method_60655", String.class, String.class);
        return m.invoke(null, namespace, path);
    }

    private static Object renderLayer(Object textureId) throws Exception {
        Class<?> renderLayer = Class.forName("net.minecraft.class_12249");
        for (Method m : renderLayer.getDeclaredMethods()) {
            if (!m.getName().equals("method_75982") || !Modifier.isStatic(m.getModifiers()) || m.getParameterCount() != 1) {
                continue;
            }
            m.setAccessible(true);
            return m.invoke(null, textureId);
        }
        throw new NoSuchMethodException("class_12249.method_75982");
    }

    private static Method findCreate3DMesh(Class<?> helperClass, Class<?> nativeImageClass) throws Exception {
        for (Method m : helperClass.getMethods()) {
            if (!m.getName().equals("create3DMesh") || m.getParameterCount() != 8) continue;
            Class<?>[] p = m.getParameterTypes();
            if (p[0].isAssignableFrom(nativeImageClass) || nativeImageClass.isAssignableFrom(p[0])) {
                m.setAccessible(true);
                return m;
            }
        }
        throw new NoSuchMethodException("MeshHelper.create3DMesh(NativeImage,...8 args)");
    }

    private static Method findCompatibleSubmit(Class<?> queueClass, Object mesh, Object matrices, Object renderLayer)
            throws Exception {
        for (Class<?> c = queueClass; c != null; c = c.getSuperclass()) {
            for (Method m : c.getDeclaredMethods()) {
                if (!m.getName().equals("method_73492") || m.getParameterCount() != 8) continue;
                Class<?>[] p = m.getParameterTypes();
                if (!p[0].isInstance(mesh)) continue;
                if (!p[1].isInstance(matrices)) continue;
                if (!p[2].isInstance(renderLayer)) continue;
                m.setAccessible(true);
                return m;
            }
        }
        throw new NoSuchMethodException("render queue method_73492");
    }

    private static int extract(String text, Pattern pattern, int def) {
        Matcher matcher = pattern.matcher(text == null ? "" : text);
        if (!matcher.find()) return def;
        try {
            return Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException ignored) {
            return def;
        }
    }

    private static Object readField(Object target, String name) throws Exception {
        Field f = findField(target.getClass(), name);
        f.setAccessible(true);
        return f.get(target);
    }

    private static boolean readBooleanField(Object target, String name, boolean def) {
        try {
            Object v = readField(target, name);
            return v instanceof Boolean b ? b : def;
        } catch (Throwable ignored) {
            return def;
        }
    }

    private static Field findField(Class<?> type, String name) throws Exception {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {}
        }
        throw new NoSuchFieldException(name);
    }

    private static Method findMethod(Class<?> type, String name, int params) throws Exception {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.getName().equals(name) && m.getParameterCount() == params) {
                    m.setAccessible(true);
                    return m;
                }
            }
        }
        for (Method m : type.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == params) {
                m.setAccessible(true);
                return m;
            }
        }
        throw new NoSuchMethodException(name + "/" + params);
    }

    private static Object invokeNoArg(Object target, String name) throws Exception {
        Method m = findMethod(target.getClass(), name, 0);
        return m.invoke(target);
    }

    private static Object invokeOneArg(Object target, String name, Object arg) throws Exception {
        for (Class<?> c = target.getClass(); c != null; c = c.getSuperclass()) {
            for (Method m : c.getDeclaredMethods()) {
                if (!m.getName().equals(name) || m.getParameterCount() != 1) continue;
                if (arg != null && !m.getParameterTypes()[0].isInstance(arg)) continue;
                m.setAccessible(true);
                return m.invoke(target, arg);
            }
        }
        throw new NoSuchMethodException(name + "/1");
    }

    private static void invokeFloats(Object target, String name, float a, float b, float c) throws Exception {
        for (Class<?> k = target.getClass(); k != null; k = k.getSuperclass()) {
            for (Method m : k.getDeclaredMethods()) {
                if (!m.getName().equals(name) || m.getParameterCount() != 3) continue;
                Class<?>[] p = m.getParameterTypes();
                if (p[0] == float.class && p[1] == float.class && p[2] == float.class) {
                    m.setAccessible(true);
                    m.invoke(target, a, b, c);
                    return;
                }
            }
        }
        throw new NoSuchMethodException(name + "(float,float,float)");
    }
}
