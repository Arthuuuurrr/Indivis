package net.hautecapitale.quetes.sequence;

import java.lang.reflect.*;
import java.util.*;
import net.minecraft.*;
import net.hautecapitale.dialogue.client.DialogueClientState;
import net.hautecapitale.dialogue.client.DialogueClientSettings;
import net.hautecapitale.dialogue.client.capture.*;
import net.hautecapitale.dialogue.network.DialogueNetwork;
import net.hautecapitale.dialogue.session.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.call;

/** Mixin réellement appliqué par Knot ; état b10 réel ; surfaces graphiques doublées. */
final class Pq02ScreenGuardBench {
 static final class_310 CLIENT=class_310.method_1551();static int session=4100,entity=4100,token=12345;
 static void invoke(String method,Class<?> type,Object argument)throws Exception{Method m=DialogueClientState.class.getDeclaredMethod(method,type);m.setAccessible(true);try{m.invoke(null,argument);}catch(InvocationTargetException e){throw new RuntimeException(e.getCause());}}
 static void check(boolean ok,String label){Pq02WorldBench.ok(ok,"screen HF5: "+label);}
 static UUID actor(int id){return UUID.nameUUIDFromBytes(("PQ02 screen fixture "+id).getBytes(java.nio.charset.StandardCharsets.UTF_8));}
 static void tick()throws Exception{Pq02Client.state("changementMs",0L);invoke("tick",class_310.class,CLIENT);Pq02Client.tickClient(CLIENT);}
 static void publish(String value){Object t=stat("net.minecraft.class_2561","method_43470",value);Object c=stat("net.hautecapitale.dialogue.client.capture.MessageCapture","classer",t,DialogueClientState.pnjNom(),true,List.of());if(c!=null)call(DialogueClientState.class,"onMessageCapture",c);}
 static void frame(){Object t=stat("net.minecraft.class_2561","method_43470","");Object style=call(en("net.minecraft.class_2583","field_24360"),"method_10975","indivis:pq02:v1:a01:leovic:0:0:0");call(t,"method_10862",style);Pq02Client.message(t,false);publish("[Léovic] : Vous devez être le passager du capitaine Brumeforge. Je me trompe ?");
  Object choice=stat("net.minecraft.class_2561","method_43470","[Oui.]");Object click=make("net.minecraft.class_2558$class_10609","/trigger Pq02Choix set 1000000001");Object st=call(en("net.minecraft.class_2583","field_24360"),"method_10958",click);call(choice,"method_10862",st);Object c=stat("net.hautecapitale.dialogue.client.capture.MessageCapture","classer",choice,DialogueClientState.pnjNom(),true,List.of());call(DialogueClientState.class,"onMessageCapture",c);
 }
 static CaptureDialogueScreen prepare()throws Exception{
  Pq02FocusClient.clear();
  if(DialogueClientState.captureActive())invoke("onServeurFerme",DialogueNetwork.SessionClose.class,new DialogueNetwork.SessionClose(DialogueClientState.sessionId(),CloseReason.REMPLACEE));
  CLIENT.method_1507(null);Pq02ScreenNetwork.SENT.clear();class_634.SENT.clear();
  DialogueClientSettings.get().jamais_changer_perspective=true;DialogueClientSettings.get().ne_pas_orienter_joueur=true;
  int publicSession=++session;int publicEntity=++entity;CLIENT.field_1687.actors.put(publicEntity,new class_638.Entity(actor(publicEntity)));
  invoke("ouvrir",DialogueNetwork.SessionOpen.class,new DialogueNetwork.SessionOpen(publicSession,publicEntity,actor(publicEntity),"Leovic",DialogueMode.CAPTURE,CameraProfile.DEFAUT,142));tick();
  invoke("onServeurFerme",DialogueNetwork.SessionClose.class,new DialogueNetwork.SessionClose(publicSession,CloseReason.REMPLACEE));
  int privateSession=++session;int privateEntity=++entity;CLIENT.field_1687.actors.put(privateEntity,new class_638.Entity(actor(privateEntity)));
  invoke("ouvrir",DialogueNetwork.SessionOpen.class,new DialogueNetwork.SessionOpen(privateSession,privateEntity,actor(privateEntity),"Léovic",DialogueMode.CAPTURE,CameraProfile.DEFAUT,142));
  Pq02FocusClient.receive(Pq02Focus.encode(new Pq02Focus.Request(privateSession,actor(privateEntity),++token)));tick();tick();
  check(Pq02FocusClient.sent,"la session privée est confirmée avant le retrait simulé");
  frame();tick();CaptureDialogueScreen screen=(CaptureDialogueScreen)CLIENT.field_1755;screen.versionVue=DialogueClientState.captureVersion();
  check(DialogueClientState.repliquesCapturees().size()==1&&DialogueClientState.choixCaptures().size()==1,"première réplique et réponse déjà présentes dans b10");return screen;
 }
 static void runAll(boolean legacy)throws Exception{
  CaptureDialogueScreen screen=prepare();int id=DialogueClientState.sessionId(),version=DialogueClientState.captureVersion();
  if(legacy){CLIENT.method_1507(null);check(CLIENT.field_1755==null,"HF4 laisse retirer une capture déjà confirmée et remplie");for(int i=0;i<18;i++)tick();
   check(Pq02ScreenNetwork.SENT.stream().anyMatch(p->p instanceof DialogueNetwork.SessionEnd e&&e.sessionId()==id&&e.reason()==CloseReason.ECRAN_FERME),"HF4 reproduit ECRAN_FERME après le retrait de l’écran rempli");
   prepare();CLIENT.method_1507(new class_437());CLIENT.method_1507(null);for(int i=0;i<18;i++)tick();check(Pq02ScreenNetwork.SENT.stream().anyMatch(p->p instanceof DialogueNetwork.SessionEnd e&&e.reason()==CloseReason.ECRAN_FERME),"HF4 manque aussi un menu étranger ouvert et fermé entre deux ticks");Pq02FocusClient.clear();return;}
  check(java.util.Arrays.stream(CLIENT.getClass().getDeclaredMethods()).anyMatch(m->m.getName().contains("pq02$keepOwnedCapture")),"le mixin de production est réellement appliqué à setScreen par Knot");
  for(int i=0;i<35;i++){CLIENT.method_1507(null);tick();check(CLIENT.field_1755==screen,"retrait extérieur "+i+" bloqué avant removed");}
  check(DialogueClientState.captureActive()&&DialogueClientState.sessionId()==id&&DialogueClientState.captureVersion()==version,"session et version de page intactes après les retraits répétés");
  check(DialogueClientState.repliquesCapturees().size()==1&&DialogueClientState.choixCaptures().size()==1,"aucune réplique ni réponse perdue");
  check(Pq02ScreenNetwork.SENT.isEmpty(),"aucune fausse fin ECRAN_FERME envoyée");
  screen.method_25419();tick();check(CLIENT.field_1755==null&&!DialogueClientState.captureActive(),"Échap volontaire ferme réellement la capture");
  check(Pq02ScreenNetwork.SENT.stream().anyMatch(p->p instanceof DialogueNetwork.SessionEnd e&&e.reason()==CloseReason.ESC),"Échap reste transmis au serveur");
  prepare();invoke("onServeurFerme",DialogueNetwork.SessionClose.class,new DialogueNetwork.SessionClose(session,CloseReason.DISTANCE));check(CLIENT.field_1755==null&&!DialogueClientState.captureActive(),"fin serveur conservée sans réouverture");
  prepare();class_437 foreign=new class_437();CLIENT.method_1507(foreign);tick();check(CLIENT.field_1755==foreign,"un inventaire ou menu étranger peut remplacer la capture");CLIENT.method_1507(null);check(CLIENT.field_1755==null,"fermeture du menu étranger autorisée");tick();check(CLIENT.field_1755 instanceof CaptureDialogueScreen,"retour natif b10 après le menu étranger conservé");
  prepare();CLIENT.method_1507(new class_437());CLIENT.method_1507(null);tick();check(CLIENT.field_1755 instanceof CaptureDialogueScreen&&DialogueClientState.captureActive(),"menu étranger furtif entre deux ticks : retour natif conservé");
  screen=prepare();screen.fermetureNotifiee=true;CLIENT.method_1507(null);check(CLIENT.field_1755==null,"capture marquée volontairement fermée libérée");
  prepare();int next=++session;CLIENT.field_1687.actors.put(++entity,new class_638.Entity(actor(entity)));invoke("ouvrir",DialogueNetwork.SessionOpen.class,new DialogueNetwork.SessionOpen(next,entity,actor(entity),"Autre personnage",DialogueMode.CAPTURE,CameraProfile.DEFAUT,142));CLIENT.method_1507(null);check(CLIENT.field_1755==null,"une autre session n’hérite pas de la protection PQ02");
  prepare();Pq02FocusClient.clear();CLIENT.method_1507(null);check(CLIENT.field_1755==null,"nettoyage de connexion libère la fenêtre");
  Pq02FocusClient.clear();
 }
}
