import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import net.minecraft.*;
import dev.tr7zw.skinlayers.api.*;
import dev.tr7zw.skinlayers.versionless.ModBase;
import dev.tr7zw.skinlayers.versionless.config.Config;
import net.tompsen.nexuscharacters.*;

/** Uses the real Minecraft NativeImage, matrices and Skin Layers voxel builder; no game/API stubs. */
public final class RealGeometryTest {
 static Method pixels, meshSet;
 static int checks, vertices;
 static Object invoke(Class<?> c,String n,Object...a)throws Exception {for(Method m:c.getDeclaredMethods())if(m.getName().equals(n)&&m.getParameterCount()==a.length){m.setAccessible(true);return m.invoke(null,a);}throw new NoSuchMethodException(n);}
 static void check(boolean b,String message){checks++;if(!b)throw new AssertionError(message);}
 static int[] read(Object image)throws Exception{return (int[])pixels.invoke(null,image);}
 static Object[] meshes(int[] p)throws Exception{return (Object[])meshSet.invoke(null,(Object)p);}
 static int render(Object mesh)throws Exception {
  if(mesh==null)return 0;
  int[] count={0};
  class_4588 consumer=(class_4588)Proxy.newProxyInstance(class_4588.class.getClassLoader(),new Class<?>[]{class_4588.class},(p,m,a)->{
    if(m.isDefault())return InvocationHandler.invokeDefault(p,m,a);
    if(m.getName().equals("method_22912")){count[0]++;for(int i=0;i<3;i++)check(Float.isFinite(((Number)a[i]).floatValue()),"Non-finite vertex");}
    if(m.getReturnType().isInstance(p))return p;return null;
  });
  ((Mesh)mesh).render(new class_630(List.of(),Map.of()),new class_4587(),consumer,0xf000f0,0,-1);
  check(count[0]>0,"Mesh emitted no vertices");vertices+=count[0];return count[0];
 }
 public static void main(String[] args)throws Exception {
  class_155.method_36208();class_2966.method_12851();
  ModBase.config=new Config();ModBase.config.fastRender=true;
  pixels=GenericSkinLayerSupport.class.getDeclaredMethod("readPixels",Object.class);pixels.setAccessible(true);
  meshSet=GenericSkinLayerSupport.class.getDeclaredMethod("meshSet",int[].class);meshSet.setAccessible(true);
  int hairs=0,beards=0,outfits=0;
  for(String folder:new String[]{"appearance_parts_v064/hair","appearance_parts_v068/facial_hair","appearance_parts_v064/outfits"}){
   for(Path asset:Files.list(Path.of(args[0],"assets/nexuscharacters",folder)).filter(p->p.toString().endsWith(".png") && (!folder.endsWith("/hair") || p.getFileName().toString().matches("hair_(short|long)_[0-9]+\\.png"))).sorted().toList()){
    try(class_1011 image=class_1011.method_4309(Files.newInputStream(asset))){
      int[] raw=read(image), selected;
      if(folder.endsWith("outfits")){selected=GenericLayerPixels.exterior(raw,false);outfits++;}else {selected=GenericLayerPixels.exterior(raw,true);if(folder.endsWith("/hair"))hairs++;else beards++;}
      Object[] set=meshes(selected);int v=0;for(Object mesh:set)v+=render(mesh);
      check(v>0,"Cosmetic vanished: "+asset);
      check(ModBase.config.fastRender,"fastRender setting leaked: "+asset);
      System.out.println("GEOMETRY "+asset.getFileName()+" vertices="+v);
    }
   }
  }
  // New, uncatalogued assets and all six body footprints must work without an index whitelist.
  int[] future=new int[4096];int[][] uv={{40,8},{20,36},{4,36},{4,52},{44,36},{52,52}};
  for(int[] xy:uv)future[xy[1]*64+xy[0]]=0xffabcdef;
  Object[] set=meshes(future);for(int i=0;i<8;i++)check(render(set[i])>0,"Future part missing "+i);
  // Simulate resources added after PRE18: catalog, marker and parser must expand
  // without patching a numeric maximum in any of those consumers.
  Field hairField=DynamicAssetCatalog.class.getDeclaredField("hair");hairField.setAccessible(true);
  Field facialField=DynamicAssetCatalog.class.getDeclaredField("facial");facialField.setAccessible(true);
  Field outfitsField=DynamicAssetCatalog.class.getDeclaredField("outfits");outfitsField.setAccessible(true);
  List<String> futureHair=new ArrayList<>();for(int i=1;i<=5;i++)futureHair.add(String.format("hair_short_%02d.png",i));for(int i=1;i<=9;i++)futureHair.add(String.format("hair_long_%02d.png",i));
  hairField.set(null,List.copyOf(futureHair));
  facialField.set(null,List.of("stubble.png","moustache_short.png","moustache_thick.png","moustache_user_01.png","beard_light.png","beard_light_02.png","future_beard.png"));
  int[] futureOutfits=new int[34];int at=0;for(int i:new int[]{1,3,4,5,6,7,10,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39})futureOutfits[at++]=i;outfitsField.set(null,futureOutfits);
  check(DynamicAssetCatalog.hairCount()==14 && DynamicAssetCatalog.facialHairCount()==7 && DynamicAssetCatalog.outfitCount()==34,"Future catalog counts were clamped");
  check(LongHair3DRenderSupport.hairAssetName(14).equals("hair_long_09.png"),"Future hair was not addressable");
  String futureMarker=DynamicAssetCatalog.withOutfit(Appearance69Support.marker(CharacterRace.HUMAN,2000,1,2,2,1,14,7,4,0,0,1),39);
  check(Appearance69Support.isId(Appearance69Support.markerId(futureMarker)),"Expanded marker was rejected");
  Appearance69Support.Params futureParams=Appearance69Support.parse(Appearance69Support.markerId(futureMarker));
  check(futureParams.hair()==14 && futureParams.facialHair()==7 && futureParams.outfit()==39,"Expanded marker was not preserved");
  DynamicAssetCatalog.clear();
  // Exercise actual patched composition, independently colored hair/beard and existing catalog.
  for(int hair=0;hair<=13;hair++)for(int beard=0;beard<=6;beard++){
   String marker=String.format("player_v69_b1_e1_ec2_ey0_h%02d_hc03_s01_o01_fh%d_fc04_mk0_mc0",hair,beard);
   Object params=Appearance69Support.parse(marker);
   try(class_1011 image=(class_1011)invoke(DynamicAppearanceSupport.class,"compose69",params)){
    check(GenericLayerPixels.count(read(image))>0,"Empty composed skin");
    GenericSkinLayerSupport.complete(image,marker);
   }
  }
  Field plansField=GenericSkinLayerSupport.class.getDeclaredField("PLANS");plansField.setAccessible(true);
  Map<?,?> plans=(Map<?,?>)plansField.get(null);
  check(plans.size()==98,"Not all 14 hair x 7 beard combinations were composed");
  for(Object plan:plans.values()){
    Method cooked=plan.getClass().getDeclaredMethod("cooked");cooked.setAccessible(true);
    Object[] parts=(Object[])cooked.invoke(plan);for(Object mesh:parts)render(mesh);
  }
  ModBase.config.irisCompatibilityMode=true;
  Object plan=plans.values().iterator().next();Method cook=plan.getClass().getDeclaredMethod("cooked");cook.setAccessible(true);
  Object[] compatible=(Object[])cook.invoke(plan);for(Object mesh:compatible)render(mesh);
  check(ModBase.config.fastRender,"Config leaked after compatibility meshes");
  GenericSkinLayerSupport.clearCaches();check(plans.isEmpty(),"Resource reload retained plans");
  System.out.println("REAL_GEOMETRY_PASS hairs="+hairs+" classicBeards="+beards+" outfits="+outfits+" checks="+checks+" vertices="+vertices);
 }
}
