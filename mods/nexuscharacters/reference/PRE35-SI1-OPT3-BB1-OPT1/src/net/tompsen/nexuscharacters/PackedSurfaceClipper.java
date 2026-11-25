package net.tompsen.nexuscharacters;
import java.util.*;
import org.joml.*;
import java.lang.Math;
/** Exact six-plane subtraction with primitive scratch for the discarded inside
 * remainder. Only vertices that escape into an output polygon are materialized. */
public final class PackedSurfaceClipper {
 private static final boolean VERIFY=Boolean.getBoolean("nexus.opt1.verify");
 private static final ThreadLocal<Work> WORK=ThreadLocal.withInitial(Work::new);
 public static long calls,oracleCalls;
 private static final class Buffer {
  float[] data=new float[128];SurfaceGeometry.Vertex[] refs=new SurfaceGeometry.Vertex[16];int used;
  void capacity(int n){if(n>refs.length){refs=Arrays.copyOf(refs,Math.max(n,refs.length*2));data=Arrays.copyOf(data,refs.length*8);}used=Math.max(used,n);}
  SurfaceGeometry.Vertex vertex(int i){var v=refs[i];if(v==null){int k=i*8;refs[i]=v=new SurfaceGeometry.Vertex(data[k],data[k+1],data[k+2],data[k+3],data[k+4]);}return v;}
  void clear(){Arrays.fill(refs,0,used,null);used=0;}
 }
 private static final class Work {final Buffer a=new Buffer(),b=new Buffer();final Vector3f projected=new Vector3f(),cross=new Vector3f(),edge=new Vector3f();}
 public static List<SurfaceGeometry.Polygon> intersecting(SurfaceGeometry.Polygon polygon,SurfaceGeometry.Box box,boolean connected){
  Work w=WORK.get();try{List<SurfaceGeometry.Polygon> result=clip(polygon,box,connected,w);calls++;
   if(VERIFY){SpatialSurfaceCleaner.compare(CompactSurfaceClipper.intersectingBB1(polygon,box,connected),result);oracleCalls++;}return result;
  }finally{w.a.clear();w.b.clear();}
 }
 private static List<SurfaceGeometry.Polygon> clip(SurfaceGeometry.Polygon p,SurfaceGeometry.Box box,boolean connected,Work w){
  int count=p.vertices().size();Buffer current=w.a,next=w.b;current.capacity(count);
  for(int i=0;i<count;i++){var v=p.vertices().get(i);int k=i*8;float[] a=current.data;a[k]=v.x();a[k+1]=v.y();a[k+2]=v.z();a[k+3]=v.u();a[k+4]=v.v();project(a,k,box,w);current.refs[i]=v;}
  ArrayList<SurfaceGeometry.Polygon> output=new ArrayList<>();float outside=connected?0f:2.9999999E-6f,inside=connected?0f:-2.9999999E-6f;
  for(int plane=0;plane<6&&count>0;plane++){
   int axis=plane/2,outs=0,ins=0;float[] a=current.data;
   for(int i=0;i<count;i++){float d=distance(a[i*8+5+axis],box,axis,plane);if(d>outside)outs++;if(-d>=inside)ins++;}
   if(ins==count&&outs==0)continue;
   if(outs==count&&ins==0){if(valid(current,count,w)){ArrayList<SurfaceGeometry.Vertex> vs=new ArrayList<>(count);for(int i=0;i<count;i++)vs.add(current.vertex(i));output.add(new SurfaceGeometry.Polygon(vs,p.normal(),p.rank()));}break;}
   ArrayList<SurfaceGeometry.Vertex> escaped=new ArrayList<>(count+2);next.capacity(count*2+2);int written=0;
   for(int i=0;i<count;i++){
    int j=(i+1)%count,k=i*8,l=j*8;float d=distance(a[k+5+axis],box,axis,plane),e=distance(a[l+5+axis],box,axis,plane);
    boolean od=d>outside,oe=e>outside;
    if(od)escaped.add(current.vertex(i));
    if(od!=oe){float t=(d-outside)/(d-e);if(Float.isFinite(t))escaped.add(mix(a,k,l,Math.max(0f,Math.min(1f,t))));}
    float nd=d*-1f,ne=e*-1f;boolean id=nd>=inside,ie=ne>=inside;
    if(id){System.arraycopy(a,k,next.data,written*8,8);next.refs[written]=current.refs[i];written++;}
    if(id!=ie){float t=(nd-inside)/(nd-ne);if(Float.isFinite(t)){t=Math.max(0f,Math.min(1f,t));int target=written*8;for(int c=0;c<5;c++)next.data[target+c]=a[k+c]+t*(a[l+c]-a[k+c]);project(next.data,target,box,w);next.refs[written]=null;written++;}}
   }
   if(SurfaceGeometry.valid(escaped))output.add(new SurfaceGeometry.Polygon(escaped,p.normal(),p.rank()));
   count=written;Buffer swap=current;current=next;next=swap;
  }
  return output;
 }
 private static float distance(float coordinate,SurfaceGeometry.Box box,int axis,int plane){return plane%2==0?box.lo[axis]-coordinate:coordinate-box.hi[axis];}
 private static void project(float[] a,int k,SurfaceGeometry.Box box,Work w){box.inverse.transformPosition(w.projected.set(a[k],a[k+1],a[k+2]));a[k+5]=w.projected.x;a[k+6]=w.projected.y;a[k+7]=w.projected.z;}
 private static SurfaceGeometry.Vertex mix(float[] a,int k,int l,float t){return new SurfaceGeometry.Vertex(a[k]+t*(a[l]-a[k]),a[k+1]+t*(a[l+1]-a[k+1]),a[k+2]+t*(a[l+2]-a[k+2]),a[k+3]+t*(a[l+3]-a[k+3]),a[k+4]+t*(a[l+4]-a[k+4]));}
 private static boolean valid(Buffer b,int n,Work w){if(n<3)return false;float[] v=b.data;float area=0;for(int i=1;i+1<n;i++){int a=i*8,c=(i+1)*8;area+=w.cross.set(v[a]-v[0],v[a+1]-v[1],v[a+2]-v[2]).cross(w.edge.set(v[c]-v[0],v[c+1]-v[1],v[c+2]-v[2])).length();}return area>8.9999996E-10f;}
}
