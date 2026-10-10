import java.nio.file.*;
import java.util.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;
public final class FutureAssetsRegression implements ClientModInitializer {
 static int tick,checks;static boolean started;static CharacterDto dto;static class_8765 widget;
 static final Path OUT=Path.of("pre33/qa/future-evidence");
 static void check(boolean c,String t){checks++;if(!c)throw new AssertionError(t);}
 static class Screen extends class_437 {
  Screen(){super(class_2561.method_43470("PRE30 future assets"));}
  @Override public void method_25420(class_332 ctx,int x,int y,float delta){
   widget.method_55445(240,300);widget.method_48229(field_22789/2-120,field_22790/2-150);
   GenericSkinLayerSupport.preview(widget,dto);widget.method_25394(ctx,x,y,delta);
  }
 }
 public void onInitializeClient(){ClientTickEvents.END_CLIENT_TICK.register(c->{try{
  if(!started){if(c.field_1755==null||c.method_18506()!=null||++tick<80)return;started=true;tick=0;
   check(DynamicAssetCatalog.hairAssetName(930).equals("hair_930.png"),"New hair was not discovered");check(DynamicAssetCatalog.facialHairName(930).equals("beard_930.png"),"New beard was not discovered");check(DynamicAssetCatalog.outfitAllowed(930),"New outfit was not discovered");
   dto=new CharacterDto(UUID.fromString("00000000-0000-0000-0000-000000000930"),"Future assets",null,null,"__capitale_preset__:player_v69_b2_e1_ec0_ey2_h930_hc02_s04_o930_fh930_fc00_mk0_mc0",0,false,"human",1,1,"none","none");
   widget=new class_8765(240,300,c.method_31974(),()->PreviewDummyPlayerManager.skin(dto));c.method_1507(new Screen());return;
  }
  if(++tick==30){Files.createDirectories(OUT);GenericSkinLayerSupport.preview(widget,dto);
   for(String modelName:List.of("field_59834","field_59835")){
    Object model=SurfaceGeometry.field(widget,modelName),state=RenderPerf.state(model);check(state!=null,"New-asset state absent "+modelName);
    Object[] templates=(Object[])SurfaceGeometry.field(state,"template");int polygons=0;
    for(Object t:templates)if(t!=null){var shape=NativeVoxelSurface.SHAPES.get(t);check(shape!=null,"New native shape absent");polygons+=shape.faces().size();}
    check(polygons>100,"Future cosmetics were flattened or lost");
    Files.writeString(OUT.resolve(modelName+"-geometry.txt"),RenderPerf.geometry(model));
   }
   Object classic=SurfaceGeometry.field(widget,"field_59834"),slim=SurfaceGeometry.field(widget,"field_59835");
   Object[] normal=new Object[8],a=(Object[])SurfaceGeometry.field(RenderPerf.state(classic),"template"),b=(Object[])SurfaceGeometry.field(RenderPerf.state(slim),"template");
   System.arraycopy(a,0,normal,0,6);normal[6]=b[4];normal[7]=b[5];Object[] first=RenderPerf.firstCooked(normal);
   Files.createDirectories(FirstPersonRegression.OUT);
   for(int kind=0;kind<2;kind++)for(int part=4;part<=5;part++){
    Object model=kind==0?classic:slim;
    FirstPersonRegression.compare(model,first,kind==1,part,"future-png-930/"+kind+"/"+part);
    check(FirstPersonRegression.data(model,part,"outer",true).length>23*20,"New garment lacks native 3D hand faces");
   }
   Files.write(OUT.resolve("new-arms.csv"),FirstPersonRegression.gold);
   class_318.method_1663(c.method_1522(),im->{try{im.method_4314(OUT.resolve("future.png"));}catch(Exception e){throw new RuntimeException(e);}finally{im.close();}});
  }
  if(tick==40){String pass="PRE30_FUTURE_ASSETS_PASS checks="+checks+" hair=930 beard=930 outfit=930 body=2 classic+slim";Files.writeString(OUT.resolve("pass.txt"),pass);System.out.println(pass);c.method_1490();}
 }catch(Throwable e){System.err.println("PRE30_FUTURE_ASSETS_FAILURE "+e);e.printStackTrace();c.method_1490();}});}
}

