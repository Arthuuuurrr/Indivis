import java.lang.reflect.*;
import net.minecraft.class_1011;
import net.tompsen.nexuscharacters.*;

/** Independent coverage oracle: opaque body faces must stay opaque under every outfit. */
public final class ArmTextureAudit {
  public static void run(String id) throws Exception {
    Method compose=DynamicAppearanceSupport.class.getDeclaredMethod("compose69",Appearance69Support.Params.class);
    compose.setAccessible(true);
    try(class_1011 image=(class_1011)compose.invoke(null,Appearance69Support.parse(id))) {
      int checked=0;
      for(int[] uv:new int[][]{{40,16},{32,48}}) {
        for(int y=uv[1]+4;y<uv[1]+16;y++) for(int x=uv[0];x<uv[0]+16;x++) {
          if((image.method_61940(x,y)>>>24)!=255)
            throw new AssertionError("ARM_UV_HOLE "+id+" x="+x+" y="+y);
          checked++;
        }
        for(int y=uv[1];y<uv[1]+4;y++) for(int x=uv[0]+4;x<uv[0]+12;x++) {
          if((image.method_61940(x,y)>>>24)!=255)
            throw new AssertionError("ARM_CAP_HOLE "+id+" x="+x+" y="+y);
          checked++;
        }
      }
      System.out.println("ARM_TEXTURE_COVERAGE_PASS "+id+" pixels="+checked);
    }
  }
}
