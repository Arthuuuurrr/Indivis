package net.tompsen.nexuscharacters;

import java.util.*;
import org.joml.Matrix4f;
import net.tompsen.nexuscharacters.PosedSurfaceSupport.*;
import net.tompsen.nexuscharacters.SurfaceGeometry.Box;

/** Immutable world-grid slices, owned by one reentrant pose workspace.
 * No pose rounding, animation skipping, geometry or shader changes.
 */
public final class PartVolumeCache {
    static final long LIMIT=8L*1024*1024;
    static final boolean VERIFY=Boolean.getBoolean("nexus.opt1.verify");
    public static long hits,misses,evictions,peakBytes,oracleCalls,oracleBoxes,fallbacks;
    private PartVolumeCache() {}

    public static final class Pool {
        @SuppressWarnings("unchecked") final ArrayList<Entry>[] entries=new ArrayList[6];
        final NativeVoxelSurface.Shape[][] uniqueShapes=new NativeVoxelSurface.Shape[6][];
        int nextSignature;
        long bytes,clock;
        Pool(){for(int p=0;p<6;p++)entries[p]=new ArrayList<>(6);}
    }
    static final class Entry {
        final Object source,template,shape;
        final Matrix4f base,outer;
        final Map<Long,List<Box>> cells;
        final List<Box> boxes;
        final long bytes;
        long lastUse;
        Entry(State state,Matrix4f[] matrices,Object[] templates,int part,NativeVoxelSurface.Shape shape) {
            source=state.boxes[part];template=templates[part];this.shape=shape;
            base=new Matrix4f(matrices[part]);outer=new Matrix4f(matrices[part+6]);
            Grid grid=new Grid();boxes=new ArrayList<>();
            addPart(grid,boxes,state,matrices,part,shape);
            cells=grid.cells[part];
            long estimate=256L+cells.size()*144L+boxes.size()*512L;
            for(List<Box> list:cells.values())estimate+=list.size()*16L;
            bytes=estimate;
        }
        boolean matches(State state,Matrix4f[] matrices,Object[] templates,int p,NativeVoxelSurface.Shape next) {
            return source==state.boxes[p]&&template==templates[p]&&shape==next
                &&base.equals(matrices[p])&&outer.equals(matrices[p+6]);
        }
    }
    static void addPart(Grid grid,List<Box> boxes,State state,Matrix4f[] matrices,int p,NativeVoxelSurface.Shape shape) {
        for(float[][] bounds:state.boxes[p]) {
            Box box=new Box(bounds[0],bounds[1],matrices[p],100+p,p);
            grid.add(box);if(boxes!=null)boxes.add(box);
        }
        if(shape!=null)for(NativeVoxelSurface.Bounds bounds:shape.boxes()) {
            Box box=new Box(bounds.lo(),bounds.hi(),matrices[p+6],2000+bounds.rank()*6+p,p);
            grid.add(box);if(boxes!=null)boxes.add(box);
        }
    }
    static NativeVoxelSurface.Shape shape(Object template) {
        return template==null?null:NativeVoxelSurface.SHAPES.get(template);
    }
    static Grid reference(State state,Matrix4f[] matrices,Object[] templates) {
        Grid grid=new Grid();
        for(int p=0;p<6;p++)addPart(grid,null,state,matrices,p,shape(templates[p]));
        return grid;
    }
    public static Grid world(State state,Matrix4f[] matrices,Object[] templates) {
        PoseScratch.Workspace workspace=PoseScratch.ACTIVE.get();
        if(workspace==null)return reference(state,matrices,templates);
        // A repeated rank makes PRE35's identity-set tie order observable.
        // Retain its fresh-box construction rather than change that behavior.
        NativeVoxelSurface.Shape[] shapes=new NativeVoxelSurface.Shape[6];
        for(int p=0;p<6;p++) {
            if(state.boxes[p].size()>1){fallbacks++;return reference(state,matrices,templates);}
            shapes[p]=shape(templates[p]);
        }
        Pool pool=workspace.opt3Volumes;
        if(pool==null)workspace.opt3Volumes=pool=new Pool();
        boolean unique=false;
        for(var known:pool.uniqueShapes)if(known!=null) {
            boolean match=true;for(int p=0;p<6;p++)if(known[p]!=shapes[p]){match=false;break;}
            if(match){unique=true;break;}
        }
        if(!unique) {
            HashSet<Integer> ranks=new HashSet<>();
            for(int p=0;p<6;p++) {
                // Reserve all six base ranks, including currently absent cubes.
                // This is conservative if unusual modded ranks collide.
                ranks.add(100+p);
            }
            for(int p=0;p<6;p++)if(shapes[p]!=null)for(NativeVoxelSurface.Bounds b:shapes[p].boxes())
                if(!ranks.add(2000+b.rank()*6+p)){fallbacks++;return reference(state,matrices,templates);}
            pool.uniqueShapes[pool.nextSignature]=shapes;
            pool.nextSignature=(pool.nextSignature+1)%pool.uniqueShapes.length;
        }
        Grid result=new Grid();ArrayList<Box> all=new ArrayList<>();
        for(int p=0;p<6;p++) {
            ArrayList<Entry> entries=pool.entries[p];Entry entry=null;
            for(int i=entries.size()-1;i>=0;i--)if(entries.get(i).matches(state,matrices,templates,p,shapes[p])) {
                entry=entries.remove(i);entries.add(entry);hits++;break;
            }
            if(entry==null) {
                misses++;entry=new Entry(state,matrices,templates,p,shapes[p]);
                if(entry.bytes<=LIMIT) {
                    if(entries.size()==6){pool.bytes-=entries.remove(0).bytes;evictions++;}
                    entries.add(entry);pool.bytes+=entry.bytes;
                }
            }
            entry.lastUse=++pool.clock;result.cells[p]=entry.cells;all.addAll(entry.boxes);
        }
        // The entries are immutable; dropping a cache reference cannot affect
        // the grid currently being cleaned. Nothing outlives its workspace.
        while(pool.bytes>LIMIT) {
            int oldest=-1;long time=Long.MAX_VALUE;
            for(int p=0;p<6;p++)if(!pool.entries[p].isEmpty()&&pool.entries[p].get(0).lastUse<time) {
                oldest=p;time=pool.entries[p].get(0).lastUse;
            }
            if(oldest<0)throw new AssertionError("Volume cache accounting");
            pool.bytes-=pool.entries[oldest].remove(0).bytes;evictions++;
        }
        peakBytes=Math.max(peakBytes,pool.bytes);
        result.opt3Boxes=all.toArray(Box[]::new);
        if(VERIFY)compare(reference(state,matrices,templates),result);
        return result;
    }
    static void value(float a,float b) {
        if(Float.floatToRawIntBits(a)!=Float.floatToRawIntBits(b))throw new AssertionError("Volume float mismatch");
    }
    static void compare(Grid reference,Grid result) {
        for(int p=0;p<6;p++) {
            if(!reference.cells[p].keySet().equals(result.cells[p].keySet()))throw new AssertionError("Volume cell mismatch");
            for(var cell:reference.cells[p].entrySet()) {
                List<Box> a=cell.getValue(),b=result.cells[p].get(cell.getKey());
                if(a.size()!=b.size())throw new AssertionError("Volume occupancy mismatch");
                for(int i=0;i<a.size();i++) {
                    Box x=a.get(i),y=b.get(i);
                    if(x.rank!=y.rank||x.part!=y.part)throw new AssertionError("Volume order mismatch");
                    for(int axis=0;axis<3;axis++) {
                        value(x.lo[axis],y.lo[axis]);value(x.hi[axis],y.hi[axis]);
                        value(x.worldLo[axis],y.worldLo[axis]);value(x.worldHi[axis],y.worldHi[axis]);
                    }
                    float[] xm=new float[16],ym=new float[16];x.inverse.get(xm);y.inverse.get(ym);
                    for(int k=0;k<16;k++)value(xm[k],ym[k]);
                    oracleBoxes++;
                }
            }
        }
        oracleCalls++;
    }
}
