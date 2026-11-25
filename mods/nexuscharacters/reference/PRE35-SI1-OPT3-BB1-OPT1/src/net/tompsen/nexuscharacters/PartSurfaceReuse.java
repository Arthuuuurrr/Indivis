package net.tompsen.nexuscharacters;
import java.util.*;import org.joml.Matrix4f;
/** Exact part-local reuse, bounded even when many recolored appearances cycle.
 * A canonical profile never bypasses the original input/matrix/mask checks. */
public final class PartSurfaceReuse {
 private static final long LIMIT=16L*1024*1024;
 public static long hits,stores,evictions,peakBytes;
 private PartSurfaceReuse(){}
 static Object shape(Object object){return object==null?null:NativeVoxelSurface.SHAPES.get(object);}
 static long estimate(List<SurfaceGeometry.Polygon> list){long b=32L+8L*list.size();for(var p:list)b+=256L+48L*p.vertices().size();return b;}
 static Token lookup(List<SurfaceGeometry.Polygon> list,PosedSurfaceSupport.Grid grid,SpatialSurfaceCleaner.Index index,int part,boolean base){
  if(list.isEmpty()||!index.bounded)return null;var w=PoseScratch.ACTIVE.get();if(w==null)return null;
  int lx=Integer.MAX_VALUE,ly=lx,lz=lx,hx=Integer.MIN_VALUE,hy=hx,hz=hx;
  for(var p:list){var n=p.normal();if(n!=null&&(!Float.isFinite(n.x)||!Float.isFinite(n.y)||!Float.isFinite(n.z)))return null;
   for(var v:p.vertices()){
    if(!Float.isFinite(v.x())||!Float.isFinite(v.y())||!Float.isFinite(v.z())||!Float.isFinite(v.u())||!Float.isFinite(v.v())||Math.abs(v.x())>32f||Math.abs(v.y())>32f||Math.abs(v.z())>32f)return null;
    lx=Math.min(lx,grid.cell(v.x()-3e-5f));hx=Math.max(hx,grid.cell(v.x()+3e-5f));ly=Math.min(ly,grid.cell(v.y()-3e-5f));hy=Math.max(hy,grid.cell(v.y()+3e-5f));lz=Math.min(lz,grid.cell(v.z()-3e-5f));hz=Math.max(hz,grid.cell(v.z()+3e-5f));}}
  int mask=0;for(int i=0;i<6;i++){if(i==part||index.partLo[i][0]>hx||index.partHi[i][0]<lx||index.partLo[i][1]>hy||index.partHi[i][1]<ly||index.partLo[i][2]>hz||index.partHi[i][2]<lz)continue;mask|=1<<i;}
  Pool pool=w.opt1Reuse;if(pool==null)w.opt1Reuse=pool=new Pool();
  Object[] keys=pool.keys;for(int i=0;i<6;i++)keys[i]=NativeGeometryKeys.key(w.templates[i]);
  Profile profile=null;for(var it=pool.profiles.iterator();it.hasNext();){var p=it.next();if(p.matches(keys)){profile=p;it.remove();break;}}
  if(profile==null)profile=new Profile(keys);pool.profiles.addLast(profile);while(pool.profiles.size()>32)evict(pool);
  int slot=part+(base?0:6);List<SurfaceGeometry.Polygon> hit=null;
  for(int i=0;i<4;i++){Entry e=profile.entries[slot*4+i];if(e!=null&&e.matches(list,mask,w)){hit=e.output;break;}}
  if(hit!=null)hits++;return new Token(pool,profile,slot,mask,w,hit);
 }
 static void store(Token t,List<SurfaceGeometry.Polygon> input,List<SurfaceGeometry.Polygon> output){
  Entry e=new Entry(input,output,t.mask,t.workspace);if(e.bytes>LIMIT)return;Pool pool=t.pool;Profile profile=t.profile;
  int at=t.slot*4+profile.next[t.slot];profile.next[t.slot]=(profile.next[t.slot]+1)%4;Entry previous=profile.entries[at];long delta=e.bytes-(previous==null?0:previous.bytes);
  profile.entries[at]=e;profile.bytes+=delta;pool.bytes+=delta;stores++;while(pool.bytes>LIMIT&&!pool.profiles.isEmpty())evict(pool);peakBytes=Math.max(peakBytes,pool.bytes);
 }
 static void evict(Pool pool){pool.bytes-=pool.profiles.removeFirst().bytes;evictions++;}
 public static final class Pool{final LinkedList<Profile> profiles=new LinkedList<>();final Object[]keys=new Object[6];long bytes;}
 static final class Profile{final Object[]keys;final Entry[]entries=new Entry[48];final int[]next=new int[12];long bytes;Profile(Object[]k){keys=k.clone();}boolean matches(Object[]k){for(int i=0;i<6;i++)if(keys[i]!=k[i])return false;return true;}}
 static final class Entry{
  final List<SurfaceGeometry.Polygon>input,output;final Matrix4f[]matrices=new Matrix4f[12];final int mask;final long bytes;
  Entry(List<SurfaceGeometry.Polygon>i,List<SurfaceGeometry.Polygon>o,int m,PoseScratch.Workspace w){input=i;output=o;mask=m;for(int p=0;p<6;p++)if((m&1<<p)!=0){matrices[p]=new Matrix4f(w.matrices[p]);matrices[p+6]=new Matrix4f(w.matrices[p+6]);}bytes=1024L+estimate(i)+estimate(o);}
  boolean matches(List<SurfaceGeometry.Polygon>i,int m,PoseScratch.Workspace w){if(mask!=m)return false;for(int p=0;p<6;p++)if((m&1<<p)!=0&&(!matrices[p].equals(w.matrices[p])||!matrices[p+6].equals(w.matrices[p+6])||matrices[p].properties()!=w.matrices[p].properties()||matrices[p+6].properties()!=w.matrices[p+6].properties()))return false;return input.equals(i);}
 }
 record Token(Pool pool,Profile profile,int slot,int mask,PoseScratch.Workspace workspace,List<SurfaceGeometry.Polygon>hit){}
}
