package net.tompsen.nexuscharacters;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** BETA 1.0 PRE4 cultural beard model catalogue. Name retained for binary compatibility. */
public final class LongBeardModelSupport {
    private LongBeardModelSupport() {}

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map extendBeardMap(Map original) {
        LinkedHashMap copy = new LinkedHashMap(original);
        for (String style : new String[]{"long","double_braid","triple_braid","runic","segmented","wild","split","trimmed","noble_goatee"}) {
            copy.put(style, createCustom(style));
        }
        return java.util.Collections.unmodifiableMap(copy);
    }

    public static Object createForStyle(String styleKey) {
        String raw = styleKey == null ? "none" : styleKey.trim().toLowerCase(Locale.ROOT);
        String ornament = "none";
        int tilde = raw.indexOf('~');
        if (tilde >= 0) {
            ornament = CulturalBeardSupport.canonicalOrnament(raw.substring(tilde + 1));
            raw = raw.substring(0, tilde);
        }
        String style = CulturalBeardSupport.baseStyle(raw);
        Object beard = isBuiltIn(style) ? createBuiltIn(style) : createCustom(style);
        if (beard == null) return null;
        if (!"none".equals(ornament)) {
            Object jewel = BeardOrnamentModelSupport.create(style, ornament);
            BeardVoxelModelFactory.attach(beard, "nexuscharacters$beard_ornament", jewel);
        }
        return beard;
    }

