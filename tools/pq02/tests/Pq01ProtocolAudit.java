package net.hautecapitale.quetes.sequence;
import java.util.*;
import java.nio.file.*;
import com.google.gson.*;
import net.minecraft.*;
import net.hautecapitale.dialogue.client.*;
import net.hautecapitale.dialogue.client.capture.*;
import net.hautecapitale.dialogue.network.DialogueNetwork;
import net.hautecapitale.dialogue.session.*;

/** Ressources PQ01 livrées, vrais événements Fabric/b10 ; surfaces graphiques doublées. */
public final class Pq01ProtocolAudit {
 static int checks,session=1000;
 static final Map<String,String> NAMES=Map.of("geraud","Géraud Rivet","altheon","Althéon Brumeforge","gautier","Gautier","hugues","Hugues","anselme","Anselme","perrin","Perrin");
 static void check(boolean ok,String label){if(!ok)throw new AssertionError(label+" session="+DialogueClientState.sessionId()+" lines="+DialogueClientState.repliquesCapturees()+" choices="+DialogueClientState.choixCaptures());checks++;System.out.println("PQ01 PROTOCOL "+label);}
 static void tick()throws Exception {
  var client=class_310.method_1551();
  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.START_CLIENT_TICK.invoker().onStartTick(client);
  Pq01Client.state("changementMs",0L);
  Pq02ProtocolBench.invoke("tick",class_310.class,client);
  Pq01Client.tickClient(client);Pq02Client.tickClient(client);
 }
 static class_2561 choice(JsonObject o){
  String label=o.get("text").getAsString();
  return class_2561.method_43470(label).method_10862(class_2583.field_24360.method_10958(new class_2558.class_10609(o.getAsJsonObject("click_event").get("command").getAsString())));
 }
 public static void main(String[] args)throws Exception {
  DialogueClientSettings.get().jamais_changer_perspective=true;DialogueClientSettings.get().ne_pas_orienter_joueur=true;
  MessageCapture.init();new Pq01Client().onInitializeClient();new Pq02Client().onInitializeClient();
  JsonArray nodes=JsonParser.parseString(Files.readString(Path.of(args[0]))).getAsJsonArray();
  int rendered=0,manual=0;
  for(JsonElement item:nodes){
   JsonObject node=item.getAsJsonObject();String key=node.get("key").getAsString(),name=NAMES.get(node.get("role").getAsString());
   if(rendered>0)Pq02ProtocolBench.close(session);
   session++;Pq02ProtocolBench.load(session);
   Pq02ProtocolBench.invoke("ouvrir",DialogueNetwork.SessionOpen.class,new DialogueNetwork.SessionOpen(session,session,Pq02ProtocolBench.uuid(session),name,DialogueMode.CAPTURE,CameraProfile.DEFAUT,142));
   tick();Pq02ProtocolBench.control(node.get("frame").getAsString());
   for(JsonElement line:node.getAsJsonArray("lines"))Pq02ProtocolBench.publish(class_2561.method_43470(line.getAsString()));
   for(JsonElement ch:node.getAsJsonArray("choices"))Pq02ProtocolBench.publish(choice(ch.getAsJsonObject()));
   CaptureDialogueScreen screen=(CaptureDialogueScreen)class_310.method_1551().field_1755;screen.versionVue=DialogueClientState.captureVersion();
   int before=class_634.SENT.size();for(int i=0;i<8;i++)tick();
   check(DialogueClientState.captureActive(),key+" capture survives native monitor");
   check(DialogueClientState.pnjNom().equals(name)&&screen.panneau.name.equals(name),key+" exact speaker in native state and panel");
   check(DialogueClientState.repliquesCapturees().size()==node.getAsJsonArray("lines").size(),key+" every supplied dialogue line captured");
   check(DialogueClientState.choixCaptures().size()==node.getAsJsonArray("choices").size(),key+" all choices captured without control-frame leakage");
   check(class_634.SENT.size()==before,key+" no timer-driven unread-page advance");
   if(node.get("frame").getAsString().contains(":1000000001:")){
    screen.panneau.typing=true;check(!Pq01Client.requestAdvance(screen),key+" cannot skip while typewriter active");screen.panneau.typing=false;
    screen.panneau.last=false;check(!Pq01Client.requestAdvance(screen),key+" cannot skip intermediate page");screen.panneau.last=true;
    check(Pq01Client.requestAdvance(screen),key+" explicit final-page advance accepted");tick();
    check(class_634.SENT.size()==before+1&&class_634.SENT.get(before).equals("trigger QuestChoix set 1000000001"),key+" sends only current continuation token");manual++;
   }
   rendered++;
  }
  check(rendered==94,"94 rendered blocks; c0 is an immediate c1 alias");
  int before=class_634.SENT.size();((CaptureDialogueScreen)class_310.method_1551().field_1755).method_25419();for(int i=0;i<4;i++)tick();
  check(class_310.method_1551().field_1755==null&&class_634.SENT.size()==before,"Escape never reopens or advances a closed PQ01 capture");
  System.out.println("PASS "+checks+" PQ01 real client protocol checks; blocks="+rendered+", manual="+manual+"; actual Fabric message events/native b10, graphical surfaces/world/C2S doubled");
 }
}
