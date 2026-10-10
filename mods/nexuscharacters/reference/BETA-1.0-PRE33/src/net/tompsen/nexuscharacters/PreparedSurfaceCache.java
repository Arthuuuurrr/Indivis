package net.tompsen.nexuscharacters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.joml.Matrix4f;

/** Retain exact results of the existing clipping algorithm; never approximate an animation. */
public final class PreparedSurfaceCache {
    private static final Map<Object, Pool> POOLS = Collections.synchronizedMap(new WeakHashMap<>());
    private static final boolean DIAGNOSTICS = ReflectionAccess.DIAGNOSTICS;
    private static final long MAX_FRAME_BYTES = 16L * 1024 * 1024;
    public static long localHits, localMisses, frameHits, frameStores, cubeChanges, resourceClears;

    private static final class Pool {
        final Object[] lower = new Object[6], upper = new Object[6];
        final Object[][] cubes = new Object[6][];
        final NativeVoxelSurface.Shape[] activeShapes = new NativeVoxelSurface.Shape[6];
        final Local[] locals = new Local[64];
        final Frame[] frames = new Frame[16];
        int nextLocal, nextFrame;
        long frameBytes;
    }

    private record Local(int part, Object template, NativeVoxelSurface.Shape shape,
                         PosedSurfaceSupport.Prepared prepared) {}

    private static final class Frame {
        final Object[] templates;
        final NativeVoxelSurface.Shape[] shapes = new NativeVoxelSurface.Shape[6];
        final Matrix4f[] matrices, relatives;
        final boolean firstPerson;
        final float[][] data = new float[12][];
        final List<SurfaceGeometry.Polygon>[] faces;
        long bytes;

        Frame(PosedSurfaceSupport.State state) throws Exception {
            templates = state.template.clone();
            matrices = copy(state.matrices);
            relatives = copy(state.relatives);
            firstPerson = state.firstPerson;
            faces = state.localFaces.clone();
            for (int p = 0; p < 6; p++) {
                shapes[p] = shape(templates[p]);
                data[p] = (float[]) SurfaceGeometry.field(state.base[p], "polygonData");
                data[p + 6] = (float[]) SurfaceGeometry.field(state.outer[p], "polygonData");
                bytes += 4L * (data[p].length + data[p + 6].length);
            }
        }

        boolean matches(Object[] templates, Matrix4f[] matrices, Matrix4f[] relatives, boolean firstPerson) {
            if (this.firstPerson != firstPerson) return false;
            for (int p = 0; p < 6; p++)
                if (this.templates[p] != templates[p] || shapes[p] != shape(templates[p])
                        || !this.relatives[p].equals(relatives[p])) return false;
            for (int p = 0; p < 12; p++) if (!this.matrices[p].equals(matrices[p])) return false;
            return true;
        }
    }

    private static Matrix4f[] copy(Matrix4f[] values) {
        Matrix4f[] copies = new Matrix4f[values.length];
        for (int i = 0; i < values.length; i++) copies[i] = new Matrix4f(values[i]);
        return copies;
    }

    private static NativeVoxelSurface.Shape shape(Object template) {
        return template == null ? null : NativeVoxelSurface.SHAPES.get(template);
    }

    /** Called inside PoseScratch's model lock, before the old state reads its base cubes. */
    public static void beforeApply(Object model) throws Exception {
        Pool pool = POOLS.get(model);
        boolean changed = false;
        if (pool == null) { pool = new Pool(); POOLS.put(model, pool); }
        for (int p = 0; p < 6; p++) {
            Object lower = SurfaceGeometry.field(model, PosedSurfaceSupport.LOWER[p]);
            Object upper = SurfaceGeometry.field(model, PosedSurfaceSupport.UPPER[p]);
            List<?> cubes = (List<?>) SurfaceGeometry.field(lower, "field_3663");
            boolean same = pool.lower[p] == lower && pool.upper[p] == upper
                    && pool.cubes[p] != null && pool.cubes[p].length == cubes.size();
            if (same) for (int i = 0; i < cubes.size(); i++)
                if (pool.cubes[p][i] != cubes.get(i)) { same = false; break; }
            if (!same) {
                if (pool.lower[p] != null) changed = true;
                pool.lower[p] = lower; pool.upper[p] = upper; pool.cubes[p] = cubes.toArray();
                for (int i = 0; i < pool.locals.length; i++)
                    if (pool.locals[i] != null && pool.locals[i].part == p) pool.locals[i] = null;
            }
        }
        if (changed) {
            java.util.Arrays.fill(pool.frames, null); pool.frameBytes = 0;
            PosedSurfaceSupport.clearPRE30(model);
            if (DIAGNOSTICS) cubeChanges++;
        }
    }

