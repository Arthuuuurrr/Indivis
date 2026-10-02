package net.tompsen.nexuscharacters;

import java.lang.reflect.*;
import java.util.*;
import org.joml.*;

/** Convex subtraction preserves positions, UVs and native corner triangles. */
public final class SurfaceGeometry {
  public static final float EPS = 0.00003f;

  public record Vertex(float x, float y, float z, float u, float v) {
    public Vertex mix(Vertex b, float t) {
      return new Vertex(
          x + t * (b.x - x),
          y + t * (b.y - y),
          z + t * (b.z - z),
          u + t * (b.u - u),
          v + t * (b.v - v));
    }

    public Vertex transform(Matrix4f m) {
      Vector3f p = m.transformPosition(new Vector3f(x, y, z));
      return new Vertex(p.x, p.y, p.z, u, v);
    }
  }

  public record Polygon(List<Vertex> vertices, Vector3f normal, int rank) {}

  public static final class Box {
    public final float[] lo, hi, worldLo = new float[3], worldHi = new float[3];
    public final Matrix4f inverse;
    public final int rank, part;

    public Box(float[] lo, float[] hi, Matrix4f matrix, int rank, int part) {
      this.lo = lo.clone();
      this.hi = hi.clone();
      this.inverse = new Matrix4f(matrix).invert();
      this.rank = rank;
      this.part = part;
      Arrays.fill(worldLo, Float.POSITIVE_INFINITY);
      Arrays.fill(worldHi, Float.NEGATIVE_INFINITY);
      for (int bits = 0; bits < 8; bits++) {
        Vector3f p =
            matrix.transformPosition(
                new Vector3f(
                    (bits & 1) == 0 ? lo[0] : hi[0],
                    (bits & 2) == 0 ? lo[1] : hi[1],
                    (bits & 4) == 0 ? lo[2] : hi[2]));
        float[] xyz = {p.x, p.y, p.z};
        for (int a = 0; a < 3; a++) {
          worldLo[a] = java.lang.Math.min(worldLo[a], xyz[a]);
          worldHi[a] = java.lang.Math.max(worldHi[a], xyz[a]);
        }
      }
    }

    public boolean near(Polygon f) {
      for (int axis = 0; axis < 3; axis++) {
        float low = Float.POSITIVE_INFINITY, high = Float.NEGATIVE_INFINITY;
        for (Vertex v : f.vertices) {
          float x = axis == 0 ? v.x : axis == 1 ? v.y : v.z;
          low = java.lang.Math.min(low, x);
          high = java.lang.Math.max(high, x);
        }
        if (high < worldLo[axis] - EPS || low > worldHi[axis] + EPS) return false;
      }
      return true;
    }

    float distance(Vertex p, int plane) {
      Vector3f v = inverse.transformPosition(new Vector3f(p.x, p.y, p.z));
      int axis = plane / 2;
      float n = axis == 0 ? v.x : axis == 1 ? v.y : v.z;
      return plane % 2 == 0 ? lo[axis] - n : n - hi[axis];
    }

    boolean sameExterior(Polygon f) {
      for (int plane = 0; plane < 6; plane++) {
        boolean same = true;
        for (Vertex p : f.vertices)
          if (java.lang.Math.abs(distance(p, plane)) > EPS * 2) {
            same = false;
            break;
          }
        if (same) {
          Vector3f local = inverse.transformDirection(new Vector3f(f.normal)).normalize();
          float n = plane / 2 == 0 ? local.x : plane / 2 == 1 ? local.y : local.z;
          if (n * (plane % 2 == 0 ? -1 : 1) > .99f) return true;
        }
      }
      return false;
    }
  }

  public static List<Polygon> subtract(Polygon source, Box b) {
    if (!b.near(source) || b.sameExterior(source) && b.rank <= source.rank) return List.of(source);
    List<Vertex> inside = source.vertices;
    List<Polygon> result = new ArrayList<>();
    for (int plane = 0; plane < 6 && !inside.isEmpty(); plane++) {
      List<Vertex> out = clip(inside, b, plane, true), next = clip(inside, b, plane, false);
      if (valid(out)) result.add(new Polygon(out, source.normal, source.rank));
      inside = next;
    }
    return result;
  }

