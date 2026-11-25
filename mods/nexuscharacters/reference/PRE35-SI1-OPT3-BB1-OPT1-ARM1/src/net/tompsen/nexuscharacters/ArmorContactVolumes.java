package net.tompsen.nexuscharacters;

import java.util.*;

/** Reusable spatial topology of the occluders, refitted to current bone poses.
 * Every leaf query tests the CURRENT original world boxes. No merged box is
 * substituted into the actual clipping, and no rank/order is changed. */
public final class ArmorContactVolumes {
    private static final long LIMIT=16L*1024*1024;
    private static final LinkedList<Topology> CACHE=new LinkedList<>();
    private static long bytes;
    public static long bakes,hits,evictions,peakBytes;
    private ArmorContactVolumes(){}
    private static long signature(SurfaceGeometry.Box[] boxes){
        long h=0xcbf29ce484222325L;for(var b:boxes){h=(h^b.part)*0x100000001b3L;h=(h^b.rank)*0x100000001b3L;for(int a=0;a<3;a++){h=(h^Float.floatToRawIntBits(b.lo[a]))*0x100000001b3L;h=(h^Float.floatToRawIntBits(b.hi[a]))*0x100000001b3L;}}return h;
    }
    private static synchronized Topology topology(SurfaceGeometry.Box[] boxes){
        long hash=signature(boxes);Topology found=null;
        for(var it=CACHE.iterator();it.hasNext();){var t=it.next();if(t.hash==hash&&t.matches(boxes)){found=t;it.remove();break;}}
        if(found!=null){CACHE.addLast(found);hits++;return found;}
        Topology t=new Topology(boxes,hash);bakes++;
        if(t.bytes<=LIMIT/2){CACHE.addLast(t);bytes+=t.bytes;while(bytes>LIMIT||CACHE.size()>32){bytes-=CACHE.removeFirst().bytes;evictions++;}peakBytes=Math.max(peakBytes,bytes);}return t;
    }
    private static final class Topology {
        final long hash,bytes;
        final int[] descriptors,order,roots=new int[6];
        final ArrayList<Node> nodes=new ArrayList<>();
        Topology(SurfaceGeometry.Box[] boxes,long hash){
            this.hash=hash;descriptors=new int[boxes.length*8];order=new int[boxes.length];int at=0;
            for(int p=0;p<6;p++){int from=at;for(int i=0;i<boxes.length;i++)if(boxes[i].part==p)order[at++]=i;roots[p]=from==at?-1:build(boxes,from,at);}
            if(at!=boxes.length)throw new IllegalArgumentException("Unknown Nexus part index");
            for(int i=0;i<boxes.length;i++){var b=boxes[i];int k=i*8;descriptors[k]=b.part;descriptors[k+1]=b.rank;for(int a=0;a<3;a++){descriptors[k+2+a]=Float.floatToRawIntBits(b.lo[a]);descriptors[k+5+a]=Float.floatToRawIntBits(b.hi[a]);}}
            bytes=256L+4L*(descriptors.length+order.length)+64L*nodes.size();
        }
        boolean matches(SurfaceGeometry.Box[] boxes){
            if(boxes.length*8!=descriptors.length)return false;
            for(int i=0;i<boxes.length;i++){var b=boxes[i];int k=i*8;if(descriptors[k]!=b.part||descriptors[k+1]!=b.rank)return false;for(int a=0;a<3;a++)if(descriptors[k+2+a]!=Float.floatToRawIntBits(b.lo[a])||descriptors[k+5+a]!=Float.floatToRawIntBits(b.hi[a]))return false;}return true;
        }
        int build(SurfaceGeometry.Box[] boxes,int from,int to){
            int left=-1,right=-1;
            if(to-from>8){
                double[] lo={Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY},hi={Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY};
                for(int i=from;i<to;i++)for(int a=0;a<3;a++){var b=boxes[order[i]];lo[a]=Math.min(lo[a],b.worldLo[a]);hi[a]=Math.max(hi[a],b.worldHi[a]);}
                int axis=0;for(int a=1;a<3;a++)if(hi[a]-lo[a]>hi[axis]-lo[axis])axis=a;int mid=(from+to)>>>1;select(boxes,from,to-1,mid,axis);left=build(boxes,from,mid);right=build(boxes,mid,to);
            }
            int id=nodes.size();nodes.add(new Node(from,to,left,right));return id;
        }
        int compare(SurfaceGeometry.Box[] boxes,int a,int b,int axis){var x=boxes[a];var y=boxes[b];int c=Double.compare((double)x.worldLo[axis]+x.worldHi[axis],(double)y.worldLo[axis]+y.worldHi[axis]);return c==0?Integer.compare(a,b):c;}
        void select(SurfaceGeometry.Box[] boxes,int lo,int hi,int middle,int axis){
            while(lo<hi){int i=lo,j=hi,pivot=order[(lo+hi)>>>1];while(i<=j){while(compare(boxes,order[i],pivot,axis)<0)i++;while(compare(boxes,order[j],pivot,axis)>0)j--;if(i<=j){int t=order[i];order[i++]=order[j];order[j--]=t;}}if(middle<=j)hi=j;else if(middle>=i)lo=i;else return;}
        }
    }
    private record Node(int from,int to,int left,int right){}
    static final class Frame {
        private PosedSurfaceSupport.Grid grid;
        private SurfaceGeometry.Box[] boxes;
        private Topology topology;
        private float[] bounds=new float[0];
        void prepare(PosedSurfaceSupport.Grid value){
            if(grid==value && (value.opt3Boxes!=null?boxes==value.opt3Boxes:value.opt1Index!=null&&boxes==value.opt1Index.boxes))return;grid=value;
            if(value.opt3Boxes!=null)boxes=value.opt3Boxes;
            else{var index=value.opt1Index;if(index==null)value.opt1Index=index=new SpatialSurfaceCleaner.Index(value);boxes=index.boxes;}
            topology=topology(boxes);int size=topology.nodes.size()*6;if(bounds.length<size)bounds=new float[size];
            // Nodes are stored in post-order: children are refitted first.
            for(int id=0;id<topology.nodes.size();id++){
                var node=topology.nodes.get(id);int k=id*6;
                if(node.left<0){Arrays.fill(bounds,k,k+3,Float.POSITIVE_INFINITY);Arrays.fill(bounds,k+3,k+6,Float.NEGATIVE_INFINITY);for(int i=node.from;i<node.to;i++){var b=boxes[topology.order[i]];for(int a=0;a<3;a++){bounds[k+a]=Math.min(bounds[k+a],b.worldLo[a]);bounds[k+a+3]=Math.max(bounds[k+a+3],b.worldHi[a]);}}}
                else for(int a=0;a<3;a++){bounds[k+a]=Math.min(bounds[node.left*6+a],bounds[node.right*6+a]);bounds[k+a+3]=Math.max(bounds[node.left*6+a+3],bounds[node.right*6+a+3]);}
            }
        }
        boolean near(double x0,double y0,double z0,double x1,double y1,double z1,int own){
            for(int p=0;p<6;p++)if(p!=own&&topology.roots[p]>=0&&near(topology.roots[p],x0,y0,z0,x1,y1,z1))return true;return false;
        }
        private boolean near(int id,double x0,double y0,double z0,double x1,double y1,double z1){
            int k=id*6;if(x1<bounds[k]||x0>bounds[k+3]||y1<bounds[k+1]||y0>bounds[k+4]||z1<bounds[k+2]||z0>bounds[k+5])return false;
            var n=topology.nodes.get(id);if(n.left>=0)return near(n.left,x0,y0,z0,x1,y1,z1)||near(n.right,x0,y0,z0,x1,y1,z1);
            for(int i=n.from;i<n.to;i++){var b=boxes[topology.order[i]];if(x1<b.worldLo[0]||x0>b.worldHi[0]||y1<b.worldLo[1]||y0>b.worldHi[1]||z1<b.worldLo[2]||z0>b.worldHi[2])continue;return true;}return false;
        }
    }
    static synchronized void clear(){CACHE.clear();bytes=0;}
}
