package net.tompsen.nexuscharacters;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BETA 1.0 PRE17.
 *
 * Dedicated 3D hair renderer using 3D Skin Layers' MeshHelper. PRE17 also removes the PRE14 nested-record packaging failure that prevented the renderer from ever initialising. Unlike PRE12,
 * hair is not expected to become a separate object merely because it occupies
 * the vanilla jacket UVs. The hair-only mesh is generated from dedicated
 * hair-only textures and submitted as its own ModelPart.
 */
public final class LongHair3DRenderSupport {
    private static final Pattern HAIR = Pattern.compile("(?:^|_)h(0?[0-9]|1[0-3])(?:_|$)");
    private static final Pattern HAIR_COLOR = Pattern.compile("(?:^|_)hc(0?[0-9]|1[0-2])(?:_|$)");

    private static final Map<Integer, Object[]> PARTS = new ConcurrentHashMap<>();

    private static volatile int apiState; // 0 unknown, 1 available, -1 unavailable
    private static volatile boolean successLogged;
    private static volatile boolean failureLogged;

    // Additional separation after Skin Layers' own offsets.
    // Body hair is deliberately farther out than the vanilla jacket mesh.
    private static final float BODY_EXTRA_X = 1.16f;
    private static final float BODY_EXTRA_Y = 1.02f;
    private static final float BODY_EXTRA_Z = 1.45f;

    // Small extra volume for hair on the head; enough to make the hair asset
    // visibly voxelised without making the whole hairstyle float.
    private static final float HEAD_EXTRA = 1.035f;

    private LongHair3DRenderSupport() {}

    /**
     * PRE17 removes the flat outer hair from the composed skin only after the
     * dedicated mesh has been built successfully. Merely finding the Skin Layers
     * classes is not enough to strip the fallback anymore.
     */
    public static String hairAssetName(int hairIndex) {
        boolean ready = rendererReadyFor(hairIndex);
        if (hairIndex <= 5) {
            return String.format(Locale.ROOT,
                    ready ? "hair_short_%02d_pre13.png" : "hair_short_%02d.png",
                    hairIndex);
        }
        int longIndex = hairIndex - 5;
        return String.format(Locale.ROOT,
                ready ? "hair_long_%02d_pre13.png" : "hair_long_%02d.png",
                longIndex);
    }

    private static boolean rendererReadyFor(int hairIndex) {
        if (hairIndex < 1 || hairIndex > 13 || !skinLayersAvailable()) return false;
        return getParts(hairIndex) != null;
    }

    /**
     * Called from the existing CharacterCosmeticFeatureRenderer hook.
     * The hook is already used by both the real player renderer and the
     * character-creation preview renderer.
     */
    public static void render(Object renderer, Object matrices, Object queue, int light, Object state) {
        if (renderer == null || matrices == null || queue == null || state == null) return;
        if (!skinLayersAvailable()) return;

        try {
            if (readBooleanField(state, "field_53333", false)) return;

            String textureString = currentTextureId(state);
            int hairIndex = extract(textureString, HAIR, 0);
            if (hairIndex < 1 || hairIndex > 13) return;

            Object[] parts = getParts(hairIndex);
            if (parts == null || parts.length < 2 || parts[0] == null) return;

            Object model = invokeNoArg(renderer, "method_17165");
            if (model == null) return;

            Object head = readField(model, "field_3398");
            // Yarn 1.21.11: field_3391 = body, field_3394 = hat.
            // PRE16 still used the hat transform here in the normal entity
            // renderer; PRE17 anchors the torso hair to the actual torso.
            Object body = readField(model, "field_3391");
            int hairColorIndex = extract(textureString, HAIR_COLOR, 0);
            String texturePath;
            if (hairColorIndex <= 0) {
                texturePath = String.format(
                        Locale.ROOT,
                        "textures/cosmetic/hair_3d/hair_%02d.png",
                        hairIndex);
            } else if (textureString.contains("player_v69_")) {
                texturePath = String.format(
                        Locale.ROOT,
                        "textures/cosmetic/hair_3d_tinted/v69/hc%02d/hair_%02d.png",
                        hairColorIndex,
                        hairIndex);
            } else {
                texturePath = String.format(
                        Locale.ROOT,
                        "textures/cosmetic/hair_3d_tinted/legacy/hc%02d/hair_%02d.png",
                        hairColorIndex,
                        hairIndex);
            }

            Object textureId = resourceId("nexuscharacters", texturePath);
            Object renderLayer = renderLayer(textureId);
            int overlay = playerOverlay(state);
            int tint = -1;

            submitPart(queue, matrices, light, overlay, tint, renderLayer, head, parts[0]);

            if (parts[1] != null && body != null) {
                submitPart(queue, matrices, light, overlay, tint, renderLayer, body, parts[1]);
            }

            if (!successLogged) {
                successLogged = true;
                System.out.println("[NexusCharacters] PRE17 dedicated 3D hair mesh active via 3D Skin Layers.");
            }
        } catch (Throwable t) {
            if (!failureLogged) {
                failureLogged = true;
                System.err.println("[NexusCharacters] PRE17 dedicated 3D hair render failed: " + t);
                t.printStackTrace();
            }
        }
    }

