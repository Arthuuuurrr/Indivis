import java.util.*;
import net.tompsen.nexuscharacters.*;

/** Independent rear-plane oracle: the delivered hair surface must stand outside native clothes. */
public final class HairClearanceAudit {
  static void run(Object model) throws Exception {
    Object states=SurfaceGeometry.field(PosedSurfaceSupport.class,"STATES");
    Object state=((Map<?,?>)states).get(model);
    Object[] templates=(Object[])SurfaceGeometry.field(state,"template");
    var shape=NativeVoxelSurface.SHAPES.get(templates[1]);
    float min=Float.POSITIVE_INFINITY;int faces=0;
    for(var f:shape.faces()) {
      if(f.rank()<20000||f.rank()>=30000||f.normal().z<.99f)continue;
      float u=0,v=0,z=0;
      for(var p:f.vertices()){u+=p.u();v+=p.v();z+=p.z();}
      u=u/f.vertices().size()*64;v=v/f.vertices().size()*64;
      if(u<32||u>=40||v<36||v>=48)continue;
      min=Math.min(min,z/f.vertices().size()*16-2);faces++;
    }
    if(faces==0)throw new AssertionError("Rear hair surface missing");
    var bounds=NativeVoxelSurface.class.getDeclaredMethod("fullBounds",int.class);
    bounds.setAccessible(true);
    float[][] nativeBounds=(float[][])bounds.invoke(null,1);
    float clothingEnvelope=nativeBounds[1][2]*16-2;
    float required=Math.max(1.15f,clothingEnvelope+.20f);
    System.out.println("HAIR_REAR_DEPTH faces="+faces+" minPixels="+min+" clothingEnvelope="+clothingEnvelope+" required="+required);
    if(min<required-.001f)throw new AssertionError("Rear strands too close to clothing: "+min);
  }
}
