package net.tompsen.nexuscharacters;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PRE16 worldless hair overlay. PlayerSkinWidget does not run the normal
 * CharacterCosmeticFeatureRenderer, so the dedicated Skin Layers hair mesh has
 * to be rendered through its own widget, like the already working beard preview.
 */
public final class WorldlessHairWidgetRenderer {
    private static final String HEAD_CHILD = "nexuscharacters$hair_head";
    private static final String BODY_CHILD = "nexuscharacters$hair_body";
    private static final Pattern HAIR = Pattern.compile("(?:^|_)h(\\d{1,2})(?:_|$)");
    private static final Pattern COLOR = Pattern.compile("(?:^|_)hc(\\d{1,2})(?:_|$)");

    private static Object hairWidget;
    private static Object currentDto;
    private static int configuredHair = -1;
    private static String lastReadyKey;
    private static String lastFailure;

    private WorldlessHairWidgetRenderer() {}

    public static void render(Object drawContext, Object dto, Object mainWidget,
                              int x, int y, int width, int height,
                              int mouseX, int mouseY, float delta) {
        if (drawContext == null || dto == null || mainWidget == null) return;
        String stage = "BEGIN";
        try {
            String marker = appearanceMarker(dto);
            int hair = extract(HAIR, marker, 0);
            int color = extract(COLOR, marker, 0);
            if (hair < 1 || hair > 13) return;

            currentDto = dto;
            stage = "PARTS";
            Object[] parts = LongHair3DRenderSupport.previewParts(hair);
            if (parts == null || parts.length < 2 || parts[0] == null) {
                throw new IllegalStateException("dedicated hair HEAD mesh unavailable for h" + hair);
            }

            stage = "WIDGET";
            Object overlay = ensureWidget();

            stage = "LAYOUT";
            int mainX = intInvoke(mainWidget, "method_46426");
            int mainY = intInvoke(mainWidget, "method_46427");
            int mainW = intInvoke(mainWidget, "method_25368");
            int mainH = intInvoke(mainWidget, "method_25364");
            invoke(overlay, "method_55445", 2, mainW, mainH);
            invoke(overlay, "method_46421", 1, mainX);
            invoke(overlay, "method_46419", 1, mainY);

            stage = "ROTATION";
            Field mainXRot = requireField(mainWidget.getClass(), "field_46005");
            Field overlayXRot = requireField(overlay.getClass(), "field_46005");
            Field overlayYRot = requireField(overlay.getClass(), "field_46006");
            overlayXRot.setFloat(overlay, mainXRot.getFloat(mainWidget));
            overlayYRot.setFloat(overlay, 30.0f + PreviewDragInput.getYawDegrees());

            stage = "MODELS";
            Object mainWide = fieldValue(mainWidget, "field_59834");
            Object mainSlim = fieldValue(mainWidget, "field_59835");
            Object overlayWide = fieldValue(overlay, "field_59834");
            Object overlaySlim = fieldValue(overlay, "field_59835");

            if (hair != configuredHair) {
                configureModel(overlayWide, parts);
                configureModel(overlaySlim, parts);
                configuredHair = hair;
            }

            stage = "MORPH_WIDE";
            syncModelTransforms(mainWide, overlayWide);
            stage = "MORPH_SLIM";
            syncModelTransforms(mainSlim, overlaySlim);

            stage = "SUBMIT";
            invoke(overlay, "method_25394", 4, drawContext, mouseX, mouseY, delta);

            String key = marker + "|h" + hair + "|hc" + color;
            if (!key.equals(lastReadyKey)) {
                lastReadyKey = key;
                lastFailure = null;
                System.out.println("[NexusCharacters][HairPreview3D] READY h=" + hair
                        + " hc=" + color + " head=true body=" + (parts[1] != null));
            }
        } catch (Throwable throwable) {
            String fp = stage + "|" + throwable.getClass().getName() + "|" + String.valueOf(throwable.getMessage());
            if (!fp.equals(lastFailure)) {
                lastFailure = fp;
                System.err.println("[NexusCharacters][HairPreview3D] FAIL stage=" + stage + " : " + throwable);
                throwable.printStackTrace();
            }
        }
    }

