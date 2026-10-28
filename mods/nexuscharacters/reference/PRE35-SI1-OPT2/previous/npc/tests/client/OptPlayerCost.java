import java.util.*;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;
import fr.indivis.nexusnpc.*;
import com.mojang.authlib.GameProfile;

/** Actual OtherClientPlayer entities with native Nexus appearance payloads. */
final class OptPlayerCost {
 @SuppressWarnings("unchecked") static List<class_745> players(class_310 c)throws Exception {
  var appearances=(Map<UUID,CharacterAppearancePayload>)SurfaceGeometry.field(CharacterAppearanceClient.class,"ACTIVE");
  var cosmetics=(Map<UUID,CharacterCosmeticPayload>)SurfaceGeometry.field(CharacterCosmeticsClient.class,"ACTIVE");
  List<class_745> out=new ArrayList<>();
  var profiles=List.of("haut_elfe_ivoire","elfe_des_bois","nain_bronze","nordique_roux","naine_forge");
  for(int i=0;i<8;i++) {
   var p=PresetStore.load(profiles.get(i%profiles.size()));
   UUID id=UUID.nameUUIDFromBytes(("OPT1_player_"+i).getBytes(java.nio.charset.StandardCharsets.UTF_8));
   var dto=new CharacterDto(id,"OPT1_Player_"+i,null,null,"__capitale_preset__:"+p.skin(),0,false,p.race(),p.height(),p.build(),p.ears(),p.beard());
   appearances.put(id,CharacterAppearancePayload.active(id,dto));cosmetics.put(id,CharacterCosmeticPayload.active(id,dto));PreviewDummyPlayerManager.skin(dto);
   var player=new class_745(c.field_1687,new GameProfile(id,"OPT1_Player_"+i));player.method_5838(12000+i);
   player.method_5780("nexuscharacters.skin."+p.skin());player.method_5780("nexuscharacters.build."+java.lang.Math.round(p.build()*100));
   player.method_5814(i*1.5-5,100,7);c.field_1687.method_53875(player);out.add(player);
  }
  return out;
 }
 static Object frame(class_310 c,class_745 player,String pose,int frame,int index) {
  var renderer=c.method_1561().method_74405(player);var state=(class_10055)renderer.method_62425(player,0);
  OptCost.pose(state,pose,frame,index);var model=(class_591)renderer.method_4038();model.method_62110(state);return model;
 }
 static void run(class_310 c,com.sun.management.ThreadMXBean bean)throws Exception {
  var players=players(c);
  try {
   for(String pose:List.of("head_turn","walking"))for(int f=0;f<18;f++)for(int e=0;e<3;e++) {
    var model=frame(c,players.get(e),pose,800+f,e);
    NpcIntegrationTest.check(RenderPerf.state(model)!=null,"Native player surface missing");
    OptCost.geometry.add("player,"+pose+","+f+","+e+","+RenderPerf.geometry(model));
   }
   for(int f=0;f<320;f++)for(int e=0;e<3;e++)frame(c,players.get(e),f%2==0?"head_turn":"walking",30000+f,e);
   for(String pose:List.of("static","head_turn","walking"))for(int count:new int[]{1,3,8}) {
    for(int f=0;f<(pose.equals("static")?1500:64);f++)for(int e=0;e<count;e++)frame(c,players.get(e),pose,2000+f,e);
    for(int sample=0;sample<5;sample++) {
     int frames=24;long id=Thread.currentThread().threadId(),bytes=bean.getThreadAllocatedBytes(id),cpu=bean.getCurrentThreadCpuTime(),wall=System.nanoTime(),rebuild=PosedSurfaceSupport.recomputeCount;
     for(int f=0;f<frames;f++)for(int e=0;e<count;e++)frame(c,players.get(e),pose,10000+sample*1000+f,e);
     String row="player,"+pose+","+count+","+sample+","+frames+","+(bean.getCurrentThreadCpuTime()-cpu)+","+(System.nanoTime()-wall)+","+(bean.getThreadAllocatedBytes(id)-bytes)+","+(PosedSurfaceSupport.recomputeCount-rebuild);
     OptCost.performance.add(row);System.out.println("OPT_PERF "+row);
    }
   }
  } finally {for(var p:players)c.field_1687.method_2945(p.method_5628(),class_1297.class_5529.field_26998);}
 }
}

