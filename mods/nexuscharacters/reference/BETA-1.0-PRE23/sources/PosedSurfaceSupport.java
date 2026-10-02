package net.tompsen.nexuscharacters;

import dev.tr7zw.skinlayers.accessor.*;
import dev.tr7zw.skinlayers.api.*;
import java.lang.reflect.*;
import java.util.*;
import net.minecraft.*;
import org.joml.*;

/** Clips the union of the posed body and native cosmetic voxels before drawing. */
public final class PosedSurfaceSupport {
  static final String[]
      LOWER = {"field_3398", "field_3391", "field_3397", "field_3392", "field_27433", "field_3401"},
      UPPER = {"field_3394", "field_3483", "field_3482", "field_3479", "field_3484", "field_3486"};
  public static long recomputeCount, recomputeNanos, recomputeMaxNanos;
  static final Map<Object, State> STATES = Collections.synchronizedMap(new WeakHashMap<>());

  static record Prepared(Matrix4f relative, List<SurfaceGeometry.Polygon> faces) {}

  static final class State {
    final Map<Object, Prepared> prepared =
        new LinkedHashMap<>(32, .75f, true) {
          protected boolean removeEldestEntry(Map.Entry<Object, Prepared> e) {
            return size() > 32;
          }
        };
    Object[] template;
    Matrix4f[] matrices, relatives;
    boolean firstPerson;
    final List<SurfaceGeometry.Polygon>[] localFaces = new List[6];
    final Object[] base = new Object[6],
        outer = new Object[6],
        baseProxy = new Object[6],
        outerProxy = new Object[6];
    final List<SurfaceGeometry.Polygon>[] original = new List[6];
    final List<float[][]>[] boxes = new List[6];
  }

  static final class Grid {
    final Map<Long, List<SurfaceGeometry.Box>>[] cells = new Map[6];

    Grid() {
      for (int p = 0; p < 6; p++) cells[p] = new HashMap<>();
    }

    int cell(float f) {
      return (int) java.lang.Math.floor(f * 8);
    }

    long key(int x, int y, int z) {
      return ((long) x & 0x1fffffL) << 42 | ((long) y & 0x1fffffL) << 21 | ((long) z & 0x1fffffL);
    }

    void add(SurfaceGeometry.Box b) {
      for (int x = cell(b.worldLo[0]); x <= cell(b.worldHi[0]); x++)
        for (int y = cell(b.worldLo[1]); y <= cell(b.worldHi[1]); y++)
          for (int z = cell(b.worldLo[2]); z <= cell(b.worldHi[2]); z++)
            cells[b.part].computeIfAbsent(key(x, y, z), k -> new ArrayList<>()).add(b);
    }

    Set<SurfaceGeometry.Box> find(SurfaceGeometry.Polygon p, int excludedPart) {
      float[] lo = {Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY},
          hi = {-lo[0], -lo[1], -lo[2]};
      for (SurfaceGeometry.Vertex v : p.vertices()) {
        float[] xyz = {v.x(), v.y(), v.z()};
        for (int a = 0; a < 3; a++) {
          lo[a] = java.lang.Math.min(lo[a], xyz[a] - SurfaceGeometry.EPS);
          hi[a] = java.lang.Math.max(hi[a], xyz[a] + SurfaceGeometry.EPS);
        }
      }
      Set<SurfaceGeometry.Box> found = Collections.newSetFromMap(new IdentityHashMap<>());
      for (int x = cell(lo[0]); x <= cell(hi[0]); x++)
        for (int y = cell(lo[1]); y <= cell(hi[1]); y++)
          for (int z = cell(lo[2]); z <= cell(hi[2]); z++) {
            for (int part = 0; part < 6; part++) {
              if (part == excludedPart) continue;
              List<SurfaceGeometry.Box> c = cells[part].get(key(x, y, z));
              if (c != null) found.addAll(c);
            }
          }
      return found;
    }
  }

