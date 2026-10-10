import java.util.*;
import net.tompsen.nexuscharacters.SurfaceGeometry;
import net.tompsen.nexuscharacters.SurfaceGeometry.*;
import org.joml.*;

public class SurfaceGeometryRegression {
  static float area(List<Polygon> faces) {
    float result = 0;
    for (Polygon f : faces) {
      Vertex o = f.vertices().get(0);
      for (int i = 1; i + 1 < f.vertices().size(); i++) {
        Vertex a = f.vertices().get(i), b = f.vertices().get(i + 1);
        result +=
            new Vector3f(a.x() - o.x(), a.y() - o.y(), a.z() - o.z())
                    .cross(new Vector3f(b.x() - o.x(), b.y() - o.y(), b.z() - o.z()))
                    .length()
                / 2;
      }
    }
    return result;
  }

  static void close(float a, float b) {
    if (java.lang.Math.abs(a - b) > .0001) throw new AssertionError(a + " != " + b);
  }

  public static void main(String[] args) {
    Polygon square =
        new Polygon(
            List.of(
                new Vertex(-1, -1, 1, 0, 0),
                new Vertex(1, -1, 1, 1, 0),
                new Vertex(1, 1, 1, 1, 1),
                new Vertex(-1, 1, 1, 0, 1)),
            new Vector3f(0, 0, 1),
            0);
    for (int i = 0; i < 36; i++) {
      Matrix4f m =
          new Matrix4f()
              .translate(.2f, -.3f, .1f)
              .rotateXYZ(i * .07f, i * .11f, i * .09f)
              .scale(.9f, 1.2f, 1.1f);
      Polygon posed = SurfaceGeometry.transform(List.of(square), m).get(0);
      Box box = new Box(new float[] {-.5f, -.5f, 0}, new float[] {.5f, .5f, 1}, m, 1, 0);
      List<Polygon> cut = SurfaceGeometry.subtract(posed, box);
      cut = SurfaceGeometry.transform(cut, new Matrix4f(m).invert());
      close(area(cut), 3);
      for (Polygon f : cut)
        for (Vertex v : f.vertices()) {
          close(v.u(), (v.x() + 1) / 2);
          close(v.v(), (v.y() + 1) / 2);
        }
    }
    Box equal = new Box(new float[] {-1, -1, 0}, new float[] {1, 1, 1}, new Matrix4f(), 0, 0);
    close(area(SurfaceGeometry.subtract(square, equal)), 4);
    Box higher = new Box(new float[] {-1, -1, 0}, new float[] {1, 1, 1}, new Matrix4f(), 1, 0);
    close(area(SurfaceGeometry.subtract(square, higher)), 0);
    Polygon triangle = new Polygon(square.vertices().subList(0, 3), square.normal(), 0);
    List<Polygon> roundtrip = SurfaceGeometry.read(SurfaceGeometry.write(List.of(triangle)), 0);
    close(area(roundtrip), 2);
    if (roundtrip.get(0).vertices().size() != 3)
      throw new AssertionError("Native corner triangle filled");
    System.out.println(
        "SURFACE_GEOMETRY_PASS rotated-subtraction=36 area+uv+corner-triangle+priority");
  }
}
