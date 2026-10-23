package net.hautecapitale.quetes.sequence;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;

/** Extension bornée de PQ01. Les PNJ du monde ne sont jamais déplacés par une scène. */
public final class Pq01Bridge {
  private static final String EASY="de.markusbordihn.easynpc.entity.easynpc.EasyNPC";
  private static final String DM="net.hautecapitale.dialogue.session.DialogueManager";
  private static final String STORAGE="capitale:pq01_setup";
  private static final Map<UUID,View> VIEWS=new HashMap<>();
  private static final Set<UUID> ACTORS=new HashSet<>(),SUPPRESS=new HashSet<>();
  private static final Map<UUID,Long> GRACE=new HashMap<>();
  private static final Map<UUID,NearState> NEAR=new HashMap<>();
  private static final Set<String> ERRORS=new HashSet<>();
  private static long clock; private static boolean initialized,teamReady;
  private static final String[] ROLES={"geraud","altheon","gautier","hugues","anselme","perrin"};
  private static final List<String> POINTS=List.of("gautier_poste","gautier_approche","hugues_charge","anselme_instruments","perrin_a","perrin_b","gautier_poste_quai","hugues_charge_quai","anselme_instruments_quai","perrin_a_quai","perrin_b_quai");
  private static final Map<String,String> NAMES=Map.of("geraud","Géraud Rivet","altheon","Althéon Brumeforge","gautier","Gautier","hugues","Hugues","anselme","Anselme","perrin","Perrin");
  private Pq01Bridge(){}
  private static final class NearState {
    final UUID character;final Object world;final Pq01Motion.Point origin;final long readyAt;boolean fired;
    NearState(Object p){character=character(p);world=world(p);origin=pos(p,world);readyAt=clock+100;}
  }
  static Class<?> type(String n){try{return Class.forName(n);}catch(ClassNotFoundException e){throw new IllegalStateException(n,e);}}
  static Object stat(String c,String m,Object...a){return call(type(c),m,a);}
  static Object en(String c,String n){try{return type(c).getField(n).get(null);}catch(ReflectiveOperationException e){throw new IllegalStateException(c+"."+n,e);}}
  static Object make(String c,Object...a){
    outer:for(Constructor<?> k:type(c).getConstructors()){
      if(k.getParameterCount()!=a.length)continue;
      for(int i=0;i<a.length;i++)if(a[i]!=null&&!k.getParameterTypes()[i].isInstance(a[i]))continue outer;
      try{return k.newInstance(a);}catch(ReflectiveOperationException e){throw new IllegalStateException(c,e);}
    }throw new IllegalStateException("Constructeur absent : "+c);
  }
  static void report(Throwable e){if(ERRORS.add(e.toString())){System.err.println("[Indivis PQ01-R3-HF6] "+e);e.printStackTrace(System.err);}}
  static void say(Object p,String s){run(p,"tellraw @s "+quote("[PQ01] "+s));}
  static void diagnostic(Object p,String text){if(ERRORS.add(text))System.err.println("[Indivis PQ01-R3-HF6] "+text);}
  static void unavailable(Object p,String reason){diagnostic(p,reason);run(p,"title @s actionbar "+quote("Cette conversation n’est pas disponible pour le moment."));}
  static Object world(Object p){return call(p,"method_51469");}
  static String dimension(Object world){return String.valueOf(call(call(world,"method_27983"),"method_29177"));}
  static Pq01Motion.Point pos(Object e,Object w){return new Pq01Motion.Point(dimension(w),num(call(e,"method_23317")),num(call(e,"method_23318")),num(call(e,"method_23321")));}
  static double num(Object n){return ((Number)n).doubleValue();}
  static Set<String> tags(Object e){@SuppressWarnings("unchecked") Set<String> s=(Set<String>)call(e,"method_5752");return s;}
  static boolean actor(Object e){return e!=null&&tags(e).contains(Pq01Actors.TAG);}
  static UUID character(Object p){Object c=stat("net.tompsen.nexuscharacters.NexusCharacters","getSelectedCharacter",p);return c==null?null:(UUID)call(c,"id");}
  static boolean available(Object p){return p!=null&&score(p,"CAP_PROFILE_LOCK")==0&&character(p)!=null;}
  static Object child(Object n,String key){return n==null?null:GuardNbt.read(n,key);}
  static String str(Object n,String key,String fallback){return n==null?fallback:(String)call(n,"method_68564",key,fallback);}
  static int integer(Object n,String key,int fallback){return n==null?fallback:((Number)call(n,"method_68083",key,fallback)).intValue();}
  static Object storage(Object p,String name){Object id=stat("net.minecraft.class_2960","method_60654",name);return call(call(server(p),"method_22827"),"method_22546",id);}
  static Object setup(Object p){return storage(p,STORAGE);}
  static Object rootCopy(Object p){return Pq01Nbt.copy(setup(p));}
  static void save(Object p,Object n){GuardNbt.save(server(p),STORAGE,n);}
  static Pq01Motion.Point point(Object n){
    if(n==null)return null;
    return new Pq01Motion.Point(str(n,"dimension","minecraft:overworld"),number(n,"x"),number(n,"y"),number(n,"z"));
  }
  static double number(Object n,String key){Object v=child(n,key);if(v==null)throw new IllegalArgumentException("Coordonnée absente : "+key);return Double.parseDouble(v.toString().replaceFirst("[dDfFbBsSlL]$",""));}
  static Object pointNbt(Pq01Motion.Point p){Object n=GuardNbt.compound();GuardNbt.string(n,"dimension",p.dimension());call(n,"method_10549","x",p.x());call(n,"method_10549","y",p.y());call(n,"method_10549","z",p.z());return n;}
  static Object binding(Object p,String key){return child(child(setup(p),"bindings"),key);}
  static Object source(Object p,String key){
    Object b=binding(p,key);if(b==null||!dimension(world(p)).equals(str(b,"dimension","")))return null;
    String id=str(b,"uuid","");Object npc=id.isEmpty()?null:call(world(p),"method_66347",UUID.fromString(id));
    if(npc!=null&&key.startsWith("anselme_")&&!tags(npc).contains("hcm_pq1_anselme")){
      call(npc,"method_5780","hcm_pq1_anselme");
      if(integer(b,"added_role_tag",0)==1)call(npc,"method_5738","hcq_pnj_anselme");
    }
    return npc;
  }
  static Pq01Motion.Point configuredPoint(Object p,String key,Pq01Motion.Point fallback){
    String scoped=phase(p).equals("quai")?key+"_quai":key;
    Object n=child(child(setup(p),"points"),scoped);if(n==null)return fallback;
    Pq01Motion.Point found=point(n);return found.dimension().equals(dimension(world(p)))?found:fallback;
  }
  static boolean same(View v,Object p){return available(p)&&v.account.equals(uuid(p))&&v.character.equals(character(p))&&v.world==world(p);}
  static boolean dialogue(Object p){return available(p)&&score(p,"hcm_pq1_active")==1&&score(p,"CAP_QDIALOG_OWNER")>=91&&score(p,"CAP_QDIALOG_OWNER")<=96;}
  static boolean alive(Actor a){return a!=null&&!(Boolean)call(a.entity,"method_31481");}
  private static final class Actor {
    final String role;final Object entity,source;final UUID id,sourceId;final Object goals,targets;final Pq01Motion.Point post;
    Pq01Motion motion;long nextWork;boolean leg;
    Actor(String r,Object e,Object s,Object g,Object t,Pq01Motion.Point post){role=r;entity=e;source=s;id=uuid(e);sourceId=uuid(s);goals=g;targets=t;this.post=post;}
  }
  private static final class View {
    final UUID account,character;final Object player,world;final Map<String,Actor> actors=new HashMap<>();final Set<UUID> hidden=new HashSet<>();
    String phase,cameraRole,pendingNode;Object pendingCamera,camera,captureSession;long cameraAt;boolean scene,banter;int waitingNode;Pq01Motion.Point approach;
    View(Object p,String phase){account=uuid(p);character=character(p);player=p;world=world(p);this.phase=phase;}
  }
  public static void init(){
    if(initialized)return;initialized=true;
    Pq01Actions.register();
    event("net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback","EVENT",a->{register(a[0]);return null;});
    event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick","END_SERVER_TICK",a->{tick(a[0]);return null;});
    event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStopping","SERVER_STOPPING",a->{for(View v:new ArrayList<>(VIEWS.values()))close(v.player,true);return null;});
    event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStopped","SERVER_STOPPED",a->{VIEWS.clear();ACTORS.clear();GRACE.clear();NEAR.clear();SUPPRESS.clear();Pq01Actors.reset();clock=0;teamReady=false;return null;});
    event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents$Load","ENTITY_LOAD",a->{if(actor(a[0])&&!ACTORS.contains(uuid(a[0])))call(a[0],"method_31472");return null;});
    stat(DM,"onFermeture",(BiConsumer<Object,Object>)Pq01Bridge::captureClosed);
    stat(DM,"sondeMouvement",(Function<Object,Optional<Boolean>>)e->actor(e)?Optional.of(false):Optional.empty());
  }
  interface EventHandler{Object run(Object[] a)throws Throwable;}
  static void event(String contract,String field,EventHandler handler){
    try{Class<?> c=type(contract),owner=contract.contains("$")?type(contract.substring(0,contract.indexOf('$'))):c;
      Object e=owner.getField(field).get(null);
      Object proxy=Proxy.newProxyInstance(c.getClassLoader(),new Class<?>[]{c},(p,m,a)->{
        if(m.getDeclaringClass()==Object.class)return objectMethod(p,m,a);
        try{return handler.run(a);}catch(Throwable failure){report(failure);return null;}
      });type("net.fabricmc.fabric.api.event.Event").getMethod("register",Object.class).invoke(e,proxy);
    }catch(ReflectiveOperationException e){throw new IllegalStateException(contract,e);}
  }
  static Object objectMethod(Object p,Method m,Object[] a){return switch(m.getName()){case "toString"->"IndivisPQ01";case "hashCode"->System.identityHashCode(p);case "equals"->p==a[0];default->null;};}
  static void register(Object dispatcher){
    Object root=stat("com.mojang.brigadier.builder.LiteralArgumentBuilder","literal","indivispq01");
    List<String> actions=new ArrayList<>(List.of("approach","return","close","banter","status","cleanup","near_geraud","reconcile","rearm_near"));
    for(String r:ROLES){actions.add("camera_"+r);for(String phase:List.of("large","quai")){for(String op:List.of("bind_","unbind_","place_"))actions.add(op+r+"_"+phase);}}
    for(String p:POINTS)actions.add("point_"+p);
    for(String key:Pq01Ids.keys())actions.add("line_"+key);
    for(String action:actions){
      Object node=stat("com.mojang.brigadier.builder.LiteralArgumentBuilder","literal",action);
      if(action.startsWith("bind_")||action.startsWith("unbind_")||action.startsWith("point_")||action.startsWith("place_")||action.equals("status")||action.equals("cleanup")||action.equals("rearm_near")){
        Object permission=en("net.minecraft.class_2170","field_31839");call(node,"requires",stat("net.minecraft.class_2170","method_71774",permission));
      }
      Class<?> command=type("com.mojang.brigadier.Command");
      Object proxy=Proxy.newProxyInstance(command.getClassLoader(),new Class<?>[]{command},(p,m,a)->{
        if(m.getDeclaringClass()==Object.class)return objectMethod(p,m,a);
        Object player=null;try{player=call(call(a[0],"getSource"),"method_9207");return execute(player,action);}catch(Throwable failure){report(failure);if(player!=null){if(action.startsWith("camera_")||action.startsWith("line_")||List.of("approach","return","banter","near_geraud").contains(action))diagnostic(player,"Scène indisponible : "+failure.getMessage());else say(player,"Action annulée : "+failure.getMessage());}return 0;}
      });call(node,"executes",proxy);call(root,"then",node);
    }call(dispatcher,"register",root);
  }
  static int execute(Object p,String action){
    if(action.equals("close")){close(p,true);return 1;}
    if(action.equals("cleanup")){for(View v:new ArrayList<>(VIEWS.values())){close(v.player,true);if(score(v.player,"hcm_pq1_active")==1)run(v.player,"function capitale:pq01/close_self");}say(p,"Scènes personnelles nettoyées. Les originaux restent en place.");return 1;}
    if(action.equals("status")){status(p);return 1;}
    if(action.equals("near_geraud"))return nearGeraud(p);
    if(!available(p))throw new IllegalStateException("Sélectionnez un personnage Nexus chargé.");
    if(action.equals("rearm_near")){NEAR.remove(uuid(p));return 1;}
    if(action.equals("reconcile"))return reconcile(p);
    if(action.startsWith("bind_"))return bind(p,action.substring(5));
    if(action.startsWith("unbind_"))return unbind(p,action.substring(7));
    if(action.startsWith("place_"))return place(p,action.substring(6));
    if(action.startsWith("point_")){Object root=rootCopy(p);GuardNbt.put(GuardNbt.childOrCreate(root,"points"),action.substring(6),pointNbt(pos(p,world(p))));save(p,root);say(p,"Point réel enregistré : "+action.substring(6));return 1;}
    if(!dialogue(p))return 0;
    if(action.startsWith("line_"))return line(p,action.substring(5));
    if(action.startsWith("camera_"))return camera(p,action.substring(7));
    if(action.equals("approach"))return moveScene(p,false);
    if(action.equals("return"))return moveScene(p,true);
    if(action.equals("banter"))return banter(p);
    return 0;
  }
  static String hook(String role,String phase){
    if(role.equals("geraud"))return "function capitale:npc/geraud_rivet/interact_self";
    if(role.equals("altheon"))return "function capitale:quest/spawn_dirigeable_couronne/capitaine_large/interact_self";
    int i=List.of("gautier","hugues","anselme","perrin").indexOf(role)+1;
    return "function capitale:quest/spawn_dirigeable_couronne/matelots/matelot_0"+i+(phase.equals("quai")?"_quai_self":"_self");
  }
  static Object action(String key,String event){return Pq01Actions.plan(null,key,event);}
  static Object eventSet(Object snapshot){return child(child(snapshot,"ActionData"),"ActionEventSet");}
  static boolean captureInteraction(Object p,Object npc){
    // Dialogue b10 n'ouvre automatiquement la capture que pour COMMAND.
    // Une action CUSTOM PQ01 doit la préparer avant d'exécuter son texte,
    // y compris si le personnage n'est pas à la phase de cette liaison.
    Object config=stat("net.hautecapitale.dialogue.config.DialogueConfig","get");
    if(!Boolean.TRUE.equals(call(config,"capture",npc)))return true;
    Optional<?> refusal=(Optional<?>)stat(DM,"refus",p,npc);
    if(refusal.isPresent()){stat(DM,"dire",p,refusal.get());return false;}
    if(Boolean.TRUE.equals(stat(DM,"enSession",p)))return true;
    UUID account=uuid(p);boolean added=SUPPRESS.add(account);
    try{return stat(DM,"ouvrir",p,npc,en("net.hautecapitale.dialogue.session.DialogueMode","CAPTURE"))!=null;}
    finally{if(added)SUPPRESS.remove(account);}
  }
  static void interact(Object p,Object npc,String key,String event){
    Pq01Actions.command(key,event); // Le couple rôle/phase/événement est une liste fermée.
    if(p==null||npc==null||world(p)!=call(npc,"method_73183"))return;
    double maxDistance=event.equals("near")?3:8;
    if(pos(npc,world(p)).distance2(pos(p,world(p)))>maxDistance*maxDistance)return;
    String role=key.substring(0,key.lastIndexOf('_'));
    if(actor(npc)){
      View v=VIEWS.get(uuid(p));Actor a=v==null?null:v.actors.get(role);
      if(v==null||!same(v,p)||a==null||!a.id.equals(uuid(npc))||!ACTORS.contains(a.id)||!key.endsWith("_"+v.phase)||!event.equals("interact"))return;
    }else{
      Object record=binding(p,key);
      if(record==null||!uuid(npc).toString().equals(str(record,"uuid",""))||!dimension(world(p)).equals(str(record,"dimension","")))return;
    }
    boolean click=event.equals("interact");
    if(!click){if(key.equals("geraud_large"))nearGeraud(p);return;}
    reconcile(p);
    if(click&&!captureInteraction(p,npc))return;
    if(!available(p)){if(click)unavailable(p,"Personnage Nexus pas encore chargé.");return;}
    if(!key.endsWith("_"+phase(p))){
      if(click)unavailable(p,"Phase incompatible : "+key+", personnage="+phase(p)+". Relecture explicite : capitale:pq01/admin/test_debut.");
      return;
    }
    run(p,Pq01Actions.function(key,event));
    View v=VIEWS.get(uuid(p));
    if(v!=null&&dialogue(p)&&v.captureSession==null)v.captureSession=currentSession(p);
  }
  static Object currentSession(Object p){return ((Optional<?>)stat(DM,"session",p)).orElse(null);}
  static void captureClosed(Object p,Object session){
    View v=VIEWS.get(uuid(p));
    if(v!=null&&v.captureSession==session&&!SUPPRESS.contains(uuid(p))){close(p,false);if(dialogue(p))run(p,"function capitale:pq01/close_self");}
  }
  static int reconcile(Object p){
    if(!dialogue(p))return 0;
    View v=VIEWS.get(uuid(p));Object session=currentSession(p);
    if(v!=null&&same(v,p)&&((v.captureSession!=null&&v.captureSession==session)||(v.captureSession==null&&v.pendingCamera!=null)))return 0;
    close(p,false);run(p,"function capitale:pq01/close_self");return 1;
  }
  static int nearGeraud(Object p){
    if(!available(p)){NEAR.remove(uuid(p));return 0;}
    NearState state=NEAR.get(uuid(p));
    if(state==null||!state.character.equals(character(p))||state.world!=world(p)){state=new NearState(p);NEAR.put(uuid(p),state);}
    if(state.fired||clock<state.readyAt||score(p,"hcq_pq01")>=40||score(p,"hcf_pq1_a")!=0||score(p,"hch_pq1node")!=0||score(p,"hcm_pq1_active")!=0||score(p,"CAP_QSEQ")!=0||score(p,"CAP_QDIALOG_OWNER")!=0||currentSession(p)!=null||clock<GRACE.getOrDefault(uuid(p),0L))return 0;
    Pq01Motion.Point here=pos(p,state.world);double dx=here.x()-state.origin.x(),dz=here.z()-state.origin.z();
    if(dx*dx+dz*dz<0.75*0.75)return 0;
    Object npc=source(p,"geraud_large");if(npc==null||pos(npc,state.world).distance2(here)>9)return 0;
    state.fired=true;run(p,"function capitale:pq01/open/geraud_self");return 1;
  }
  static Object nearest(Object p){
    Object box=call(call(p,"method_5829"),"method_1014",3.0d);
    @SuppressWarnings("unchecked") List<Object> nearby=(List<Object>)call(world(p),"method_8333",p,box,(Predicate<Object>)e->type(EASY).isInstance(e)&&!actor(e));
    nearby=new ArrayList<>(nearby);nearby.removeIf(e->pos(e,world(p)).distance2(pos(p,world(p)))>9);
    nearby.sort(Comparator.comparingDouble(e->pos(e,world(p)).distance2(pos(p,world(p)))));
    if(nearby.isEmpty())throw new IllegalStateException("Aucun EasyNPC chargé à moins de trois blocs.");
    if(nearby.size()>1){double a=Math.sqrt(pos(nearby.get(0),world(p)).distance2(pos(p,world(p)))),b=Math.sqrt(pos(nearby.get(1),world(p)).distance2(pos(p,world(p))));if(b-a<0.5)throw new IllegalStateException("Sélection ambiguë : rapprochez-vous du seul PNJ à lier.");}
    return nearby.get(0);
  }
  static void mutateEvent(Object p,Object npc,String name,Object value){Pq01Actions.mutate(npc,name,value);}
  static void editable(Object p,Object npc){
    if(stat(DM,"pnjOccupe",uuid(npc),uuid(p)).equals(true)||VIEWS.values().stream().anyMatch(v->v.hidden.contains(uuid(npc))))throw new IllegalStateException("Ce PNJ participe à une scène ou un dialogue. Fermer les scènes avant de le modifier.");
  }
  static int bind(Object p,String key){
    String[] split=key.split("_");String role=split[0],phase=split[1];Object npc=nearest(p),snapshot=GuardNbt.entity(npc),events=eventSet(snapshot),old=child(events,"ON_INTERACTION");
    editable(p,npc);
    Object previous=binding(p,key);
    if(previous!=null&&!str(previous,"uuid","").equals(uuid(npc).toString()))throw new IllegalStateException("Cette liaison existe déjà. La libérer avant de choisir un autre PNJ.");
    Pq01Actions.assertUnchanged(previous,old,child(events,"ON_DISTANCE_NEAR"));
    for(String r:ROLES)for(String ph:List.of("large","quai")){String other=r+"_"+ph;Object b=binding(p,other);if(!other.equals(key)&&b!=null&&str(b,"uuid","").equals(uuid(npc).toString()))throw new IllegalStateException("Ce PNJ est déjà lié à "+other);}
    Object linked=Pq01Actions.plan(old,key,"interact");
    Object root=rootCopy(p),bindings=GuardNbt.childOrCreate(root,"bindings"),record=previous==null?GuardNbt.compound():Pq01Nbt.copy(previous);
    if(previous==null){
      GuardNbt.string(record,"uuid",uuid(npc).toString());GuardNbt.string(record,"dimension",dimension(world(p)));GuardNbt.string(record,"type",Pq01EntityFactory.registryId(npc));
      if(old!=null)GuardNbt.put(record,"original_interaction",Pq01Nbt.copy(old));
      Object near=child(events,"ON_DISTANCE_NEAR");if(near!=null)GuardNbt.put(record,"original_near",Pq01Nbt.copy(near));
      for(String n:List.of("CustomName","custom_name")){Object name=child(snapshot,n);if(name!=null){GuardNbt.string(record,"name_key",n);GuardNbt.put(record,"original_name",Pq01Nbt.copy(name));break;}}
      GuardNbt.integer(record,"added_role_tag",tags(npc).contains("hcq_pnj_"+role)?0:1);
    }
    GuardNbt.put(record,"linked_interaction",Pq01Nbt.copy(linked));
    Object newNear=null;
    if(phase.equals("large")&&(role.equals("geraud")||role.equals("altheon"))){
      newNear=Pq01Actions.migrateNear(child(events,"ON_DISTANCE_NEAR"),key);
      if(newNear!=null)GuardNbt.put(record,"linked_near",Pq01Nbt.copy(newNear));
    }
    GuardNbt.string(record,"linked_name_text",NAMES.get(role));
    GuardNbt.put(bindings,key,record);save(p,root); // sauvegarder avant toute mutation
    mutateEvent(p,npc,"ON_INTERACTION",linked);
    if(newNear!=null)mutateEvent(p,npc,"ON_DISTANCE_NEAR",newNear);
    call(npc,"method_5780","hcq_pnj_"+role);call(npc,"method_5665",stat("net.minecraft.class_2561","method_43470",NAMES.get(role)));
    if(role.equals("anselme")){call(npc,"method_5780","hcm_pq1_anselme");if(integer(record,"added_role_tag",0)==1)call(npc,"method_5738","hcq_pnj_anselme");}
    Object named=GuardNbt.entity(npc);for(String k:List.of("CustomName","custom_name"))if(child(named,k)!=null){GuardNbt.string(record,"linked_name_key",k);GuardNbt.put(record,"linked_name",Pq01Nbt.copy(child(named,k)));}
    GuardNbt.put(record,"post",pointNbt(pos(npc,world(p))));GuardNbt.put(bindings,key,record);save(p,root);
    run(p,"data modify storage hc_quetes:lieux pq01_"+key+" set value "+waypoint(pos(npc,world(p))));
    say(p,"Liaison "+key+" enregistrée ; apparence et autres actions conservées.");return 1;
  }
  static Object waypoint(Pq01Motion.Point at){Object n=pointNbt(at);GuardNbt.integer(n,"r",3);return n;}
  static int unbind(Object p,String key){
    Object record=binding(p,key),npc=source(p,key);if(record==null||npc==null)throw new IllegalStateException("PNJ lié non chargé dans cette dimension.");
    editable(p,npc);
    if(pos(npc,world(p)).distance2(pos(p,world(p)))>64)throw new IllegalStateException("Rapprochez-vous à huit blocs du PNJ lié.");
    Object events=eventSet(GuardNbt.entity(npc));
    for(String ev:List.of("interaction","near")){
      Object linked=child(record,"linked_"+ev),actual=child(events,ev.equals("near")?"ON_DISTANCE_NEAR":"ON_INTERACTION");
      if(linked!=null&&!Pq01Actions.equivalent(linked,actual))throw new IllegalStateException("Actions changées depuis la liaison : aucune restauration forcée.");
    }
    mutateEvent(p,npc,"ON_INTERACTION",child(record,"original_interaction"));
    if(child(record,"linked_near")!=null)mutateEvent(p,npc,"ON_DISTANCE_NEAR",child(record,"original_near"));
    String role=key.split("_")[0];if(integer(record,"added_role_tag",0)==1)run(p,"tag "+uuid(npc)+" remove hcq_pnj_"+role);
    if(role.equals("anselme"))call(npc,"method_5738","hcm_pq1_anselme");
    String nameKey=str(record,"linked_name_key","");Object current=GuardNbt.entity(npc);
    Object currentName=call(npc,"method_5797");
    boolean interruptedNameWrite=nameKey.isEmpty()&&currentName!=null&&str(record,"linked_name_text","").equals(call(currentName,"getString"));
    if(interruptedNameWrite)nameKey=child(current,"CustomName")!=null?"CustomName":"custom_name";
    if(interruptedNameWrite||!nameKey.isEmpty()&&Objects.equals(String.valueOf(child(current,nameKey)),String.valueOf(child(record,"linked_name")))){
      Object old=child(record,"original_name");String oldKey=str(record,"name_key",nameKey);
      run(p,old==null?"data remove entity "+uuid(npc)+" "+nameKey:"data modify entity "+uuid(npc)+" "+oldKey+" set value "+old);
    }
    Object root=rootCopy(p);call(GuardNbt.childOrCreate(root,"bindings"),"method_10551",key);save(p,root);say(p,"Liaison libérée ; sauvegarde conservée en cas de refus uniquement.");return 1;
  }
  static int place(Object p,String key){
    Object npc=source(p,key);if(npc==null)throw new IllegalStateException("Lier d'abord le PNJ chargé dans cette dimension.");
    if(score(p,"CAP_QDIALOG_OWNER")!=0||stat(DM,"pnjOccupe",uuid(npc),uuid(p)).equals(true)||VIEWS.values().stream().anyMatch(v->v.hidden.contains(uuid(npc))))throw new IllegalStateException("PNJ utilisé dans une conversation/scène : placement refusé.");
    if(pos(npc,world(p)).distance2(pos(p,world(p)))>64*64)throw new IllegalStateException("Placement limité à 64 blocs, dans la dimension actuelle.");
    Pq01Motion.Point at=pos(p,world(p));run(p,"tp "+uuid(npc)+" "+coords(at)+" "+call(p,"method_36454")+" "+call(p,"method_36455"));
    Object root=rootCopy(p);GuardNbt.put(GuardNbt.childOrCreate(root,"bindings"),key,Pq01Nbt.copy(binding(p,key)));GuardNbt.put(child(child(root,"bindings"),key),"post",pointNbt(at));save(p,root);
    run(p,"data modify storage hc_quetes:lieux pq01_"+key+" set value "+waypoint(at));say(p,"PNJ "+key+" placé à votre position. Cette commande d'édition n'est pas un trajet de scène.");return 1;
  }
  static String coords(Pq01Motion.Point p){return p.x()+" "+p.y()+" "+p.z();}
  static String phase(Object p){return score(p,"hcq_pq01")>=40?"quai":"large";}
  static View view(Object p){
    View v=VIEWS.get(uuid(p));String phase=phase(p);
    if(v!=null&&(!same(v,p)||!v.phase.equals(phase))){close(p,true);v=null;}
    if(v==null){v=new View(p,phase);VIEWS.put(uuid(p),v);}return v;
  }
  static void refresh(Object p){stat("net.hautecapitale.quetes.visibility.QuestVisibility","forget",p);stat("net.hautecapitale.quetes.visibility.QuestVisibility","refresh",p);}
  public static boolean visibleOriginal(Object entity,Object player){
    if(VIEWS.isEmpty())return true;
    View v=VIEWS.get(uuid(player));return v==null||!v.hidden.contains(uuid(entity))||!same(v,player);
  }
  static Actor spawn(View v,String role,Pq01Motion.Point start){
    Actor old=v.actors.get(role);if(alive(old))return old;
    if(old!=null)discard(v,role);
    Object src=source(v.player,role+"_"+v.phase);if(src==null)throw new IllegalStateException("Liaison "+role+"_"+v.phase+" absente/non chargée.");
    Pq01Motion.Point origin=pos(src,v.world);if(start==null)start=origin;
    if(!origin.dimension().equals(start.dimension())||origin.distance2(start)>24*24)throw new IllegalStateException("Point d'activité hors portée du PNJ source.");
    Object template=Pq01Nbt.copy(GuardNbt.entity(src));
    for(String field:List.of("UUID","UUIDMost","UUIDLeast","Pos","Motion","Rotation","Passengers","Leash","Brain","Tags","Home","ActionData","ObjectiveData","TradingData"))call(template,"method_10551",field);
    GuardNbt.put(template,"Tags",GuardNbt.list(List.of(Pq01Actors.TAG,"hcm_private","hcm_owner_"+v.account,"hcm_character_"+v.character,"indivis_no_hard_despawn").stream().map(GuardNbt::stringValue).toList()));
    call(template,"method_10556","Invulnerable",true);call(template,"method_10556","Silent",true);call(template,"method_10556","NoAI",!role.equals("gautier")&&!role.equals("perrin"));
    call(template,"method_10556","NoGravity",false);call(template,"method_10556","CanPickUpLoot",false);GuardNbt.string(template,"DeathLootTable","minecraft:empty");
    Object events=GuardNbt.childOrCreate(GuardNbt.childOrCreate(template,"ActionData"),"ActionEventSet");GuardNbt.put(events,"ON_INTERACTION",action(role+"_"+v.phase,"interact"));
    UUID fresh=UUID.randomUUID();
    ACTORS.add(fresh);v.hidden.add(uuid(src));Object e=null,g=null,t=null;
    try{
      e=Pq01EntityFactory.create(src,v.world,template,fresh,start);
      call(e,"method_5665",stat("net.minecraft.class_2561","method_43470",NAMES.get(role)));
      configureActor(e,role.equals("gautier")||role.equals("perrin"));
      Object yaw=call(src,"method_36454"),pitch=call(src,"method_36455");call(e,"method_36456",yaw);call(e,"method_36457",pitch);call(e,"method_5847",yaw);call(e,"method_5636",yaw);
      g=call(e,"getEntityGoalSelector");t=call(e,"getEntityTargetSelector");Pq01Actors.protect(g);Pq01Actors.protect(t);
      call(g,"method_35113",(Predicate<Object>)goal->true);call(t,"method_35113",(Predicate<Object>)goal->true);
      if(!teamReady){run(v.player,"team add indivis_pq01");run(v.player,"team modify indivis_pq01 collisionRule never");teamReady=true;}
      Actor a=new Actor(role,e,src,g,t,start);v.actors.put(role,a);
      if(!Boolean.TRUE.equals(call(v.world,"method_8649",e))||call(v.world,"method_66347",fresh)!=e)throw new IllegalStateException("Insertion de l'acteur refusée par le monde.");
      Pq01Actions.mutate(e,"ON_INTERACTION",action(role+"_"+v.phase,"interact"));
      run(v.player,"team join indivis_pq01 "+fresh);refresh(v.player);return a;
    }catch(Throwable failure){v.actors.remove(role);ACTORS.remove(fresh);v.hidden.remove(uuid(src));if(g!=null)Pq01Actors.forget(g);if(t!=null)Pq01Actors.forget(t);if(e!=null)call(e,"method_31472");refresh(v.player);throw failure;}
  }
  static void configureActor(Object e,boolean mobile){
    Object provider=call(e,"getEasyNPCAttributeData"),attributes=call(provider,"getEntityAttributes");
    Object copy=make("de.markusbordihn.easynpc.data.attribute.EntityAttributes",Pq01Nbt.copy(call(attributes,"createTag")));
    Object move=call(copy,"getMovementAttributes");
    move=call(move,"withIsImmovable",!mobile);move=call(move,"withCanJump",mobile);move=call(move,"withCanPassDoor",true);move=call(move,"withCanOpenDoor",false);move=call(move,"withCanCloseDoor",false);move=call(move,"withCanUseNetherPortal",false);
    move=call(move,"withNavigationType",en("de.markusbordihn.easynpc.data.attribute.NavigationType","GROUND"));
    call(copy,"setMovementAttributes",move);
    Object inventory=call(copy,"getInventoryAttributes");if(inventory==null)inventory=en("de.markusbordihn.easynpc.data.attribute.InventoryAttributes","EMPTY");inventory=call(inventory,"withDropOnDeath",false);inventory=call(inventory,"withPickUpItems",false);call(copy,"setInventoryAttributes",inventory);
    Object interaction=call(copy,"getInteractionAttributes");
    interaction=call(interaction,"withIsPushable",false);interaction=call(interaction,"withPushEntities",false);interaction=call(interaction,"withCanBeHitByProjectile",false);interaction=call(interaction,"withCanBeLeashed",false);interaction=call(interaction,"withBlockVehicleMounting",true);interaction=call(interaction,"withCanSitOnShoulder",false);call(copy,"setInteractionAttributes",interaction);
    call(provider,"setEntityAttributes",copy);call(e,"refreshNavigation");
    call(e,"method_5977",!mobile);call(e,"method_5684",true);call(e,"method_5803",true);call(e,"method_5875",false);
  }
  static void discard(View v,String role){
    Actor a=v.actors.remove(role);if(a==null)return;
    if(a.motion!=null)a.motion.cancel();Pq01Actors.forget(a.goals);Pq01Actors.forget(a.targets);ACTORS.remove(a.id);v.hidden.remove(a.sourceId);call(a.entity,"method_31472");
  }
  static void stop(Actor a){if(a==null)return;Object nav=call(a.entity,"getGroundPathNavigation");if(nav!=null)call(nav,"method_6340");call(a.entity,"method_18799",en("net.minecraft.class_243","field_1353"));}
  static void face(Actor a,Pq01Motion.Point target){
    if(!alive(a))return;Pq01Motion.Point here=pos(a.entity,call(a.entity,"getEntityServerLevel"));
    double dx=target.x()-here.x(),dy=target.y()-here.y(),dz=target.z()-here.z();
    float yaw=(float)(Math.toDegrees(Math.atan2(dz,dx))-90),pitch=(float)-Math.toDegrees(Math.atan2(dy,Math.sqrt(dx*dx+dz*dz)));
    call(a.entity,"method_36456",yaw);call(a.entity,"method_36457",Math.max(-30f,Math.min(30f,pitch)));call(a.entity,"method_5847",yaw);call(a.entity,"method_5636",yaw);
  }
  static int camera(Object p,String role){
    if(score(p,"CAP_QDIALOG_OWNER")!=91+Arrays.asList(ROLES).indexOf(role))return 0;
    View v=view(p);Object src=source(p,role+"_"+v.phase);if(src==null)return 0;
    if(v.cameraRole!=null)return 1;
    Object focus=src;boolean resumed=false;
    boolean intro=role.equals("geraud")&&v.phase.equals("large")&&score(p,"hcf_pq1_a")==0;
    if(intro){
      v.scene=true;v.approach=configuredPoint(p,"gautier_approche",null);
      try{
        Actor geraud=spawn(v,"geraud",null);focus=geraud.entity;
        Object gsource=source(p,"gautier_large");if(gsource==null)throw new IllegalStateException("Lier Gautier au large.");
        Pq01Motion.Point post=configuredPoint(p,"gautier_poste",pos(gsource,v.world));
        int resume=score(p,"hch_pq1node");Actor ga=spawn(v,"gautier",post);
        if(resume>=Pq01Ids.id("a4")&&resume<=Pq01Ids.id("a6")&&v.approach!=null&&pos(ga.entity,v.world).distance2(v.approach)>0.64){
          resumed=true;
          String resumeKey="a"+resume;v.waitingNode=resume;
          run(p,"scoreboard players set @s hcm_pq1_node "+resume);run(p,"scoreboard players set @s hcm_pq1_wait 1");run(p,"function capitale:pq01/scene/resume_wait_self");
          travel(v,ga,v.approach,()->{face(ga,pos(geraud.entity,v.world));face(geraud,pos(ga.entity,v.world));advance(v,resumeKey);},reason->{diagnostic(p,"Reprise mobile interrompue : "+reason);discard(v,"gautier");refresh(p);advance(v,resumeKey);});
        }
      }catch(Throwable e){report(e);diagnostic(p,"Scène mobile en repli : "+e.getMessage());}
    }else{
      Actor a=v.actors.get(role);
      if(alive(a))focus=a.entity;
      else if(occupied(v,src))focus=spawn(v,role,null).entity;
    }
    Actor a=v.actors.get(role);if(a!=null){if(a.motion!=null){a.motion.cancel();a.motion=null;}stop(a);}
    v.cameraRole=role;v.pendingCamera=focus;v.cameraAt=clock+1;return resumed?2:1;
  }
  static int line(Object p,String key){
    String role=Pq01Ids.role(key);if(role==null)return 0;
    View v=view(p);Actor a=v.actors.get(role);Object focus=alive(a)?a.entity:source(p,role+"_"+v.phase);
    if(focus==null)return 1; // texte de secours, nom transmis dans la trame client.
    focus=safeFocus(v,focus);if(focus==null){abortCamera(v);return 0;}
    if(v.pendingCamera==focus){v.cameraRole=role;v.pendingNode=key;return 0;}
    Object session=currentSession(p);
    if(v.camera==focus&&v.captureSession!=null&&v.captureSession==session){v.cameraRole=role;return 1;}
    v.cameraRole=role;v.pendingCamera=focus;v.pendingNode=key;v.cameraAt=clock+1;return 0;
  }
  static Object safeFocus(View v,Object preferred){
    Object config=stat("net.hautecapitale.dialogue.config.DialogueConfig","get");double radius;
    try{radius=((Number)config.getClass().getField("distance_ouverture").get(config)).doubleValue();}
    catch(ReflectiveOperationException e){throw new IllegalStateException("Distance d’ouverture Dialogue b10 introuvable",e);}
    for(Object candidate:new Object[]{preferred,v.camera,source(v.player,"geraud_"+v.phase)})
      if(candidate!=null&&!Boolean.TRUE.equals(call(candidate,"method_31481"))&&call(candidate,"method_73183")==v.world&&pos(candidate,v.world).distance2(pos(v.player,v.world))<=radius*radius)return candidate;
    return null;
  }
  static void abortCamera(View v){
    close(v.player,true);if(dialogue(v.player))run(v.player,"function capitale:pq01/close_self");
    unavailable(v.player,"Ouverture de caméra refusée ; session PQ01 libérée pour une nouvelle interaction.");
  }
  static boolean openCamera(View v,Object focus){
    focus=safeFocus(v,focus);if(focus==null){abortCamera(v);return false;}
    if(v.camera==focus&&v.captureSession!=null&&v.captureSession==currentSession(v.player))return true;
    if(!actor(focus)&&occupied(v,focus)){Actor replacement=spawn(v,v.cameraRole,null);v.pendingCamera=replacement.entity;v.cameraAt=clock+1;return true;}
    SUPPRESS.add(v.account);try{v.captureSession=stat(DM,"ouvrir",v.player,focus,en("net.hautecapitale.dialogue.session.DialogueMode","CAPTURE"));v.camera=v.captureSession==null?null:focus;}
    finally{SUPPRESS.remove(v.account);}
    if(v.captureSession==null){abortCamera(v);return false;}
    if(v.waitingNode!=0)run(v.player,"function capitale:pq01/scene/resume_wait_self");
    return true;
  }
  static boolean occupied(View current,Object entity){
    return stat(DM,"pnjOccupe",uuid(entity),current.account).equals(true)||VIEWS.values().stream().anyMatch(other->other!=current&&(other.pendingCamera==entity||other.camera==entity&&other.captureSession!=null));
  }
  static void close(Object p,boolean closeCamera){
    View v=VIEWS.remove(uuid(p));if(v==null)return;
    v.waitingNode=0;GRACE.put(v.account,clock+40);
    if(closeCamera&&v.captureSession!=null&&((Optional<?>)stat(DM,"session",p)).orElse(null)==v.captureSession){SUPPRESS.add(v.account);try{stat(DM,"fermer",p,en("net.hautecapitale.dialogue.session.CloseReason","REMPLACEE"));}finally{SUPPRESS.remove(v.account);}}
    for(String role:new ArrayList<>(v.actors.keySet()))try{discard(v,role);}catch(Throwable e){report(e);}
    run(p,"scoreboard players set @s hcm_pq1_wait 0");refresh(p);
  }
  static int moveScene(Object p,boolean returning){
    String expected=returning?"a6":"a2",next=returning?"a7":"a3";
    if(score(p,"hcm_pq1_node")!=Pq01Ids.id(expected))return 0;
    if(score(p,"hcm_pq1_voice")>0)return 1;
    View v=VIEWS.get(uuid(p));
    if(v==null||!same(v,p)||!v.scene){diagnostic(p,"Scène mobile indisponible ; repli du dialogue.");run(p,"function capitale:pq01/node/"+next);return 1;}
    if(v.waitingNode!=0)return 1;
    Actor a=v.actors.get("gautier");Pq01Motion.Point target=returning?(a==null?null:configuredPoint(p,"gautier_poste",point(child(binding(p,"gautier_large"),"post")))):v.approach;
    if(!alive(a)||target==null){diagnostic(p,"Trajet non configuré/acteur absent ; repli de l'interruption.");run(p,"function capitale:pq01/node/"+next);return 1;}
    if(returning&&v.camera==a.entity){Actor geraud=v.actors.get("geraud");if(alive(geraud)){v.cameraRole="geraud";openCamera(v,geraud.entity);}}
    v.waitingNode=Pq01Ids.id(expected);run(p,"scoreboard players set @s hcm_pq1_wait 1");
    travel(v,a,target,()->{
      if(returning)face(v.actors.get("geraud"),pos(p,v.world));
      else {face(a,pos(v.actors.get("geraud").entity,v.world));face(v.actors.get("geraud"),pos(a.entity,v.world));}
      advance(v,next);
    },reason->{diagnostic(p,"Trajet Gautier interrompu : "+reason);discard(v,"gautier");refresh(p);advance(v,next);});
    return 1;
  }
  static void travel(View v,Actor a,Pq01Motion.Point target,Runnable done,Consumer<String> failed){
    if(a.motion!=null)a.motion.cancel();
    a.motion=new Pq01Motion(new Pq01Motion.Port(){
      public boolean valid(){return VIEWS.get(v.account)==v&&same(v,v.player)&&alive(a);}
      public Pq01Motion.Point position(){return pos(a.entity,v.world);}
      public boolean navigating(){Object nav=call(a.entity,"getGroundPathNavigation");return nav!=null&&!Boolean.TRUE.equals(call(nav,"method_6357"));}
      public boolean navigate(Pq01Motion.Point t){try{Object nav=call(a.entity,"getGroundPathNavigation");if(nav==null)return false;if(Boolean.TRUE.equals(call(nav,"method_6337",t.x(),t.y(),t.z(),0.65d)))return true;return Math.abs(t.y()-Math.ceil(t.y()))>0.01&&Boolean.TRUE.equals(call(nav,"method_6337",t.x(),Math.ceil(t.y()),t.z(),0.65d));}catch(RuntimeException e){report(e);return false;}}
      public void stop(){try{Pq01Bridge.stop(a);}catch(RuntimeException e){report(e);}}
      public void arrived(){done.run();}
      public void failed(String reason){failed.accept(reason);}
    },target,integer(setup(v.player),"move_timeout_ticks",300));a.motion.start();
  }
  static void advance(View v,String node){
    if(!same(v,v.player)||!dialogue(v.player)||score(v.player,"hcm_pq1_node")!=v.waitingNode){close(v.player,true);return;}
    v.waitingNode=0;run(v.player,"scoreboard players set @s hcm_pq1_wait 0");run(v.player,"function capitale:pq01/node/"+node);
  }
  static int banter(Object p){
    if(integer(storage(p,"capitale:pq01_config"),"allow_banter",0)!=1)return 0;
    View v=view(p);if(!v.phase.equals("large")||score(p,"hcd_pq1_banter")>0||score(p,"hcm_pq1_node")!=Pq01Ids.id("hugues_menu"))return 0;
    @SuppressWarnings("unchecked") List<Object> players=(List<Object>)call(call(server(p),"method_3760"),"method_14571");
    for(Object other:players)if(other!=p&&world(other)==v.world&&pos(other,v.world).distance2(pos(p,v.world))<=256)return 0;
    Actor a=spawn(v,"gautier",null),b=spawn(v,"hugues",null);if(pos(a.entity,v.world).distance2(pos(b.entity,v.world))>64)return 0;
    for(Actor x:List.of(a,b)){if(x.motion!=null){x.motion.cancel();x.motion=null;}stop(x);}
    v.banter=true;face(a,pos(b.entity,v.world));face(b,pos(a.entity,v.world));run(p,"scoreboard players set @s hcd_pq1_banter 1200");run(p,"function capitale:pq01/node/b0");return 1;
  }
  static void status(Object p){
    say(p,"PQ01-R3 : liaisons réelles, scènes personnelles et occupations activées.");
    for(String r:ROLES)for(String ph:List.of("large","quai")){Object b=binding(p,r+"_"+ph);say(p,r+"_"+ph+" : "+(b==null?"À LIER":source(p,r+"_"+ph)==null?"configuré, non chargé dans cette dimension":"chargé"));}
    for(String name:POINTS)say(p,name+" : "+(child(child(setup(p),"points"),name)==null?"à configurer":"configuré"));
  }
  static void tick(Object server){
    clock++;
    @SuppressWarnings("unchecked") List<Object> players=(List<Object>)call(call(server,"method_3760"),"method_14571");
    Set<UUID> online=new HashSet<>();for(Object p:players)online.add(uuid(p));
    if(clock%20==0)for(Object p:players)try{nearGeraud(p);}catch(RuntimeException e){report(e);}
    if(clock%20==0)for(Object p:players)try{source(p,"anselme_large");source(p,"anselme_quai");}catch(RuntimeException e){report(e);}
    for(View v:new ArrayList<>(VIEWS.values()))try{
      if(!online.contains(v.account)||!same(v,v.player)||!phase(v.player).equals(v.phase)){close(v.player,true);continue;}
      if(v.captureSession!=null&&v.captureSession!=currentSession(v.player)){close(v.player,false);if(dialogue(v.player))run(v.player,"function capitale:pq01/close_self");continue;}
      for(Actor a:new ArrayList<>(v.actors.values()))if(a.motion!=null&&!a.motion.done())a.motion.tick();
      if(VIEWS.get(v.account)!=v)continue;
      if(v.pendingCamera!=null&&clock>=v.cameraAt){Object focus=v.pendingCamera;v.pendingCamera=null;if(dialogue(v.player)){if(!openCamera(v,focus)||VIEWS.get(v.account)!=v)continue;if(v.pendingCamera==null&&v.captureSession!=null){String node=v.pendingNode;v.pendingNode=null;if(node!=null)run(v.player,"function capitale:pq01/node/"+node);}}}
      int node=score(v.player,"hcm_pq1_node");
      if(v.scene&&node>=Pq01Ids.id("ga_menu"))v.scene=false;
      if(v.banter&&(node<Pq01Ids.id("b0")||node>Pq01Ids.id("b7")))v.banter=false;
      if(clock%20==0)work(v);
    }catch(Throwable e){report(e);close(v.player,true);if(online.contains(v.account)&&score(v.player,"hcm_pq1_active")==1)run(v.player,"function capitale:pq01/close_self");}
    if(clock%20==0)for(Object p:players)try{
      if(!available(p)||score(p,"hcq_pq01")<1||score(p,"hcq_pq01")>=100||clock<GRACE.getOrDefault(uuid(p),0L))continue;
      if(integer(setup(p),"ambient_enabled",1)==0)continue;
      View v=VIEWS.get(uuid(p));
      for(String role:List.of("gautier","hugues","anselme","perrin")){
        Object s=source(p,role+"_"+phase(p));if(s==null||pos(s,world(p)).distance2(pos(p,world(p)))>24*24)continue;
        if(v==null)v=view(p);if(!v.actors.containsKey(role))spawn(v,role,configuredPoint(p,role.equals("perrin")?"perrin_a":role.equals("gautier")?"gautier_poste":"__none",null));
      }
    }catch(Throwable e){report(e);close(p,true);}
    GRACE.keySet().retainAll(online);
    NEAR.keySet().retainAll(online);
  }
  static void work(View v){
    Object p=v.player;boolean active=dialogue(p);
    for(Actor a:new ArrayList<>(v.actors.values())){
      Object src=source(p,a.role+"_"+v.phase);
      if(src==null||pos(src,v.world).distance2(pos(p,v.world))>32*32){discard(v,a.role);refresh(p);continue;}
      if((v.scene&&a.role.equals("gautier"))||(v.banter&&(a.role.equals("gautier")||a.role.equals("hugues")))||active&&a.role.equals(v.cameraRole))continue;
      if(a.motion!=null&&!a.motion.done())continue;
      if(clock<a.nextWork)continue;a.nextWork=clock+80;
      if(a.role.equals("hugues")){
        Pq01Motion.Point target=configuredPoint(p,"hugues_charge",null);if(target!=null)face(a,target);
        call(a.entity,"method_6104",en("net.minecraft.class_1268","field_5808"));
      }else if(a.role.equals("anselme")){
        Pq01Motion.Point target=configuredPoint(p,"anselme_instruments",null);if(target!=null)face(a,target);
      }else if(a.role.equals("perrin")){
        Pq01Motion.Point target=configuredPoint(p,a.leg?"perrin_a":"perrin_b",null);
        if(target!=null)travel(v,a,target,()->{a.leg=!a.leg;a.nextWork=clock+120;},reason->{a.nextWork=clock+600;diagnostic(p,"Service de Perrin suspendu : "+reason);});
      }
    }
    if(v.actors.isEmpty()&&!active)VIEWS.remove(v.account);
  }
}
