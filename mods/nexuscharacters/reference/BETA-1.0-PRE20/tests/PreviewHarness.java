import java.util.*;
import java.nio.file.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;
public final class PreviewHarness implements ClientModInitializer {
 static int ticks, scene=-1, captures;static boolean worldStarted,reloadStarted;static int worldTicks;static long worldBaseStart,worldOuterStart;
 static final String[] ids={
 "player_v69_b1_e1_ec2_ey2_h08_hc02_s05_o20_fh0_fc04_mk0_mc0",
 "player_v69_b1_e1_ec2_ey2_h09_hc02_s05_o20_fh6_fc04_mk0_mc0",
 "player_v69_b1_e1_ec2_ey2_h10_hc02_s05_o20_fh5_fc04_mk0_mc0",
 "player_v69_b4_e1_ec2_ey2_h01_hc03_s01_o16_fh4_fc04_mk0_mc0",
 "player_v69_b1_e1_ec2_ey2_h06_hc03_s01_o01_fh3_fc04_mk0_mc0",
 "player_v69_b1_e1_ec2_ey2_h10_hc04_s03_o37_fh1_fc02_mk0_mc0",
 "player_v69_b1_e1_ec2_ey2_h01_hc01_s01_o10_fh5_fc05_mk0_mc0",
 "player_v69_b1_e1_ec2_ey2_h14_hc03_s01_o39_fh7_fc04_mk0_mc0",
 "player_v69_b1_e1_ec2_ey2_h1006_hc03_s01_o39_fh9_fc04_mk0_mc0"
 };
 public void onInitializeClient(){
  if(Boolean.getBoolean("worldOnly"))scene=ids.length*3-1;
  ClientTickEvents.END_CLIENT_TICK.register(client->{
    if(worldStarted){
       if(client.field_1724!=null && client.field_1687!=null){
         worldTicks++;
         if(worldTicks==20)System.out.println("WORLD_HARNESS_READY player="+client.field_1724);
         if(worldTicks>=40 && worldTicks<40+ids.length*30){
           int current=Math.min(ids.length-1,(worldTicks-40)/30);
           client.method_1507(new WorldScene(current));
           if((worldTicks-60)%30==0 && worldTicks>=60)class_318.method_1663(client.method_1522(),image->{try{image.method_4314(Path.of("runtime/rendered/world-"+current+".png"));System.out.println("WORLD_CAPTURE "+current);}catch(Exception e){throw new RuntimeException(e);}finally{image.close();}});
         }
         int end=40+ids.length*30;
         if(worldTicks==end){try{
           CharacterDto actual=new CharacterDto(client.field_1724.method_5667(),"PRE19Avatar",null,null,"__capitale_preset__:"+ids[1],0,false,"human",1,1,"none","none");
           java.lang.reflect.Field selected=NexusCharacters.class.getDeclaredField("selectedCharacter");selected.setAccessible(true);selected.set(null,actual);
           client.field_1724.method_5780("nexuscharacters.skin."+ids[1]);
           client.method_1507(null);client.field_1690.method_31043(class_5498.field_26666);
         }catch(Exception e){throw new RuntimeException(e);}}
         if(worldTicks==end+20)class_318.method_1663(client.method_1522(),im->{try{im.method_4314(Path.of("runtime/rendered/avatar.png"));}catch(Exception e){throw new RuntimeException(e);}finally{im.close();}});
         if(worldTicks==end+30)client.field_1690.method_31043(class_5498.field_26664);
         if(worldTicks==end+50)class_318.method_1663(client.method_1522(),im->{try{im.method_4314(Path.of("runtime/rendered/first-person.png"));}catch(Exception e){throw new RuntimeException(e);}finally{im.close();}});
         if(worldTicks==end+60){
           if(GenericSkinLayerSupport.drawnBase!=0 || GenericSkinLayerSupport.drawnOuter<=worldOuterStart)throw new AssertionError("Outer meshes missing, or base wrongly extruded");
           System.out.println("WORLD_HARNESS_PASS baseDraws="+GenericSkinLayerSupport.drawnBase+" outerDraws="+GenericSkinLayerSupport.drawnOuter);client.method_1490();
         }
       }return;
    }
    if(client.field_1755==null || client.method_18506()!=null)return;
    ticks++;
    if(ticks==20){
      int h=DynamicAssetCatalog.hairCount(),f=DynamicAssetCatalog.facialHairCount(),o=DynamicAssetCatalog.outfitCount();
      if(h!=15||f!=8||o!=34)throw new AssertionError("Dynamic resource catalog failed: "+h+","+f+","+o);
      if(FacialHairRaceSupport.next(CharacterRace.HUMAN,7)!=9)throw new AssertionError("Sparse beard button failed");
      System.out.println("DYNAMIC_CATALOG_PASS hair="+h+" facial="+f+" outfits="+o);
      if(!reloadStarted){reloadStarted=true;client.method_1521().thenRun(()->client.method_18859(()->{
        if(DynamicAssetCatalog.hairCount()!=15 || DynamicAssetCatalog.facialHairCount()!=8 || DynamicAssetCatalog.outfitCount()!=34)throw new AssertionError("Reload lost dynamic assets");
        System.out.println("RESOURCE_RELOAD_PASS");ticks=19;client.method_1507(new Scene());
      }));return;}
      verifyCreationScreen(client);
      try {GeometryAudit.run();}catch(Exception e){throw new RuntimeException(e);}
      client.method_1507(new Scene());
    }
    if(!(client.field_1755 instanceof Scene))return;
    if(ticks>=20 && (ticks-20)%30==0){scene++;if(scene>=ids.length*3){System.out.println("PREVIEW_HARNESS_PASS captures="+captures);worldStarted=true;worldBaseStart=GenericSkinLayerSupport.drawnBase;worldOuterStart=GenericSkinLayerSupport.drawnOuter;
      net.minecraft.class_7712 config=class_7712.field_40260;
      class_1940 settings=new class_1940("PRE18 test",class_1934.method_8378("creative",class_1934.field_9220),false,class_1267.field_5801,true,new class_1928(config.comp_1011()),config);
      client.method_41735().method_41895("pre18-render-test",settings,new class_5285(1,false,false),class_5317::method_64225,null);
      return;}System.out.println("PREVIEW_SCENE "+scene);}
    if(ticks>=40 && (ticks-40)%30==0){
      int current=scene;class_318.method_1663(client.method_1522(),image->{try{Path p=Path.of("runtime/rendered/scene-"+current+".png");Files.createDirectories(p.getParent());image.method_4314(p);captures++;System.out.println("CAPTURE "+p);}catch(Exception e){throw new RuntimeException(e);}finally{image.close();}});
    }
  });
 }
 static final class AvatarScene extends class_437 {
   AvatarScene(){super(class_2561.method_43470("Real player rendering"));}
   public void method_25394(class_332 draw,int mx,int my,float delta){
     draw.method_25294(0,0,field_22789,field_22790,0xffcccccc);
     PreviewRotationSupport.drawEntity(draw,field_22789/2-100,15,field_22789/2+100,field_22790-15,100,0.0625f,35,0,class_310.method_1551().field_1724);
   }
 }
 static java.lang.reflect.Field field(Object value,String name)throws Exception{java.lang.reflect.Field f=value.getClass().getDeclaredField(name);f.setAccessible(true);return f;}
 static java.lang.reflect.Method method(Object value,String fragment)throws Exception{for(java.lang.reflect.Method m:value.getClass().getDeclaredMethods())if(m.getName().contains(fragment)){m.setAccessible(true);return m;}throw new NoSuchMethodException(fragment);}
 static void verifyCreationScreen(class_310 client){try{
   CharacterCreationScreen screen=new CharacterCreationScreen(client.field_1755,()->{});client.method_1507(screen);
   field(screen,"race").set(screen,CharacterRace.HUMAN);
   field(screen,"nexuscharacters$hair").setInt(screen,13);
   method(screen,"lambda$nexuscharacters$install$2").invoke(screen,new Object[]{null});
   if(field(screen,"nexuscharacters$hair").getInt(screen)!=14)throw new AssertionError("Real hair button did not reach added hair");
   field(screen,"nexuscharacters$hair").setInt(screen,1006);
   field(screen,"nexuscharacters$facialHair").setInt(screen,7);UnifiedPilositySupport.cycle(screen);
   field(screen,"nexuscharacters$outfit").setInt(screen,38);
   method(screen,"lambda$nexuscharacters$install$4").invoke(screen,new Object[]{null});
   String marker=(String)method(screen,"nexuscharacters$v69Marker").invoke(screen,CharacterRace.HUMAN,field(screen,"selectedPreset").getInt(screen));
   Appearance69Support.Params params=Appearance69Support.parse(Appearance69Support.markerId(marker));
   if(params.hair()!=1006 || params.facialHair()!=9 || params.outfit()!=39)throw new AssertionError("Real creation UI lost added assets: "+marker);
   System.out.println("CREATION_UI_PASS hair=1006 facial=9 outfit=39");
 }catch(Exception e){throw new RuntimeException(e);}}
  static final class WorldScene extends class_437 {
    final int current;
    WorldScene(int current){super(class_2561.method_43470("World rendering test"));this.current=current;}
    public void method_25394(class_332 draw,int mx,int my,float delta){
      draw.method_25294(0,0,field_22789,field_22790,0xffcccccc);
      CharacterDto dto=new CharacterDto(UUID.fromString("00000000-0000-0000-0000-000000000002"),"PRE19World",null,null,"__capitale_preset__:"+ids[current],0,false,"human",1,1,"none","none");
      CharacterPreviewRenderer.draw(draw,dto,field_22789/2,15,field_22790-15,field_22789/2,field_22790/2);
    }
  }
  static final class Scene extends class_437 {
   Scene(){super(class_2561.method_43470("PRE18 rendering test"));}
   public void method_25394(class_332 draw,int mx,int my,float delta){
    draw.method_25294(0,0,field_22789,field_22790,0xffcccccc);
    if(scene<0)return;
    String id=ids[Math.min(ids.length-1,scene/3)];
    CharacterDto dto=new CharacterDto(UUID.fromString("00000000-0000-0000-0000-000000000001"),"PRE18",null,null,"__capitale_preset__:"+id,0,false,"human",1,1,"none","none");
    CharacterPreviewRenderer.draw(draw,dto,field_22789/2,15,field_22790-15,field_22789/2,field_22790/2);
    try{
     java.lang.reflect.Field f=CharacterPreviewRenderer.class.getDeclaredField("skinWidget");f.setAccessible(true);Object widget=f.get(null);
     if(widget!=null){java.lang.reflect.Field r=widget.getClass().getDeclaredField("field_46006");r.setAccessible(true);r.setFloat(widget,new float[]{0,45,135}[scene%3]);}
    }catch(Exception e){throw new RuntimeException(e);}
   }
 }
}
