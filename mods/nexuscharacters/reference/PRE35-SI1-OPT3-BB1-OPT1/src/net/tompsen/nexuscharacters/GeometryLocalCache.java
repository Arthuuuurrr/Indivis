package net.tompsen.nexuscharacters;
import java.util.*;import org.joml.Matrix4f;
/** Reuse unchanged local geometry across recolored meshes on the SAME player
 * model. Cube replacement clears this pool through clearPRE30. */
public final class GeometryLocalCache {
 private static final long LIMIT=8L*1024*1024;
 private static final Map<Object,Pool> POOLS=Collections.synchronizedMap(new WeakHashMap<>());
 public static long hits,stores,evictions,peakBytes;
 private static final class Pool{final LinkedList<Entry> entries=new LinkedList<>();long bytes;}
 private record Entry(Object key,int part,PosedSurfaceSupport.Prepared prepared,long bytes){}
 private GeometryLocalCache(){}
 public static PosedSurfaceSupport.Prepared lookup(Object model,Object template,Matrix4f relative,int part){
  Pool pool=POOLS.get(model);Object key=NativeGeometryKeys.key(template);
  if(pool!=null)for(var it=pool.entries.iterator();it.hasNext();){var e=it.next();if(e.key==key&&e.part==part&&e.prepared.relative().equals(relative)&&e.prepared.relative().properties()==relative.properties()){it.remove();pool.entries.addLast(e);hits++;if(ReflectionAccess.DIAGNOSTICS)PreparedSurfaceCache.localHits++;return e.prepared;}}
  return PreparedSurfaceCache.lookup(model,template,relative,part);
 }
 public static Object store(Map<Object,PosedSurfaceSupport.Prepared> original,Object template,Object value,Object model,int part){
  var prepared=(PosedSurfaceSupport.Prepared)value;long size=256+PartSurfaceReuse.estimate(prepared.faces());
  if(size<=LIMIT/2){Pool pool=POOLS.computeIfAbsent(model,k->new Pool());pool.entries.addLast(new Entry(NativeGeometryKeys.key(template),part,prepared,size));pool.bytes+=size;stores++;
   while(pool.entries.size()>256||pool.bytes>LIMIT){pool.bytes-=pool.entries.removeFirst().bytes;evictions++;}peakBytes=Math.max(peakBytes,pool.bytes);}
  return PreparedSurfaceCache.store(original,template,value,model,part);
 }
 public static void clear(Object model){POOLS.remove(model);}
 public static void clear(){POOLS.clear();}
}
