package net.hautecapitale.quetes.sequence;
import java.lang.reflect.*;
import java.util.*;
import net.hautecapitale.dialogue.session.DialogueManager;
import static net.hautecapitale.quetes.sequence.Pq01InteractionBench.*;
/** Régressions réelles du pont livré ; monde/horloge/caméra et effets de fonctions doublés. */
public final class Pq01GeraudBench {
  static int checks;
  static void check(boolean ok,String label){checks++;if(!ok)throw new AssertionError(label+" "+events);}
  static Field field(Object target,String name)throws Exception{Class<?> c=target instanceof Class<?> t?t:target.getClass();Field f=c.getDeclaredField(name);f.setAccessible(true);return f;}
  static Object get(Object target,String name)throws Exception{return field(target,name).get(target instanceof Class<?>?null:target);}
  static void set(Object target,String name,Object value)throws Exception{field(target,name).set(target instanceof Class<?>?null:target,value);}
  static void clock(long value)throws Exception{set(Pq01Bridge.class,"clock",value);}
  static void fresh()throws Exception{
    reset();for(String n:List.of("VIEWS","NEAR","GRACE"))((Map<?,?>)get(Pq01Bridge.class,n)).clear();clock(0);
    DialogueManager.closed=Pq01Bridge::captureClosed;
  }
  static void active(){world.server.board.values.put("hcm_pq1_active",1);world.server.board.values.put("CAP_QDIALOG_OWNER",91);world.server.board.values.put("hcm_pq1_role",91);world.server.board.values.put("hch_pq1node",4);}
  static Object view()throws Exception{return Pq01Bridge.view(player);}
  static Object invoke(String name,Object...args)throws Exception{
    Method m=Arrays.stream(Pq01Bridge.class.getDeclaredMethods()).filter(x->x.getName().equals(name)).findFirst().orElseThrow();m.setAccessible(true);
    try{return m.invoke(null,args);}catch(InvocationTargetException e){throw new RuntimeException(e.getCause());}
  }
  static boolean called(String c){return events.contains("command:"+c);}
  static void bind(String key,Entity e){world.entities.put(e.id,e);Object n=GuardNbt.compound();GuardNbt.string(n,"uuid",e.id.toString());GuardNbt.string(n,"dimension","minecraft:overworld");GuardNbt.put(GuardNbt.childOrCreate(world.server.store.nbt,"bindings"),key,n);}
  public static void main(String[] args)throws Exception {
    check(Pq01Bridge.class.getProtectionDomain().getCodeSource().getLocation().toString().endsWith(System.getProperty("indivis.expected.jar","PQ01-R3-HF6.jar")),"pont du JAR livré");
    fresh();npc.x=8;check(Pq01Bridge.nearGeraud(player)==0,"spawn : pas de dialogue immédiat");clock(200);check(Pq01Bridge.nearGeraud(player)==0,"immobilité au spawn ne déclenche rien");player.x=4;check(Pq01Bridge.nearGeraud(player)==0,"quatre blocs restent hors portée");player.x=5;check(Pq01Bridge.nearGeraud(player)==1,"approche à trois blocs après chargement");check(called("function capitale:pq01/open/geraud_self"),"ouverture canonique après approche");events.clear();check(Pq01Bridge.nearGeraud(player)==0&&events.isEmpty(),"aucune relance automatique après fermeture");
    fresh();npc.x=1;Pq01Bridge.nearGeraud(player);clock(200);check(Pq01Bridge.nearGeraud(player)==0,"spawn déjà proche attend aussi un déplacement");player.x=0.5;check(Pq01Bridge.nearGeraud(player)==0,"microdéplacement insuffisant");player.x=0.75;check(Pq01Bridge.nearGeraud(player)==1,"mouvement volontaire suffisant");
    check(Pq01Bridge.execute(player,"rearm_near")==1,"relecture volontaire réarme uniquement le near");check(Pq01Bridge.nearGeraud(player)==0,"relecture réarmée attend de nouveau le délai");clock(400);check(Pq01Bridge.nearGeraud(player)==0,"relecture réarmée attend un nouveau déplacement");player.x=1.5;check(Pq01Bridge.nearGeraud(player)==1,"nouvelle approche fonctionne après réarmement");
    fresh();npc.x=2;Pq01Bridge.nearGeraud(player);player.x=1;clock(99);check(Pq01Bridge.nearGeraud(player)==0,"délai de cinq secondes non écourté");clock(100);check(Pq01Bridge.nearGeraud(player)==1,"délai atteint");
    fresh();npc.x=2;Pq01Bridge.interact(player,npc,"geraud_large","interact");check(DialogueManager.opened==1&&called("function capitale:npc/geraud_rivet/interact_self"),"clic volontaire immédiat sans délai de proximité");
    fresh();npc.x=16;Pq01Bridge.interact(player,npc,"geraud_large","near");check(events.isEmpty()&&DialogueManager.opened==0,"ancien near à seize blocs filtré");
    fresh();npc.x=2;Pq01Bridge.nearGeraud(player);player.x=1;clock(200);world.server.board.values.put("hch_pq1node",4);check(Pq01Bridge.nearGeraud(player)==0,"checkpoint existant : reprise uniquement au clic");
    fresh();npc.x=2;Pq01Bridge.nearGeraud(player);player.x=1;clock(200);world.server.board.values.put("CAP_QDIALOG_OWNER",12);check(Pq01Bridge.nearGeraud(player)==0,"autre quête respectée");
    fresh();npc.x=2;Pq01Bridge.nearGeraud(player);player.x=1;clock(200);world.server.board.values.put("CAP_PROFILE_LOCK",1);check(Pq01Bridge.nearGeraud(player)==0,"profil en chargement ne déclenche rien");
    check(Pq01Bridge.execute(player,"near_geraud")==0&&events.isEmpty(),"entrée historique pendant chargement : aucune exception ni message de dialogue");
    fresh();active();check(Pq01Bridge.reconcile(player)==1,"état actif sans vue récupéré");check(world.server.board.values.get("hcm_pq1_active")==0&&world.server.board.values.get("hch_pq1node")==4,"récupération conserve le checkpoint persistant");
    fresh();active();Object v=view();Object old=new Object();DialogueManager.session=old;set(v,"captureSession",old);set(v,"camera",npc);
    check(Pq01Bridge.reconcile(player)==0&&world.server.board.values.get("hcm_pq1_active")==1,"session courante valide préservée");
    DialogueManager.session=null;Pq01Bridge.captureClosed(player,old);check(((Map<?,?>)get(Pq01Bridge.class,"VIEWS")).isEmpty(),"fermeture enlève la vue");check(world.server.board.values.get("hcm_pq1_active")==0&&world.server.board.values.get("hch_pq1node")==4,"fermeture libère l’état sans reset");
    Pq01Bridge.interact(player,npc,"geraud_large","interact");check(DialogueManager.opened==1&&called("function capitale:npc/geraud_rivet/interact_self"),"réouverture par clic après fermeture");
    fresh();active();v=view();old=new Object();set(v,"captureSession",old);DialogueManager.session=new Object();Object unrelated=DialogueManager.session;
    check(Pq01Bridge.reconcile(player)==1&&DialogueManager.session==unrelated,"réconciliation ne ferme pas la caméra d’une autre session");
    fresh();active();v=view();set(v,"pendingCamera",npc);check(Pq01Bridge.reconcile(player)==0,"ouverture différée valide non annulée");
    fresh();commandEffect=c->{if(c.equals("function capitale:npc/geraud_rivet/interact_self")){active();Pq01Bridge.view(player);}};
    Pq01Bridge.interact(player,npc,"geraud_large","interact");v=view();old=DialogueManager.session;
    check(get(v,"captureSession")==old,"première capture adoptée avant le changement de caméra");
    DialogueManager.session=null;Pq01Bridge.captureClosed(player,old);check(world.server.board.values.get("hcm_pq1_active")==0,"fermeture pendant la première capture nettoie aussi PQ01");
    fresh();active();v=view();old=new Object();DialogueManager.session=old;set(v,"captureSession",old);set(v,"camera",npc);set(v,"cameraRole","geraud");Entity far=new Entity(world);far.x=12;bind("gautier_large",far);
    check(Pq01Bridge.line(player,"a3")==1,"locuteur hors portée : texte autorisé sur la caméra existante");check(get(v,"cameraRole").equals("gautier"),"nom du locuteur reste Gautier");check((Boolean)invoke("openCamera",v,far)&&DialogueManager.session==old&&DialogueManager.opened==0,"aucune tentative de remplacement hors portée ni fermeture de la bonne caméra");
    fresh();active();v=view();DialogueManager.session=new Object();set(v,"captureSession",DialogueManager.session);DialogueManager.returnNull=true;
    check(!(Boolean)invoke("openCamera",v,npc),"refus natif d’ouverture détecté");check(world.server.board.values.get("hcm_pq1_active")==0&&((Map<?,?>)get(Pq01Bridge.class,"VIEWS")).isEmpty(),"refus natif ne laisse aucun état actif orphelin");check(world.server.board.values.get("hch_pq1node")==4,"refus conserve le checkpoint");
    fresh();active();v=view();old=new Object();DialogueManager.session=old;set(v,"captureSession",old);set(v,"camera",npc);npc.x=20;
    check(!(Boolean)invoke("openCamera",v,npc)&&world.server.board.values.get("hcm_pq1_active")==0,"aucun point de caméra valide : fermeture propre et nouvelle tentative possible");
    fresh();active();v=view();set(v,"waitingNode",4);check((Boolean)invoke("openCamera",v,npc),"caméra de reprise ouverte pendant l’approche");check(called("function capitale:pq01/scene/resume_wait_self"),"attente de marche renvoyée après ouverture pour masquer les points de suspension");
    System.out.println("PASS "+checks+" assertions de régression Géraud sur le pont livré ; monde, effets de fonctions, horloge et caméra doublés. Aucun rendu client réel.");
  }
}
