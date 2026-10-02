import dev.tr7zw.skinlayers.accessor.*;
import dev.tr7zw.skinlayers.api.*;
import java.lang.reflect.*;
import java.util.*;
import net.minecraft.*;
import org.joml.Vector3f;

/** Measures submitted polygons with their real rotations and scales. */
public final class PoseAudit {
  static final double EPS = 0.0001;
  static final String[] LOWER = {
    "field_3398", "field_3391", "field_3397", "field_3392", "field_27433", "field_3401"
  };
  static final String[] UPPER = {
    "field_3394", "field_3483", "field_3482", "field_3479", "field_3484", "field_3486"
  };

  record Face(String owner, double[][] p, double[] n, double plane, double[] lo, double[] hi) {}

  static int[] run(Object model) throws Exception {
    List<Face> faces = new ArrayList<>();
    for (int index = 0; index < 6; index++) {
      class_630 lower = (class_630) SubmittedGeometryAudit.field(model, LOWER[index]);
      class_4587 stack = new class_4587();
      lower.method_22703(stack);
      submitted(faces, "base/" + index, lower, stack, true);
      class_630 upper = (class_630) SubmittedGeometryAudit.field(model, UPPER[index]);
      upper.method_22703(stack);
      submitted(faces, "outer/" + index, upper, stack, false);
    }
    int coplanar = 0, crossing = 0;
    Map<String, Integer> pairs = new TreeMap<>();
    for (int i = 0; i < faces.size(); i++)
      for (int j = i + 1; j < faces.size(); j++) {
        Face a = faces.get(i), b = faces.get(j);
        boolean possible = true;
        for (int k = 0; k < 3; k++)
          if (a.hi[k] < b.lo[k] - EPS || b.hi[k] < a.lo[k] - EPS) {
            possible = false;
            break;
          }
        if (!possible) continue;
        double[] line = cross(a.n, b.n);
        double length = Math.sqrt(dot(line, line));
        if (length < 0.00001) {
          if (Math.abs(dot(a.n, b.p[0]) - a.plane) < EPS && coplanarArea(a, b) > EPS) {
            coplanar++;
            if (coplanar <= 16)
              System.out.println(
                  "POSE_COPLANAR parts="
                      + a.owner
                      + ","
                      + b.owner
                      + " area="
                      + coplanarArea(a, b)
                      + " a="
                      + Arrays.deepToString(a.p)
                      + " b="
                      + Arrays.deepToString(b.p));
          }
        } else if (cuts(a, b) && cuts(b, a)) {
          for (int k = 0; k < 3; k++) line[k] /= length;
          double[] aa = segment(a, b, line), bb = segment(b, a, line);
          if (aa != null && bb != null && Math.min(aa[1], bb[1]) - Math.max(aa[0], bb[0]) > EPS) {
            crossing++;
            pairs.merge(a.owner + " <> " + b.owner, 1, Integer::sum);
          }
        }
      }
    System.out.println(
        "POSE_GEOMETRY_RESULT faces="
            + faces.size()
            + " coplanar="
            + coplanar
            + " crossings="
            + crossing
            + " pairs="
            + pairs);
    return new int[] {coplanar, crossing};
  }

  static void submitted(
      List<Face> faces, String name, class_630 part, class_4587 stack, boolean fallback)
      throws Exception {
    Mesh injected = ((ModelPartInjector) (Object) part).getInjectedMesh();
    if (injected == null) {
      if (fallback)
        cubes(faces, name, (List<?>) SubmittedGeometryAudit.field(part, "field_3663"), stack);
      return;
    }
    List<Mesh> meshes = new ArrayList<>();
    if (Proxy.isProxyClass(injected.getClass())) {
      Object h = Proxy.getInvocationHandler(injected);
      for (Field f : h.getClass().getDeclaredFields()) {
        f.setAccessible(true);
        Object v = f.get(h);
        if (v instanceof Mesh m) meshes.add(m);
        if (v instanceof Object[] a) for (Object e : a) if (e instanceof Mesh m) meshes.add(m);
      }
    } else meshes.add(injected);
    for (Mesh mesh : meshes) {
      stack.method_22903();
      ((ModelPartInjector) (Object) part).getOffsetProvider().applyOffset(stack, mesh);
      mesh.getClass().getMethod("translateAndRotate", class_4587.class).invoke(mesh, stack);
      float[] data = (float[]) SubmittedGeometryAudit.field(mesh, "polygonData");
      for (int q = 0; q < data.length; q += 23) {
        double[][] p = new double[4][3];
        for (int v = 0; v < 4; v++) for (int a = 0; a < 3; a++) p[v][a] = data[q + 3 + v * 5 + a];
        append(faces, name, p, stack);
      }
      cubes(faces, name, (List<?>) SubmittedGeometryAudit.field(mesh, "cubes"), stack);
      stack.method_22909();
    }
  }

