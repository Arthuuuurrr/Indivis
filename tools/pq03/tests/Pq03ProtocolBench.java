package net.hautecapitale.quetes.sequence;
import java.util.*;
import java.lang.reflect.*;
import net.minecraft.*;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.hautecapitale.dialogue.client.DialogueClientState;
import net.hautecapitale.dialogue.client.DialogueClientSettings;
import net.hautecapitale.dialogue.client.capture.MessageCapture;
import net.hautecapitale.dialogue.network.DialogueNetwork;
import net.hautecapitale.dialogue.session.*;
/** Actual Fabric events and native b10 state; graphical surfaces and C2S recorder doubled. */
public final class Pq03ProtocolBench {
 static int checks,session=1,actor=10,token=100001;
 public static final class Draw {public final List<String> text=new ArrayList<>();public int color,rectangles;public void method_25294(int x1,int y1,int x2,int y2,int color){rectangles++;}public void method_51433(Object font,String value,int x,int y,int color,boolean shadow){text.add(value);this.color=color;}}
 static void feedback(){
  int lines=DialogueClientState.repliquesCapturees().size(),choices=DialogueClientState.choixCaptures().size();
  var n=new QuestReputationNotice.Notice("coeur",1,12);control(QuestReputationNotice.encode(n));check(n.equals(QuestReputationNoticeClient.notice),"real message event receives immediate positive reputation metadata");
  check(DialogueClientState.repliquesCapturees().size()==lines&&DialogueClientState.choixCaptures().size()==choices,"reputation notice is separate from NPC text and choices");Draw draw=new Draw();QuestReputationNoticeClient.draw(class_310.method_1551().field_1755,draw);check(draw.text.equals(List.of("Garde du Cœur : +1 en réputation"))&&draw.color==0xFF80E59A&&draw.rectangles==1,"dedicated overlay emits positive reputation text with green color");
  n=new QuestReputationNotice.Notice("port",-1,-7);control(QuestReputationNotice.encode(n));draw=new Draw();QuestReputationNoticeClient.draw(class_310.method_1551().field_1755,draw);check(draw.text.equals(List.of("Garde du Port : -1 en réputation"))&&draw.color==0xFFFF8585,"same overlay emits negative Port reputation with red color");
  QuestReputationNoticeClient.until=0;QuestReputationNoticeClient.tick();check(QuestReputationNoticeClient.notice==null,"expired reputation overlay clears without modifying dialogue");
 }

