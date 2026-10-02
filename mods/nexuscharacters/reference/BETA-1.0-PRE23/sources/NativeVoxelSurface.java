package net.tompsen.nexuscharacters;

import dev.tr7zw.skinlayers.api.*;
import java.lang.reflect.*;
import java.util.*;
import net.minecraft.*;
import org.joml.*;

/** Native Skin Layers geometry and its solid voxel bounds, in part coordinates. */
public final class NativeVoxelSurface {
  public record Bounds(float[] lo, float[] hi, int rank) {}

  public record Shape(List<SurfaceGeometry.Polygon> faces, List<Bounds> boxes) {}

  static final String[] OFFSETS = {
    "HEAD",
    "BODY",
    "LEFT_LEG",
    "RIGHT_LEG",
    "LEFT_ARM",
    "RIGHT_ARM",
    "LEFT_ARM_SLIM",
    "RIGHT_ARM_SLIM"
  };
  static final int[][] SPECS = {
    {8, 8, 8, 32, 0, 0},
    {8, 12, 4, 16, 32, 1},
    {4, 12, 4, 0, 48, 1},
    {4, 12, 4, 0, 32, 1},
    {4, 12, 4, 48, 48, 1},
    {4, 12, 4, 40, 32, 1},
    {3, 12, 4, 48, 48, 1},
    {3, 12, 4, 40, 32, 1}
  };
  static final Map<String, float[][]> FULL = new HashMap<>();
  public static final OffsetProvider IDENTITY = (stack, mesh) -> mesh.setPosition(0, 0, 0);

  public static Shape build(
      class_1011 image, int part, int owner, boolean promoted, boolean shallow) throws Exception {
    Object config =
        Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase").getField("config").get(null);
    boolean fast = config.getClass().getField("fastRender").getBoolean(config),
        compat = config.getClass().getField("irisCompatibilityMode").getBoolean(config);
    config.getClass().getField("fastRender").setBoolean(config, false);
    config.getClass().getField("irisCompatibilityMode").setBoolean(config, false);
    try {
      return create(image, part, owner, promoted, shallow);
    } finally {
      config.getClass().getField("fastRender").setBoolean(config, fast);
      config.getClass().getField("irisCompatibilityMode").setBoolean(config, compat);
    }
  }

  static Shape create(class_1011 image, int part, int owner, boolean promoted, boolean shallow)
      throws Exception {
    int[] spec = SPECS[part];
    List<float[]> rawBoxes = new ArrayList<>();
    Class<?> builderType =
        Class.forName("dev.tr7zw.skinlayers.versionless.util.wrapper.ModelBuilder");
    Object builder =
        Class.forName("dev.tr7zw.skinlayers.render.CustomizableCubeListBuilder")
            .getConstructor()
            .newInstance();
    Object proxy =
        Proxy.newProxyInstance(
            builderType.getClassLoader(),
            new Class<?>[] {builderType},
            (p, m, a) -> {
              if (m.getName().equals("addBox")) {
                float x = ((Number) a[0]).floatValue(),
                    y = ((Number) a[1]).floatValue(),
                    z = ((Number) a[2]).floatValue(),
                    s = ((Number) a[3]).floatValue();
                rawBoxes.add(new float[] {x, y, z, x + s, y + s, z + s});
              }
              Object r = m.invoke(builder, a);
              return builderType.isInstance(r) ? p : r;
            });
    Object wrapped =
        Class.forName("dev.tr7zw.skinlayers.util.NMSWrapper$WrappedNativeImage")
            .getConstructor(class_1011.class)
            .newInstance(image);
    for (Method m :
        Class.forName("dev.tr7zw.skinlayers.versionless.util.wrapper.SolidPixelWrapper")
            .getMethods())
      if (m.getName().equals("wrapBox"))
        m.invoke(
            null,
            proxy,
            wrapped,
            spec[0],
            spec[1],
            spec[2],
            spec[3],
            spec[4],
            spec[5] != 0,
            part == 0 ? .6f : part >= 4 ? -2f : 0f);
    Object original =
        Class.forName("dev.tr7zw.skinlayers.render.CustomizableModelPart")
            .getConstructor(List.class, List.class, Map.class)
            .newInstance(
                SurfaceGeometry.call(builder, "getVanillaCubes"),
                SurfaceGeometry.call(builder, "getCubes"),
                Map.of());
    class_4587 stack = new class_4587();
    ((OffsetProvider) OffsetProvider.class.getField(OFFSETS[part]).get(null))
        .applyOffset(stack, (Mesh) original);
    SurfaceGeometry.call(original, "translateAndRotate", stack);
    Matrix4f transform = new Matrix4f(stack.method_23760().method_23761());
    int rank = owner * 10000 + (promoted ? 0 : 5000);
    List<SurfaceGeometry.Polygon> faces = new ArrayList<>();
    float[] data = (float[]) SurfaceGeometry.field(original, "polygonData");
    int offset = 0, ordinal = 0;
    for (Object cube : (List<?>) SurfaceGeometry.call(builder, "getCubes")) {
      int length = ((Number) SurfaceGeometry.field(cube, "polygonCount")).intValue() * 23;
      faces.addAll(
          SurfaceGeometry.transform(
              SurfaceGeometry.read(
                  Arrays.copyOfRange(data, offset, offset + length), rank + ordinal++),
              transform));
      offset += length;
    }
    List<Bounds> boxes = new ArrayList<>();
    ordinal = 0;
    for (float[] b : rawBoxes) {
      Vector3f lo = transform.transformPosition(new Vector3f(b[0] / 16, b[1] / 16, b[2] / 16)),
          hi = transform.transformPosition(new Vector3f(b[3] / 16, b[4] / 16, b[5] / 16));
      boxes.add(
          new Bounds(
              new float[] {lo.x, lo.y, lo.z}, new float[] {hi.x, hi.y, hi.z}, rank + ordinal++));
    }
    if (promoted && shallow && !boxes.isEmpty()) {
      float[][] full = fullBounds(part);
      float[][] base = baseBounds(part);
      float depth = (owner == 1 ? .035f : .1f) / 16;
      Matrix4f shrink = new Matrix4f();
      Vector3f scale = new Vector3f(), shift = new Vector3f();
      for (int a = 0; a < 3; a++) {
        float factor = (base[1][a] - base[0][a] + 2 * depth) / (full[1][a] - full[0][a]);
        scale.setComponent(a, factor);
        shift.setComponent(a, base[0][a] - depth - full[0][a] * factor);
      }
      shrink.translation(shift).scale(scale);
      faces = SurfaceGeometry.transform(faces, shrink);
      List<Bounds> resized = new ArrayList<>();
      for (Bounds b : boxes) {
        Vector3f lo = shrink.transformPosition(new Vector3f(b.lo)),
            hi = shrink.transformPosition(new Vector3f(b.hi));
        resized.add(
            new Bounds(
                new float[] {lo.x, lo.y, lo.z}, new float[] {hi.x, hi.y, hi.z}, rank + ordinal++));
      }
      boxes = resized;
    }
    return new Shape(List.copyOf(faces), List.copyOf(boxes));
  }

