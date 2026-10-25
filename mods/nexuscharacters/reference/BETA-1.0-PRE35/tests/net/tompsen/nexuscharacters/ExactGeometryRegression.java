package net.tompsen.nexuscharacters;
import java.util.*;
import org.joml.*;
import net.tompsen.nexuscharacters.SurfaceGeometry.*;

public final class ExactGeometryRegression {
 static final java.util.Random RANDOM=new java.util.Random(0x4e657875735035L);
 static float range(float a,float b){return a+RANDOM.nextFloat()*(b-a);}
 static Polygon face(int axis,float offset,float size,Matrix4f transform,int rank,boolean triangle){
  List<Vertex> list=new ArrayList<>();
  float[][] corners={{-size,-size},{size,-size},{size,size},{-size,size}};
  for(int k=0;k<(triangle?3:4);k++){
   float[] p=new float[3];p[axis]=offset;p[(axis+1)%3]=corners[k][0];p[(axis+2)%3]=corners[k][1];
   list.add(new Vertex(p[0],p[1],p[2],k*.137f,k*.217f).transform(transform));
  }
  Vector3f normal=new Vector3f();normal.setComponent(axis,1);
  new Matrix3f(transform).invert().transpose().transform(normal).normalize();
  return new Polygon(list,normal,rank);
 }
 static void one(Polygon f,Box b,boolean connected){
  List<Polygon> old=connected?SurfaceGeometry.referenceSubtractConnected(f,b):SurfaceGeometry.referenceSubtract(f,b);
  List<Polygon> next=ExactSurfaceClipper.subtract(f,b,connected);
  SurfaceComparisonOracle.compare(old,next);
  if(!ExactSurfaceClipper.unchanged(f,b))for(Polygon p:next)
    SurfaceComparisonOracle.check(p.vertices()!=f.vertices(),"New mutable alias to input");
 }
 public static void main(String[] args)throws Exception {
  int clips=0;
  float[] boundaries={-1,1,-1-SurfaceGeometry.EPS,-1+SurfaceGeometry.EPS,1-SurfaceGeometry.EPS,1+SurfaceGeometry.EPS,
   -1-SurfaceGeometry.EPS/10,-1+SurfaceGeometry.EPS/10,1-SurfaceGeometry.EPS/10,1+SurfaceGeometry.EPS/10,
   java.lang.Math.nextDown(1f),java.lang.Math.nextUp(1f),0,2};
  for(int i=0;i<24000;i++){
   Matrix4f m=new Matrix4f();
   if(i%4!=0)m.translate(range(-2,2),range(-2,2),range(-2,2)).rotateXYZ(range(-2,2),range(-2,2),range(-2,2)).scale(range(.5f,1.8f),range(.5f,1.8f),range(.5f,1.8f));
   Box box=new Box(new float[]{-1,-1,-1},new float[]{1,1,1},m,100+i%5,i%6);
   Polygon f=face(i%3,i%3==0?boundaries[(i/3)%boundaries.length]:range(-2,2),range(.01f,2),i%7==0?new Matrix4f():m,100+(i/5)%5,i%5==0);
   one(f,box,false);one(f,box,true);clips++;
  }
  int modes=0;
  for(int scene=0;scene<10;scene++){
   PosedSurfaceSupport.Grid grid=new PosedSurfaceSupport.Grid();
   List<Polygon> faces=new ArrayList<>();
   for(int i=0;i<70;i++){
    Matrix4f m=new Matrix4f().translate(range(-.5f,.5f),range(-.5f,.5f),range(-.5f,.5f)).rotateXYZ(range(-1,1),range(-1,1),range(-1,1));
    grid.add(new Box(new float[]{-.2f,-.2f,-.2f},new float[]{.2f,.2f,.2f},m,100+(i%4),i%6));
    faces.add(face(i%3,range(-.5f,.5f),range(.02f,.3f),new Matrix4f(),100+i%4,i%4==0));
   }
   for(int part=0;part<6;part++)for(int flags=0;flags<8;flags++){
    SurfaceComparisonOracle.clean(faces,grid,part,(flags&1)!=0,(flags&2)!=0,(flags&4)!=0);modes++;
   }
  }
  List<Polygon> many=new ArrayList<>();
  for(int i=0;i<2150;i++)many.add(face(0,0,.01f,new Matrix4f().translate(i,0,0),100,false));
  SurfaceComparisonOracle.clean(many,new PosedSurfaceSupport.Grid(),0,false,false,false);modes++;
  System.out.println("EXACT35_PASS sample_cases="+clips+" grid_modes="+modes+" assertions="+SurfaceComparisonOracle.assertions+" polygons="+SurfaceComparisonOracle.polygons+" vertices="+SurfaceComparisonOracle.vertices);
 }
}
