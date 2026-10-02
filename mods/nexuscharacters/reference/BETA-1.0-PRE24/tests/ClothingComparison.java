import dev.tr7zw.skinlayers.api.*;
import dev.tr7zw.skinlayers.accessor.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;

/** Identical texture and pose, comparing emitted PRE23 surfaces to native Skin Layers. */
public class ClothingComparison implements ClientModInitializer {
  static int ticks, wt, variant=-1, outfit, captures;
  static int failedGridChecks;
  static boolean started;
  static Object plan;
  static Object[] originals, full, raw;
  static final Path EVIDENCE=Path.of(System.getProperty("evidenceDir","pre24/evidence/candidate"));
  static void record(String s)throws Exception {
    Files.createDirectories(EVIDENCE);
    Files.writeString(EVIDENCE.resolve("comparison-status.txt"),s+"\n",StandardOpenOption.CREATE,StandardOpenOption.APPEND);
    System.out.println(s);
  }
  static final int[] OUTFITS={1,20,22,23};
  static final String[] UP={"field_3394","field_3483","field_3482","field_3479","field_3484","field_3486"};
  static final String[] LOW={"field_3398","field_3391","field_3397","field_3392","field_27433","field_3401"};
  static final String[] OFF={"HEAD","BODY","LEFT_LEG","RIGHT_LEG","LEFT_ARM","RIGHT_ARM"};
  static final int[][] SPECS={{8,8,8,32,0,0},{8,12,4,16,32,1},{4,12,4,0,48,1},{4,12,4,0,32,1},{4,12,4,48,48,1},{4,12,4,40,32,1}};
  static Object f(Object o,String n)throws Exception{return SurfaceGeometry.field(o,n);}
  static Object[] nativeMeshes(int[] pixels)throws Exception {
    class_1011 image=new class_1011(64,64,false);
    for(int y=0;y<64;y++)for(int x=0;x<64;x++)image.method_4305(x,y,pixels[y*64+x]);
    Object[] result=new Object[6];
    var config=dev.tr7zw.skinlayers.SkinLayersModBase.config;
    boolean fast=config.fastRender,iris=config.irisCompatibilityMode;
    try {
      config.fastRender=false;config.irisCompatibilityMode=false;
      for(int p=0;p<6;p++){int[] s=SPECS[p];result[p]=SkinLayersAPI.getMeshHelper().create3DMesh(image,s[0],s[1],s[2],s[3],s[4],s[5]!=0,p==0?.6f:p>=4?-2f:0);}
    }finally{config.fastRender=fast;config.irisCompatibilityMode=iris;image.close();}
    return result;
  }
  static void prepare(class_310 c,int index)throws Exception {
    outfit=OUTFITS[index];variant=-1;
    String id=String.format(Locale.ROOT,"player_v69_b1_e1_ec0_ey2_h00_hc02_s02_o%02d_fh0_fc00_mk0_mc0",outfit);
    Field chosen=NexusCharacters.class.getDeclaredField("selectedCharacter");chosen.setAccessible(true);
    chosen.set(null,new CharacterDto(c.field_1724.method_5667(),"Clothing",null,null,"__capitale_preset__:"+id,0,false,"human",1,1,"none","none"));
    var renderer=c.method_1561().method_74405(c.field_1724);var state=renderer.method_62608();renderer.method_62604(c.field_1724,state,0);renderer.method_4038().method_62110(state);
    plan=((Map<?,?>)f(GenericSkinLayerSupport.class,"PLANS")).get("nexuscharacters:dynamic/appearance/"+id);
    if(plan==null)throw new AssertionError("Missing original composed texture "+id);
    int[][] layers=(int[][])f(plan,"layers");
    originals=nativeMeshes(layers[1]);full=nativeMeshes((int[])f(plan,"outer"));
    Method cooked=plan.getClass().getDeclaredMethod("cooked");cooked.setAccessible(true);raw=(Object[])cooked.invoke(plan);
    var grid=ClothingGridAudit.run(raw[1]);
    record("CLOTHING_GRID outfit="+outfit+" texels="+grid.texels()+" maxDisplacementPixels="+grid.maxDisplacement());
    if(grid.maxDisplacement()>.001) {
      failedGridChecks++;
      if(!Boolean.getBoolean("expectClothingGridFailure"))throw new AssertionError("Clothing pixels displaced: "+grid);
    }
    class_1011 image=new class_1011(64,64,false);int[] merged=(int[])f(plan,"outer");
    for(int y=0;y<64;y++)for(int x=0;x<64;x++)image.method_4305(x,y,merged[y*64+x]);
    image.method_4314(EVIDENCE.resolve("outfit-"+outfit+"-composed-upper.png"));image.close();
    record("CLOTHING_COMPARISON_PREPARED outfit="+outfit);
  }
  public static void apply(Object model)throws Exception {
    if(variant<=0||plan==null)return;
    for(int p=0;p<6;p++){
      ((ModelPartInjector)f(model,LOW[p])).setInjectedMesh(null,null);
      Object mesh=variant==1?originals[p]:variant==2?full[p]:raw[p];
      ((ModelPartInjector)f(model,UP[p])).setInjectedMesh((Mesh)mesh,variant==3?NativeVoxelSurface.IDENTITY:(OffsetProvider)OffsetProvider.class.getField(OFF[p]).get(null));
    }
  }
  public void onInitializeClient(){
    ClientTickEvents.END_CLIENT_TICK.register(c->{try{
      if(!started){if(c.method_18506()!=null||++ticks<20)return;started=true;
        var config=class_7712.field_40260;var settings=new class_1940("Clothing comparison",class_1934.field_9220,false,class_1267.field_5801,true,new class_1928(config.comp_1011()),config);
        record("ACTUAL_START_WORLD clothing comparison modVersion="+net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("nexuscharacters").orElseThrow().getMetadata().getVersion());
        c.method_41735().method_41895("pre24-clothing",settings,new class_5285(1,false,false),class_5317::method_64225,null);return;}
      if(c.field_1724==null||c.field_1687==null)return;
      wt++;int index=(wt-20)/160, phase=(wt-20)%160;
      if(wt>=20&&index<OUTFITS.length){
        if(phase==0){prepare(c,index);c.method_1507(new Scene());}
        variant=phase/40;
        if(phase%40==15||phase%40==25||phase%40==35){
          int angle=(phase%40-5)/10-1;Path name=EVIDENCE.resolve("outfit-"+outfit+"-variant-"+variant+"-angle-"+angle+".png");
          class_318.method_1663(c.method_1522(),im->{try{im.method_4314(name);record("CAPTURE "+name);}catch(Exception e){throw new RuntimeException(e);}finally{im.close();}});captures++;}
      }
      if(wt==20+OUTFITS.length*160){variant=-1;if(captures!=48)throw new AssertionError(captures);if(Boolean.getBoolean("expectClothingGridFailure")&&failedGridChecks==0)throw new AssertionError("Rejected PRE23 not detected");record("ACTUAL_PLAYER_REGRESSION_PASS clothing comparison captures="+captures+" failedGridChecks="+failedGridChecks);c.method_1490();}
    }catch(Throwable e){try{record("CLOTHING_COMPARISON_FAILURE "+e);}catch(Exception ignored){}e.printStackTrace();c.method_1490();}});
  }
  static class Scene extends class_437 {
    Scene(){super(class_2561.method_43470("Clothing comparison"));}
    public void method_25394(class_332 d,int mx,int my,float delta){try{
      d.method_25294(0,0,field_22789,field_22790,0xffcccccc);
      var c=class_310.method_1551();var player=c.field_1724;
      Method extract=class_490.class.getDeclaredMethod("method_48472",class_1309.class);extract.setAccessible(true);
      var state=(class_10055)extract.invoke(null,player);state.field_53446=180;ActualPlayerRegression.pose(state,0);state.field_53403=0;
      int angle=Math.min(2,Math.max(0,((wt-20)%40)/10-1));float yaw=new float[]{.45f,1.57f,3.14f}[angle];
      d.method_70856(state,160,new org.joml.Vector3f(0,player.method_17682()/2,0),new org.joml.Quaternionf().rotateZ((float)Math.PI).rotateY(yaw).rotateX(-.12f),new org.joml.Quaternionf(),field_22789/2-200,15,field_22789/2+200,field_22790-15);
    }catch(Exception e){throw new RuntimeException(e);}}
  }
}
