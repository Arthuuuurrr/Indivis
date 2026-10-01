import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class PreparePre13HairAssets {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException("usage: PreparePre13HairAssets <source-dir> <composite-dir> <mesh-dir>");
        }
        File srcDir = new File(args[0]);
        File compositeDir = new File(args[1]);
        File meshDir = new File(args[2]);
        compositeDir.mkdirs();
        meshDir.mkdirs();

        for (int hairIndex = 1; hairIndex <= 13; hairIndex++) {
            boolean shortHair = hairIndex <= 5;
            int local = shortHair ? hairIndex : hairIndex - 5;
            String inputName = String.format(shortHair ? "hair_short_%02d.png" : "hair_long_%02d.png", local);
            BufferedImage input = ImageIO.read(new File(srcDir, inputName));
            if (input == null || input.getWidth() != 64 || input.getHeight() != 64) {
                throw new IllegalStateException("Expected 64x64 PNG: " + inputName);
            }

            BufferedImage mesh = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);

            // Promote base-head hair to the equivalent outer-head UVs.
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 32; x++) {
                    int px = input.getRGB(x, y);
                    if (((px >>> 24) & 255) != 0) mesh.setRGB(x + 32, y, px);
                }
            }
            // Preserve authored outer-head detail over the promoted copy.
            for (int y = 0; y < 16; y++) {
                for (int x = 32; x < 64; x++) {
                    int px = input.getRGB(x, y);
                    if (((px >>> 24) & 255) != 0) mesh.setRGB(x, y, px);
                }
            }

            // Long styles 01..05 currently contain the torso hair.
            if (!shortHair && local <= 5) {
                for (int y = 32; y < 48; y++) {
                    for (int x = 16; x < 40; x++) {
                        int px = input.getRGB(x, y);
                        if (((px >>> 24) & 255) != 0) mesh.setRGB(x, y, px);
                    }
                }
            }

            // Composite fallback with only base-head hair left in the
            // ordinary skin. Outer hair is exclusively the PRE13 mesh.
            BufferedImage composite = copy(input);
            clear(composite, 32, 0, 64, 16);
            if (!shortHair && local <= 5) clear(composite, 16, 32, 40, 48);

            String compositeName = String.format(
                    shortHair ? "hair_short_%02d_pre13.png" : "hair_long_%02d_pre13.png",
                    local);
            ImageIO.write(composite, "PNG", new File(compositeDir, compositeName));
            ImageIO.write(mesh, "PNG", new File(meshDir, String.format("hair_%02d.png", hairIndex)));
        }
    }

    private static void clear(BufferedImage img, int x0, int y0, int x1, int y1) {
        for (int y = y0; y < y1; y++) {
            for (int x = x0; x < x1; x++) img.setRGB(x, y, 0);
        }
    }

    private static BufferedImage copy(BufferedImage src) {
        BufferedImage out = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) out.setRGB(x, y, src.getRGB(x, y));
        }
        return out;
    }
}
