package net.hautecapitale.quetes.sequence;
import java.util.*;
import java.nio.file.*;
import com.google.gson.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.score2;
import net.hautecapitale.quetes.sequence.Pq01Motion.Point;

/** Tick serveur réel : tous les blocs/choix directs PQ01, proximité, Perrin et transitions. */
final class Pq01AuditBench {
 static Object player,other,world,perrinSource,actor,otherActor,perrinView;static int index,state,wait,phase,checksBefore;
 static final List<JsonObject> jobs=new ArrayList<>();static final Map<String,Object> sources=new HashMap<>();
 static final List<String> ROLES=List.of("geraud","altheon","gautier","hugues","anselme","perrin");
 static Point pa,pb;static boolean moved,returned,otherMoved;static UUID originalCharacter;
 static Object bridge(String name,Object...args){return Pq01GautierBench.bridge(name,args);}
 static Object get(Object o,String name)throws Exception{return Pq02WorldBench.get(o,name);}
 static void check(boolean b,String s){Pq02WorldBench.ok(b,"prologue audit: "+s);}
 static Object bindingFor(Object npc){Object n=GuardNbt.compound();GuardNbt.string(n,"uuid",uuid(npc).toString());GuardNbt.string(n,"dimension",dimension(world));GuardNbt.string(n,"type",Pq01EntityFactory.registryId(npc));GuardNbt.put(n,"post",pointNbt(pos(npc,world)));return n;}
 static void start(Object p,Object spectator)throws Exception{
  player=p;other=spectator;world=world(p);checksBefore=Pq02WorldBench.checks;originalCharacter=character(p);
  Pq02Runtime.suspend(p);Pq02Runtime.suspend(spectator);bridge("close",p,true);bridge("close",spectator,true);
  Object config=GuardNbt.compound(),bindings=GuardNbt.childOrCreate(config,"bindings");
  for(int i=0;i<ROLES.size();i++){
   String role=ROLES.get(i);Object npc=Pq02WorldBench.npc(24.5+(i%3),14.5+(i/3),"audit_"+role);sources.put(role,npc);
   for(String ph:List.of("large","quai"))GuardNbt.put(bindings,role+"_"+ph,bindingFor(npc));
  }
  GuardNbt.integer(config,"ambient_enabled",0);save(p,config);
  run(p,"data modify storage capitale:pq01_config set value {timeout_ticks:3600}");
  JsonArray nodes=JsonParser.parseString(Files.readString(Path.of("pq01_protocol_fixture.json"))).getAsJsonArray();
  for(JsonElement e:nodes){JsonObject node=e.getAsJsonObject();JsonObject base=node.deepCopy();base.addProperty("pick",0);jobs.add(base);
   for(JsonElement edge:node.getAsJsonArray("edges")){JsonObject j=node.deepCopy();j.addProperty("pick",edge.getAsJsonObject().get("pick").getAsInt());j.addProperty("target",edge.getAsJsonObject().get("target").getAsString());jobs.add(j);}}
  check(nodes.size()==94&&jobs.size()==411,"94 blocs et 317 choix directs préparés depuis les packs livrés");
  index=state=wait=phase=0;
 }
 static void seed(String key,String role){
  bridge("close",player,true);run(player,"function capitale:pq01/close_self");
  call(player,"method_5814",25.5d,64d,18.5d);
  int stage=key.startsWith("f_")?100:key.startsWith("e")?40:10;
  for(String k:List.of("CAP_QSEQ","CAP_QSEQ_TIMER","hcm_pq1_wait","hcm_pq1_voice","hcm_pq1_vqueue","hcf_pq1_test","hcd_pq1_banter"))score2(player,k,0);
  score2(player,"CAP_PROFILE_LOCK",0);score2(player,"hcq_pq01",stage);score2(player,"hcf_pq1_a",1);score2(player,"hcf_pq1_c",1);score2(player,"hcf_pq1_av",1);score2(player,"hcf_pq1_ver",1);
  score2(player,"hcm_pq1_active",1);score2(player,"CAP_QDIALOG_OWNER",91+ROLES.indexOf(role));score2(player,"hcm_pq1_role",91+ROLES.indexOf(role));score2(player,"CAP_QDIALOG_KEEP",1);score2(player,"hcm_pq1_ttl",3600);
  run(player,"indivissequence context");run(player,"function capitale:pq01/session/save_macro with storage hc_quetes:session current");
 }
 static void prepare(JsonObject j){String key=j.get("key").getAsString();seed(key,j.get("role").getAsString());run(player,"function capitale:pq01/node/"+key);wait=0;state=1;}
 static boolean tick()throws Exception{
  if(phase==0){
   if(index>=jobs.size()){extra();phase=1;wait=0;return false;}
   JsonObject j=jobs.get(index);String key=j.get("key").getAsString();int pick=j.get("pick").getAsInt();
   if(state==0){prepare(j);return false;}
   if(++wait<3)return false;
   if(state==1){
    check(score(player,"hcm_pq1_node")==j.get("id").getAsInt(),"bloc natif "+key+" choix="+pick);
    check(currentSession(player)!=null,"session b10 présente "+key);
    if(pick==0){index++;state=0;return false;}
    int token=score(player,"hcm_pq1_tok");run(player,"trigger QuestChoix set "+(token+pick));run(player,"function capitale:pq01/dispatch_self");state=2;wait=0;return false;
   }
   String target=j.get("target").getAsString();check(score(player,"hcm_pq1_node")==Pq01Ids.id(target),"choix natif "+key+" #"+pick+" → "+target);index++;state=0;return false;
  }
  if(phase==1){
   if(++wait<110)return false;
   check(score(player,"hcm_pq1_active")==0,"Géraud ne déclenche pas sans déplacement après le délai de spawn");
   call(player,"method_5814",24.5d,64d,16.8d);bridge("nearGeraud",player);
   check(score(player,"hcm_pq1_active")==1,"Géraud interpelle après déplacement et approche à trois blocs");
   bridge("close",player,true);run(player,"function capitale:pq01/close_self");startPerrin();phase=2;wait=0;return false;
  }
  if(phase==2){
   wait++;Point here=pos(actor,world),there=pos(otherActor,world);
   moved|=here.distance2(pa)>0.8;otherMoved|=there.distance2(pa)>0.8;
   returned|=moved&&wait>140&&here.distance2(pa)<0.5;
   if(wait>650)throw new AssertionError("Perrin ne finit pas son aller-retour : "+here+" échec="+get(get(perrinView,"actors") instanceof Map<?,?> map?map.get("perrin"):null,"failure"));
   if(!returned)return false;
   check(moved&&returned,"Perrin marche réellement de A à B puis revient à A");check(otherMoved,"Perrin marche aussi pour le second joueur");
   check(!Boolean.TRUE.equals(stat("net.hautecapitale.quetes.visibility.QuestVisibility","isVisibleTo",actor,other)),"Perrin personnel invisible à l’autre joueur");
   run(player,"execute store result score @s hcm_pq1_temp run attribute "+uuid(perrinSource)+" minecraft:movement_speed base get 1000");check(score(player,"hcm_pq1_temp")==0,"vitesse nulle du Perrin public conservée");
   check(pos(perrinSource,world).equals(pa),"Perrin public reste au poste");
   bridge("close",player,true);check(!Boolean.TRUE.equals(call(otherActor,"method_31481")),"nettoyage d’une ambiance conserve l’acteur de l’autre joueur");bridge("close",other,true);
   tail();check(net.tompsen.nexuscharacters.NexusCharacters.getSelectedCharacter(player).id().equals(originalCharacter),"personnage Nexus inchangé après audit");
   System.out.println("PASS "+(Pq02WorldBench.checks-checksBefore)+" additional prologue native assertions");return true;
  }
  return false;
 }
 static void extra(){
  seed("ga_menu","geraud");run(player,"function capitale:pq01/node/ga_menu");
  // Le jeton périmé est refusé avant toute réplique ou mutation de progression.
  score2(player,"QuestChoix",1);int before=score(player,"hcq_pq01");run(player,"function capitale:pq01/dispatch_self");check(score(player,"hcq_pq01")==before,"jeton périmé sans progression");
  bridge("close",player,true);run(player,"function capitale:pq01/close_self");
  for(String k:List.of("hcf_pq1_a","hch_pq1node","CAP_QSEQ","CAP_QDIALOG_OWNER","hcm_pq1_active"))score2(player,k,0);
  score2(player,"hcq_pq01",0);bridge("execute",player,"rearm_near");call(player,"method_5814",25.5d,64d,17.5d);bridge("nearGeraud",player);
 }
 static void startPerrin()throws Exception{
  Object config=Pq01Nbt.copy(setup(player)),points=GuardNbt.childOrCreate(config,"points");perrinSource=sources.get("perrin");
  pa=new Point(dimension(world),32.5,64,16.5);pb=new Point(dimension(world),34.5,64,16.5);call(perrinSource,"method_5814",pa.x(),pa.y(),pa.z());
  for(String name:List.of("perrin_a","perrin_b"))GuardNbt.put(points,name,pointNbt(name.endsWith("a")?pa:pb));
  GuardNbt.integer(config,"ambient_enabled",0);save(player,config);run(player,"attribute "+uuid(perrinSource)+" minecraft:movement_speed base set 0");
  for(Object p:List.of(player,other)){score2(p,"hcq_pq01",10);score2(p,"CAP_PROFILE_LOCK",0);score2(p,"hcm_pq1_active",0);score2(p,"CAP_QDIALOG_OWNER",0);call(p,"method_5814",33.5d,64d,18.5d);}
  perrinView=bridge("view",player);Object otherView=bridge("view",other);actor=get(bridge("spawn",perrinView,"perrin",pa),"entity");otherActor=get(bridge("spawn",otherView,"perrin",pa),"entity");
  check(!uuid(actor).equals(uuid(otherActor)),"deux joueurs ont deux Perrin distincts");moved=returned=otherMoved=false;
 }
 static int result(String fn){run(player,"execute store result score @s hcm_pq1_valid run function "+fn);return score(player,"hcm_pq1_valid");}
 static void tail(){
  for(String k:List.of("hcq_pq01","hcq_pq02","hcq_pq03","hcq_pq04","hcq_pq05","CAP_QUETEACTIVE","CAP_GUIDE_LOCK","CAP_QSEQ","CAP_QDIALOG_OWNER"))score2(player,k,0);
  score2(player,"hcq_pq01",40);score2(player,"hcm_pq1_node",89);score2(player,"hcf_pq1_gain",0);score2(player,"hcf_pq1_test",0);score2(player,"REP_COURONNE",20);score2(player,"CAP_RANGSOCIAL",0);
  run(player,"function capitale:pq01/complete_self");check(score(player,"hcq_pq01")==100&&score(player,"hcq_pq02")==30,"fin PQ01 ouvre PQ02 disponible (état canonique 30) : "+score(player,"hcq_pq01")+"/"+score(player,"hcq_pq02"));check(score(player,"REP_COURONNE")==22,"récompense historique Couronne +2 conservée");
  run(player,"function capitale:pq01/complete_self");run(player,"function capitale:pq01/reward_body_self");check(score(player,"REP_COURONNE")==22,"rejeu de fin PQ01 ne double pas la réputation");
  score2(player,"hcq_pq02",100);score2(player,"CAP_QUETEACTIVE",0);check(result("capitale:quest/markers/available/pq03")==1,"marqueur Aurèle disponible après PQ02");
  score2(player,"hcq_pq03",100);score2(player,"CAP_QUETEACTIVE",0);check(result("capitale:quest/markers/available/pq04")==1,"marqueur Roch disponible après PQ03");
  score2(player,"hcq_pq04",100);score2(player,"CAP_QUETEACTIVE",0);score2(player,"CAP_RANGSOCIAL",10);check(result("capitale:quest/markers/available/pq05")==1,"marqueur résidence disponible après PQ04");
  score2(player,"CAP_RANGSOCIAL",20);check(result("capitale:quest/markers/available/pq05")==0,"résidence déjà inscrite ne propose pas une nouvelle inscription");
  score2(player,"hcq_pq05",100);run(player,"function capitale:quest/prologue/normalize_self");check(score(player,"QUEST_PROLOGUE")==100,"normalisation reconnaît les cinq quêtes terminées");
 }
}
