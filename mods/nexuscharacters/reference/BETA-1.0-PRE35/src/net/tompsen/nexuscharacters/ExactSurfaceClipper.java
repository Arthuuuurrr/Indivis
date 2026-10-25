package net.tompsen.nexuscharacters;

import java.util.*;
import org.joml.Vector3f;
import net.tompsen.nexuscharacters.SurfaceGeometry.*;

/** Exact convex subtraction; only projections and temporary storage are reused. */
public final class ExactSurfaceClipper {
  private ExactSurfaceClipper() {}
  private static final ThreadLocal<Workspace> WORK = ThreadLocal.withInitial(Workspace::new);
  private static final class Workspace {
    float[] first = new float[72], second = new float[72];
    final Vector3f projection = new Vector3f();
    float[] capacity(float[] buffer, int count) {
      if (count <= buffer.length) return buffer;
      float[] larger = Arrays.copyOf(buffer, java.lang.Math.max(count, buffer.length * 2));
      if (larger.length <= 3072) {
        if (buffer == first) first = larger;
        else if (buffer == second) second = larger;
      }
      return larger;
    }
  }

  static boolean unchanged(Polygon source, Box box) {
    if (!box.near(source)) return true;
    // Preserve the legacy null-normal exception path as well as valid geometry.
    if (source.normal() == null) return box.sameExterior(source) && box.rank <= source.rank();
    return box.rank <= source.rank() && box.sameExterior(source);
  }

  public static List<Polygon> subtract(Polygon source, Box box, boolean connected) {
    return unchanged(source, box) ? List.of(source) : intersecting(source, box, connected);
  }

  private static void project(Vertex vertex, Box box, Workspace work, float[] out, int at) {
    Vector3f v = box.inverse.transformPosition(work.projection.set(vertex.x(), vertex.y(), vertex.z()));
    out[at] = v.x; out[at + 1] = v.y; out[at + 2] = v.z;
  }

  static List<Polygon> intersecting(Polygon source, Box box, boolean connected) {
    Workspace work = WORK.get();
    List<Vertex> inside = source.vertices();
    List<Polygon> result = new ArrayList<>();
    float[] coordinates = work.capacity(work.first, inside.size() * 3);
    float[] spare = work.second;
    for (int i = 0; i < inside.size(); i++) project(inside.get(i), box, work, coordinates, i * 3);
    float outsideBoundary = connected ? 0 : SurfaceGeometry.EPS / 10;
    float insideBoundary = connected ? 0 : -SurfaceGeometry.EPS / 10;
    for (int plane = 0; plane < 6 && !inside.isEmpty(); plane++) {
      int axis = plane / 2, size = inside.size(), outsideCount = 0, insideCount = 0;
      for (int i = 0; i < size; i++) {
        float value = coordinates[i * 3 + axis];
        float distance = plane % 2 == 0 ? box.lo[axis] - value : value - box.hi[axis];
        if (distance > outsideBoundary) outsideCount++;
        if (-distance >= insideBoundary) insideCount++;
      }
      if (insideCount == size && outsideCount == 0) continue;
      if (outsideCount == size && insideCount == 0) {
        if (SurfaceGeometry.valid(inside))
          result.add(new Polygon(new ArrayList<>(inside), source.normal(), source.rank()));
        break;
      }
      List<Vertex> outside = new ArrayList<>(), next = new ArrayList<>();
      spare = work.capacity(spare, (size * 2 + 2) * 3);
      for (int i = 0; i < size; i++) {
        int j = (i + 1) % size;
        Vertex a = inside.get(i), b = inside.get(j);
        float av = coordinates[i * 3 + axis], bv = coordinates[j * 3 + axis];
        float d = plane % 2 == 0 ? box.lo[axis] - av : av - box.hi[axis];
        float e = plane % 2 == 0 ? box.lo[axis] - bv : bv - box.hi[axis];
        boolean ia = d > outsideBoundary, ib = e > outsideBoundary;
        if (ia) outside.add(a);
        if (ia != ib) {
          float t = (d - outsideBoundary) / (d - e);
          if (Float.isFinite(t)) outside.add(a.mix(b, java.lang.Math.max(0, java.lang.Math.min(1, t))));
        }
        // Do not simplify this formula: preserve the base's floating-point operations.
        float nd = d * -1, ne = e * -1;
        boolean na = nd >= insideBoundary, nb = ne >= insideBoundary;
        if (na) {
          int at = next.size() * 3;
          next.add(a);
          System.arraycopy(coordinates, i * 3, spare, at, 3);
        }
        if (na != nb) {
          float t = (nd - insideBoundary) / (nd - ne);
          if (Float.isFinite(t)) {
            Vertex v = a.mix(b, java.lang.Math.max(0, java.lang.Math.min(1, t)));
            project(v, box, work, spare, next.size() * 3);
            next.add(v);
          }
        }
      }
      if (SurfaceGeometry.valid(outside)) result.add(new Polygon(outside, source.normal(), source.rank()));
      inside = next;
      float[] swap = coordinates; coordinates = spare; spare = swap;
    }
    return result;
  }
}
