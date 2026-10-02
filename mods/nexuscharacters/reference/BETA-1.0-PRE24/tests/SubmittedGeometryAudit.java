import dev.tr7zw.skinlayers.accessor.*;
import dev.tr7zw.skinlayers.api.*;
import java.lang.reflect.*;
import java.util.*;
import net.minecraft.*;
import org.joml.Vector3f;

/** Tests partial coplanar overlaps across parts, including the lower vanilla cubes. */
public final class SubmittedGeometryAudit {
  static final String[] lower = {
    "field_3398", "field_3391", "field_3397", "field_3392", "field_27433", "field_3401"
  };

  record Face(String part, int axis, float plane, float[] lo, float[] hi) {}

  static Object field(Object o, String n) throws Exception {
    Class<?> type = o instanceof Class<?> k ? k : o.getClass();
    for (Class<?> c = type; c != null; c = c.getSuperclass())
      try {
        Field f = c.getDeclaredField(n);
        f.setAccessible(true);
        return f.get(o instanceof Class<?> ? null : o);
      } catch (NoSuchFieldException ignored) {
      }
    throw new NoSuchFieldException(n);
  }

  static void verifyUvs(Object model) throws Exception {
    Map<?, ?> originals =
        (Map<?, ?>) field(net.tompsen.nexuscharacters.LowerJointSupport.class, "ORIGINALS");
    int checked = 0;
    for (String name : lower) {
      Object part = field(model, name), original = originals.get(part);
      List<?> source = (List<?>) field(original, "cubes"),
          target = (List<?>) field(part, "field_3663");
      for (int i = 0; i < source.size(); i++) {
        Object[] a = (Object[]) field(source.get(i), "field_3649"),
            b = (Object[]) field(target.get(i), "field_3649");
        if (a.length != b.length) throw new AssertionError("Original base faces changed");
        for (int p = 0; p < a.length; p++) {
          Object[] av = (Object[]) a[p].getClass().getMethod("comp_3184").invoke(a[p]),
              bv = (Object[]) b[p].getClass().getMethod("comp_3184").invoke(b[p]);
          for (int v = 0; v < av.length; v++)
            for (String uv : new String[] {"comp_3187", "comp_3188"})
              if (!av[v]
                  .getClass()
                  .getMethod(uv)
                  .invoke(av[v])
                  .equals(bv[v].getClass().getMethod(uv).invoke(bv[v])))
                throw new AssertionError("Original UV modified " + name);
          checked++;
        }
        try {
          if (!Arrays.equals(
              (long[]) field(field(source.get(i), "sodium$cuboid"), "textures"),
              (long[]) field(field(target.get(i), "sodium$cuboid"), "textures")))
            throw new AssertionError("Sodium UV modified");
        } catch (NoSuchFieldException absent) {
        }
      }
    }
    net.tompsen.nexuscharacters.LowerJointSupport.apply(model, false);
    for (String name : lower) {
      Object part = field(model, name);
      if (field(part, "field_3663") != field(originals.get(part), "cubes"))
        throw new AssertionError("Original model was not restored");
    }
    net.tompsen.nexuscharacters.LowerJointSupport.apply(model, true);
    System.out.println("LOWER_UV_AND_RESTORE_PASS faces=" + checked);
  }

