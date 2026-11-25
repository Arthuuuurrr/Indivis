package net.tompsen.nexuscharacters;

import java.util.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/** Immutable local meshes follow the model's ordinary bone transforms. Only
 * polygons whose WORLD bounds can meet another part use the original clipping.
 * Output order, UVs and contact clipping are preserved. Eliminating a redundant
 * world/inverse round-trip changes rigid coordinates by float round-off only. */
public final class ArmorPreparedSurface {
    private static final boolean VERIFY = Boolean.getBoolean("nexus.armor.verify") || Boolean.getBoolean("nexus.opt1.verify");
    private static final long LIMIT = 32L * 1024 * 1024;
    private static final int MAX_SOURCES = 256;
    private static final IdentityHashMap<List<SurfaceGeometry.Polygon>, Source> CACHE = new IdentityHashMap<>();
    private static Source oldest, newest;
    private static long bytes;
    private static final ThreadLocal<Work> WORK = ThreadLocal.withInitial(Work::new);
    public static long bakes, cacheHits, evictions, peakBytes, parts, fullyRigidParts;
    public static long rigidPolygons, contactPolygons, fallbacks, oracleCalls, oracleFloats;
    public static double maxPositionError, maxNormalError;

    private ArmorPreparedSurface() {}

    private static final class RigidInput extends AbstractList<SurfaceGeometry.Polygon> implements RandomAccess {
        final List<SurfaceGeometry.Polygon> local;
        final Matrix4f matrix;
        private List<SurfaceGeometry.Polygon> world;
        RigidInput(List<SurfaceGeometry.Polygon> input, Matrix4f pose) { local=input; matrix=new Matrix4f(pose); }
        List<SurfaceGeometry.Polygon> world() { if(world==null)world=SurfaceGeometry.transform(local,matrix);return world; }
        public int size(){return local.size();}
        public SurfaceGeometry.Polygon get(int i){return world().get(i);}
    }
    private static final class Plan extends AbstractList<SurfaceGeometry.Polygon> {
        final RigidInput input;
        final PosedSurfaceSupport.Grid grid;
        final int part;
        final boolean base,first,world;
        private List<SurfaceGeometry.Polygon> reference;
        Plan(RigidInput i,PosedSurfaceSupport.Grid g,int p,boolean b,boolean f,boolean w){input=i;grid=g;part=p;base=b;first=f;world=w;}
        List<SurfaceGeometry.Polygon> reference(){if(reference==null)reference=PosedSurfaceSupport.clean(input.world(),grid,part,base,first,world);return reference;}
        public int size(){return reference().size();}
        public SurfaceGeometry.Polygon get(int i){return reference().get(i);}
    }
    public static List<SurfaceGeometry.Polygon> transform(List<SurfaceGeometry.Polygon> input,Matrix4f matrix){return new RigidInput(input,matrix);}
    public static List<SurfaceGeometry.Polygon> clean(List<SurfaceGeometry.Polygon> input,PosedSurfaceSupport.Grid grid,int part,boolean base,boolean first,boolean world){
        if(input instanceof RigidInput lazy && world && !first)return new Plan(lazy,grid,part,base,first,world);
        return PosedSurfaceSupport.clean(input,grid,part,base,first,world);
    }
    public static float[] write(List<SurfaceGeometry.Polygon> polygons,Matrix4f inverse){
        if(!(polygons instanceof Plan plan))return PackedSurfaceOutput.write(polygons,inverse);
        if(plan.input.local.size()>16_000 || !supported(plan.input.matrix) || !supported(inverse)){
            fallbacks++;return PackedSurfaceOutput.write(plan.reference(),inverse);
        }
        Source source=source(plan.input.local);
        if(!source.supported){fallbacks++;return PackedSurfaceOutput.write(plan.reference(),inverse);}
        Work w=WORK.get();
        try{
            float[] data=prepare(source,plan,inverse,w);parts++;
            if(VERIFY){same(PackedSurfaceOutput.write(plan.reference(),inverse),data);oracleCalls++;}
            return data;
        }finally{w.chunks.clear();}
    }
    private static boolean supported(Matrix4f m){
        if(m.m03()!=0 || m.m13()!=0 || m.m23()!=0 || m.m33()!=1)return false;
        for(int c=0;c<4;c++)for(int r=0;r<4;r++){float v=m.get(c,r);if(!Float.isFinite(v)||Math.abs(v)>8)return false;}
        return Math.abs(m.determinant())>.01f;
    }
    private static synchronized Source source(List<SurfaceGeometry.Polygon> input){
        Source s=CACHE.get(input);
        if(s!=null){cacheHits++;unlink(s);append(s);return s;}
        s=new Source(input);bakes++;
        if(s.bytes<=LIMIT/2){CACHE.put(input,s);append(s);bytes+=s.bytes;
            while(bytes>LIMIT||CACHE.size()>MAX_SOURCES){Source e=oldest;unlink(e);CACHE.remove(e.input);bytes-=e.bytes;evictions++;}
            peakBytes=Math.max(peakBytes,bytes);
        }
        return s;
    }
    private static void unlink(Source s){if(s.prev!=null)s.prev.next=s.next;else oldest=s.next;if(s.next!=null)s.next.prev=s.prev;else newest=s.prev;s.prev=s.next=null;}
    private static void append(Source s){s.prev=newest;s.next=null;if(newest!=null)newest.next=s;else oldest=s;newest=s;}