    private static Object ensureWidget() throws Exception {
        if (hairWidget != null) return hairWidget;
        Class<?> mcClass = Class.forName("net.minecraft.class_310");
        Object client = mcClass.getMethod("method_1551").invoke(null);
        if (client == null) throw new IllegalStateException("MinecraftClient null");
        Object loadedModels = invoke(client, "method_31974", 0);
        if (loadedModels == null) throw new IllegalStateException("LoadedEntityModels null");

        Class<?> widgetClass = Class.forName("net.minecraft.class_8765");
        Constructor<?> selected = null;
        for (Constructor<?> c : widgetClass.getDeclaredConstructors()) {
            if (c.getParameterCount() != 4) continue;
            Class<?>[] p = c.getParameterTypes();
            if (p[0] == Integer.TYPE && p[1] == Integer.TYPE
                    && Supplier.class.isAssignableFrom(p[3])) {
                selected = c;
                break;
            }
        }
        if (selected == null) throw new NoSuchMethodException("PlayerSkinWidget(int,int,models,Supplier)");
        selected.setAccessible(true);
        Supplier<Object> supplier = WorldlessHairWidgetRenderer::currentHairSkin;
        hairWidget = selected.newInstance(100, 150, loadedModels, supplier);
        return hairWidget;
    }

    private static Object currentHairSkin() {
        try {
            if (currentDto == null) throw new IllegalStateException("DTO hair absent in Supplier");
            String marker = appearanceMarker(currentDto);
            int hair = extract(HAIR, marker, 0);
            int color = extract(COLOR, marker, 0);
            String path;
            if (color <= 0) {
                path = String.format("textures/cosmetic/hair_3d/hair_%02d.png", hair);
            } else if (marker.contains("player_v69_")) {
                path = String.format("textures/cosmetic/hair_3d_tinted/v69/hc%02d/hair_%02d.png", color, hair);
            } else {
                path = String.format("textures/cosmetic/hair_3d_tinted/legacy/hc%02d/hair_%02d.png", color, hair);
            }
            return skinTextures(path, hair, color);
        } catch (Throwable throwable) {
            throw new RuntimeException("Unable to create PRE16 hair SkinTextures", throwable);
        }
    }

    private static Object skinTextures(String texturePath, int hair, int color) throws Exception {
        Object textureId = identifier(texturePath);
        Object cacheId = identifier(String.format("hair_overlay/h%02d_hc%02d", hair, color));

        Class<?> refClass = Class.forName("net.minecraft.class_12079$class_10726");
        Constructor<?> refCtor = null;
        for (Constructor<?> c : refClass.getDeclaredConstructors()) {
            if (c.getParameterCount() == 2) { refCtor = c; break; }
        }
        if (refCtor == null) throw new NoSuchMethodException("TextureRef(cache,texture)");
        refCtor.setAccessible(true);
        Object textureRef = refCtor.newInstance(cacheId, textureId);

        Object model = null;
        try {
            Object base = previewSkin(currentDto);
            if (base != null) model = invoke(base, "comp_1629", 0);
        } catch (Throwable ignored) {}
        if (model == null) {
            Class<?> modelClass = Class.forName("net.minecraft.class_7920");
            Field f = requireField(modelClass, "field_41123");
            model = f.get(null);
        }

        Class<?> skinsClass = Class.forName("net.minecraft.class_8685");
        Constructor<?> ctor = null;
        for (Constructor<?> c : skinsClass.getDeclaredConstructors()) {
            if (c.getParameterCount() == 5) { ctor = c; break; }
        }
        if (ctor == null) throw new NoSuchMethodException("SkinTextures/5");
        ctor.setAccessible(true);
        return ctor.newInstance(textureRef, null, null, model, false);
    }

    private static Object previewSkin(Object dto) throws Exception {
        Class<?> c = Class.forName("net.tompsen.nexuscharacters.PreviewDummyPlayerManager");
        Method m = findMethod(c, "skin", 1);
        if (m == null) throw new NoSuchMethodException("PreviewDummyPlayerManager.skin");
        return m.invoke(null, dto);
    }