  static int run(Object model) throws Exception {
    List<Face> faces = new ArrayList<>();
    for (String name : lower) {
      class_630 part = (class_630) field(model, name);
      class_4587 stack = new class_4587();
      float x = part.field_3654, y = part.field_3675, z = part.field_3674;
      part.field_3654 = part.field_3675 = part.field_3674 = 0;
      part.method_22703(stack);
      part.field_3654 = x;
      part.field_3675 = y;
      part.field_3674 = z;
      for (Object cube : (List<?>) field(part, "field_3663"))
        for (Object poly : (Object[]) field(cube, "field_3649")) {
          Object[] vs = (Object[]) poly.getClass().getMethod("comp_3184").invoke(poly);
          float[] lo = {Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY},
              hi = {-Float.POSITIVE_INFINITY, -Float.POSITIVE_INFINITY, -Float.POSITIVE_INFINITY};
          for (Object vertex : vs) {
            float[] p = new float[3];
            for (int a = 0; a < 3; a++)
              p[a] =
                  ((Number)
                              vertex
                                  .getClass()
                                  .getMethod(
                                      new String[] {"comp_4804", "comp_4805", "comp_4806"}[a])
                                  .invoke(vertex))
                          .floatValue()
                      / 16f;
            Vector3f v =
                stack
                    .method_23760()
                    .method_23761()
                    .transformPosition(new Vector3f(p[0], p[1], p[2]))
                    .mul(16);
            float[] q = {v.x, v.y, v.z};
            for (int a = 0; a < 3; a++) {
              lo[a] = Math.min(lo[a], q[a]);
              hi[a] = Math.max(hi[a], q[a]);
            }
          }
          for (int a = 0; a < 3; a++)
            if (hi[a] - lo[a] < .00001f) faces.add(new Face(name, a, lo[a], lo, hi));
        }
    }
    String[] upper = {
      "field_3394", "field_3483", "field_3482", "field_3479", "field_3484", "field_3486"
    };
    for (int partIndex = 0; partIndex < upper.length; partIndex++) {
      String name = upper[partIndex];
      class_630 part = (class_630) field(model, name);
      ModelPartInjector injector = (ModelPartInjector) (Object) part;
      Mesh injected = injector.getInjectedMesh();
      if (injected == null) continue;
      List<Mesh> children = new ArrayList<>();
      if (Proxy.isProxyClass(injected.getClass())) {
        Object handler = Proxy.getInvocationHandler(injected);
        for (Field f : handler.getClass().getDeclaredFields()) {
          f.setAccessible(true);
          Object candidate = f.get(handler);
          if (candidate instanceof Mesh m && !Proxy.isProxyClass(m.getClass())) children.add(m);
          if (candidate instanceof Object[] array)
            for (Object element : array) if (element instanceof Mesh m) children.add(m);
        }
      } else children.add(injected);
      int layer = 0;
      for (Mesh mesh : children) {
        String meshName = name + "/" + (layer++);
        class_4587 stack = new class_4587();
        class_630 parent = (class_630) field(model, lower[partIndex]);
        float px = parent.field_3654, py = parent.field_3675, pz = parent.field_3674;
        parent.field_3654 = parent.field_3675 = parent.field_3674 = 0;
        parent.method_22703(stack);
        parent.field_3654 = px;
        parent.field_3675 = py;
        parent.field_3674 = pz;
        float x = part.field_3654, y = part.field_3675, z = part.field_3674;
        part.field_3654 = part.field_3675 = part.field_3674 = 0;
        part.method_22703(stack);
        part.field_3654 = x;
        part.field_3675 = y;
        part.field_3674 = z;
        System.out.println(
            "SUBMITTED_PART "
                + name
                + " pivot="
                + part.field_3657
                + ","
                + part.field_3656
                + ","
                + part.field_3655
                + " scale="
                + part.field_37938
                + ","
                + part.field_37939
                + ","
                + part.field_37940
                + " meshPose="
                + field(mesh, "x")
                + ","
                + field(mesh, "y")
                + ","
                + field(mesh, "xRot")
                + ","
                + field(mesh, "yRot")
                + ","
                + field(mesh, "zRot"));
        injector.getOffsetProvider().applyOffset(stack, mesh);
        mesh.getClass().getMethod("translateAndRotate", class_4587.class).invoke(mesh, stack);
        float[] data = (float[]) field(mesh, "polygonData");
        for (int q = 0; q < data.length; q += 23) {
          float[][] points = new float[4][3];
          for (int v = 0; v < 4; v++)
            for (int a = 0; a < 3; a++) points[v][a] = data[q + 3 + v * 5 + a];
          append(faces, meshName, points, stack);
        }
        for (Object cube : (List<?>) field(mesh, "cubes"))
          for (Object poly : (Object[]) field(cube, "field_3649")) {
            verifySodiumFace(cube, poly);
            Object[] vs = (Object[]) poly.getClass().getMethod("comp_3184").invoke(poly);
            float[][] points = new float[4][3];
            for (int v = 0; v < 4; v++)
              for (int a = 0; a < 3; a++)
                points[v][a] =
                    ((Number)
                                vs[v]
                                    .getClass()
                                    .getMethod(
                                        new String[] {"comp_4804", "comp_4805", "comp_4806"}[a])
                                    .invoke(vs[v]))
                            .floatValue()
                        / 16f;
            append(faces, meshName, points, stack);
          }
      }
    }
    int overlaps = 0;
    for (int i = 0; i < faces.size(); i++)
      for (int j = i + 1; j < faces.size(); j++) {
        Face a = faces.get(i), b = faces.get(j);
        if (a.axis != b.axis || Math.abs(a.plane - b.plane) > .00001f) continue;
        float area = 1;
        for (int k = 0; k < 3; k++)
          if (k != a.axis)
            area *= Math.max(0, Math.min(a.hi[k], b.hi[k]) - Math.max(a.lo[k], b.lo[k]));
        if (area > .00001f) {
          overlaps++;
          if (overlaps < 25)
            System.out.println(
                "SUBMITTED_COPLANAR_OVERLAP parts="
                    + a.part
                    + ","
                    + b.part
                    + " axis="
                    + a.axis
                    + " plane="
                    + a.plane
                    + " area="
                    + area);
        }
      }
    System.out.println(
        "SUBMITTED_GEOMETRY_RESULT allLayerOverlaps=" + overlaps + " faces=" + faces.size());
    return overlaps;
  }

