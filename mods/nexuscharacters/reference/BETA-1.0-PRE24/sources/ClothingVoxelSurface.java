package net.tompsen.nexuscharacters;

import java.util.*;
import org.joml.Vector3f;

/** Connected texel columns. Only their normal depth changes; the authored UV grid stays fixed. */
public final class ClothingVoxelSurface {
  private static final int[][] AXES = {{1,-1},{1,1},{2,-1},{2,1},{0,-1},{0,1}};
  private static final float FABRIC_DEPTH = .10f, EMBED = .02f;

  public static NativeVoxelSurface.Shape build(int[] fabric, int[] details, int part,
      float[][] nativeBounds) {
    int[] spec = NativeVoxelSurface.SPECS[part];
    int[] size = {spec[0],spec[1],spec[2]};
    float[][] base = NativeVoxelSurface.baseBounds(part);
    List<SurfaceGeometry.Polygon> faces = new ArrayList<>();
    List<NativeVoxelSurface.Bounds> boxes = new ArrayList<>();
    int ordinal = 0;
    for (int face=0;face<6;face++) {
      int axis=AXES[face][0], sign=AXES[face][1];
      int width=axis==0?size[2]:size[0], height=axis==1?size[2]:size[1];
      for (int u=0;u<width;u++) for (int v=0;v<height;v++) {
        int pixel=uv(spec,face,u,v);
        boolean authored=(details[pixel]>>>24)!=0;
        if (!authored&&(fabric[pixel]>>>24)==0) continue;
        float depth=depth(authored,axis,sign,base,nativeBounds);
        int[] xyz=position(size,face,u,v);
        float[] lo=new float[3],hi=new float[3];
        for (int a=0;a<3;a++) {
          lo[a]=base[0][a]+xyz[a]/16f;
          hi[a]=base[0][a]+(xyz[a]+1)/16f;
          if (a==axis) {
            float plane=base[sign<0?0:1][a];
            lo[a]=sign<0?plane-depth:plane-EMBED/16f;
            hi[a]=sign<0?plane+EMBED/16f:plane+depth;
          } else {
            // Border columns meet their neighbour around the cube corner.
            // Internal texel boundaries are never stretched or translated.
            if (xyz[a]==0) lo[a]-=neighbourDepth(fabric,details,spec,size,xyz,a,-1,base,nativeBounds,depth);
            if (xyz[a]==size[a]-1) hi[a]+=neighbourDepth(fabric,details,spec,size,xyz,a,1,base,nativeBounds,depth);
          }
        }
        int rank=10000+(authored?5000:0)+ordinal++;
        boxes.add(new NativeVoxelSurface.Bounds(lo,hi,rank));
        faces.addAll(boxFaces(lo,hi,(pixel%64+.5f)/64f,(pixel/64+.5f)/64f,rank));
      }
    }
    return new NativeVoxelSurface.Shape(List.copyOf(faces),List.copyOf(boxes));
  }

  private static float depth(boolean authored,int axis,int sign,float[][] base,float[][] outer) {
    float nativeDepth=sign<0?base[0][axis]-outer[0][axis]:outer[1][axis]-base[1][axis];
    return authored?Math.max(.25f/16f,nativeDepth):FABRIC_DEPTH/16f;
  }

  private static float neighbourDepth(int[] fabric,int[] details,int[] spec,int[] size,int[] xyz,
      int axis,int sign,float[][] base,float[][] outer,float fallback) {
    int face=axis==0?(sign<0?4:5):axis==1?(sign<0?0:1):(sign<0?2:3);
    int u=axis==0?(sign<0?size[2]-1-xyz[2]:xyz[2]):axis==2&&sign>0?size[0]-1-xyz[0]:xyz[0];
    int v=axis==1?size[2]-1-xyz[2]:xyz[1];
    int pixel=uv(spec,face,u,v);
    if ((details[pixel]>>>24)!=0) return depth(true,axis,sign,base,outer);
    if ((fabric[pixel]>>>24)!=0) return depth(false,axis,sign,base,outer);
    return fallback;
  }

  private static int uv(int[] s,int face,int u,int v) {
    int w=s[0],d=s[2],x=s[3],y=s[4];
    return switch(face) {
      case 0 -> (y+v)*64+x+d+u;
      case 1 -> (y+v)*64+x+d+w+u;
      case 2 -> (y+d+v)*64+x+d+u;
      case 3 -> (y+d+v)*64+x+d+w+d+u;
      case 4 -> (y+d+v)*64+x+u;
      default -> (y+d+v)*64+x+d+w+u;
    };
  }

  private static int[] position(int[] s,int face,int u,int v) {
    return switch(face) {
      case 0 -> new int[]{u,0,s[2]-1-v};
      case 1 -> new int[]{u,s[1]-1,s[2]-1-v};
      case 2 -> new int[]{u,v,0};
      case 3 -> new int[]{s[0]-1-u,v,s[2]-1};
      case 4 -> new int[]{0,v,s[2]-1-u};
      default -> new int[]{s[0]-1,v,u};
    };
  }

  private static List<SurfaceGeometry.Polygon> boxFaces(float[] lo,float[] hi,float u,float v,int rank) {
    float x=lo[0],y=lo[1],z=lo[2],X=hi[0],Y=hi[1],Z=hi[2];
    float[][][] corners={{{X,y,Z},{x,y,Z},{x,y,z},{X,y,z}},
      {{X,Y,z},{x,Y,z},{x,Y,Z},{X,Y,Z}},{{X,y,z},{x,y,z},{x,Y,z},{X,Y,z}},
      {{x,y,Z},{X,y,Z},{X,Y,Z},{x,Y,Z}},{{x,y,z},{x,y,Z},{x,Y,Z},{x,Y,z}},
      {{X,y,Z},{X,y,z},{X,Y,z},{X,Y,Z}}};
    List<SurfaceGeometry.Polygon> result=new ArrayList<>(6);
    for(int f=0;f<6;f++) {
      List<SurfaceGeometry.Vertex> vs=new ArrayList<>(4);
      for(float[] p:corners[f]) vs.add(new SurfaceGeometry.Vertex(p[0],p[1],p[2],u,v));
      Vector3f normal=new Vector3f().setComponent(AXES[f][0],AXES[f][1]);
      result.add(new SurfaceGeometry.Polygon(List.copyOf(vs),normal,rank));
    }
    return result;
  }
}
