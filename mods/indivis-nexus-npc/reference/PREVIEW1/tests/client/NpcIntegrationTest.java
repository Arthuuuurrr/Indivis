import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.reflect.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.data.skin.SkinDataEntry;
import fr.indivis.nexusnpc.*;
import fr.indivis.nexusnpc.client.*;

public final class NpcIntegrationTest implements ClientModInitializer {
 static final Path OUT=Path.of(System.getProperty("npcqa.out"));
 static final boolean RELOAD=Boolean.getBoolean("npcqa.reload");
 static int tick,stage,worldTick,checks,initialCharacters;
 static volatile boolean spawned;static boolean failure,done;
 static final Map<UUID,String> ids=new ConcurrentHashMap<>();
 static final Map<String,Integer> seen=new TreeMap<>();
 static final Map<String,Set<String>> hashes=new TreeMap<>();
 static final List<String> results=new ArrayList<>();
 static class_1297 control;
 static void check(boolean ok,String text){checks++;if(!ok)throw new AssertionError(text);}
 static void fail(Throwable e){if(failure)return;failure=true;try{Files.createDirectories(OUT);java.io.StringWriter w=new java.io.StringWriter();e.printStackTrace(new java.io.PrintWriter(w));Files.writeString(OUT.resolve("failure.txt"),w+"\nseen="+seen+" feature registered="+NpcCosmeticLayer.registered+" calls="+NpcCosmeticLayer.calls+" draws="+NpcClientBridge.cosmeticDraws+" extracts="+NpcClientBridge.extracts+" hits="+NpcClientBridge.cacheHits+" preparations="+NpcClientBridge.preparations);}catch(Exception x){}e.printStackTrace();class_310.method_1551().method_1490();}
 public void onInitializeClient(){ClientTickEvents.END_CLIENT_TICK.register(c->{try{
  if(done||failure)return;
  if(stage==0){
   if(c.field_1755==null||c.method_18506()!=null||++tick<80)return;
   Files.createDirectories(OUT);
   if(RELOAD){selectCharacter();c.method_41735().method_57784("npc-isolated",()->{});stage=4;tick=0;return;}
   for(String id:PresetStore.BUILTIN){NpcPreset p=PresetStore.load(id);check(NpcPreset.parse(p.json()).equals(p),"Preset JSON roundtrip "+id);results.add("PRESET "+p.json().replace('\n',' '));}
   try{NpcPreset.parse(PresetStore.load("haut_elfe_ivoire").json().replace("_s02_","_s00_"));throw new AssertionError("Invalid recipe silently became Steve");}catch(IllegalArgumentException expected){checks++;}
   check(!NexusNpc.admin(class_2170.method_75745(permission->false)),"No-permission source may edit");
   for(int level=0;level<=4;level++)check(NexusNpc.admin(class_2170.method_75745(class_12086.method_75010(class_12094.method_75027(level))))==(level==4),"Wrong OP level threshold "+level);
   for(String bad:List.of("../evil","/tmp/evil","a/b","", "AA")){try{PresetStore.path(bad);throw new AssertionError("Path accepted "+bad);}catch(IllegalArgumentException expected){checks++;}}
   initialCharacters=NexusCharacters.DATA_FILE_MANAGER.characterList.size();
   NpcEditor.open("qa_export");((class_342)IndivisMenus.get(c.field_1755,"name")).method_1852("Preset");((class_342)IndivisMenus.get(c.field_1755,"nexuscharacters$lastName")).method_1852("Test");stage=1;tick=0;return;
  }
  if(stage==1){
   if(++tick==25){capture(c,"editor.png");}
   if(tick<35)return;
   var screen=c.field_1755;class_339 button=null;
   for(class_364 child:screen.method_25396())if(child instanceof class_339 w&&w.method_25369().getString().equals("Créer")){button=w;break;}
   check(button!=null,"Nexus create control absent");
   var mouse=new class_11909(button.method_46426()+button.method_25368()/2d,button.method_46427()+button.method_25364()/2d,new class_11910(0,0));
   screen.method_25402(mouse,false);screen.method_25406(mouse);
   check(NpcEditor.exports==1,"Actual editor click did not export: "+IndivisMenus.get(screen,"status"));
   check(NexusCharacters.DATA_FILE_MANAGER.characterList.size()==initialCharacters,"PNJ export created a player character");
   NpcPreset exported=PresetStore.loadLocal("qa_export");check(exported.skin().startsWith("player_v"),"Editor recipe lost");
   c.method_1507(null);
   class_7712 config=class_7712.field_40260;var settings=new class_1940("Nexus PNJ isolated",class_1934.field_9220,false,class_1267.field_5801,true,new class_1928(config.comp_1011()),config);
   selectCharacter();
   c.method_41735().method_41895("npc-isolated",settings,new class_5285(1,false,false),class_5317::method_64225,null);stage=2;tick=0;return;
  }
  if(c.field_1724==null||c.field_1687==null)return;
  if(stage==4){if(++tick<30)return;c.method_1576().execute(()->{try{
   int count=0,sentinels=0;
   for(var entity:c.method_1576().method_30002().method_27909())if(entity instanceof EasyNPC<?> npc){var p=NpcAppearance.read(npc.getEasyNPCSkinData().getSkinDataEntry());if(p==null)continue;check(p.preset().equals(PresetStore.load(p.preset().id())),"Saved world recipe changed");ids.put(entity.method_5667(),p.preset().id());count++;if(entity.method_5752().contains("quest_sentinel_do_not_change"))sentinels++;}
   check(count==10&&sentinels==5,"World reload count/tags differ "+count+"/"+sentinels);results.add("WORLD_RELOAD_PASS real saved world: 10 NPCs, 5 original tags, recipes and UUIDs retained");spawned=true;
  }catch(Throwable e){fail(e);}});stage=3;return;}
  if(stage==2){
   if(++tick<30)return;
   ClientPlayNetworking.send(new UploadPreset(PresetStore.loadLocal("qa_export").json()));
   c.method_1576().execute(()->{try{spawn(c);}catch(Throwable e){fail(e);}});stage=3;tick=0;return;
  }
  if(!spawned)return;worldTick++;
  c.field_1724.method_36456(0);c.field_1724.method_36457(0);c.field_1690.method_31043(class_5498.field_26664);
  if(worldTick==50)capture(c,"races-face.png");
  if(worldTick==100){c.method_1576().execute(()->{for(var entry:ids.entrySet()){var entity=c.method_1576().method_30002().method_73284(entry.getKey());if(entity instanceof class_1309 living)living.method_6104(class_1268.field_5808);}});capture(c,"races-action.png");}
  if(worldTick==150){check(PresetStore.load("qa_export").id().equals("qa_export"),"C2S upload missing on server");NpcClientBridge.clear();GenericSkinLayerSupport.clearCaches();}
  if(worldTick==200)capture(c,"races-after-clear.png");
  if(worldTick<260)return;
  for(String id:PresetStore.BUILTIN)check(seen.getOrDefault(id,0)>5,"No actual EasyNPC 3D render for "+id+" seen="+seen);
  check(NpcClientBridge.cosmeticDraws>25,"Actual cosmetic feature absent");check(NpcClientBridge.cacheHits>50,"No reuse of appearance preparations");
  results.add("NPC_CLIENT_PASS checks="+checks+" seen="+seen+" preparations="+NpcClientBridge.preparations+" hits="+NpcClientBridge.cacheHits+" layers="+NpcClientBridge.cosmeticDraws);
  Files.write(OUT.resolve("pass.txt"),results);Files.writeString(OUT.resolve("hashes.json"),NpcPreset.JSON.toJson(hashes));System.out.println(results.get(results.size()-1));done=true;c.method_1490();
 }catch(Throwable e){fail(e);}});}
 static class_2487 snapshot(class_1297 entity){var out=class_11362.method_71458(class_8942.field_60348);entity.method_5647(out);return out.method_71475();}
 static void selectCharacter(){NexusCharacters.selectedCharacter=new CharacterDto(UUID.fromString("00000000-0000-0000-0000-000000000001"),"NPC QA",null,null,"__capitale_preset__:player_v69_b1_e1_ec0_ey2_h01_hc02_s04_o01_fh0_fc00_mk0_mc0",0,false,"human",1,1,"none","none");}
 static void spawn(class_310 c)throws Exception{
  var server=c.method_1576();var world=server.method_30002();
  for(String command:List.of("fill -12 99 -12 12 99 12 minecraft:stone","fill -12 100 -12 12 104 12 minecraft:air","tp @a 0 100 0 0 0","time set noon","weather clear"))server.method_3734().method_44252(server.method_3739(),command);
  int i=0;
  for(String id:PresetStore.BUILTIN){
   NpcPreset preset=PresetStore.load(id);
   var source=server.method_3739().method_9208(new class_243((i++-2)*1.7,100,5));
   class_1297 entity=NexusNpc.spawn(source,preset);entity.method_36456(180);entity.method_36457(0);if(entity instanceof class_1309 living){living.method_5847(180);living.method_5636(180);}if(entity instanceof class_1308 mob)mob.method_5977(true);
   entity.method_5780("quest_sentinel_do_not_change");ids.put(entity.method_5667(),id);
   EasyNPC<?> npc=(EasyNPC<?>)entity;
   results.add("SERVER_MORPH "+id+" default="+npc.getEasyNPCModelData().getDefaultModelScale()+" root="+npc.getEasyNPCModelData().getModelRootData());
   var saved=NpcAppearance.read(npc.getEasyNPCSkinData().getSkinDataEntry());check(saved.preset().equals(preset),"Saved recipe differs");
   class_2487 nbt=snapshot(entity);UUID uuid=entity.method_5667();
   NpcAppearance.apply(entity,preset);class_2487 again=snapshot(entity);
   nbt.method_10551("SkinData");again.method_10551("SkinData");
   nbt.method_10562("Status").ifPresent(t->t.method_10551("npc_data_last_update"));again.method_10562("Status").ifPresent(t->t.method_10551("npc_data_last_update"));
   if(!nbt.equals(again)){Files.writeString(OUT.resolve("nbt-before.snbt"),nbt.toString());Files.writeString(OUT.resolve("nbt-after.snbt"),again.toString());}
   check(nbt.equals(again),"Applying preset twice modified other NPC fields: "+id);
   check(entity.method_5667().equals(uuid)&&entity.method_5752().contains("quest_sentinel_do_not_change"),"UUID/tags changed");
   SkinDataEntry round=new SkinDataEntry(npc.getEasyNPCSkinData().getSkinDataEntry().createTag());check(NpcAppearance.read(round).preset().equals(preset),"SkinData NBT roundtrip");
   check(NpcAppearance.reset(entity),"Restore failed");
   var restored=npc.getEasyNPCSkinData().getSkinDataEntry();check(restored.createTag().toString().equals(saved.originalSkin()),"Original skin not restored");
   NpcAppearance.apply(entity,preset);
  }
  NativePresets.run(server);NpcDataRegression.run(world);
  spawned=true;System.out.println("NPC_SERVER_APPLY_PASS entities="+ids.size());
 }
 public static void audit(Object model,Object state){
  if(!(state instanceof NpcRenderState flag)||!flag.indivis$hasNexusPreset()||failure)return;
  try{
   Object e=SurfaceGeometry.call(state,"getTransitionEntity");if(!(e instanceof class_1297 entity))return;
   String id=ids.get(entity.method_5667());if(id==null)return;
   if(!Files.exists(OUT.resolve("render-"+id+".txt"))){
    var renderer=class_310.method_1551().method_1561().method_3953(entity);
    StringBuilder details=new StringBuilder("renderer="+renderer.getClass()+" features="+SurfaceGeometry.field(renderer,"field_4738")+" root="+((EasyNPC<?>)entity).getEasyNPCModelData().getModelRootData()+" firstPerson="+SurfaceGeometry.call(state,"playerAnimLib$isFirstPersonPass")+"\n");
    for(String field:RenderPerf.LOW){class_630 part=(class_630)SurfaceGeometry.field(model,field);details.append(field+" pos="+part.field_3657+","+part.field_3656+","+part.field_3655+" scale="+part.field_37938+","+part.field_37939+","+part.field_37940+"\n");}
    Files.writeString(OUT.resolve("render-"+id+".txt"),details);
   }
   Object s=RenderPerf.state(model);check(s!=null,"EasyNPC native surface state absent: model="+model.getClass()+" texture="+((class_10055)state).field_53520+" plans="+((Map<?,?>)SurfaceGeometry.field(GenericSkinLayerSupport.class,"PLANS")).keySet());
   for(String name:RenderPerf.LOW){var part=(dev.tr7zw.skinlayers.accessor.ModelPartInjector)SurfaceGeometry.field(model,name);check(part.getInjectedMesh()!=null&&part.getInjectedMesh().toString().startsWith("NexusPosed23"),"Native EasyNPC base not injected "+name);}
   int count=0;for(Object mesh:(Object[])SurfaceGeometry.field(s,"outer"))count+=((float[])SurfaceGeometry.field(mesh,"polygonData")).length/23;
   check(count>100,"3D outer geometry absent "+id);
   int n=seen.getOrDefault(id,0);seen.put(id,n+1);if(n<4)hashes.computeIfAbsent(id,k->new TreeSet<>()).add(RenderPerf.geometry(model));
  }catch(Throwable e){fail(e);}
 }
 static void capture(class_310 c,String name){class_318.method_1663(c.method_1522(),image->{try{image.method_4314(OUT.resolve(name));}catch(Exception e){fail(e);}finally{image.close();}});}
 public static void featureStage(Object renderer,Object state)throws Exception{
  if(!(state instanceof NpcRenderState flag)||!flag.indivis$hasNexusPreset())return;
  Path path=OUT.resolve("feature-stage.txt");
  if(!Files.exists(path))Files.writeString(path,"layer invocation on "+renderer.getClass()+" firstPerson="+SurfaceGeometry.call(state,"playerAnimLib$isFirstPersonPass")+" features="+SurfaceGeometry.field(renderer,"field_4738"));
 }
}
