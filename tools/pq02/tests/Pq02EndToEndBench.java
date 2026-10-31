package net.hautecapitale.quetes.sequence;

import java.util.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.*;
import static net.hautecapitale.quetes.sequence.Pq02WorldBench.*;

/** Chronological playthroughs. No ready(), showKey(), forced stages or decision resets after start. */
final class Pq02EndToEndBench {
 static int step,age,pausedWaypoint;static boolean done;static Object pausedActor;static net.hautecapitale.quetes.sequence.Pq01Motion.Point pausedPosition;
 static final Set<String> visited=new LinkedHashSet<>(),interrupted=new LinkedHashSet<>();
 static final Set<Integer> walkedA=new LinkedHashSet<>(),walkedB=new LinkedHashSet<>();
 static void check(boolean value,String label){ok(value,"PQ02 chronological: "+label);}
 static void initial(Object p){seed(p,30);for(String key:List.of("CAP_PQ2_NODE","CAP_PQ2_WP","CAP_PQ2_DA","CAP_PQ2_DG","CAP_PQ2_DM","CAP_PQ2_DD","CAP_PQ2_IMPR","CAP_PQ2_REG","CAP_PQ2_REW","CAP_PQ2_SENT","CAP_PQ2_ANECD","CAP_PQ2_PORT","CAP_PQ2_STATUS","CAP_LEOVIC_GIFT","CAP_PQ2_OFFER","REP_GARDEPORT","fixture_bonus","fixture_skills","CAPSK_REWARD_REGPORT","CAP_QUETEACTIVE"))score2(p,key,0);}
 static void start()throws Exception{
  initial(a);initial(b);seed(observer,100);run(a,"function capitale:pq02/load");Pq02SetupBench.run();
  Pq02HandoffBench.installLookCallbacks();
  call(call(srv,"method_3760"),"method_14582",make("net.minecraft.class_11560",call(a,"method_7334")));
  score2(b,"CAP_RANGSOCIAL",30);score2(a,"CAP_RANGSOCIAL",0);
  Pq02EntryBench.click(a,leo);production();settle(a);record(a);
  choose(a,1);
  for(String key:List.of("a04","a04a","a04b","a05","a05b","a06","a06b","a07")){go(a,key);interrupt(a);}
  // Rest is a real chosen branch; re-entry must lead back to the departure decision.
  go(a,"a08");choose(a,2);advance(a);
  check(v(a)==null&&stage(a)==30&&score(a,"CAP_PQ2_DA")==1,"rest disposes its scene without losing arrival decision");
  Pq02EntryBench.click(a,leo);production();settle(a);check(v(a).node.key.equals("a08"),"return after rest offers departure again");
  choose(a,1);interrupt(a); // closing accepted a09 really starts the first escort
  check(stage(a)==40&&v(a).walking,"accepted departure starts through native client close");
  Pq02EntryBench.click(b,leo);production();settle(b);choose(b,3);go(b,"a08");choose(b,1);advance(b);
  check(stage(b)==40&&v(b).walking,"second player starts independently");
  step=0;age=0;
 }
 static void record(Object p){View v=v(p);if(v==null||v.pendingNode!=null||!v.ui)return;visited.add(v.node.key);check(v.session==current(p)&&v.baseToken>0,"current native session and selectable frame at "+v.node.key);}
 static void choose(Object p,int pick){Pq02WorldBench.pick(p,pick);record(p);}
 static void forward(Object p){choose(p,14);}
 static void interact(Object p,String role){View v=v(p);Object actor=v!=null&&v.actors.containsKey(role)?v.actors.get(role).entity:role.equals("leovic")?leo:mag;
  stat(DM,"ouvrir",p,actor,en("net.hautecapitale.dialogue.session.DialogueMode","CAPTURE"));
  if(tags(actor).contains(TAG))call(call(actor,"getEasyNPCActionEventData"),"handleActionEvent",en("de.markusbordihn.easynpc.data.action.ActionEventType","ON_INTERACTION"),p);else Pq02EntryBench.command(p,actor);
  production();settle(p);check(v(p)!=null&&v(p).ui,"real NPC action reopens "+role);record(p);
 }
 static void interrupt(Object p){View v=v(p);String key=v.node.key;int phase=stage(p),wp=v.waypoint,rep=score(p,"REP_GARDEPORT"),impression=score(p,"CAP_PQ2_IMPR"),gift=score(p,"CAP_LEOVIC_GIFT");
  Object session=v.session,actor=v.actors.get("leovic").entity;int base=v.baseToken;
  Pq02ProfileCheckpointBench.snapshot(p);
  check(v(p)==v&&v.session==session&&current(p)==session&&v.baseToken==base,"real periodic Nexus save retains frame at "+key);
  Pq02HandoffBench.nativeClose(p,"ESC");interrupted.add(key);
  String agreed=Pq02Transitions.agreedExit(v.node.next,phase);if(!agreed.isEmpty())return;
  check(stage(p)==phase&&score(p,"REP_GARDEPORT")==rep&&score(p,"CAP_PQ2_IMPR")==impression&&score(p,"CAP_LEOVIC_GIFT")==gift,"Escape preserves state and awards at "+key);
  interact(p,key.startsWith("m")&&phase==50?"magistrat":"leovic");
  check(v(p).node.key.equals(key),"reopening resumes exact narrative page "+key);
  check(v(p).waypoint==wp,"reopening preserves waypoint at "+key);
  if(phase>=40&&phase<100)check(v(p).actors.get("leovic").entity==actor,"reopening keeps current private guide at "+key);
 }
 record Hop(String key,List<Integer> picks){}
 static void go(Object p,String target){
  View v=v(p);if(v.node.key.equals(target))return;Queue<Hop> queue=new ArrayDeque<>();Set<String> seen=new HashSet<>();queue.add(new Hop(v.node.key,List.of()));List<Integer> found=null;
  while(!queue.isEmpty()){Hop hop=queue.remove();if(!seen.add(hop.key))continue;if(hop.key.equals(target)){found=hop.picks;break;}Pq02Data.Node n=Pq02Data.get(hop.key);
   List<Pq02Data.Choice> choices=n.choices.stream().filter(c->condition(v,c.condition)).toList();
   for(int i=0;i<choices.size();i++){var c=choices.get(i);if(c.effect.contains(":")){String objective=Pq02Rules.DECISIONS.get(c.effect.split(":")[0]);if(objective!=null&&score(p,objective)!=0)continue;}enqueue(v,queue,hop,c.target,i+1);}
   if(!n.next.isEmpty())enqueue(v,queue,hop,n.next,14);
  }
  if(found==null)throw new AssertionError("No offered path from "+v.node.key+" to "+target+" at phase "+stage(p));
  for(int pick:found)choose(p,pick);check(v(p).node.key.equals(target),"offered choices naturally reach "+target);
 }
 static void enqueue(View v,Queue<Hop> queue,Hop hop,String target,int pick){if(target.startsWith("event:"))return;String resolved=resolvedKey(v,target);if(!Pq02Rules.validNode(resolved,stage(v.player)))return;List<Integer> path=new ArrayList<>(hop.picks);path.add(pick);queue.add(new Hop(resolved,List.copyOf(path)));}
 static void walkingTopics(Object p)throws Exception{
  interact(p,"leovic");check(v(p).node.key.equals("t_menu"),"walking interaction opens optional topics");
  check(v(p).choices.stream().anyMatch(c->c.target.equals("t05"))==(score(p,"CAP_PQ2_SENT")==1)&&v(p).choices.stream().noneMatch(c->c.target.equals("t06")),"conditional topics match real sentinel discovery and hide the unknown anecdote");
  go(p,"t02a");check(score(p,"CAP_PQ2_ANECD")==1,"anecdote unlocked through its real dialogue");
  // Aim the synthetic player's eyes at the actual, visible sentinel, then run the real observation rule.
  var at=pos(p,w);var guard=pos(sentinel,w);float yaw=(float)(Math.toDegrees(Math.atan2(guard.z()-at.z(),guard.x()-at.x()))-90);call(p,"method_36456",yaw);
  closeUi(v(p));resume(v(p));observe(v(p));check(score(p,"CAP_PQ2_SENT")==1,"real visible sentinel observation unlocks its topic");interact(p,"leovic");
  for(String key:List.of("t02a_stay","t02a_leave","t02b","t03","t03a","t03b","t04","t04a","t04a_reputation","t04a_cooperation","t04b","t04b_a","t04c","t04c_field","t04c_mission","t04d","t05","t05a","t05b","t05c","t06","t06a")){go(p,key);interrupt(p);}
  int rep=score(p,"REP_GARDEPORT"),impr=score(p,"CAP_PQ2_IMPR");go(p,"t04");choose(p,2);check(v(p).node.key.equals("t04b_a")&&score(p,"REP_GARDEPORT")==rep&&score(p,"CAP_PQ2_IMPR")==impr,"revisiting guard decision uses remembered answer without repeat reward");
  go(p,"t_resume");forward(p);check(v(p).walking&&!v(p).ui,"explicit return resumes current leg");
 }
 static void magistrate(Object p,boolean positive)throws Exception{
  settle(p);record(p);check(v(p).node.key.equals("t07"),"physical arrival at M opens its real pre-interview");
  choose(p,positive?1:2);forward(p);
  check(v(p).focus==v(p).anchor&&v(p).actors.size()==2,"three-party camera uses both existing private characters");
  if(positive)interrupt(p);
  forward(p);forward(p);forward(p);forward(p); // four interleaved speaker blocks -> m02
  check(v(p).node.key.equals("m02"),"magistrate opening naturally reaches identity choice");
  choose(p,positive?2:3);if(positive)interrupt(p);choose(p,positive?1:3);choose(p,positive?1:3);
  if(positive){forward(p);interrupt(p);forward(p);
   for(String key:List.of("m05a","m05a_unknown","m05b","m05b_resident","m05c","m05c_leovic","m05c_magistrat","m05d")){go(p,key);interrupt(p);}
  }else {forward(p);forward(p);}
  go(p,"m06");if(positive)interrupt(p);forward(p);
  check(stage(p)==60&&score(p,"CAP_PQ2_REG")==1&&score(p,"CAP_RANGSOCIAL")== (positive?10:30),"registration happens once and preserves a higher social rank");
  if(positive)interrupt(p);choose(p,positive?1:3);forward(p);choose(p,positive?1:3);forward(p);
  check(stage(p)==70&&v(p).walking&&v(p).waypoint==14,"post-registration dialogue starts the actual short M-P escort");
 }
 static void finalConversation(Object p,boolean positive)throws Exception{
  settle(p);record(p);check(v(p).node.key.equals("f_menu"),"physical arrival at P opens the final conversation");
  if(positive){for(String key:List.of("f02","f03","f03_prefer","f04","f04_return","f05_short")){go(p,key);interrupt(p);if(v(p).node.next.equals("event:practical_menu"))forward(p);}}
  go(p,"f06");choose(p,positive?3:4);if(positive)interrupt(p);forward(p);
  if(!positive)forward(p); // contempt -> f_report -> f07
  check(v(p).node.key.equals("f07"),"gift branch reaches separation questions");
  choose(p,positive?2:3);if(positive)interrupt(p);forward(p);
  check(stage(p)==100&&v(p)==null&&current(p)==null&&score(p,"CAP_PQ2_REW")==1,"completion closes camera, retires actors and advances the quest");
  check(score(p,"REP_GARDEPORT")== (positive?3:-1)&&score(p,"CAP_PQ2_IMPR")== (positive?4:-1),"full chronological reputation and global impression match choices");
  check(score(p,"CAP_LEOVIC_GIFT")== (positive?1:0)&&score(p,"fixture_skills")==1,"immediate gift/refusal and original skill reward remain distinct");
 }
 static void returnConversation(Object p,boolean positive){call(p,"method_5814",0.5d,64d,2.5d);interact(p,"leovic");check(v(p).node.key.equals(positive?"post_very_friendly":"post_unfriendly"),"permanent post greeting uses total impression");
  go(p,"f03");choose(p,1);forward(p);check(v(p).node.key.equals("post_menu"),"post-quest practical reply returns to the post-quest menu");
  if(!positive){go(p,"post_gift");choose(p,2);go(p,"post_gift");choose(p,1);check(v(p).node.key.equals("post_menu")&&score(p,"CAP_LEOVIC_GIFT")==1,"multiple late refusals then acceptance give the pending gift");}
  run(p,"execute store result score @s fixture_money run clear @s minecraft:gold_nugget 0");check(score(p,"fixture_money")==50,"entire playthrough and late return give exactly 50 currency substitute items");
  run(p,"function capitale:pq02/gift_once_self");run(p,"function capitale:pq02/complete_once_self");run(p,"execute store result score @s fixture_money run clear @s minecraft:gold_nugget 0");
  check(score(p,"fixture_money")==50&&score(p,"fixture_skills")==1&&score(p,"REP_GARDEPORT")== (positive?3:-1),"replayed terminal functions cannot duplicate rewards or change reputation");suspend(p);
 }
 static void diskResume(){View before=v(a);String node=before.node.key;int wp=before.waypoint;var at=pos(before.actors.get("leovic").entity,w);var dto=Pq02ProfileCheckpointBench.dto(a);
  net.tompsen.nexuscharacters.ProfileStateBridge.save((net.minecraft.class_3222)a,dto);check(v(a)==null&&current(a)==null,"real final Nexus save retires the paused scene");
  net.tompsen.nexuscharacters.ProfileStateBridge.loadStrict((net.minecraft.class_3222)a,dto);interact(a,"leovic");
  check(v(a).waypoint==wp&&v(a).node.key.equals(node)&&pos(v(a).actors.get("leovic").entity,w).distance2(at)<0.01,"real Nexus disk reload restores paused page, precise position and waypoint");
 }
 static void diskCharacters(){var dto=Pq02ProfileCheckpointBench.dto(a);Map<String,Integer> expected=new LinkedHashMap<>();for(String key:List.of("hcq_pq02","CAP_PQ2_DA","CAP_PQ2_DG","CAP_PQ2_DM","CAP_PQ2_DD","CAP_PQ2_IMPR","CAP_LEOVIC_GIFT","CAP_PQ2_OFFER","CAP_PQ2_REW","REP_GARDEPORT"))expected.put(key,score(a,key));
  net.tompsen.nexuscharacters.ProfileStateBridge.save((net.minecraft.class_3222)a,dto);
  net.tompsen.nexuscharacters.NexusCharacters.select(a,charB);var other=Pq02ProfileCheckpointBench.dto(a);net.tompsen.nexuscharacters.ProfileStateBridge.loadStrict((net.minecraft.class_3222)a,other);
  check(score(a,"hcq_pq02")==0&&score(a,"CAP_PQ2_IMPR")==0&&score(a,"CAP_LEOVIC_GIFT")==0,"actual Nexus loading of a second character clears first-character quest, memory and gift");
  net.tompsen.nexuscharacters.ProfileStateBridge.save((net.minecraft.class_3222)a,other);net.tompsen.nexuscharacters.NexusCharacters.select(a,dto.id());net.tompsen.nexuscharacters.ProfileStateBridge.loadStrict((net.minecraft.class_3222)a,dto);
  for(var row:expected.entrySet())check(score(a,row.getKey())==row.getValue(),"actual Nexus A-B-A disk restore retains "+row.getKey());
 }
 static void followingQuest(){
  check(stage(a)==100&&score(a,"QUEST_PROLOGUE")==30&&score(a,"CAP_QUETEACTIVE")==0&&score(a,"CAP_GUIDE_LOCK")==0&&score(a,"CAP_QDIALOG_OWNER")==0,"real PQ02 completion leaves no active quest, guide or dialogue lock for its successor");
  check(waypoint(a,"hcm_pq2_leovic").isEmpty()&&waypoint(a,"hcm_pq2_magistrat").isEmpty(),"completed private scene no longer overrides journal waypoints");
  run(a,"execute store result score @s hcm_pq1_valid run function capitale:quest/markers/available/pq03");check(score(a,"hcm_pq1_valid")==1,"Aurele quest marker is eligible after actual completion and disk restoration");
  int rep=score(a,"REP_GARDEPORT"),gift=score(a,"CAP_LEOVIC_GIFT");run(a,"function capitale:quest/sous_le_regard_du_coeur/aurele/interact_self");
  check(score(a,"hcq_pq03")==10&&score(a,"CAP_QUETEACTIVE")==3&&score(a,"CAP_QSEQ")==701,"original Aurele router naturally opens PQ03 offer after PQ02");
  check(stage(a)==100&&score(a,"REP_GARDEPORT")==rep&&score(a,"CAP_LEOVIC_GIFT")==gift&&v(a)==null,"entering PQ03 does not reopen PQ02, replay a reward or recreate its escort");
 }
 static boolean tick()throws Exception{
  if(done)return true;age++;if(age>2200)throw new AssertionError("chronological timeout step="+step+" A="+(v(a)==null?"none":v(a).failure)+" B="+(v(b)==null?"none":v(b).failure));
  Pq02HandoffBench.follow(b);if(step!=1)Pq02HandoffBench.follow(a);production();
  for(Object p:List.of(a,b)){View v=v(p);if(v!=null){(p==a?walkedA:walkedB).add(v.waypoint);if(age%20==0&&v.ui&&v.pendingNode==null)check(v.session==current(p),"periodic b10 verification retains "+v.node.key);}}
  if(step==0&&v(a).waypoint>=2){walkingTopics(a);interact(a,"leovic");pausedActor=v(a).actors.get("leovic").entity;pausedPosition=pos(pausedActor,w);pausedWaypoint=v(a).waypoint;step=1;age=0;}
  else if(step==1&&age==220){check(v(a).ui&&!v(a).walking&&v(a).session==current(a)&&pos(pausedActor,w).distance2(pausedPosition)<0.001,"long optional menu stays stationary through actual server and native DialogueManager ticks");diskResume();Pq02HandoffBench.nativeClose(a,"ESC");check(v(a).walking&&v(a).waypoint==pausedWaypoint,"long pause resumes its original waypoint");step=2;age=0;}
  else if(step==2&&stage(a)==50&&stage(b)==50){magistrate(a,true);magistrate(b,false);step=3;age=0;}
  else if(step==3&&stage(a)==90&&stage(b)==90){finalConversation(a,true);finalConversation(b,false);returnConversation(a,true);returnConversation(b,false);diskCharacters();
   trackers();check(!foreign&&pos(leo,w).distance2(publicStart)<0.001,"two complete escorts remain private and never move public Leovic");check(v(a)==null&&v(b)==null&&Pq02Entries.PENDING.isEmpty(),"no orphan scene or queued transition remains");check(ERRORS.isEmpty(),"complete chronological routes cause no PQ02 runtime exceptions");
   check(walkedA.size()>=16&&walkedB.size()>=16,"both physical routes passed their existing intermediate waypoints");
   evidence.add("Chronological visited pages="+visited);evidence.add("Native close/reopen pages="+interrupted);evidence.add("A waypoints="+walkedA+" B waypoints="+walkedB);
   // Existing targeted regressions complement the chronological paths, without rewriting those quests.
   followingQuest();HuguesBench.runAll(a);Mq03Bench.runAll(a,observer);done=true;
  }
  return done;
 }
}
