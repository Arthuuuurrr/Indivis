package net.tompsen.nexuscharacters;
import java.util.*;
import org.joml.*;
import net.tompsen.nexuscharacters.SurfaceGeometry.*;

/** Check raw float values, ownership and singular/non-finite inputs. */
public final class ExactTransformRegression {
    public static void main(String[] args) {
        java.util.Random random=new java.util.Random(0x45584143544f5054L);int cases=0;
        for(int trial=0;trial<1200;trial++) {
            Matrix4f matrix=new Matrix4f().translation(random.nextFloat(),random.nextFloat(),random.nextFloat())
                .rotateXYZ(random.nextFloat(),random.nextFloat(),random.nextFloat())
                .scale(random.nextFloat()*2,trial%23==0?0:random.nextFloat()*2,random.nextFloat()*2);
            if(trial%19==0)matrix.identity();
            if(trial%29==0)matrix.perspective(.7f,1.5f,.1f,100f);
            List<Polygon> input=new ArrayList<>();
            for(int p=0;p<12;p++) {
                List<Vertex> vertices=new ArrayList<>();
                for(int v=0;v<3+p%6;v++) {
                    float x=random.nextFloat()*2-1,y=random.nextFloat()*2-1,z=random.nextFloat()*2-1;
                    if(trial%37==0&&v==0)x=Float.intBitsToFloat(0x7fa00001);
                    if(trial%41==0&&v==1)y=Float.NEGATIVE_INFINITY;
                    vertices.add(new Vertex(x,y,z,random.nextFloat(),random.nextFloat()));
                }
                input.add(new Polygon(vertices,new Vector3f(random.nextFloat(),random.nextFloat(),random.nextFloat()),p-trial));
            }
            var expected=SurfaceGeometry.transformOPT1(input,matrix);
            var actual=ExactTransformSupport.transform(input,matrix);
            ExactTransformSupport.compare(expected,actual);cases++;
            // Later invocations must not mutate vertices/normals already returned.
            ExactTransformSupport.transform(input,new Matrix4f().rotateY(.3f));
            ExactTransformSupport.compare(expected,actual);cases++;
        }
        System.out.println("EXACT_TRANSFORM_PASS cases="+cases+" polygons="+ExactTransformSupport.oraclePolygons+" raw-floats/UV/normals/ranks/ownership/singular/non-finite");
    }
}