    public static void restore(PosedSurfaceSupport.State state, Object model, Matrix4f[] matrices,
                               Matrix4f[] relatives, Object[] templates, boolean firstPerson) throws Exception {
        Pool pool = POOLS.get(model);
        // Template objects can survive replacement of their native shape during a reload.
        for (int p = 0; p < 6; p++) {
            NativeVoxelSurface.Shape current = shape(templates[p]);
            if (state.template != null && state.template[p] == templates[p]
                    && pool.activeShapes[p] != current) state.template = null;
            pool.activeShapes[p] = current;
            // Model children can change independently of cubes and cached polygon arrays.
            SurfaceGeometry.set(state.base[p], "children", SurfaceGeometry.field(pool.lower[p], "field_3661"));
        }
        // The original fast path already handles an unchanged frame without touching arrays.
        if (state.firstPerson == firstPerson && java.util.Arrays.equals(templates, state.template)
                && java.util.Arrays.equals(matrices, state.matrices)) return;
        for (Frame frame : pool.frames) {
            if (frame == null || !frame.matches(templates, matrices, relatives, firstPerson)) continue;
            for (int p = 0; p < 6; p++) {
                SurfaceGeometry.set(state.base[p], "polygonData", frame.data[p]);
                SurfaceGeometry.set(state.outer[p], "polygonData", frame.data[p + 6]);
                state.localFaces[p] = frame.faces[p];
            }
            state.template = templates; state.matrices = matrices; state.relatives = relatives;
            state.firstPerson = firstPerson;
            if (DIAGNOSTICS) frameHits++;
            return;
        }
    }

    public static PosedSurfaceSupport.Prepared lookup(Object model, Object template, Matrix4f relative, int part) {
        Pool pool = POOLS.get(model);
        NativeVoxelSurface.Shape shape = shape(template);
        for (Local local : pool.locals) {
            if (local != null && local.part == part && local.template == template && local.shape == shape
                    && local.prepared.relative().equals(relative)) {
                if (DIAGNOSTICS) localHits++;
                return local.prepared;
            }
        }
        if (DIAGNOSTICS) localMisses++;
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object store(Map map, Object template, Object value, Object model, int part) {
        Pool pool = POOLS.get(model);
        pool.locals[pool.nextLocal] = new Local(part, template, shape(template), (PosedSurfaceSupport.Prepared) value);
        pool.nextLocal = (pool.nextLocal + 1) % pool.locals.length;
        return map.put(template, value);
    }

    /** Called only after an actual execution of the original clipping code. */
    public static void capture(PosedSurfaceSupport.State state, Object model) throws Exception {
        Pool pool = POOLS.get(model);
        Frame frame = new Frame(state);
        if (frame.bytes > MAX_FRAME_BYTES) return;
        int slot = pool.nextFrame;
        if (pool.frames[slot] != null) pool.frameBytes -= pool.frames[slot].bytes;
        pool.frames[slot] = frame; pool.frameBytes += frame.bytes;
        pool.nextFrame = (slot + 1) % pool.frames.length;
        for (int i = 0; i < pool.frames.length && pool.frameBytes > MAX_FRAME_BYTES; i++) {
            int evict = (pool.nextFrame + i) % pool.frames.length;
            if (evict != slot && pool.frames[evict] != null) {
                pool.frameBytes -= pool.frames[evict].bytes; pool.frames[evict] = null;
            }
        }
        if (DIAGNOSTICS) frameStores++;
    }

    public static void clearCaches() {
        List<Object> models;
        synchronized (POOLS) { models = new ArrayList<>(POOLS.keySet()); }
        for (Object model : models) synchronized (model) {
            POOLS.remove(model);
            try { PosedSurfaceSupport.clearPRE30(model); }
            catch (Exception e) { throw new IllegalStateException("Could not detach Nexus surfaces", e); }
        }
        if (DIAGNOSTICS) resourceClears++;
    }
}
