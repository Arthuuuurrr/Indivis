import java.util.*;
import net.minecraft.*;
import net.minecraft.server.MinecraftServer;
import net.tompsen.nexuscharacters.*;
import fr.indivis.nexusnpc.*;
/** New authored geometry through the unchanged EasyNPC bridge, not a mock. */
final class BlockbenchNpcRegression {
 static final Map<String,Long> before=new HashMap<>();
 static final String[][] CASES={
  {"human","style_noble","none","moustache_imperiale#brown"},
  {"nordic","tresses_viking_2","none","barbe_viking#red"},
  {"dwarf","chignon_guerrier","none","barbe_runique#black"},
  {"wood_elf","style_elfique","sylvestre","none"},
  {"high_elf","elfe_noble","bijou_dore","none"}};
 static String style(String s){return s.equals("none")?s:"bb_"+s;}
 static void editor(class_437 screen){
  for(var row:CASES)for(int i=1;i<4;i++)if(!row[i].equals("none")){String id=BlockbenchPresetCatalog.base(style(row[i]));before.put(id,BlockbenchRenderSupport.renderCount(id));}
  IndivisMenus.set(screen,"nexuscharacters$hair",BlockbenchPresetCatalog.entry("coiffures","bb_court_classique").hairId());
  IndivisMenus.set(screen,"nexuscharacters$beardStyle","bb_moustache_imperiale#brown");
  IndivisMenus.invoke(screen,"nexuscharacters$changed");
 }
 static void exported(NpcPreset p){
  NpcIntegrationTest.check(BlockbenchPresetCatalog.params(p.skin()).hair()==BlockbenchPresetCatalog.entry("coiffures","bb_court_classique").hairId(),"Editor lost BB hairstyle");
  NpcIntegrationTest.check(p.beard().equals("bb_moustache_imperiale#brown"),"Editor lost BB beard colour");
 }
 static void spawn(MinecraftServer server)throws Exception {
  int index=0;
  for(var row:CASES){
   CharacterRace r=CharacterRace.fromId(row[0]);int hair=BlockbenchPresetCatalog.entry("coiffures",style(row[1])).hairId();
   String recipe="player_v69_b"+(index%2+1)+"_e1_ec0_ey2_h"+hair+"_hc02_s04_o01_fh0_fc00_mk0_mc0";
   NpcPreset p=new NpcPreset(1,"bb1_"+r.id(),"BB1 "+r.label(),r.id(),recipe,(r.minHeight()+r.maxHeight())/2,(r.minBuild()+r.maxBuild())/2,style(row[2]),style(row[3]),index%2==1).validated();
   PresetStore.saveNew(p);NpcIntegrationTest.check(NpcPreset.parse(p.json()).equals(p),"BB NPC JSON roundtrip");
   class_1297 e=NexusNpc.spawn(server.method_3739().method_9208(new class_243((index++-2)*1.7,100,3)),p);
   e.method_36456(180);if(e instanceof class_1309 living){living.method_5847(180);living.method_5636(180);}if(e instanceof class_1308 mob)mob.method_5977(true);
   NpcIntegrationTest.ids.put(e.method_5667(),p.id());
  }
  NpcIntegrationTest.results.add("BB_NPC_SPAWN_PASS 5 racial presets, classic/slim bodies, server validation, JSON persistence");
 }
 static void verify(){
  Set<String> expected=new LinkedHashSet<>();for(var row:CASES)for(int i=1;i<4;i++)if(!row[i].equals("none"))expected.add(BlockbenchPresetCatalog.base(style(row[i])));
  for(String id:expected)NpcIntegrationTest.check(BlockbenchRenderSupport.renderCount(id)>before.getOrDefault(id,0L)+5,"BB feature not rendered in world: "+id);
  for(var row:CASES)NpcIntegrationTest.check(NpcIntegrationTest.seen.getOrDefault("bb1_"+row[0],0)>5,"BB racial NPC did not render: "+row[0]);
  NpcIntegrationTest.results.add("BB_NPC_RENDER_PASS models="+expected);
 }
}
