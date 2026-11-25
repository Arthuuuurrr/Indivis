package net.tompsen.nexuscharacters;
import java.util.*;
/** Geometry-only canonical identities. Texture/material objects are NEVER shared.
 * Equality includes every raw float bit, UV, normal, rank and ordered bound. */
public final class NativeGeometryKeys {
 private static final int MAX_KEYS=256;private static final long LIMIT=8L*1024*1024;
 private static final WeakHashMap<Object,Stamp> MESHES=new WeakHashMap<>();
 private static final LinkedList<Key> KEYS=new LinkedList<>();private static long bytes;
 public static long aliases,creates,evictions,peakBytes;
 private record Stamp(NativeVoxelSurface.Shape shape,Key key){}
 private record Key(NativeVoxelSurface.Shape shape,int hash,long bytes){}
 private NativeGeometryKeys(){}
 public static synchronized Object key(Object mesh){
  if(mesh==null)return null;
  var shape=NativeVoxelSurface.SHAPES.get(mesh);if(shape==null)return mesh;
  Stamp stamp=MESHES.get(mesh);if(stamp!=null&&stamp.shape==shape)return stamp.key;
  int hash=hash(shape);Key found=null;
  for(var it=KEYS.iterator();it.hasNext();){var k=it.next();if(k.hash==hash&&same(k.shape,shape)){found=k;it.remove();KEYS.addLast(k);aliases++;break;}}
  if(found==null){long size=PartSurfaceReuse.estimate(shape.faces())+128L*shape.boxes().size();found=new Key(shape,hash,size);creates++;
   if(size<=LIMIT/2){KEYS.addLast(found);bytes+=size;while(KEYS.size()>MAX_KEYS||bytes>LIMIT){bytes-=KEYS.removeFirst().bytes;evictions++;}peakBytes=Math.max(peakBytes,bytes);}}
  MESHES.put(mesh,new Stamp(shape,found));return found;
 }
 static int bits(float v){return Float.floatToRawIntBits(v);}
 static int hash(NativeVoxelSurface.Shape s){int h=1;for(var p:s.faces()){h=31*h+p.rank();h=31*h+p.vertices().size();if(p.normal()!=null){h=31*h+bits(p.normal().x);h=31*h+bits(p.normal().y);h=31*h+bits(p.normal().z);}else h=31*h+1237;for(var v:p.vertices()){h=31*h+bits(v.x());h=31*h+bits(v.y());h=31*h+bits(v.z());h=31*h+bits(v.u());h=31*h+bits(v.v());}}for(var b:s.boxes()){h=31*h+b.rank();for(float f:b.lo())h=31*h+bits(f);for(float f:b.hi())h=31*h+bits(f);}return h;}
 static boolean same(NativeVoxelSurface.Shape a,NativeVoxelSurface.Shape b){
  if(a==b)return true;if(a.faces().size()!=b.faces().size()||a.boxes().size()!=b.boxes().size())return false;
  for(int i=0;i<a.faces().size();i++){var p=a.faces().get(i);var q=b.faces().get(i);if(p.rank()!=q.rank()||p.vertices().size()!=q.vertices().size())return false;
   var n=p.normal();var m=q.normal();if(n==null?m!=null:m==null||bits(n.x)!=bits(m.x)||bits(n.y)!=bits(m.y)||bits(n.z)!=bits(m.z))return false;
   for(int j=0;j<p.vertices().size();j++){var v=p.vertices().get(j);var w=q.vertices().get(j);if(bits(v.x())!=bits(w.x())||bits(v.y())!=bits(w.y())||bits(v.z())!=bits(w.z())||bits(v.u())!=bits(w.u())||bits(v.v())!=bits(w.v()))return false;}}
  for(int i=0;i<a.boxes().size();i++){var x=a.boxes().get(i);var y=b.boxes().get(i);if(x.rank()!=y.rank()||!same(x.lo(),y.lo())||!same(x.hi(),y.hi()))return false;}return true;
 }
 static boolean same(float[]a,float[]b){if(a.length!=b.length)return false;for(int i=0;i<a.length;i++)if(bits(a[i])!=bits(b[i]))return false;return true;}
 public static synchronized void clear(){MESHES.clear();KEYS.clear();bytes=0;}
}
