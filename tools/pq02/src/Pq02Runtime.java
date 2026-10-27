package net.hautecapitale.quetes.sequence;

import java.util.*;import java.util.function.*;import java.lang.reflect.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.class_1297;import net.minecraft.class_3222;import net.minecraft.class_2960;
import net.hautecapitale.quetes.waypoint.ResolvedTarget;import net.hautecapitale.quetes.data.WaypointDef;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import net.hautecapitale.quetes.sequence.Pq01Motion.Point;

/** Extension PQ02 ; aucun état permanent ne dépend du seul UUID du compte. */
public final class Pq02Runtime implements ModInitializer {
 static final String PREFIX="capitale:pq02/",SETUP="capitale:pq02_setup",STATE="capitale:pq02_scene",DM="net.hautecapitale.dialogue.session.DialogueManager",TAG="hcm_pq2_actor";
 static final List<String> ROUTE=List.of("a","b","c","d","e","f","g","h_before","h","h_after","i","j","k","l","m","n","o","p");
 static final Set<String> OPTIONAL=Set.of("h_before","h_after");
 static final Map<UUID,View> VIEWS=new HashMap<>();static final Set<UUID> LIVE=new HashSet<>();static final Map<UUID,Object> DIRTY=new LinkedHashMap<>();
 static final Map<UUID,Set<UUID>> HIDDEN=new HashMap<>();static final Set<String> ERRORS=new HashSet<>();
 static int clock,nonce=new java.security.SecureRandom().nextInt(50_000_000);static boolean initialized,teamReady;
 static final class Actor {final String role;final UUID id,source;final Object entity,goals,targets;Actor(String r,Object e,UUID source,Object g,Object t){role=r;entity=e;id=uuid(e);this.source=source;goals=g;targets=t;}}
 static final class View {
  final Object player,world;final UUID account,character;final Map<String,Actor> actors=new HashMap<>();final Set<UUID> hidden=new HashSet<>();
  Object session,focus,pendingFocus,anchor,questMarker;Pq02Data.Node node;List<Pq02Data.Choice> choices=List.of();List<String> lastLines=List.of();String lastSpeaker="leovic",pendingNode;
  Pq02Motion motion;Point saved;int savedStage=-1,savedWaypoint=-1,waypoint,baseToken,voiceUntil,queuedChoice,frameAt,lastStage;boolean ui,suppress,walking,far,disposed;String failure="";
  View(Object p){player=p;world=world(p);account=uuid(p);character=character(p);waypoint=score(p,"CAP_PQ2_WP");lastStage=stage(p);}
 }
 public void onInitialize(){init();}
 public static void init(){if(initialized)return;initialized=true;
  event("net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback","EVENT",a->{register(a[0]);return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick","END_SERVER_TICK",a->{tick(a[0]);return null;});
  event("net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents$Disconnect","DISCONNECT",a->{Object p=call(a[0],"method_32311");suspend(p);dispose(uuid(p));flush();return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStopping","SERVER_STOPPING",a->{for(View v:new ArrayList<>(VIEWS.values())){pause(v);savePosition(v);closeUi(v);dispose(v.account);}flush();return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStopped","SERVER_STOPPED",a->{VIEWS.clear();LIVE.clear();HIDDEN.clear();DIRTY.clear();ERRORS.clear();NEAR.clear();clock=0;teamReady=false;return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents$Load","ENTITY_LOAD",a->{Object e=a[0];if(tags(e).contains(TAG)&&!LIVE.contains(uuid(e)))call(e,"method_31472");return null;});
  customActions();
 }
 static void report2(Throwable t){String msg=t.toString();if(ERRORS.add(msg)){System.err.println("[Indivis PQ02 1.0] "+msg);t.printStackTrace(System.err);}}
 static void say2(Object p,String s){run(p,"tellraw @s "+quote("[PQ02] "+s));}
 static boolean same(View v){return !v.disposed&&v.character.equals(character(v.player))&&v.world==world(v.player)&&score(v.player,"CAP_PROFILE_LOCK")==0;}
 static boolean ready(Object p){return available(p)&&score(p,"hcq_pq01")>=100;}
 static Object characterScene(Object p,UUID character){return storage(p,STATE+"/"+character);}
 static Object config(Object p){return storage(p,SETUP);}
 static Object roleRecord(Object p,String role){return child(child(config(p),"roles"),role);}
 static void score2(Object p,String name,int value){run(p,"scoreboard players set @s "+name+" "+value);}
 static int stage(Object p){return score(p,"hcq_pq02");}
 static Object current(Object p){return ((Optional<?>)stat(DM,"session",p)).orElse(null);}
 static void register(Object dispatcher){
  Object root=stat("com.mojang.brigadier.builder.LiteralArgumentBuilder","literal","indivispq02");
  for(String action:List.of("leovic","magistrat","near","dispatch","suspend","restore","rewards_ready","status","bind_leovic","bind_magistrat","bind_sentinel","prepare_legacy","clear_scenes")){
   Object node=stat("com.mojang.brigadier.builder.LiteralArgumentBuilder","literal",action);
   boolean admin=action.startsWith("bind_")||Set.of("status","prepare_legacy","clear_scenes").contains(action);
   if(admin)call(node,"requires",stat("net.minecraft.class_2170","method_71774",en("net.minecraft.class_2170","field_31839")));
   Class<?> c=type("com.mojang.brigadier.Command");Object cmd=Proxy.newProxyInstance(c.getClassLoader(),new Class<?>[]{c},(p,m,a)->{if(m.getDeclaringClass()==Object.class)return objectMethod(p,m,a);try{return execute2(call(call(a[0],"getSource"),"method_9207"),action);}catch(Throwable t){report2(t);return 0;}});
   call(node,"executes",cmd);call(root,"then",node);
  }call(dispatcher,"register",root);
 }
 public static int execute2(Object p,String action){
  if(action.equals("status")){diagnostic(p);return 1;}
  if(action.equals("suspend")){suspend(p);return 1;}
  if(action.equals("restore")){dispose(uuid(p));return 1;}
  if(action.equals("rewards_ready")){return (score(p,"CAPSK_REWARD_REGPORT")>0||CoreQuestMarkers.functionPresent(p,"capskills:reward/core/registre_port_self"))&&CoreQuestMarkers.functionPresent(p,"capitale:pq02/gift_once_self")?1:0;}
  if(action.equals("clear_scenes")){suspend(p);dispose(uuid(p));flush();return 1;}
  if(action.equals("prepare_legacy")){prepareLegacy(p);return 1;}
  if(action.startsWith("bind_")){bind(p,action.substring(5));return 1;}
  if(!ready(p))return 0;
  ensure(p);
  if(action.equals("near")){near(p);return 1;}
  if(action.equals("dispatch")){View v=VIEWS.get(uuid(p));return v==null?0:dispatch(v);}
  if(action.equals("leovic")||action.equals("magistrat")){open(p,action);return 1;}return 0;
 }
 static final Map<UUID,Near> NEAR=new HashMap<>();
 record Near(UUID character,Point origin,int at){}
 static void near(Object p){if(!ready(p)||!Pq02Rules.introduction(stage(p))||score(p,"CAP_PQ2_NODE")!=0||current(p)!=null)return;Near n=NEAR.get(uuid(p));if(n==null||!n.character.equals(character(p))){NEAR.put(uuid(p),new Near(character(p),pos(p,world(p)),clock+60));return;}if(clock<n.at||n.origin.distance2(pos(p,world(p)))<0.5625)return;Object src=source(p,"leovic");if(src!=null&&pos(src,world(p)).distance2(pos(p,world(p)))<=9)open(p,"leovic");}
 static void ensure(Object p){
  for(String s:List.of("CAP_PQ2_VER","CAP_PQ2_NODE","CAP_PQ2_DA","CAP_PQ2_DG","CAP_PQ2_DM","CAP_PQ2_DD","CAP_PQ2_IMPR","CAP_PQ2_SENT","CAP_PQ2_ANECD","CAP_PQ2_PORT","CAP_PQ2_STATUS","CAP_PQ2_OFFER","CAP_PQ2_REG","CAP_PQ2_REW","CAP_PQ2_WP","CAP_PQ2_PAUSE","CAP_LEOVIC_GIFT"))run(p,"scoreboard players add @s "+s+" 0");
  if(score(p,"CAP_PQ2_VER")!=0)return;
  int s=stage(p);score2(p,"CAP_PQ2_VER",1);score2(p,"CAP_PQ2_PAUSE",1);
  // Anciens dossiers : aucun recalcul rétroactif de réputation ni récompense rejouée.
  if(s>=100){score2(p,"CAP_PQ2_REG",1);score2(p,"CAP_PQ2_REW",1);if(score(p,"CAP_LEOVIC_GIFT")==0)score2(p,"CAP_PQ2_OFFER",1);for(String d:Pq02Rules.DECISIONS.values())score2(p,d,2);}
  else if(s>=40){score2(p,"CAP_PQ2_DA",2);if(s>=60){score2(p,"CAP_PQ2_REG",1);score2(p,"CAP_PQ2_DM",2);}if(s>=70){score2(p,"CAP_PQ2_WP",14);}else if(s==50||s==60)score2(p,"CAP_PQ2_WP",14);else score2(p,"CAP_PQ2_WP",nearestRouteIndex(p));}
  if(score(p,"CAP_QSEQ")>=510&&score(p,"CAP_QSEQ")<=609){score2(p,"CAP_QSEQ",0);score2(p,"CAP_QSEQ_TIMER",0);}
 }
 static int nearestRouteIndex(Object p){int best=0;double distance=24*24;for(int i=0;i<=14;i++){Object e=marker(p,ROUTE.get(i));if(e!=null){double d=pos(e,world(p)).distance2(pos(p,world(p)));if(d<distance){distance=d;best=i;}}}return best;}
 static Object marker(Object p,String name){return unique(p,"marker","wp_leovic_registre_port_"+name,256, false);}
 @SuppressWarnings("unchecked") static List<Object> local(Object p,Point at,double radius,Predicate<Object> filter){
  Object box=new net.minecraft.class_238(at.x()-radius,at.y()-32,at.z()-radius,at.x()+radius,at.y()+32,at.z()+radius);
  return (List<Object>)call(world(p),"method_8333",p,box,filter);
 }
 static Object unique(Object p,String kind,String tag,double radius,boolean required){
  List<Object> found=local(p,pos(p,world(p)),radius,e->tags(e).contains(tag)&&(kind.equals("marker")?Pq01EntityFactory.registryId(e).equals("minecraft:marker"):type("de.markusbordihn.easynpc.entity.easynpc.EasyNPC").isInstance(e)));
  if(found.size()>1)throw new IllegalStateException("Plusieurs entités portent "+tag+" ; aucune position choisie au hasard");
  if(found.isEmpty()&&required)throw new IllegalStateException("Entité absente/non chargée : "+tag);return found.isEmpty()?null:found.get(0);
 }
 static Object source(Object p,String role){
  Object r=roleRecord(p,role);if(r!=null){if(!dimension(world(p)).equals(str(r,"dimension","")))return null;return call(world(p),"method_66347",UUID.fromString(str(r,"uuid","")));}
  String tag=role.equals("leovic")?"npc_leovic_registre_port":"hcm_pq2_source_magistrat";Object npc=unique(p,"npc",tag,128,false);
  if(npc==null){Point origin=pos(p,world(p));Object m=marker(p,role.equals("leovic")?"a":"m");if(m!=null)origin=pos(m,world(p));String token=role.equals("leovic")?"quest/le_registre_du_port/leovic/":"quest/le_registre_du_port/magistrat/register_self";
   List<Object> found=local(p,origin,16,e->type("de.markusbordihn.easynpc.entity.easynpc.EasyNPC").isInstance(e)&&!tags(e).contains("hcm_private")&&!tags(e).contains(TAG)&&actionText(e).contains(token));
   if(found.size()>1)throw new IllegalStateException("Liaison explicite nécessaire pour "+role+" : plusieurs PNJ compatibles");if(found.size()==1)npc=found.get(0);
  }
  if(npc!=null)saveBinding(p,role,npc);return npc;
 }
 static String actionText(Object e){Object set=call(call(e,"getEasyNPCActionEventData"),"getActionEventSet");return set==null?"":String.valueOf(call(set,"createTag"));}
 static void saveBinding(Object p,String role,Object npc){Object root=Pq01Nbt.copy(config(p)),r=GuardNbt.compound();GuardNbt.string(r,"uuid",uuid(npc).toString());GuardNbt.string(r,"dimension",dimension(world(p)));GuardNbt.string(r,"type",Pq01EntityFactory.registryId(npc));GuardNbt.put(r,"template",GuardNbt.entity(npc));GuardNbt.put(GuardNbt.childOrCreate(root,"roles"),role,r);GuardNbt.save(server(p),SETUP,root);}
 static void bind(Object p,String role){
  List<Object> nearby=local(p,pos(p,world(p)),4,e->type("de.markusbordihn.easynpc.entity.easynpc.EasyNPC").isInstance(e)&&!tags(e).contains("hcm_private")&&!tags(e).contains(TAG));nearby.sort(Comparator.comparingDouble(e->pos(e,world(p)).distance2(pos(p,world(p)))));
  if(nearby.isEmpty())throw new IllegalStateException("Approchez du PNJ existant à moins de quatre blocs");Object npc=nearby.get(0);
  if(nearby.size()>1&&Math.sqrt(pos(nearby.get(1),world(p)).distance2(pos(p,world(p))))-Math.sqrt(pos(npc,world(p)).distance2(pos(p,world(p))))<0.5)throw new IllegalStateException("Sélection ambiguë : rapprochez-vous du PNJ voulu");
  if(role.equals("sentinel")){call(npc,"method_5780","hcm_pq2_sentinel");say2(p,"Sentinelle existante reconnue pour l'observation ; actions et apparence conservées.");return;}
  if(!Set.of("leovic","magistrat").contains(role))throw new IllegalArgumentException(role);if(!VIEWS.isEmpty())throw new IllegalStateException("Attendre la fin des scènes PQ02 avant de changer une liaison");
  saveBinding(p,role,npc);say2(p,"Liaison "+role+" enregistrée, sans déplacer ni modifier le PNJ.");
 }
 static View view(Object p){View old=VIEWS.get(uuid(p));if(old!=null&&!same(old)){dispose(old.account);old=null;}if(old!=null)return old;View v=new View(p);VIEWS.put(v.account,v);HIDDEN.put(v.account,v.hidden);return v;}
 static Actor spawn(View v,String role,Point at){Actor old=v.actors.get(role);if(old!=null&&!Boolean.TRUE.equals(call(old.entity,"method_31481")))return old;
  Object src=source(v.player,role),record=roleRecord(v.player,role);if(record==null)throw new IllegalStateException("PNJ source non reconnu : "+role+" ; effectuer sa liaison près du PNJ existant");
  Object template=Pq01Nbt.copy(child(record,"template"));UUID original=UUID.fromString(str(record,"uuid",""));if(at==null){if(src!=null)at=pos(src,v.world);else {Object m=marker(v.player,role.equals("leovic")?"a":"m");if(m==null)throw new IllegalStateException("Position source non chargée : "+role);at=pos(m,v.world);}}
  for(String field:List.of("UUID","UUIDMost","UUIDLeast","Pos","Motion","Rotation","Passengers","Leash","Brain","Tags","Home","ActionData","ObjectiveData","TradingData"))call(template,"method_10551",field);
  UUID fresh=UUID.randomUUID();stat("de.markusbordihn.easynpc.utils.CompoundTagUtils","writeUUID",template,"UUID",fresh);
  GuardNbt.put(template,"Tags",GuardNbt.list(List.of(TAG,"hcm_pq2_"+role,"hcm_owner_"+v.account,"hcm_character_"+v.character,"indivis_no_hard_despawn").stream().map(GuardNbt::stringValue).toList()));
  call(template,"method_10556","Invulnerable",true);call(template,"method_10556","Silent",true);call(template,"method_10556","NoAI",!role.equals("leovic"));call(template,"method_10556","NoGravity",false);call(template,"method_10556","CanPickUpLoot",false);GuardNbt.string(template,"DeathLootTable","minecraft:empty");
  LIVE.add(fresh);Object e=null,g=null,t=null;try{
   run(v.player,"summon "+str(record,"type","")+" "+at.x()+" "+at.y()+" "+at.z()+" "+template);
   e=call(v.world,"method_66347",fresh);if(e==null||!tags(e).contains(TAG))throw new IllegalStateException("Copie EasyNPC non créée : "+role);
   configureActor(e,role.equals("leovic"));call(e,"method_5665",stat("net.minecraft.class_2561","method_43470",role.equals("leovic")?"Léovic":"Magistrat"));
   g=call(e,"getEntityGoalSelector");t=call(e,"getEntityTargetSelector");Pq01Actors.protect(g);Pq01Actors.protect(t);call(g,"method_35113",(Predicate<Object>)goal->true);call(t,"method_35113",(Predicate<Object>)goal->true);
   Object row=GuardNbt.compound();GuardNbt.string(row,"Type","CUSTOM");GuardNbt.string(row,"Cmd","indivis:pq02 "+role);Pq01Actions.mutate(e,"ON_INTERACTION",GuardNbt.list(List.of(row)));
   if(role.equals("leovic")){run(v.player,"attribute "+fresh+" minecraft:movement_speed base set 0.25");}
   if(!teamReady){run(v.player,"team add indivis_pq02");run(v.player,"team modify indivis_pq02 collisionRule never");teamReady=true;}run(v.player,"team join indivis_pq02 "+fresh);
   Actor actor=new Actor(role,e,original,g,t);v.actors.put(role,actor);v.hidden.add(original);DIRTY.put(v.account,v.player);return actor;
  }catch(Throwable failure){LIVE.remove(fresh);if(g!=null)Pq01Actors.forget(g);if(t!=null)Pq01Actors.forget(t);if(e!=null)call(e,"method_31472");throw failure;}
 }
 static void customActions(){Class<?> contract=type("de.markusbordihn.easynpc.api.action.CustomActionExecutor");Object cb=Proxy.newProxyInstance(contract.getClassLoader(),new Class<?>[]{contract},(proxy,method,args)->{
  if(method.getDeclaringClass()==Object.class)return objectMethod(proxy,method,args);if(!method.getName().equals("execute")||args==null||args.length!=4)return null;
  Object p=call(args[3],"initiator");if(p==null||!(args[2] instanceof List<?> words)||words.size()!=1)return null;String role=String.valueOf(words.get(0));Object npc=call(args[1],"getEntity");View v=VIEWS.get(uuid(p));
  if(v!=null&&same(v)&&v.actors.containsKey(role)&&v.actors.get(role).entity==npc&&pos(npc,v.world).distance2(pos(p,v.world))<=64){pause(v);open(p,role);}return null;});
  stat("de.markusbordihn.easynpc.api.action.ActionRegistry","register",stat("net.minecraft.class_2960","method_60654","indivis:pq02"),cb);
 }
 static Point routePoint(Object p,String key){Object e=marker(p,key);Object record=child(child(config(p),"points"),key);
  if(e!=null){Point point=pos(e,world(p));if(record==null||!point.equals(point(record))){Object root=Pq01Nbt.copy(config(p));GuardNbt.put(GuardNbt.childOrCreate(root,"points"),key,pointNbt(point));GuardNbt.save(server(p),SETUP,root);}return point;}
  if(record!=null){Point point=point(record);if(point.dimension().equals(dimension(world(p))))return point;}return null;
 }
 static Point startPosition(View v){Object saved=characterScene(v.player,v.character);int s=stage(v.player);if(saved!=null&&str(saved,"owner","").equals(v.account.toString())&&integer(saved,"waypoint",-1)==v.waypoint&&integer(saved,"stage",-1)==s){Object n=child(saved,"position");if(n!=null){Point point=point(n);if(point.dimension().equals(dimension(v.world)))return point;}}
  if(s>=40&&s<100){String key=s==50||s==60?"m":s==90?"p":ROUTE.get(Math.max(0,Math.min(17,v.waypoint)));Point at=routePoint(v.player,key);if(at!=null)return at;}return null;
 }
 static void savePosition(View v){Actor a=v.actors.get("leovic");if(a==null)return;Point at=pos(a.entity,v.world);int s=same(v)?stage(v.player):v.lastStage;if(v.saved!=null&&v.saved.distance2(at)<0.01&&v.savedStage==s&&v.savedWaypoint==v.waypoint)return;
  Object root=GuardNbt.compound();GuardNbt.string(root,"owner",v.account.toString());GuardNbt.integer(root,"waypoint",v.waypoint);GuardNbt.integer(root,"stage",s);GuardNbt.put(root,"position",pointNbt(at));GuardNbt.save(server(v.player),STATE+"/"+v.character,root);v.saved=at;v.savedStage=s;v.savedWaypoint=v.waypoint;
 }
 static void validateRoute(Object p){for(String key:ROUTE){if(OPTIONAL.contains(key))continue;if(routePoint(p,key)==null)throw new IllegalStateException("Repère d'escorte absent/non chargé et non mis en cache : "+key+". Les points existants ne sont pas recréés.");}}
 static void open(Object p,String role){
  if(!ready(p))return;ensure(p);int s=stage(p);if(!Set.of("leovic","magistrat").contains(role))return;
  if(role.equals("magistrat")&&s!=50&&s!=60)return;
  source(p,role); // Le mod Dialogue peut avoir ouvert sa capture avant la première liaison.
  View v=view(p);Object other=current(p);
  if(other!=null&&other!=v.session){UUID id=(UUID)Pq01Client.field(other,"pnj");Object r=roleRecord(p,role);if(r==null||!id.toString().equals(str(r,"uuid","")))return;}
  if(score(p,"CAP_QUETEACTIVE")!=0&&score(p,"CAP_QUETEACTIVE")!=4)return;
  Actor guide=spawn(v,"leovic",startPosition(v));
  if(pos(guide.entity,v.world).distance2(pos(p,v.world))>144){say2(p,"L'escorte vous attend à son dernier point du parcours.");return;}
  pause(v);v.lastStage=s;
  Pq02Data.Node remembered=Pq02Data.IDS.get(score(p,"CAP_PQ2_NODE"));String key=remembered!=null&&Pq02Rules.validNode(remembered.key,s)?remembered.key:Pq02Rules.initial(s);
  if(s>=100)key=Pq02Rules.greeting(score(p,"CAP_PQ2_IMPR"));
  if(key.equals("a01")&&score(p,"CAP_PQ2_DA")>0)key=List.of("a02a","a02b","a02c").get(Math.min(3,score(p,"CAP_PQ2_DA"))-1);
  show(v,key);
 }
 static boolean condition(View v,String name){return name==null||name.isEmpty()||switch(name){case "port"->score(v.player,"CAP_PQ2_PORT")>0;case "sentinel"->score(v.player,"CAP_PQ2_SENT")>0;case "anecdote"->score(v.player,"CAP_PQ2_ANECD")>0;case "gift_pending"->stage(v.player)>=100&&score(v.player,"CAP_PQ2_OFFER")==1&&score(v.player,"CAP_LEOVIC_GIFT")==0;default->false;};}
 static String resolvedKey(View v,String key){Object p=v.player;
  if((key.equals("m03a")||key.equals("m03b"))&&score(p,"CAP_PQ2_DM")>0)return "m04"+List.of("a","b","c").get(Math.min(3,score(p,"CAP_PQ2_DM"))-1);
  if(key.equals("t04b")&&score(p,"CAP_PQ2_DG")>0)return "t04b_"+List.of("a","b","c").get(Math.min(3,score(p,"CAP_PQ2_DG"))-1);
  if(key.equals("f05")&&score(p,"CAP_PQ2_STATUS")>0)return "f05_short";
  if(key.equals("f06")&&score(p,"CAP_PQ2_DD")>0)return score(p,"CAP_LEOVIC_GIFT")>0?"f07":score(p,"CAP_PQ2_DD")==4?"f_report":score(p,"CAP_PQ2_DD")==3?"f06c":score(p,"CAP_PQ2_DD")==2?"f06b":"f06a";
  return key;
 }
 static void show(View v,String key){if(!same(v))return;if(key.startsWith("event:")){event2(v,key.substring(6));return;}
  key=resolvedKey(v,key);Pq02Data.Node n=Pq02Data.get(key);int s=stage(v.player);if(!Pq02Rules.validNode(key,s)||!condition(v,n.condition))return;
  pause(v);v.node=n;v.baseToken=0;v.queuedChoice=0;score2(v.player,"CAP_PQ2_NODE",n.id);v.ui=true;
  Object focus=focus(v,n);if(focus==null){v.ui=false;return;}
  v.pendingFocus=focus;v.pendingNode=key;v.frameAt=clock+1;
 }
 static Object focus(View v,Pq02Data.Node n){Actor leo=v.actors.get("leovic");if(leo==null)leo=spawn(v,"leovic",startPosition(v));
  if(n.key.startsWith("m")||n.key.startsWith("t07")){Actor magistrate=spawn(v,"magistrat",null);faceEntity(leo.entity,pos(magistrate.entity,v.world));faceEntity(magistrate.entity,pos(leo.entity,v.world));
   if(n.camera.equals("three")){if(v.anchor==null){Point a=pos(leo.entity,v.world),b=pos(magistrate.entity,v.world);UUID id=UUID.randomUUID();LIVE.add(id);run(v.player,"summon minecraft:armor_stand "+((a.x()+b.x())/2)+" "+((a.y()+b.y())/2+0.8)+" "+((a.z()+b.z())/2)+" {UUID:"+uuidArray(id)+",Marker:1b,Invisible:1b,NoGravity:1b,Invulnerable:1b,Tags:[\"hcm_pq2_actor\",\"hcm_owner_"+v.account+"\",\"hcm_character_"+v.character+"\"]}");v.anchor=call(v.world,"method_66347",id);}if(v.anchor!=null)return v.anchor;}
   return n.camera.equals("magistrat")?magistrate.entity:leo.entity;
  }faceEntity(leo.entity,pos(v.player,v.world));return leo.entity;
 }
 static String uuidArray(UUID id){long a=id.getMostSignificantBits(),b=id.getLeastSignificantBits();return "[I;"+(int)(a>>32)+","+(int)a+","+(int)(b>>32)+","+(int)b+"]";}
 static void faceEntity(Object e,Point to){Point at=pos(e,call(e,"getEntityServerLevel"));float yaw=(float)(Math.toDegrees(Math.atan2(to.z()-at.z(),to.x()-at.x()))-90);call(e,"method_36456",yaw);call(e,"method_5847",yaw);call(e,"method_5636",yaw);}
 static boolean openCamera(View v,Object focus){
  double max=((Number)Pq01Client.field(stat("net.hautecapitale.dialogue.config.DialogueConfig","get"),"distance_ouverture")).doubleValue();
  if(pos(focus,v.world).distance2(pos(v.player,v.world))>max*max)throw new IllegalStateException("Caméra hors de portée : rejoindre le guide avant de reprendre");
  if(v.focus==focus&&v.session!=null&&v.session==current(v.player))return true;
  v.suppress=true;try{v.session=stat(DM,"ouvrir",v.player,focus,en("net.hautecapitale.dialogue.session.DialogueMode","CAPTURE"));v.focus=v.session==null?null:focus;}finally{v.suppress=false;}
  return v.session!=null;
 }
 static void render(View v){if(!same(v)||v.node==null)return;Pq02Data.Node n=v.node;
  v.choices=n.choices.stream().filter(c->condition(v,c.condition)).toList();v.baseToken=1_000_000_000+((++nonce)%60_000_000)*16;
  int advance=n.next.isEmpty()?0:v.baseToken+14;List<String> texts=n.lines;String speaker=n.speaker;
  if(texts.isEmpty()){texts=v.lastLines;speaker=v.lastSpeaker;}else{v.lastLines=texts;v.lastSpeaker=speaker;}
  if(texts.isEmpty()){int s=stage(v.player);String fallback=s>=100?Pq02Rules.greeting(score(v.player,"CAP_PQ2_IMPR")):Pq02Rules.walking(s)?"t01":s==50?"m_menu":s==60?"m07":s==90?"f_menu":"a01";texts=Pq02Data.get(fallback).lines;}
  int voiceTicks=voice(v,n.key,n.lines.isEmpty());v.voiceUntil=clock+voiceTicks;
  run(v.player,"tellraw @s {\"text\":\"\",\"insertion\":"+quote("indivis:pq02:v1:"+n.key+":"+speaker+":"+advance+":0:"+voiceTicks)+"}");
  String name=speaker.equals("magistrat")?"Magistrat":"Léovic";
  for(String text:texts)run(v.player,"tellraw @s [{\"text\":"+quote("["+name+"]")+",\"color\":\"yellow\"},{\"text\":"+quote(" : "+text)+",\"color\":\"white\"}]");
  for(int i=0;i<v.choices.size();i++){Pq02Data.Choice c=v.choices.get(i);run(v.player,"tellraw @s {\"text\":"+quote("["+c.text+"]")+",\"color\":"+quote(c.target.startsWith("event:")||Set.of("a08","m06","f06").contains(c.target)?"green":"white")+",\"click_event\":{\"action\":\"run_command\",\"command\":"+quote("/trigger Pq02Choix set "+(v.baseToken+i+1))+"}}");}
  run(v.player,"tellraw @s {\"text\":\"[Fermer la conversation]\",\"color\":\"gray\",\"click_event\":{\"action\":\"run_command\",\"command\":"+quote("/trigger Pq02Choix set "+(v.baseToken+15))+"}}");
  score2(v.player,"CAP_QDIALOG_OWNER",97);score2(v.player,"CAP_QDIALOG_KEEP",1);score2(v.player,"hcm_pq2_ui",1);score2(v.player,"Pq02Choix",0);run(v.player,"scoreboard players enable @s Pq02Choix");
 }
 static int voice(View v,String key,boolean menu){if(menu)return 0;Object r=child(child(config(v.player),"voices"),key);if(r==null)return 0;String sound=str(r,"sound","");int ticks=integer(r,"ticks",0);if(!sound.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")||ticks<1||ticks>12000)return 0;run(v.player,"playsound "+sound+" voice @s ~ ~ ~ 1 1");return ticks;}
 static int dispatch(View v){Object p=v.player;int token=score(p,"Pq02Choix");score2(p,"Pq02Choix",0);
  if(!same(v)||!v.ui||v.session==null||v.session!=current(p)||v.pendingNode!=null||v.node==null||score(p,"CAP_QDIALOG_OWNER")!=97||!Pq02Rules.validNode(v.node.key,stage(p)))return 0;
  int pick=token-v.baseToken;if(v.baseToken==0||pick<1||pick>15){run(p,"scoreboard players enable @s Pq02Choix");return 0;}
  if(pick==15){closeUi(v);if(Pq02Rules.walking(stage(p)))resume(v);else if(stage(p)>=100||Pq02Rules.introduction(stage(p)))dispose(v.account);return 1;}
  if(clock<v.voiceUntil){v.queuedChoice=token;run(p,"scoreboard players enable @s Pq02Choix");return 1;}
  if(pick==14){if(v.node.next.isEmpty())return 0;v.baseToken=0;show(v,v.node.next);retryFrame(v);return 1;}
  if(pick>v.choices.size())return 0;Pq02Data.Choice choice=v.choices.get(pick-1);if(!condition(v,choice.condition))return 0;
  if(!effect(v,choice.effect))return 0;v.baseToken=0;show(v,choice.target);retryFrame(v);return 1;
 }
 static void retryFrame(View v){if(v.ui&&v.session==current(v.player)&&v.pendingNode==null&&v.baseToken==0)render(v);}
 static void addSigned(Object p,String objective,int delta){if(delta!=0)run(p,"scoreboard players "+(delta<0?"remove":"add")+" @s "+objective+" "+Math.abs(delta));}
 static boolean effect(View v,String effect){if(effect==null||effect.isEmpty())return true;Object p=v.player;
  if(effect.equals("anecdote")){score2(p,"CAP_PQ2_ANECD",1);return true;}if(effect.equals("status_known")){score2(p,"CAP_PQ2_STATUS",1);return true;}if(effect.equals("subject_guard"))return true;
  String[] words=effect.split(":");String key=Pq02Rules.DECISIONS.get(words[0]);if(key==null)return false;int value=Integer.parseInt(words[1]);if(score(p,key)!=0)return false;
  Pq02Rules.Delta d=Pq02Rules.delta(words[0],value);score2(p,key,value);if(d.reputation()!=0){addSigned(p,"REP_GARDEPORT",d.reputation());run(p,"function capitale:bounds/reputation_all_self");}addSigned(p,"CAP_PQ2_IMPR",d.impression());if(words[0].equals("gift"))score2(p,"CAP_PQ2_OFFER",1);return true;
 }
 static void event2(View v,String name){Object p=v.player;if(!same(v))return;
  switch(name){
   case "rest"->{score2(p,"CAP_PQ2_NODE",Pq02Data.get("a08").id);closeUi(v);dispose(v.account);}
   case "start_walk"->{validateRoute(p);closeUi(v);run(p,"function hc_quetes:migration/write/pq02/v20");score2(p,"QUEST_PROLOGUE",20);score2(p,"CAP_QUETEACTIVE",4);v.waypoint=0;score2(p,"CAP_PQ2_WP",0);score2(p,"CAP_PQ2_NODE",Pq02Data.get("t_menu").id);resume(v);}
   case "resume_walk"->{closeUi(v);score2(p,"CAP_PQ2_NODE",Pq02Data.get("t_menu").id);resume(v);}
   case "register"->{run(p,"function capitale:pq02/register_once_self");show(v,"m06_registered");}
   case "elevator"->{closeUi(v);run(p,"function hc_quetes:migration/write/pq02/v50");score2(p,"CAP_QUETEACTIVE",4);v.waypoint=14;score2(p,"CAP_PQ2_WP",14);score2(p,"CAP_PQ2_NODE",Pq02Data.get("t_menu").id);resume(v);}
   case "practical_menu"->show(v,stage(p)>=100?"post_menu":"f_questions");
   case "gift"->{run(p,"function capitale:pq02/gift_once_self");if(score(p,"CAP_LEOVIC_GIFT")==1)show(v,"f07");}
   case "late_gift"->{run(p,"function capitale:pq02/gift_once_self");show(v,"post_menu");}
   case "complete"->{run(p,"function capitale:pq02/complete_once_self");if(stage(p)>=100){closeUi(v);dispose(v.account);}}
   default->throw new IllegalArgumentException(name);
  }
 }
 static void stopActor(Actor a){if(a==null)return;Object nav=call(a.entity,"getGroundPathNavigation");if(nav!=null)call(nav,"method_6340");call(a.entity,"method_18799",en("net.minecraft.class_243","field_1353"));}
 static void pause(View v){if(v.motion!=null){v.motion.pause();v.motion=null;}stopActor(v.actors.get("leovic"));v.walking=false;if(same(v))score2(v.player,"CAP_PQ2_PAUSE",1);savePosition(v);}
 static void resume(View v){if(!same(v)||!Pq02Rules.walking(stage(v.player)))return;v.walking=true;v.far=false;v.failure="";score2(v.player,"CAP_PQ2_PAUSE",0);nextLeg(v);}
 static void nextLeg(View v){if(!v.walking||!same(v)||v.ui)return;Actor a=v.actors.get("leovic");if(a==null)a=spawn(v,"leovic",startPosition(v));final Actor actor=a;int end=stage(v.player)==40?14:17;
  if(v.waypoint>=end){arrivedPhase(v);return;}int index=v.waypoint+1;Point goal=null;
  while(index<=end){goal=routePoint(v.player,ROUTE.get(index));if(goal!=null||!OPTIONAL.contains(ROUTE.get(index)))break;index++;}
  if(goal==null){v.walking=false;score2(v.player,"CAP_PQ2_PAUSE",1);v.failure="Repère non chargé : "+ROUTE.get(Math.min(index,end));return;}
  if(pos(actor.entity,v.world).distance2(pos(v.player,v.world))>18*18){v.far=true;return;}
  final int target=index;Point point=goal;
  v.motion=new Pq02Motion(new Pq02Motion.Port(){
   public boolean valid(){return same(v)&&v.walking&&!v.ui&&!Boolean.TRUE.equals(call(actor.entity,"method_31481"));}
   public Point position(){return pos(actor.entity,v.world);}
   public boolean navigate(Point p){Object nav=call(actor.entity,"getGroundPathNavigation");if(nav==null)return false;
    if(Boolean.TRUE.equals(call(nav,"method_58160",p.x(),p.y(),p.z(),0,0.8d)))return true;
    return Math.abs(p.y()-Math.ceil(p.y()))>0.01&&Boolean.TRUE.equals(call(nav,"method_58160",p.x(),Math.ceil(p.y()),p.z(),0,0.8d));
   }
   public boolean navigating(){Object nav=call(actor.entity,"getGroundPathNavigation");return nav!=null&&!Boolean.TRUE.equals(call(nav,"method_6357"));}
   public void stop(){stopActor(actor);}
   public void arrived(){if(!same(v))return;v.motion=null;v.waypoint=target;score2(v.player,"CAP_PQ2_WP",target);v.saved=null;savePosition(v);if(target==1&&score(v.player,"CAP_PQ2_PORT")==0){score2(v.player,"CAP_PQ2_PORT",1);remark(v,"t01");}if(target>=end)arrivedPhase(v);}
   public void failed(String reason){v.motion=null;v.walking=false;v.failure=reason;if(same(v))score2(v.player,"CAP_PQ2_PAUSE",1);savePosition(v);report2(new IllegalStateException("Trajet PQ02 "+v.character+" point "+ROUTE.get(target)+" : "+reason));}
  },point,integer(config(v.player),"move_timeout_ticks",2400));v.motion.start();
 }
 static void arrivedPhase(View v){int s=stage(v.player);pause(v);score2(v.player,"CAP_QUETEACTIVE",0);score2(v.player,"CAP_GUIDE_LOCK",0);score2(v.player,"CAP_GUIDE_ID",0);
  if(s==40){run(v.player,"function hc_quetes:migration/write/pq02/v30");v.lastStage=50;score2(v.player,"CAP_PQ2_NODE",Pq02Data.get("t07").id);show(v,"t07");}
  else if(s==70){run(v.player,"function hc_quetes:migration/write/pq02/v60");v.lastStage=90;score2(v.player,"CAP_PQ2_NODE",Pq02Data.get("f_menu").id);show(v,"f_menu");}
 }
 static void remark(View v,String key){Pq02Data.Node n=Pq02Data.get(key);for(String text:n.lines)run(v.player,"tellraw @s [{\"text\":\"[Léovic]\",\"color\":\"yellow\"},{\"text\":"+quote(" : "+text)+",\"color\":\"white\"}]");voice(v,key,false);}
 static void observe(View v){if(v.ui||!v.walking||score(v.player,"CAP_PQ2_SENT")!=0)return;
  List<Object> found=local(v.player,pos(v.player,v.world),12,e->type("de.markusbordihn.easynpc.entity.easynpc.EasyNPC").isInstance(e)&&!tags(e).contains("hcm_private")&&!tags(e).contains(TAG)&&(tags(e).contains("hcm_pq2_sentinel")||tags(e).stream().anyMatch(t->t.startsWith("hcq_pnj_sentinelle_archives")||t.startsWith("hcm_guard_archives"))));
  double yaw=Math.toRadians(num(call(v.player,"method_36454"))),lookX=-Math.sin(yaw),lookZ=Math.cos(yaw);Point p=pos(v.player,v.world);
  for(Object npc:found){Point at=pos(npc,v.world);if((at.x()-p.x())*lookX+(at.z()-p.z())*lookZ<0)continue;if(Boolean.TRUE.equals(call(npc,"method_5756",v.player))||!Boolean.TRUE.equals(stat("net.hautecapitale.quetes.visibility.QuestVisibility","isVisibleTo",npc,v.player))||!Boolean.TRUE.equals(call(v.player,"method_6057",npc)))continue;score2(v.player,"CAP_PQ2_SENT",1);remark(v,"t_sentinel_observed");return;}
 }
 static void closeUi(View v){v.suppress=true;try{if(v.session!=null&&v.session==current(v.player))stat(DM,"fermer",v.player,en("net.hautecapitale.dialogue.session.CloseReason","ECRAN_FERME"));}finally{v.suppress=false;}
  v.session=null;v.focus=null;v.pendingFocus=null;v.pendingNode=null;v.ui=false;v.baseToken=0;v.queuedChoice=0;
  if(same(v)){score2(v.player,"hcm_pq2_ui",0);score2(v.player,"Pq02Choix",0);if(score(v.player,"CAP_QDIALOG_OWNER")==97){score2(v.player,"CAP_QDIALOG_OWNER",0);score2(v.player,"CAP_QDIALOG_KEEP",0);}}
 }
 /** Le hook reçoit la véritable raison b10 : aucune reprise sur mort, remplacement ou déconnexion. */
 public static void onClosing(Object player,Object session,String reason){View v=VIEWS.get(uuid(player));if(v==null||v.suppress||v.session!=session)return;
  v.session=null;v.focus=null;v.pendingFocus=null;v.pendingNode=null;v.ui=false;v.baseToken=0;v.queuedChoice=0;
  if(!same(v))return;score2(player,"hcm_pq2_ui",0);score2(player,"Pq02Choix",0);if(score(player,"CAP_QDIALOG_OWNER")==97){score2(player,"CAP_QDIALOG_OWNER",0);score2(player,"CAP_QDIALOG_KEEP",0);}
  if(reason.equals("ECRAN_FERME")&&Pq02Rules.walking(stage(player)))resume(v);else{pause(v);if(stage(player)>=100||Pq02Rules.introduction(stage(player)))dispose(v.account);}
 }
 public static void suspend(Object p){View v=VIEWS.get(uuid(p));if(v==null)return;pause(v);savePosition(v);closeUi(v);dispose(v.account);}
 static void dispose(UUID account){View v=VIEWS.remove(account);if(v==null)return;v.disposed=true;v.walking=false;if(v.motion!=null)v.motion.pause();
  for(Actor a:v.actors.values()){stopActor(a);Pq01Actors.forget(a.goals);Pq01Actors.forget(a.targets);LIVE.remove(a.id);call(a.entity,"method_31472");}
  v.actors.clear();if(v.anchor!=null){LIVE.remove(uuid(v.anchor));call(v.anchor,"method_31472");}if(v.questMarker!=null){LIVE.remove(uuid(v.questMarker));call(v.questMarker,"method_31472");}HIDDEN.remove(account);DIRTY.put(account,v.player);
 }
 public static boolean visibleOriginal(class_1297 entity,class_3222 player){
  if(entity.method_5752().contains(TAG)){View v=VIEWS.get(player.method_5667());return v!=null&&same(v)&&LIVE.contains(entity.method_5667())&&entity.method_5752().contains("hcm_owner_"+v.account)&&entity.method_5752().contains("hcm_character_"+v.character);}
  Set<UUID> hidden=HIDDEN.get(player.method_5667());return hidden==null||!hidden.contains(entity.method_5667());
 }
 public static Optional<ResolvedTarget> waypoint(Object player,String tag){
  if(!Set.of("hcm_pq2_leovic","hcm_pq2_magistrat").contains(tag))return Optional.empty();View v=VIEWS.get(uuid(player));if(v==null||!same(v))return Optional.empty();Actor a=v.actors.get(tag.endsWith("magistrat")?"magistrat":"leovic");if(a==null)return Optional.empty();Point at=pos(a.entity,v.world);
  return Optional.of(new ResolvedTarget(WaypointDef.Mode.EXACT,class_2960.method_60654(at.dimension()),at.x(),at.y(),at.z(),0,tag.endsWith("magistrat")?"Magistrat du Port":"Léovic"));
 }
 static void updateMarker(View v){int s=stage(v.player);boolean useful=s>=40&&s<100&&same(v)&&v.actors.containsKey("leovic");
  if(!useful){if(v.questMarker!=null){LIVE.remove(uuid(v.questMarker));call(v.questMarker,"method_31472");v.questMarker=null;}return;}
  Actor target=s==50&&v.actors.containsKey("magistrat")?v.actors.get("magistrat"):v.actors.get("leovic");Point at=pos(target.entity,v.world);Point above=new Point(at.dimension(),at.x(),at.y()+3.05,at.z());
  if(v.questMarker==null){UUID id=UUID.randomUUID();LIVE.add(id);run(v.player,"summon minecraft:text_display "+above.x()+" "+above.y()+" "+above.z()+" {UUID:"+uuidArray(id)+",Tags:[\"hcm_pq2_actor\",\"hcm_owner_"+v.account+"\",\"hcm_character_"+v.character+"\"],text:{text:\"?\",color:\"yellow\",bold:true},billboard:\"center\",background:0,see_through:1b,brightness:{sky:15,block:15}}");v.questMarker=call(v.world,"method_66347",id);}
  else if(pos(v.questMarker,v.world).distance2(above)>0.0025)call(v.questMarker,"method_5814",above.x(),above.y(),above.z());
 }
 static void tick(Object server){clock++;@SuppressWarnings("unchecked") List<Object> players=(List<Object>)call(call(server,"method_3760"),"method_14571");Set<UUID> online=new HashSet<>();for(Object p:players)online.add(uuid(p));
  for(View v:new ArrayList<>(VIEWS.values()))try{
   if(!online.contains(v.account)||!same(v)||!Boolean.TRUE.equals(call(v.player,"method_5805"))){if(same(v))pause(v);savePosition(v);closeUi(v);dispose(v.account);continue;}
   v.lastStage=stage(v.player);
   if(v.pendingNode!=null&&clock>=v.frameAt){Object focus=v.pendingFocus;if(openCamera(v,focus)){v.pendingFocus=null;v.pendingNode=null;render(v);}else{v.pendingFocus=null;v.pendingNode=null;v.ui=false;pause(v);}}
   if(v.ui&&v.session!=null&&v.session!=current(v.player)){onClosing(v.player,v.session,"REMPLACEE");continue;}
   if(v.ui){int pick=score(v.player,"Pq02Choix");if(pick!=0)dispatch(v);else if(v.queuedChoice!=0&&clock>=v.voiceUntil){int value=v.queuedChoice;v.queuedChoice=0;score2(v.player,"Pq02Choix",value);dispatch(v);}if(clock%20==0&&v.session!=null)call(v.session,"toucher",((Number)call(call(v.world,"method_8503"),"method_3780")).longValue());}
   if(v.walking&&!v.ui){Actor a=v.actors.get("leovic");if(a!=null&&pos(a.entity,v.world).distance2(pos(v.player,v.world))>18*18){if(v.motion!=null){v.motion.pause();v.motion=null;}v.far=true;}else{v.far=false;if(v.motion!=null)v.motion.tick();else nextLeg(v);}}
   if(clock%20==0){savePosition(v);observe(v);updateMarker(v);}
  }catch(Throwable t){report2(t);pause(v);closeUi(v);v.failure=t.getMessage()==null?t.toString():t.getMessage();}
  // Restauration d'un parcours existant ; aucun don ou dialogue automatique sur chargement.
  if(clock%20==0)for(Object p:players)try{if(!ready(p)||VIEWS.containsKey(uuid(p)))continue;int s=stage(p);if(s!=40&&s!=50&&s!=60&&s!=70&&s!=90)continue;ensure(p);View v=view(p);spawn(v,"leovic",startPosition(v));if(Pq02Rules.walking(s)&&score(p,"CAP_PQ2_PAUSE")==0)resume(v);}catch(Throwable t){report2(t);dispose(uuid(p));}
  flush();
 }
 static void flush(){if(DIRTY.isEmpty())return;for(UUID id:new HashSet<>(DIRTY.keySet())){Object p=DIRTY.get(id);if(p==null)continue;stat("net.hautecapitale.quetes.visibility.QuestVisibility","refresh",p);}DIRTY.clear();}
 static void prepareLegacy(Object p){Object a=marker(p,"a");if(a==null)throw new IllegalStateException("Charger le point A existant avant de préparer le guide historique");
  List<Object> guides=local(p,pos(a,world(p)),256,e->Pq01EntityFactory.registryId(e).equals("minecraft:armor_stand")&&tags(e).contains("guide_leovic_registre_port"));
  if(guides.size()>1)throw new IllegalStateException("Plusieurs guides historiques chargés ; aucune sélection arbitraire");
  if(guides.size()==1){Object guide=guides.get(0);for(String tag:List.of("escort_active","escort_returning","escort_moving"))call(guide,"method_5738",tag);Point at=pos(a,world(p));call(guide,"method_5814",at.x(),at.y(),at.z());}
  for(String key:ROUTE)routePoint(p,key);say2(p,guides.isEmpty()?"Aucun guide historique chargé. Repères existants mis en cache, sans création ni progression.":"Guide historique stationné sur le point A existant ; aucune progression ni récompense attribuée.");
 }
 static void diagnostic(Object p){say2(p,"Personnage="+character(p)+", verrou="+score(p,"CAP_PROFILE_LOCK")+", PQ01="+score(p,"hcq_pq01")+", PQ02="+stage(p)+", nœud="+score(p,"CAP_PQ2_NODE")+", point="+score(p,"CAP_PQ2_WP")+", pause="+score(p,"CAP_PQ2_PAUSE")+".");
  say2(p,"Décisions A/T/M/F="+score(p,"CAP_PQ2_DA")+"/"+score(p,"CAP_PQ2_DG")+"/"+score(p,"CAP_PQ2_DM")+"/"+score(p,"CAP_PQ2_DD")+", impression="+score(p,"CAP_PQ2_IMPR")+", don="+score(p,"CAP_LEOVIC_GIFT")+", offre="+score(p,"CAP_PQ2_OFFER")+".");
  List<String> missing=new ArrayList<>();for(String key:ROUTE)if(!OPTIONAL.contains(key)&&routePoint(p,key)==null)missing.add(key);say2(p,"Repères manquants/non chargés : "+missing+" ; sources configurées="+(child(config(p),"roles")!=null)+".");
  View v=VIEWS.get(uuid(p));if(v!=null)say2(p,"Acteurs="+v.actors.size()+", UI="+v.ui+", marche="+v.walking+", attente joueur="+v.far+", anomalie="+v.failure+".");
 }
}
