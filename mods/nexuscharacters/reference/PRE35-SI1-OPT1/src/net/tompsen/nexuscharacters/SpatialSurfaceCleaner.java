package net.tompsen.nexuscharacters;

import java.util.*;
import net.tompsen.nexuscharacters.SurfaceGeometry.*;
import net.tompsen.nexuscharacters.PosedSurfaceSupport.Grid;

/** Exact PRE35 subtraction with per-grid integer candidates and per-piece bounds.
 * No pose quantisation, geometric tolerance change, skipped animation, or LOD.
 */
public final class SpatialSurfaceCleaner {
    private SpatialSurfaceCleaner() {}
    private static final boolean VERIFY = Boolean.getBoolean("nexus.opt1.verify");
    public static long calls, queries, queryHits, fallbacks, oracleCalls, oraclePolygons;
    private record Query(int x0,int x1,int y0,int y1,int z0,int z1,int mask) {}

    /** The same axis extrema as Box.near, evaluated once for an immutable piece. */
    static final class Piece {
        final Polygon polygon;
        final float x0,x1,y0,y1,z0,z1;
        Piece(Polygon p) {
            polygon=p;
            float ax=Float.POSITIVE_INFINITY,ay=ax,az=ax;
            float bx=Float.NEGATIVE_INFINITY,by=bx,bz=bx;
            for(Vertex v:p.vertices()) {
                ax=java.lang.Math.min(ax,v.x()); bx=java.lang.Math.max(bx,v.x());
                ay=java.lang.Math.min(ay,v.y()); by=java.lang.Math.max(by,v.y());
                az=java.lang.Math.min(az,v.z()); bz=java.lang.Math.max(bz,v.z());
            }
            x0=ax;x1=bx;y0=ay;y1=by;z0=az;z1=bz;
        }
        boolean near(Box b) {
            return !(x1<b.worldLo[0]-SurfaceGeometry.EPS || x0>b.worldHi[0]+SurfaceGeometry.EPS
                || y1<b.worldLo[1]-SurfaceGeometry.EPS || y0>b.worldHi[1]+SurfaceGeometry.EPS
                || z1<b.worldLo[2]-SurfaceGeometry.EPS || z0>b.worldHi[2]+SurfaceGeometry.EPS);
        }
        boolean unchanged(Box b) {
            if(!near(b))return true;
            // Preserve PRE35's evaluation order, including its null-normal path.
            if(polygon.normal()==null)return b.sameExterior(polygon)&&b.rank<=polygon.rank();
            return b.rank<=polygon.rank()&&b.sameExterior(polygon);
        }
    }

    /** Open-addressed cell lookup; only queried cells acquire integer contents. */
    static final class Cells {
        final long[] keys;
        final List<Box>[] sources;
        final int[][] contents;
        final int mask;
        @SuppressWarnings("unchecked") Cells(Map<Long,List<Box>> source) {
            int size=2;while(size<source.size()*2)size<<=1;
            keys=new long[size];sources=new List[size];contents=new int[size][];mask=size-1;
            for(var entry:source.entrySet()) {
                long k=entry.getKey();int at=hash(k)&mask;
                while(sources[at]!=null)at=(at+1)&mask;
                keys[at]=k;sources[at]=entry.getValue();
            }
        }
        int[] get(long key,Index index) {
            int at=hash(key)&mask;
            while(sources[at]!=null) {
                if(keys[at]==key) {
                    int[] ids=contents[at];
                    if(ids==null) {
                        var list=sources[at];ids=new int[list.size()];
                        for(int i=0;i<ids.length;i++)ids[i]=index.id(list.get(i));
                        contents[at]=ids;
                    }
                    return ids;
                }
                at=(at+1)&mask;
            }
            return null;
        }
        static int hash(long key) {
            key^=key>>>33;key*=0xff51afd7ed558ccdL;key^=key>>>33;
            key*=0xc4ceb9fe1a85ec53L;return (int)(key^(key>>>33));
        }
    }

