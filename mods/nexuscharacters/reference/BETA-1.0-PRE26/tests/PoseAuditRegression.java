/** Independent known intersections for the geometric acceptance audit. */
public final class PoseAuditRegression {
  static PoseAudit.Face face(double[][] points) {
    return new PoseAudit.Face("test", points, new double[] {1, 0, 0}, 0, null, null);
  }

  static double[][] yz(double[][] points) {
    double[][] result = new double[4][3];
    for (int i = 0; i < 4; i++) result[i] = new double[] {0, points[i][0], points[i][1]};
    return result;
  }

  static void check(double actual, double expected) {
    if (Math.abs(actual - expected) > 1e-9) throw new AssertionError(actual + " != " + expected);
  }

  public static void main(String[] args) {
    var square = face(yz(new double[][] {{0, 0}, {1, 0}, {1, 1}, {0, 1}}));
    var overlap = face(yz(new double[][] {{.5, 0}, {1.5, 0}, {1.5, 1}, {.5, 1}}));
    var adjacent = face(yz(new double[][] {{1, 0}, {2, 0}, {2, 1}, {1, 1}}));
    var triangle = face(yz(new double[][] {{0, 0}, {1, 0}, {0, 1}, {0, 1}}));
    check(PoseAudit.coplanarArea(square, square), 1);
    check(PoseAudit.coplanarArea(square, overlap), .5);
    check(PoseAudit.coplanarArea(square, adjacent), 0);
    check(PoseAudit.coplanarArea(square, triangle), .5);
    check(PoseAudit.coplanarArea(triangle, square), .5);
    // Actual clipped outfit 22: a tiny adjacent triangle must not cover its neighbour.
    var a =
        face(
            yz(
                new double[][] {
                  {10.023332595825195, -2.299999952316284},
                  {10.142999649047852, -2.299999952316284},
                  {10.142999649047852, -2.2749183177948},
                  {10.023332595825195, -2.2994494438171387}
                }));
    var b =
        face(
            yz(
                new double[][] {
                  {10.020647048950195, -2.299999952316284},
                  {10.023332595825195, -2.299999952316284},
                  {10.023332595825195, -2.2994494438171387},
                  {10.023332595825195, -2.2994494438171387}
                }));
    check(PoseAudit.coplanarArea(a, b), 0);
    check(PoseAudit.coplanarArea(b, a), 0);
    System.out.println(
        "POSE_AUDIT_PASS true-overlap+contained-triangle+shared-edge+small-adjacent-triangle");
  }
}
