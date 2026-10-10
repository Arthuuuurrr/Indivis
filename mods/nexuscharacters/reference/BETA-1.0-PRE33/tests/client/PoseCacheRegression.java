import java.nio.file.*;
import java.util.*;
import java.lang.management.ManagementFactory;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;

/** Compare the same real, cooked geometry on PRE32 and PRE33, including cache eviction/clear. */
public final class PoseCacheRegression {
    static final boolean BASE = Boolean.getBoolean("pre30.baseline");
    static int checks;
    static final List<String> golden = new ArrayList<>(), perf = new ArrayList<>();
    static void check(boolean value, String name) { checks++; if (!value) throw new AssertionError(name); }
    static void record(String name, Object model) throws Exception { golden.add(name + "," + RenderPerf.geometry(model)); }
    static void pose(Object model, int frame) throws Exception { RenderPerf.pose(model, frame); }
    static void apply(Object model, Object[] cooked, boolean slim, boolean hide, boolean first) throws Exception {
        RenderPerf.apply(model, cooked, slim, hide, first);
    }
    static void measure(String name, Object model, Object[] cooked, int poses, boolean clear, boolean upper, int count) throws Exception {
        var bean = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean(); bean.setThreadAllocatedMemoryEnabled(true);
        PosedSurfaceSupport.clear(model);
        for (int i = 0; i < (poses == 0 ? 25 : 80); i++) {
            int frame = poses == 0 ? i : i % poses;
            if (upper) pose(model, frame); else RenderPerf.poseFirst(model, frame);
            if (clear) PosedSurfaceSupport.clear(model);
            apply(model, cooked, false, false, false);
        }
        for (int sample = 0; sample < 3; sample++) {
            long id = Thread.currentThread().threadId(), allocated = bean.getThreadAllocatedBytes(id),
                    cpu = bean.getCurrentThreadCpuTime(), start = System.nanoTime(), recomputes = PosedSurfaceSupport.recomputeCount;
            for (int i = 0; i < count; i++) {
                int frame = poses == 0 ? 2000 + i + count * sample : i % poses;
                if (upper) pose(model, frame); else RenderPerf.poseFirst(model, frame);
                if (clear) PosedSurfaceSupport.clear(model);
                apply(model, cooked, false, false, false);
            }
            long rebuilds = PosedSurfaceSupport.recomputeCount - recomputes;
            String row = name + "," + sample + "," + count + "," + (bean.getCurrentThreadCpuTime() - cpu) + ","
                    + (System.nanoTime() - start) + "," + (bean.getThreadAllocatedBytes(id) - allocated) + "," + rebuilds;
            perf.add(row); System.out.println("POSE33_PERF " + row);
            if (!BASE && poses > 0 && poses <= 16) check(rebuilds == 0, name + " known poses were clipped again");
        }
    }

