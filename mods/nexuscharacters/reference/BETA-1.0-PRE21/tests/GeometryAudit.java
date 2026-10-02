import java.lang.reflect.*;
import java.util.*;
import net.minecraft.class_4587;
import net.tompsen.nexuscharacters.*;
import dev.tr7zw.skinlayers.api.*;
import dev.tr7zw.skinlayers.SkinLayersModBase;
import org.joml.Vector3f;

/** Audits the actual library geometry after native offsets, on the render thread. */
public final class GeometryAudit {
 static long vertices,faces;static int combinations;
 static final String[] offsets={"HEAD","BODY","LEFT_LEG","RIGHT_LEG","LEFT_ARM","RIGHT_ARM","LEFT_ARM_SLIM","RIGHT_ARM_SLIM"};
 static Object field(Object o,String name)throws Exception{for(Class<?> c=o.getClass();c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(name);f.setAccessible(true);return f.get(o);}catch(NoSuchFieldException e){}throw new NoSuchFieldException(name);}
 static Method method(Class<?> c,String name,Class<?>...types)throws Exception{Method m=c.getDeclaredMethod(name,types);m.setAccessible(true);return m;}
 static void require(boolean value,String message){if(!value)throw new AssertionError(message);}
 static void run()throws Exception{
  boolean previous=SkinLayersModBase.config.irisCompatibilityMode;
  try{
   for(boolean compatibility:new boolean[]{false,true}){
    SkinLayersModBase.config.irisCompatibilityMode=compatibility;
    int[] outfits={1,3,4,5,6,7,10,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39};
    for(int outfit:outfits){
     for(int hair=1;hair<=14;hair++)auditMarker(hair,outfit,0);
     for(int beard:new int[]{1,2,3,4,5,6,7,9})auditMarker(10,outfit,beard);
    }
   }
  }finally{SkinLayersModBase.config.irisCompatibilityMode=previous;}
  int[] sample=new int[4096];sample[8*64+8]=0xff123456;sample[8*64+40]=0xffabcdef;
  int[] prepared=GenericLayerPixels.prepareCosmetic(sample,true,false);
  require(prepared[8*64+8]==sample[8*64+8] && prepared[8*64+40]==sample[8*64+40],"Original base/outer colors changed");
  require(prepared[8*64+41]==0,"Transparent hole was filled");
  sample[8*64+40]=0;prepared=GenericLayerPixels.prepareCosmetic(sample,false,false);
  require(prepared[8*64+40]==sample[8*64+8],"New base-only outfit was not promoted");
  System.out.println("GEOMETRY_AUDIT_PASS combinations="+combinations+" vertices="+vertices+" faces="+faces+" wide+slim=true compatibility=true");
 }
 static void auditMarker(int hair,int outfit,int beard)throws Exception{
  String marker=String.format(Locale.ROOT,"player_v69_b1_e1_ec2_ey2_h%02d_hc02_s05_o%02d_fh%d_fc04_mk0_mc0",hair,outfit,beard);
  require(DynamicAppearanceSupport.textures(marker)!=null,"Texture composition failed: "+marker);
  Object plan=((Map<?,?>)fieldStatic(GenericSkinLayerSupport.class,"PLANS")).get("nexuscharacters:dynamic/appearance/"+marker);
  require(plan!=null,"Missing plan: "+marker);
  int[][] layers=(int[][])field(plan,"layers");
  for(int category=0;category<layers.length;category++){
   Object[] meshes=(Object[])method(GenericSkinLayerSupport.class,"meshSet",int[].class,boolean.class,int.class).invoke(null,layers[category],false,category);
   for(int part=0;part<meshes.length;part++)if(meshes[part]!=null)auditMesh(meshes[part],part,marker+" category="+category);
  }
  combinations++;
 }
 static Object fieldStatic(Class<?> c,String name)throws Exception{Field f=c.getDeclaredField(name);f.setAccessible(true);return f.get(null);}
 static void auditMesh(Object mesh,int part,String marker)throws Exception{
  class_4587 stack=new class_4587();
  ((OffsetProvider)OffsetProvider.class.getField(offsets[part]).get(null)).applyOffset(stack,(Mesh)mesh);
  mesh.getClass().getMethod("translateAndRotate",class_4587.class).invoke(mesh,stack);
  Set<String> seen=new HashSet<>();
  float[] data=(float[])field(mesh,"polygonData");
  for(int q=0;q<data.length;q+=23){float[][] points=new float[4][3];for(int i=0;i<4;i++)for(int a=0;a<3;a++)points[i][a]=data[q+3+i*5+a];auditFace(points,stack,part,marker,seen);}
  for(Object cube:(List<?>)field(mesh,"cubes"))for(Object polygon:(Object[])field(cube,"field_3649")){
   Object[] vs=(Object[])polygon.getClass().getMethod("comp_3184").invoke(polygon);float[][] points=new float[4][3];
   for(int i=0;i<4;i++)for(int a=0;a<3;a++)points[i][a]=((Number)vs[i].getClass().getMethod(new String[]{"comp_4804","comp_4805","comp_4806"}[a]).invoke(vs[i])).floatValue()/16f;
   auditFace(points,stack,part,marker,seen);
  }
 }
 static void auditFace(float[][] points,class_4587 stack,int part,String marker,Set<String> seen){
  List<String> key=new ArrayList<>();float[] lo={Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY},hi={-Float.POSITIVE_INFINITY,-Float.POSITIVE_INFINITY,-Float.POSITIVE_INFINITY};
  for(float[] p:points){
   Vector3f v=stack.method_23760().method_23761().transformPosition(new Vector3f(p[0],p[1],p[2]));v.mul(16f);
   float pivot=part==2?1.9f:part==3?-1.9f:part==4||part==6?5f:part==5||part==7?-5f:0f;
   v.x+=pivot;v.y+=part==2||part==3?12f:part>=4?2f:0f;
   float[] coords={v.x,v.y,v.z};for(int a=0;a<3;a++){require(Float.isFinite(coords[a]),"Nonfinite vertex");lo[a]=Math.min(lo[a],coords[a]);hi[a]=Math.max(hi[a],coords[a]);}
   if(part==0)require(v.y<=-0.0248f,"Head intersects torso "+marker);
   if(part==1)require(v.y>=0.0248f && v.y<=11.9752f,"Torso intersects head/legs "+marker);
   if(part==2)require(v.x>=0.0248f && v.y>=12.0248f,"Left leg joint overlap "+marker);
   if(part==3)require(v.x<=-0.0248f && v.y>=12.0248f,"Right leg joint overlap "+marker);
   float edge=4f*SkinLayersModBase.config.bodyVoxelWidthSize+0.0248f;
   if(part==4||part==6)require(v.x>=edge,"Left sleeve intersects jacket "+marker);
   if(part==5||part==7)require(v.x<=-edge,"Right sleeve intersects jacket "+marker);
   key.add(Math.round(v.x*10000)+","+Math.round(v.y*10000)+","+Math.round(v.z*10000));vertices++;
  }
  Vector3f ab=new Vector3f(points[1][0]-points[0][0],points[1][1]-points[0][1],points[1][2]-points[0][2]);
  Vector3f ac=new Vector3f(points[2][0]-points[0][0],points[2][1]-points[0][1],points[2][2]-points[0][2]);
  Vector3f ad=new Vector3f(points[3][0]-points[0][0],points[3][1]-points[0][1],points[3][2]-points[0][2]);
  if(new Vector3f(ab).cross(ac).lengthSquared()+new Vector3f(ac).cross(ad).lengthSquared()<1e-16f)return;
  Collections.sort(key);require(seen.add(key.toString()),"Duplicate overlapping face "+marker+" part="+part+" vertices="+key);
  // A face wholly coplanar with a flat base face is a genuine z-fighting candidate.
  for(int a=0;a<3;a++)if(hi[a]-lo[a]<0.00001f){
   float f=lo[a];float[] basePlanes;
   if(a==2)basePlanes=part==0?new float[]{-4,4}:new float[]{-2,2};
   else if(a==1)basePlanes=part==0?new float[]{-8,0}:part==1?new float[]{0,12}:part==2||part==3?new float[]{12,24}:new float[]{0,12};
   else basePlanes=part==0||part==1?new float[]{-4,4}:part==2?new float[]{-0.1f,3.9f}:part==3?new float[]{-3.9f,0.1f}:part==4?new float[]{4,8}:part==5?new float[]{-8,-4}:part==6?new float[]{4,7}:new float[]{-7,-4};
   for(float plane:basePlanes)require(Math.abs(f-plane)>0.00001f,"Outer face coplanar with base "+marker+" part="+part+" axis="+a+" value="+f);
  }
  faces++;
 }
}
