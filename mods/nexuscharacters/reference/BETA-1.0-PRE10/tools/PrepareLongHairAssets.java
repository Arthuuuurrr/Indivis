import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Generates PRE10 derived long-hair textures from PRE9 originals.
 *
 * Originals are never edited:
 * - head3d keeps every pixel except torso base + torso outer UVs;
 * - body mesh texture keeps only the torso outer UV region expected by
 *   skinlayers3d MeshHelper.create3DMesh(... 8,12,4,16,32 ...).
 */
public final class PrepareLongHairAssets {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException("usage: PrepareLongHairAssets <source-dir> <head-dir> <body-dir>");
        }

        File sourceDir = new File(args[0]);
        File headDir = new File(args[1]);
        File bodyDir = new File(args[2]);
        headDir.mkdirs();
        bodyDir.mkdirs();

        for (int i = 1; i <= 5; i++) {
            File src = new File(sourceDir, String.format("hair_long_%02d.png", i));
            BufferedImage original = ImageIO.read(src);
            if (original == null || original.getWidth() != 64 || original.getHeight() != 64) {
                throw new IllegalStateException("Expected 64x64 PNG: " + src);
            }

            BufferedImage head = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            BufferedImage body = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < 64; y++) {
                for (int x = 0; x < 64; x++) {
                    int argb = original.getRGB(x, y);
                    boolean torsoBase = x >= 16 && x < 40 && y >= 16 && y < 32;
                    boolean torsoOuter = x >= 16 && x < 40 && y >= 32 && y < 48;

                    if (!torsoBase && !torsoOuter) {
                        head.setRGB(x, y, argb);
                    }
                    if (torsoOuter) {
                        body.setRGB(x, y, argb);
                    }
                }
            }

            File headOut = new File(headDir, String.format("hair_long_%02d_head3d.png", i));
            File bodyOut = new File(bodyDir, String.format("hair_long_body_%02d.png", i));
            ImageIO.write(head, "PNG", headOut);
            ImageIO.write(body, "PNG", bodyOut);

            int bodyPixels = countOpaque(body);
            if (bodyPixels <= 0) {
                throw new IllegalStateException("Derived body mesh texture is empty for long hair " + i);
            }
            if (countOpaqueInRect(head, 16, 16, 40, 48) != 0) {
                throw new IllegalStateException("Head-only texture still contains torso pixels for long hair " + i);
            }
        }
    }

    private static int countOpaque(BufferedImage image) {
        return countOpaqueInRect(image, 0, 0, image.getWidth(), image.getHeight());
    }

    private static int countOpaqueInRect(BufferedImage image, int x0, int y0, int x1, int y1) {
        int count = 0;
        for (int y = y0; y < y1; y++) {
            for (int x = x0; x < x1; x++) {
                if (((image.getRGB(x, y) >>> 24) & 0xFF) != 0) count++;
            }
        }
        return count;
    }
}