  static float[][] fullBounds(int part) throws Exception {
    Object c = Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase").getField("config").get(null);
    String key =
        part
            + ":"
            + SurfaceGeometry.field(c, "baseVoxelSize")
            + ":"
            + SurfaceGeometry.field(c, "headVoxelSize")
            + ":"
            + SurfaceGeometry.field(c, "bodyVoxelWidthSize");
    float[][] cached = FULL.get(key);
    if (cached != null) return cached;
    class_1011 image = new class_1011(64, 64, false);
    for (int y = 0; y < 64; y++) for (int x = 0; x < 64; x++) image.method_4305(x, y, 0xffffffff);
    Shape s = create(image, part, 0, false, false);
    image.close();
    float[] lo = {Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY},
        hi = {-lo[0], -lo[1], -lo[2]};
    for (Bounds b : s.boxes)
      for (int a = 0; a < 3; a++) {
        lo[a] = java.lang.Math.min(lo[a], b.lo[a]);
        hi[a] = java.lang.Math.max(hi[a], b.hi[a]);
      }
    float[][] result = {lo, hi};
    FULL.put(key, result);
    return result;
  }

  public static float[][] baseBounds(int part) {
    int p = part >= 6 ? part - 2 : part;
    float[] lo, hi;
    if (p == 0) {
      lo = new float[] {-4, -8, -4};
      hi = new float[] {4, 0, 4};
    } else if (p == 1) {
      lo = new float[] {-4, 0, -2};
      hi = new float[] {4, 12, 2};
    } else if (p == 2 || p == 3) {
      lo = new float[] {-2, 0, -2};
      hi = new float[] {2, 12, 2};
    } else if (p == 4) {
      lo = new float[] {-1, -2, -2};
      hi = new float[] {part == 6 ? 2 : 3, 10, 2};
    } else {
      lo = new float[] {part == 7 ? -2 : -3, -2, -2};
      hi = new float[] {1, 10, 2};
    }
    for (int a = 0; a < 3; a++) {
      lo[a] /= 16;
      hi[a] /= 16;
    }
    return new float[][] {lo, hi};
  }

  public static Object emptyMesh() throws Exception {
    return Class.forName("dev.tr7zw.skinlayers.render.CustomizableModelPart")
        .getConstructor(List.class, List.class, Map.class)
        .newInstance(List.of(), List.of(), Map.of());
  }

  public static Object mesh(Shape shape) throws Exception {
    Object m = emptyMesh();
    SurfaceGeometry.set(m, "polygonData", SurfaceGeometry.write(shape.faces));
    SHAPES.put(m, shape);
    return m;
  }

  public static final Map<Object, Shape> SHAPES = Collections.synchronizedMap(new WeakHashMap<>());

  public static Shape combine(List<Object> meshes) {
    List<SurfaceGeometry.Polygon> faces = new ArrayList<>();
    List<Bounds> boxes = new ArrayList<>();
    for (Object m : meshes) {
      Shape s = SHAPES.get(m);
      if (s != null) {
        faces.addAll(s.faces);
        boxes.addAll(s.boxes);
      }
    }
    return new Shape(List.copyOf(faces), List.copyOf(boxes));
  }

  public static void clear() {
    FULL.clear();
    SHAPES.clear();
  }
}
