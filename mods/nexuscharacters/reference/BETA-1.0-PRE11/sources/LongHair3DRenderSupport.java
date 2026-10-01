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
 * BETA 1.0 PRE11.
 *
 * Fixes PRE10's long-hair 3D bridge without changing the character skin
 * compositor again.
 *
 * PRE10 correctly created a Skin Layers Mesh, but then attempted to submit that
 * Mesh directly to Minecraft 1.21.11's OrderedRenderCommandQueue. The queue
 * accepts ModelPart, not dev.tr7zw.skinlayers.api.Mesh, so rendering failed
 * after the PRE10 compositor had already removed the torso hair pixels.
 *
 * PRE11 keeps the PRE9/PRE10 original long-hair texture in the composed player
 * skin (safe fallback) and wraps the Skin Layers Mesh inside an empty ModelPart
 * through Skin Layers' ModelPartInjector. The normal Minecraft render queue can
 * then submit the wrapper ModelPart with a dedicated hair texture.
 */
public final class LongHair3DRenderSupport {
    private static final Pattern HAIR = Pattern.compile("(?:^|_)h(0?[0-9]|1[0-3])(?:_|$)");
    private static final Pattern HAIR_COLOR = Pattern.compile("(?:^|_)hc(0?[0-9]|1[0-2])(?:_|$)");

    private static final Map<Integer, Object> BODY_PARTS = new ConcurrentHashMap<>();

    private static volatile boolean apiChecked;
    private static volatile boolean apiAvailable;
    private static volatile boolean renderFailureLogged;

    // Conservative separation from the clothing jacket shell.
    private static final float EXTRA_X = 1.01f;
    private static final float EXTRA_Y = 1.00f;
    private static final float EXTRA_Z = 1.07f;

    private LongHair3DRenderSupport() {}

    /**
     * PRE11 safety rule: never remove torso pixels from the composed player skin.
     * If the optional 3D pass fails, the long hairstyle therefore remains fully
     * visible with the exact PRE9/PRE10 fallback rendering.
     */
    public static String hairAssetName(int hairIndex) {
        if (hairIndex <= 5) {
            return String.format(Locale.ROOT, "hair_short_%02d.png", hairIndex);
        }
        return String.format(Locale.ROOT, "hair_long_%02d.png", hairIndex - 5);
    }

