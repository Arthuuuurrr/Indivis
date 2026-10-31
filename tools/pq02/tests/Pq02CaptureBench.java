package net.hautecapitale.quetes.sequence;
import java.util.*;import java.lang.reflect.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.*;
/** Classification et état de capture b10 réels ; écran et moteur graphique doublés. */
public final class Pq02CaptureBench {
 static boolean active;static Object player;static Class<?> state;static int captures,edges;
 static final net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen screen=new net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen();
 public static final class Client {public Object field_1755=screen;public Network method_1562(){return new Network();}}
 public static final class Network {public void method_45730(String command){run(player,command);dispatch(VIEWS.get(uuid(player)));}}
 static void field(String name,Object value){try{Field f=state.getDeclaredField(name);f.setAccessible(true);f.set(null,value);}catch(Exception e){throw new RuntimeException(e);}}
 static void packet(Object p,Object text){if(!active||p!=player)return;
  Object session=current(p);if(session==null)return;
  field("etat",en("net.hautecapitale.dialogue.client.DialogueClientState$Etat","DIALOGUE_ACTIVE"));field("mode",en("net.hautecapitale.dialogue.session.DialogueMode","CAPTURE"));field("sessionId",Pq02Client.field(session,"id"));
  if(!Pq02Client.message(text,false))return;
  Object c=stat("net.hautecapitale.dialogue.client.capture.MessageCapture","classer",text,stat("net.hautecapitale.dialogue.client.DialogueClientState","pnjNom"),true,List.of());if(c!=null)call(state,"onMessageCapture",c);
  screen.versionVue=((Number)call(state,"captureVersion")).intValue();
 }
 static int phase(Pq02Data.Node n){String k=n.key;if(k.startsWith("post"))return 100;if(k.startsWith("a"))return 30;if(k.startsWith("t07"))return 50;if(k.startsWith("t"))return 40;if(k.startsWith("m06_registered")||k.matches("m06[abc]")||k.startsWith("m07"))return 60;if(k.startsWith("m"))return 50;return 90;}
 static void ready(Object p,Pq02Data.Node n){int s=phase(n);View v=VIEWS.get(uuid(p));if(v==null||stage(p)!=s){suspend(p);score2(p,"hcq_pq02",s);score2(p,"CAP_PQ2_WP",s==40?0:s>=90?17:14);v=Pq02Runtime.view(p);spawn(v,"leovic",startPosition(v));}
  score2(p,"hcq_pq02",s);score2(p,"CAP_PQ2_VER",1);score2(p,"CAP_QUETEACTIVE",0);score2(p,"CAP_PQ2_NODE",n.id);for(String key:Pq02Rules.DECISIONS.values())score2(p,key,0);
  score2(p,"REP_GARDEPORT",10);score2(p,"CAP_PQ2_IMPR",0);score2(p,"CAP_LEOVIC_GIFT",0);score2(p,"CAP_PQ2_OFFER",1);score2(p,"CAP_PQ2_REG",s>=60?1:0);score2(p,"CAP_PQ2_REW",0);score2(p,"CAPSK_REWARD_REGPORT",0);score2(p,"CAP_PQ2_SENT",1);score2(p,"CAP_PQ2_ANECD",1);score2(p,"CAP_PQ2_PORT",1);score2(p,"CAP_PQ2_STATUS",0);
  Object actor=v.actors.get("leovic").entity;var at=pos(actor,world(p));call(p,"method_5814",at.x(),at.y(),at.z()+2);show(v,n.key);Pq02WorldBench.settle(p);
 }
 @SuppressWarnings("unchecked") static void runAll(Object p){player=p;state=type("net.hautecapitale.dialogue.client.DialogueClientState");active=true;
  for(Pq02Data.Node n:Pq02Data.NODES.values()){
   ready(p,n);View v=VIEWS.get(uuid(p));List<Object> lines=(List<Object>)call(state,"repliquesCapturees"),choices=(List<Object>)call(state,"choixCaptures");
   Pq02WorldBench.ok(!lines.isEmpty(),"b10 capture contains real text for "+n.key);
   String speaker=(String)call(state,"pnjNom");Pq02WorldBench.ok(speaker.equals(n.speaker.equals("magistrat")?"Magistrat":"Léovic")||n.lines.isEmpty(),"b10 speaker for "+n.key);
   String captured=String.join("\n",lines.stream().map(e->String.valueOf(call(e,"getString"))).toList());for(String expected:n.lines)Pq02WorldBench.ok(captured.contains(expected),"b10 exact quote captured "+n.key);
   Pq02WorldBench.ok(choices.size()==v.choices.size()+1,"b10 has all choices and close for "+n.key);captures++;
   for(int i=0;i<n.choices.size();i++){ready(p,n);v=VIEWS.get(uuid(p));var choice=v.choices.get(i);int token=v.baseToken+i+1;score2(p,"Pq02Choix",token);Pq02WorldBench.ok(dispatch(v)==1,"graph native branch "+n.key+" #"+(i+1));Pq02WorldBench.settle(p);edges++;
    if(choice.effect!=null&&choice.effect.contains(":")){String[] d=choice.effect.split(":");var delta=Pq02Rules.delta(d[0],Integer.parseInt(d[1]));Pq02WorldBench.ok(score(p,"REP_GARDEPORT")==10+delta.reputation()&&score(p,"CAP_PQ2_IMPR")==delta.impression(),"branch delta exact "+n.key+" #"+(i+1));}
   }
   if(!n.next.isEmpty()){ready(p,n);v=VIEWS.get(uuid(p));if(n.next.equals("event:complete"))score2(p,"CAP_PQ2_DD",2);score2(p,"Pq02Choix",v.baseToken+14);Pq02WorldBench.ok(dispatch(v)==1,"graph native advance "+n.key);Pq02WorldBench.settle(p);edges++;}
  }
  Pq02WorldBench.ok(captures==Pq02Data.NODES.size(),"all 111 nodes pass native b10 capture");
  Pq02WorldBench.ok(edges>250,"all narrative choices and manual edges dispatched natively: "+edges);
  ready(p,Pq02Data.get("m01a"));screen.panneau.typing=true;Pq02WorldBench.ok(!Pq02Client.requestAdvance(screen),"client refuses advance during typewriter");screen.panneau.typing=false;screen.panneau.last=false;Pq02WorldBench.ok(!Pq02Client.requestAdvance(screen),"client refuses advance before final page");screen.panneau.last=true;
  Pq02WorldBench.ok(Pq02Client.requestAdvance(screen),"client accepts explicit final-page advance");Client client=new Client();for(int i=0;i<4;i++)Pq02Client.tickClient(client);Pq02WorldBench.settle(p);Pq02WorldBench.ok(VIEWS.get(uuid(p)).node.key.equals("m01b"),"real PQ02 client adapter advances correct native frame");
  active=false;suspend(p);
 }
}
