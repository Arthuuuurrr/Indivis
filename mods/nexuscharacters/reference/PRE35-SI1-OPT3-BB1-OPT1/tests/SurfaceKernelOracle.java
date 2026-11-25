package net.tompsen.nexuscharacters;
import java.util.*;import org.joml.*;
import java.lang.Math;
import java.util.Random;
/** Byte-for-byte primitive geometry oracle against the delivered BB1 methods. */
public final class SurfaceKernelOracle {
 static int checks;
 static void same(List<SurfaceGeometry.Polygon>a,List<SurfaceGeometry.Polygon>b){SpatialSurfaceCleaner.compare(a,b);checks++;}
 static SurfaceGeometry.Polygon polygon(Random r,int n){
  ArrayList<SurfaceGeometry.Vertex> v=new ArrayList<>();float z=r.nextFloat()-.5f;
  for(int i=0;i<n;i++){float angle=(float)(2*Math.PI*i/Math.max(1,n));v.add(new SurfaceGeometry.Vertex((float)Math.cos(angle)*(.1f+r.nextFloat()*.15f),(float)Math.sin(angle)*(.1f+r.nextFloat()*.15f),z,r.nextFloat(),r.nextFloat()));}
  return new SurfaceGeometry.Polygon(v,new Vector3f(0,0,1),17);
 }
 public static void main(String[] args){
  Random r=new Random(187_20261125L);int count=Integer.parseInt(args.length==0?"20000":args[0]);
  for(int i=0;i<count;i++){
   var p=polygon(r,3+r.nextInt(7));Matrix4f boxMatrix=new Matrix4f().translation(r.nextFloat()*.3f-.15f,r.nextFloat()*.3f-.15f,r.nextFloat()*.3f-.15f).rotateXYZ(r.nextFloat()*2-1,r.nextFloat()*2-1,r.nextFloat()*2-1).scale(.4f+r.nextFloat()*1.2f,.4f+r.nextFloat()*1.2f,.4f+r.nextFloat()*1.2f);
   var box=new SurfaceGeometry.Box(new float[]{-.15f,-.15f,-.15f},new float[]{.15f,.15f,.15f},boxMatrix,31,0);
   for(boolean connected:new boolean[]{false,true})same(CompactSurfaceClipper.intersectingBB1(p,box,connected),PackedSurfaceClipper.intersecting(p,box,connected));
   Matrix4f matrix=new Matrix4f().translation(r.nextFloat(),r.nextFloat(),r.nextFloat()).rotateXYZ(r.nextFloat()*3,r.nextFloat()*3,r.nextFloat()*3).scale(.5f+r.nextFloat(),.5f+r.nextFloat(),.5f+r.nextFloat());
   var polygons=List.of(p,polygon(r,4));PackedSurfaceOutput.same(SurfaceGeometry.write(SurfaceGeometry.transform(polygons,matrix)),PackedSurfaceOutput.write(polygons,matrix));checks++;
   var first=MemoSurfaceTransform.transform(polygons,matrix);same(SurfaceGeometry.transform(polygons,matrix),first);same(first,MemoSurfaceTransform.transform(polygons,new Matrix4f(matrix)));
  }
  // Exact shared planes, zero-area shapes and signed-zero coordinates.
  for(float delta:new float[]{0,-0f,-3e-6f,3e-6f,-6e-5f,6e-5f,Math.nextUp(3e-6f),Math.nextDown(3e-6f)}){
   var p=new SurfaceGeometry.Polygon(List.of(new SurfaceGeometry.Vertex(-.2f,-.2f,delta,0,0),new SurfaceGeometry.Vertex(.2f,-.2f,delta,1,0),new SurfaceGeometry.Vertex(.2f,.2f,delta,1,1),new SurfaceGeometry.Vertex(-.2f,.2f,delta,0,1)),new Vector3f(0,0,1),4);
   var box=new SurfaceGeometry.Box(new float[]{-.2f,-.2f,0},new float[]{.2f,.2f,.2f},new Matrix4f(),3,0);for(boolean c:new boolean[]{false,true})same(CompactSurfaceClipper.intersectingBB1(p,box,c),PackedSurfaceClipper.intersecting(p,box,c));
  }
  for(int n=0;n<7;n++){var vs=new ArrayList<SurfaceGeometry.Vertex>();for(int i=0;i<n;i++)vs.add(new SurfaceGeometry.Vertex(i*1e-5f,0,-0f,i,0));var p=new SurfaceGeometry.Polygon(vs,new Vector3f(0,0,1),1);
   for(var m:List.of(new Matrix4f(),new Matrix4f().scale(1.2f,-.9f,.7f),new Matrix4f().scale(0f))){PackedSurfaceOutput.same(SurfaceGeometry.write(SurfaceGeometry.transform(List.of(p),m)),PackedSurfaceOutput.write(List.of(p),m));checks++;}}
  for(int test=0;test<2000;test++){
   var grid=new PosedSurfaceSupport.Grid();
   for(int i=0;i<6;i++){var m=new Matrix4f().translation(r.nextFloat()*.5f-.25f,r.nextFloat()*.5f-.25f,r.nextFloat()*.5f-.25f).rotateXYZ(r.nextFloat(),r.nextFloat(),r.nextFloat());grid.add(new SurfaceGeometry.Box(new float[]{-.1f,-.1f,-.1f},new float[]{.1f,.1f,.1f},m,200+i,i));}
   var index=new SpatialSurfaceCleaner.Index(grid);var source=List.of(polygon(r,4),polygon(r,5),polygon(r,3));
   for(int mode=0;mode<8;mode++){int part=test%6;boolean base=(mode&1)!=0,first=(mode&2)!=0,world=(mode&4)!=0;
    same(SpatialSurfaceCleaner.cleanIndexedBB1(source,grid,index,part,base,first,world),ScratchSurfaceCleaner.clean(source,grid,index,part,base,first,world));}
  }
  // Identical geometry under different mesh identities may alias. UV/rank,
  // signed zero and live shape replacement must not alias incorrectly.
  Object meshA=new Object(),meshB=new Object();var face=polygon(r,4);
  var sa=new NativeVoxelSurface.Shape(List.of(face),List.of(new NativeVoxelSurface.Bounds(new float[]{0,0,0},new float[]{1,1,1},7)));
  var sb=new NativeVoxelSurface.Shape(List.copyOf(sa.faces()),List.copyOf(sa.boxes()));
  NativeVoxelSurface.SHAPES.put(meshA,sa);NativeVoxelSurface.SHAPES.put(meshB,sb);
  Object ka=NativeGeometryKeys.key(meshA);if(ka!=NativeGeometryKeys.key(meshB))throw new AssertionError("Equivalent shapes not canonicalized");checks++;
  var vertices=new ArrayList<>(face.vertices());var v=vertices.get(0);vertices.set(0,new SurfaceGeometry.Vertex(v.x(),v.y(),v.z(),Math.nextUp(v.u()),v.v()));
  var changed=new NativeVoxelSurface.Shape(List.of(new SurfaceGeometry.Polygon(vertices,face.normal(),face.rank())),sa.boxes());NativeVoxelSurface.SHAPES.put(meshB,changed);
  if(ka==NativeGeometryKeys.key(meshB))throw new AssertionError("UV replacement reused stale geometry");checks++;
  NativeVoxelSurface.SHAPES.put(meshB,sb);if(ka!=NativeGeometryKeys.key(meshB))throw new AssertionError("Shape restore failed");checks++;
  var changedBounds=new NativeVoxelSurface.Shape(sa.faces(),List.of(new NativeVoxelSurface.Bounds(new float[]{-0f,0,0},new float[]{1,1,1},7)));NativeVoxelSurface.SHAPES.put(meshB,changedBounds);
  if(ka==NativeGeometryKeys.key(meshB))throw new AssertionError("Raw signed-zero bound ignored");checks++;
  NativeVoxelSurface.clear();if(ka==NativeGeometryKeys.key(meshA))throw new AssertionError("Resource clear retained geometry key");checks++;
  System.out.println("SURFACE_KERNEL_ORACLE_PASS checks="+checks+" random_polygons="+count+" raw_float_bits=true geometry_key_invalidation=true");
 }
}
