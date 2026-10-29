package net.hautecapitale.quetes.sequence;

import java.util.*;
import net.hautecapitale.dialogue.client.DialogueClientState;
import net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen;
import net.hautecapitale.dialogue.network.DialogueNetwork;
import net.hautecapitale.dialogue.session.CloseReason;

import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.call;

/** État/capture b10 réels ; écran, réseau C2S et caméra sans rendu doublés. */
public final class Pq02ScreenBench {
 static final String STATE="net.hautecapitale.dialogue.client.DialogueClientState";
 static final net.minecraft.class_310 CLIENT=net.minecraft.class_310.method_1551();
 static int id=300;
 static void set(String key,Object value){Pq02Client.state(key,value);}
 static void check(boolean value,String label){Pq02WorldBench.ok(value,"screen HF3: "+label);}
 static CaptureDialogueScreen prepare(){
  Pq02ScreenNetwork.SENT.clear();CLIENT.field_1755=null;
  set("etat",en(STATE+"$Etat","DIALOGUE_ACTIVE"));set("mode",en("net.hautecapitale.dialogue.session.DialogueMode","CAPTURE"));
  set("sessionId",++id);set("pnjNom","Léovic");set("finEnvoyee",false);set("flags",2);set("perspectiveForcee",false);
  set("ecranVu",false);set("ecranEtrangerVu",false);set("attenteRetourTicks",0);set("ecranCourant",null);
  CaptureDialogueScreen screen=new CaptureDialogueScreen();CLIENT.method_1507(screen);
  Object text=stat("net.minecraft.class_2561","method_43470","");
  Object style=call(en("net.minecraft.class_2583","field_24360"),"method_10975","indivis:pq02:v1:a01:leovic:0:0:0");
  call(text,"method_10862",style);check(!Pq02Client.message(text,false),"private frame consumed without chat text");
  screen.versionVue=DialogueClientState.captureVersion();return screen;
 }
 static void invoke(String name,Class<?> parameter,Object argument){try{var method=type(STATE).getDeclaredMethod(name,parameter);method.setAccessible(true);method.invoke(null,argument);}catch(java.lang.reflect.InvocationTargetException e){throw new RuntimeException(e.getCause());}catch(ReflectiveOperationException e){throw new RuntimeException(e);}}
 static void monitor(){invoke("surveillerEcran",net.minecraft.class_310.class,CLIENT);}
 static void remove(){CLIENT.method_1507(null);}
 static void end(){set("etat",en(STATE+"$Etat","IDLE"));set("sessionId",-1);CLIENT.field_1755=null;Pq02Client.tickClient(CLIENT);}
 static void reproduce(){
  prepare();remove();Pq02Client.tickClient(CLIENT);
  check(CLIENT.field_1755==null,"HF2 leaves an involuntarily removed capture absent");
  for(int i=0;i<17;i++)monitor();
  check(Pq02ScreenNetwork.SENT.stream().anyMatch(p->p instanceof DialogueNetwork.SessionEnd e&&e.reason()==CloseReason.ECRAN_FERME),"HF2 native b10 monitor sends ECRAN_FERME after absent screen");end();
 }
 static void verify(){
  CaptureDialogueScreen screen=prepare();int session=DialogueClientState.sessionId();int version=DialogueClientState.captureVersion();
  remove();Pq02Client.tickClient(CLIENT);
  check(CLIENT.field_1755==screen,"involuntary removal restores the same private capture screen");
  check(DialogueClientState.sessionId()==session&&DialogueClientState.captureVersion()==version,"recovery preserves session and page version");
  for(int i=0;i<25;i++){monitor();Pq02Client.tickClient(CLIENT);}
  check(Pq02ScreenNetwork.SENT.isEmpty()&&DialogueClientState.captureActive(),"native b10 monitor keeps the recovered conversation open");
  remove();Pq02Client.tickClient(CLIENT);check(CLIENT.field_1755==null,"at most one recovery per server frame");end();

  screen=prepare();screen.method_25419();Pq02Client.tickClient(CLIENT);
  check(CLIENT.field_1755==null,"explicit Escape is never reopened");
  check(Pq02ScreenNetwork.SENT.stream().anyMatch(p->p instanceof DialogueNetwork.SessionEnd e&&e.reason()==CloseReason.ESC),"explicit Escape reaches native b10 end handling");end();

  screen=prepare();invoke("onServeurFerme",DialogueNetwork.SessionClose.class,new DialogueNetwork.SessionClose(id,CloseReason.ECRAN_FERME));Pq02Client.tickClient(CLIENT);
  check(CLIENT.field_1755==null&&!DialogueClientState.captureActive(),"server termination is never reopened");end();

  screen=prepare();net.minecraft.class_437 other=new net.minecraft.class_437();CLIENT.method_1507(other);monitor();Pq02Client.tickClient(CLIENT);
  check(CLIENT.field_1755==other,"another menu retains focus");remove();Pq02Client.tickClient(CLIENT);
  check(CLIENT.field_1755==null,"foreign menu return remains governed by native flags");end();

  screen=prepare();remove();set("sessionId",id+1);Pq02Client.tickClient(CLIENT);
  check(CLIENT.field_1755==null,"stale session frame cannot reopen a screen");end();

  screen=prepare();screen.fermetureNotifiee=true;remove();Pq02Client.tickClient(CLIENT);
  check(CLIENT.field_1755==null,"screen marked deliberately closed cannot recover");end();
 }
}
