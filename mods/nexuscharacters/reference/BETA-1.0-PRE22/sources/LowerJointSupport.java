package net.tompsen.nexuscharacters;

import java.lang.reflect.*;
import java.util.*;

/** Keeps original UVs and isolates modified cube copies to Nexus player models. */
public final class LowerJointSupport {
 private static final String[] PARTS={"field_3398","field_3391","field_3397","field_3392","field_27433","field_3401"};
 private static final Map<Object,Original> ORIGINALS=Collections.synchronizedMap(new WeakHashMap<>());
 private record Original(List<?> cubes,Map<Float,List<?>> variants){}
 public static void apply(Object model,boolean active)throws Exception {
  float bodyWidth=1.05f;
  try{bodyWidth=((Number)field(field(Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase"),"config"),"bodyVoxelWidthSize")).floatValue();}catch(Exception ignored){}
  for(int index=0;index<PARTS.length;index++){
   Object part=field(model,PARTS[index]);Original original=ORIGINALS.get(part);
   if(original==null){if(!active)continue;original=new Original((List<?>)field(part,"field_3663"),new HashMap<>());ORIGINALS.put(part,original);}
   List<?> cubes=original.cubes();
   if(active){List<?> cached=original.variants().get(bodyWidth);if(cached==null){cached=copy(cubes,index,bodyWidth);original.variants().put(bodyWidth,cached);}cubes=cached;}
   set(part,"field_3663",cubes);
  }
 }
 private static List<?> copy(List<?> cubes,int part,float width)throws Exception {
  List<Object> result=new ArrayList<>();
  Class<?> cubeType=Class.forName("net.minecraft.class_630$class_628"),vertexType=Class.forName("net.minecraft.class_630$class_618"),polygonType=Class.forName("net.minecraft.class_630$class_593");
  Constructor<?> cubeCtor=cubeType.getConstructors()[0],vertexCtor=vertexType.getConstructor(float.class,float.class,float.class,float.class,float.class);
  Constructor<?> polygonCtor=polygonType.getConstructor(Array.newInstance(vertexType,0).getClass(),Class.forName("org.joml.Vector3fc"));
  for(Object cube:cubes){
   float x=((Number)field(cube,"field_3645")).floatValue(),y=((Number)field(cube,"field_3644")).floatValue(),z=((Number)field(cube,"field_3643")).floatValue();
   Object duplicate=cubeCtor.newInstance(0,0,x,y,z,((Number)field(cube,"field_3648")).floatValue()-x,((Number)field(cube,"field_3647")).floatValue()-y,((Number)field(cube,"field_3646")).floatValue()-z,0f,0f,0f,false,64f,64f,Set.of());
   Object[] originalPolys=(Object[])field(cube,"field_3649");Object[] polygons=(Object[])Array.newInstance(polygonType,originalPolys.length);
   for(int i=0;i<polygons.length;i++){
    Object[] originalVertices=(Object[])call(originalPolys[i],"comp_3184");Object[] vertices=(Object[])Array.newInstance(vertexType,originalVertices.length);
    for(int v=0;v<vertices.length;v++){
     Object old=originalVertices[v];float[] p=new float[3];for(int a=0;a<3;a++)p[a]=((Number)call(old,new String[]{"comp_4804","comp_4805","comp_4806"}[a])).floatValue();
     // Half the upper-layer clearance: the two sets of joint faces must
     // themselves lie on different planes, while preserving native UVs.
     if(part==0)p[1]=Math.min(p[1],-.0125f);
     if(part==1){p[1]=Math.max(p[1],.0125f);p[1]=Math.min(p[1],11.9875f);}
     if(part==2){p[0]=Math.max(p[0],-1.8875f);p[1]=Math.max(p[1],.0125f);}
     if(part==3){p[0]=Math.min(p[0],1.8875f);p[1]=Math.max(p[1],.0125f);}
     float edge=4f*width+.0125f-5f;
     if(part==4)p[0]=Math.max(p[0],edge);
     if(part==5)p[0]=Math.min(p[0],-edge);
     vertices[v]=vertexCtor.newInstance(p[0],p[1],p[2],((Number)call(old,"comp_3187")).floatValue(),((Number)call(old,"comp_3188")).floatValue());
    }
    polygons[i]=polygonCtor.newInstance(vertices,call(originalPolys[i],"comp_3185"));
   }
   set(duplicate,"field_3649",polygons);
   // Sodium renders its own cached cuboid and bypasses the vanilla polygon
   // records. Keep its original face order, UVs and culling, and clip that
   // representation too. No references in the original cuboid are changed.
   try{
    Object source=field(cube,"sodium$cuboid"),target=field(duplicate,"sodium$cuboid");
    for(String n:new String[]{"positions","normals","textures","cullMask"})set(target,n,field(source,n));
    for(int a=0;a<3;a++){
     String suffix=new String[]{"X","Y","Z"}[a];float start=((Number)field(source,"origin"+suffix)).floatValue()*16f,end=start+((Number)field(source,"size"+suffix)).floatValue()*16f;
     start=coordinate(start,part,a,width);end=coordinate(end,part,a,width);
     set(target,"origin"+suffix,start/16f);set(target,"size"+suffix,(end-start)/16f);
    }
   }catch(NoSuchFieldException ignored){}
   result.add(duplicate);
  }
  return List.copyOf(result);
 }
 private static float coordinate(float p,int part,int axis,float width){
  if(axis==1){if(part==0)return Math.min(p,-.0125f);if(part==1)return Math.max(.0125f,Math.min(p,11.9875f));if(part==2||part==3)return Math.max(p,.0125f);}
  if(axis==0){if(part==2)return Math.max(p,-1.8875f);if(part==3)return Math.min(p,1.8875f);float edge=4f*width+.0125f-5f;if(part==4)return Math.max(p,edge);if(part==5)return Math.min(p,-edge);}
  return p;
 }
 /** Synchronize the cached Sodium geometry after native voxel faces change. */
 public static void syncSodium(Object cube)throws Exception {
  Object cached;try{cached=field(cube,"sodium$cuboid");}catch(NoSuchFieldException absent){return;}
  float[] lo={Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY},hi={-Float.POSITIVE_INFINITY,-Float.POSITIVE_INFINITY,-Float.POSITIVE_INFINITY};int mask=0;
  int[] normals=(int[])field(cached,"normals");
  for(Object polygon:(Object[])field(cube,"field_3649")){
   Object normal=call(polygon,"comp_3185");float nx=((Number)call(normal,"x")).floatValue(),ny=((Number)call(normal,"y")).floatValue(),nz=((Number)call(normal,"z")).floatValue();
   int face=Math.abs(ny)>.9f?(ny<0?0:1):Math.abs(nx)>.9f?(nx<0?2:4):(nz<0?3:5);
   for(int f=0;f<normals.length;f++)if(normals[f]==face)mask|=1<<f;
   for(Object vertex:(Object[])call(polygon,"comp_3184"))for(int a=0;a<3;a++){
    float p=((Number)call(vertex,new String[]{"comp_4804","comp_4805","comp_4806"}[a])).floatValue();lo[a]=Math.min(lo[a],p);hi[a]=Math.max(hi[a],p);
   }
  }
  set(cached,"cullMask",mask);
  if(mask==0)return;
  for(int a=0;a<3;a++){
   String suffix=new String[]{"X","Y","Z"}[a];boolean reversed=((Number)field(cached,"size"+suffix)).floatValue()<0;
   set(cached,"origin"+suffix,(reversed?hi[a]:lo[a])/16f);set(cached,"size"+suffix,(reversed?lo[a]-hi[a]:hi[a]-lo[a])/16f);
  }
 }
 private static Object call(Object o,String name)throws Exception{return o.getClass().getMethod(name).invoke(o);}
 private static Object field(Object o,String name)throws Exception {Class<?> type=o instanceof Class<?> c?c:o.getClass();for(Class<?> c=type;c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(name);f.setAccessible(true);return f.get(o instanceof Class<?>?null:o);}catch(NoSuchFieldException ignored){}throw new NoSuchFieldException(name);}
 private static void set(Object o,String name,Object value)throws Exception{for(Class<?> c=o.getClass();c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(name);f.setAccessible(true);f.set(o,value);return;}catch(NoSuchFieldException ignored){}throw new NoSuchFieldException(name);}
}
