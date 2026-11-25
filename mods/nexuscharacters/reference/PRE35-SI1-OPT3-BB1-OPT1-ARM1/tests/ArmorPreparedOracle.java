package net.tompsen.nexuscharacters;
import java.util.*;import org.joml.Matrix4f;import org.joml.Vector3f;
public final class ArmorPreparedOracle {
 static int checks;
 static SurfaceGeometry.Polygon polygon(Random r,int rank){
  int n=3+r.nextInt(5);float x=r.nextFloat()-.5f,y=r.nextFloat()-.5f,z=r.nextFloat()-.5f,radius=.01f+r.nextFloat()*.12f;var vs=new ArrayList<SurfaceGeometry.Vertex>();
  for(int i=0;i<n;i++){double a=2*Math.PI*i/n;vs.add(new SurfaceGeometry.Vertex(x+(float)Math.cos(a)*radius,y+(float)Math.sin(a)*radius,z,r.nextFloat(),r.nextFloat()));}
  return new SurfaceGeometry.Polygon(vs,new Vector3f(0,0,1),rank);
 }
 static float[] reference(List<SurfaceGeometry.Polygon> input,Matrix4f m,PosedSurfaceSupport.Grid g,int part,boolean base){return PackedSurfaceOutput.write(PosedSurfaceSupport.clean(SurfaceGeometry.transform(input,m),g,part,base,false,true),new Matrix4f(m).invert());}
 static float[] prepared(List<SurfaceGeometry.Polygon> input,Matrix4f m,PosedSurfaceSupport.Grid g,int part,boolean base){return ArmorPreparedSurface.write(ArmorPreparedSurface.clean(ArmorPreparedSurface.transform(input,m),g,part,base,false,true),new Matrix4f(m).invert());}
 public static void main(String[]args){
  Random r=new Random(189_20261125L);int cases=args.length==0?3000:Integer.parseInt(args[0]);
  for(int f=0;f<cases;f++){
   var g=new PosedSurfaceSupport.Grid();for(int p=0;p<6;p++){var m=new Matrix4f().translation(r.nextFloat()-.5f,r.nextFloat()-.5f,r.nextFloat()-.5f).rotateXYZ(r.nextFloat(),r.nextFloat(),r.nextFloat());g.add(new SurfaceGeometry.Box(new float[]{-.12f,-.12f,-.12f},new float[]{.12f,.12f,.12f},m,100+p,p));}
   var input=new ArrayList<SurfaceGeometry.Polygon>();for(int i=0;i<32;i++)input.add(polygon(r,300+i));
   Matrix4f m=new Matrix4f().translation(r.nextFloat()*.4f-.2f,r.nextFloat()*.4f-.2f,r.nextFloat()*.4f-.2f).rotateXYZ(r.nextFloat()*6,r.nextFloat()*6,r.nextFloat()*6).scale(.5f+r.nextFloat(),.5f+r.nextFloat(),.5f+r.nextFloat());
   for(boolean base:new boolean[]{false,true}){ArmorPreparedSurface.same(reference(input,m,g,f%6,base),prepared(input,m,g,f%6,base));checks++;}
  }
  // Multiple voxel occluders per part: reuse their hierarchy across changing
  // poses and both Grid layouts, without reusing stale world bounds.
  var movingSource=new ArrayList<SurfaceGeometry.Polygon>();for(int i=0;i<96;i++)movingSource.add(polygon(r,400+i));
  for(int frame=0;frame<128;frame++){
   var grid=new PosedSurfaceSupport.Grid();var boxes=new ArrayList<SurfaceGeometry.Box>();
   for(int part=0;part<6;part++){var pose=new Matrix4f().translation((part-2)*.15f,0,0).rotateXYZ((float)Math.sin(frame*.07+part)*.8f,frame*.01f,.02f*part);
    for(int i=0;i<24;i++){float x=(i%4-2)*.065f,y=(i/4-3)*.065f;var b=new SurfaceGeometry.Box(new float[]{x,y,-.06f},new float[]{x+.065f,y+.065f,.06f},pose,100+part*100+i,part);grid.add(b);boxes.add(b);}}
   if(frame%2==0)grid.opt3Boxes=boxes.toArray(SurfaceGeometry.Box[]::new);
   var pose=new Matrix4f().rotateXYZ(frame*.013f,.15f,(float)Math.sin(frame*.05)*.3f);
   ArmorPreparedSurface.same(reference(movingSource,pose,grid,0,false),prepared(movingSource,pose,grid,0,false));checks++;
  }
  // A completely disjoint part follows 128 different bone poses while reusing
  // the VERY SAME immutable local mesh, with no appearance rebake.
  ArmorPreparedSurface.clear();var far=new PosedSurfaceSupport.Grid();for(int p=1;p<6;p++)far.add(new SurfaceGeometry.Box(new float[]{-.1f,-.1f,-.1f},new float[]{.1f,.1f,.1f},new Matrix4f().translation(20+p,0,0),100+p,p));
  var rigid=List.of(polygon(r,400),polygon(r,401));long before=ArmorPreparedSurface.bakes;float[] stable=prepared(rigid,new Matrix4f(),far,0,false);
  for(int i=0;i<128;i++){var m=new Matrix4f().rotateXYZ(i*.1f,i*.07f,i*.03f);float[] data=prepared(rigid,m,far,0,false);if(data!=stable)throw new AssertionError("Rigid mesh buffer replaced during animation");ArmorPreparedSurface.same(reference(rigid,m,far,0,false),data);checks++;}
  if(ArmorPreparedSurface.bakes!=before+1)throw new AssertionError("Pose change rebaked source mesh");checks++;
  // Mutating a Grid via its supported add method must invalidate the hierarchy.
  far.add(new SurfaceGeometry.Box(new float[]{-.6f,-.6f,-.6f},new float[]{.6f,.6f,.6f},new Matrix4f(),110,1));
  ArmorPreparedSurface.same(reference(rigid,new Matrix4f(),far,0,false),prepared(rigid,new Matrix4f(),far,0,false));checks++;
  // Shared planes and epsilon-scale offsets, including signed zero.
  for(float d:new float[]{0f,-0f,-3e-5f,3e-5f,-3e-6f,3e-6f,Math.nextDown(3e-5f),Math.nextUp(3e-5f)}){
   var p=new SurfaceGeometry.Polygon(List.of(new SurfaceGeometry.Vertex(-.2f,-.2f,d,0,0),new SurfaceGeometry.Vertex(.2f,-.2f,d,1,0),new SurfaceGeometry.Vertex(.2f,.2f,d,1,1),new SurfaceGeometry.Vertex(-.2f,.2f,d,0,1)),new Vector3f(0,0,1),400);
   var g=new PosedSurfaceSupport.Grid();g.add(new SurfaceGeometry.Box(new float[]{-.2f,-.2f,0},new float[]{.2f,.2f,.2f},new Matrix4f(),101,1));ArmorPreparedSurface.same(reference(List.of(p),new Matrix4f(),g,0,false),prepared(List.of(p),new Matrix4f(),g,0,false));checks++;
  }
  System.out.println("ARMOR_ORACLE_PASS checks="+checks+" random_cases="+cases+" rigid_mesh_pose_changes=128 topology_exact=true uv_bits_exact=true max_position_error="+ArmorPreparedSurface.maxPositionError+" max_normal_error="+ArmorPreparedSurface.maxNormalError+" rigid_polygons="+ArmorPreparedSurface.rigidPolygons+" contact_polygons="+ArmorPreparedSurface.contactPolygons);
 }
}