    private static void submitPart(
            Object queue,
            Object matrices,
            int light,
            int overlay,
            int tint,
            Object renderLayer,
            Object parentPart,
            Object customPart) throws Exception {
        if (parentPart == null || customPart == null) return;

        invokeNoArg(matrices, "method_22903");
        try {
            invokeOneArg(parentPart, "method_22703", matrices);
            Method submit = findCompatibleSubmit(queue.getClass(), customPart, matrices, renderLayer);
            submit.invoke(queue, customPart, matrices, renderLayer, light, overlay, null, tint, null);
        } finally {
            invokeNoArg(matrices, "method_22909");
        }
    }

    /**
     * PRE17 worldless creation preview bridge. Returns a shallow copy so the
     * preview renderer can attach the already validated Skin Layers meshes
     * without exposing the mutable cache entry.
     */
    public static Object[] previewParts(int hairIndex) {
        Object[] parts = getParts(hairIndex);
        if (parts == null || parts.length < 2) return null;
        return new Object[]{parts[0], parts[1]};
    }

    private static Object[] getParts(int hairIndex) {
        Object[] cached = PARTS.get(hairIndex);
        if (cached != null) return cached;

        synchronized (PARTS) {
            cached = PARTS.get(hairIndex);
            if (cached != null) return cached;

            Object image = null;
            try (InputStream in = LongHair3DRenderSupport.class.getResourceAsStream(
                    String.format(Locale.ROOT,
                            "/assets/nexuscharacters/textures/cosmetic/hair_3d/hair_%02d.png",
                            hairIndex))) {
                if (in == null) throw new IllegalStateException("Missing PRE13 hair texture " + hairIndex);

                Class<?> nativeImage = Class.forName("net.minecraft.class_1011");
                Method read = nativeImage.getMethod("method_4309", InputStream.class);
                image = read.invoke(null, in);

                Class<?> api = Class.forName("dev.tr7zw.skinlayers.api.SkinLayersAPI");
                Object meshHelper = api.getMethod("getMeshHelper").invoke(null);
                Class<?> meshHelperType = Class.forName("dev.tr7zw.skinlayers.api.MeshHelper");
                Method create = meshHelperType.getMethod(
                        "create3DMesh",
                        nativeImage,
                        int.class, int.class, int.class,
                        int.class, int.class,
                        boolean.class, float.class);

                // Same head mesh dimensions/UV origin used by Skin Layers.
                Object headMesh = create.invoke(meshHelper, image, 8, 8, 8, 32, 0, false, 0.6f);
                Object headPart = wrapMesh(headMesh, createOffset("HEAD", HEAD_EXTRA, HEAD_EXTRA, HEAD_EXTRA));

                Object bodyPart = null;
                if (hairIndex >= 6 && hairIndex <= 10) {
                    // Same torso dimensions/UV origin used by Skin Layers.
                    Object bodyMesh = create.invoke(meshHelper, image, 8, 12, 4, 16, 32, true, 0.0f);
                    bodyPart = wrapMesh(
                            bodyMesh,
                            createOffset("BODY", BODY_EXTRA_X, BODY_EXTRA_Y, BODY_EXTRA_Z));
                }

                Object[] result = new Object[]{headPart, bodyPart};
                PARTS.put(hairIndex, result);
                return result;
            } catch (Throwable t) {
                if (!failureLogged) {
                    failureLogged = true;
                    System.err.println("[NexusCharacters] PRE17 could not initialise 3D hair meshes: " + t);
                    t.printStackTrace();
                }
                return null;
            } finally {
                if (image != null) {
                    try {
                        image.getClass().getMethod("close").invoke(image);
                    } catch (Throwable ignored) {}
                }
            }
        }
    }

