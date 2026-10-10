import java.nio.file.*;
import java.util.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;
public final class WorldDiagnostic implements ClientModInitializer {
 static int tick,worldTick,scene=-1,checks,frames;
 static boolean starting,reloading,finished;
 static java.util.concurrent.CompletableFuture<Void> reload;
 static final Path OUT=Path.of("pre31/qa/world-evidence");
 static void check(boolean c,String t){checks++;if(!c)throw new AssertionError(t);}
 static CharacterDto dto(String marker,String race,float height,float build){return new CharacterDto(UUID.fromString("00000000-0000-0000-0000-000000000001"),"PRE30",null,null,"__capitale_preset__:"+marker,0,false,race,height,build,"none","none");}
 static final String[] markers={"player_v69_b1_e1_ec0_ey2_h07_hc02_s02_o22_fh2_fc00_mk0_mc0","player_v69_b2_e2_ec0_ey2_h08_hc03_s04_o24_fh1_fc00_mk0_mc0"};
 public void onInitializeClient(){
  ClientTickEvents.END_CLIENT_TICK.register(c->{try{
   if(!starting){if(c.field_1755==null||c.method_18506()!=null||++tick<80)return;starting=true;
    NexusCharacters.selectedCharacter=dto(markers[0],"human",1,1);NexusCharacters.DATA_FILE_MANAGER.characterList.clear();NexusCharacters.DATA_FILE_MANAGER.characterList.add(NexusCharacters.selectedCharacter);NexusCharacters.DATA_FILE_MANAGER.save();
    class_7712 config=class_7712.field_40260;class_1940 settings=new class_1940("PRE31 isolated diagnostic",class_1934.field_9220,false,class_1267.field_5801,true,new class_1928(config.comp_1011()),config);
    c.method_41735().method_41895("pre31-isolated-world",settings,new class_5285(1,false,false),class_5317::method_64225,null);return;
   }
   if(c.field_1724==null||c.field_1687==null)return;
   if(reloading){if(!reload.isDone()||c.method_18506()!=null)return;reload.join();reloading=false;System.out.println("PRE30_ACTUAL_RESOURCE_RELOAD_DONE");}
   worldTick++;
   if(worldTick<30)return;
   int next=(worldTick-30)/50;
   if(next>=7){check(frames>30,"Too few actual render frames");check(checks>=30,"Native surface checks did not run");String passed="PRE31_WORLD_PASS checks="+checks+" frames="+frames+" diagnostics="+Pre30Diagnostics.snapshot();Files.createDirectories(OUT);Files.writeString(OUT.resolve("pass.txt"),passed);System.out.println(passed);finished=true;c.method_1490();return;}
   if(next!=scene){
    if(scene>=0)check(samples.getOrDefault(scene,0)>=6,"Scene lacks verified render frames "+scene);
    scene=next;
    c.method_1507(null);c.field_1690.method_31043(scene==0||scene==4||scene==6?class_5498.field_26664:class_5498.field_26665);
    int variant=scene>=3?1:0;NexusCharacters.selectedCharacter=dto(markers[variant],variant==1?"dwarf":"human",variant==1?.82f:1,variant==1?1.17f:1);
    c.field_1724.method_5752().removeIf(t->t.startsWith("nexuscharacters.skin.")||t.startsWith("nexuscharacters.build."));c.field_1724.method_5780("nexuscharacters.skin."+markers[variant]);c.field_1724.method_5780("nexuscharacters.build."+(variant==1?117:100));
    if(scene==2)c.method_1507(new class_490(c.field_1724));
    if(scene==5){reloading=true;reload=c.method_1521();System.out.println("PRE30_ACTUAL_RESOURCE_RELOAD_START");}
    System.out.println("PRE30_WORLD_SCENE "+scene);
   }
   if(scene==0||scene==6)if(worldTick%10==0)c.field_1724.method_6104(class_1268.field_5808);
  }catch(Throwable e){System.err.println("PRE30_WORLD_FAILURE "+e);e.printStackTrace();c.method_1490();}});
 }
 static int traces;
 public static void traceCaller(){if(scene==1&&traces++<2){System.out.println("PRE30_PLAYER_CALL_TRACE");for(StackTraceElement frame:Thread.currentThread().getStackTrace())if(frame.getClassName().contains("nexuscharacters")||frame.getClassName().contains("minecraft"))System.out.println("  "+frame);}}
 static Map<String,Long> before;
 public static void frameStart(){before=Pre30Diagnostics.snapshot();}
 static final Map<Integer,Integer> samples=new HashMap<>();
 public static void frameEnd(){try{
  if(scene<0||finished||reloading)return;frames++;
  Map<String,Long> after=Pre30Diagnostics.snapshot();int sample=samples.getOrDefault(scene,0);samples.put(scene,sample+1);
  if(sample==5||sample==10||sample==15){Map<String,Long> delta=new LinkedHashMap<>();for(String k:List.of("playerCalls","previewCalls","firstPersonCalls","applyCount","recomputeCount","methodResolutionScans","fieldHierarchyScans"))delta.put(k,after.get(k)-before.get(k));System.out.println("PRE30_FRAME_COUNTS scene="+scene+" sample="+sample+" "+delta);}
  if(sample==5){
   var c=class_310.method_1551();var renderer=c.method_1561().method_74405(c.field_1724);var model=renderer.method_4038();
   if(scene==0||scene==1||scene==2||scene==3||scene==5||scene==6){
    Object state=RenderPerf.state(model);check(state!=null,"World model has no posed state scene="+scene);
    if(scene==0||scene==6)check((Boolean)SurfaceGeometry.field(state,"firstPerson"),"First-person native state missing");
    for(String name:RenderPerf.LOW){var part=(dev.tr7zw.skinlayers.accessor.ModelPartInjector)SurfaceGeometry.field(model,name);check(part.getInjectedMesh()!=null&&part.getInjectedMesh().toString().startsWith("NexusPosed23"),"World native mesh absent "+name);}
   }
   Files.createDirectories(OUT);int index=scene;class_318.method_1663(c.method_1522(),im->{try{im.method_4314(OUT.resolve("world-"+index+".png"));}catch(Exception e){throw new RuntimeException(e);}finally{im.close();}});
  }
 }catch(Throwable e){System.err.println("PRE30_WORLD_RENDER_FAILURE "+e);e.printStackTrace();class_310.method_1551().method_1490();}}
}

