import java.util.*;
import java.nio.file.*;
import java.lang.reflect.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;
import dev.tr7zw.skinlayers.accessor.*;
import dev.tr7zw.skinlayers.api.*;

public final class ActualPlayerRegression implements ClientModInitializer {
 static int ticks,worldTicks;static boolean started;static int missing,overlaps;static long firstPersonBefore;
 static final String[] ids={
 "player_v69_b1_e1_ec0_ey2_h07_hc02_s05_o01_fh0_fc00_mk0_mc0",
 "player_v69_b3_e1_ec0_ey2_h05_hc02_s07_o19_fh0_fc00_mk0_mc0",
 "player_v69_b1_e1_ec0_ey2_h08_hc02_s05_o20_fh0_fc00_mk0_mc0",
 "player_v69_b1_e1_ec0_ey2_h09_hc02_s05_o20_fh6_fc04_mk0_mc0",
 "player_v69_b1_e1_ec0_ey2_h10_hc02_s05_o20_fh5_fc04_mk0_mc0",
 "player_v69_b4_e1_ec0_ey2_h01_hc03_s01_o16_fh4_fc04_mk0_mc0",
 "player_v69_b1_e1_ec0_ey2_h14_hc03_s01_o39_fh7_fc04_mk0_mc0",
 "player_v69_b1_e1_ec0_ey2_h1006_hc03_s01_o39_fh9_fc04_mk0_mc0"};
 public void onInitializeClient(){System.out.println("ACTUAL_REGRESSION_INITIALIZED version=21.3");ClientTickEvents.END_CLIENT_TICK.register(c->{try{
  if(!started){if(c.field_1755==null||c.method_18506()!=null)return;if(++ticks<20)return;started=true;
   class_7712 config=class_7712.field_40260;
   class_1940 settings=new class_1940("PRE21 regression",class_1934.method_8378("creative",class_1934.field_9220),false,class_1267.field_5801,true,new class_1928(config.comp_1011()),config);
   if(DynamicAssetCatalog.hairCount()!=15||DynamicAssetCatalog.facialHairCount()!=8||DynamicAssetCatalog.outfitCount()!=34)throw new AssertionError("New assets not discovered");
   System.out.println("DYNAMIC_CATALOG_PASS hair=15 facial=8 outfits=34");
   dev.tr7zw.skinlayers.SkinLayersModBase.config.irisCompatibilityMode=Boolean.getBoolean("compatibilityTest");
   System.out.println("ACTUAL_START_WORLD compatibility="+Boolean.getBoolean("compatibilityTest"));c.method_41735().method_41895("pre21-actual-player",settings,new class_5285(1,false,false),class_5317::method_64225,null);return;}
  if(c.field_1724==null||c.field_1687==null)return;
  worldTicks++;
  if(worldTicks%20==0)System.out.println("ACTUAL_WORLD_TICK "+worldTicks);
  int scene=(worldTicks-30)/60,phase=(worldTicks-30)%60;
  if(worldTicks>=30&&scene<ids.length&&phase==0){int i=scene;
   CharacterDto dto=new CharacterDto(c.field_1724.method_5667(),"ActualPlayer",null,null,"__capitale_preset__:"+ids[i],0,false,"human",1,1,"none","none");
   Field selected=NexusCharacters.class.getDeclaredField("selectedCharacter");selected.setAccessible(true);selected.set(null,dto);
   c.field_1724.method_5780("nexuscharacters.skin."+ids[i]);c.method_1507(new ActualScene(i));
  }
  if(worldTicks>=30&&scene<ids.length&&phase==20){
   var renderer=c.method_1561().method_74405(c.field_1724);
   class_10055 state=renderer.method_62608();renderer.method_62604(c.field_1724,state,0);
   var model=renderer.method_4038();
   PlayerSettings settings=(PlayerSettings)c.field_1724;
   settings.clearMeshes();settings.setCurrentSkin((class_2960)Class.forName("dev.tr7zw.transition.mc.PlayerUtil").getMethod("getPlayerSkin",class_11890.class).invoke(null,c.field_1724));settings.setThinArms(((PlayerEntityModelAccessor)model).hasThinArms());
   model.method_62110(state);overlaps+=SubmittedGeometryAudit.run(model);SubmittedGeometryAudit.verifyUvs(model);
   Object plan=((Map<?,?>)SubmittedGeometryAudit.field(GenericSkinLayerSupport.class,"PLANS")).get("nexuscharacters:dynamic/appearance/"+ids[scene]);
   Method cook=plan.getClass().getDeclaredMethod("cooked");cook.setAccessible(true);Object[] expected=(Object[])cook.invoke(plan);boolean slim=((PlayerEntityModelAccessor)model).hasThinArms();int partIndex=0;
   String[] names={"field_3394","field_3483","field_3482","field_3479","field_3484","field_3486"};
   for(String n:names){Field f=null;for(Class<?> k=model.getClass();k!=null&&f==null;k=k.getSuperclass())try{f=k.getDeclaredField(n);}catch(NoSuchFieldException ignored){}f.setAccessible(true);ModelPartInjector p=(ModelPartInjector)f.get(model);Mesh mesh=p.getInjectedMesh();
    System.out.println("ACTUAL_FINAL_MESH case="+scene+" part="+n+" mesh="+mesh);
    int index=slim&&partIndex>=4?partIndex+2:partIndex;partIndex++;if(mesh!=expected[index])missing++;
   }
   System.out.println("ACTUAL_NATIVE_NULL_RESULT missing="+missing);
  }
  if(worldTicks>=30&&scene<ids.length&&(phase==28||phase==38||phase==48)){int i=scene,angle=(phase-28)/10;class_318.method_1663(c.method_1522(),im->{try{Files.createDirectories(Path.of("pre21/evidence"));im.method_4314(Path.of("pre21/evidence/actual-"+i+"-"+angle+".png"));}catch(Exception e){throw new RuntimeException(e);}finally{im.close();}});}
  int end=30+ids.length*60;
  if(worldTicks==end){c.method_1507(null);c.field_1690.method_31043(class_5498.field_26666);}
  if(worldTicks==end+20)class_318.method_1663(c.method_1522(),im->{try{im.method_4314(Path.of("pre21/evidence/third-person.png"));}catch(Exception e){throw new RuntimeException(e);}finally{im.close();}});
  if(worldTicks==end+30){firstPersonBefore=GenericSkinLayerSupport.drawnOuter;c.field_1690.method_31043(class_5498.field_26664);}
  if(worldTicks==end+50)class_318.method_1663(c.method_1522(),im->{try{im.method_4314(Path.of("pre21/evidence/first-person.png"));}catch(Exception e){throw new RuntimeException(e);}finally{im.close();}});
  if(worldTicks==end+60){if(missing!=0||overlaps!=0)throw new AssertionError("Actual renderer failed missing="+missing+" overlaps="+overlaps);if(GenericSkinLayerSupport.drawnOuter<=firstPersonBefore)throw new AssertionError("First person meshes not drawn");System.out.println("FIRST_PERSON_RENDER_PASS draws="+(GenericSkinLayerSupport.drawnOuter-firstPersonBefore));GeometryAudit.run();System.out.println("ACTUAL_PLAYER_REGRESSION_PASS cases="+ids.length+" captures="+(ids.length*3)+" missing="+missing+" overlaps="+overlaps);c.method_1490();}
 }catch(Throwable e){System.err.println("ACTUAL_REGRESSION_FAILURE "+e);e.printStackTrace();c.method_1490();}});}
 static final class ActualScene extends class_437 {
  final int i;ActualScene(int i){super(class_2561.method_43470("Actual player regression"));this.i=i;}
  public void method_25394(class_332 d,int mx,int my,float delta){try{
   d.method_25294(0,0,field_22789,field_22790,0xffcccccc);
   var player=class_310.method_1551().field_1724;
   Method extract=class_490.class.getDeclaredMethod("method_48472",class_1309.class);extract.setAccessible(true);
   class_10055 state=(class_10055)extract.invoke(null,player);state.field_53446=180;
   int angle=Math.max(0,Math.min(2,((worldTicks-30)%60-20)/10));float yaw=new float[]{.5f,2.5f,.5f}[angle],pitch=new float[]{-.8f,0f,.8f}[angle];
   d.method_70856(state,160,new org.joml.Vector3f(0,player.method_17682()/2,0),new org.joml.Quaternionf().rotateZ((float)Math.PI).rotateY(yaw).rotateX(pitch),new org.joml.Quaternionf(),field_22789/2-200,15,field_22789/2+200,field_22790-15);
  }catch(Exception e){throw new RuntimeException(e);}}
 }
}