    public static void run() throws Exception {
        Object widget = RenderPerf.get(CharacterPreviewRenderer.class, "skinWidget");
        Object classic = RenderPerf.get(widget, "field_59834"), slim = RenderPerf.get(widget, "field_59835");
        Object[] cooked = new Object[8];
        System.arraycopy((Object[]) RenderPerf.get(RenderPerf.state(classic), "template"), 0, cooked, 0, 6);
        Object[] narrow = (Object[]) RenderPerf.get(RenderPerf.state(slim), "template");
        cooked[6] = narrow[4]; cooked[7] = narrow[5];
        for (int kind = 0; kind < 2; kind++) {
            Object model = kind == 0 ? classic : slim;
            boolean thin = kind == 1;
            PosedSurfaceSupport.clear(model);
            for (int i = 0; i < 42; i++) {
                int frame = i % 7;
                pose(model, frame);
                boolean first = i % 9 == 4, hide = i % 5 == 1;
                if (i % 3 == 2) PosedSurfaceSupport.clear(model);
                apply(model, cooked, thin, hide, first);
                record("sequence/" + kind + "/" + i, model);
                String before = RenderPerf.geometry(model);
                apply(model, cooked, thin, hide, first);
                check(before.equals(RenderPerf.geometry(model)), "Repeated pose changed geometry");
            }
            // Replacement of cubes must use the new geometry even if the pose/template is identical.
            Object head = RenderPerf.get(model, RenderPerf.LOW[0]);
            Object otherHead = RenderPerf.get(kind == 0 ? slim : classic, RenderPerf.LOW[0]);
            Object original = RenderPerf.get(head, "field_3663"), replacement = RenderPerf.get(otherHead, "field_3663");
            pose(model, 0); apply(model, cooked, thin, false, false);
            SurfaceGeometry.set(head, "field_3663", replacement);
            if (BASE) PosedSurfaceSupport.clear(model); // Exact PRE32 uncached reference for changed cubes.
            apply(model, cooked, thin, false, false); record("cube-replacement/" + kind, model);
            SurfaceGeometry.set(head, "field_3663", original);
            if (BASE) PosedSurfaceSupport.clear(model);
            apply(model, cooked, thin, false, false); record("cube-restore/" + kind, model);
        }
        // A new shape registered under an existing template must not retrieve old polygon data.
        Object template = cooked[0]; NativeVoxelSurface.Shape original = NativeVoxelSurface.SHAPES.get(template);
        var changed = new NativeVoxelSurface.Shape(List.of(new SurfaceGeometry.Polygon(List.of(
                new SurfaceGeometry.Vertex(-.3f, -.5f, -.31f, 0, 0), new SurfaceGeometry.Vertex(.3f, -.5f, -.31f, 1, 0),
                new SurfaceGeometry.Vertex(.3f, 0, -.31f, 1, 1), new SurfaceGeometry.Vertex(-.3f, 0, -.31f, 0, 1)),
                new org.joml.Vector3f(0, 0, -1), 71000)), List.of(new NativeVoxelSurface.Bounds(
                new float[]{-.3f, -.5f, -.31f}, new float[]{.3f, 0, -.28f}, 71000)));
        pose(classic, 0); apply(classic, cooked, false, false, false); String old = RenderPerf.geometry(classic);
        NativeVoxelSurface.SHAPES.put(template, changed);
        if (BASE) PosedSurfaceSupport.clear(classic);
        apply(classic, cooked, false, false, false); record("shape-replacement", classic);
        check(!old.equals(RenderPerf.geometry(classic)), "Shape replacement returned stale surfaces");
        NativeVoxelSurface.SHAPES.put(template, original);
        if (BASE) PosedSurfaceSupport.clear(classic);
        apply(classic, cooked, false, false, false); record("shape-restore", classic);
        check(old.equals(RenderPerf.geometry(classic)), "Shape restore changed surfaces");
        // Refresh children without replacing their cube list.
        Object head = RenderPerf.get(classic, RenderPerf.LOW[0]), children = RenderPerf.get(head, "field_3661");
        Map<String, class_630> newChildren = new HashMap<>(); SurfaceGeometry.set(head, "field_3661", newChildren);
        apply(classic, cooked, false, false, false);
        if (!BASE) check(RenderPerf.get(((Object[]) RenderPerf.get(RenderPerf.state(classic), "base"))[0], "children") == newChildren,
                "Cached mesh kept old model children");
        SurfaceGeometry.set(head, "field_3661", children);
        if (!BASE) {
            PreparedSurfaceCache.clearCaches();
            check(RenderPerf.state(classic) == null, "Resource clear retained posed state");
        } else PosedSurfaceSupport.clear(classic);
        pose(classic, 0); apply(classic, cooked, false, false, false); record("resource-clear", classic);
        perf.add("case,sample,iterations,cpu_ns,wall_ns,allocated_bytes,recompute_count");
        measure("warm-two-poses", classic, cooked, 2, false, true, 80);
        measure("clear-same-pose", classic, cooked, 1, true, true, 60);
        measure("four-poses", classic, cooked, 4, false, true, 80);
        measure("eight-relative-poses", classic, cooked, 8, false, true, 64);
        measure("unique-lower-poses", classic, cooked, 0, false, false, 40);
        measure("unique-relative-poses", classic, cooked, 0, false, true, 40);
        Path out = Path.of("pre33/qa/" + (BASE ? "baseline" : "candidate")); Files.createDirectories(out);
        Files.write(out.resolve("pose-geometry.csv"), golden); Files.write(out.resolve("pose-perf.csv"), perf);
        System.out.println("POSE33_PASS checks=" + checks + " geometry_states=" + golden.size());
    }
}