  static List<Vertex> clip(List<Vertex> poly, Box box, int plane, boolean outside) {
    List<Vertex> out = new ArrayList<>();
    float sense = outside ? 1 : -1;
    for (int i = 0; i < poly.size(); i++) {
      Vertex a = poly.get(i), b = poly.get((i + 1) % poly.size());
      float d = box.distance(a, plane) * sense, e = box.distance(b, plane) * sense;
      boolean ia = d > EPS, ib = e > EPS;
      if (!outside) {
        ia = d >= -EPS;
        ib = e >= -EPS;
      }
      if (ia) out.add(a);
      if (ia != ib) {
        float t = d / (d - e);
        if (Float.isFinite(t)) out.add(a.mix(b, java.lang.Math.max(0, java.lang.Math.min(1, t))));
      }
    }
    return out;
  }

  public static boolean valid(List<Vertex> p) {
    if (p.size() < 3) return false;
    Vertex o = p.get(0);
    float area = 0;
    for (int i = 1; i + 1 < p.size(); i++) {
      Vertex a = p.get(i), b = p.get(i + 1);
      area +=
          new Vector3f(a.x - o.x, a.y - o.y, a.z - o.z)
              .cross(new Vector3f(b.x - o.x, b.y - o.y, b.z - o.z))
              .length();
    }
    return area > EPS * EPS;
  }

  public static List<Polygon> read(float[] data, int rank) {
    List<Polygon> out = new ArrayList<>();
    for (int i = 0; i < data.length; i += 23) {
      List<Vertex> p = new ArrayList<>();
      for (int k = 0; k < 4; k++) {
        int at = i + 3 + k * 5;
        Vertex v = new Vertex(data[at], data[at + 1], data[at + 2], data[at + 3], data[at + 4]);
        if (p.isEmpty() || !p.get(p.size() - 1).equals(v)) p.add(v);
      }
      if (valid(p))
        out.add(new Polygon(List.copyOf(p), new Vector3f(data[i], data[i + 1], data[i + 2]), rank));
    }
    return out;
  }

  public static List<Polygon> transform(List<Polygon> input, Matrix4f m) {
    List<Polygon> out = new ArrayList<>();
    Matrix3f normal = new Matrix3f(m).invert().transpose();
    for (Polygon f : input) {
      List<Vertex> p = new ArrayList<>();
      for (Vertex v : f.vertices) p.add(v.transform(m));
      out.add(new Polygon(p, normal.transform(new Vector3f(f.normal)).normalize(), f.rank));
    }
    return out;
  }

  public static float[] write(List<Polygon> faces) {
    List<Float> out = new ArrayList<>();
    for (Polygon f : faces) {
      List<Vertex> p = f.vertices;
      if (!valid(p)) continue;
      if (p.size() == 4) add(out, f, p.get(0), p.get(1), p.get(2), p.get(3));
      else
        for (int i = 1; i + 1 < p.size(); i++)
          add(out, f, p.get(0), p.get(i), p.get(i + 1), p.get(i + 1));
    }
    float[] data = new float[out.size()];
    for (int i = 0; i < data.length; i++) data[i] = out.get(i);
    return data;
  }

  static void add(List<Float> data, Polygon f, Vertex... p) {
    data.add(f.normal.x);
    data.add(f.normal.y);
    data.add(f.normal.z);
    for (Vertex v : p) {
      data.add(v.x);
      data.add(v.y);
      data.add(v.z);
      data.add(v.u);
      data.add(v.v);
    }
  }

  public static Object field(Object o, String n) throws Exception {
    for (Class<?> c = o instanceof Class<?> k ? k : o.getClass(); c != null; c = c.getSuperclass())
      try {
        Field f = c.getDeclaredField(n);
        f.setAccessible(true);
        return f.get(o instanceof Class<?> ? null : o);
      } catch (NoSuchFieldException ignored) {
      }
    throw new NoSuchFieldException(n);
  }

  public static void set(Object o, String n, Object v) throws Exception {
    for (Class<?> c = o.getClass(); c != null; c = c.getSuperclass())
      try {
        Field f = c.getDeclaredField(n);
        f.setAccessible(true);
        f.set(o, v);
        return;
      } catch (NoSuchFieldException ignored) {
      }
    throw new NoSuchFieldException(n);
  }

  public static Object call(Object o, String n, Object... args) throws Exception {
    for (Method m : o.getClass().getMethods())
      if (m.getName().equals(n) && m.getParameterCount() == args.length) return m.invoke(o, args);
    throw new NoSuchMethodException(n);
  }
}
