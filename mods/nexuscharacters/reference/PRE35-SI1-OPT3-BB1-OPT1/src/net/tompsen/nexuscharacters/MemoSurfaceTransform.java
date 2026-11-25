package net.tompsen.nexuscharacters;
import java.util.*;
import org.joml.*;
import java.lang.Math;
/** Only called for PosedSurfaceSupport's immutable original/local surface lists.
 * Not substituted into the public arbitrary-list SurfaceGeometry API. */
public final class MemoSurfaceTransform {
 private static final long LIMIT=12L*1024*1024;
 private static final ThreadLocal<Pool> POOL=ThreadLocal.withInitial(Pool::new);
 private static final boolean VERIFY=Boolean.getBoolean("nexus.opt1.verify");
 public static long hits,misses,evictions,peakBytes,oracleCalls;
 private static final class Pool {final LinkedList<Entry> entries=new LinkedList<>();long bytes;}
 private static final class Entry {
  final List<SurfaceGeometry.Polygon> input,output;final Matrix4f matrix;final int properties;final long bytes;
  Entry(List<SurfaceGeometry.Polygon> in,List<SurfaceGeometry.Polygon> out,Matrix4f m,long bytes){input=in;output=out;matrix=new Matrix4f(m);properties=m.properties();this.bytes=bytes;}
 }
 public static List<SurfaceGeometry.Polygon> transform(List<SurfaceGeometry.Polygon> input,Matrix4f matrix){
  Pool p=POOL.get();for(var i=p.entries.iterator();i.hasNext();){Entry e=i.next();if(e.input==input&&e.properties==matrix.properties()&&e.matrix.equals(matrix)){
   i.remove();p.entries.addLast(e);hits++;if(VERIFY){SpatialSurfaceCleaner.compare(SurfaceGeometry.transform(input,matrix),e.output);oracleCalls++;}return e.output;
  }}
  misses++;List<SurfaceGeometry.Polygon> out=SurfaceGeometry.transform(input,matrix);long bytes=128+PartSurfaceReuse.estimate(input)+PartSurfaceReuse.estimate(out);
  if(bytes<=LIMIT/2){p.entries.addLast(new Entry(input,out,matrix,bytes));p.bytes+=bytes;while(p.bytes>LIMIT||p.entries.size()>128){p.bytes-=p.entries.removeFirst().bytes;evictions++;}peakBytes=Math.max(peakBytes,p.bytes);}
  return out;
 }
 public static void clear(){POOL.remove();}
}
