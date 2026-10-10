import java.lang.management.ManagementFactory;
import java.lang.reflect.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import dev.tr7zw.skinlayers.accessor.ModelPartInjector;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;

/** Uses PRE31's retained implementation as an independent oracle on actual game models/assets. */
public final class FirstPersonRegression {
    static final boolean BASE = Boolean.getBoolean("pre30.baseline");
    static final Path OUT = Path.of("pre32/qa/" + (BASE ? "baseline-hand" : "candidate-hand"));
    static int checks;
    static void check(boolean value, String message) { checks++; if (!value) throw new AssertionError(message); }
    static Object get(Object value, String name) throws Exception { return SurfaceGeometry.field(value, name); }
    static class_630 low(Object model, int part) throws Exception { return (class_630) get(model, RenderPerf.LOW[part]); }
    static class_630 up(Object model, int part) throws Exception { return (class_630) get(model, RenderPerf.UP[part]); }
    static Object armState(Object model, int part) throws Exception {
        Object state = ((Map<?,?>) get(Class.forName("net.tompsen.nexuscharacters.FirstPersonSurfaceSupport"), "STATES")).get(model);
        return ((Object[]) get(state, "arms"))[part - 4];
    }
    static float[] data(Object model, int part, String group, boolean candidate) throws Exception {
        Object mesh = candidate ? get(armState(model, part), group) : ((Object[]) get(RenderPerf.state(model), group))[part];
        return (float[]) get(mesh, "polygonData");
    }
    static String hash(float[] data) throws Exception {
        check(data.length % 23 == 0, "Incomplete native polygon data");
        List<String> faces = new ArrayList<>();
        for (int i = 0; i < data.length; i += 23) {
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < 23; j++) s.append(HexFormat.of().toHexDigits(Float.floatToRawIntBits(data[i+j])));
            faces.add(s.toString());
        }
        Collections.sort(faces);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        for (String face : faces) digest.update(face.getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        return HexFormat.of().formatHex(digest.digest());
    }
    static void apply(Object model, Object[] templates, boolean slim, int part) throws Exception {
        if (BASE) PosedSurfaceSupport.firstPerson(model, templates, slim);
        else {
            Class<?> c = Class.forName("net.tompsen.nexuscharacters.FirstPersonSurfaceSupport");
            c.getMethod("firstPerson", Object.class, Object[].class, boolean.class, Object.class)
                .invoke(null, model, templates, slim, low(model, part));
        }
    }
    static void direct(Object model, Object[] templates, boolean slim, int part) throws Exception {
        // Direct call avoids reflection overhead in benchmarks; only evaluated by the candidate.
        if (BASE) PosedSurfaceSupport.firstPerson(model, templates, slim);
        else FirstPersonSurfaceSupport.firstPerson(model, templates, slim, low(model, part));
    }
    static final List<String> gold = new ArrayList<>();
    static void compare(Object model, Object[] templates, boolean slim, int part, String label) throws Exception {
        PosedSurfaceSupport.firstPerson(model, templates, slim);
        String base = hash(data(model,part,"base",false)), outer = hash(data(model,part,"outer",false));
        apply(model,templates,slim,part);
        check(hash(data(model,part,"base",true)).equals(base), "Base arm differs from PRE31: " + label);
        check(hash(data(model,part,"outer",true)).equals(outer), "3D arm differs from PRE31: " + label);
        Object a = armState(model,part);
        check(((ModelPartInjector)(Object)low(model,part)).getInjectedMesh()==get(a,"baseProxy"), "Wrong submitted base mesh");
        Object template=templates[slim?part+2:part];
        check(((ModelPartInjector)(Object)up(model,part)).getInjectedMesh()==(template==null?null:get(a,"outerProxy")), "Wrong submitted sleeve mesh");
        gold.add(label + "," + base + "," + outer);
    }
    static void regression(Object classic, Object slim, Object[] normal, Object[] first) throws Exception {
        Object[] future = first.clone(), absent = first.clone();
        // New template objects really carry different geometry; no asset IDs are hardcoded in production.
        for (int p=4;p<8;p++) {
            NativeVoxelSurface.Shape shape=NativeVoxelSurface.SHAPES.get(first[p]);
            if(shape!=null) future[p]=NativeVoxelSurface.mesh(new NativeVoxelSurface.Shape(
                SurfaceGeometry.transform(shape.faces(),new org.joml.Matrix4f().translate(.013f,0,.017f)),
                shape.boxes().stream().map(b->new NativeVoxelSurface.Bounds(
                    new float[]{b.lo()[0]+.013f,b.lo()[1],b.lo()[2]+.017f},
                    new float[]{b.hi()[0]+.013f,b.hi()[1],b.hi()[2]+.017f},b.rank())).toList()));
            absent[p]=null;
        }
        for(int kind=0;kind<2;kind++) {
            Object model=kind==0?classic:slim;
            for(int pose=0;pose<12;pose++) for(int part=4;part<=5;part++) {
                RenderPerf.pose(model,pose);
                Object[] templates=pose%5==0?future:pose%7==0?absent:first;
                compare(model,templates,kind==1,part,"pose/"+kind+"/"+pose+"/"+part);
            }
            for(int part=4;part<=5;part++) {
                RenderPerf.poseFirst(model,0);
                apply(model,first,kind==1,part);
                float[] stable=data(model,part,"outer",true);
                for(int frame=1;frame<=40;frame++) {
                    RenderPerf.poseFirst(model,frame);
                    // Exercise the non-arm relative transforms omitted by PRE31's benchmark.
                    for(int p=0;p<4;p++) up(model,p).field_3654=.013f*frame;
                    apply(model,first,kind==1,part);
                    check(data(model,part,"outer",true)==stable,"Unused body/animation rewrote the arm");
                }
                PosedSurfaceSupport.clear(model);
                check(((ModelPartInjector)(Object)low(model,part)).getInjectedMesh()==null,"Clear retained hand injection");
                apply(model,first,kind==1,part);
                check(data(model,part,"outer",true)==stable,"World clear discarded prepared hand");
                // Alternate multiple relative poses and different outfits, then return to a cached pose.
                RenderPerf.poseFirst(model,0);
                apply(model,first,kind==1,part); stable=data(model,part,"outer",true);
                up(model,part).field_3654=.217f;
                compare(model,future,kind==1,part,"changed-relative/"+kind+"/"+part);
                check(data(model,part,"outer",true)!=stable,"Required relative/template invalidation missing");
                RenderPerf.poseFirst(model,0);
                apply(model,first,kind==1,part);
                check(data(model,part,"outer",true)==stable,"Returning to a cached pose rebuilt it");
            }
            // A hand must neither replace nor poison world/inventory geometry caches.
            RenderPerf.poseFirst(model,0);
            PosedSurfaceSupport.apply(model,normal,kind==1,false);
            Object world=RenderPerf.state(model);
            Object[] arrays=RenderPerf.arrays(model);
            String worldHash=RenderPerf.geometry(model);
            apply(model,first,kind==1,4); apply(model,first,kind==1,5);
            check(RenderPerf.state(model)==world,"Hand replaced world state");
            check(!(Boolean)get(world,"firstPerson"),"Hand changed world context");
            check(RenderPerf.geometry(model).equals(worldHash),"Hand rewrote world geometry");
            PosedSurfaceSupport.apply(model,normal,kind==1,false);
            Object[] restored=RenderPerf.arrays(model);
            for(int p=0;p<arrays.length;p++)check(arrays[p]==restored[p],"Unchanged world mesh rebuilt after hand");
            for(int part=4;part<=5;part++)
                check(((ModelPartInjector)(Object)low(model,part)).getInjectedMesh()==((Object[])get(world,"baseProxy"))[part],"World arm not restored");
        }
        // Model cube replacements invalidate both bounds and base UVs.
        class_630 left=low(classic,4);
        Object old=get(left,"field_3663");
        SurfaceGeometry.set(left,"field_3663",get(low(slim,4),"field_3663"));
        PosedSurfaceSupport.clear(classic);
        compare(classic,first,false,4,"replaced-arm-cubes");
        SurfaceGeometry.set(left,"field_3663",old);
        PosedSurfaceSupport.clear(classic);
        compare(classic,first,false,4,"restored-arm-cubes");
        // Distinct ModelPart instances must not share cached geometry/injections.
        check(armState(classic,4)!=armState(slim,4),"Classic and slim shared arm cache");
        Object before=armState(classic,4);
        FirstPersonSurfaceSupport.clearCaches();
        apply(classic,first,false,4);
        check(armState(classic,4)!=before,"Resource reload failed to drop old prepared state");
        compare(classic,first,false,4,"cache-resource-reload");
        int states=((Map<?,?>)get(FirstPersonSurfaceSupport.class,"STATES")).size();
        FirstPersonSurfaceSupport.firstPerson(classic,first,false,low(classic,0));
        check(((Map<?,?>)get(FirstPersonSurfaceSupport.class,"STATES")).size()==states,"Non-arm mutated hand state");
        check((Boolean)get(RenderPerf.state(classic),"firstPerson"),"Unknown arm failed to preserve legacy renderer behavior");
        Files.write(OUT.resolve("arm-geometry.csv"),gold);
    }
    static void step(Object model,Object[] first,int i,int mode) throws Exception {
        RenderPerf.poseFirst(model,i);
        if(mode==1)for(int p=0;p<4;p++)up(model,p).field_3654=.0007f*(i+1);
        if(mode==2)PosedSurfaceSupport.clear(model);
        if(mode==3)up(model,5).field_3654=.013f*(i%8);
        if(mode==4)up(model,5).field_3654=.0009f*(i+1);
        direct(model,first,false,5);
    }
    static void performance(Object model,Object[] first) throws Exception {
        com.sun.management.ThreadMXBean bean=(com.sun.management.ThreadMXBean)ManagementFactory.getThreadMXBean();
        bean.setThreadAllocatedMemoryEnabled(true);
        List<String> rows=new ArrayList<>(List.of("case,sample,iterations,cpu_ns,wall_ns,allocated_bytes"));
        String[] cases={"warm-arm","non-arm-relative-change","world-model-clear","eight-arm-relative-poses","unique-arm-relative-change"};
        for(int mode=0;mode<cases.length;mode++) {
            RenderPerf.poseFirst(model,0);
            for(int i=0;i<24;i++)step(model,first,i,mode);
            int iterations=mode==0?10000:32;
            for(int sample=0;sample<5;sample++) {
                long tid=Thread.currentThread().threadId(),alloc=bean.getThreadAllocatedBytes(tid),cpu=bean.getCurrentThreadCpuTime(),wall=System.nanoTime();
                for(int i=0;i<iterations;i++)step(model,first,i+sample*iterations,mode);
                String row=cases[mode]+","+sample+","+iterations+","+(bean.getCurrentThreadCpuTime()-cpu)+","+(System.nanoTime()-wall)+","+(bean.getThreadAllocatedBytes(tid)-alloc);
                rows.add(row);System.out.println("PRE32_HAND_PERF "+row);
            }
        }
        Files.write(OUT.resolve("perf.csv"),rows);
    }
    public static void run() throws Exception {
        Files.createDirectories(OUT);
        Object widget=get(CharacterPreviewRenderer.class,"skinWidget"),classic=get(widget,"field_59834"),slim=get(widget,"field_59835");
        Object[] normal=new Object[8],a=(Object[])get(RenderPerf.state(classic),"template"),b=(Object[])get(RenderPerf.state(slim),"template");
        System.arraycopy(a,0,normal,0,6);normal[6]=b[4];normal[7]=b[5];
        Object[] first=RenderPerf.firstCooked(normal);
        if(!BASE)regression(classic,slim,normal,first);
        performance(classic,first);
        RenderPerf.poseFirst(classic,0);RenderPerf.poseFirst(slim,0);
        PosedSurfaceSupport.apply(classic,normal,false,false);PosedSurfaceSupport.apply(slim,normal,true,false);
        String pass="PRE32_HAND_PASS baseline="+BASE+" checks="+checks+" matched_arm_states="+gold.size();
        Files.writeString(OUT.resolve("pass.txt"),pass);System.out.println(pass);
    }
}
