package net.hautecapitale.quetes.sequence;

import java.util.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.*;
import static net.hautecapitale.quetes.sequence.Pq02WorldBench.*;

/** C1 native command/inventory checks; currency substitute and synthetic players declared. */
final class HcQuetesMergeBench {
 static final String FLAG="hcf_cqvcbb01_ref";
 static void check(boolean value,String label){ok(value,"C1 merge: "+label);}
 static void fn(Object p,String id){run(p,"function hc_quetes:"+id);}
 static void state(Object p,String key,int legacy){fn(p,"migration/write/"+key+"/v"+legacy);}
 static void clean(Object p){suspend(p);score2(p,"hcq_pq02",100);score2(p,"CAP_QSEQ",0);score2(p,"CAP_QUETEACTIVE",0);score2(p,"CAP_PROFILE_LOCK",0);run(p,"clear @s");run(p,"kill @e[type=minecraft:item,distance=..8]");}
 static int count(Object p,String filter){run(p,"execute store result score @s fixture_money run clear @s "+filter+" 0");return score(p,"fixture_money");}
 static int item(Object p,String id){return count(p,"*[custom_data~{hc_quete:\""+id+"\"}]");}
 static boolean said(Object p,String text,int from){return messages.get(p).subList(from,messages.get(p).size()).stream().anyMatch(s->s.contains(text));}
 static void money(Object p){
  for(int amount:new int[]{0,1,3,9,10,11,20}){
   clean(p);if(amount>0)run(p,"give @s minecraft:gold_nugget "+amount);int start=messages.get(p).size();fn(p,"aurele/q12/recaisse");
   check(count(p,"minecraft:gold_nugget")== (amount>=10?amount-10:amount),"replacement crate costs exactly ten with "+amount+" coins");
   check(item(p,"sqvcep03_caisse")== (amount>=10?1:0),"crate delivered only with sufficient inventory coins: "+amount);
   if(amount>0&&amount<10)check(said(p,"sac à dos",start),"insufficient-money dialogue explains backpack boundary");
  }
 }
 static void badges(Object p){
  clean(p);state(p,"arc_a",5);state(p,"arc_m",6);fn(p,"aurele/q8/kill_vasca");
  check(item(p,"arc_common_q8_insigne")==1&&item(p,"cqvcep02_boussole")==1,"first Vasca kill grants canonical badge and compass");
  fn(p,"aurele/q8/kill_vasca");check(item(p,"arc_common_q8_insigne")==1&&item(p,"cqvcep02_boussole")==1,"repeated kill duplicates neither badge nor compass");
  run(p,"clear @s *[custom_data~{hc_quete:\"arc_common_q8_insigne\"}]");fn(p,"aurele/q8/kill_vasca");
  check(item(p,"arc_common_q8_insigne")==1&&item(p,"cqvcep02_boussole")==1,"later Vasca kill restores only a lost badge");
  fn(p,"lib/balayage");check(item(p,"arc_common_q8_insigne")==1,"cleanup preserves badge while Aveline still needs it");
  run(p,"clear @s *[custom_data~{hc_quete:\"arc_common_q8_insigne\"}]");int start=messages.get(p).size();fn(p,"pnj/aveline/interact");
  check(said(p,"insigne d'un homme de mer",start)&&item(p,"sqvcra02_talisman")==1,"empty-handed Aveline visit restores badge and completes pardon in one interaction");
  state(p,"arc_m",8);fn(p,"aurele/q8/rend_insigne");check(item(p,"arc_common_q8_insigne")==0,"completed Aveline quest cannot regenerate badge");
  // Full inventory: no progression past an item that never entered the inventory.
  clean(p);state(p,"arc_h",5);
  for(int i=0;i<36;i++)run(p,"item replace entity @s "+(i<9?"hotbar."+i:"inventory."+(i-9))+" with minecraft:stone 64");
  start=messages.get(p).size();fn(p,"hector/q5/prend_insigne");
  run(p,"execute store result score @s hcm_pq2_check run execute if predicate hc_quetes:migration/state/arc_h/5");
  check(score(p,"hcm_pq2_check")==1&&item(p,"sqvccr02_insigne")==0&&said(p,"sac est plein",start),"Hector badge pickup keeps previous stage when inventory is full");
  clean(p);state(p,"arc_h",5);fn(p,"hector/q5/prend_insigne");
  run(p,"execute store result score @s hcm_pq2_check run execute if predicate hc_quetes:migration/state/arc_h/6");
  check(score(p,"hcm_pq2_check")==1&&item(p,"sqvccr02_insigne")==1,"Hector badge pickup advances only after receipt");
  run(p,"clear @s *[custom_data~{hc_quete:\"sqvccr02_insigne\"}]");fn(p,"hector/q5/retour");
  check(item(p,"sqvccr02_insigne")==1,"already-stuck Hector hand-in restores missing badge");
 }
 static void refusal(Object p){
  clean(p);state(p,"arc_a",10);state(p,"arc_n",0);fn(p,"marqueurs/aurele");check(score(p,"hcq_mrk_aurele")==1,"Aurele offer initially displays exclamation marker");
  score2(p,"hcq_choix",1102);fn(p,"carrec/choix");fn(p,"marqueurs/aurele");
  check(score(p,FLAG)==1&&score(p,"hcq_cqvcbb01")==0&&score(p,"hcq_mrk_aurele")==0,"decline hides offer marker without negative canonical quest state");
  int start=messages.get(p).size();fn(p,"aurele/interact");check(said(p,"Bête blanche",start)||said(p,"Couperet",start),"declined offer remains accessible through manual NPC interaction");
  var dto=Pq02ProfileCheckpointBench.dto(p);net.tompsen.nexuscharacters.ProfileStateBridge.save((net.minecraft.class_3222)p,dto);score2(p,FLAG,0);net.tompsen.nexuscharacters.ProfileStateBridge.loadStrict((net.minecraft.class_3222)p,dto);
  check(score(p,FLAG)==1,"actual Nexus OPT3 disk save/load preserves new refusal flag");
  var first=dto.id();net.tompsen.nexuscharacters.NexusCharacters.select(p,UUID.randomUUID());var other=Pq02ProfileCheckpointBench.dto(p);net.tompsen.nexuscharacters.ProfileStateBridge.loadStrict((net.minecraft.class_3222)p,other);
  check(score(p,FLAG)==0,"another Nexus character does not inherit refusal flag");
  net.tompsen.nexuscharacters.NexusCharacters.select(p,first);net.tompsen.nexuscharacters.ProfileStateBridge.loadStrict((net.minecraft.class_3222)p,dto);check(score(p,FLAG)==1,"returning to original character restores its refusal memory");
  score2(p,"hcq_choix",1101);fn(p,"carrec/choix");check(score(p,FLAG)==0&&score(p,"hcq_cqvcbb01")==10,"later acceptance starts existing canonical quest and clears refusal");
  score2(p,FLAG,1);state(p,"arc_n",0);check(score(p,FLAG)==0,"existing quest reset also clears refusal memory");
 }
 static void souvenir(Object p,Object other,String id,String state,int terminal,int ongoing){
  clean(p);clean(other);state(p,state,terminal);state(other,state,ongoing);
  run(p,"give @s minecraft:paper[custom_data={hc_id:\""+id+"\"}] 1");run(other,"give @s minecraft:paper[custom_data={hc_id:\""+id+"\"}] 1");fn(p,"lib/balayage");
  String filter="*[custom_data~{hc_id:\""+id+"\"}]";
  check(count(p,filter)==0,"completed "+state+"/"+terminal+" cleans souvenir "+id);
  check(count(other,filter)==1,"ongoing "+state+"/"+ongoing+" retains another player's "+id);
 }
 static void souvenirs(Object p,Object other){
  souvenir(p,other,"q1_lettre","arc_h",2,1);souvenir(p,other,"q5_note","arc_h",7,6);souvenir(p,other,"q5_registre","arc_h",7,6);souvenir(p,other,"q8_ordre","arc_a",7,6);
  state(p,"sqvcso02",5);state(other,"sqvcso02",5);souvenir(p,other,"q2_releve","sqvccr04",3,2);
  clean(p);clean(other);score2(p,"hcq_sqvchr01_o2",1);score2(other,"hcq_sqvchr01_o2",0);
  for(Object player:List.of(p,other))run(player,"give @s minecraft:paper[custom_data={hc_id:\"q13_chronique\"}] 1");fn(p,"lib/balayage");
  check(count(p,"*[custom_data~{hc_id:\"q13_chronique\"}]")==0&&count(other,"*[custom_data~{hc_id:\"q13_chronique\"}]")==1,"chronicle cleanup follows its existing completion flag");
  souvenir(p,other,"q14_role","arc_v",43,42);
  for(int terminal:new int[]{30,50,70}){
   souvenir(p,other,"q16_ordre","arc_p",terminal,61);
   for(String id:List.of("q17_extrait","q17_pavillon"))souvenir(p,other,id,"arc_n",terminal,64);
   for(String id:List.of("q18_arret","q18_dormeurs"))souvenir(p,other,id,"arc_c",terminal,63);
  }
  for(int terminal:new int[]{50,70}){
   souvenir(p,other,"j05_releve","sqlpjo05",terminal,1);souvenir(p,other,"j06_releve","sqlpjo06",terminal,1);
   for(Object player:List.of(p,other))state(player,"sqlpjo08",50);
   for(String id:List.of("j08_copie","j09_copie","j09_recu"))souvenir(p,other,id,"sqlpjo09",terminal,1);
   for(String id:List.of("j11_echantillon","j11_releve"))souvenir(p,other,id,"sqlpjo11",terminal,1);
  }
 }
 static void runAll(Object p,Object other){
  for(Object player:List.of(p,other)){clean(player);for(String group:List.of("arc_h","arc_a","arc_m","arc_n","arc_p","arc_c","arc_v","sqvccr04","sqvcso02","sqlpjo05","sqlpjo06","sqlpjo08","sqlpjo09","sqlpjo11"))state(player,group,0);}
  money(p);badges(p);refusal(p);souvenirs(p,other);clean(p);clean(other);
 }
}
