import java.util.*;
import java.nio.file.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;
public final class PreviewHarness implements ClientModInitializer {
 static int ticks, scene=-1, captures;static boolean worldStarted,reloadStarted;static int worldTicks;
 static final String[] ids={
 "player_v69_b1_e1_ec2_ey0_h06_hc03_s01_o01_fh3_fc04_mk0_mc0",
 "player_v69_b1_e1_ec2_ey0_h10_hc04_s03_o37_fh1_fc02_mk0_mc0",
 "player_v69_b1_e1_ec2_ey0_h01_hc01_s01_o10_fh5_fc05_mk0_mc0",
 "player_v69_b1_e1_ec2_ey0_h14_hc03_s01_o39_fh7_fc04_mk0_mc0",
 "player_v69_b1_e1_ec2_ey0_h1006_hc03_s01_o39_fh9_fc04_mk0_mc0"
 };
 public void onInitializeClient(){
  ClientTickEvents.END_CLIENT_TICK.register(client->{
    if(worldStarted){
       if(client.field_1724!=null && client.field_1687!=null){
         worldTicks++;
         if(worldTicks==20)System.out.println("WORLD_HARNESS_READY player="+client.field_1724);
         if(worldTicks>=40){
           client.method_1507(new WorldScene());
           if(worldTicks==60)class_318.method_1663(client.method_1522(),image->{try{image.method_4314(Path.of("runtime/rendered/world.png"));System.out.println("WORLD_HARNESS_PASS");}catch(Exception e){throw new RuntimeException(e);}finally{image.close();}});
           if(worldTicks==80)client.method_1490();
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
      client.method_1507(new Scene());
    }
    if(!(client.field_1755 instanceof Scene))return;
    if(ticks>=20 && (ticks-20)%30==0){scene++;if(scene>=ids.length*3){System.out.println("PREVIEW_HARNESS_PASS captures="+captures);worldStarted=true;
      net.minecraft.class_7712 config=class_7712.field_40260;
      class_1940 settings=new class_1940("PRE18 test",class_1934.method_8378("creative",class_1934.field_9220),false,class_1267.field_5801,true,new class_1928(config.comp_1011()),config);
      client.method_41735().method_41895("pre18-render-test",settings,new class_5285(1,false,false),class_5317::method_64225,null);
      return;}System.out.println("PREVIEW_SCENE "+scene);}
    if(ticks>=40 && (ticks-40)%30==0){
      int current=scene;class_318.method_1663(client.method_1522(),image->{try{Path p=Path.of("runtime/rendered/scene-"+current+".png");Files.createDirectories(p.getParent());image.method_4314(p);captures++;System.out.println("CAPTURE "+p);}catch(Exception e){throw new RuntimeException(e);}finally{image.close();}});
    }
  });
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
    WorldScene(){super(class_2561.method_43470("World rendering test"));}
    public void method_25394(class_332 draw,int mx,int my,float delta){
      draw.method_25294(0,0,field_22789,field_22790,0xffcccccc);
      CharacterDto dto=new CharacterDto(UUID.fromString("00000000-0000-0000-0000-000000000002"),"PRE18World",null,null,"__capitale_preset__:"+ids[0],0,false,"human",1,1,"none","none");
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
     if(widget!=null){java.lang.reflect.Field r=widget.getClass().getDeclaredField("field_46006");r.setAccessible(true);r.setFloat(widget,new float[]{0,90,180}[scene%3]);}
    }catch(Exception e){throw new RuntimeException(e);}
   }
 }
}