    private static boolean isBuiltIn(String style) {
        return style.equals("short") || style.equals("full") || style.equals("forked") || style.equals("braided");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Object createBuiltIn(String style) {
        try {
            Class<?> shape = Class.forName("net.tompsen.nexuscharacters.CharacterCosmeticFeatureRenderer$BeardShape");
            Object enumValue = Enum.valueOf((Class<? extends Enum>) shape.asSubclass(Enum.class), style.toUpperCase(Locale.ROOT));
            Class<?> renderer = Class.forName("net.tompsen.nexuscharacters.CharacterCosmeticFeatureRenderer");
            Method method = renderer.getDeclaredMethod("createBeardPart", shape);
            method.setAccessible(true);
            return method.invoke(null, enumValue);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to create built-in beard " + style, e);
        }
    }

    private static Object createCustom(String style) {
        return switch (style) {
            case "long" -> BeardVoxelModelFactory.bake(
                    BeardVoxelModelFactory.c(-3.15f,-2.15f,-4.73f,2.58f,1.18f,0.82f),
                    BeardVoxelModelFactory.c( 0.57f,-2.15f,-4.73f,2.58f,1.18f,0.82f),
                    BeardVoxelModelFactory.c(-3.42f,-1.05f,-4.69f,1.02f,3.15f,0.76f),
                    BeardVoxelModelFactory.c( 2.40f,-1.05f,-4.69f,1.02f,3.15f,0.76f),
                    BeardVoxelModelFactory.c(-1.62f, 0.65f,-4.68f,3.24f,3.45f,0.80f),
                    BeardVoxelModelFactory.c(-1.62f, 3.75f,-4.64f,0.70f,4.55f,0.66f),
                    BeardVoxelModelFactory.c(-0.36f, 4.15f,-4.65f,0.72f,4.90f,0.68f),
                    BeardVoxelModelFactory.c( 0.92f, 3.75f,-4.64f,0.70f,4.55f,0.66f)
            );
            case "trimmed" -> BeardVoxelModelFactory.bake(
                    BeardVoxelModelFactory.c(-3.05f,-2.10f,-4.73f,2.55f,1.10f,0.80f),
                    BeardVoxelModelFactory.c( 0.50f,-2.10f,-4.73f,2.55f,1.10f,0.80f),
                    BeardVoxelModelFactory.c(-3.22f,-0.95f,-4.68f,1.00f,2.70f,0.74f),
                    BeardVoxelModelFactory.c( 2.22f,-0.95f,-4.68f,1.00f,2.70f,0.74f),
                    BeardVoxelModelFactory.c(-2.78f, 0.30f,-4.67f,5.56f,2.85f,0.78f),
                    BeardVoxelModelFactory.c(-1.82f, 2.70f,-4.63f,3.64f,1.65f,0.68f)
            );
            case "noble_goatee" -> BeardVoxelModelFactory.bake(
                    BeardVoxelModelFactory.c(-3.02f,-2.12f,-4.73f,2.45f,1.05f,0.78f),
                    BeardVoxelModelFactory.c( 0.57f,-2.12f,-4.73f,2.45f,1.05f,0.78f),
                    BeardVoxelModelFactory.c(-1.38f, 0.05f,-4.68f,2.76f,3.55f,0.76f),
                    BeardVoxelModelFactory.c(-1.05f, 3.10f,-4.64f,2.10f,2.10f,0.68f),
                    BeardVoxelModelFactory.c(-0.68f, 4.85f,-4.61f,1.36f,1.75f,0.60f)
            );
            case "wild" -> BeardVoxelModelFactory.bake(
                    BeardVoxelModelFactory.c(-3.25f,-2.15f,-4.74f,2.72f,1.30f,0.84f),
                    BeardVoxelModelFactory.c( 0.48f,-2.08f,-4.73f,2.67f,1.14f,0.82f),
                    BeardVoxelModelFactory.c(-3.40f,-0.95f,-4.70f,1.30f,4.25f,0.82f),
                    BeardVoxelModelFactory.c( 2.18f,-0.65f,-4.69f,1.18f,3.65f,0.80f),
                    BeardVoxelModelFactory.c(-2.72f, 1.10f,-4.68f,4.85f,3.10f,0.82f),
                    BeardVoxelModelFactory.c(-2.42f, 3.65f,-4.64f,1.65f,3.85f,0.70f),
                    BeardVoxelModelFactory.c(-0.52f, 4.00f,-4.63f,1.55f,4.65f,0.70f),
                    BeardVoxelModelFactory.c( 1.25f, 3.35f,-4.64f,1.30f,3.45f,0.68f)
            );
            case "split" -> BeardVoxelModelFactory.bake(
                    BeardVoxelModelFactory.c(-3.18f,-2.13f,-4.73f,2.62f,1.18f,0.82f),
                    BeardVoxelModelFactory.c( 0.56f,-2.13f,-4.73f,2.62f,1.18f,0.82f),
                    BeardVoxelModelFactory.c(-3.28f,-0.80f,-4.69f,6.56f,3.95f,0.84f),
                    BeardVoxelModelFactory.c(-2.72f, 2.85f,-4.66f,2.45f,4.10f,0.76f),
                    BeardVoxelModelFactory.c( 0.27f, 2.85f,-4.66f,2.45f,4.10f,0.76f),
                    BeardVoxelModelFactory.c(-2.28f, 6.55f,-4.62f,1.72f,3.25f,0.66f),
                    BeardVoxelModelFactory.c( 0.56f, 6.55f,-4.62f,1.72f,3.25f,0.66f)
            );
            case "double_braid" -> BeardVoxelModelFactory.bake(
                    BeardVoxelModelFactory.c(-3.20f,-2.14f,-4.73f,2.62f,1.20f,0.82f),
                    BeardVoxelModelFactory.c( 0.58f,-2.14f,-4.73f,2.62f,1.20f,0.82f),
                    BeardVoxelModelFactory.c(-3.30f,-0.85f,-4.69f,6.60f,3.85f,0.84f),
                    BeardVoxelModelFactory.c(-2.55f, 2.70f,-4.65f,1.78f,2.10f,0.72f),
                    BeardVoxelModelFactory.c(-2.40f, 4.55f,-4.63f,1.55f,2.00f,0.68f),
                    BeardVoxelModelFactory.c(-2.58f, 6.30f,-4.61f,1.68f,2.05f,0.66f),
                    BeardVoxelModelFactory.c(-2.37f, 8.10f,-4.59f,1.42f,2.90f,0.60f),
                    BeardVoxelModelFactory.c( 0.77f, 2.70f,-4.65f,1.78f,2.10f,0.72f),
                    BeardVoxelModelFactory.c( 0.85f, 4.55f,-4.63f,1.55f,2.00f,0.68f),
                    BeardVoxelModelFactory.c( 0.90f, 6.30f,-4.61f,1.68f,2.05f,0.66f),
                    BeardVoxelModelFactory.c( 0.95f, 8.10f,-4.59f,1.42f,2.90f,0.60f)
            );
            case "triple_braid" -> BeardVoxelModelFactory.bake(
                    BeardVoxelModelFactory.c(-3.18f,-2.14f,-4.73f,2.60f,1.18f,0.82f),
                    BeardVoxelModelFactory.c( 0.58f,-2.14f,-4.73f,2.60f,1.18f,0.82f),
                    BeardVoxelModelFactory.c(-3.18f,-0.80f,-4.69f,6.36f,3.55f,0.82f),
                    BeardVoxelModelFactory.c(-2.62f, 2.45f,-4.65f,1.22f,2.45f,0.69f),
                    BeardVoxelModelFactory.c(-2.48f, 4.60f,-4.62f,1.02f,2.55f,0.64f),
                    BeardVoxelModelFactory.c(-2.58f, 6.85f,-4.60f,1.10f,3.65f,0.58f),
                    BeardVoxelModelFactory.c(-0.66f, 2.55f,-4.66f,1.32f,2.70f,0.71f),
                    BeardVoxelModelFactory.c(-0.56f, 4.95f,-4.63f,1.12f,2.70f,0.65f),
                    BeardVoxelModelFactory.c(-0.52f, 7.35f,-4.60f,1.04f,3.85f,0.58f),
                    BeardVoxelModelFactory.c( 1.40f, 2.45f,-4.65f,1.22f,2.45f,0.69f),
                    BeardVoxelModelFactory.c( 1.46f, 4.60f,-4.62f,1.02f,2.55f,0.64f),
                    BeardVoxelModelFactory.c( 1.48f, 6.85f,-4.60f,1.10f,3.65f,0.58f)
            );
            case "runic" -> BeardVoxelModelFactory.bake(
                    BeardVoxelModelFactory.c(-3.20f,-2.14f,-4.73f,2.62f,1.18f,0.82f),
                    BeardVoxelModelFactory.c( 0.58f,-2.14f,-4.73f,2.62f,1.18f,0.82f),
                    BeardVoxelModelFactory.c(-3.32f,-0.80f,-4.69f,6.64f,3.90f,0.86f),
                    BeardVoxelModelFactory.c(-2.92f, 2.80f,-4.66f,5.84f,2.15f,0.80f),
                    BeardVoxelModelFactory.c(-2.42f, 4.70f,-4.63f,4.84f,2.05f,0.73f),
                    BeardVoxelModelFactory.c(-1.92f, 6.45f,-4.61f,1.25f,2.85f,0.65f),
                    BeardVoxelModelFactory.c( 0.67f, 6.45f,-4.61f,1.25f,2.85f,0.65f),
                    BeardVoxelModelFactory.c(-0.58f, 6.70f,-4.59f,1.16f,3.35f,0.62f)
            );
            case "segmented" -> BeardVoxelModelFactory.bake(
                    BeardVoxelModelFactory.c(-3.20f,-2.14f,-4.73f,2.62f,1.18f,0.82f),
                    BeardVoxelModelFactory.c( 0.58f,-2.14f,-4.73f,2.62f,1.18f,0.82f),
                    BeardVoxelModelFactory.c(-3.25f,-0.80f,-4.69f,6.50f,3.65f,0.84f),
                    BeardVoxelModelFactory.c(-2.90f, 3.05f,-4.66f,5.80f,1.55f,0.78f),
                    BeardVoxelModelFactory.c(-2.48f, 4.92f,-4.63f,4.96f,1.65f,0.72f),
                    BeardVoxelModelFactory.c(-2.02f, 6.92f,-4.60f,4.04f,1.75f,0.66f),
                    BeardVoxelModelFactory.c(-1.45f, 9.00f,-4.57f,2.90f,1.40f,0.58f)
            );
            default -> null;
        };
    }
}