    private static final class Source {
        final List<SurfaceGeometry.Polygon> input;
        final float[] data,bounds;
        final int[] offsets,order;
        final boolean[] tiny;
        final Node root;
        final boolean supported;
        final long bytes;
        Source prev,next;
        Source(List<SurfaceGeometry.Polygon> input){
            this.input=input;int n=input.size();bounds=new float[n*6];offsets=new int[n+1];order=new int[n];tiny=new boolean[n];
            boolean ok=n<=100_000;Vector3f a=new Vector3f(),b=new Vector3f();
            for(int i=0;i<n;i++){
                var p=input.get(i);order[i]=i;
                var normal=p.normal();if(normal==null||!Float.isFinite(normal.x)||!Float.isFinite(normal.y)||!Float.isFinite(normal.z)||normal.lengthSquared()<1e-12f)ok=false;
                float x0=Float.POSITIVE_INFINITY,y0=x0,z0=x0,x1=Float.NEGATIVE_INFINITY,y1=x1,z1=x1;
                for(var v:p.vertices()){
                    if(!Float.isFinite(v.x())||!Float.isFinite(v.y())||!Float.isFinite(v.z())||!Float.isFinite(v.u())||!Float.isFinite(v.v())||Math.abs(v.x())>8||Math.abs(v.y())>8||Math.abs(v.z())>8)ok=false;
                    x0=Math.min(x0,v.x());x1=Math.max(x1,v.x());y0=Math.min(y0,v.y());y1=Math.max(y1,v.y());z0=Math.min(z0,v.z());z1=Math.max(z1,v.z());
                }
                int k=i*6;bounds[k]=x0;bounds[k+1]=y0;bounds[k+2]=z0;bounds[k+3]=x1;bounds[k+4]=y1;bounds[k+5]=z1;
                float area=0;if(!p.vertices().isEmpty()){var v=p.vertices().get(0);for(int j=1;j+1<p.vertices().size();j++){var q=p.vertices().get(j);var t=p.vertices().get(j+1);area+=a.set(q.x()-v.x(),q.y()-v.y(),q.z()-v.z()).cross(b.set(t.x()-v.x(),t.y()-v.y(),t.z()-v.z())).length();}}
                tiny[i]=!(area>1e-6f);
            }
            supported=ok;
            if(ok){
                var normalized=SurfaceGeometry.transform(input,new Matrix4f());data=SurfaceGeometry.write(normalized);
                int at=0;for(int i=0;i<n;i++){offsets[i]=at;var vs=normalized.get(i).vertices();if(SurfaceGeometry.valid(vs))at+=(vs.size()==4?1:vs.size()-2)*23;}offsets[n]=at;
                if(at!=data.length)throw new AssertionError("Rigid mesh offsets differ");root=n==0?null:new Node(this,0,n);
            }else{data=new float[0];root=null;}
            bytes=256L+PartSurfaceReuse.estimate(input)+4L*(data.length+bounds.length+offsets.length+order.length)+tiny.length+128L*n;
        }
        double center(int index,int axis){int k=index*6;return ((double)bounds[k+axis]+bounds[k+axis+3])*.5;}
        int compare(int a,int b,int axis){int c=Double.compare(center(a,axis),center(b,axis));return c==0?Integer.compare(a,b):c;}
        void select(int lo,int hi,int middle,int axis){
            while(lo<hi){int i=lo,j=hi,pivot=order[(lo+hi)>>>1];while(i<=j){while(compare(order[i],pivot,axis)<0)i++;while(compare(order[j],pivot,axis)>0)j--;if(i<=j){int t=order[i];order[i++]=order[j];order[j--]=t;}}
                if(middle<=j)hi=j;else if(middle>=i)lo=i;else return;
            }
        }
    }
    private static final class Node {
        final int from,to;final float[] box=new float[6];final Node left,right;
        Node(Source s,int from,int to){
            this.from=from;this.to=to;Arrays.fill(box,0,3,Float.POSITIVE_INFINITY);Arrays.fill(box,3,6,Float.NEGATIVE_INFINITY);
            for(int i=from;i<to;i++){int k=s.order[i]*6;for(int axis=0;axis<3;axis++){box[axis]=Math.min(box[axis],s.bounds[k+axis]);box[axis+3]=Math.max(box[axis+3],s.bounds[k+axis+3]);}}
            if(to-from>16){int axis=0;for(int a=1;a<3;a++)if(box[a+3]-box[a]>box[axis+3]-box[axis])axis=a;int mid=(from+to)>>>1;s.select(from,to-1,mid,axis);left=new Node(s,from,mid);right=new Node(s,mid,to);}else{left=right=null;}
        }
        void classify(Source s,Matrix4f pose,Work w,int part){
            if(!near(box,0,pose,w.volumes,part))return;
            if(left!=null){left.classify(s,pose,w,part);right.classify(s,pose,w,part);}
            else for(int i=from;i<to;i++){int id=s.order[i];if(near(s.bounds,id*6,pose,w.volumes,part))w.dynamic[id]=true;}
        }
    }
    /** World-space conservative broad phase. Testing inverse/local hulls here
     * would NOT preserve the original six-plane subtraction's subdivisions. */
    private static boolean near(float[] b,int k,Matrix4f m,ArmorContactVolumes.Frame volumes,int own){
        double cx=((double)b[k]+b[k+3])*.5,cy=((double)b[k+1]+b[k+4])*.5,cz=((double)b[k+2]+b[k+5])*.5;
        double ex=((double)b[k+3]-b[k])*.5,ey=((double)b[k+4]-b[k+1])*.5,ez=((double)b[k+5]-b[k+2])*.5;
        double x=m.m00()*cx+m.m10()*cy+m.m20()*cz+m.m30(),y=m.m01()*cx+m.m11()*cy+m.m21()*cz+m.m31(),z=m.m02()*cx+m.m12()*cy+m.m22()*cz+m.m32();
        double dx=Math.abs(m.m00())*ex+Math.abs(m.m10())*ey+Math.abs(m.m20())*ez,dy=Math.abs(m.m01())*ex+Math.abs(m.m11())*ey+Math.abs(m.m21())*ez,dz=Math.abs(m.m02())*ex+Math.abs(m.m12())*ey+Math.abs(m.m22())*ez;
        double magnitude=Math.max(Math.abs(x)+dx,Math.max(Math.abs(y)+dy,Math.abs(z)+dz));double pad=1e-4+128d*Math.ulp((float)magnitude);
        return volumes.near(x-dx-pad,y-dy-pad,z-dz-pad,x+dx+pad,y+dy+pad,z+dz+pad,own);
    }
    private record Chunk(float[] data,int offset,int length){}
    private static final class Work {
        final ArmorContactVolumes.Frame volumes=new ArmorContactVolumes.Frame();
        boolean[] dynamic=new boolean[128];
        final ArrayList<Chunk> chunks=new ArrayList<>();
    }
    private static SpatialSurfaceCleaner.Index index(PosedSurfaceSupport.Grid grid){var i=grid.opt1Index;if(i==null)grid.opt1Index=i=new SpatialSurfaceCleaner.Index(grid);return i;}
    private static float[] prepare(Source s,Plan p,Matrix4f inverse,Work w){
        int n=s.input.size();if(w.dynamic.length<n)w.dynamic=new boolean[n];System.arraycopy(s.tiny,0,w.dynamic,0,n);w.volumes.prepare(p.grid);
        if(s.root!=null)s.root.classify(s,p.input.matrix,w,p.part);
        int contacts=0;for(int i=0;i<n;i++)if(w.dynamic[i])contacts++;contactPolygons+=contacts;rigidPolygons+=n-contacts;
        if(contacts==0){fullyRigidParts++;return s.data;}
        int total=0;
        for(int from=0;from<n;){boolean dynamic=w.dynamic[from];int to=from+1;while(to<n&&w.dynamic[to]==dynamic)to++;
            if(!dynamic){int start=s.offsets[from],length=s.offsets[to]-start;w.chunks.add(new Chunk(s.data,start,length));total=Math.addExact(total,length);}
            else{
                var world=SurfaceGeometry.transform(s.input.subList(from,to),p.input.matrix);
                List<SurfaceGeometry.Polygon> clipped;
                if(VERIFY)clipped=PosedSurfaceSupport.clean(world,p.grid,p.part,p.base,p.first,p.world);
                else{var idx=index(p.grid);clipped=idx.uniqueRanks?SpatialSurfaceCleaner.cleanIndexed(world,p.grid,idx,p.part,p.base,p.first,p.world):ExactSurfaceCleaner.cleanPRE35(world,p.grid,p.part,p.base,p.first,p.world);}
                float[] data=PackedSurfaceOutput.write(clipped,inverse);w.chunks.add(new Chunk(data,0,data.length));total=Math.addExact(total,data.length);
            }
            from=to;
        }
        float[] out=new float[total];int at=0;for(var c:w.chunks){System.arraycopy(c.data,c.offset,out,at,c.length);at+=c.length;}return out;
    }
    static void same(float[] reference,float[] actual){
        if(reference.length!=actual.length)throw new AssertionError("Prepared mesh topology differs "+reference.length+"/"+actual.length);
        for(int i=0;i<reference.length;i++){
            boolean normal=i%23<3,uv=!normal&&(i%23-3)%5>=3;
            if(uv){if(Float.floatToRawIntBits(reference[i])!=Float.floatToRawIntBits(actual[i]))throw new AssertionError("Prepared UV differs at "+i);}
            else{
                double error=Math.abs((double)reference[i]-actual[i]);float scale=Math.max(Math.abs(reference[i]),Math.abs(actual[i]));double tolerance=2e-6+8d*Math.ulp(scale);
                if(!Double.isFinite(error)||error>tolerance)throw new AssertionError("Prepared "+(normal?"normal":"position")+" differs at "+i+": "+reference[i]+"/"+actual[i]+" error="+error+" limit="+tolerance);
                if(normal)maxNormalError=Math.max(maxNormalError,error);else maxPositionError=Math.max(maxPositionError,error);
            }
            oracleFloats++;
        }
    }
    public static synchronized void clear(){CACHE.clear();oldest=newest=null;bytes=0;WORK.remove();ArmorContactVolumes.clear();}
}
