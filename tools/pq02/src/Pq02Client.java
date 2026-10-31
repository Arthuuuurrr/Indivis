package net.hautecapitale.quetes.sequence;

import java.lang.reflect.*;
import java.util.*;
import net.fabricmc.api.ClientModInitializer;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.call;

/** Adaptateur client limité aux pages PQ02, sans modifier le mod Dialogue. */
public final class Pq02Client implements ClientModInitializer {
  private static final String STATE="net.hautecapitale.dialogue.client.DialogueClientState";
  private static final Map<String,String> NAMES=Map.of("leovic","Léovic","magistrat","Magistrat");
  private static final Map<String,Field> FIELDS=new HashMap<>();
  private static Pq02Pacing.Frame frame;
  private static Object frameSession;
  private static int age,finalPageTicks,retryAt,attempts,pageTicks;
  private static boolean advanceRequested;
  private static Object voices;private static boolean voicesChecked;
  public void onInitializeClient(){
    Class<?> contract=type("net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents$AllowGame");
    Object event=en("net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents","ALLOW_GAME");
    Object phase=stat("net.minecraft.class_2960","method_60654","indivis_pq02:frame");
    call(event,"addPhaseOrdering",phase,en("net.fabricmc.fabric.api.event.Event","DEFAULT_PHASE"));
    Object listener=Proxy.newProxyInstance(contract.getClassLoader(),new Class<?>[]{contract},(p,m,a)->{
      if(m.getDeclaringClass()==Object.class)return objectMethod(p,m,a);
      try{return message(a[0],Boolean.TRUE.equals(a[1]));}catch(RuntimeException e){report(e);return true;}
    });call(event,"register",phase,listener);
    event("net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents$Disconnect","DISCONNECT",a->{Pq02FocusClient.clear();frame=null;advanceRequested=false;return null;});
    event("net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents$StartTick","START_CLIENT_TICK",a->{Pq02FocusClient.keepAlive();return null;});
    event("net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents$EndTick","END_CLIENT_TICK",a->{tickClient(a[0]);return null;});
    event("net.fabricmc.fabric.api.client.screen.v1.ScreenEvents$AfterInit","AFTER_INIT",a->{installInput(a[1]);return null;});
  }
  static boolean captureScreen(Object screen){return screen!=null&&screen.getClass().getName().equals("net.hautecapitale.dialogue.client.capture.CaptureDialogueScreen");}
  /** Appelé avant setScreen(null), avant tout retrait et avant le moniteur b10. */
  public static boolean blockScreenReset(Object client,Object nextScreen){
    if(nextScreen!=null)return false;
    Object screen=field(client,"field_1755");
    Pq02Focus.Request expected=Pq02FocusClient.pending;
    if(expected==null||!Boolean.TRUE.equals(stat(STATE,"captureActive"))||Boolean.TRUE.equals(Pq02FocusClient.state("finEnvoyee")))return false;
    if(!Objects.equals(Pq02FocusClient.state("sessionId"),expected.session())||!Objects.equals(Pq02FocusClient.state("pnjUuid"),expected.actor()))return false;
    if(!captureScreen(screen)){
      // Le menu peut avoir été ouvert et fermé entre deux ticks b10.
      // Mémoriser cette visite pour conserver son retour natif, sans le bloquer.
      if(screen!=null)state("ecranEtrangerVu",true);
      return false;
    }
    return !Boolean.TRUE.equals(field(screen,"fermetureNotifiee"));
  }
  static void listen(Object event,String contract,EventHandler handler){
    Class<?> c=type(contract);Object listener=Proxy.newProxyInstance(c.getClassLoader(),new Class<?>[]{c},(p,m,a)->{if(m.getDeclaringClass()==Object.class)return objectMethod(p,m,a);try{return handler.run(a);}catch(RuntimeException e){report(e);return m.getReturnType()==boolean.class?true:null;}});call(event,"register",listener);
  }
  static void installInput(Object screen){
    if(!captureScreen(screen))return;
    listen(stat("net.fabricmc.fabric.api.client.screen.v1.ScreenEvents","remove",screen),"net.fabricmc.fabric.api.client.screen.v1.ScreenEvents$Remove",a->{Pq02FocusClient.removed(a[0]);return null;});
    listen(stat("net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents","allowKeyPress",screen),"net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents$AllowKeyPress",a->!key(a[0],a[1]));
    listen(stat("net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents","allowMouseClick",screen),"net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents$AllowMouseClick",a->!mouse(a[0],a[1]));
    listen(stat("net.fabricmc.fabric.api.client.screen.v1.ScreenEvents","afterRender",screen),"net.fabricmc.fabric.api.client.screen.v1.ScreenEvents$AfterRender",a->{hint(a[0],a[1]);return null;});
  }
  static Object field(Object owner,String name){
    try{
      Class<?> type=owner instanceof Class<?> c?c:owner.getClass();String key=type.getName()+"#"+name;
      Field f=FIELDS.get(key);if(f==null){for(Class<?> c=type;c!=null;c=c.getSuperclass())try{f=c.getDeclaredField(name);break;}catch(NoSuchFieldException ignored){}if(f==null)throw new NoSuchFieldException(key);f.setAccessible(true);FIELDS.put(key,f);}return f.get(owner instanceof Class<?>?null:owner);
    }catch(ReflectiveOperationException e){throw new IllegalStateException("Dialogue b10 : "+name,e);}
  }
  static void state(String name,Object value){
    Class<?> c=type(STATE);field(c,name);try{FIELDS.get(c.getName()+"#"+name).set(null,value);}catch(ReflectiveOperationException e){throw new IllegalStateException(name,e);}
  }
  static boolean message(Object text,boolean overlay){
    if(overlay)return true;
    String insertion=(String)call(call(text,"method_10866"),"method_10955");
    if(insertion!=null&&insertion.startsWith(Pq02Focus.PREFIX)){Pq02FocusClient.receive(insertion);return false;}
    if(insertion==null||!insertion.startsWith("indivis:pq02:v1:"))return true;
    Pq02Pacing.Frame parsed=Pq02Pacing.parse(insertion);
    if(parsed==null||!Boolean.TRUE.equals(stat(STATE,"captureActive")))return false;
    frame=parsed;frameSession=field(type(STATE),"sessionId");age=0;finalPageTicks=0;pageTicks=0;retryAt=0;attempts=0;advanceRequested=false;
    Pq02FocusClient.page(frameSession);
    System.out.println("[PQ02 page] reçue : session="+frameSession+", nœud="+frame.key()+", locuteur="+frame.role());
    stat(STATE,"onChoixEnvoye",false,""); // remise de page sans réponse fictive dans l'historique.
    state("pnjNom",NAMES.get(frame.role()));
    return false; // trame vide, jamais un texte de PNJ ni une ligne de chat.
  }
  static void tickClient(Object client){
    Pq02FocusClient.tick(client);
    if(frame==null)return;
    if(!currentFrame()){frame=null;advanceRequested=false;return;}
    Object screen=field(client,"field_1755");
    if(!captureScreen(screen))return;
    Object panel=field(screen,"panneau");if(panel==null)return;
    call(panel,"nom",NAMES.get(frame.role()));call(panel,"attente",false);
    // Une marche ou une longue voix n'est pas un silence qui ferme la session.
    state("fenetreJusquaMs",System.currentTimeMillis()+2000L);
    // Attendre le vrai rafraîchissement de la nouvelle capture, jamais l'état de l'ancienne page.
    if(!Objects.equals(field(screen,"versionVue"),stat(STATE,"captureVersion")))return;
    boolean typewriter=Boolean.TRUE.equals(field(panel,"typewriter"));
    boolean last=Boolean.TRUE.equals(call(panel,"dernierePage")),typing=Boolean.TRUE.equals(call(panel,"typewriterActif"));
    boolean speaking=voicePlaying();age++;if(last&&!typing)finalPageTicks++;else finalPageTicks=0;
    // Aucune page ni aucun bloc n'est effacé par un minuteur de lecture.
    if(advanceRequested&&attempts<60&&age>=retryAt&&Pq02Pacing.ready(frame,age,finalPageTicks,last,typing,speaking,typewriter)){
      Object network=call(client,"method_1562");if(network==null)return;
      attempts++;retryAt=age+20;call(network,"method_45730","trigger Pq02Choix set "+frame.token());
    }
  }
  static boolean currentFrame(){return frame!=null&&Boolean.TRUE.equals(stat(STATE,"captureActive"))&&Objects.equals(frameSession,field(type(STATE),"sessionId"));}
  public static boolean requestAdvance(Object screen){
    if(!currentFrame()||frame.token()==0||!captureScreen(screen))return false;
    Object panel=field(screen,"panneau");
    if(panel==null||!Objects.equals(field(screen,"versionVue"),stat(STATE,"captureVersion"))||Boolean.TRUE.equals(call(panel,"historiqueVisible"))||Boolean.TRUE.equals(call(panel,"typewriterActif"))||!Boolean.TRUE.equals(call(panel,"dernierePage")))return false;
    advanceRequested=true;return true;
  }
  public static boolean key(Object screen,Object input){
    int code=((Number)call(input,"method_74228")).intValue();
    if(code!=32&&code!=257&&code!=335||Boolean.TRUE.equals(call(input,"method_74231")))return false;
    return requestAdvance(screen);
  }
  public static boolean mouse(Object screen,Object input){
    if(!captureScreen(screen)||((Number)call(input,"method_74245")).intValue()!=0)return false;
    Object panel=field(screen,"panneau");if(panel==null||((Number)call(panel,"choixSous",call(input,"comp_4798"),call(input,"comp_4799"))).intValue()>=0)return false;
    return requestAdvance(screen);
  }
  static void hint(Object screen,Object draw){
    if(!currentFrame()||frame.token()==0)return;
    Object panel=field(screen,"panneau");if(panel==null||Boolean.TRUE.equals(call(panel,"historiqueVisible")))return;
    int y=((Number)field(screen,"field_22790")).intValue()-36;
    Object font=field(panel,"textRenderer");String text="Espace ou clic : suite";
    int width=((Number)call(font,"method_1727",text)).intValue();
    call(draw,"method_25294",6,y-2,12+width,y+11,0x90000000);
    call(draw,"method_51433",font,text,9,y,0xFFE8D7AB,false);
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

