import java.util.*;
import net.tompsen.nexuscharacters.*;
import net.tompsen.nexuscharacters.SurfaceGeometry.*;
import org.joml.Vector3f;
import org.joml.Matrix4f;

/** Independent dimensions and exposed step-area oracle for a two-pixel clothing profile. */
public class ClothingProfileRegression {
  static void close(float actual,float expected) {
    if(Math.abs(actual-expected)>.001f) throw new AssertionError(actual+" != "+expected);
  }
  static float area(List<Polygon> faces) {
    float result=0;
    for(Polygon f:faces) {
      Vertex o=f.vertices().get(0);
      for(int i=1;i+1<f.vertices().size();i++) {
        Vertex a=f.vertices().get(i),b=f.vertices().get(i+1);
        result+=new Vector3f(a.x()-o.x(),a.y()-o.y(),a.z()-o.z())
          .cross(new Vector3f(b.x()-o.x(),b.y()-o.y(),b.z()-o.z())).length()*128;
      }
    }
    return result;
  }
  static List<Polygon> union(NativeVoxelSurface.Shape shape,int part) {
    float[][] core=NativeVoxelSurface.baseBounds(part);
    List<Box> boxes=new ArrayList<>();boxes.add(new Box(core[0],core[1],new Matrix4f(),0,part));
    for(var b:shape.boxes()) boxes.add(new Box(b.lo(),b.hi(),new Matrix4f(),b.rank(),part));
    List<Polygon> result=new ArrayList<>();
    for(Polygon f:shape.faces()) {
      List<Polygon> fragments=List.of(f);
      for(Box box:boxes) {
        List<Polygon> next=new ArrayList<>();
        for(Polygon p:fragments)next.addAll(SurfaceGeometry.subtract(p,box));
        fragments=next;
      }
      result.addAll(fragments);
    }
    return result;
  }
  public static void main(String[] args) {
    int[] fabric=new int[4096],details=new int[4096];
    fabric[38*64+21]=0xff553311;details[38*64+22]=0xffffcc00;
    float[][] outer={{-4.2f/16,-.2f/16,-2.3f/16},{4.2f/16,12.2f/16,2.3f/16}};
    var shape=ClothingVoxelSurface.build(fabric,details,1,outer);
    if(shape.boxes().size()!=2)throw new AssertionError("Unexpected material outside source mask");
    var a=shape.boxes().get(0);var b=shape.boxes().get(1);
    close(a.lo()[0]*16,-3);close(a.hi()[0]*16,-2);close(b.lo()[0]*16,-2);close(b.hi()[0]*16,-1);
    close(a.lo()[1]*16,2);close(a.hi()[1]*16,3);close(b.lo()[1]*16,2);close(b.hi()[1]*16,3);
    close(a.lo()[2]*16,-2.1f);close(b.lo()[2]*16,-2.3f);
    List<Polygon> surface=union(shape,1);
    List<Polygon> step=surface.stream().filter(f->f.normal().x()<-.99f
      &&f.vertices().stream().allMatch(v->Math.abs(v.x()*16+2)<.001)).toList();
    // Caps: 2; top+bottom: .8; free ends: .4; height step: .2 square pixels.
    close(area(step),.2f);close(area(surface),3.4f);
    for(Polygon p:surface)for(Vertex v:p.vertices()) {
      int texel=(int)(v.v()*64)*64+(int)(v.u()*64);
      if(texel!=38*64+21&&texel!=38*64+22)throw new AssertionError("Side wall samples another pixel");
    }
    // A hole between two raised details must retain the lower fabric height.
    fabric[38*64+22]=0xff553311;details[38*64+22]=0;
    details[38*64+20]=0xffffcc00;details[38*64+23]=0xffffcc00;
    shape=ClothingVoxelSurface.build(fabric,details,1,outer);
    long lower=shape.boxes().stream().filter(bx->Math.abs(bx.lo()[2]*16+2.1)<.001).count();
    if(lower!=2)throw new AssertionError("Detail hole was filled at upper height");
    // The same fabric pixel remains at the same height if an unrelated detail is added.
    details[40*64+25]=0xffffcc00;
    var expanded=ClothingVoxelSurface.build(fabric,details,1,outer);
    for(var bx:expanded.boxes()) if(Math.abs(bx.lo()[0]*16+3)<.001&&Math.abs(bx.lo()[1]*16-2)<.001)
      close(bx.lo()[2]*16,-2.1f);
    // 3-pixel arms retain their authored texel width and asymmetric origin.
    Arrays.fill(fabric,0);Arrays.fill(details,0);fabric[53*64+53]=0xff553311;
    var slim=ClothingVoxelSurface.build(fabric,details,6,new float[][]{{-2,-2,-2},{2,2,2}});
    if(slim.boxes().size()!=1)throw new AssertionError("Slim-arm mask");
    close((slim.boxes().get(0).hi()[0]-slim.boxes().get(0).lo()[0])*16,1);
    System.out.println("CLOTHING_PROFILE_PASS fixed-texel-grid + closed-height-step + detail-hole + stable-fabric-depth + slim-arm + source-pixel-sides");
  }
}

