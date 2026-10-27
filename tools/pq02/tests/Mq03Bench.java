package net.hautecapitale.quetes.sequence;
import java.util.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.score2;
public final class Mq03Bench {
 static void fn(Object p,String f){run(p,"function "+f);}
 static void ok(boolean b,String s){Pq02WorldBench.ok(b,"MQ03: "+s);}
 static void runAll(Object p,Object other){
  Object world=world(p);call(p,"method_5814",0.5d,64d,8.5d);score2(p,"CAP_PROFILE_LOCK",0);SequenceBridge.context(p);score2(p,"hcq_mq01",100);score2(p,"hcq_mq02",100);score2(p,"hcq_mq03",10);score2(p,"hcq_mq04",0);score2(p,"hcq_mqscene",0);
  fn(p,"capitale:quest/spawn_dirigeable_couronne/capitaine_quai/interact_self");ok(score(p,"hcq_mqscene")==14,"quay captain gives reunion dialogue after prologue and PQ02");
  score2(p,"hcq_mqscene",0);fn(p,"capitale:npc/altheon_brumeforge/interact_self");ok(score(p,"hcq_mqscene")==14,"generic captain retains same MQ03 priority");
  fn(p,"hc_quetes:quests/mq03/altheon_retrouvailles/choix_3045");ok(score(p,"hcq_mq03")==20&&score(p,"hcq_mqscene")==15,"reunion opens logbook");
  fn(p,"hc_quetes:quests/mq03/altheon_journal/choix_3048");ok(score(p,"hcq_mq03")==30&&score(p,"hcq_mqscene")==16,"logbook opens consultation");
  fn(p,"hc_quetes:quests/mq03/altheon_consultation/choix_3052");ok(score(p,"hcq_mq03")==40,"consultation directs return to Archives");
  call(p,"method_5814",4.5d,64d,6.5d);fn(p,"hc_quetes:runtime/interact/sentinelle_archives");ok(score(p,"hcq_mqscene")==17,"Archives sentinel recognizes return stage");
  fn(p,"hc_quetes:quests/mq03/sentinelle_retour/choix_3053");ok(score(p,"hcq_mq03")==50&&score(p,"hcf_mq03_access")==1&&score(p,"ACCESS_ARCHIVES")==3,"temporary Archives access granted by existing chain; stages="+score(p,"hcq_mq03")+"/"+score(p,"hcf_mq03_access")+"/"+score(p,"ACCESS_ARCHIVES"));
  Pq02WorldBench.npc(8.5,8.5,"hcq_pnj_archivaire_couronne");call(p,"method_5814",8.5d,64d,9.5d);
  run(p,"data modify storage hc_quetes:lieux havre_fort_port set value {x:0,y:64,z:0,r:4,dimension:\"minecraft:overworld\"}");
  fn(p,"hc_quetes:quests/mq03/archivaire_final/ouvrir");ok(score(p,"hcq_mqscene")==18,"Archivaire final dialogue remains reachable");fn(p,"hc_quetes:quests/mq03/archivaire_final/choix_3059");ok(score(p,"hcq_mq03")==100&&score(p,"hcq_mq04")==10,"MQ03 closes and MQ04 opens");
  int stage=score(p,"hcq_mq04");fn(p,"hc_quetes:quests/mq03/archivaire_final/choix_3059");ok(score(p,"hcq_mq04")==stage,"final choice cannot apply again outside stage");
  score2(other,"hcq_mq03",0);score2(other,"hcq_mqscene",0);call(other,"method_5814",0.5d,64d,8.5d);fn(other,"hc_quetes:runtime/interact/altheon_brumeforge");ok(score(other,"hcq_mqscene")==0,"another character cannot enter captain MQ03 before its stage");
 }
}
