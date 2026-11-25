package net.tompsen.nexuscharacters;
import java.util.*;
/** Same ranked subtraction as cleanIndexedBB1; Piece wrappers and the two
 * intermediate lists are scratch-only and never escape to a rendered mesh. */
public final class ScratchSurfaceCleaner {
 private static final ThreadLocal<Work> WORK=ThreadLocal.withInitial(Work::new);
 private static final boolean VERIFY=Boolean.getBoolean("nexus.opt1.verify");
 public static long calls,oracleCalls;
 private static final class Work{
  final ArrayList<SpatialSurfaceCleaner.Piece> pool=new ArrayList<>(),a=new ArrayList<>(),b=new ArrayList<>();
  final SpatialSurfaceCleaner.ExteriorScratch exterior=new SpatialSurfaceCleaner.ExteriorScratch();int used;
  SpatialSurfaceCleaner.Piece piece(SurfaceGeometry.Polygon polygon){
   SpatialSurfaceCleaner.Piece p;if(used<pool.size()){p=pool.get(used);p.reset(polygon);}else{p=new SpatialSurfaceCleaner.Piece(polygon);if(used<1024)pool.add(p);}used++;return p;
  }
  void release(){for(int i=0;i<Math.min(used,pool.size());i++)pool.get(i).polygon=null;used=0;a.clear();b.clear();}
 }
 public static List<SurfaceGeometry.Polygon> clean(List<SurfaceGeometry.Polygon> input,PosedSurfaceSupport.Grid grid,SpatialSurfaceCleaner.Index index,int part,boolean base,boolean first,boolean world){
  Work w=WORK.get();try{var out=clean(input,grid,index,part,base,first,world,w);calls++;
   if(VERIFY){SpatialSurfaceCleaner.compare(SpatialSurfaceCleaner.cleanIndexedBB1(input,grid,index,part,base,first,world),out);oracleCalls++;}return out;
  }finally{w.release();}
 }
 private static List<SurfaceGeometry.Polygon> clean(List<SurfaceGeometry.Polygon> input,PosedSurfaceSupport.Grid grid,SpatialSurfaceCleaner.Index index,int part,boolean base,boolean first,boolean world,Work w){
  int mask=63;if(world||base)mask&=~(1<<part);if(first)mask&=1<<part;
  if(mask==0)return new ArrayList<>(input);
  ArrayList<SurfaceGeometry.Polygon> out=new ArrayList<>(input.size());
  for(var polygon:input){
   w.release();var original=w.piece(polygon);ArrayList<SpatialSurfaceCleaner.Piece> pieces=null;
   for(int id:index.query(original,grid,mask)){
    var box=index.boxes[id];
    if(pieces==null){
     if(original.unchanged(box,w.exterior))continue;
     pieces=w.a;for(var p:ExactSurfaceClipper.intersecting(polygon,box,!world))pieces.add(w.piece(p));
    }else{
     ArrayList<SpatialSurfaceCleaner.Piece> next=null;
     for(int i=0;i<pieces.size();i++){
      var piece=pieces.get(i);
      if(piece.unchanged(box,w.exterior)){if(next!=null)next.add(piece);continue;}
      if(next==null){next=pieces==w.a?w.b:w.a;next.clear();for(int j=0;j<i;j++)next.add(pieces.get(j));}
      for(var p:ExactSurfaceClipper.intersecting(piece.polygon,box,!world))next.add(w.piece(p));
     }
     if(next!=null)pieces=next;
    }
    if(pieces.isEmpty())break;
   }
   if(pieces==null)out.add(polygon);else for(var piece:pieces)out.add(piece.polygon);
  }
  return out;
 }
 public static void clear(){WORK.remove();}
}
