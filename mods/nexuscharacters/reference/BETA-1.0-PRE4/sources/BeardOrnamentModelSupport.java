package net.tompsen.nexuscharacters;

import java.util.ArrayList;
import java.util.List;

/** Small metallic pieces positioned on the beard's head-local coordinate system. */
public final class BeardOrnamentModelSupport {
    private BeardOrnamentModelSupport() {}

    public static Object create(String style, String ornament) {
        style = CulturalBeardSupport.baseStyle(style);
        ornament = CulturalBeardSupport.canonicalOrnament(ornament);
        if ("none".equals(ornament) || "none".equals(style)) return null;
        return BeardVoxelModelFactory.bake(specs(style, ornament));
    }

    private static List<BeardVoxelModelFactory.Cuboid> specs(String style, String ornament) {
        ArrayList<BeardVoxelModelFactory.Cuboid> q = new ArrayList<>();
        if (ornament.endsWith("_ring")) {
            addRingGeometry(q, style);
        } else if ("runic_bead".equals(ornament)) {
            float[] a = centerAnchor(style);
            q.add(BeardVoxelModelFactory.jewel(a[0]-0.70f, a[1]-0.55f, -4.92f, 1.40f, 1.25f, 0.42f));
            q.add(BeardVoxelModelFactory.jewel(a[0]-0.20f, a[1]-0.20f, -4.97f, 0.40f, 0.55f, 0.16f));
        } else if ("engraved_clasp".equals(ornament)) {
            float[] a = centerAnchor(style);
            q.add(BeardVoxelModelFactory.jewel(a[0]-1.12f, a[1]-0.38f, -4.93f, 2.24f, 0.82f, 0.44f));
            q.add(BeardVoxelModelFactory.jewel(a[0]-0.10f, a[1]-0.28f, -4.98f, 0.20f, 0.62f, 0.16f));
        } else if ("braid_tip".equals(ornament)) {
            addTipGeometry(q, style);
        }
        return q;
    }

    private static void addRingGeometry(List<BeardVoxelModelFactory.Cuboid> q, String style) {
        switch (style) {
            case "double_braid" -> {
                q.add(BeardVoxelModelFactory.jewel(-2.33f, 7.15f, -4.90f, 1.40f, 0.58f, 0.46f));
                q.add(BeardVoxelModelFactory.jewel( 0.93f, 7.15f, -4.90f, 1.40f, 0.58f, 0.46f));
            }
            case "triple_braid" -> {
                q.add(BeardVoxelModelFactory.jewel(-2.55f, 7.25f, -4.90f, 1.15f, 0.54f, 0.44f));
                q.add(BeardVoxelModelFactory.jewel(-0.55f, 7.55f, -4.91f, 1.10f, 0.54f, 0.44f));
                q.add(BeardVoxelModelFactory.jewel( 1.40f, 7.25f, -4.90f, 1.15f, 0.54f, 0.44f));
            }
            case "forked", "split" -> {
                q.add(BeardVoxelModelFactory.jewel(-2.35f, 6.85f, -4.90f, 1.35f, 0.56f, 0.45f));
                q.add(BeardVoxelModelFactory.jewel( 1.00f, 6.85f, -4.90f, 1.35f, 0.56f, 0.45f));
            }
            case "braided" -> q.add(BeardVoxelModelFactory.jewel(-0.82f, 7.15f, -4.91f, 1.64f, 0.58f, 0.46f));
            case "noble_goatee" -> q.add(BeardVoxelModelFactory.jewel(-0.73f, 4.65f, -4.91f, 1.46f, 0.50f, 0.42f));
            case "trimmed" -> q.add(BeardVoxelModelFactory.jewel(-1.10f, 3.40f, -4.91f, 2.20f, 0.50f, 0.42f));
            default -> {
                float[] a = centerAnchor(style);
                q.add(BeardVoxelModelFactory.jewel(a[0]-0.95f, a[1]-0.26f, -4.91f, 1.90f, 0.54f, 0.44f));
            }
        }
    }

    private static void addTipGeometry(List<BeardVoxelModelFactory.Cuboid> q, String style) {
        switch (style) {
            case "double_braid" -> {
                q.add(BeardVoxelModelFactory.jewel(-2.12f, 10.35f, -4.88f, 0.98f, 0.78f, 0.46f));
                q.add(BeardVoxelModelFactory.jewel( 1.14f, 10.35f, -4.88f, 0.98f, 0.78f, 0.46f));
            }
            case "triple_braid" -> {
                q.add(BeardVoxelModelFactory.jewel(-2.37f, 10.10f, -4.88f, 0.82f, 0.72f, 0.44f));
                q.add(BeardVoxelModelFactory.jewel(-0.40f, 10.82f, -4.89f, 0.80f, 0.72f, 0.44f));
                q.add(BeardVoxelModelFactory.jewel( 1.55f, 10.10f, -4.88f, 0.82f, 0.72f, 0.44f));
            }
            case "forked", "split" -> {
                q.add(BeardVoxelModelFactory.jewel(-2.05f, 9.10f, -4.87f, 0.95f, 0.78f, 0.44f));
                q.add(BeardVoxelModelFactory.jewel( 1.10f, 9.10f, -4.87f, 0.95f, 0.78f, 0.44f));
            }
            case "braided" -> q.add(BeardVoxelModelFactory.jewel(-0.55f, 9.60f, -4.88f, 1.10f, 0.82f, 0.44f));
            default -> {
                float[] a = centerAnchor(style);
                q.add(BeardVoxelModelFactory.jewel(a[0]-0.52f, a[1]+2.10f, -4.87f, 1.04f, 0.76f, 0.44f));
            }
        }
    }

    private static float[] centerAnchor(String style) {
        return switch (style) {
            case "short", "trimmed" -> new float[]{0f, 3.45f};
            case "noble_goatee" -> new float[]{0f, 4.80f};
            case "long", "wild" -> new float[]{0f, 5.75f};
            case "full", "runic", "segmented" -> new float[]{0f, 6.20f};
            case "forked", "split", "braided", "double_braid", "triple_braid" -> new float[]{0f, 6.75f};
            default -> new float[]{0f, 5.80f};
        };
    }
}
