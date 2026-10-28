package net.tompsen.nexuscharacters;
import java.util.*;
import org.joml.*;
import net.tompsen.nexuscharacters.SurfaceGeometry.*;
import net.tompsen.nexuscharacters.PosedSurfaceSupport.Grid;

/** Seeded, dependency-light checks against the unmodified PRE35 method body. */
public final class ExactIndexRegression {
 static void exterior() {
  java.util.Random r=new java.util.Random(0x4558544552494f52L);
  var scratch=new SpatialSurfaceCleaner.ExteriorScratch();int checks=0;
  float e=6.0E-5f;
  for(int trial=0;trial<96;trial++) {
   Matrix4f transform=new Matrix4f().translation(r.nextFloat()-.5f,r.nextFloat()-.5f,r.nextFloat()-.5f)
     .rotateXYZ(r.nextFloat(),r.nextFloat(),r.nextFloat()).scale(.7f+r.nextFloat(),.7f+r.nextFloat(),.7f+r.nextFloat());
   Box box=new Box(new float[]{-.2f,-.3f,-.4f},new float[]{.2f,.3f,.4f},transform,0,0);
   for(int plane=0;plane<6;plane++)for(float offset:new float[]{0,e,-e,2*e,-2*e,java.lang.Math.nextUp(2*e),java.lang.Math.nextDown(2*e),.001f,-.001f})
    for(int sign:new int[]{-1,1}) {
     int axis=plane/2;float position=(plane%2==0?box.lo[axis]:box.hi[axis])+offset;
     List<Vertex> vertices=new ArrayList<>();
     for(int i=0;i<7;i++) {
      float[] v={0,0,0};v[axis]=position;
      v[(axis+1)%3]=(float)java.lang.Math.cos(i*2*java.lang.Math.PI/7)*.1f;
      v[(axis+2)%3]=(float)java.lang.Math.sin(i*2*java.lang.Math.PI/7)*.1f;
      vertices.add(new Vertex(v[0],v[1],v[2],i/7f,1-i/7f));
     }
     Vector3f normal=new Vector3f();normal.setComponent(axis,sign);
     Polygon polygon=SurfaceGeometry.transform(List.of(new Polygon(vertices,normal,1)),transform).get(0);
     if(scratch.same(polygon,box)!=box.sameExterior(polygon))throw new AssertionError("Exterior plane/epsilon mismatch");
     checks++;
    }
  }
  System.out.println("EXACT_EXTERIOR_PASS checks="+checks+" coplanar/near-threshold/nonuniform-transform/reversed-normal/growing-scratch");
 }
 public static void main(String[] args) {
  exterior();
  java.util.Random random=new java.util.Random(0x354f505431L);int cases=0;
  for(int trial=0;trial<600;trial++) {
   Grid grid=new Grid();List<Polygon> polygons=new ArrayList<>();
   for(int i=0;i<24;i++) {
    Matrix4f transform=new Matrix4f().translation((random.nextFloat()-.5f)*1.3f,(random.nextFloat()-.5f)*1.3f,(random.nextFloat()-.5f)*1.3f)
       .rotateXYZ(random.nextFloat(),random.nextFloat(),random.nextFloat());
    int rank=trial%7==0?i/3:i;
    grid.add(new Box(new float[]{-.2f,-.2f,-.2f},new float[]{.2f,.2f,.2f},transform,rank,i%6));
    if(i<12) {
     float z=(i%3-1)*.125f;
     var p=new Polygon(List.of(new Vertex(-.25f,-.25f,z,0,0),new Vertex(.25f,-.25f,z,1,0),
       new Vertex(.25f,.25f,z,1,1),new Vertex(-.25f,.25f,z,0,1)),new Vector3f(0,0,1),i+10);
     polygons.addAll(SurfaceGeometry.transform(List.of(p),transform));
    }
   }
   for(int flags=0;flags<8;flags++) {
    boolean base=(flags&1)!=0,first=(flags&2)!=0,skip=(flags&4)!=0;
    int part=trial%6;
    var old=ExactSurfaceCleaner.cleanPRE35(polygons,grid,part,base,first,skip);
    var next=SpatialSurfaceCleaner.clean(polygons,grid,part,base,first,skip);
    SpatialSurfaceCleaner.compare(old,next);cases++;
   }
   Object oldIndex=grid.opt1Index;
   grid.add(new Box(new float[]{-.1f,-.1f,-.1f},new float[]{.1f,.1f,.1f},new Matrix4f(),8000,0));
   if(grid.opt1Index!=null)throw new AssertionError("Grid.add kept stale index");
   SpatialSurfaceCleaner.compare(ExactSurfaceCleaner.cleanPRE35(polygons,grid,1,false,false,true),
     SpatialSurfaceCleaner.clean(polygons,grid,1,false,false,true));
   if(grid.opt1Index==oldIndex)throw new AssertionError("Grid index not rebuilt");cases++;
  }
  System.out.println("EXACT_INDEX_PASS cases="+cases+" polygons="+SpatialSurfaceCleaner.oraclePolygons+" fallbacks="+SpatialSurfaceCleaner.fallbacks);
 }
}


