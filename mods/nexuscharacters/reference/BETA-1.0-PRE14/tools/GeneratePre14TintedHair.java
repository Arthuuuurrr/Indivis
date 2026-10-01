import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Generates 3D hair textures with exactly the same HAIR tint formula used by
 * DynamicAppearanceSupport for the flat/composed player skin.
 */
public final class GeneratePre14TintedHair {
    private static final int[] V69 = {
        0,
        0xFF1B1717, 0xFF563622, 0xFF7A5135, 0xFFD4B36A,
        0xFFE7D59B, 0xFF9B783E, 0xFFA94D2D, 0xFFD96526,
        0xFF7E3428, 0xFF817A75, 0xFFE4DED1, 0xFFC8CCD0
    };

    private static final int[] LEGACY = {
        0,
        0xFF1B1717, 0xFF563622, 0xFF7A5135, 0xFFD4B36A,
        0xFFA94D2D, 0xFFE4DED1, 0xFF817A75
    };

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("usage: GeneratePre14TintedHair <mesh-dir> <output-root>");
        }
        File meshDir = new File(args[0]);
        File root = new File(args[1]);

        generateFamily(meshDir, new File(root, "v69"), V69);
        generateFamily(meshDir, new File(root, "legacy"), LEGACY);
    }

    private static void generateFamily(File meshDir, File familyDir, int[] palette) throws Exception {
        for (int color = 1; color < palette.length; color++) {
            File outDir = new File(familyDir, String.format("hc%02d", color));
            outDir.mkdirs();
            for (int hair = 1; hair <= 13; hair++) {
                BufferedImage src = ImageIO.read(new File(meshDir, String.format("hair_%02d.png", hair)));
                if (src == null || src.getWidth() != 64 || src.getHeight() != 64) {
                    throw new IllegalStateException("Missing/bad mesh texture hair " + hair);
                }
                BufferedImage out = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < 64; y++) {
                    for (int x = 0; x < 64; x++) {
                        out.setRGB(x, y, tint(src.getRGB(x, y), palette[color]));
                    }
                }
                ImageIO.write(out, "PNG", new File(outDir, String.format("hair_%02d.png", hair)));
            }
        }
    }

    private static int tint(int argb, int target) {
        int a = (argb >>> 24) & 255;
        if (a == 0) return 0;

        int r = (argb >>> 16) & 255;
        int g = (argb >>> 8) & 255;
        int b = argb & 255;
        int lum = (r * 30 + g * 59 + b * 11) / 100;

        int tr = (target >>> 16) & 255;
        int tg = (target >>> 8) & 255;
        int tb = target & 255;

        double factor = 0.38 + ((double) lum / 255.0) * 0.92;
        return (a << 24)
                | (clamp((int)Math.round(tr * factor)) << 16)
                | (clamp((int)Math.round(tg * factor)) << 8)
                | clamp((int)Math.round(tb * factor));
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}
