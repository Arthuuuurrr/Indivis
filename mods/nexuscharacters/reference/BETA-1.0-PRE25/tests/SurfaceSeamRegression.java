import java.util.*;
import net.tompsen.nexuscharacters.*;
import org.joml.*;

/** Two adjacent strand fronts must cover exactly two square pixels after subtraction. */
public final class SurfaceSeamRegression {
  public static void main(String[] args) {
    int[] strands=new int[4096];strands[40*64+34]=strands[40*64+35]=0xff553311;
    float[][] outer={{-4.3f/16,-.3f/16,-2.3f/16},{4.3f/16,12.3f/16,2.3f/16}};
    var shape=HairVoxelSurface.build(strands,new int[4096],1,outer);
    double area=0;
    for(var face:shape.faces()) {
      if(face.normal().z<.99)continue;
      List<SurfaceGeometry.Polygon> fragments=List.of(face);
      for(var b:shape.boxes()) {
        List<SurfaceGeometry.Polygon> next=new ArrayList<>();
        for(var f:fragments)next.addAll(Boolean.getBoolean("legacySeam")?SurfaceGeometry.subtract(f,new SurfaceGeometry.Box(b.lo(),b.hi(),new Matrix4f(),b.rank(),1)):SurfaceGeometry.subtractConnected(f,new SurfaceGeometry.Box(b.lo(),b.hi(),new Matrix4f(),b.rank(),1)));
        fragments=next;
      }
      for(var f:fragments){var o=f.vertices().get(0);for(int i=1;i+1<f.vertices().size();i++){
        var a=f.vertices().get(i);var b=f.vertices().get(i+1);
        area+=new Vector3f(a.x()-o.x(),a.y()-o.y(),a.z()-o.z()).cross(new Vector3f(b.x()-o.x(),b.y()-o.y(),b.z()-o.z())).length()*128;
      }}
    }
    System.out.println("STRAND_SEAM_AREA "+area);
    if(java.lang.Math.abs(area-2)>0.000001)throw new AssertionError("A crack remains between adjacent strand pixels: "+area);
    System.out.println("STRAND_SEAM_PASS exact shared partition");
  }
}
