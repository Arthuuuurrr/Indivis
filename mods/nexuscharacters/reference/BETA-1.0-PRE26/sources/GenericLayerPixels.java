package net.tompsen.nexuscharacters;

/** Pure pixel operations, shared by the compositor and the regression tests. */
public final class GenericLayerPixels {
  public static final int SIZE = 64;

  private GenericLayerPixels() {}

  // The six UV footprints; unused corners are excluded by the mesh generator.
  private static final int[][] BASE = {
    {0, 0, 32, 16},
    {16, 16, 40, 32},
    {0, 16, 16, 32},
    {16, 48, 32, 64},
    {40, 16, 56, 32},
    {32, 48, 48, 64}
  };
  private static final int[][] OUTER = {
    {32, 0, 64, 16},
    {16, 32, 40, 48},
    {0, 32, 16, 48},
    {0, 48, 16, 64},
    {40, 32, 56, 48},
    {48, 48, 64, 64}
  };

  public static boolean isOuter(int x, int y) {
    for (int[] r : OUTER) if (x >= r[0] && x < r[2] && y >= r[1] && y < r[3]) return true;
    return false;
  }

  public static boolean isBase(int x, int y) {
    for (int[] r : BASE) if (x >= r[0] && x < r[2] && y >= r[1] && y < r[3]) return true;
    return false;
  }

  public static int[] prepareCosmetic(int[] source, boolean hair, boolean beard) {
    int[] result = source.clone();
    for (int p = 0; p < BASE.length; p++) {
      int[] b = BASE[p], o = OUTER[p];
      // Preserve authored upper pixels, while giving each visible lower
      // cosmetic pixel its own exterior footprint. One upper detail must
      // not prevent every other lower detail on this part from extruding.
      for (int y = 0; y < b[3] - b[1]; y++)
        for (int x = 0; x < b[2] - b[0]; x++) {
          int oi = (o[1] + y) * SIZE + o[0] + x, bi = (b[1] + y) * SIZE + b[0] + x;
          if (alpha(result[oi]) == 0 && alpha(source[bi]) > 0) result[oi] = source[bi];
        }
    }
    return result;
  }

  /** Preserve original outer pixels; promote base pixels only where outer is absent. */
  public static int[] exterior(int[] source, boolean promoteScalp) {
    int[] result = new int[SIZE * SIZE];
    for (int p = 0; p < BASE.length; p++) {
      int[] b = BASE[p], o = OUTER[p];
      for (int y = 0; y < b[3] - b[1]; y++)
        for (int x = 0; x < b[2] - b[0]; x++) {
          int oi = (o[1] + y) * SIZE + o[0] + x;
          int outer = source[oi];
          int base = source[(b[1] + y) * SIZE + b[0] + x];
          result[oi] = alpha(outer) > 0 ? outer : (p != 0 || promoteScalp ? base : 0);
        }
    }
    return result;
  }

  /** Main layer keeps the scalp paint; torso/limb hair and facial hair live outside. */
  public static int[] cosmeticForSkin(int[] source, int[] exterior, boolean hair) {
    int[] result = exterior.clone();
    if (hair)
      for (int y = 0; y < 16; y++)
        for (int x = 0; x < 32; x++) result[y * SIZE + x] = source[y * SIZE + x];
    return result;
  }

  public static int tintHair(int pixel, int color) {
    if (color == 0) return pixel;
    int r = (pixel >>> 16) & 255, g = (pixel >>> 8) & 255, b = pixel & 255;
    int gray = (r * 30 + g * 59 + b * 11) / 100;
    double scale = 0.38 + gray / 255.0 * 0.92;
    return (pixel & 0xff000000)
        | clamp((int) Math.round(((color >>> 16) & 255) * scale)) << 16
        | clamp((int) Math.round(((color >>> 8) & 255) * scale)) << 8
        | clamp((int) Math.round((color & 255) * scale));
  }

  public static int blend(int dst, int src) {
    int sa = alpha(src);
    if (sa == 255) return src;
    if (sa == 0) return dst;
    int da = alpha(dst), oa = sa + (da * (255 - sa) + 127) / 255;
    if (oa == 0) return 0;
    int d = da * (255 - sa), result = oa << 24;
    for (int shift : new int[] {16, 8, 0}) {
      int c =
          ((((src >>> shift) & 255) * sa * 255 + ((dst >>> shift) & 255) * d + oa * 127)
              / (oa * 255));
      result |= clamp(c) << shift;
    }
    return result;
  }

  public static int alpha(int p) {
    return p >>> 24;
  }

  public static int count(int[] pixels) {
    int n = 0;
    for (int p : pixels) if (alpha(p) > 0) n++;
    return n;
  }

  private static int clamp(int n) {
    return Math.max(0, Math.min(255, n));
  }
}