    private static Object wrapMesh(Object mesh, Object offsetProvider) throws Exception {
        if (mesh == null) return null;

        Object part = BeardVoxelModelFactory.bake();
        Class<?> injector = Class.forName("dev.tr7zw.skinlayers.accessor.ModelPartInjector");
        Class<?> meshType = Class.forName("dev.tr7zw.skinlayers.api.Mesh");
        Class<?> offsetType = Class.forName("dev.tr7zw.skinlayers.api.OffsetProvider");

        if (!injector.isInstance(part)) {
            throw new IllegalStateException("Minecraft ModelPart is not transformed by 3D Skin Layers");
        }

        Method set = injector.getMethod("setInjectedMesh", meshType, offsetType);
        set.invoke(part, mesh, offsetProvider);
        return part;
    }

    private static Object createOffset(String fieldName, float sx, float sy, float sz) throws Exception {
        Class<?> offsetType = Class.forName("dev.tr7zw.skinlayers.api.OffsetProvider");
        Object delegate = offsetType.getField(fieldName).get(null);
        Method apply = offsetType.getMethod(
                "applyOffset",
                Class.forName("net.minecraft.class_4587"),
                Class.forName("dev.tr7zw.skinlayers.api.Mesh"));

        return Proxy.newProxyInstance(
                offsetType.getClassLoader(),
                new Class<?>[]{offsetType},
                (proxy, method, args) -> {
                    if ("applyOffset".equals(method.getName()) && args != null && args.length == 2) {
                        apply.invoke(delegate, args[0], args[1]);
                        invokeFloats(args[0], "method_22905", sx, sy, sz);
                        return null;
                    }
                    if ("toString".equals(method.getName())) {
                        return "NexusPRE17HairOffset[" + fieldName + "]";
                    }
                    if ("hashCode".equals(method.getName())) return System.identityHashCode(proxy);
                    if ("equals".equals(method.getName())) return proxy == (args == null ? null : args[0]);
                    return null;
                });
    }

    private static boolean skinLayersAvailable() {
        int state = apiState;
        if (state != 0) return state > 0;

        synchronized (LongHair3DRenderSupport.class) {
            if (apiState != 0) return apiState > 0;
            try {
                Class<?> api = Class.forName("dev.tr7zw.skinlayers.api.SkinLayersAPI");
                Class<?> injector = Class.forName("dev.tr7zw.skinlayers.accessor.ModelPartInjector");
                Class<?> mesh = Class.forName("dev.tr7zw.skinlayers.api.Mesh");
                Class<?> offset = Class.forName("dev.tr7zw.skinlayers.api.OffsetProvider");
                api.getMethod("getMeshHelper");
                injector.getMethod("setInjectedMesh", mesh, offset);
                offset.getField("HEAD");
                offset.getField("BODY");
                apiState = 1;
            } catch (Throwable ignored) {
                apiState = -1;
            }
            return apiState > 0;
        }
    }