    /**
     * Called by the PRE10 hook already present in CharacterCosmeticFeatureRenderer.
     * All failures remain contained so character rendering can always fall back to
     * the ordinary composed skin.
     */
    public static void render(Object renderer, Object matrices, Object queue, int light, Object state) {
        if (renderer == null || matrices == null || queue == null || state == null) return;

        boolean pushed = false;
        try {
            if (readBooleanField(state, "field_53333", false)) return;

            String marker = currentTextureId(state);
            int hairIndex = extract(marker, HAIR, 0);
            if (hairIndex < 6 || hairIndex > 10) return;

            int longIndex = hairIndex - 5;
            Object bodyPart = getBodyPart(longIndex);
            if (bodyPart == null) return;

            Object model = invokeNoArg(renderer, "method_17165");
            if (model == null) return;

            // PlayerEntityModel.jacket. We only borrow its current pose transform;
            // the hair mesh itself is rendered using our empty wrapper ModelPart.
            Object jacket = readField(model, "field_3483");
            if (jacket == null) return;

            invokeNoArg(matrices, "method_22903");
            pushed = true;
            invokeOneArg(jacket, "method_22703", matrices);

            Object hairTexture = resourceId(
                    "nexuscharacters",
                    String.format(Locale.ROOT,
                            "textures/cosmetic/long_hair_body/hair_long_body_%02d.png",
                            longIndex));
            Object renderLayer = renderLayer(hairTexture);
            int overlay = playerOverlay(state);
            int tint = hairTint(marker);

            Method submit = findCompatibleSubmit(queue.getClass(), bodyPart, matrices, renderLayer);
            submit.invoke(queue, bodyPart, matrices, renderLayer, light, overlay, null, tint, null);
        } catch (Throwable t) {
            if (!renderFailureLogged) {
                renderFailureLogged = true;
                System.err.println(
                        "[NexusCharacters] PRE11 long-hair 3D pass unavailable; PRE9 fallback kept visible: " + t);
            }
        } finally {
            if (pushed) {
                try {
                    invokeNoArg(matrices, "method_22909");
                } catch (Throwable ignored) {}
            }
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

    private static Object getBodyPart(int longIndex) {
        if (longIndex < 1 || longIndex > 5) return null;

        Object cached = BODY_PARTS.get(longIndex);
        if (cached != null) return cached;
        if (!ensureApi()) return null;

        synchronized (BODY_PARTS) {
            cached = BODY_PARTS.get(longIndex);
            if (cached != null) return cached;

            Object image = null;
            try (InputStream in = LongHair3DRenderSupport.class.getResourceAsStream(
                    String.format(Locale.ROOT,
                            "/assets/nexuscharacters/textures/cosmetic/long_hair_body/hair_long_body_%02d.png",
                            longIndex))) {
                if (in == null) return null;

                Class<?> nativeImage = Class.forName("net.minecraft.class_1011");
                Method read = nativeImage.getMethod("method_4309", InputStream.class);
                image = read.invoke(null, in);

                Class<?> api = Class.forName("dev.tr7zw.skinlayers.api.SkinLayersAPI");
                Object meshHelper = api.getMethod("getMeshHelper").invoke(null);
                Method create = findCreate3DMesh(meshHelper.getClass(), nativeImage);

                // Exact BODY parameters used by 3D Skin Layers itself.
                Object mesh = create.invoke(
                        meshHelper,
                        image,
                        8, 12, 4,
                        16, 32,
                        true,
                        0.0f);

                if (mesh == null) return null;

                // Empty Minecraft ModelPart accepted by the 1.21.11 render queue.
                Object part = BeardVoxelModelFactory.bake();
                if (part == null) return null;

                Object offset = createHairBodyOffset();

                // Skin Layers' ModelPartMixin adds ModelPartInjector at runtime.
                Method setInjectedMesh = findMethod(part.getClass(), "setInjectedMesh", 2);
                setInjectedMesh.invoke(part, mesh, offset);

                BODY_PARTS.put(longIndex, part);
                return part;
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

    private static Object createHairBodyOffset() throws Exception {
        Class<?> offsetProviderClass = Class.forName("dev.tr7zw.skinlayers.api.OffsetProvider");
        Object bodyOffset = offsetProviderClass.getField("BODY").get(null);
        Method bodyApply = findMethod(offsetProviderClass, "applyOffset", 2);

        return Proxy.newProxyInstance(
                offsetProviderClass.getClassLoader(),
                new Class<?>[]{offsetProviderClass},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("applyOffset".equals(name) && args != null && args.length == 2) {
                        bodyApply.invoke(bodyOffset, args[0], args[1]);
                        invokeFloats(args[0], "method_22905", EXTRA_X, EXTRA_Y, EXTRA_Z);
                        return null;
                    }
                    if ("toString".equals(name)) return "NexusLongHairBodyOffset";
                    if ("hashCode".equals(name)) return System.identityHashCode(proxy);
                    if ("equals".equals(name)) return proxy == (args == null ? null : args[0]);
                    return null;
                });
    }

    private static boolean ensureApi() {
        if (apiChecked) return apiAvailable;
        synchronized (LongHair3DRenderSupport.class) {
            if (apiChecked) return apiAvailable;
            try {
                Class<?> api = Class.forName("dev.tr7zw.skinlayers.api.SkinLayersAPI");
                Class<?> mesh = Class.forName("dev.tr7zw.skinlayers.api.Mesh");
                Class<?> offset = Class.forName("dev.tr7zw.skinlayers.api.OffsetProvider");
                Class<?> injector = Class.forName("dev.tr7zw.skinlayers.accessor.ModelPartInjector");

                api.getMethod("getMeshHelper");
                offset.getField("BODY");
                injector.getMethod("setInjectedMesh", mesh, offset);
                apiAvailable = true;
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
            if (!m.getName().equals("method_23622")
                    || !Modifier.isStatic(m.getModifiers())
                    || m.getParameterCount() != 2) {
                continue;
            }
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
        Method m = id.getMethod("method_60655", String.class, String.class);
        return m.invoke(null, namespace, path);
    }

    private static Object renderLayer(Object textureId) throws Exception {
        Class<?> renderLayer = Class.forName("net.minecraft.class_12249");
        for (Method m : renderLayer.getDeclaredMethods()) {
            if (!m.getName().equals("method_75982")
                    || !Modifier.isStatic(m.getModifiers())
                    || m.getParameterCount() != 1) {
                continue;
            }
            Class<?> p = m.getParameterTypes()[0];
            if (!p.isInstance(textureId)) continue;
            m.setAccessible(true);
            return m.invoke(null, textureId);
        }
        throw new NoSuchMethodException("class_12249.method_75982(texture)");
    }

    private static Method findCreate3DMesh(Class<?> helperClass, Class<?> nativeImageClass)
            throws Exception {
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

    private static Method findCompatibleSubmit(
            Class<?> queueClass, Object modelPart, Object matrices, Object renderLayer)
            throws Exception {
        for (Class<?> c = queueClass; c != null; c = c.getSuperclass()) {
            for (Method m : c.getDeclaredMethods()) {
                if (!m.getName().equals("method_73492") || m.getParameterCount() != 8) continue;
                Class<?>[] p = m.getParameterTypes();
                if (!p[0].isInstance(modelPart)) continue;
                if (!p[1].isInstance(matrices)) continue;
                if (!p[2].isInstance(renderLayer)) continue;
                m.setAccessible(true);
                return m;
            }
        }
        throw new NoSuchMethodException("OrderedRenderCommandQueue.method_73492(ModelPart,...)");
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

    private static void invokeFloats(
            Object target, String name, float a, float b, float c) throws Exception {
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
