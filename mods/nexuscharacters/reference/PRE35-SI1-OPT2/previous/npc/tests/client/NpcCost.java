import java.lang.management.ManagementFactory;
import java.nio.file.*;
import java.util.*;
import net.minecraft.*;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.client.renderer.entity.standard.PlayerRenderer;
import fr.indivis.nexusnpc.*;
import fr.indivis.nexusnpc.client.*;
import net.tompsen.nexuscharacters.*;

/** Actual EasyNPC CPU preparation, excluding GPU, world and cosmetic submission. */
final class NpcCost {
 static void frame(class_310 client,List<class_1314> entities,int count,int frame,boolean walking){
  for(int i=0;i<count;i++){
   var entity=entities.get(i);var renderer=(PlayerRenderer)client.method_1561().method_3953(entity);
   var state=(class_10055)renderer.method_62425(entity,0);
   if(walking){state.field_53450=frame*.17f+i*.31f;state.field_53451=.6f;}
   ((class_591)renderer.method_4038()).method_62110(state);
  }
 }
 static void run(class_310 client)throws Exception{
  List<class_1314> all=new ArrayList<>();
  for(var e:client.field_1687.method_18112())if(e instanceof class_1314 mob&&e instanceof EasyNPC<?> npc&&NpcAppearance.read(npc.getEasyNPCSkinData().getSkinDataEntry())!=null)all.add(mob);
  all.sort(Comparator.comparing(e->e.method_5667().toString()));NpcIntegrationTest.check(all.size()>=8,"Not enough real client NPCs for cost check");
  var bean=(com.sun.management.ThreadMXBean)ManagementFactory.getThreadMXBean();bean.setThreadAllocatedMemoryEnabled(true);
  List<String> output=new ArrayList<>(List.of("pose,npcs,sample,frames,cpu_ns,wall_ns,allocated_bytes"));NpcIntegrationTest.benchmark=true;
  try{
   for(boolean walking:new boolean[]{false,true})for(int count:new int[]{1,4,8}){
    for(int i=0;i<8;i++)frame(client,all,count,i,walking);
    for(int sample=0;sample<3;sample++){
     long thread=Thread.currentThread().threadId(),bytes=bean.getThreadAllocatedBytes(thread),cpu=bean.getCurrentThreadCpuTime(),wall=System.nanoTime();int frames=16;
     for(int i=0;i<frames;i++)frame(client,all,count,i+sample*frames+8,walking);
     output.add((walking?"walking":"static")+","+count+","+sample+","+frames+","+(bean.getCurrentThreadCpuTime()-cpu)+","+(System.nanoTime()-wall)+","+(bean.getThreadAllocatedBytes(thread)-bytes));
    }
   }
  }finally{NpcIntegrationTest.benchmark=false;}
  Files.write(NpcIntegrationTest.OUT.resolve("npc-cpu.csv"),output);
  NpcIntegrationTest.results.add("NPC_CPU_MEASURED real entities=8; 1/4/8 static+walking; excludes GPU, cosmetic submission and world rendering");
 }
 static void cacheIsolation(class_310 client)throws Exception{
  class_1314 profiled=null;for(var e:client.field_1687.method_18112())if(e instanceof class_1314 mob&&e instanceof EasyNPC<?> npc&&NpcAppearance.read(npc.getEasyNPCSkinData().getSkinDataEntry())!=null){profiled=mob;break;}
  NpcIntegrationTest.check(profiled!=null,"No profile for isolation check");
  var normal=(class_1314)profiled.method_5864().method_5883(client.field_1687,class_3730.field_16460);
  var renderer=(PlayerRenderer)client.method_1561().method_3953(profiled);var state=new class_10055();
  renderer.extractRenderState(profiled,state,0);NpcIntegrationTest.check(((NpcRenderState)state).indivis$hasNexusPreset(),"Profile missing before reuse");
  NpcIntegrationTest.check(client.method_1561().method_68832(state)==renderer,"NPC wrongly dispatched to player renderer");
  renderer.extractRenderState(normal,state,0);
  NpcIntegrationTest.check(!((NpcRenderState)state).indivis$hasNexusPreset(),"Profile leaked onto ordinary NPC");
  NpcIntegrationTest.check(((CosmeticRenderStateAccess)state).nexuscharacters$getEarStyle().equals("none")&&((CosmeticRenderStateAccess)state).nexuscharacters$getBeardStyle().equals("none"),"Cosmetics leaked onto ordinary NPC");
  NpcIntegrationTest.check(!state.field_53520.comp_1626().comp_3627().toString().startsWith("nexuscharacters:dynamic/appearance/"),"Texture leaked onto ordinary NPC");
  NpcIntegrationTest.results.add("STATE_REUSE_PASS Nexus profile -> ordinary NPC; flags, texture, ears and beard reset");
 }
}


