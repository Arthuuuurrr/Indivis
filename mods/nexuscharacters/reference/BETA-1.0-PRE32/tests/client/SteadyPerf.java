import java.nio.file.*;
import java.util.*;
import java.lang.management.ManagementFactory;
import net.tompsen.nexuscharacters.*;

/** Additional steady-state check for the unchanged static and preview paths. */
public final class SteadyPerf {
    public static void run() throws Exception {
        Object widget=RenderPerf.get(CharacterPreviewRenderer.class,"skinWidget");
        Object model=RenderPerf.get(widget,"field_59834");
        Object[] templates=(Object[])RenderPerf.get(RenderPerf.state(model),"template"),cooked=new Object[8];
        System.arraycopy(templates,0,cooked,0,6);cooked[6]=cooked[4];cooked[7]=cooked[5];
        CharacterDto dto=(CharacterDto)RenderPerf.get(CharacterPreviewRenderer.class,"widgetCharacter");
        var bean=(com.sun.management.ThreadMXBean)ManagementFactory.getThreadMXBean();bean.setThreadAllocatedMemoryEnabled(true);
        List<String> rows=new ArrayList<>();rows.add("case,sample,iterations,cpu_ns,wall_ns,allocated_bytes,recompute_count");
        for(boolean preview:new boolean[]{false,true}) {
            for(int i=0;i<100000;i++) {
                if(preview)GenericSkinLayerSupport.preview(widget,dto);
                else PosedSurfaceSupport.apply(model,cooked,false,false);
            }
            for(int sample=0;sample<9;sample++) {
                int count=20000;long id=Thread.currentThread().threadId(),bytes=bean.getThreadAllocatedBytes(id),cpu=bean.getCurrentThreadCpuTime(),wall=System.nanoTime(),rebuild=PosedSurfaceSupport.recomputeCount;
                for(int i=0;i<count;i++) {
                    if(preview)GenericSkinLayerSupport.preview(widget,dto);
                    else PosedSurfaceSupport.apply(model,cooked,false,false);
                }
                String row=(preview?"preview":"static")+","+sample+","+count+","+(bean.getCurrentThreadCpuTime()-cpu)+","+(System.nanoTime()-wall)+","+(bean.getThreadAllocatedBytes(id)-bytes)+","+(PosedSurfaceSupport.recomputeCount-rebuild);
                rows.add(row);System.out.println("STEADY_PERF "+row);
            }
        }
        Path out=Path.of("pre32/qa/"+(Boolean.getBoolean("pre30.baseline")?"baseline":"candidate"));Files.createDirectories(out);Files.write(out.resolve("steady-perf.csv"),rows);
        System.out.println("STEADY_PASS warmup=100000 samples=9 iterations=20000");
    }
}
