package net.tompsen.nexuscharacters;

import java.util.*;
import org.joml.Matrix4f;
import net.tompsen.nexuscharacters.SurfaceGeometry.*;
import net.tompsen.nexuscharacters.PosedSurfaceSupport.Grid;

/** Exact part reuse. A dependency may be omitted only if its occupied grid-cell
 * range cannot intersect any original input polygon's candidate-cell range.
 * Cache lifetime follows the existing PRE35 workspace/cube/resource lifetime.
 */
public final class PartSurfaceReuse {
    private static final long LIMIT=8L*1024*1024;
    public static long hits, stores, evictions, peakBytes;
    private PartSurfaceReuse() {}

    public static final class Pool {
        final LinkedList<Profile> profiles=new LinkedList<>();
        long bytes;
    }
    static final class Profile {
        final Object[] templates,shapes=new Object[6];
        final Entry[] entries=new Entry[12];
        long bytes;
        Profile(PoseScratch.Workspace w) {
            templates=w.templates.clone();
            for(int p=0;p<6;p++)shapes[p]=shape(templates[p]);
        }
        boolean matches(PoseScratch.Workspace w) {
            for(int p=0;p<6;p++)if(templates[p]!=w.templates[p]||shapes[p]!=shape(w.templates[p]))return false;
            return true;
        }
    }
    static final class Entry {
        final List<Polygon> input,output;
        final Matrix4f[] matrices=new Matrix4f[12];
        final int mask;
        final long bytes;
        Entry(List<Polygon> in,List<Polygon> out,int mask,PoseScratch.Workspace w) {
            input=in;output=out;this.mask=mask;
            for(int p=0;p<6;p++)if((mask&(1<<p))!=0) {
                matrices[p]=new Matrix4f(w.matrices[p]);
                matrices[p+6]=new Matrix4f(w.matrices[p+6]);
            }
            bytes=1024+estimate(in)+estimate(out);
        }
        boolean matches(List<Polygon> next,int nextMask,PoseScratch.Workspace w) {
            if(mask!=nextMask)return false;
            for(int p=0;p<6;p++)if((mask&(1<<p))!=0)
                if(!matrices[p].equals(w.matrices[p])||!matrices[p+6].equals(w.matrices[p+6]))return false;
            return input.equals(next);
        }
    }
    record Token(Pool pool,Profile profile,int slot,int mask,PoseScratch.Workspace workspace,List<Polygon> hit) {}
    static Object shape(Object template) {return template==null?null:NativeVoxelSurface.SHAPES.get(template);}
    static long estimate(List<Polygon> polygons) {
        long bytes=32L+8L*polygons.size();
        for(Polygon p:polygons)bytes+=256L+48L*p.vertices().size();
        return bytes;
    }
    static Token lookup(List<Polygon> input,Grid grid,SpatialSurfaceCleaner.Index index,int part,boolean base) {
        if(input.isEmpty()||!index.bounded)return null;
        PoseScratch.Workspace w=PoseScratch.ACTIVE.get();
        if(w==null)return null;
        int x0=Integer.MAX_VALUE,y0=x0,z0=x0,x1=Integer.MIN_VALUE,y1=x1,z1=x1;
        for(Polygon p:input) {
          if(p.normal()!=null&&(!Float.isFinite(p.normal().x)||!Float.isFinite(p.normal().y)||!Float.isFinite(p.normal().z)))return null;
          for(Vertex v:p.vertices()) {
            if(!Float.isFinite(v.x())||!Float.isFinite(v.y())||!Float.isFinite(v.z())
                    ||!Float.isFinite(v.u())||!Float.isFinite(v.v())
                    ||java.lang.Math.abs(v.x())>32||java.lang.Math.abs(v.y())>32||java.lang.Math.abs(v.z())>32)return null;
            x0=java.lang.Math.min(x0,grid.cell(v.x()-SurfaceGeometry.EPS));
            x1=java.lang.Math.max(x1,grid.cell(v.x()+SurfaceGeometry.EPS));
            y0=java.lang.Math.min(y0,grid.cell(v.y()-SurfaceGeometry.EPS));
            y1=java.lang.Math.max(y1,grid.cell(v.y()+SurfaceGeometry.EPS));
            z0=java.lang.Math.min(z0,grid.cell(v.z()-SurfaceGeometry.EPS));
            z1=java.lang.Math.max(z1,grid.cell(v.z()+SurfaceGeometry.EPS));
          }
        }
        int mask=0;
        for(int p=0;p<6;p++)if(p!=part
            &&index.partLo[p][0]<=x1&&index.partHi[p][0]>=x0
            &&index.partLo[p][1]<=y1&&index.partHi[p][1]>=y0
            &&index.partLo[p][2]<=z1&&index.partHi[p][2]>=z0)mask|=1<<p;
        Pool pool=w.opt1Reuse;
        if(pool==null)w.opt1Reuse=pool=new Pool();
        Profile profile=null;
        for(var it=pool.profiles.iterator();it.hasNext();) {
            Profile candidate=it.next();
            if(candidate.matches(w)){profile=candidate;it.remove();break;}
        }
        if(profile==null)profile=new Profile(w);
        pool.profiles.addLast(profile);
        while(pool.profiles.size()>6)evict(pool);
        int slot=part+(base?0:6);Entry e=profile.entries[slot];
        List<Polygon> hit=e!=null&&e.matches(input,mask,w)?e.output:null;
        if(hit!=null)hits++;
        return new Token(pool,profile,slot,mask,w,hit);
    }
    static void store(Token token,List<Polygon> input,List<Polygon> output) {
        Entry next=new Entry(input,output,token.mask,token.workspace);
        if(next.bytes>LIMIT)return;
        Pool pool=token.pool;Profile profile=token.profile;Entry old=profile.entries[token.slot];
        long delta=next.bytes-(old==null?0:old.bytes);
        profile.entries[token.slot]=next;profile.bytes+=delta;pool.bytes+=delta;stores++;
        while(pool.bytes>LIMIT&&!pool.profiles.isEmpty())evict(pool);
        peakBytes=java.lang.Math.max(peakBytes,pool.bytes);
    }
    static void evict(Pool pool) {Profile old=pool.profiles.removeFirst();pool.bytes-=old.bytes;evictions++;}
}
