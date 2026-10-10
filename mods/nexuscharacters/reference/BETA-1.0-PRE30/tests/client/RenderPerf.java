import java.lang.reflect.*;
import java.util.*;
import java.nio.file.*;
import java.security.*;
import java.lang.management.ManagementFactory;
import dev.tr7zw.skinlayers.accessor.*;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;
import org.joml.*;
/** Run on the real Minecraft render thread, after real preview textures/meshes have been cooked. */
public final class RenderPerf {
 static final boolean BASE=Boolean.getBoolean("pre30.baseline");
 static final String[] LOW={"field_3398","field_3391","field_3397","field_3392","field_27433","field_3401"},UP={"field_3394","field_3483","field_3482","field_3479","field_3484","field_3486"};
 static final Path OUT=Path.of("pre30/qa/"+(BASE?"baseline":"candidate"));
 static int checks;
 static void check(boolean c,String t){checks++;if(!c)throw new AssertionError(t);}
 static Object get(Object target,String name)throws Exception{return SurfaceGeometry.field(target,name);}
 static Object state(Object model)throws Exception{return ((Map<?,?>)get(PosedSurfaceSupport.class,"STATES")).get(model);}
 static String geometry(Object model)throws Exception{
  Object s=state(model);MessageDigest hash=MessageDigest.getInstance("SHA-256");
  for(String group:List.of("base","outer"))for(Object mesh:(Object[])get(s,group)){
   float[] data=(float[])get(mesh,"polygonData");List<String> polygons=new ArrayList<>();
   for(int i=0;i<data.length;i+=23){StringBuilder p=new StringBuilder();for(int j=0;j<23;j++)p.append(HexFormat.of().toHexDigits(Float.floatToIntBits(data[i+j])));polygons.add(p.toString());}
   Collections.sort(polygons);hash.update((group+"/"+polygons.size()+":").getBytes(java.nio.charset.StandardCharsets.UTF_8));for(String p:polygons)hash.update(p.getBytes(java.nio.charset.StandardCharsets.US_ASCII));
  }
  return HexFormat.of().formatHex(hash.digest());
 }
 static void apply(Object model,Object[] cooked,boolean slim,boolean hide,boolean first)throws Exception{
  if(first)PosedSurfaceSupport.firstPerson(model,cooked,slim);else PosedSurfaceSupport.apply(model,cooked,slim,hide);
 }
 static void pose(Object model,int frame)throws Exception{
  for(int p=0;p<6;p++){
   class_630 low=(class_630)get(model,LOW[p]),up=(class_630)get(model,UP[p]);
   low.field_3654=(float)java.lang.Math.sin(frame*.071+p)*.6f;
   low.field_3675=(float)java.lang.Math.cos(frame*.043+p)*.15f;
   low.field_3674=(float)java.lang.Math.sin(frame*.061+p)*.09f;
   low.field_37938=.9f+(frame%5)*.05f;low.field_37939=.8f+(frame%7)*.055f;low.field_37940=1.05f;
   up.field_3654=.01f*(frame%4);up.field_3675=.015f*(frame%3);up.field_3674=-.01f*(frame%5);
  }
 }
 static List<String> results=new ArrayList<>();
 static void measurePreview(com.sun.management.ThreadMXBean bean,Object widget,CharacterDto dto)throws Exception{
  for(int i=0;i<2000;i++)GenericSkinLayerSupport.preview(widget,dto);
  int count=10000;
  for(int sample=0;sample<5;sample++){
   long thread=Thread.currentThread().threadId(),allocated=bean.getThreadAllocatedBytes(thread),cpu=bean.getCurrentThreadCpuTime(),start=System.nanoTime(),rebuild=PosedSurfaceSupport.recomputeCount;
   for(int i=0;i<count;i++)GenericSkinLayerSupport.preview(widget,dto);
   String line="preview,"+sample+","+count+","+(bean.getCurrentThreadCpuTime()-cpu)+","+(System.nanoTime()-start)+","+(bean.getThreadAllocatedBytes(thread)-allocated)+","+(PosedSurfaceSupport.recomputeCount-rebuild);results.add(line);System.out.println("PRE30_PERF "+line);
  }
 }
 static void measure(String name,com.sun.management.ThreadMXBean bean,java.lang.Runnable unused,Object model,Object[] cooked,boolean slim,int count,boolean animated)throws Exception{
  for(int i=0;i<2000;i++)apply(model,cooked,slim,false,false);
  for(int sample=0;sample<5;sample++){
   long thread=Thread.currentThread().threadId(),allocated=bean.getThreadAllocatedBytes(thread),cpu=bean.getCurrentThreadCpuTime(),start=System.nanoTime(),rebuild=PosedSurfaceSupport.recomputeCount;
   for(int i=0;i<count;i++){if(animated)pose(model,i+sample*count);apply(model,cooked,slim,false,false);}
   long wall=System.nanoTime()-start,cpun=bean.getCurrentThreadCpuTime()-cpu,bytes=bean.getThreadAllocatedBytes(thread)-allocated;
   String line=name+","+sample+","+count+","+cpun+","+wall+","+bytes+","+(PosedSurfaceSupport.recomputeCount-rebuild);results.add(line);System.out.println("PRE30_PERF "+line);
  }
 }
 public static void run()throws Exception{
  Files.createDirectories(OUT);
  Object widget=get(CharacterPreviewRenderer.class,"skinWidget");
  Object classic=get(widget,"field_59834"),slim=get(widget,"field_59835");
  check(state(classic)!=null&&state(slim)!=null,"Real preview models not prepared");
  Object[] cooked=new Object[8];Object[] a=(Object[])get(state(classic),"template"),b=(Object[])get(state(slim),"template");
  System.arraycopy(a,0,cooked,0,6);cooked[6]=b[4];cooked[7]=b[5];
  List<String> golden=new ArrayList<>();
  // One genuinely new native mesh, absent from the PRE29 catalog: exercise template invalidation.
  Object[] future=cooked.clone();
  var shape=new NativeVoxelSurface.Shape(List.of(new SurfaceGeometry.Polygon(List.of(new SurfaceGeometry.Vertex(-.28f,-.52f,-.3f,0,0),new SurfaceGeometry.Vertex(.28f,-.52f,-.3f,1,0),new SurfaceGeometry.Vertex(.28f,.01f,-.3f,1,1),new SurfaceGeometry.Vertex(-.28f,.01f,-.3f,0,1)),new Vector3f(0,0,-1),50000)),List.of(new NativeVoxelSurface.Bounds(new float[]{-.28f,-.52f,-.3f},new float[]{.28f,.01f,-.27f},50000)));
  future[0]=NativeVoxelSurface.mesh(shape);
  for(int frame=0;frame<96;frame++)for(int kind=0;kind<2;kind++){
   Object model=kind==0?classic:slim;boolean narrow=kind==1,first=frame%7==0,hide=frame%11==0;Object[] templates=frame%9==0?future:cooked;
   pose(model,frame);long before=PosedSurfaceSupport.recomputeCount;apply(model,templates,narrow,hide,first);
   check(PosedSurfaceSupport.recomputeCount==before+1,"Changed pose missed frame="+frame);
   String hash=geometry(model);golden.add(frame+","+kind+","+hash);
   Object snapshot=get(state(model),"matrices");Matrix4f[] snap=(Matrix4f[])snapshot;Matrix4f[] copies=Arrays.stream(snap).map(Matrix4f::new).toArray(Matrix4f[]::new);
   before=PosedSurfaceSupport.recomputeCount;apply(model,templates,narrow,hide,first);
   check(PosedSurfaceSupport.recomputeCount==before,"Static pose rebuilt");check(geometry(model).equals(hash),"Static geometry changed");check(Arrays.equals(snap,copies),"Pose snapshot overwritten");
  }
  Files.write(OUT.resolve("geometry.csv"),golden);
  PosedSurfaceSupport.clear(classic);check(state(classic)==null,"State not cleared");
  apply(classic,cooked,false,false,false);check(state(classic)!=null,"State not rebuilt");
  pose(classic,3);apply(classic,cooked,false,false,false);String stable=geometry(classic);
  com.sun.management.ThreadMXBean bean=(com.sun.management.ThreadMXBean)ManagementFactory.getThreadMXBean();check(bean.isThreadAllocatedMemorySupported(),"Allocation accounting unavailable");bean.setThreadAllocatedMemoryEnabled(true);
  results.add("case,sample,iterations,cpu_ns,wall_ns,allocated_bytes,recompute_count");
  measure("static",bean,null,classic,cooked,false,10000,false);
  measurePreview(bean,widget,(CharacterDto)get(CharacterPreviewRenderer.class,"widgetCharacter"));
  measure("animated",bean,null,classic,cooked,false,100,true);
  Files.write(OUT.resolve("perf.csv"),results);
  // Warm reflection helpers still read instance values, never cached values.
  class_630 head=(class_630)get(classic,LOW[0]);head.field_3654=.371f;pose(classic,3);apply(classic,cooked,false,false,false);check(geometry(classic).equals(stable),"Return pose differs");
  System.out.println("PRE30_RENDER_PASS checks="+checks+" geometry_states="+golden.size()+" recompute="+PosedSurfaceSupport.recomputeCount);
 }
}
