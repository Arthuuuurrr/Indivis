package net.tompsen.nexuscharacters;
import java.util.*;
import net.tompsen.nexuscharacters.SurfaceGeometry.*;
import net.tompsen.nexuscharacters.PosedSurfaceSupport.Grid;

/** QA-only: invokes the bytecode copied from the exact SI1 base on the same objects. */
public final class SurfaceComparisonOracle {
 public static long calls,polygons,vertices,assertions;
 static void check(boolean c,String s){assertions++;if(!c)throw new AssertionError(s);}
 static void value(float a,float b){check(Float.floatToRawIntBits(a)==Float.floatToRawIntBits(b),"Float mismatch: "+a+" / "+b);}
 public static void compare(List<Polygon> a,List<Polygon> b){
  check(a.size()==b.size(),"Polygon count "+a.size()+" / "+b.size());
  for(int i=0;i<a.size();i++){
   Polygon x=a.get(i),y=b.get(i);polygons++;
   check(x.rank()==y.rank(),"Rank");check(x.vertices().size()==y.vertices().size(),"Vertex count");
   if(x.normal()==null||y.normal()==null)check(x.normal()==y.normal(),"Normal null");
   else{value(x.normal().x,y.normal().x);value(x.normal().y,y.normal().y);value(x.normal().z,y.normal().z);}
   for(int k=0;k<x.vertices().size();k++){
    Vertex p=x.vertices().get(k),q=y.vertices().get(k);vertices++;
    value(p.x(),q.x());value(p.y(),q.y());value(p.z(),q.z());value(p.u(),q.u());value(p.v(),q.v());
   }
  }
 }
 public static List<Polygon> clean(List<Polygon> input,Grid grid,int part,boolean base,boolean first,boolean skip){
  List<Polygon> old=PosedSurfaceSupport.referenceClean(input,grid,part,base,first,skip);
  List<Polygon> next=ExactSurfaceCleaner.clean(input,grid,part,base,first,skip);
  compare(old,next);calls++;return next;
 }
}
