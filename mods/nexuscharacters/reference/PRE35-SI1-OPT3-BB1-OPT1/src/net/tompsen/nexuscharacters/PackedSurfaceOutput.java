package net.tompsen.nexuscharacters;
import java.util.*;
import org.joml.*;
import java.lang.Math;
/** Fuse the existing inverse transform and primitive writer. Same float operations,
 * normals, winding and UVs; no intermediate world->local Vertex/Polygon graph. */
public final class PackedSurfaceOutput {
 private static final boolean VERIFY=Boolean.getBoolean("nexus.opt1.verify");
 private static final ThreadLocal<Work> WORK=ThreadLocal.withInitial(Work::new);
 public static long calls,oracleCalls;
 private static final class Work {
  float[] vertices=new float[80],output=new float[4096];
  final Vector3f position=new Vector3f(),normal=new Vector3f(),a=new Vector3f(),b=new Vector3f();
  final Matrix3f normals=new Matrix3f();
 }
 public static float[] write(List<SurfaceGeometry.Polygon> polygons,Matrix4f matrix){
  Work w=WORK.get();w.normals.set(matrix).invert().transpose();int max=0;
  for(var p:polygons){int n=p.vertices().size();max=Math.addExact(max,Math.multiplyExact(n==4?1:Math.max(0,n-2),23));}
  float[] out=max<=w.output.length?w.output:new float[max];if(max>w.output.length&&max<=262144)w.output=out;
  int at=0;
  for(var p:polygons){
   List<SurfaceGeometry.Vertex> vs=p.vertices();int n=vs.size();
   if(n*5>w.vertices.length)w.vertices=new float[n*5];float[] v=w.vertices;
   for(int i=0;i<n;i++){var q=vs.get(i);matrix.transformPosition(w.position.set(q.x(),q.y(),q.z()));int k=i*5;v[k]=w.position.x;v[k+1]=w.position.y;v[k+2]=w.position.z;v[k+3]=q.u();v[k+4]=q.v();}
   w.normals.transform(w.normal.set(p.normal())).normalize();
   if(!valid(v,n,w))continue;
   if(n==4)at=quad(out,at,v,0,1,2,3,w.normal);
   else for(int i=1;i+1<n;i++)at=quad(out,at,v,0,i,i+1,i+1,w.normal);
  }
  float[] result=Arrays.copyOf(out,at);calls++;
  if(VERIFY){float[] reference=SurfaceGeometry.write(SurfaceGeometry.transform(polygons,matrix));same(reference,result);oracleCalls++;}
  return result;
 }
 private static boolean valid(float[] v,int n,Work w){
  if(n<3)return false;float area=0;
  for(int i=1;i+1<n;i++){int a=i*5,b=(i+1)*5;area+=w.a.set(v[a]-v[0],v[a+1]-v[1],v[a+2]-v[2]).cross(w.b.set(v[b]-v[0],v[b+1]-v[1],v[b+2]-v[2])).length();}
  return area>8.9999996E-10f;
 }
 private static int quad(float[] out,int at,float[] v,int a,int b,int c,int d,Vector3f normal){
  out[at++]=normal.x;out[at++]=normal.y;out[at++]=normal.z;
  System.arraycopy(v,a*5,out,at,5);at+=5;System.arraycopy(v,b*5,out,at,5);at+=5;System.arraycopy(v,c*5,out,at,5);at+=5;System.arraycopy(v,d*5,out,at,5);return at+5;
 }
 static void same(float[] a,float[] b){if(a.length!=b.length)throw new AssertionError("Packed output length differs "+a.length+"/"+b.length);for(int i=0;i<a.length;i++)if(Float.floatToRawIntBits(a[i])!=Float.floatToRawIntBits(b[i]))throw new AssertionError("Packed output float differs at "+i+": "+a[i]+"/"+b[i]);}
}
