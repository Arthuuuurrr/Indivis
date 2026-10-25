import java.util.*;
import java.nio.file.*;
import java.lang.management.ManagementFactory;
import net.tompsen.nexuscharacters.*;
/** Shared real player models; unique poses on each call defeat the exact-pose cache. */
public final class MultiPlayerPerf {
 static void check(boolean c,String s){if(!c)throw new AssertionError(s);}
 static Object[] models,cooked;
 static void apply(int frame,int player,boolean relative)throws Exception{
  int kind=player%2;Object model=models[kind];
  int step=frame*13+player*3;
  if(relative)RenderPerf.pose(model,step);else RenderPerf.poseFirst(model,step);
  RenderPerf.apply(model,cooked,kind==1,false,false);
 }
 public static void run()throws Exception{
  Object widget=RenderPerf.get(CharacterPreviewRenderer.class,"skinWidget");
  models=new Object[]{RenderPerf.get(widget,"field_59834"),RenderPerf.get(widget,"field_59835")};
  cooked=new Object[8];
  Object[] a=(Object[])RenderPerf.get(RenderPerf.state(models[0]),"template"),b=(Object[])RenderPerf.get(RenderPerf.state(models[1]),"template");
  System.arraycopy(a,0,cooked,0,6);cooked[6]=b[4];cooked[7]=b[5];
  List<String> geometry=new ArrayList<>(),results=new ArrayList<>();
  for(int frame=0;frame<48;frame++){
   for(int p=0;p<8;p++)apply(frame,p,frame%2==1);
   for(int kind=0;kind<2;kind++)geometry.add(frame+","+kind+","+RenderPerf.geometry(models[kind]));
  }
  var bean=(com.sun.management.ThreadMXBean)ManagementFactory.getThreadMXBean();
  check(bean.isCurrentThreadCpuTimeSupported()&&bean.isThreadAllocatedMemorySupported(),"Missing CPU/allocation counters");
  bean.setThreadAllocatedMemoryEnabled(true);
  results.add("case,sample,players,frames,cpu_ns,wall_ns,allocated_bytes,recompute_count");
  for(int group:new int[]{1,4,8,4}){
   boolean relative=results.size()>15;
   String name=relative?"relative4":"walking"+group;
   for(int frame=0;frame<24;frame++)for(int p=0;p<group;p++)apply(1000+frame,p,relative);
   for(int sample=0;sample<5;sample++){
    long thread=Thread.currentThread().threadId(),allocated=bean.getThreadAllocatedBytes(thread),cpu=bean.getCurrentThreadCpuTime(),wall=System.nanoTime(),count=PosedSurfaceSupport.recomputeCount;
    for(int frame=0;frame<24;frame++)for(int p=0;p<group;p++)apply(10000+sample*1000+frame,p,relative);
    long cpuTime=bean.getCurrentThreadCpuTime()-cpu,wallTime=System.nanoTime()-wall,bytes=bean.getThreadAllocatedBytes(thread)-allocated,recomputes=PosedSurfaceSupport.recomputeCount-count;
    check(recomputes==24L*group,"Cache defeated sample missing: "+name+" "+recomputes);
    String line=name+","+sample+","+group+",24,"+cpuTime+","+wallTime+","+bytes+","+recomputes;results.add(line);System.out.println("MULTI35_PERF "+line);
   }
  }
  Files.createDirectories(RenderPerf.OUT);Files.write(RenderPerf.OUT.resolve("multi-geometry.csv"),geometry);Files.write(RenderPerf.OUT.resolve("multi-perf.csv"),results);
  System.out.println("MULTI35_PASS samples=20 geometry_states="+geometry.size());
 }
}