  static void verifySodiumFace(Object cube, Object poly) throws Exception {
    Object cached;
    try {
      cached = field(cube, "sodium$cuboid");
    } catch (NoSuchFieldException absent) {
      return;
    }
    if (((Object[]) field(cube, "field_3649")).length != 1) return;
    Object n = poly.getClass().getMethod("comp_3185").invoke(poly);
    float nx = ((Number) n.getClass().getMethod("x").invoke(n)).floatValue(),
        ny = ((Number) n.getClass().getMethod("y").invoke(n)).floatValue(),
        nz = ((Number) n.getClass().getMethod("z").invoke(n)).floatValue();
    int f =
        Math.abs(ny) > .9f
            ? (ny < 0 ? 0 : 1)
            : Math.abs(nx) > .9f ? (nx < 0 ? 2 : 4) : (nz < 0 ? 3 : 5);
    if (((Number) field(cached, "cullMask")).intValue() != (1 << f))
      throw new AssertionError("Sodium reconstructed extra face");
    Object[] vs = (Object[]) poly.getClass().getMethod("comp_3184").invoke(poly);
    int[] positions = (int[]) field(cached, "positions");
    long[] textures = (long[]) field(cached, "textures");
    int[][] bits = {{0, 1, 1, 0, 0, 1, 1, 0}, {0, 0, 1, 1, 0, 0, 1, 1}, {0, 0, 0, 0, 1, 1, 1, 1}};
    for (int i = 0; i < 4; i++) {
      int vi = positions[f * 4 + i];
      float[] p = new float[3];
      for (int a = 0; a < 3; a++) {
        String suffix = new String[] {"X", "Y", "Z"}[a];
        p[a] =
            ((Number) field(cached, "origin" + suffix)).floatValue()
                + bits[a][vi] * ((Number) field(cached, "size" + suffix)).floatValue();
      }
      boolean found = false;
      for (Object v : vs) {
        float dist = 0;
        for (int a = 0; a < 3; a++)
          dist +=
              Math.abs(
                  p[a]
                      - ((Number)
                                  v.getClass()
                                      .getMethod(
                                          new String[] {"comp_4804", "comp_4805", "comp_4806"}[a])
                                      .invoke(v))
                              .floatValue()
                          / 16f);
        if (dist < .000001f) {
          float u = ((Number) v.getClass().getMethod("comp_3187").invoke(v)).floatValue(),
              vv = ((Number) v.getClass().getMethod("comp_3188").invoke(v)).floatValue();
          long t = textures[f * 4 + i];
          if (Math.abs(Float.intBitsToFloat((int) t) - u) > .000001f
              || Math.abs(Float.intBitsToFloat((int) (t >>> 32)) - vv) > .000001f)
            throw new AssertionError("Sodium UV does not match clipped face");
          found = true;
          break;
        }
      }
      if (!found) throw new AssertionError("Sodium enlarged clipped face");
    }
  }

  static void append(List<Face> faces, String name, float[][] points, class_4587 stack) {
    float[] lo = {Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY},
        hi = {-Float.POSITIVE_INFINITY, -Float.POSITIVE_INFINITY, -Float.POSITIVE_INFINITY};
    for (float[] p : points) {
      Vector3f v =
          stack
              .method_23760()
              .method_23761()
              .transformPosition(new Vector3f(p[0], p[1], p[2]))
              .mul(16);
      float[] q = {v.x, v.y, v.z};
      for (int a = 0; a < 3; a++) {
        lo[a] = Math.min(lo[a], q[a]);
        hi[a] = Math.max(hi[a], q[a]);
      }
    }
    for (int a = 0; a < 3; a++)
      if (hi[a] - lo[a] < .00001f) faces.add(new Face(name, a, lo[a], lo, hi));
  }
}
