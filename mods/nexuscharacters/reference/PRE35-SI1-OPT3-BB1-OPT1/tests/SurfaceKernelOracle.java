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
  System.out.println("SURFACE_KERNEL_ORACLE_PASS checks="+checks+" random_polygons="+count+" raw_float_bits=true");
 }
}
