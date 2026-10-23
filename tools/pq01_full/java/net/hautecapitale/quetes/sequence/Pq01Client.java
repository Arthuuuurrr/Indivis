package net.hautecapitale.quetes.sequence;

import java.lang.reflect.*;
import java.util.*;
import net.fabricmc.api.ClientModInitializer;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.call;

/** Adaptateur client limité aux pages PQ01, sans modifier le mod Dialogue. */
public final class Pq01Client implements ClientModInitializer {
  private static final String STATE="net.hautecapitale.dialogue.client.DialogueClientState";
  private static final Map<String,String> NAMES=Map.of("geraud","Géraud Rivet","altheon","Althéon Brumeforge","gautier","Gautier","hugues","Hugues","anselme","Anselme","perrin","Perrin");
  private static final Map<String,Field> FIELDS=new HashMap<>();
  private static Pq01Pacing.Frame frame;
  private static int age,finalPageTicks,retryAt,attempts,pageTicks;
  private static Object voices;private static boolean voicesChecked;
  public void onInitializeClient(){
    Class<?> contract=type("net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents$AllowGame");
    Object event=en("net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents","ALLOW_GAME");
    Object phase=stat("net.minecraft.class_2960","method_60654","haute_capitale_quetes:pq01_frame");
    call(event,"addPhaseOrdering",phase,en("net.fabricmc.fabric.api.event.Event","DEFAULT_PHASE"));
    Object listener=Proxy.newProxyInstance(contract.getClassLoader(),new Class<?>[]{contract},(p,m,a)->{
      if(m.getDeclaringClass()==Object.class)return objectMethod(p,m,a);
      try{return message(a[0],Boolean.TRUE.equals(a[1]));}catch(RuntimeException e){report(e);return true;}
    });call(event,"register",phase,listener);
    event("net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents$EndTick","END_CLIENT_TICK",a->{tickClient(a[0]);return null;});
  }
  static Object field(Object owner,String name){
    try{
      Class<?> type=owner instanceof Class<?> c?c:owner.getClass();String key=type.getName()+"#"+name;
      Field f=FIELDS.get(key);if(f==null){f=type.getDeclaredField(name);f.setAccessible(true);FIELDS.put(key,f);}return f.get(owner instanceof Class<?>?null:owner);
    }catch(ReflectiveOperationException e){throw new IllegalStateException("Dialogue b10 : "+name,e);}
  }
  static void state(String name,Object value){
    Class<?> c=type(STATE);field(c,name);try{FIELDS.get(c.getName()+"#"+name).set(null,value);}catch(ReflectiveOperationException e){throw new IllegalStateException(name,e);}
  }
  static boolean message(Object text,boolean overlay){
    if(overlay)return true;
    String insertion=(String)call(call(text,"method_10866"),"method_10955");
    if(insertion==null||!insertion.startsWith("indivis:pq01:v1:"))return true;
    Pq01Pacing.Frame parsed=Pq01Pacing.parse(insertion);
    if(parsed==null||!Boolean.TRUE.equals(stat(STATE,"captureActive")))return false;
    frame=parsed;age=0;finalPageTicks=0;pageTicks=0;retryAt=0;attempts=0;
    stat(STATE,"onChoixEnvoye",false,""); // remise de page sans réponse fictive dans l'historique.
    state("pnjNom",NAMES.get(frame.role()));
    return false; // trame vide, jamais un texte de PNJ ni une ligne de chat.
  }
  static void tickClient(Object client){
    if(frame==null)return;
    if(!Boolean.TRUE.equals(stat(STATE,"captureActive"))){frame=null;return;}
    Object screen=field(client,"field_1755");
    if(screen==null||!screen.getClass().getName().equals("net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen"))return;
    Object panel=field(screen,"panneau");if(panel==null)return;
    call(panel,"nom",NAMES.get(frame.role()));call(panel,"attente",false);
    // Une marche ou une longue voix n'est pas un silence qui ferme la session.
    state("fenetreJusquaMs",System.currentTimeMillis()+2000L);
    boolean last=Boolean.TRUE.equals(call(panel,"dernierePage")),typing=Boolean.TRUE.equals(call(panel,"typewriterActif"));
    boolean speaking=voicePlaying();age++;if(last&&!typing)finalPageTicks++;else finalPageTicks=0;
    if(!last&&!typing&&!speaking){if(++pageTicks>=40){call(panel,"avancer");pageTicks=0;}}else pageTicks=0;
    if(attempts<60&&age>=retryAt&&Pq01Pacing.ready(frame,age,finalPageTicks,last,typing,speaking)){
      Object network=call(client,"method_1562");if(network==null)return;
      attempts++;retryAt=age+20;call(network,"method_45730","trigger QuestChoix set "+frame.token());
    }
  }
  static boolean voicePlaying(){
    try{
      if(!voicesChecked){
        voicesChecked=true;Object loader=stat("net.fabricmc.loader.api.FabricLoader","getInstance");
        if(Boolean.TRUE.equals(call(loader,"isModLoaded","indivis_voices"))){
          for(Object entry:(List<?>)call(loader,"getEntrypoints","client",type("net.fabricmc.api.ClientModInitializer")))if(entry.getClass().getName().equals("net.indivis.voices.client.VoicesClient")){voices=entry;break;}
        }
      }
      return voices!=null&&(field(voices,"active")!=null||field(voices,"pending")!=null||field(voices,"fading")!=null);
    }catch(RuntimeException e){voices=null;report(e);return false;}
  }
}
