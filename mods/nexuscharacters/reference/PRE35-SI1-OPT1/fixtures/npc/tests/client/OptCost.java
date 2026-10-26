import java.lang.management.ManagementFactory;
import java.nio.file.*;
import java.util.*;
import net.minecraft.*;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.client.renderer.entity.standard.PlayerRenderer;
import fr.indivis.nexusnpc.*;
import net.tompsen.nexuscharacters.*;

/** Paired PRE35/OPT1 workload, genuinely unique poses, real EasyNPCs. */
final class OptCost {
 static final List<String> geometry=new ArrayList<>(List.of("kind,pose,frame,entity,hash"));
 static final List<String> performance=new ArrayList<>(List.of("kind,pose,entities,sample,frames,cpu_ns,wall_ns,allocated_bytes,recomputes"));
 static void pose(class_10055 state,String pose,int frame,int entity) {
  // Age drives vanilla idle arm bobbing. Freeze it in both paired fixtures.
  state.field_53328=0;
  state.field_53446=180;state.field_53447=0;state.field_53448=0;
  state.field_53450=0;state.field_53451=0;
  if(!pose.equals("static")) {
   state.field_53447=(float)java.lang.Math.sin(frame*.071+entity)*55;
   state.field_53448=(float)java.lang.Math.cos(frame*.043+entity)*16;
  }
  if(pose.equals("walking")) {state.field_53450=frame*.17f+entity*.31f;state.field_53451=.6f;}
 }
 static Object frame(class_310 c,class_1314 entity,String pose,int frame,int index) {
  var renderer=(PlayerRenderer)c.method_1561().method_3953(entity);
  var state=(class_10055)renderer.method_62425(entity,0);pose(state,pose,frame,index);
  var model=(class_591)renderer.method_4038();model.method_62110(state);return model;
 }
 static void run(class_310 c)throws Exception {
  List<class_1314> all=new ArrayList<>();
  for(var e:c.field_1687.method_18112())if(e instanceof class_1314 mob&&e instanceof EasyNPC<?> npc&&NpcAppearance.read(npc.getEasyNPCSkinData().getSkinDataEntry())!=null)all.add(mob);
  List<String> order=List.of("haut_elfe_ivoire","elfe_des_bois","nain_bronze","nordique_roux","naine_forge");
  all.sort(Comparator.comparingInt((class_1314 e)->e.method_23321()>0?0:1)
    .thenComparingInt(e->order.indexOf(NpcAppearance.read(((EasyNPC<?>)e).getEasyNPCSkinData().getSkinDataEntry()).preset().id())));
  NpcIntegrationTest.check(all.size()>=8,"Need eight real EasyNPCs");
  var bean=(com.sun.management.ThreadMXBean)ManagementFactory.getThreadMXBean();bean.setThreadAllocatedMemoryEnabled(true);
  NpcIntegrationTest.benchmark=true;
  try {
   for(String pose:List.of("head_turn","walking"))for(int frame=0;frame<18;frame++)for(int e=0;e<3;e++) {
    Object model=frame(c,all.get(e),pose,frame+800,e);
    geometry.add("npc,"+pose+","+frame+","+e+","+RenderPerf.geometry(model));
   }
   for(String pose:List.of("static","head_turn","walking"))for(int count:new int[]{1,3,8}) {
    for(int f=0;f<32;f++)for(int e=0;e<count;e++)frame(c,all.get(e),pose,2000+f,e);
    for(int sample=0;sample<3;sample++) {
     int frames=24;long id=Thread.currentThread().threadId(),bytes=bean.getThreadAllocatedBytes(id),cpu=bean.getCurrentThreadCpuTime(),wall=System.nanoTime(),rebuild=PosedSurfaceSupport.recomputeCount;
     for(int f=0;f<frames;f++)for(int e=0;e<count;e++)frame(c,all.get(e),pose,10000+sample*1000+f,e);
     String row="npc,"+pose+","+count+","+sample+","+frames+","+(bean.getCurrentThreadCpuTime()-cpu)+","+(System.nanoTime()-wall)+","+(bean.getThreadAllocatedBytes(id)-bytes)+","+(PosedSurfaceSupport.recomputeCount-rebuild);
     performance.add(row);System.out.println("OPT_PERF "+row);
    }
   }
   OptPlayerCost.run(c,bean);
  } finally {NpcIntegrationTest.benchmark=false;}
  Files.write(NpcIntegrationTest.OUT.resolve("opt-geometry.csv"),geometry);
  Files.write(NpcIntegrationTest.OUT.resolve("opt-perf.csv"),performance);
  NpcIntegrationTest.results.add("OPT_WORKLOAD_PASS real NPC/player model preparation; static/head_turn/walking; unique poses; no GPU FPS claim");
 }
 static void summary()throws Exception {
  try {
   Class<?> type=Class.forName("net.tompsen.nexuscharacters.SpatialSurfaceCleaner");
   List<String> lines=new ArrayList<>();
   for(String name:List.of("calls","queries","queryHits","fallbacks","oracleCalls","oraclePolygons"))lines.add(name+"="+type.getField(name).getLong(null));
   if(Boolean.getBoolean("nexus.opt1.verify"))NpcIntegrationTest.check(type.getField("oracleCalls").getLong(null)>100,"Exact oracle did not execute");
   Files.write(NpcIntegrationTest.OUT.resolve("opt-counters.txt"),lines);
   Class<?> reuse=Class.forName("net.tompsen.nexuscharacters.PartSurfaceReuse");
   List<String> reused=new ArrayList<>();
   for(String name:List.of("hits","stores","evictions","peakBytes"))reused.add(name+"="+reuse.getField(name).getLong(null));
   Files.write(NpcIntegrationTest.OUT.resolve("opt-reuse.txt"),reused);
  }catch(ClassNotFoundException baseline){}
 }
}
