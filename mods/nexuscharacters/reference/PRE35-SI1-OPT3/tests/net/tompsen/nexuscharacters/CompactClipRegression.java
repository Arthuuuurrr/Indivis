package net.tompsen.nexuscharacters;
import java.util.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import net.tompsen.nexuscharacters.SurfaceGeometry.*;
public final class CompactClipRegression {
    public static void main(String[] args) {
        Random r=new Random(0x434c495033L);int cases=0;
        List<List<Polygon>> escaped=new ArrayList<>(),copies=new ArrayList<>();
        for(int trial=0;trial<3000;trial++) {
            Matrix4f m=new Matrix4f().translate(r.nextFloat()-.5f,r.nextFloat()-.5f,r.nextFloat()-.5f)
                .rotateXYZ(r.nextFloat(),r.nextFloat(),r.nextFloat()).scale(.5f+r.nextFloat(),.5f+r.nextFloat(),.5f+r.nextFloat());
            if(trial%53==0)m.scale(0);
            Box box=new Box(new float[]{-.15f,-.2f,-.25f},new float[]{.15f,.2f,.25f},m,trial,trial%6);
            int count=trial%71==0?1100:3+trial%12;
            ArrayList<Vertex> vertices=new ArrayList<>();
            for(int i=0;i<count;i++) {
                float x=(float)java.lang.Math.cos(i*2*java.lang.Math.PI/count)*.3f;
                float y=(float)java.lang.Math.sin(i*2*java.lang.Math.PI/count)*.3f;
                vertices.add(new Vertex(x,y,(trial%9-4)*.05f,r.nextFloat(),r.nextFloat()));
            }
            if(trial%47==0)vertices.set(0,new Vertex(Float.NaN,0,0,0,0));
            if(trial%43==0)vertices.set(1,new Vertex(-0.0f,Float.POSITIVE_INFINITY,0,0,0));
            Polygon polygon=new Polygon(vertices,trial%17==0?null:new Vector3f(0,0,1),trial%37);
            for(boolean strict:new boolean[]{false,true}) {
                List<Polygon> reference=ExactSurfaceClipper.intersectingOPT2(polygon,box,strict);
                List<Polygon> candidate=CompactSurfaceClipper.intersecting(polygon,box,strict);
                SpatialSurfaceCleaner.compare(reference,candidate);cases++;
                if(trial%100==0){escaped.add(candidate);copies.add(reference);}
            }
        }
        for(int i=0;i<escaped.size();i++)SpatialSurfaceCleaner.compare(copies.get(i),escaped.get(i));
        Box boundary=new Box(new float[]{-.2f,-.2f,-.2f},new float[]{.2f,.2f,.2f},new Matrix4f(),0,0);
        float epsilon=2.9999999E-6f;
        for(int plane=0;plane<6;plane++)for(float offset:new float[]{0.0f,-0.0f,epsilon,-epsilon,java.lang.Math.nextUp(epsilon),java.lang.Math.nextDown(epsilon),2*epsilon,-2*epsilon}) {
            int axis=plane/2;float surface=plane%2==0?-.2f:.2f;
            for(boolean crossing:new boolean[]{false,true}) {
                ArrayList<Vertex> vertices=new ArrayList<>();
                for(int i=0;i<4;i++) {
                    float[] v={0,0,0};
                    v[axis]=surface+(crossing&&(i&1)!=0?-offset:offset);
                    v[(axis+1)%3]=(i==0||i==3?-.1f:.1f);v[(axis+2)%3]=(i<2?-.1f:.1f);
                    vertices.add(new Vertex(v[0],v[1],v[2],i*.25f,1-i*.25f));
                }
                Polygon p=new Polygon(vertices,new Vector3f(1,0,0),1);
                for(boolean strict:new boolean[]{false,true}) {
                    SpatialSurfaceCleaner.compare(ExactSurfaceClipper.intersectingOPT2(p,boundary,strict),CompactSurfaceClipper.intersecting(p,boundary,strict));cases++;
                }
            }
        }
        System.out.println("EXACT_COMPACT_CLIP_PASS cases="+cases+" escapedOwnership="+escaped.size()+" raw-floats/UV/normals/rank/nonfinite/singular/large-input");
    }
}
