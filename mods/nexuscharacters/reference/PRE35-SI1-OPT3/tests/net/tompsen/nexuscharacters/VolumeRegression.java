package net.tompsen.nexuscharacters;
import java.util.*;
import org.joml.Matrix4f;
import net.tompsen.nexuscharacters.PosedSurfaceSupport.*;
import net.tompsen.nexuscharacters.SurfaceGeometry.*;

public final class VolumeRegression {
    public static void main(String[] args) {
        State state=new State();Matrix4f[] matrices=new Matrix4f[12];Object[] templates=new Object[6];
        for(int i=0;i<12;i++)matrices[i]=new Matrix4f();
        for(int i=0;i<6;i++)state.boxes[i]=List.<float[][]>of(new float[][]{{-.125f,-.3f,-.125f},{.125f,.3f,.125f}});
        PoseScratch.Workspace workspace=new PoseScratch.Workspace();workspace.templates=templates;workspace.matrices=matrices;PoseScratch.ACTIVE.set(workspace);
        Random r=new Random(0x564f4c554d45L);int cases=0;
        for(int trial=0;trial<500;trial++) {
            int part=trial%6;
            matrices[part].identity().translate(r.nextFloat()-.5f,r.nextFloat()-.5f,r.nextFloat()-.5f)
                .rotateXYZ(r.nextFloat(),r.nextFloat(),r.nextFloat()).scale(.7f+r.nextFloat(),.7f+r.nextFloat(),.7f+r.nextFloat());
            matrices[part+6].set(matrices[part]);
            Grid a=PartVolumeCache.reference(state,matrices,templates),b=PartVolumeCache.world(state,matrices,templates);
            PartVolumeCache.compare(a,b);
            PartVolumeCache.compare(a,PartVolumeCache.world(state,matrices,templates));cases+=2;
            List<Polygon> polygons=List.of(new Polygon(List.of(new Vertex(-.2f,-.2f,0,0,0),new Vertex(.2f,-.2f,0,1,0),new Vertex(.2f,.2f,0,1,1),new Vertex(-.2f,.2f,0,0,1)),new org.joml.Vector3f(0,0,1),500));
            SpatialSurfaceCleaner.compare(SpatialSurfaceCleaner.clean(polygons,a,part,false,false,true),SpatialSurfaceCleaner.clean(polygons,b,part,false,false,true));
            if(workspace.opt3Volumes.bytes>PartVolumeCache.LIMIT)throw new AssertionError("Volume memory bound");
            if(trial%50==0) {
                state.boxes[part]=List.<float[][]>of(new float[][]{{-.13f,-.3f,-.125f},{.13f,.3f,.125f}});
                PartVolumeCache.compare(PartVolumeCache.reference(state,matrices,templates),PartVolumeCache.world(state,matrices,templates));cases++;
            }
        }
        if(PartVolumeCache.hits<500)throw new AssertionError("Cache never hit");
        // Many distinct poses must evict entries without flushing all stable
        // members, leaking memory or retaining stale native shape identities.
        ArrayList<NativeVoxelSurface.Bounds> bounds=new ArrayList<>();
        for(int k=0;k<1000;k++) {
            float x=(k%10)*.003f,y=((k/10)%10)*.003f,z=(k/100)*.003f;
            bounds.add(new NativeVoxelSurface.Bounds(new float[]{x,y,z},new float[]{x+.001f,y+.001f,z+.001f},10000+k));
        }
        for(int p=0;p<6;p++) {
            templates[p]=new Object();NativeVoxelSurface.SHAPES.put(templates[p],new NativeVoxelSurface.Shape(List.of(),List.copyOf(bounds)));
        }
        for(int f=0;f<24;f++) {
            for(int p=0;p<12;p++)matrices[p].identity().rotateXYZ(f*.002f,p*.01f,f*.003f);
            PartVolumeCache.compare(PartVolumeCache.reference(state,matrices,templates),PartVolumeCache.world(state,matrices,templates));cases++;
            if(workspace.opt3Volumes.bytes>PartVolumeCache.LIMIT)throw new AssertionError("Pressure memory bound");
        }
        if(PartVolumeCache.peakBytes<PartVolumeCache.LIMIT/2||PartVolumeCache.evictions==0)throw new AssertionError("Pressure fixture did not exercise bound");
        // Same template identity, new shape identity (resource/live option reload).
        NativeVoxelSurface.SHAPES.put(templates[0],new NativeVoxelSurface.Shape(List.of(),List.of(bounds.get(0))));
        PartVolumeCache.compare(PartVolumeCache.reference(state,matrices,templates),PartVolumeCache.world(state,matrices,templates));cases++;
        for(Object template:templates)NativeVoxelSurface.SHAPES.remove(template);
        state.boxes[0]=List.<float[][]>of(new float[][]{{0,0,0},{.1f,.1f,.1f}},new float[][]{{0,0,0},{.2f,.2f,.2f}});
        PartVolumeCache.world(state,matrices,templates);
        if(PartVolumeCache.fallbacks==0)throw new AssertionError("Tied rank fallback absent");
        PoseScratch.ACTIVE.remove();
        PartVolumeCache.compare(PartVolumeCache.reference(state,matrices,templates),PartVolumeCache.world(state,matrices,templates));
        System.out.println("EXACT_VOLUMES_PASS cases="+cases+" hits="+PartVolumeCache.hits+" misses="+PartVolumeCache.misses+" peak="+PartVolumeCache.peakBytes+" fallbacks="+PartVolumeCache.fallbacks);
    }
}