  public static void apply(Object model, Object[] cooked, boolean slim, boolean hideHead)
      throws Exception {
    apply(model, cooked, slim, hideHead, false);
  }

  public static void firstPerson(Object model, Object[] cooked, boolean slim) throws Exception {
    apply(model, cooked, slim, false, true);
  }

  static void apply(
      Object model, Object[] cooked, boolean slim, boolean hideHead, boolean firstPerson)
      throws Exception {
    State s = STATES.get(model);
    if (s == null) {
      s = new State();
      STATES.put(model, s);
      for (int p = 0; p < 6; p++) {
        Object lower = SurfaceGeometry.field(model, LOWER[p]);
        s.original[p] = readCubes((List<?>) SurfaceGeometry.field(lower, "field_3663"), 100 + p);
        s.boxes[p] = bounds((List<?>) SurfaceGeometry.field(lower, "field_3663"));
        s.base[p] = NativeVoxelSurface.emptyMesh();
        s.outer[p] = NativeVoxelSurface.emptyMesh();
        SurfaceGeometry.set(s.base[p], "children", SurfaceGeometry.field(lower, "field_3661"));
        s.baseProxy[p] = wrap(s.base[p], true);
        s.outerProxy[p] = wrap(s.outer[p], false);
      }
    }
    Matrix4f[] matrices = new Matrix4f[12], relatives = new Matrix4f[6];
    Object[] templates = new Object[6];
    for (int p = 0; p < 6; p++) {
      class_630 low = (class_630) SurfaceGeometry.field(model, LOWER[p]),
          up = (class_630) SurfaceGeometry.field(model, UPPER[p]);
      up.field_37938 = up.field_37939 = up.field_37940 = 1;
      class_4587 stack = new class_4587();
      low.method_22703(stack);
      matrices[p] = new Matrix4f(stack.method_23760().method_23761());
      up.method_22703(stack);
      matrices[p + 6] = new Matrix4f(stack.method_23760().method_23761());
      class_4587 relative = new class_4587();
      up.method_22703(relative);
      relatives[p] = new Matrix4f(relative.method_23760().method_23761());
      templates[p] = p == 0 && hideHead ? null : cooked[slim && p >= 4 ? p + 2 : p];
      up.field_3665 = !(p == 0 && hideHead);
    }
    boolean changed =
        s.firstPerson != firstPerson
            || s.template == null
            || !Arrays.equals(templates, s.template)
            || !Arrays.equals(matrices, s.matrices);
    if (changed) {
      long started = System.nanoTime();
      boolean localChanged =
          s.template == null
              || !Arrays.equals(templates, s.template)
              || !Arrays.equals(relatives, s.relatives);
      if (localChanged)
        for (int p = 0; p < 6; p++) {
          NativeVoxelSurface.Shape shape =
              templates[p] == null ? null : NativeVoxelSurface.SHAPES.get(templates[p]);
          s.localFaces[p] = List.of();
          if (shape == null) continue;
          Prepared cached = s.prepared.get(templates[p]);
          if (cached != null && cached.relative().equals(relatives[p])) {
            s.localFaces[p] = cached.faces();
            continue;
          }
          Grid local = new Grid();
          for (float[][] b : s.boxes[p])
            local.add(
                new SurfaceGeometry.Box(
                    b[0], b[1], new Matrix4f(relatives[p]).invert(), 100 + p, p));
          for (NativeVoxelSurface.Bounds b : shape.boxes())
            local.add(
                new SurfaceGeometry.Box(
                    b.lo(), b.hi(), new Matrix4f(), 2000 + b.rank() * 6 + p, p));
          List<SurfaceGeometry.Polygon> ranked = new ArrayList<>();
          for (SurfaceGeometry.Polygon f : shape.faces())
            ranked.add(
                new SurfaceGeometry.Polygon(f.vertices(), f.normal(), 2000 + f.rank() * 6 + p));
          s.localFaces[p] = clean(ranked, local, p, false, false, false);
          s.prepared.put(templates[p], new Prepared(new Matrix4f(relatives[p]), s.localFaces[p]));
        }
      if (firstPerson) {
        for (int p = 0; p < 6; p++) {
          SurfaceGeometry.set(s.base[p], "polygonData", SurfaceGeometry.write(s.original[p]));
          SurfaceGeometry.set(s.outer[p], "polygonData", SurfaceGeometry.write(s.localFaces[p]));
        }
      } else {
        Grid grid = new Grid();
        for (int p = 0; p < 6; p++) {
          for (float[][] b : s.boxes[p]) {
            SurfaceGeometry.Box core = new SurfaceGeometry.Box(b[0], b[1], matrices[p], 100 + p, p);
            grid.add(core);
          }
          NativeVoxelSurface.Shape shape =
              templates[p] == null ? null : NativeVoxelSurface.SHAPES.get(templates[p]);
          if (shape != null)
            for (NativeVoxelSurface.Bounds b : shape.boxes())
              grid.add(
                  new SurfaceGeometry.Box(
                      b.lo(), b.hi(), matrices[p + 6], 2000 + b.rank() * 6 + p, p));
        }
        for (int p = 0; p < 6; p++) {
          List<SurfaceGeometry.Polygon>
              lower =
                  clean(
                      SurfaceGeometry.transform(s.original[p], matrices[p]),
                      grid,
                      p,
                      true,
                      firstPerson,
                      true),
              outer = new ArrayList<>();
          if (s.localFaces[p] != null)
            outer =
                clean(
                    SurfaceGeometry.transform(s.localFaces[p], matrices[p + 6]),
                    grid,
                    p,
                    false,
                    firstPerson,
                    true);
          SurfaceGeometry.set(
              s.base[p],
              "polygonData",
              SurfaceGeometry.write(
                  SurfaceGeometry.transform(lower, new Matrix4f(matrices[p]).invert())));
          SurfaceGeometry.set(
              s.outer[p],
              "polygonData",
              SurfaceGeometry.write(
                  SurfaceGeometry.transform(outer, new Matrix4f(matrices[p + 6]).invert())));
        }
      }
      s.template = templates;
      s.matrices = matrices;
      s.relatives = relatives;
      s.firstPerson = firstPerson;
      long elapsed = System.nanoTime() - started;
      recomputeCount++;
      recomputeNanos += elapsed;
      recomputeMaxNanos = java.lang.Math.max(recomputeMaxNanos, elapsed);
    }
    for (int p = 0; p < 6; p++) {
      ((ModelPartInjector) SurfaceGeometry.field(model, LOWER[p]))
          .setInjectedMesh((Mesh) s.baseProxy[p], NativeVoxelSurface.IDENTITY);
      ((ModelPartInjector) SurfaceGeometry.field(model, UPPER[p]))
          .setInjectedMesh(
              templates[p] == null ? null : (Mesh) s.outerProxy[p], NativeVoxelSurface.IDENTITY);
    }
  }

