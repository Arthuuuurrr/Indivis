import java.util.*;
import net.tompsen.nexuscharacters.*;

public final class HairProfileRegression {
  static void close(float value,float expected){if(Math.abs(value-expected)>.001f)throw new AssertionError(value+" != "+expected);}
  public static void main(String[] args) {
    int[] strands=new int[4096],details=new int[4096];
    strands[40*64+34]=0xff553311;details[40*64+35]=0xff663322;
    float[][] envelope={{-4.4f/16,-.4f/16,-2.4f/16},{4.4f/16,12.4f/16,2.4f/16}};
    var shape=HairVoxelSurface.build(strands,details,1,envelope);
    if(shape.boxes().size()!=2)throw new AssertionError("Missing strand or filled mask hole");
    var a=shape.boxes().get(0);var b=shape.boxes().get(1);
    close(a.hi()[2]*16-2,1.15f);close(b.hi()[2]*16-2,1.35f);
    for(var box:shape.boxes()){close((box.hi()[0]-box.lo()[0])*16,1);close((box.hi()[1]-box.lo()[1])*16,1);}
    // Changing Skin Layers' envelope must preserve physical separation.
    envelope[1][2]=3.4f/16;
    shape=HairVoxelSurface.build(strands,details,1,envelope);
    close(shape.boxes().get(0).hi()[2]*16-2,1.60f);
    close(shape.boxes().get(1).hi()[2]*16-2,1.80f);
    // The short-hair/head path gets thickness without a torso-back-specific offset.
    Arrays.fill(strands,0);Arrays.fill(details,0);strands[10*64+45]=0xff553311;
    envelope=new float[][]{{-4.3f/16,-8.3f/16,-4.3f/16},{4.3f/16,.3f/16,4.3f/16}};
    shape=HairVoxelSurface.build(strands,details,0,envelope);
    if(shape.boxes().size()!=1)throw new AssertionError("Head mask changed");
    close(-shape.boxes().get(0).lo()[2]*16-4,.65f);
    System.out.println("HAIR_PROFILE_PASS rear=1.15 detail=1.35 normalGrid=1x1 adaptiveClearance=true head=.65");
  }
}
