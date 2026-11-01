package net.hautecapitale.quetes.sequence;

import java.util.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.*;
import static net.hautecapitale.quetes.sequence.Pq02WorldBench.*;

/** Native commands, actual inventory and real Nexus profile persistence. No GUI rendering. */
final class Pq02AutomaticGiftBench {
 static void check(boolean value,String message){ok(value,"HF8 systematic gift: "+message);}
 static int money(Object p){run(p,"execute store result score @s fixture_money run clear @s minecraft:gold_nugget 0");return score(p,"fixture_money");}
 static void clean(Object p){suspend(p);score2(p,"CAP_PROFILE_LOCK",0);score2(p,"CAP_QSEQ",0);score2(p,"CAP_QUETEACTIVE",0);run(p,"clear @s minecraft:gold_nugget");score2(p,"fixture_bonus",0);score2(p,"fixture_skills",0);}
 static int phase(Pq02Data.Node n){String k=n.key;if(k.startsWith("post"))return 100;if(k.startsWith("a"))return 30;if(k.startsWith("t07"))return 50;if(k.startsWith("t"))return 40;if(k.startsWith("m06_registered")||k.matches("m06[abc]")||k.startsWith("m07"))return 60;if(k.startsWith("m"))return 50;return 90;}
 static void prepare(Object p,Pq02Data.Node n){int s=phase(n);View v=VIEWS.get(uuid(p));if(v==null||stage(p)!=s){suspend(p);score2(p,"hcq_pq02",s);score2(p,"CAP_PQ2_WP",s==40?0:s>=90?17:14);v=Pq02Runtime.view(p);spawn(v,"leovic",startPosition(v));}
  score2(p,"hcq_pq02",s);score2(p,"CAP_PQ2_VER",1);score2(p,"CAP_QUETEACTIVE",0);score2(p,"CAP_PQ2_NODE",n.id);for(String key:Pq02Rules.DECISIONS.values())score2(p,key,0);
  score2(p,"REP_GARDEPORT",10);score2(p,"CAP_PQ2_IMPR",0);score2(p,"CAP_LEOVIC_GIFT",0);score2(p,"CAP_PQ2_OFFER",1);score2(p,"CAP_PQ2_REG",s>=60?1:0);score2(p,"CAP_PQ2_REW",0);score2(p,"CAPSK_REWARD_REGPORT",0);score2(p,"CAP_PQ2_SENT",1);score2(p,"CAP_PQ2_ANECD",1);score2(p,"CAP_PQ2_PORT",1);score2(p,"CAP_PQ2_STATUS",0);
  Object actor=v.actors.get("leovic").entity;var at=pos(actor,world(p));call(p,"method_5814",at.x(),at.y(),at.z()+2);show(v,n.key);Pq02WorldBench.settle(p);
 }
 static void runAll(Object p){
  // Each initial reply receives the money before choosing, with no nested acceptance.
  for(int response=1;response<=4;response++){
   clean(p);prepare(p,Pq02Data.get("f06"));
   check(money(p)==50&&score(p,"CAP_LEOVIC_GIFT")==1&&score(p,"CAP_PQ2_DD")==0,"response "+response+": payment precedes choice");
   check(score(p,"REP_GARDEPORT")==10&&score(p,"CAP_PQ2_IMPR")==0&&score(p,"fixture_bonus")==1,"payment itself has no reputation or impression effect");
   Object actor=v(p).actors.get("leovic").entity;
   Pq02HandoffBench.nativeClose(p,"ESC");open(p,"leovic");settle(p);
   check(v(p).node.key.equals("f06")&&v(p).actors.get("leovic").entity==actor&&money(p)==50,"Escape before choice resumes same actor and never pays twice");
   pick(p,response);var delta=Pq02Rules.delta("gift",response);
   check(score(p,"CAP_PQ2_DD")==response&&score(p,"REP_GARDEPORT")==10+delta.reputation()&&score(p,"CAP_PQ2_IMPR")==delta.impression()&&money(p)==50,"reply "+response+": exact one-time social effect, all 50 Martins kept");
   check(v(p).choices.isEmpty()&&v(p).node.next.equals("f07"),"reply "+response+": no second acceptance or refusal menu");
   var dto=Pq02ProfileCheckpointBench.dto(p);
   net.tompsen.nexuscharacters.ProfileStateBridge.save((net.minecraft.class_3222)p,dto);
   net.tompsen.nexuscharacters.ProfileStateBridge.loadStrict((net.minecraft.class_3222)p,dto);
   open(p,"leovic");settle(p);
   check(money(p)==50&&score(p,"CAP_PQ2_DD")==response&&score(p,"fixture_bonus")==1,"real Nexus disk save/reload retains unique payment and decision");
   advance(p);check(v(p).node.key.equals("f07")&&money(p)==50,"all four replies naturally reach farewell");
   pick(p,1);advance(p);
   check(stage(p)==100&&v(p)==null&&score(p,"fixture_skills")==1&&score(p,"fixture_bonus")==2&&money(p)==50,"completion without a return visit preserves original rewards exactly once");
   score2(p,"CAP_QDIALOG_OWNER",97);run(p,"function capitale:pq02/gift_once_self");run(p,"function capitale:pq02/complete_once_self");score2(p,"CAP_QDIALOG_OWNER",0);
   check(money(p)==50&&score(p,"fixture_bonus")==2&&score(p,"fixture_skills")==1,"terminal function replay gives no duplicate");
  }
  // Every old donation checkpoint resumes directly, including the retired report node.
  for(String key:List.of("f06","f06a","f06b","f06c","f06d","f_report","f07","f07a","f07b","f07c")){
   clean(p);prepare(p,Pq02Data.get("f_questions"));
   score2(p,"CAP_PQ2_DD",4);score2(p,"CAP_PQ2_IMPR",-1);score2(p,"REP_GARDEPORT",9);score2(p,"CAP_PQ2_NODE",Pq02Data.get(key).id);
   suspend(p);open(p,"leovic");settle(p);
   check(v(p)!=null&&v(p).ui&&money(p)==50&&score(p,"CAP_LEOVIC_GIFT")==1,"legacy ongoing "+key+" resumes with unique payment");
   check(!v(p).node.key.equals("f_report")&&score(p,"CAP_PQ2_DD")==4&&score(p,"REP_GARDEPORT")==9&&score(p,"CAP_PQ2_IMPR")==-1,"legacy "+key+" never replays a decision or offers a refusal");
   suspend(p);open(p,"leovic");settle(p);check(money(p)==50&&score(p,"fixture_bonus")==1,"legacy "+key+" reconnect never duplicates payment");
  }
  clean(p);prepare(p,Pq02Data.get("f_questions"));score2(p,"CAP_LEOVIC_GIFT",1);show(v(p),"f06");settle(p);
  check(money(p)==0&&score(p,"fixture_bonus")==0,"already-paid character does not receive spent money again");
  clean(p);prepare(p,Pq02Data.get("post_gift"));
  check(v(p).node.key.equals("post_menu")&&money(p)==0&&v(p).choices.size()==3,"completed legacy unpaid profile redirects to practical menu without late recovery");
  score2(p,"CAP_QDIALOG_OWNER",97);run(p,"function capitale:pq02/gift_once_self");
  check(money(p)==0&&score(p,"CAP_LEOVIC_GIFT")==0,"completed legacy profile is never retroactively credited by old gift function");
  clean(p);prepare(p,Pq02Data.get("f_questions"));score2(p,"CAP_PQ2_DD",2);run(p,"function capitale:pq02/complete_once_self");
  check(stage(p)==90&&score(p,"CAP_PQ2_REW")==0,"completion refuses an unpaid in-progress scene");
  for(Pq02Data.Node node:Pq02Data.NODES.values())if(node.key.startsWith("post"))check(node.choices.stream().noneMatch(c->c.target.equals("post_gift")||c.target.equals("event:late_gift")),"no post-quest gift retrieval choice in "+node.key);
  clean(p);
 }
}