  static void cubes(List<Face> faces, String name, List<?> cubes, class_4587 stack)
      throws Exception {
    for (Object cube : cubes)
      for (Object poly : (Object[]) SubmittedGeometryAudit.field(cube, "field_3649")) {
        Object[] vs = (Object[]) poly.getClass().getMethod("comp_3184").invoke(poly);
        double[][] p = new double[4][3];
        for (int v = 0; v < 4; v++)
          for (int a = 0; a < 3; a++)
            p[v][a] =
                ((Number)
                            vs[v]
                                .getClass()
                                .getMethod(new String[] {"comp_4804", "comp_4805", "comp_4806"}[a])
                                .invoke(vs[v]))
                        .doubleValue()
                    / 16;
        append(faces, name, p, stack);
      }
  }

  static void append(List<Face> faces, String name, double[][] p, class_4587 stack) {
    double[] lo = {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY},
        hi = {-lo[0], -lo[1], -lo[2]};
    for (int i = 0; i < 4; i++) {
      Vector3f v =
          stack
              .method_23760()
              .method_23761()
              .transformPosition(new Vector3f((float) p[i][0], (float) p[i][1], (float) p[i][2]))
              .mul(16);
      p[i] = new double[] {v.x, v.y, v.z};
      for (int a = 0; a < 3; a++) {
        lo[a] = Math.min(lo[a], p[i][a]);
        hi[a] = Math.max(hi[a], p[i][a]);
      }
    }
    double[] n = cross(sub(p[1], p[0]), sub(p[3], p[0]));
    double len = Math.sqrt(dot(n, n));
    if (len < EPS * EPS) return;
    for (int a = 0; a < 3; a++) n[a] /= len;
    faces.add(new Face(name, p, n, dot(n, p[0]), lo, hi));
  }

  static boolean cuts(Face a, Face plane) {
    double lo = Double.POSITIVE_INFINITY, hi = -lo;
    for (double[] p : a.p) {
      double d = dot(plane.n, p) - plane.plane;
      lo = Math.min(lo, d);
      hi = Math.max(hi, d);
    }
    return lo < -EPS && hi > EPS;
  }

  static double[] segment(Face a, Face plane, double[] direction) {
    double lo = Double.POSITIVE_INFINITY, hi = -lo;
    for (int i = 0; i < 4; i++) {
      double[] p = a.p[i], q = a.p[(i + 1) % 4];
      double d = dot(plane.n, p) - plane.plane, e = dot(plane.n, q) - plane.plane;
      if (Math.abs(d) < EPS) {
        double t = dot(direction, p);
        lo = Math.min(lo, t);
        hi = Math.max(hi, t);
      }
      if (d * e < 0) {
        double t = d / (d - e);
        double[] x = {p[0] + t * (q[0] - p[0]), p[1] + t * (q[1] - p[1]), p[2] + t * (q[2] - p[2])};
        double s = dot(direction, x);
        lo = Math.min(lo, s);
        hi = Math.max(hi, s);
      }
    }
    return Double.isFinite(lo) ? new double[] {lo, hi} : null;
  }

  static double coplanarArea(Face a, Face b) {
    int axis = 0;
    for (int k = 1; k < 3; k++) if (Math.abs(a.n[k]) > Math.abs(a.n[axis])) axis = k;
    int u = (axis + 1) % 3, v = (axis + 2) % 3;
    List<double[]> poly = new ArrayList<>();
    for (double[] p : a.p) poly.add(new double[] {p[u], p[v]});
    double signed = 0;
    for (int i = 0; i < 4; i++)
      signed += b.p[i][u] * b.p[(i + 1) % 4][v] - b.p[(i + 1) % 4][u] * b.p[i][v];
    double sense = signed >= 0 ? 1 : -1;
    for (int edge = 0; edge < 4 && !poly.isEmpty(); edge++) {
      double[] x = {b.p[edge][u], b.p[edge][v]},
          y = {b.p[(edge + 1) % 4][u], b.p[(edge + 1) % 4][v]};
      List<double[]> next = new ArrayList<>();
      for (int i = 0; i < poly.size(); i++) {
        double[] p = poly.get(i), q = poly.get((i + 1) % poly.size());
        double d = side(x, y, p) * sense, e = side(x, y, q) * sense;
        if (d >= -EPS) next.add(p);
        if ((d > EPS && e < -EPS) || (d < -EPS && e > EPS)) {
          double t = d / (d - e);
          next.add(new double[] {p[0] + t * (q[0] - p[0]), p[1] + t * (q[1] - p[1])});
        }
      }
      poly = next;
    }
    double area = 0;
    for (int i = 0; i < poly.size(); i++) {
      double[] p = poly.get(i), q = poly.get((i + 1) % poly.size());
      area += p[0] * q[1] - q[0] * p[1];
    }
    return Math.abs(area) / 2;
  }

  static double side(double[] a, double[] b, double[] p) {
    return (b[0] - a[0]) * (p[1] - a[1]) - (b[1] - a[1]) * (p[0] - a[0]);
  }

  static double dot(double[] a, double[] b) {
    return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
  }

  static double[] sub(double[] a, double[] b) {
    return new double[] {a[0] - b[0], a[1] - b[1], a[2] - b[2]};
  }

  static double[] cross(double[] a, double[] b) {
    return new double[] {
      a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]
    };
  }
}