  static List<SurfaceGeometry.Polygon> clean(
      List<SurfaceGeometry.Polygon> input,
      Grid grid,
      int part,
      boolean base,
      boolean firstPerson,
      boolean skipOwn) {
    List<SurfaceGeometry.Polygon> out = new ArrayList<>();
    for (SurfaceGeometry.Polygon f : input) {
      List<SurfaceGeometry.Polygon> fragments = new ArrayList<>();
      fragments.add(f);
      List<SurfaceGeometry.Box> boxes = new ArrayList<>(grid.find(f, skipOwn ? part : -1));
      boxes.removeIf(b -> skipOwn && b.part == part || firstPerson && b.part != part);
      boxes.sort(Comparator.comparingInt(b -> b.rank));
      for (SurfaceGeometry.Box b : boxes) {
        if (base && b.part == part || firstPerson && b.part != part) continue;
        List<SurfaceGeometry.Polygon> next = new ArrayList<>();
        for (SurfaceGeometry.Polygon piece : fragments)
          next.addAll(SurfaceGeometry.subtract(piece, b));
        fragments = next;
        if (fragments.isEmpty()) break;
      }
      out.addAll(fragments);
    }
    return out;
  }

  static Object wrap(Object mesh, boolean base) {
    return Proxy.newProxyInstance(
        Mesh.class.getClassLoader(),
        new Class<?>[] {Mesh.class},
        (p, m, args) -> {
          if (m.getDeclaringClass() == Object.class) {
            if (m.getName().equals("toString"))
              return "NexusPosed23[" + (base ? "base" : "outer") + "]";
            if (m.getName().equals("hashCode")) return System.identityHashCode(p);
            return p == args[0];
          }
          if (m.getName().equals("render") && m.getParameterCount() == 6) {
            if (base) GenericSkinLayerSupport.drawnBase++;
            else GenericSkinLayerSupport.drawnOuter++;
          }
          try {
            return m.invoke(mesh, args);
          } catch (InvocationTargetException e) {
            throw e.getCause();
          }
        });
  }

