package net.tompsen.nexuscharacters;
import java.lang.reflect.*;import java.util.*;
/** Removes partial coplanar intersections, then stores every surviving face
 * as a one-face vanilla cube so Sodium cannot expand it to another face's bounds. */
public final class CoplanarSurfaceSupport {
 static final float EPS=0.000001f;
 static final String[] XYZ={"comp_4804","comp_4805","comp_4806"};
 record Face(float[] data,int axis,int u,int v,float loU,float hiU,float loV,float hiV){
  static Face of(float[] d){int axis=-1;for(int a=0;a<3;a++){float lo=d[3+a],hi=lo;for(int i=1;i<4;i++){lo=Math.min(lo,d[3+i*5+a]);hi=Math.max(hi,d[3+i*5+a]);}if(hi-lo<EPS)axis=a;}
   if(axis<0)throw new IllegalArgumentException("Non-axis-aligned native face");int u=(axis+1)%3,v=(axis+2)%3;float lu=d[3+u],hu=lu,lv=d[3+v],hv=lv;for(int i=1;i<4;i++){lu=Math.min(lu,d[3+i*5+u]);hu=Math.max(hu,d[3+i*5+u]);lv=Math.min(lv,d[3+i*5+v]);hv=Math.max(hv,d[3+i*5+v]);}return new Face(d,axis,u,v,lu,hu,lv,hv);}
  Face rect(float lu,float hu,float lv,float hv){float[] d=data.clone();float pU=data[3+u],pV=data[3+v];float e1U=data[8+u]-pU,e1V=data[8+v]-pV,e3U=data[18+u]-pU,e3V=data[18+v]-pV;float det=e1U*e3V-e3U*e1V;
   for(int i=0;i<4;i++){int at=3+i*5;float nu=Math.abs(data[at+u]-loU)<EPS?lu:hu,nv=Math.abs(data[at+v]-loV)<EPS?lv:hv;float t=((nu-pU)*e3V-(nv-pV)*e3U)/det,s=(e1U*(nv-pV)-e1V*(nu-pU))/det;d[at+u]=nu;d[at+v]=nv;for(int tex=3;tex<5;tex++)d[at+tex]=data[3+tex]+t*(data[8+tex]-data[3+tex])+s*(data[18+tex]-data[3+tex]);}return Face.of(d);}
  List<Face> minus(float lu,float hu,float lv,float hv){List<Face> out=new ArrayList<>(4);if(lu-loU>EPS)out.add(rect(loU,lu,loV,hiV));if(hiU-hu>EPS)out.add(rect(hu,hiU,loV,hiV));if(lv-loV>EPS)out.add(rect(lu,hu,loV,lv));if(hiV-hv>EPS)out.add(rect(lu,hu,hv,hiV));return out;}
 }
 public static void clean(Object mesh)throws Exception {
  List<Face> input=new ArrayList<>();float[] data=(float[])field(mesh,"polygonData");for(int i=0;i<data.length;i+=23)input.add(Face.of(Arrays.copyOfRange(data,i,i+23)));
  for(Object cube:(List<?>)field(mesh,"cubes"))for(Object poly:(Object[])field(cube,"field_3649")){
   float[] d=new float[23];Object normal=call(poly,"comp_3185");for(int a=0;a<3;a++)d[a]=((Number)call(normal,new String[]{"x","y","z"}[a])).floatValue();Object[] vs=(Object[])call(poly,"comp_3184");for(int i=0;i<4;i++){for(int a=0;a<3;a++)d[3+i*5+a]=((Number)call(vs[i],XYZ[a])).floatValue()/16f;d[6+i*5]=((Number)call(vs[i],"comp_3187")).floatValue();d[7+i*5]=((Number)call(vs[i],"comp_3188")).floatValue();}input.add(Face.of(d));
  }
  Map<String,List<Face>> groups=new LinkedHashMap<>();
  for(Face f:input){if(f.hiU-f.loU<EPS||f.hiV-f.loV<EPS)continue;String key=f.axis+":"+Math.round(f.data[3+f.axis]/EPS);List<Face> accepted=groups.computeIfAbsent(key,k->new ArrayList<>());List<Face> pending=new ArrayList<>();pending.add(f);
   for(int ai=0;ai<accepted.size()&&!pending.isEmpty();ai++){Face old=accepted.get(ai);List<Face> next=new ArrayList<>();List<Face> oldPieces=new ArrayList<>();oldPieces.add(old);
    for(Face current:pending){float lu=Math.max(old.loU,current.loU),hu=Math.min(old.hiU,current.hiU),lv=Math.max(old.loV,current.loV),hv=Math.min(old.hiV,current.hiV);
     if(hu-lu<EPS||hv-lv<EPS){next.add(current);continue;}next.addAll(current.minus(lu,hu,lv,hv));float dot=0;for(int a=0;a<3;a++)dot+=old.data[a]*current.data[a];if(dot<0){List<Face> remains=new ArrayList<>();for(Face piece:oldPieces){float a=Math.max(piece.loU,lu),b=Math.min(piece.hiU,hu),c=Math.max(piece.loV,lv),e=Math.min(piece.hiV,hv);if(b-a<EPS||e-c<EPS)remains.add(piece);else remains.addAll(piece.minus(a,b,c,e));}oldPieces=remains;}
    }pending=next;if(oldPieces.size()!=1||oldPieces.get(0)!=old){accepted.remove(ai);accepted.addAll(ai,oldPieces);ai+=oldPieces.size()-1;}
   }accepted.addAll(pending);
  }
  Object config=Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase").getField("config").get(null);
  boolean compatibility=config.getClass().getField("irisCompatibilityMode").getBoolean(config)||config.getClass().getField("applySodiumWorkaround").getBoolean(config);
  if(compatibility){List<Object> cubes=new ArrayList<>();for(List<Face> group:groups.values())for(Face f:group)cubes.add(cube(f));set(mesh,"polygonData",new float[0]);set(mesh,"cubes",List.copyOf(cubes));}
  else{int count=groups.values().stream().mapToInt(List::size).sum();float[] clean=new float[count*23];int at=0;for(List<Face> group:groups.values())for(Face f:group){System.arraycopy(f.data,0,clean,at,23);at+=23;}set(mesh,"polygonData",clean);set(mesh,"cubes",List.of());}
 }
 static Object cube(Face f)throws Exception {
  float[] d=f.data,lo={d[3],d[4],d[5]},hi=lo.clone();for(int i=1;i<4;i++)for(int a=0;a<3;a++){lo[a]=Math.min(lo[a],d[3+i*5+a]);hi[a]=Math.max(hi[a],d[3+i*5+a]);}
  Class<?> ct=Class.forName("net.minecraft.class_630$class_628"),vt=Class.forName("net.minecraft.class_630$class_618"),pt=Class.forName("net.minecraft.class_630$class_593");Object c=ct.getConstructors()[0].newInstance(0,0,lo[0]*16,lo[1]*16,lo[2]*16,(hi[0]-lo[0])*16,(hi[1]-lo[1])*16,(hi[2]-lo[2])*16,0f,0f,0f,false,64f,64f,Set.of());Object[] vs=(Object[])Array.newInstance(vt,4);Constructor<?> vc=vt.getConstructor(float.class,float.class,float.class,float.class,float.class);for(int i=0;i<4;i++)vs[i]=vc.newInstance(d[3+i*5]*16,d[4+i*5]*16,d[5+i*5]*16,d[6+i*5],d[7+i*5]);Object normal=Class.forName("org.joml.Vector3f").getConstructor(float.class,float.class,float.class).newInstance(d[0],d[1],d[2]);Object poly=pt.getConstructor(vs.getClass(),Class.forName("org.joml.Vector3fc")).newInstance(vs,normal);Object[] polys=(Object[])Array.newInstance(pt,1);polys[0]=poly;set(c,"field_3649",polys);
  try{Object cached=field(c,"sodium$cuboid");int face=Math.abs(d[1])>.9f?(d[1]<0?0:1):Math.abs(d[0])>.9f?(d[0]<0?2:4):(d[2]<0?3:5);set(cached,"cullMask",1<<face);int[] positions=(int[])field(cached,"positions");long[] tex=(long[])field(cached,"textures");int[] bx={0,1,1,0,0,1,1,0},by={0,0,1,1,0,0,1,1},bz={0,0,0,0,1,1,1,1};for(int i=0;i<4;i++){int vi=positions[face*4+i];float[] pos={bx[vi]==0?lo[0]:hi[0],by[vi]==0?lo[1]:hi[1],bz[vi]==0?lo[2]:hi[2]};int best=0;float dist=Float.POSITIVE_INFINITY;for(int v=0;v<4;v++){float delta=0;for(int a=0;a<3;a++){float x=pos[a]-d[3+v*5+a];delta+=x*x;}if(delta<dist){dist=delta;best=v;}}tex[face*4+i]=((long)Float.floatToRawIntBits(d[7+best*5])<<32)|(Float.floatToRawIntBits(d[6+best*5])&0xffffffffL);}}
  catch(NoSuchFieldException absent){}return c;
 }
 static Object call(Object o,String n)throws Exception{return o.getClass().getMethod(n).invoke(o);}
 static Object field(Object o,String n)throws Exception{for(Class<?> c=o.getClass();c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(n);f.setAccessible(true);return f.get(o);}catch(NoSuchFieldException ignored){}throw new NoSuchFieldException(n);}
 static void set(Object o,String n,Object v)throws Exception{for(Class<?> c=o.getClass();c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(n);f.setAccessible(true);f.set(o,v);return;}catch(NoSuchFieldException ignored){}throw new NoSuchFieldException(n);}
}
