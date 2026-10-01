import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * PRE12 asset correction.
 *
 * PRE2 copied long-hair torso pixels from the base torso UVs to the jacket UVs
 * but left the original base pixels in place. That means every long hairstyle
 * existed twice: once flush with the body and once on the outer layer.
 *
 * PRE12 removes only the duplicate base-torso hair pixels. The existing outer
 * copy is preserved byte-for-pixel, so vanilla fallback still shows the full
 * hairstyle while 3D Skin Layers can voxelise it from the jacket layer.
 */
public final class SeparateLongHairFromBody {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("usage: SeparateLongHairFromBody <hair-dir>");
        }

        File dir = new File(args[0]);
        for (int i = 1; i <= 5; i++) {
            File file = new File(dir, String.format("hair_long_%02d.png", i));
            BufferedImage image = ImageIO.read(file);
            if (image == null || image.getWidth() != 64 || image.getHeight() != 64) {
                throw new IllegalStateException("Expected 64x64 PNG: " + file);
            }

            int baseBefore = countOpaque(image, 16, 16, 40, 32);
            int outerBefore = countOpaque(image, 16, 32, 40, 48);
            if (outerBefore <= 0) {
                throw new IllegalStateException(
                        "Expected outer torso hair before PRE12 for " + file
                                + " (base=" + baseBefore + ", outer=" + outerBefore + ")");
            }

            for (int y = 16; y < 32; y++) {
                for (int x = 16; x < 40; x++) {
                    int argb = image.getRGB(x, y);
                    if (((argb >>> 24) & 0xFF) != 0) {
                        image.setRGB(x, y, 0x00000000);
                    }
                }
            }

            int baseAfter = countOpaque(image, 16, 16, 40, 32);
            int outerAfter = countOpaque(image, 16, 32, 40, 48);
            if (baseAfter != 0) {
                throw new IllegalStateException("Base torso hair remained in " + file + ": " + baseAfter);
            }
            if (outerAfter != outerBefore) {
                throw new IllegalStateException(
                        "Outer torso hair changed in " + file + ": before="
                                + outerBefore + ", after=" + outerAfter);
            }

            ImageIO.write(image, "PNG", file);
            System.out.println(file.getName() + ": removed " + baseBefore
                    + " duplicate base pixels; kept " + outerAfter + " outer pixels");
        }
    }

    private static int countOpaque(BufferedImage image, int x0, int y0, int x1, int y1) {
        int count = 0;
        for (int y = y0; y < y1; y++) {
            for (int x = x0; x < x1; x++) {
                if (((image.getRGB(x, y) >>> 24) & 0xFF) != 0) count++;
            }
        }
        return count;
    }
}
