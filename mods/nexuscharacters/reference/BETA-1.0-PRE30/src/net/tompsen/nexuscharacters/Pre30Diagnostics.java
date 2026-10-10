package net.tompsen.nexuscharacters;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
/** Opt-in counters only. No automatic output, and no retained entity/model references. */
public final class Pre30Diagnostics {
 static final AtomicLong playerCalls=new AtomicLong(),previewCalls=new AtomicLong(),firstPersonCalls=new AtomicLong();
 static void player(){if(ReflectionAccess.DIAGNOSTICS)playerCalls.incrementAndGet();}
 static void preview(){if(ReflectionAccess.DIAGNOSTICS)previewCalls.incrementAndGet();}
 static void firstPerson(){if(ReflectionAccess.DIAGNOSTICS)firstPersonCalls.incrementAndGet();}
 public static Map<String,Long> snapshot(){
  Map<String,Long> result=new LinkedHashMap<>();
  result.put("enabled",ReflectionAccess.DIAGNOSTICS?1L:0L);
  result.put("methodHits",ReflectionAccess.methodHits.get());result.put("methodMisses",ReflectionAccess.methodMisses.get());result.put("methodResolutionScans",ReflectionAccess.methodScans.get());
  result.put("fieldHits",ReflectionAccess.fieldHits.get());result.put("fieldMisses",ReflectionAccess.fieldMisses.get());result.put("fieldHierarchyScans",ReflectionAccess.fieldScans.get());
  result.put("playerCalls",playerCalls.get());result.put("previewCalls",previewCalls.get());result.put("firstPersonCalls",firstPersonCalls.get());
  result.put("applyCount",PoseScratch.applyCount.get());result.put("applyNanos",PoseScratch.applyNanos.get());result.put("applyMaxNanos",PoseScratch.applyMaxNanos.get());
  result.put("recomputeCount",PosedSurfaceSupport.recomputeCount);result.put("recomputeNanos",PosedSurfaceSupport.recomputeNanos);result.put("recomputeMaxNanos",PosedSurfaceSupport.recomputeMaxNanos);
  result.put("activeModelStates",(long)PosedSurfaceSupport.STATES.size());return Collections.unmodifiableMap(result);
 }
 private Pre30Diagnostics(){}
}
