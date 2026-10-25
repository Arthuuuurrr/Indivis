package net.tompsen.nexuscharacters;

import java.util.*;
import net.tompsen.nexuscharacters.SurfaceGeometry.*;
import net.tompsen.nexuscharacters.PosedSurfaceSupport.Grid;

/** Per-pass exact grid-query reuse. Never caches geometry across poses or reloads. */
public final class ExactSurfaceCleaner {
  private ExactSurfaceCleaner() {}
  private static final int MAX_QUERIES = 2048;
  private static final Comparator<Box> RANK = Comparator.comparingInt(box -> box.rank);
  private record Cells(int x0, int x1, int y0, int y1, int z0, int z1) {}

  private static Cells cells(Polygon polygon, Grid grid) {
    float x0 = Float.POSITIVE_INFINITY, y0 = x0, z0 = x0;
    float x1 = -x0, y1 = -x0, z1 = -x0;
    for (Vertex v : polygon.vertices()) {
      x0 = java.lang.Math.min(x0, v.x() - SurfaceGeometry.EPS);
      x1 = java.lang.Math.max(x1, v.x() + SurfaceGeometry.EPS);
      y0 = java.lang.Math.min(y0, v.y() - SurfaceGeometry.EPS);
      y1 = java.lang.Math.max(y1, v.y() + SurfaceGeometry.EPS);
      z0 = java.lang.Math.min(z0, v.z() - SurfaceGeometry.EPS);
      z1 = java.lang.Math.max(z1, v.z() + SurfaceGeometry.EPS);
    }
    return new Cells(grid.cell(x0), grid.cell(x1), grid.cell(y0), grid.cell(y1), grid.cell(z0), grid.cell(z1));
  }

  public static List<Polygon> clean(List<Polygon> input, Grid grid, int part,
      boolean base, boolean firstPerson, boolean skipOwn) {
    List<Polygon> out = new ArrayList<>();
    Map<Cells, List<Box>> queries = new HashMap<>();
    for (Polygon source : input) {
      Cells key = cells(source, grid);
      List<Box> boxes = queries.get(key);
      if (boxes == null) {
        boxes = new ArrayList<>(grid.find(source, skipOwn ? part : -1));
        boxes.removeIf(box -> skipOwn && box.part == part || firstPerson && box.part != part);
        // Preserve the original identity-set iteration and stable rank ordering.
        boxes.sort(RANK);
        if (queries.size() < MAX_QUERIES) queries.put(key, boxes);
      }
      List<Polygon> fragments = List.of(source);
      for (Box box : boxes) {
        if (base && box.part == part || firstPerson && box.part != part) continue;
        List<Polygon> next = null;
        for (int i = 0; i < fragments.size(); i++) {
          Polygon piece = fragments.get(i);
          if (ExactSurfaceClipper.unchanged(piece, box)) {
            if (next != null) next.add(piece);
          } else {
            if (next == null) {
              next = new ArrayList<>();
              for (int j = 0; j < i; j++) next.add(fragments.get(j));
            }
            next.addAll(ExactSurfaceClipper.intersecting(piece, box, !skipOwn));
          }
        }
        if (next != null) fragments = next;
        if (fragments.isEmpty()) break;
      }
      out.addAll(fragments);
    }
    return out;
  }
}
