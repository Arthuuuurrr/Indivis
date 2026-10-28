package net.tompsen.nexuscharacters;
import java.util.*;
import org.joml.*;
import net.tompsen.nexuscharacters.SurfaceGeometry.*;

/** Same JOML operations and order as SurfaceGeometry.transformOPT1. Only the
 * ArrayList capacities change. Vertex.transform and normal calculations stay
 * untouched; all returned objects are still separately owned.
 */
public final class ExactTransformSupport {
    static final boolean VERIFY=Boolean.getBoolean("nexus.opt1.verify");
    public static long oracleCalls,oraclePolygons;
    private ExactTransformSupport() {}
    public static List<Polygon> transform(List<Polygon> input,Matrix4f matrix) {
        ArrayList<Polygon> output=new ArrayList<>(input.size());
        Matrix3f normals=new Matrix3f(matrix).invert().transpose();
        for(Polygon polygon:input) {
            ArrayList<Vertex> vertices=new ArrayList<>(polygon.vertices().size());
            for(Vertex vertex:polygon.vertices()) {
                vertices.add(vertex.transform(matrix));
            }
            output.add(new Polygon(vertices,normals.transform(new Vector3f(polygon.normal())).normalize(),polygon.rank()));
        }
        if(VERIFY) {
            compare(SurfaceGeometry.transformOPT1(input,matrix),output);oracleCalls++;
        }
        return output;
    }
    static void compare(List<Polygon> a,List<Polygon> b) {
        if(a.size()!=b.size())throw new AssertionError("OPT2 transform count differs");
        for(int i=0;i<a.size();i++) {
            Polygon x=a.get(i),y=b.get(i);oraclePolygons++;
            if(x.rank()!=y.rank()||x.vertices().size()!=y.vertices().size())throw new AssertionError("OPT2 transform topology differs");
            SpatialSurfaceCleaner.value(x.normal().x,y.normal().x);SpatialSurfaceCleaner.value(x.normal().y,y.normal().y);SpatialSurfaceCleaner.value(x.normal().z,y.normal().z);
            for(int j=0;j<x.vertices().size();j++) {
                Vertex p=x.vertices().get(j),q=y.vertices().get(j);
                SpatialSurfaceCleaner.value(p.x(),q.x());SpatialSurfaceCleaner.value(p.y(),q.y());SpatialSurfaceCleaner.value(p.z(),q.z());
                SpatialSurfaceCleaner.value(p.u(),q.u());SpatialSurfaceCleaner.value(p.v(),q.v());
            }
        }
    }
}
