package net.tompsen.nexuscharacters;

import dev.tr7zw.skinlayers.accessor.ModelPartInjector;
import dev.tr7zw.skinlayers.api.Mesh;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.class_4587;
import net.minecraft.class_630;
import org.joml.Matrix4f;

/** The same local 3D surfaces as PRE31, prepared only for the arm being submitted. */
public final class FirstPersonSurfaceSupport {
    private static final Map<Object, State> STATES = Collections.synchronizedMap(new WeakHashMap<>());
    private static final boolean DIAGNOSTICS = ReflectionAccess.DIAGNOSTICS;
    public static long calls, preparations, cacheHits, cubeChanges;

    private static final class State {
        final Arm[] arms = new Arm[2];
        final class_4587 stack = new class_4587();
        final Matrix4f relative = new Matrix4f();
    }

    private static final class Prepared {
        final Object template;
        final NativeVoxelSurface.Shape shape;
        final boolean slim;
        final Matrix4f relative;
        final float[] data;
        Prepared(Object template, NativeVoxelSurface.Shape shape, boolean slim, Matrix4f relative, float[] data) {
            this.template = template;
            this.shape = shape;
            this.slim = slim;
            this.relative = new Matrix4f(relative);
            this.data = data;
        }
    }

    private static final class Arm {
        final class_630 lower, upper;
        final Object[] cubes;
        final List<float[][]> boxes;
        final Object base, outer;
        final Mesh baseProxy, outerProxy;
        // A small per-arm cache also handles multiple render passes/character choices.
        final Prepared[] prepared = new Prepared[16];
        int replace;
        Arm(class_630 lower, class_630 upper, List<?> cubes, int part) throws Exception {
            this.lower = lower;
            this.upper = upper;
            this.cubes = cubes.toArray();
            this.boxes = PosedSurfaceSupport.bounds(cubes);
            this.base = NativeVoxelSurface.emptyMesh();
            this.outer = NativeVoxelSurface.emptyMesh();
            SurfaceGeometry.set(base, "children", SurfaceGeometry.field(lower, "field_3661"));
            SurfaceGeometry.set(base, "polygonData",
                SurfaceGeometry.write(PosedSurfaceSupport.readCubes(cubes, 100 + part)));
            this.baseProxy = (Mesh) PosedSurfaceSupport.wrap(base, true);
            this.outerProxy = (Mesh) PosedSurfaceSupport.wrap(outer, false);
        }
        boolean matches(class_630 lower, class_630 upper, List<?> cubes) {
            if (this.lower != lower || this.upper != upper || this.cubes.length != cubes.size()) return false;
            for (int i = 0; i < this.cubes.length; i++) if (this.cubes[i] != cubes.get(i)) return false;
            return true;
        }
        void detach() {
            ModelPartInjector low = (ModelPartInjector) (Object) lower, up = (ModelPartInjector) (Object) upper;
            if (low.getInjectedMesh() == baseProxy) low.setInjectedMesh(null, null);
            if (up.getInjectedMesh() == outerProxy) up.setInjectedMesh(null, null);
        }
    }

