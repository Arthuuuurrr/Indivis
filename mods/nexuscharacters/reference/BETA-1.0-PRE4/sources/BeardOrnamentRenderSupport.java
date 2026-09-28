package net.tompsen.nexuscharacters;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Renders beard jewellery as a separate head-attached ModelPart with a metal tint. */
public final class BeardOrnamentRenderSupport {
    private static final Map<String,Object> CACHE = new ConcurrentHashMap<>();
    private static volatile Method SUBMIT;
    private static volatile Field RENDER_TINT;

    private BeardOrnamentRenderSupport() {}

    public static void render(String beardStyle, Object matrices, Object vertices, int light, Object state) {
        String ornament = CulturalBeardSupport.ornamentId(beardStyle);
        if ("none".equals(ornament)) return;
        String style = CulturalBeardSupport.baseStyle(beardStyle);
        if ("none".equals(style)) return;
        try {
            String key = style + "~" + ornament;
            Object model = CACHE.computeIfAbsent(key, k -> BeardOrnamentModelSupport.create(style, ornament));
            if (model == null) return;
            setTint(CulturalBeardSupport.ornamentArgb(ornament));
            Method submit = submitMethod();
            submit.invoke(null, model, matrices, vertices, light, state);
        } catch (Throwable ignored) {
        } finally {
            try { CosmeticColorSupport.clearRenderTint(); } catch (Throwable ignored) {}
        }
    }

    @SuppressWarnings("unchecked")
    private static void setTint(int argb) throws Exception {
        Field f = RENDER_TINT;
        if (f == null) {
            f = CosmeticColorSupport.class.getDeclaredField("RENDER_TINT");
            f.setAccessible(true);
            RENDER_TINT = f;
        }
        ThreadLocal<Integer> tl = (ThreadLocal<Integer>) f.get(null);
        tl.set(argb);
    }

    private static Method submitMethod() throws Exception {
        Method m = SUBMIT;
        if (m != null) return m;
        Class<?> c = Class.forName("net.tompsen.nexuscharacters.CharacterCosmeticFeatureRenderer");
        for (Method candidate : c.getDeclaredMethods()) {
            if (candidate.getName().equals("submit") && candidate.getParameterCount() == 5) {
                candidate.setAccessible(true);
                SUBMIT = candidate;
                return candidate;
            }
        }
        throw new NoSuchMethodException("CharacterCosmeticFeatureRenderer.submit");
    }
}