  public static void clear(Object model) throws Exception {
    State s = STATES.remove(model);
    if (s == null) return;
    for (int p = 0; p < 6; p++) {
      ModelPartInjector low = (ModelPartInjector) SurfaceGeometry.field(model, LOWER[p]),
          up = (ModelPartInjector) SurfaceGeometry.field(model, UPPER[p]);
      if (low.getInjectedMesh() == s.baseProxy[p]) low.setInjectedMesh(null, null);
      if (up.getInjectedMesh() == s.outerProxy[p]) up.setInjectedMesh(null, null);
    }
  }

  static List<float[][]> bounds(List<?> cubes) throws Exception {
    List<float[][]> out = new ArrayList<>();
    for (Object c : cubes) {
      float[] lo = new float[3], hi = new float[3];
      String[] low = {"field_3645", "field_3644", "field_3643"},
          high = {"field_3648", "field_3647", "field_3646"};
      for (int a = 0; a < 3; a++) {
        lo[a] = ((Number) SurfaceGeometry.field(c, low[a])).floatValue() / 16;
        hi[a] = ((Number) SurfaceGeometry.field(c, high[a])).floatValue() / 16;
      }
      out.add(new float[][] {lo, hi});
    }
    return out;
  }

  static List<SurfaceGeometry.Polygon> readCubes(List<?> cubes, int rank) throws Exception {
    List<SurfaceGeometry.Polygon> out = new ArrayList<>();
    for (Object c : cubes)
      for (Object f : (Object[]) SurfaceGeometry.field(c, "field_3649")) {
        Object[] vs = (Object[]) SurfaceGeometry.call(f, "comp_3184");
        Object normal = SurfaceGeometry.call(f, "comp_3185");
        List<SurfaceGeometry.Vertex> p = new ArrayList<>();
        for (Object v : vs)
          p.add(
              new SurfaceGeometry.Vertex(
                  ((Number) SurfaceGeometry.call(v, "comp_4804")).floatValue() / 16,
                  ((Number) SurfaceGeometry.call(v, "comp_4805")).floatValue() / 16,
                  ((Number) SurfaceGeometry.call(v, "comp_4806")).floatValue() / 16,
                  ((Number) SurfaceGeometry.call(v, "comp_3187")).floatValue(),
                  ((Number) SurfaceGeometry.call(v, "comp_3188")).floatValue()));
        out.add(
            new SurfaceGeometry.Polygon(
                p,
                new Vector3f(
                    ((Number) SurfaceGeometry.call(normal, "x")).floatValue(),
                    ((Number) SurfaceGeometry.call(normal, "y")).floatValue(),
                    ((Number) SurfaceGeometry.call(normal, "z")).floatValue()),
                rank));
      }
    return out;
  }
}