    private static Object identifier(String path) throws Exception {
        Class<?> id = Class.forName("net.minecraft.class_2960");
        Method m = id.getMethod("method_60655", String.class, String.class);
        return m.invoke(null, "nexuscharacters", path);
    }

    private static void configureModel(Object model, Object[] parts) throws Exception {
        if (model == null) throw new IllegalStateException("overlay PlayerEntityModel null");
        invoke(model, "method_2805", 1, false);

        Object head = invoke(model, "method_2838", 0);
        Object body = fieldValue(model, "field_3391");
        if (head == null || body == null) throw new IllegalStateException("head/body parent missing");

        setBoolean(head, "field_3665", true);
        setBoolean(head, "field_38456", true);
        setBoolean(body, "field_3665", true);
        setBoolean(body, "field_38456", true);

        attachChild(head, HEAD_CHILD, parts[0]);
        attachChild(body, BODY_CHILD, parts[1]);
    }

    @SuppressWarnings("unchecked")
    private static void attachChild(Object parent, String key, Object child) throws Exception {
        Field childrenField = requireField(parent.getClass(), "field_3661");
        Map<String,Object> children = (Map<String,Object>)childrenField.get(parent);
        if (children == null) {
            children = new LinkedHashMap<>();
            childrenField.set(parent, children);
        } else {
            try {
                children.clear();
            } catch (UnsupportedOperationException ex) {
                children = new LinkedHashMap<>();
                childrenField.set(parent, children);
            }
        }
        if (child != null) {
            setBoolean(child, "field_3665", true);
            setBoolean(child, "field_38456", false);
            children.put(key, child);
        }
    }

    private static void syncModelTransforms(Object source, Object target) throws Exception {
        if (source == null || target == null) throw new IllegalStateException("source/target model null");
        Class<?> modelPartClass = Class.forName("net.minecraft.class_630");
        for (Class<?> c = source.getClass(); c != null; c = c.getSuperclass()) {
            for (Field sf : c.getDeclaredFields()) {
                if (sf.getType() != modelPartClass) continue;
                sf.setAccessible(true);
                Object sourcePart = sf.get(source);
                if (sourcePart == null) continue;
                Field tf = findField(target.getClass(), sf.getName());
                if (tf == null) continue;
                Object targetPart = tf.get(target);
                if (targetPart == null) continue;
                Object pose = invoke(sourcePart, "method_32084", 0);
                invoke(targetPart, "method_32085", 1, pose);
            }
        }
    }

    private static String appearanceMarker(Object dto) throws Exception {
        Object raw = invoke(dto, "skinUsername", 0);
        String marker = raw == null ? "" : raw.toString();
        try {
            Class<?> p = Class.forName("net.tompsen.nexuscharacters.PresetSkinSupport");
            Method m = findMethod(p, "markerId", 1);
            if (m != null) {
                Object normalized = m.invoke(null, marker);
                if (normalized != null) marker = normalized.toString();
            }
        } catch (Throwable ignored) {}
        return marker;
    }

    private static int extract(Pattern pattern, String text, int fallback) {
        if (text == null) return fallback;
        Matcher m = pattern.matcher(text);
        if (!m.find()) return fallback;
        try { return Integer.parseInt(m.group(1)); }
        catch (NumberFormatException ex) { return fallback; }
    }

    private static int intInvoke(Object object, String name) throws Exception {
        Object v = invoke(object, name, 0);
        return ((Number)v).intValue();
    }

    private static Object fieldValue(Object object, String name) throws Exception {
        Field f = requireField(object.getClass(), name);
        return f.get(object);
    }

    private static Field requireField(Class<?> type, String name) throws Exception {
        Field f = findField(type, name);
        if (f == null) throw new NoSuchFieldException(type.getName() + "." + name);
        return f;
    }

    private static void setBoolean(Object object, String name, boolean value) throws Exception {
        Field f = requireField(object.getClass(), name);
        f.setBoolean(object, value);
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

    private static Object invoke(Object object, String name, int count, Object... args) throws Exception {
        Method m = findMethod(object.getClass(), name, count);
        if (m == null) throw new NoSuchMethodException(object.getClass().getName() + "." + name + "/" + count);
        return m.invoke(object, args);
    }
}
