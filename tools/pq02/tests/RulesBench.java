package net.hautecapitale.quetes.sequence;
import java.util.*;
import net.hautecapitale.quetes.sequence.Pq01Motion.Point;
public final class RulesBench {
 static int checks;static void ok(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
 public static void main(String[] args){
  int minR=100,maxR=-100,minI=100,maxI=-100,count=0;
  for(int a=1;a<=3;a++)for(int g=1;g<=3;g++)for(int m=1;m<=3;m++)for(int f=1;f<=4;f++){
   int r=0,i=0;int[] v={a,g,m,f};String[] d={"arrival","guard","magistrate","gift"};
   for(int k=0;k<4;k++){var q=Pq02Rules.delta(d[k],v[k]);r+=q.reputation();i+=q.impression();}
   minR=Math.min(minR,r);maxR=Math.max(maxR,r);minI=Math.min(minI,i);maxI=Math.max(maxI,i);count++;
   ok(r>=-1&&r<=3&&i>=-1&&i<=4,"decision bounds");
  }
  ok(count==108&&minR==-1&&maxR==3&&minI==-1&&maxI==4,"all 108 decision combinations");
  ok(Pq02Rules.greeting(1).equals("post_friendly"),"2 positives then contempt gives favorable memory");
  ok(Pq02Rules.greeting(-1).equals("post_unfriendly"),"neutral then contempt gives unfavorable memory");
  for(String s:List.of("CAP_PQ2_VER","CAP_PQ2_NODE","CAP_PQ2_DA","CAP_PQ2_DG","CAP_PQ2_DM","CAP_PQ2_DD","CAP_PQ2_IMPR","CAP_PQ2_SENT","CAP_PQ2_ANECD","CAP_PQ2_PORT","CAP_PQ2_STATUS","CAP_PQ2_OFFER","CAP_PQ2_REG","CAP_PQ2_REW","CAP_PQ2_WP","CAP_PQ2_PAUSE","CAP_LEOVIC_GIFT","hcq_pq02"))ok(net.tompsen.nexuscharacters.HCProfilePolicyV0621.shouldPersist(s),"real PRE35 policy persists "+s);
  for(String s:List.of("hcm_pq2_ui","hcm_pq2_check","Pq02Choix","CAP_QDIALOG_OWNER"))ok(!net.tompsen.nexuscharacters.HCProfilePolicyV0621.shouldPersist(s),"real PRE35 policy excludes "+s);
  var frame=Pq02Pacing.parse("indivis:pq02:v1:m01a:magistrat:1000000014:0:80");ok(frame!=null,"valid frame");
  ok(!Pq02Pacing.ready(frame,79,5,true,false,false,false),"voice gates advance");
  ok(!Pq02Pacing.ready(frame,90,5,true,true,false,true),"typewriter gates advance");
  ok(!Pq02Pacing.ready(frame,90,5,false,false,false,false),"pagination gates advance");
  ok(Pq02Pacing.ready(frame,90,5,true,false,false,false),"manual final page may advance");
  for(String s:List.of("indivis:pq01:v1:m01a:magistrat:1000000014:0:0","indivis:pq02:v1:m01a:capitaine:1000000014:0:0","indivis:pq02:v1:m01a:leovic:2147483647:0:0","indivis:pq02:v1:m01a:leovic:1000000014:0:-1"))ok(Pq02Pacing.parse(s)==null,"invalid frame rejected");
  class Port implements Pq02Motion.Port {Point at=new Point("minecraft:overworld",0,64,0);boolean arrived,failed;int moves,stops;public boolean valid(){return true;}public Point position(){return at;}public boolean navigate(Point p){moves++;return false;}public boolean navigating(){return false;}public void stop(){stops++;}public void arrived(){arrived=true;}public void failed(String r){failed=true;}}
  Port port=new Port();var motion=new Pq02Motion(port,new Point("minecraft:overworld",5,64,0),200);motion.start();motion.pause();for(int n=0;n<10000;n++)motion.tick();ok(!port.arrived&&!port.failed&&port.moves==1,"pause stops both navigation and timeout");
  port=new Port();motion=new Pq02Motion(port,new Point("minecraft:overworld",5,64,0),200);motion.start();for(int n=0;n<200;n++)motion.tick();ok(port.failed&&!port.arrived,"blocked motion fails boundedly without teleport");
  port=new Port();motion=new Pq02Motion(port,new Point("minecraft:overworld",5,64,0),200);motion.start();port.at=new Point("minecraft:overworld",5,64,0);motion.tick();ok(port.arrived&&!port.failed,"arrive on real position");
  System.out.println("PASS "+checks+" checks; 108 decision combinations, actual Nexus PRE35 persistence policy, frames and pause controller.");
 }
}
