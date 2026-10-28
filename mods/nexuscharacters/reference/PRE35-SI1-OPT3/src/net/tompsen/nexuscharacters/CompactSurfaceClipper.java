package net.tompsen.nexuscharacters;
import java.util.*;
import org.joml.Vector3f;
import net.tompsen.nexuscharacters.SurfaceGeometry.*;

/** Same six-plane arithmetic as OPT2; only non-escaping inside lists are reused. */
public final class CompactSurfaceClipper {
    static final boolean VERIFY=Boolean.getBoolean("nexus.opt1.verify");
    private static final ThreadLocal<Workspace> WORK=ThreadLocal.withInitial(Workspace::new);
    public static long oracleCalls,oraclePolygons;
    private CompactSurfaceClipper() {}
    static final class Workspace {
        float[] first=new float[72],second=new float[72];
        final Vector3f projection=new Vector3f();
        final ArrayList<Vertex> a=new ArrayList<>(8),b=new ArrayList<>(8);
        float[] capacity(float[] array,int n) {
            if(n<=array.length)return array;
            float[] larger=Arrays.copyOf(array,Math.max(n,array.length*2));
            if(larger.length<=3072) {if(array==first)first=larger;else if(array==second)second=larger;}
            return larger;
        }
        ArrayList<Vertex> inside(List<Vertex> current,int n) {
            if(n>1024)return new ArrayList<>(n+2);
            ArrayList<Vertex> answer=current==a?b:a;answer.clear();answer.ensureCapacity(n+2);return answer;
        }
    }
    static void project(Vertex v,Box box,Workspace w,float[] out,int at) {
        Vector3f p=box.inverse.transformPosition(w.projection.set(v.x(),v.y(),v.z()));
        out[at]=p.x;out[at+1]=p.y;out[at+2]=p.z;
    }
    public static List<Polygon> intersecting(Polygon p,Box box,boolean strict) {
        Workspace w=WORK.get();
        try {
            List<Polygon> result=clip(p,box,strict,w);
            if(VERIFY) {
                List<Polygon> reference=ExactSurfaceClipper.intersectingOPT2(p,box,strict);
                SpatialSurfaceCleaner.compare(reference,result);oracleCalls++;oraclePolygons+=reference.size();
            }
            return result;
        }finally {w.a.clear();w.b.clear();}
    }
    static List<Polygon> clip(Polygon p,Box box,boolean strict,Workspace w) {
        List<Vertex> list=p.vertices();ArrayList<Polygon> result=new ArrayList<>();
        float[] first=w.capacity(w.first,list.size()*3),second=w.second;
        for(int i=0;i<list.size();i++)project(list.get(i),box,w,first,i*3);
        float positive=strict?0.0f:2.9999999E-6f,negative=strict?0.0f:-2.9999999E-6f;
        for(int plane=0;plane<6&&!list.isEmpty();plane++) {
            int axis=plane/2,n=list.size(),outsideCount=0,insideCount=0;
            for(int i=0;i<n;i++) {
                float coordinate=first[i*3+axis];
                float distance=plane%2==0?box.lo[axis]-coordinate:coordinate-box.hi[axis];
                if(distance>positive)outsideCount++;
                if(-distance>=negative)insideCount++;
            }
            if(insideCount==n&&outsideCount==0)continue;
            if(outsideCount==n&&insideCount==0) {
                if(SurfaceGeometry.valid(list))result.add(new Polygon(new ArrayList<>(list),p.normal(),p.rank()));
                break;
            }
            ArrayList<Vertex> outside=new ArrayList<>(n+2),inside=w.inside(list,n);
            second=w.capacity(second,(n*2+2)*3);
            for(int i=0;i<n;i++) {
                int next=(i+1)%n;Vertex a=list.get(i),b=list.get(next);
                float ca=first[i*3+axis],cb=first[next*3+axis];
                float da=plane%2==0?box.lo[axis]-ca:ca-box.hi[axis];
                float db=plane%2==0?box.lo[axis]-cb:cb-box.hi[axis];
                boolean oa=da>positive,ob=db>positive;
                if(oa)outside.add(a);
                if(oa!=ob) {
                    float t=(da-positive)/(da-db);
                    if(Float.isFinite(t))outside.add(a.mix(b,Math.max(0.0f,Math.min(1.0f,t))));
                }
                float ia=da*-1.0f,ib=db*-1.0f;boolean ina=ia>=negative,inb=ib>=negative;
                if(ina) {int at=inside.size()*3;inside.add(a);System.arraycopy(first,i*3,second,at,3);}
                if(ina!=inb) {
                    float t=(ia-negative)/(ia-ib);
                    if(Float.isFinite(t)) {
                        Vertex v=a.mix(b,Math.max(0.0f,Math.min(1.0f,t)));
                        project(v,box,w,second,inside.size()*3);inside.add(v);
                    }
                }
            }
            if(SurfaceGeometry.valid(outside))result.add(new Polygon(outside,p.normal(),p.rank()));
            list=inside;float[] previous=first;first=second;second=previous;
        }
        return result;
    }
}
