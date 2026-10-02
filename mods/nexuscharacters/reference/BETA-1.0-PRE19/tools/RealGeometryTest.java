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
 static final class PilosityScreen {
  CharacterRace race=CharacterRace.HUMAN;
  int nexuscharacters$facialHair,nexuscharacters$facialColor,nexuscharacters$hairColor=2;
  String nexuscharacters$beardStyle="none";
 }
 static Method pixels, meshSet;
 static int checks, vertices;
 static Object invoke(Class<?> c,String n,Object...a)throws Exception {for(Method m:c.getDeclaredMethods())if(m.getName().equals(n)&&m.getParameterCount()==a.length){m.setAccessible(true);return m.invoke(null,a);}throw new NoSuchMethodException(n);}
 static void check(boolean b,String message){checks++;if(!b)throw new AssertionError(message);}
 static int[] read(Object image)throws Exception{return (int[])pixels.invoke(null,image);}
 static Object[] meshes(int[] p)throws Exception{return meshes(p,false);}
 static Object[] meshes(int[] p,boolean base)throws Exception{return (Object[])meshSet.invoke(null,p,base);}
 static Object member(Object o,String name)throws Exception{for(Class<?> c=o.getClass();c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(name);f.setAccessible(true);return f.get(o);}catch(NoSuchFieldException e){}throw new NoSuchFieldException(name);}
 static float minimumZ(Object mesh)throws Exception{
   float min=0;float[] data=(float[])member(mesh,"polygonData");
   for(int q=0;q<data.length;q+=23)for(int v=0;v<4;v++)min=Math.min(min,data[q+5+v*5]*16f);
   for(Object cube:(List<?>)member(mesh,"cubes"))for(Object polygon:(Object[])member(cube,"field_3649")){
     Method vertices=polygon.getClass().getMethod("comp_3184");
     for(Object vertex:(Object[])vertices.invoke(polygon))min=Math.min(min,(Float)vertex.getClass().getMethod("comp_4806").invoke(vertex));
   }
   return min;
 }
 static void geometryBounds(Object[] set,boolean base)throws Exception{
   for(int part=0;part<set.length;part++)if(set[part]!=null){
     float[] data=(float[])member(set[part],"polygonData");
     for(int q=0;q<data.length;q+=23)for(int v=0;v<4;v++){
       float x=data[q+3+v*5]*16f,y=data[q+4+v*5]*16f;
       if(part==1){float depth=base?0.30f:0.70f;check(x>=-4f-depth-0.0001f&&x<=4f+depth+0.0001f,"Torso exceeds jacket bounds");check(y<12f,"Torso bottom is coplanar with base");}
       if(part==2)check(x>=-2.0001f,"Left leg enters right leg");
       if(part==3)check(x<=2.0001f,"Right leg enters left leg");
       if(part==4)check(x>=-2.0001f,"Left sleeve enters torso");
       if(part==5)check(x<=2.0001f,"Right sleeve enters torso");
       if(part==6)check(x>=-1.5001f,"Slim left sleeve enters torso");
       if(part==7)check(x<=1.5001f,"Slim right sleeve enters torso");
       if(part==2||part==3)check(y>=-0.0001f,"Leg enters torso");
     }
   }
 }
 static void uvRegression()throws Exception{
   try(class_1011 out=new class_1011(64,64,false);class_1011 src=new class_1011(64,64,false)){
     for(int y=0;y<64;y++)for(int x=0;x<64;x++){out.method_61941(x,y,0);src.method_61941(x,y,0);}
     src.method_61941(8,8,0xffd02010);src.method_61941(40,8,0xff1020d0);
     Field assets=GenericSkinLayerSupport.class.getDeclaredField("ASSETS");assets.setAccessible(true);
     ((Map<Object,String>)assets.get(null)).put(src,"/assets/nexuscharacters/appearance_parts_v064/hair/hair_99.png");
     Class<?> modes=Class.forName("net.tompsen.nexuscharacters.DynamicAppearanceSupport$TintMode");
     Object none=Enum.valueOf((Class)modes,"NONE");
     GenericSkinLayerSupport.overlay(out,src,0,none,0,0);
     check(out.method_61940(8,8)==0xffd02010,"Base UV colour changed");
     check(out.method_61940(40,8)==0xff1020d0,"Outer UV colour changed");
     GenericSkinLayerSupport.complete(out,"uv-regression");
     Field plans=GenericSkinLayerSupport.class.getDeclaredField("PLANS");plans.setAccessible(true);
     Object plan=((Map<?,?>)plans.get(null)).get("nexuscharacters:dynamic/appearance/uv-regression");
     int[] base=(int[])member(plan,"base"),outer=(int[])member(plan,"outer");
     check(base[8*64+8]==0xffd02010 && outer[8*64+40]==0xff1020d0,"Distinct layer pixels lost");
     for(boolean compatibility:new boolean[]{false,true}){
       ModBase.config.irisCompatibilityMode=compatibility;
       float bz=minimumZ(meshes(base,true)[0]),oz=minimumZ(meshes(outer,false)[0]);
       check(bz<-4.29f && oz<-4.69f && oz<bz-0.39f,"Base and outer meshes are coplanar in compatibility="+compatibility);
     }
     ModBase.config.irisCompatibilityMode=false;
   }
   try(class_1011 hd=new class_1011(128,128,false)){
     for(int y=0;y<128;y++)for(int x=0;x<128;x++)hd.method_61941(x,y,0xffabc123);
     check(read(hd)[8*64+40]==0xffabc123,"128x128 texture rejected");
   }
   GenericSkinLayerSupport.clearCaches();
   System.out.println("UV_LAYERS_AND_HD_PASS");
 }
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
  meshSet=GenericSkinLayerSupport.class.getDeclaredMethod("meshSet",int[].class,boolean.class);meshSet.setAccessible(true);
  uvRegression();
  int hairs=0,beards=0,outfits=0;
  for(String folder:new String[]{"appearance_parts_v064/hair","appearance_parts_v068/facial_hair","appearance_parts_v064/outfits"}){
   for(Path asset:Files.list(Path.of(args[0],"assets/nexuscharacters",folder)).filter(p->p.toString().endsWith(".png") && (!folder.endsWith("/hair") || p.getFileName().toString().matches("hair_(short|long)_[0-9]+\\.png"))).sorted().toList()){
    try(class_1011 image=class_1011.method_4309(Files.newInputStream(asset))){
      int[] raw=read(image);
      if(folder.endsWith("outfits")){outfits++;}else {if(folder.endsWith("/hair"))hairs++;else beards++;}
      Object[] set=meshes(raw),base=meshes(raw,true);int v=0;for(Object mesh:set)v+=render(mesh);for(Object mesh:base)v+=render(mesh);
      geometryBounds(set,false);geometryBounds(base,true);
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
  Map<Integer,String> futureHair=new LinkedHashMap<>();for(int i=1;i<=5;i++)futureHair.put(i,String.format("hair_short_%02d.png",i));for(int i=1;i<=9;i++)futureHair.put(5+i,String.format("hair_long_%02d.png",i));futureHair.put(1006,"hair_short_06.png");
  hairField.set(null,Collections.unmodifiableMap(futureHair));
  Map<Integer,String> futureFacial=new LinkedHashMap<>();for(int i=0;i<6;i++)futureFacial.put(i+1,List.of("stubble.png","moustache_short.png","moustache_thick.png","moustache_user_01.png","beard_light.png","beard_light_02.png").get(i));futureFacial.put(7,"facial_07.png");futureFacial.put(9,"facial_09.png");
  facialField.set(null,Collections.unmodifiableMap(futureFacial));
  int[] futureOutfits=new int[34];int at=0;for(int i:new int[]{1,3,4,5,6,7,10,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39})futureOutfits[at++]=i;outfitsField.set(null,futureOutfits);
  check(DynamicAssetCatalog.hairCount()==15 && DynamicAssetCatalog.facialHairCount()==8 && DynamicAssetCatalog.outfitCount()==34,"Future catalog counts were clamped");
  check(LongHair3DRenderSupport.hairAssetName(14).equals("hair_long_09.png"),"Future hair was not addressable");
  check(DynamicAssetCatalog.nextHair(13)==14 && DynamicAssetCatalog.nextHair(14)==1006 && DynamicAssetCatalog.nextHair(1006)==0,"Sparse stable hair IDs were not cycled");
  check(LongHair3DRenderSupport.hairAssetName(1006).equals("hair_short_06.png"),"New short hair shifted an existing saved ID");
  check(FacialHairRaceSupport.next(CharacterRace.HUMAN,7)==9 && FacialHairRaceSupport.normalize(CharacterRace.HUMAN,9)==9,"Sparse beard IDs were not preserved");
  PilosityScreen screen=new PilosityScreen();screen.nexuscharacters$facialHair=7;UnifiedPilositySupport.cycle(screen);
  check(screen.nexuscharacters$facialHair==9,"Unified button skipped the future beard");
  UnifiedPilositySupport.fix(screen);check(screen.nexuscharacters$facialHair==9,"Unified refresh clamped the future beard");
  UnifiedPilositySupport.cycle(screen);check(screen.nexuscharacters$facialHair==0 && !CulturalBeardSupport.baseStyle(screen.nexuscharacters$beardStyle).equals("none"),"Modeled beard transition was broken");
  screen.race=CharacterRace.DWARF;screen.nexuscharacters$beardStyle="none";UnifiedPilositySupport.fix(screen);
  for(int i=0;i<20;i++){UnifiedPilositySupport.cycle(screen);check(screen.nexuscharacters$facialHair==0 && !CulturalBeardSupport.baseStyle(screen.nexuscharacters$beardStyle).equals("none"),"Dwarf lost its mandatory modeled beard");}
  String futureMarker=DynamicAssetCatalog.withOutfit(Appearance69Support.marker(CharacterRace.HUMAN,2000,1,2,2,1,1006,9,4,0,0,1),39);
  check(Appearance69Support.isId(Appearance69Support.markerId(futureMarker)),"Expanded marker was rejected");
  Appearance69Support.Params futureParams=Appearance69Support.parse(Appearance69Support.markerId(futureMarker));
  check(futureParams.hair()==1006 && futureParams.facialHair()==9 && futureParams.outfit()==39,"Expanded marker was not preserved");
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