    public static void firstPerson(Object model, Object[] cooked, boolean slim, Object arm) throws Exception {
        synchronized (model) {
            class_630 left = (class_630) SurfaceGeometry.field(model, PosedSurfaceSupport.LOWER[4]);
            class_630 right = (class_630) SurfaceGeometry.field(model, PosedSurfaceSupport.LOWER[5]);
            int part = arm == left ? 4 : arm == right ? 5 : -1;
            if (part < 0) {
                // Preserve PRE31 behavior for an unfamiliar arm supplied by another renderer.
                PosedSurfaceSupport.firstPerson(model, cooked, slim);
                return;
            }
            class_630 lower = part == 4 ? left : right;
            class_630 upper = (class_630) SurfaceGeometry.field(model, PosedSurfaceSupport.UPPER[part]);
            List<?> cubes = (List<?>) SurfaceGeometry.field(lower, "field_3663");
            State state = STATES.get(model);
            if (state == null) {
                state = new State();
                STATES.put(model, state);
            }
            Arm preparedArm = state.arms[part - 4];
            if (preparedArm == null || !preparedArm.matches(lower, upper, cubes)) {
                if (preparedArm != null) preparedArm.detach();
                preparedArm = new Arm(lower, upper, cubes, part);
                state.arms[part - 4] = preparedArm;
                if (DIAGNOSTICS) cubeChanges++;
            }
            upper.field_37938 = upper.field_37939 = upper.field_37940 = 1;
            upper.field_3665 = true;
            state.stack.method_34426();
            upper.method_22703(state.stack);
            state.relative.set(state.stack.method_23760().method_23761());
            Object template = cooked[slim ? part + 2 : part];
            NativeVoxelSurface.Shape shape = template == null ? null : NativeVoxelSurface.SHAPES.get(template);
            Prepared hit = null;
            for (Prepared cached : preparedArm.prepared) {
                if (cached != null && cached.template == template && cached.shape == shape
                        && cached.slim == slim && cached.relative.equals(state.relative)) {
                    hit = cached;
                    break;
                }
            }
            if (hit == null) {
                List<SurfaceGeometry.Polygon> faces = List.of();
                if (shape != null) {
                    PosedSurfaceSupport.Grid local = new PosedSurfaceSupport.Grid();
                    for (float[][] b : preparedArm.boxes)
                        local.add(new SurfaceGeometry.Box(b[0], b[1], new Matrix4f(state.relative).invert(), 100 + part, part));
                    for (NativeVoxelSurface.Bounds b : shape.boxes())
                        local.add(new SurfaceGeometry.Box(b.lo(), b.hi(), new Matrix4f(), 2000 + b.rank() * 6 + part, part));
                    List<SurfaceGeometry.Polygon> ranked = new ArrayList<>();
                    for (SurfaceGeometry.Polygon f : shape.faces())
                        ranked.add(new SurfaceGeometry.Polygon(f.vertices(), f.normal(), 2000 + f.rank() * 6 + part));
                    faces = PosedSurfaceSupport.clean(ranked, local, part, false, false, false);
                }
                hit = new Prepared(template, shape, slim, state.relative, SurfaceGeometry.write(faces));
                preparedArm.prepared[preparedArm.replace] = hit;
                preparedArm.replace = (preparedArm.replace + 1) % preparedArm.prepared.length;
                if (DIAGNOSTICS) preparations++;
            } else if (DIAGNOSTICS) cacheHits++;
            SurfaceGeometry.set(preparedArm.outer, "polygonData", hit.data);
            // Children may be replaced by a resource/model reload without changing the cube list.
            SurfaceGeometry.set(preparedArm.base, "children", SurfaceGeometry.field(lower, "field_3661"));
            ((ModelPartInjector) (Object) lower).setInjectedMesh(preparedArm.baseProxy, NativeVoxelSurface.IDENTITY);
            ((ModelPartInjector) (Object) upper).setInjectedMesh(template == null ? null : preparedArm.outerProxy, NativeVoxelSurface.IDENTITY);
            if (DIAGNOSTICS) calls++;
        }
    }

    /** Detach our meshes, retaining prepared arms through unrelated world/armor model clears. */
    public static void detach(Object model) {
        synchronized (model) {
            State state = STATES.get(model);
            if (state != null) for (Arm arm : state.arms) if (arm != null) arm.detach();
        }
    }

    public static void clearCaches() {
        List<Object> models;
        synchronized (STATES) { models = new ArrayList<>(STATES.keySet()); }
        for (Object model : models) synchronized (model) {
            State state = STATES.remove(model);
            if (state != null) for (Arm arm : state.arms) if (arm != null) arm.detach();
        }
    }
}