    private static String currentTextureId(Object state) throws Exception {
        Object skinSet = readField(state, "field_53520");
        if (skinSet == null) return "";
        Object skin = invokeNoArg(skinSet, "comp_1626");
        if (skin == null) return "";
        Object textureId = invokeNoArg(skin, "comp_3627");
        return textureId == null ? "" : String.valueOf(textureId);
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
            if (!m.getName().equals("method_23622")
                    || !Modifier.isStatic(m.getModifiers())
                    || m.getParameterCount() != 2) continue;
            Class<?>[] p = m.getParameterTypes();
            if (!p[0].isInstance(state) || p[1] != float.class) continue;
            m.setAccessible(true);
            Object value = m.invoke(null, state, 0.0f);
            if (value instanceof Integer i) return i;
        }
        return 0;
    }

    private static Object resourceId(String namespace, String path) throws Exception {
        Class<?> id = Class.forName("net.minecraft.class_2960");
        return id.getMethod("method_60655", String.class, String.class)
                .invoke(null, namespace, path);
    }

    private static Object renderLayer(Object textureId) throws Exception {
        Class<?> renderLayer = Class.forName("net.minecraft.class_12249");
        for (Method m : renderLayer.getDeclaredMethods()) {
            if (!m.getName().equals("method_75982")
                    || !Modifier.isStatic(m.getModifiers())
                    || m.getParameterCount() != 1) continue;
            if (!m.getParameterTypes()[0].isInstance(textureId)) continue;
            m.setAccessible(true);
            return m.invoke(null, textureId);
        }
        throw new NoSuchMethodException("class_12249.method_75982(texture)");
    }

    /**
     * PRE11 only searched declared methods on the concrete queue class.
     * On 1.21.11 method_73492 can be inherited from the public queue contract,
     * so that lookup could miss the exact method that the working beard renderer
     * calls directly. PRE13 searches the complete public method surface first.
     */
    private static Method findCompatibleSubmit(
            Class<?> queueClass, Object modelPart, Object matrices, Object renderLayer)
            throws Exception {
        for (Method m : queueClass.getMethods()) {
            if (isCompatibleSubmit(m, modelPart, matrices, renderLayer)) {
                m.setAccessible(true);
                return m;
            }
        }
        for (Class<?> c = queueClass; c != null; c = c.getSuperclass()) {
            for (Method m : c.getDeclaredMethods()) {
                if (isCompatibleSubmit(m, modelPart, matrices, renderLayer)) {
                    m.setAccessible(true);
                    return m;
                }
            }
        }
        throw new NoSuchMethodException("method_73492(ModelPart,MatrixStack,RenderLayer,...)");
    }

    private static boolean isCompatibleSubmit(
            Method m, Object modelPart, Object matrices, Object renderLayer) {
        if (!m.getName().equals("method_73492") || m.getParameterCount() != 8) return false;
        Class<?>[] p = m.getParameterTypes();
        return p[0].isInstance(modelPart)
                && p[1].isInstance(matrices)
                && p[2].isInstance(renderLayer);
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
        return findMethod(target.getClass(), name, 0).invoke(target);
    }

    private static Object invokeOneArg(Object target, String name, Object arg) throws Exception {
        for (Method m : target.getClass().getMethods()) {
            if (!m.getName().equals(name) || m.getParameterCount() != 1) continue;
            if (arg != null && !m.getParameterTypes()[0].isInstance(arg)) continue;
            m.setAccessible(true);
            return m.invoke(target, arg);
        }
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

    private static void invokeFloats(
            Object target, String name, float a, float b, float c) throws Exception {
        for (Method m : target.getClass().getMethods()) {
            if (!m.getName().equals(name) || m.getParameterCount() != 3) continue;
            Class<?>[] p = m.getParameterTypes();
            if (p[0] == float.class && p[1] == float.class && p[2] == float.class) {
                m.setAccessible(true);
                m.invoke(target, a, b, c);
                return;
            }
        }
        throw new NoSuchMethodException(name + "(float,float,float)");
    }
}