    /** Owned by one Grid, invalidated by every Grid.add. No global retention. */
    public static final class Index {
        final Box[] boxes, identity;
        final int[] identityIds, stamps, work;
        final Cells[] cells=new Cells[6];
        final Map<Query,int[]> cache=new HashMap<>();
        final int identityMask;
        final boolean uniqueRanks;
        int epoch;
        Index(Grid grid) {
            IdentityHashMap<Box,Boolean> seen=new IdentityHashMap<>();
            ArrayList<Box> ordered=new ArrayList<>();
            for(var map:grid.cells)for(var list:map.values())for(Box b:list)
                if(seen.put(b,Boolean.TRUE)==null)ordered.add(b);
            ordered.sort(Comparator.comparingInt(b->b.rank));
            boxes=ordered.toArray(Box[]::new);boolean unique=true;
            for(int i=1;i<boxes.length;i++)if(boxes[i-1].rank==boxes[i].rank)unique=false;
            uniqueRanks=unique;
            int size=2;while(size<boxes.length*2)size<<=1;
            identity=new Box[size];identityIds=new int[size];identityMask=size-1;
            for(int i=0;i<boxes.length;i++) {
                int at=System.identityHashCode(boxes[i])&identityMask;
                while(identity[at]!=null)at=(at+1)&identityMask;
                identity[at]=boxes[i];identityIds[at]=i;
            }
            stamps=new int[boxes.length];work=new int[boxes.length];
            for(int p=0;p<6;p++)cells[p]=new Cells(grid.cells[p]);
        }
        int id(Box box) {
            int at=System.identityHashCode(box)&identityMask;
            while(identity[at]!=box)at=(at+1)&identityMask;
            return identityIds[at];
        }
        int[] query(Piece p,Grid grid,int allowed) {
            Query q=new Query(grid.cell(p.x0-SurfaceGeometry.EPS),grid.cell(p.x1+SurfaceGeometry.EPS),
                grid.cell(p.y0-SurfaceGeometry.EPS),grid.cell(p.y1+SurfaceGeometry.EPS),
                grid.cell(p.z0-SurfaceGeometry.EPS),grid.cell(p.z1+SurfaceGeometry.EPS),allowed);
            int[] saved=cache.get(q);
            if(saved!=null){queryHits++;return saved;}
            queries++;
            if(++epoch<=0){Arrays.fill(stamps,0);epoch=1;}
            int count=0;
            for(int x=q.x0;x<=q.x1;x++)for(int y=q.y0;y<=q.y1;y++)for(int z=q.z0;z<=q.z1;z++) {
                long key=grid.key(x,y,z);
                for(int part=0;part<6;part++)if((allowed&(1<<part))!=0) {
                    int[] ids=cells[part].get(key,this);if(ids==null)continue;
                    for(int id:ids)if(stamps[id]!=epoch){stamps[id]=epoch;work[count++]=id;}
                }
            }
            Arrays.sort(work,0,count);
            int[] answer=Arrays.copyOf(work,count);
            if(cache.size()<4096)cache.put(q,answer);
            return answer;
        }
    }

    public static List<Polygon> clean(List<Polygon> input,Grid grid,int part,
            boolean base,boolean first,boolean skipOwn) {
        calls++;
        Index index=grid.opt1Index;
        if(index==null)grid.opt1Index=index=new Index(grid);
        List<Polygon> result;
        if(!index.uniqueRanks) {
            // Equal ranks require the exact legacy identity-set tie order.
            fallbacks++;result=ExactSurfaceCleaner.cleanPRE35(input,grid,part,base,first,skipOwn);
        } else result=cleanIndexed(input,grid,index,part,base,first,skipOwn);
        if(VERIFY) {
            var reference=ExactSurfaceCleaner.cleanPRE35(input,grid,part,base,first,skipOwn);
            compare(reference,result);oracleCalls++;
        }
        return result;
    }

    static List<Polygon> cleanIndexed(List<Polygon> input,Grid grid,Index index,int part,
            boolean base,boolean first,boolean skipOwn) {
        int allowed=63;
        if(skipOwn||base)allowed&=~(1<<part);
        if(first)allowed&=1<<part;
        if(allowed==0)return new ArrayList<>(input);
        ArrayList<Polygon> out=new ArrayList<>(input.size());
        for(Polygon source:input) {
            Piece initial=new Piece(source);
            List<Piece> fragments=null;
            for(int id:index.query(initial,grid,allowed)) {
                Box box=index.boxes[id];
                if(fragments==null) {
                    if(initial.unchanged(box))continue;
                    var clipped=ExactSurfaceClipper.intersecting(source,box,!skipOwn);
                    fragments=new ArrayList<>(clipped.size());
                    for(Polygon p:clipped)fragments.add(new Piece(p));
                } else {
                    List<Piece> next=null;
                    for(int i=0;i<fragments.size();i++) {
                        Piece piece=fragments.get(i);
                        if(piece.unchanged(box)) {if(next!=null)next.add(piece);}
                        else {
                            if(next==null){next=new ArrayList<>();for(int j=0;j<i;j++)next.add(fragments.get(j));}
                            for(Polygon p:ExactSurfaceClipper.intersecting(piece.polygon,box,!skipOwn))next.add(new Piece(p));
                        }
                    }
                    if(next!=null)fragments=next;
                }
                if(fragments.isEmpty())break;
            }
            if(fragments==null)out.add(source);else for(Piece p:fragments)out.add(p.polygon);
        }
        return out;
    }

    static void value(float a,float b) {
        if(Float.floatToRawIntBits(a)!=Float.floatToRawIntBits(b))throw new AssertionError("OPT1 float differs: "+a+" / "+b);
    }
    static void compare(List<Polygon> a,List<Polygon> b) {
        if(a.size()!=b.size())throw new AssertionError("OPT1 polygon count differs: "+a.size()+" / "+b.size());
        for(int i=0;i<a.size();i++) {
            Polygon x=a.get(i),y=b.get(i);oraclePolygons++;
            if(x.rank()!=y.rank()||x.vertices().size()!=y.vertices().size())throw new AssertionError("OPT1 polygon topology differs");
            if(x.normal()==null||y.normal()==null){if(x.normal()!=y.normal())throw new AssertionError("OPT1 normal differs");}
            else {value(x.normal().x,y.normal().x);value(x.normal().y,y.normal().y);value(x.normal().z,y.normal().z);}
            for(int v=0;v<x.vertices().size();v++) {
                Vertex p=x.vertices().get(v),q=y.vertices().get(v);
                value(p.x(),q.x());value(p.y(),q.y());value(p.z(),q.z());value(p.u(),q.u());value(p.v(),q.v());
            }
        }
    }
}
