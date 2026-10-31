package net.hautecapitale.quetes.sequence;
import java.util.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.call;

/** Attend la session b10 ET son acteur côté client avant l'accusé de réception. */
final class Pq02FocusClient {
 static final String STATE="net.hautecapitale.dialogue.client.DialogueClientState";
 static Pq02Focus.Request pending;static Object stableScreen;static int age,stable,retry,attempts;static boolean sent,traceLost,traceRemoved,pageReceived;
 static void receive(String text){Pq02Focus.Request request=Pq02Focus.parse(text);if(request==null||request.equals(pending))return;pending=request;stableScreen=null;age=stable=retry=attempts=0;sent=traceLost=traceRemoved=pageReceived=false;System.out.println("[PQ02 focus] demande reçue : session="+request.session()+", acteur="+request.actor());keepAlive();}
 static Object state(String key){return Pq02Client.field(type(STATE),key);}
 static void keepAlive(){if(pending!=null&&!pageReceived&&age<=200&&Boolean.TRUE.equals(stat(STATE,"captureActive"))&&Objects.equals(state("sessionId"),pending.session())&&Objects.equals(state("pnjUuid"),pending.actor())&&!Boolean.TRUE.equals(state("finEnvoyee")))Pq02Client.state("fenetreJusquaMs",System.currentTimeMillis()+2000L);}
 static void page(Object session){if(pending!=null&&Objects.equals(session,pending.session()))pageReceived=true;}
 static void tick(Object client){
  if(pending==null)return;age++;
  if(!Boolean.TRUE.equals(stat(STATE,"captureActive"))||!Objects.equals(state("sessionId"),pending.session())||!Objects.equals(state("pnjUuid"),pending.actor())||Boolean.TRUE.equals(state("finEnvoyee"))){
   if(sent&&!traceLost){traceLost=true;System.out.println("[PQ02 focus] fin/changement de session : attendue="+pending.session()+", courante="+state("sessionId")+", raison="+state("derniereRaison"));}
   stable=0;if(age>200)clear();return;
  }
  Object screen=Pq02Client.field(client,"field_1755");
  if(sent){if(!Pq02Client.captureScreen(screen)&&!traceLost){traceLost=true;System.err.println("[PQ02 focus] écran quitté après confirmation : session="+pending.session()+", actif="+stat(STATE,"captureActive")+", fin="+state("finEnvoyee")+", raison="+state("derniereRaison")+", écran="+(screen==null?"aucun":screen.getClass().getName()));}return;}
  Pq02Client.state("fenetreJusquaMs",System.currentTimeMillis()+2000L);
  // Une ancienne CaptureDialogueScreen peut rester affichée lors d'un remplacement
  // de session. Réarmer seulement la nouvelle session demandée par le serveur.
  if(screen==null||Pq02Client.captureScreen(screen)&&Boolean.TRUE.equals(Pq02Client.field(screen,"fermetureNotifiee"))){
   if(screen!=null)call(screen,"fermerSansPrevenir");
   screen=make("net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen",String.valueOf(stat(STATE,"pnjNom")));
   call(client,"method_1507",screen);stable=0;
  }
  if(!Pq02Client.captureScreen(screen)){stable=0;return;}
  if(state("ecranCourant")!=screen)stat(STATE,"reprendreEcran",screen);
  Object world=Pq02Client.field(client,"field_1687");Object entity=world==null?null:call(world,"method_8469",state("pnjEntityId"));
  if(entity==null||!pending.actor().equals(call(entity,"method_5667"))||Boolean.TRUE.equals(call(entity,"method_31481"))){stable=0;return;}
  if(stableScreen!=screen){stableScreen=screen;stable=0;}if(++stable<2||age<retry)return;
  Object network=call(client,"method_1562");if(network==null)return;
  call(network,"method_45730","indivispq02 ready "+pending.token());sent=true;attempts++;retry=age+20;
  System.out.println("[PQ02 focus] prêt : session="+pending.session()+", acteur="+pending.actor());
 }
 static void removed(Object screen){
  if(pending==null||traceRemoved||!Objects.equals(state("sessionId"),pending.session())||Boolean.TRUE.equals(state("finEnvoyee")))return;
  traceRemoved=true;System.err.println("[PQ02 écran] retrait sans fin : session="+pending.session()+", confirmé="+sent+", raison="+state("derniereRaison"));
  StackTraceElement[] stack=Thread.currentThread().getStackTrace();for(int i=1;i<Math.min(stack.length,13);i++)System.err.println("[PQ02 écran] "+stack[i]);
 }
 static void clear(){pending=null;stableScreen=null;age=stable=0;sent=traceLost=traceRemoved=pageReceived=false;}
}
