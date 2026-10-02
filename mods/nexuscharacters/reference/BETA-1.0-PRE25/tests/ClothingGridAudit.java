import java.util.*;
import net.tompsen.nexuscharacters.*;

/** Check submitted clothing against the original torso UV grid, in model pixels. */
public final class ClothingGridAudit {
  public record Result(int texels,double maxDisplacement) {}
  public static Result run(Object mesh) {
    NativeVoxelSurface.Shape shape=NativeVoxelSurface.SHAPES.get(mesh);
    int texels=0;double displacement=0;
    if(shape==null)throw new AssertionError("No torso surface");
    for(var face:shape.faces()) {
      if(face.normal().z()>-.99f)continue;
      double u=0,v=0;float x0=Float.POSITIVE_INFINITY,x1=-x0,y0=x0,y1=-x0;
      for(var p:face.vertices()) {
        u+=p.u()*64;v+=p.v()*64;x0=Math.min(x0,p.x()*16);x1=Math.max(x1,p.x()*16);y0=Math.min(y0,p.y()*16);y1=Math.max(y1,p.y()*16);
      }
      int tx=(int)Math.floor(u/face.vertices().size()),ty=(int)Math.floor(v/face.vertices().size());
      // Interior front-face pixels avoid corner padding and other cube-face UVs.
      if(tx<=20||tx>=27||ty<=36||ty>=47)continue;
      double expectedX=tx-24,expectedY=ty-36;
      displacement=Math.max(displacement,Math.max(Math.max(Math.abs(x0-expectedX),Math.abs(x1-expectedX-1)),Math.max(Math.abs(y0-expectedY),Math.abs(y1-expectedY-1))));
      texels++;
    }
    if(texels==0)throw new AssertionError("Empty clothing UV check");
    return new Result(texels,displacement);
  }

  public static Result hand(Object mesh,int part) {
    NativeVoxelSurface.Shape shape=NativeVoxelSurface.SHAPES.get(mesh);
    boolean left=part==4||part==6;
    int[] s={part>=6?3:4,12,4,left?48:40,left?48:32};float[][] base=NativeVoxelSurface.baseBounds(part);
    int texels=0;double displacement=0;
    if(shape==null)throw new AssertionError("No first-person template");
    for(var face:shape.faces()) {
      if(face.rank()>=20000||face.normal().z()>-.99f)continue;
      double u=0,v=0;float x0=Float.POSITIVE_INFINITY,x1=-x0,y0=x0,y1=-x0,z=0;
      for(var p:face.vertices()) {
        u+=p.u()*64;v+=p.v()*64;x0=Math.min(x0,p.x()*16);x1=Math.max(x1,p.x()*16);y0=Math.min(y0,p.y()*16);y1=Math.max(y1,p.y()*16);z=p.z()*16;
      }
      int tx=(int)Math.floor(u/face.vertices().size()),ty=(int)Math.floor(v/face.vertices().size());
      int offsetX=s[3]+s[2],offsetY=s[4]+s[2];
      if(tx<=offsetX||tx>=offsetX+s[0]-1||ty<=offsetY||ty>=offsetY+s[1]-1)continue;
      double expectedX=base[0][0]*16+tx-offsetX,expectedY=base[0][1]*16+ty-offsetY;
      displacement=Math.max(displacement,Math.max(Math.max(Math.abs(x0-expectedX),Math.abs(x1-expectedX-1)),Math.max(Math.abs(y0-expectedY),Math.abs(y1-expectedY-1))));
      if(face.rank()<15000&&Math.abs(base[0][2]*16-z-.1)>.001)throw new AssertionError("First-person fabric depth collapsed: "+z);
      texels++;
    }
    return new Result(texels,displacement);
  }
}

