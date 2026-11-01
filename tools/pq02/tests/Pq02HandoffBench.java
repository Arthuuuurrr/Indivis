package net.hautecapitale.quetes.sequence;

import java.util.*;
import net.hautecapitale.quetes.sequence.Pq01Motion.Point;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.*;
import static net.hautecapitale.quetes.sequence.Pq02WorldBench.*;

/** Replays Arthur's actual choices, then checks the real actor navigation and native b10 exits. */
final class Pq02HandoffBench {
 static boolean done;static int step,ticks,pauseWaypoint;static Point stopped;static UUID actorA,actorB;static Object entityA;
 static int lookOpens,lookCloses;
 static void installLookCallbacks()throws Exception{
  Class<?> type=Class.forName("net.hautecapitale.dialogue.compat.easynpc.EasyNpcLookAt");
  var open=type.getDeclaredMethod("poser",net.minecraft.class_3222.class,net.hautecapitale.dialogue.session.DialogueSession.class);var close=type.getDeclaredMethod("retirer",net.minecraft.class_3222.class,net.hautecapitale.dialogue.session.DialogueSession.class);open.setAccessible(true);close.setAccessible(true);
  stat(DM,"onOuverture",(java.util.function.BiConsumer<Object,Object>)(p,s)->{try{open.invoke(null,p,s);lookOpens++;}catch(ReflectiveOperationException e){throw new RuntimeException(e);}});
  stat(DM,"onFermeture",(java.util.function.BiConsumer<Object,Object>)(p,s)->{try{close.invoke(null,p,s);lookCloses++;}catch(ReflectiveOperationException e){throw new RuntimeException(e);}});
 }
 static void check(boolean value,String label){ok(value,"handoff HF7: "+label);}
 static void fresh(Object p,int s){suspend(p);score2(p,"CAP_PROFILE_LOCK",0);seed(p,s);score2(p,"CAP_PQ2_NODE",0);score2(p,"CAP_PQ2_WP",s>=90?17:s>=50?14:0);for(String k:Pq02Rules.DECISIONS.values())score2(p,k,0);score2(p,"CAP_PQ2_REG",s>=60?1:0);score2(p,"CAP_PQ2_REW",0);score2(p,"CAP_LEOVIC_GIFT",0);score2(p,"CAP_PQ2_IMPR",0);score2(p,"REP_GARDEPORT",0);score2(p,"CAP_QUETEACTIVE",0);call(p,"method_5814",0.5d,64d,2.5d);}
 static void nativeClose(Object p,String reason){View v=v(p);Object session=v.session;stat(DM,"onFinClient",p,((Number)Pq01Client.field(session,"id")).intValue(),en("net.hautecapitale.dialogue.session.CloseReason",reason));}
 static void at(Object p,String key){Pq02CaptureBench.ready(p,Pq02Data.get(key));}
 static void privateInterop(boolean legacy){
  fresh(a,40);at(a,"t_menu");pick(a,15);Object actor=v(a).actors.get("leovic").entity;
  Object data=call(actor,"getEasyNPCActionEventData"),interaction=en("de.markusbordihn.easynpc.data.action.ActionEventType","ON_INTERACTION");
  call(data,"handleActionEvent",interaction,a);settle(a);check(v(a).ui,"real EasyNPC private interaction pauses and opens the optional menu");
  closeUi(v(a));resume(v(a));stat(DM,"ouvrir",a,actor,en("net.hautecapitale.dialogue.session.DialogueMode","CAPTURE"));
  call(data,"handleActionEvent",interaction,a);settle(a);
  if(legacy)check(!v(a).ui&&current(a)!=null&&!v(a).walking,"HF6 reproduces private interaction blocked when b10 captures the owned actor first");
  else check(v(a).ui&&v(a).session==current(a)&&v(a).focus==actor,"real private interaction also accepts b10 capture already opened for the owned actor");
  stat(DM,"fermer",a,en("net.hautecapitale.dialogue.session.CloseReason","FERMEE"));suspend(a);
 }
 static void replay(Object p){fresh(p,30);Pq02EntryBench.click(p,leo);production();settle(p);
  String[] expected={"a02a","a07","a06","a06b","a08","a09"};int[] picks={1,4,3,1,5,1};
  for(int i=0;i<picks.length;i++){pick(p,picks[i]);check(v(p).node.key.equals(expected[i]),"actual log choice #"+picks[i]+" reaches "+expected[i]);}
 }
 static void start(boolean legacy)throws Exception{installLookCallbacks();fresh(a,30);fresh(b,30);run(a,"function capitale:pq02/load");Pq02SetupBench.run();
  replay(a);entityA=v(a).actors.get("leovic").entity;int reputation=score(a,"REP_GARDEPORT");pick(a,15);
  if(legacy){check(stage(a)==30&&v(a)==null&&Boolean.TRUE.equals(call(entityA,"method_31481")),"HF6 reproduces Arthur's trace: closing a09 discards accepted departure and removes actor");check(score(a,"REP_GARDEPORT")==reputation,"failure does not consume a new arrival reward");privateInterop(true);done=true;return;}
  check(stage(a)==40&&v(a)!=null&&v(a).walking&&v(a).actors.get("leovic").entity==entityA,"closing accepted a09 starts the actual escort and retains its actor");
  check(score(a,"REP_GARDEPORT")==reputation,"departure adds no reputation or gift");suspend(a);
  // The server's actual text, click event and native b10 classification are inspected at all event pages.
  Pq02CaptureBench.player=a;Pq02CaptureBench.state=type("net.hautecapitale.dialogue.client.DialogueClientState");Pq02CaptureBench.active=true;
  try{for(Pq02Data.Node n:Pq02Data.NODES.values())if(n.next.startsWith("event:")){
   at(a,n.key);View v=v(a);List<?> choices=(List<?>)stat("net.hautecapitale.dialogue.client.DialogueClientState","choixCaptures");Object action=choices.get(choices.size()-1);String label=(String)call(call(action,"libelle"),"getString");
   check(!label.equals("Fermer la conversation")&&String.valueOf(call(action,"type")).equals("QUETE"),"visible green action replaces misleading close at "+n.key);
   check(call(call(action,"evenement"),"comp_3506").equals("/trigger Pq02Choix set "+(v.baseToken+14)),"visible action executes the real transition at "+n.key);
  }}finally{Pq02CaptureBench.active=false;suspend(a);}
  for(String key:List.of("a09","a08a","t_resume","m07a","m07b","m07c"))for(String reason:List.of("ESC","ADIEU","ECRAN_FERME")){
   fresh(a,Pq02CaptureBench.phase(Pq02Data.get(key)));at(a,key);Object actor=v(a).actors.get("leovic").entity;nativeClose(a,reason);
   int expected=key.equals("a08a")?30:key.startsWith("m07")?70:40;
   check(stage(a)==expected&&(key.equals("a08a")?v(a)==null&&score(a,"CAP_PQ2_NODE")==Pq02Data.get("a08").id:v(a)!=null&&v(a).walking&&v(a).actors.get("leovic").entity==actor),key+" handles native "+reason+" with correct phase and actor");
  }
  for(String reason:List.of("MORT","DECONNEXION","DIMENSION","TELEPORTATION","REMPLACEE","INACTIVITE","PNJ_DISPARU","DISTANCE")){
   fresh(a,30);at(a,"a09");nativeClose(a,reason);check(stage(a)==30&&v(a)==null,"native "+reason+" never starts an accepted departure silently");
  }
  for(String key:List.of("m06","f06c","f07a")){
   fresh(a,Pq02CaptureBench.phase(Pq02Data.get(key)));at(a,key);int s=stage(a),gift=score(a,"CAP_LEOVIC_GIFT"),reg=score(a,"CAP_PQ2_REG"),rew=score(a,"CAP_PQ2_REW");nativeClose(a,"ESC");
   check(stage(a)==s&&score(a,"CAP_LEOVIC_GIFT")==gift&&score(a,"CAP_PQ2_REG")==reg&&score(a,"CAP_PQ2_REW")==rew,"Escape at "+key+" does not implicitly grant money, registration or completion");
  }
  fresh(a,30);at(a,"a09");suspend(a);check(stage(a)==30&&v(a)==null,"explicit profile suspension still cancels departure");
  privateInterop(false);
  replay(a);pick(a,15);actorA=v(a).actors.get("leovic").id;entityA=v(a).actors.get("leovic").entity;
  replay(b);pick(b,15);actorB=v(b).actors.get("leovic").id;check(!actorA.equals(actorB),"two accepted escorts use distinct actors");step=0;ticks=0;
 }
 static void follow(Object p){View v=v(p);if(v!=null&&v.walking){Point at=pos(v.actors.get("leovic").entity,w);call(p,"method_5814",at.x(),at.y(),at.z()+2);}}
 static boolean tick()throws Exception{if(done)return true;follow(a);follow(b);production();ticks++;
  if(ticks>1800)throw new AssertionError("HF7 navigation timed out, step="+step+", failure="+(v(a)==null?"no view":v(a).failure));
  if(step==0&&v(a).waypoint>=2){check(pos(entityA,w).distance2(publicStart)>1,"actual actor has walked beyond the first two waypoints without teleport");open(a,"leovic");settle(a);pauseWaypoint=v(a).waypoint;stopped=pos(entityA,w);step=1;ticks=0;}
  else if(step==1&&ticks==30){check(pos(entityA,w).distance2(stopped)<0.001&&!v(a).walking,"optional menu keeps actor and route paused");nativeClose(a,"ESC");check(v(a).walking&&v(a).waypoint==pauseWaypoint,"Escape resumes the paused waypoint without restarting route");step=2;ticks=0;}
  else if(step==2&&stage(a)==50){settle(a);check(v(a).waypoint==14&&v(a).actors.get("leovic").id.equals(actorA),"same private actor reaches magistrate M after optional pause");
   // Dialogue graph itself is exercised separately; this test exercises both physical handovers.
   showKey(a,"m06");advance(a);showKey(a,"m07c");nativeClose(a,"ESC");check(stage(a)==70&&v(a).walking&&v(a).waypoint==14,"closing post-registration reply starts the short M-P escort");step=3;ticks=0;
  }else if(step==3&&stage(a)==90){settle(a);check(v(a).waypoint==17&&v(a).actors.get("leovic").id.equals(actorA),"same actor reaches elevator P and opens final dialogue");trackers();check(!foreign,"neither escort leaked another player's private actor");check(pos(leo,w).distance2(publicStart)<0.001,"public Leovic remained at the original post");check(lookOpens>0&&lookCloses>0,"actual native b10 EasyNPC look-at open and close callbacks exercised through navigation");check(ERRORS.isEmpty(),"no handover or navigation runtime exception");suspend(a);suspend(b);done=true;}
  return done;
 }
}