 static void invoke(String name,Class<?> parameter,Object value)throws Exception{Method m=DialogueClientState.class.getDeclaredMethod(name,parameter);m.setAccessible(true);try{m.invoke(null,value);}catch(InvocationTargetException e){throw new RuntimeException(e.getCause());}}
 static void check(boolean value,String message){if(!value)throw new AssertionError(message+"; session="+DialogueClientState.sessionId()+", active="+DialogueClientState.captureActive()+", screen="+class_310.method_1551().field_1755+", lines="+DialogueClientState.repliquesCapturees().size()+", choices="+DialogueClientState.choixCaptures().size());checks++;System.out.println("PROTOCOL CHECK "+message);}
 static UUID uuid(int entity){return UUID.nameUUIDFromBytes(("actor"+entity).getBytes());}
 static void load(int entity){class_310.method_1551().field_1687.actors.put(entity,new class_638.Entity(uuid(entity)));}
 static void open(int id,int entity)throws Exception{invoke("ouvrir",DialogueNetwork.SessionOpen.class,new DialogueNetwork.SessionOpen(id,entity,uuid(entity),"Aurèle",DialogueMode.CAPTURE,CameraProfile.DEFAUT,142));}
 static void close(int id)throws Exception{invoke("onServeurFerme",DialogueNetwork.SessionClose.class,new DialogueNetwork.SessionClose(id,CloseReason.REMPLACEE));}
 static void publish(class_2561 text){ClientReceiveMessageEvents.ALLOW_GAME.invoker().allowReceiveGameMessage(text,false);}
 static void control(String insertion){publish(class_2561.method_43470("").method_10862(class_2583.field_24360.method_10975(insertion)));}
 static void probe(){control(Pq03Focus.encode(new Pq03Focus.Request(session,uuid(actor),token)));}
 static void tick()throws Exception{net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick(class_310.method_1551());Pq03Client.state("changementMs",0L);invoke("tick",class_310.class,class_310.method_1551());Pq03Client.tickClient(class_310.method_1551());}
 static void page(Pq03Data.Node node){
  control("indivis:pq03:v1:"+node.key+":"+node.speaker+":"+(node.next.isEmpty()?0:1000000014)+":0:0");
  String name=node.speaker.equals("magistrat")?"Magistrat":"Aurèle";
  for(String text:node.lines.isEmpty()?List.of("Parlons encore du Port."):node.lines)publish(class_2561.method_43470("["+name+"] : "+text));
  int pick=1;for(Pq03Data.Choice c:node.choices)publish(class_2561.method_43470("["+c.text+"]").method_10862(class_2583.field_24360.method_10958(new class_2558.class_10609("/trigger Pq03Choix set "+(1000000000+pick++)))));
  String action=Pq03Transitions.label(node.next);boolean terminal=!action.isEmpty();publish(class_2561.method_43470("["+(terminal?action:"Fermer la conversation")+"]").method_10862(class_2583.field_24360.method_36139(terminal?0x55ff55:0xaaaaaa).method_10958(new class_2558.class_10609("/trigger Pq03Choix set "+(terminal?1000000014:1000000015)))));
  ((net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen)class_310.method_1551().field_1755).versionVue=DialogueClientState.captureVersion();
 }
 static void legacy()throws Exception{
  load(10);open(1,10);tick();close(1);
  control("indivis:pq03:v1:a01:aurele:0:0:0");publish(class_2561.method_43470("[Aurèle] : Vous devez être le passager du capitaine Brumeforge. Je me trompe ?"));
  open(2,11);tick();check(DialogueClientState.repliquesCapturees().isEmpty()&&DialogueClientState.choixCaptures().isEmpty(),"HF3 loses a page delivered before the new native session opens");
  Pq03Client.state("fenetreJusquaMs",0L);tick();check(!DialogueClientState.captureActive(),"native b10 ends the empty capture after expiry");
  System.out.println("PASS "+checks+" HF3 adverse-order checks; fixture does not identify the original GUI-removal caller");
 }
 public static void main(String[] args)throws Exception{
  DialogueClientSettings.get().jamais_changer_perspective=true;DialogueClientSettings.get().ne_pas_orienter_joueur=true;
  MessageCapture.init();new Pq01Client().onInitializeClient();new Pq02Client().onInitializeClient();new Pq03Client().onInitializeClient();
  if(Boolean.getBoolean("pq03.protocol.legacy")){legacy();return;}
  load(actor);open(session,actor);tick();int oldSession=session;session++;actor++;token++;int acks=class_634.SENT.size();
  probe();for(int i=0;i<8;i++)tick();check(class_634.SENT.size()==acks,"early probe retained without premature acknowledgement");
  close(oldSession);open(session,actor);for(int i=0;i<4;i++)tick();check(class_634.SENT.size()==acks,"missing client actor delays acknowledgement");
  Pq03Client.state("fenetreJusquaMs",0L);tick();check(DialogueClientState.captureActive(),"pending capture survives expired timer before actor loading");
  load(actor);tick();check(class_634.SENT.size()==acks,"screen and actor must remain stable");tick();
  check(class_634.SENT.size()==acks+1&&class_634.SENT.get(acks).equals("indivispq03 ready "+token),"ready command identifies the expected native session and actor");
  close(oldSession);page(Pq03Data.get("a01"));tick();check(DialogueClientState.repliquesCapturees().size()==1&&DialogueClientState.choixCaptures().size()==4,"complete first page enters native capture through real Fabric events");
  for(int i=0;i<30;i++)tick();check(DialogueClientState.captureActive(),"first page survives native screen monitoring");probe();tick();check(class_634.SENT.size()==acks+1,"duplicate probe does not replay readiness or dialogue");feedback();
  int nodes=0;
  for(Pq03Data.Node node:Pq03Data.NODES.values()){
   close(session);session++;actor++;token++;acks=class_634.SENT.size();if(nodes%2==0)probe();open(session,actor);if(nodes%2!=0)probe();
   for(int i=0;i<nodes%4;i++)tick();check(class_634.SENT.size()==acks,"node "+node.key+" waits for client actor");
   load(actor);tick();tick();check(class_634.SENT.size()==acks+1,"node "+node.key+" acknowledges correct focus");page(node);tick();
   check(DialogueClientState.repliquesCapturees().size()==Math.max(1,node.lines.size()),"node "+node.key+" receives entire text block");
   check(DialogueClientState.choixCaptures().size()==node.choices.size()+1,"node "+node.key+" receives choices and its explicit action or close");
   check(DialogueClientState.pnjNom().equals(node.speaker.equals("magistrat")?"Magistrat":"Aurèle"),"node "+node.key+" retains speaker");nodes++;
  }
  check(nodes==Pq03Data.NODES.size(),"all PQ03 nodes pass native session and actual Fabric message pipeline");
  control(QuestReputationNotice.encode(new QuestReputationNotice.Notice("coeur",1,2)));int count=class_634.SENT.size();((net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen)class_310.method_1551().field_1755).method_25419();for(int i=0;i<3;i++)tick();check(class_310.method_1551().field_1755==null&&class_634.SENT.size()==count,"Escape does not reopen or acknowledge closed session");
  QuestReputationNoticeClient.tick();check(QuestReputationNoticeClient.notice==null,"closing conversation clears transient reputation information before another character");Pq03FocusClient.clear();check(Pq03FocusClient.pending==null,"disconnect cleanup releases pending state");
  System.out.println("PASS "+checks+" real client protocol checks; native b10 state and actual Fabric events, graphical client/world/screen and C2S recorder doubled");
 }
}
