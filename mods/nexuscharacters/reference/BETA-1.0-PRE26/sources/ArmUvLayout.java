package net.tompsen.nexuscharacters;

/** Converts slim arm faces to Nexus' existing classic-arm layout, before composition. */
public final class ArmUvLayout {
  private ArmUvLayout() {}

  public static boolean isSlimBody(int[] pixels) {
    if (pixels.length != 4096) throw new IllegalArgumentException("Expected 64x64 pixels");
    // Unused columns on both base arms distinguish the 3-pixel layout.
    for (int y = 20; y < 32; y++)
      for (int x = 54; x < 56; x++)
        if ((pixels[y * 64 + x] >>> 24) != 0) return false;
    for (int y = 52; y < 64; y++)
      for (int x = 46; x < 48; x++)
        if ((pixels[y * 64 + x] >>> 24) != 0) return false;
    // Do not mistake an empty or sparse cosmetic texture for a body.
    int filled = 0;
    for (int y = 20; y < 32; y++)
      for (int x = 40; x < 54; x++) if ((pixels[y * 64 + x] >>> 24) != 0) filled++;
    for (int y = 52; y < 64; y++)
      for (int x = 32; x < 46; x++) if ((pixels[y * 64 + x] >>> 24) != 0) filled++;
    return filled >= 302; // At least 90% of both base-arm side strips.
  }

  public static int[] classicBody(int[] source) {
    if (!isSlimBody(source)) return source;
    return classicTexture(source, 3);
  }

  public static int inferredWidth(String path, int[] pixels) {
    // These shipped sources are explicitly authored on Alex's arm UVs.
    // Sparse straps cannot be identified reliably from transparency alone.
    if (path.contains("/outfits/")) {
      String name=path.substring(path.lastIndexOf('/')+1);
      for (int id : new int[] {3,5,6,7,10,14,15,16,17,18,19,22,23,26,32,38})
        if (name.equals(String.format(java.util.Locale.ROOT,"outfit_%02d.png",id))) return 3;
    }
    if (isSlimBody(pixels)) return 3;
    // A new sleeve with full rows and two empty padding columns is unambiguously slim.
    boolean row=false;
    for (int[] uv : new int[][] {{40,16},{32,48}}) {
      for (int y=uv[1]+4;y<uv[1]+16;y++) {
        for (int x=uv[0]+14;x<uv[0]+16;x++)
          if ((pixels[y*64+x]>>>24)!=0) return 4;
        int count=0;
        for (int x=uv[0];x<uv[0]+14;x++) if ((pixels[y*64+x]>>>24)!=0) count++;
        row |= count==14;
      }
    }
    return row?3:4;
  }

  public static int[] classicTexture(int[] source, int width) {
    if (source.length != 4096) throw new IllegalArgumentException("Expected 64x64 pixels");
    if (width == 4) return source;
    if (width != 3) throw new IllegalArgumentException("Arm width must be 3 or 4");
    int[] target = source.clone();
    for (int[] uv : new int[][] {{40,16}, {32,48}, {40,32}, {48,48}}) {
      int u = uv[0], v = uv[1];
      // Read every face from the original, never an already shifted destination.
      face(source, target, u+4,v,3,4, u+4,v,4);
      face(source, target, u+7,v,3,4, u+8,v,4);
      face(source, target, u,v+4,4,12, u,v+4,4);
      face(source, target, u+4,v+4,3,12, u+4,v+4,4);
      face(source, target, u+7,v+4,4,12, u+8,v+4,4);
      face(source, target, u+11,v+4,3,12, u+12,v+4,4);
    }
    return target;
  }

  private static void face(int[] source,int[] target,int sx,int sy,int width,int height,
      int dx,int dy,int targetWidth) {
    for (int y=0;y<height;y++)
      for (int x=0;x<targetWidth;x++)
        target[(dy+y)*64+dx+x] = source[(sy+y)*64+sx+((2*x+1)*width)/(2*targetWidth)];
  }
}
