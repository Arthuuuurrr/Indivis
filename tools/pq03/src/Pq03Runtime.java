package net.hautecapitale.quetes.sequence;

import java.util.*;import java.util.function.*;import java.lang.reflect.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.class_1297;import net.minecraft.class_3222;import net.minecraft.class_2960;
import net.hautecapitale.quetes.waypoint.ResolvedTarget;import net.hautecapitale.quetes.data.WaypointDef;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import net.hautecapitale.quetes.sequence.Pq01Motion.Point;

/** Extension PQ03 ; aucun état permanent ne dépend du seul UUID du compte. */
public final class Pq03Runtime implements ModInitializer {
 static final String PREFIX="capitale:pq03/",SETUP="capitale:pq03_setup",STATE="capitale:pq03_scene",DM="net.hautecapitale.dialogue.session.DialogueManager",TAG="hcm_pq3_actor";
 static final List<String> ROUTE=List.of("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v");
 static final Set<String> OPTIONAL=Set.of();
 static final Map<UUID,View> VIEWS=new HashMap<>();static final Set<UUID> LIVE=new HashSet<>();static final Map<UUID,Object> DIRTY=new LinkedHashMap<>();
 static final Map<UUID,Set<UUID>> HIDDEN=new HashMap<>();static final Set<String> ERRORS=new HashSet<>();
 static int clock,nonce=new java.security.SecureRandom().nextInt(50_000_000);static boolean initialized,teamReady;
 static final class Actor {final String role;final UUID id,source;final Object entity,goals,targets;Actor(String r,Object e,UUID source,Object g,Object t){role=r;entity=e;id=uuid(e);this.source=source;goals=g;targets=t;}}
 static final class View {
  final Object player,world;final UUID account,character;final Map<String,Actor> actors=new HashMap<>();final Set<UUID> hidden=new HashSet<>();
  Object session,focus,pendingFocus,anchor,questMarker,acknowledgedSession;int focusToken,focusSince,focusRetry;Pq03Data.Node node;List<Pq03Data.Choice> choices=List.of();List<String> lastLines=List.of();String lastSpeaker="aurele",pendingNode;
  Pq02Motion motion;Point saved;int savedStage=-1,savedWaypoint=-1,savedLore=-2,lorePending=-1,loreCueAt,waypoint,baseToken,voiceUntil,queuedChoice,frameAt,lastStage,waitCueAt,finalizeAt;boolean ui,suppress,walking,far,disposed,retiring;String failure="";
  View(Object p){player=p;world=world(p);account=uuid(p);character=character(p);waypoint=score(p,"CAP_PQ3_WP");lastStage=stage(p);
   Object record=characterScene(p,character);if(record!=null&&str(record,"owner","").equals(account.toString())&&integer(record,"stage",-1)==lastStage&&integer(record,"waypoint",-1)==waypoint&&score(p,"CAP_PQ3_PAUSE")==1){int pending=integer(record,"pending_lore",-1);if(Set.of(3,6,13,18).contains(pending)&&pending==waypoint)lorePending=pending;}
  }
 }
 public void onInitialize(){init();}
 public static void init(){if(initialized)return;initialized=true;
  event("net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback","EVENT",a->{register(a[0]);return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick","END_SERVER_TICK",a->{tick(a[0]);return null;});
  event("net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents$Disconnect","DISCONNECT",a->{Object p=call(a[0],"method_32311");suspend(p);dispose(uuid(p));flush();return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStopping","SERVER_STOPPING",a->{Pq03Entries.clear();for(View v:new ArrayList<>(VIEWS.values()))suspend(v.player);flush();return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStopped","SERVER_STOPPED",a->{Pq03Entries.clear();VIEWS.clear();LIVE.clear();HIDDEN.clear();DIRTY.clear();ERRORS.clear();NEAR.clear();clock=0;teamReady=false;return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents$Load","ENTITY_LOAD",a->{Object e=a[0];if(tags(e).contains(TAG)&&!LIVE.contains(uuid(e)))call(e,"method_31472");return null;});
  customActions();
 }
 static void report2(Throwable t){String msg=t.toString();if(ERRORS.add(msg)){System.err.println("[Indivis PQ03 1.0] "+msg);t.printStackTrace(System.err);}}
 static void say2(Object p,String s){run(p,"tellraw @s "+quote("[PQ03] "+s));}
 static boolean same(View v){return !v.disposed&&v.character.equals(character(v.player))&&v.world==world(v.player)&&score(v.player,"CAP_PROFILE_LOCK")==0;}
 static boolean ready(Object p){return available(p)&&score(p,"hcq_pq02")>=100;}
 static Object characterScene(Object p,UUID character){return storage(p,STATE+"/"+character);}
 static Object config(Object p){return storage(p,SETUP);}
 static Object roleRecord(Object p,String role){return child(child(config(p),"roles"),role);}
 static void score2(Object p,String name,int value){run(p,"scoreboard players set @s "+name+" "+value);}
 static int stage(Object p){return score(p,"hcq_pq03");}
 static Object current(Object p){return ((Optional<?>)stat(DM,"session",p)).orElse(null);}
 static void register(Object dispatcher){
  Object root=stat("com.mojang.brigadier.builder.LiteralArgumentBuilder","literal","indivispq03");
  for(String action:List.of("aurele","near","dispatch","pause","suspend","restore","rewards_ready","reputation_ready","guide_valid","status","points","bind_aurele","prepare_route","clear_scenes","rumble_on","rumble_off")){
   Object node=stat("com.mojang.brigadier.builder.LiteralArgumentBuilder","literal",action);
   if(Set.of("status","points","bind_aurele","prepare_route","clear_scenes","rumble_on","rumble_off").contains(action))call(node,"requires",stat("net.minecraft.class_2170","method_71774",en("net.minecraft.class_2170","field_31839")));
   Class<?> c=type("com.mojang.brigadier.Command");Object cmd=Proxy.newProxyInstance(c.getClassLoader(),new Class<?>[]{c},(p,m,a)->{if(m.getDeclaringClass()==Object.class)return objectMethod(p,m,a);try{return execute2(call(call(a[0],"getSource"),"method_9207"),action);}catch(Throwable t){report2(t);return 0;}});call(node,"executes",cmd);call(root,"then",node);
  }
  Object ready=stat("com.mojang.brigadier.builder.LiteralArgumentBuilder","literal","ready"),token=stat("com.mojang.brigadier.builder.RequiredArgumentBuilder","argument","token",stat("com.mojang.brigadier.arguments.IntegerArgumentType","integer",1));
  Class<?> command=type("com.mojang.brigadier.Command");Object ack=Proxy.newProxyInstance(command.getClassLoader(),new Class<?>[]{command},(p,m,a)->{if(m.getDeclaringClass()==Object.class)return objectMethod(p,m,a);try{return acknowledgeFocus(call(call(a[0],"getSource"),"method_9207"),((Number)stat("com.mojang.brigadier.arguments.IntegerArgumentType","getInteger",a[0],"token")).intValue());}catch(Throwable t){report2(t);return 0;}});call(token,"executes",ack);call(ready,"then",token);call(root,"then",ready);call(dispatcher,"register",root);
 }
 public static int execute2(Object p,String action){
  if(action.equals("status")){diagnostic(p);return 1;}
  if(action.equals("suspend")){suspend(p);return 1;}
  if(action.equals("pause")){View v=VIEWS.get(uuid(p));if(v!=null&&same(v))pause(v);else if(ready(p)&&Pq03Rules.walking(stage(p)))score2(p,"CAP_PQ3_PAUSE",1);return 1;}
  if(action.equals("restore")){dispose(uuid(p));return 1;}
  if(action.equals("rewards_ready"))return CoreQuestMarkers.functionPresent(p,"capskills:reward/core/sous_regard_coeur_self")&&CoreQuestMarkers.functionPresent(p,"capitale:pq03/reputation/instant_ready_self")?1:0;
  if(action.equals("reputation_ready"))return score(p,"CAP_PQ3_VER")>=2&&score(p,"CAP_PQ3_RB")==1&&(score(p,"CAP_PQ3_DA")==0||score(p,"CAP_PQ3_RA")==1)?1:0;
  if(action.equals("guide_valid"))return ready(p)&&Set.of(30,60).contains(stage(p))&&score(p,"CAP_PQ3_VER")>0?1:0;
  if(action.equals("clear_scenes")){suspend(p);dispose(uuid(p));flush();return 1;}
  if(action.equals("prepare_route"))return Pq03Setup.prepare(p);
  if(action.equals("points")){Pq03Setup.describe(p);return 1;}
  if(action.equals("bind_aurele")){bind(p,"aurele");return 1;}
  if(action.equals("rumble_on")||action.equals("rumble_off")){Object root=Pq01Nbt.copy(config(p));call(root,"method_10556","rumble_at_d",action.equals("rumble_on"));GuardNbt.save(server(p),SETUP,root);say2(p,"Grondement audible à D : "+action.equals("rumble_on")+". Aucun son ambiant ajouté.");return 1;}
  return Pq03Entries.handles(action)?Pq03Entries.submit(p,action):0;
 }
 static final Map<UUID,Near> NEAR=new HashMap<>();
 record Near(UUID character,Point origin,int at){}
 static void near(Object p){if(!ready(p)||!Pq03Rules.introduction(stage(p))||current(p)!=null)return;Near n=NEAR.get(uuid(p));if(n==null||!n.character.equals(character(p))){NEAR.put(uuid(p),new Near(character(p),pos(p,world(p)),clock+60));return;}if(clock<n.at||n.origin.distance2(pos(p,world(p)))<0.5625)return;Object src=source(p,"aurele");if(src!=null&&pos(src,world(p)).distance2(pos(p,world(p)))<=9)open(p,"aurele");}
 static void ensure(Object p){
  for(String name:Pq03Rules.SCORES)run(p,"scoreboard players add @s "+name+" 0");
  if(score(p,"CAP_PQ3_VER")>=2)return;
  if(score(p,"CAP_PQ3_VER")==1){migrateReputation(p);return;}
  int phase=stage(p);score2(p,"CAP_PQ3_VER",1);score2(p,"CAP_PQ3_PAUSE",1);
  if(phase>=100){score2(p,"CAP_PQ3_REW",1);score2(p,"CAP_PQ3_DA",2);score2(p,"CAP_PQ3_DB",2);}
  else if(phase==30||phase==60){score2(p,"CAP_PQ3_WP",nearestRouteIndex(p));score2(p,"CAP_PQ3_BRANCH",phase==30?1:2);}
  else if(phase==40)score2(p,"CAP_PQ3_WP",8);else if(phase==50)score2(p,"CAP_PQ3_WP",0);else if(phase==90)score2(p,"CAP_PQ3_WP",21);
  if(score(p,"CAP_QSEQ")>=701&&score(p,"CAP_QSEQ")<=749){score2(p,"CAP_QSEQ",0);score2(p,"CAP_QSEQ_TIMER",0);}
  migrateReputation(p);
 }
 static void migrateReputation(Object p){
  if(stage(p)>=100||score(p,"CAP_PQ3_REW")>0){score2(p,"CAP_PQ3_RA",1);score2(p,"CAP_PQ3_RB",1);}
  else{applyDecision(p,false);applyDecision(p,true);}
  score2(p,"CAP_PQ3_VER",2);
 }
 static void applyDecision(Object p,boolean separation){
  String decision=separation?"CAP_PQ3_DB":"CAP_PQ3_DA",paid=separation?"CAP_PQ3_RB":"CAP_PQ3_RA";
  int value=score(p,decision);if(value<1||value>(separation?4:3)||score(p,paid)>0)return;
  int delta=Pq03Rules.decisionDelta(separation,value);score2(p,paid,1);
  if(delta==0)return;int before=score(p,"REP_GARDECOEUR");
  addSigned(p,"REP_GARDECOEUR",delta);run(p,"function capitale:bounds/reputation_all_self");addSigned(p,"CAP_PQ3_IMPR",delta);
  QuestReputationNotice.changed(p,"coeur",before,score(p,"REP_GARDECOEUR"));
 }
 static int nearestRouteIndex(Object p){int best=0;double distance=16*16;for(int i:Pq03Rules.route(stage(p))){Point at=routePoint(p,ROUTE.get(i));if(at!=null){double d=at.distance2(pos(p,world(p)));if(d<distance){distance=d;best=i;}}}return best;}
 static Object marker(Object p,String name){return unique(p,"marker","wp_aurele_coeur_"+name,256,false);}
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
  Object record=roleRecord(p,"aurele");if(record!=null){if(!dimension(world(p)).equals(str(record,"dimension","")))return null;return call(world(p),"method_66347",UUID.fromString(str(record,"uuid","")));}
  Object npc=unique(p,"npc","npc_aurele_veyrane",128,false);
  if(npc==null){List<Object> found=local(p,pos(p,world(p)),32,e->type("de.markusbordihn.easynpc.entity.easynpc.EasyNPC").isInstance(e)&&!tags(e).contains("hcm_private")&&!tags(e).contains(TAG)&&actionText(e).contains("quest/sous_le_regard_du_coeur/aurele/"));if(found.size()>1)throw new IllegalStateException("Liaison Aurèle nécessaire : plusieurs PNJ compatibles");if(found.size()==1)npc=found.get(0);}
  if(npc!=null)saveBinding(p,"aurele",npc);return npc;
 }
 static String actionText(Object e){Object set=call(call(e,"getEasyNPCActionEventData"),"getActionEventSet");return set==null?"":String.valueOf(call(set,"createTag"));}
 static void saveBinding(Object p,String role,Object npc){Object root=Pq01Nbt.copy(config(p)),r=GuardNbt.compound();GuardNbt.string(r,"uuid",uuid(npc).toString());GuardNbt.string(r,"dimension",dimension(world(p)));GuardNbt.string(r,"type",Pq01EntityFactory.registryId(npc));GuardNbt.put(r,"template",GuardNbt.entity(npc));GuardNbt.put(GuardNbt.childOrCreate(root,"roles"),role,r);GuardNbt.save(server(p),SETUP,root);}
 static void bind(Object p,String role){
  if(!VIEWS.isEmpty())throw new IllegalStateException("Attendre la fin des scènes PQ03 avant de changer la liaison");
  List<Object> found=local(p,pos(p,world(p)),4,e->type("de.markusbordihn.easynpc.entity.easynpc.EasyNPC").isInstance(e)&&!tags(e).contains(TAG)&&!tags(e).contains("hcm_private"));found.sort(Comparator.comparingDouble(e->pos(e,world(p)).distance2(pos(p,world(p)))));
  if(found.isEmpty()||found.size()>1&&Math.sqrt(pos(found.get(1),world(p)).distance2(pos(p,world(p))))-Math.sqrt(pos(found.get(0),world(p)).distance2(pos(p,world(p))))<0.5)throw new IllegalStateException("Liaison ambiguë : se rapprocher d'Aurèle, à moins de quatre blocs");
  saveBinding(p,"aurele",found.get(0));say2(p,"Liaison Aurèle enregistrée ; PNJ existant et apparence conservés.");
 }
 static View view(Object p){View old=VIEWS.get(uuid(p));if(old!=null&&!same(old)){dispose(old.account);old=null;}if(old!=null)return old;View v=new View(p);VIEWS.put(v.account,v);HIDDEN.put(v.account,v.hidden);return v;}
 static Actor spawn(View v,String role,Point at){Actor old=v.actors.get(role);if(old!=null&&!Boolean.TRUE.equals(call(old.entity,"method_31481")))return old;
  Object src=source(v.player,role),record=roleRecord(v.player,role);if(record==null)throw new IllegalStateException("PNJ source non reconnu : "+role+" ; effectuer sa liaison près du PNJ existant");
  Object template=Pq01Nbt.copy(child(record,"template"));UUID original=UUID.fromString(str(record,"uuid",""));if(at==null){if(src!=null)at=pos(src,v.world);else {Object m=marker(v.player,"a");if(m==null)throw new IllegalStateException("Position source non chargée : "+role);at=pos(m,v.world);}}
  for(String field:List.of("UUID","UUIDMost","UUIDLeast","Pos","Motion","Rotation","Passengers","Leash","Brain","Tags","Home","ActionData","ObjectiveData","TradingData"))call(template,"method_10551",field);
  UUID fresh=UUID.randomUUID();stat("de.markusbordihn.easynpc.utils.CompoundTagUtils","writeUUID",template,"UUID",fresh);
  GuardNbt.put(template,"Tags",GuardNbt.list(List.of(TAG,"hcm_pq3_"+role,"hcm_owner_"+v.account,"hcm_character_"+v.character,"indivis_no_hard_despawn").stream().map(GuardNbt::stringValue).toList()));
  call(template,"method_10556","Invulnerable",true);call(template,"method_10556","Silent",true);call(template,"method_10556","NoAI",!role.equals("aurele"));call(template,"method_10556","NoGravity",false);call(template,"method_10556","CanPickUpLoot",false);GuardNbt.string(template,"DeathLootTable","minecraft:empty");
  LIVE.add(fresh);Object e=null,g=null,t=null;try{
   run(v.player,"summon "+str(record,"type","")+" "+at.x()+" "+at.y()+" "+at.z()+" "+template);
   e=call(v.world,"method_66347",fresh);if(e==null||!tags(e).contains(TAG))throw new IllegalStateException("Copie EasyNPC non créée : "+role);
   configureActor(e,role.equals("aurele"));call(e,"method_5665",stat("net.minecraft.class_2561","method_43470","Aurèle"));
   g=call(e,"getEntityGoalSelector");t=call(e,"getEntityTargetSelector");Pq01Actors.protect(g);Pq01Actors.protect(t);call(g,"method_35113",(Predicate<Object>)goal->true);call(t,"method_35113",(Predicate<Object>)goal->true);
   Object row=GuardNbt.compound();GuardNbt.string(row,"Type","CUSTOM");GuardNbt.string(row,"Cmd","indivis:pq03 "+role);Pq01Actions.mutate(e,"ON_INTERACTION",GuardNbt.list(List.of(row)));
   if(role.equals("aurele")){run(v.player,"attribute "+fresh+" minecraft:movement_speed base set 0.25");}
   if(!teamReady){run(v.player,"team add indivis_pq03");run(v.player,"team modify indivis_pq03 collisionRule never");teamReady=true;}run(v.player,"team join indivis_pq03 "+fresh);
   Actor actor=new Actor(role,e,original,g,t);v.actors.put(role,actor);v.hidden.add(original);DIRTY.put(v.account,v.player);return actor;
  }catch(Throwable failure){LIVE.remove(fresh);if(g!=null)Pq01Actors.forget(g);if(t!=null)Pq01Actors.forget(t);if(e!=null)call(e,"method_31472");throw failure;}
 }
 static void customActions(){Class<?> contract=type("de.markusbordihn.easynpc.api.action.CustomActionExecutor");Object cb=Proxy.newProxyInstance(contract.getClassLoader(),new Class<?>[]{contract},(proxy,method,args)->{
  if(method.getDeclaringClass()==Object.class)return objectMethod(proxy,method,args);if(!method.getName().equals("execute")||args==null||args.length!=4)return null;
  Object p=call(args[3],"initiator");if(p==null||!(args[2] instanceof List<?> words)||words.size()!=1)return null;String role=String.valueOf(words.get(0));Object npc=call(args[1],"getEntity");View v=VIEWS.get(uuid(p));
  if(v!=null&&same(v)&&v.actors.containsKey(role)&&v.actors.get(role).entity==npc&&pos(npc,v.world).distance2(pos(p,v.world))<=64){pause(v);open(p,role);}return null;});
  stat("de.markusbordihn.easynpc.api.action.ActionRegistry","register",stat("net.minecraft.class_2960","method_60654","indivis:pq03"),cb);
 }
 static Point routePoint(Object p,String key){Object e=marker(p,key);Object record=child(child(config(p),"points"),key);
  if(e!=null){Point point=pos(e,world(p));if(record==null||!point.equals(point(record))){Object root=Pq01Nbt.copy(config(p));GuardNbt.put(GuardNbt.childOrCreate(root,"points"),key,pointNbt(point));GuardNbt.save(server(p),SETUP,root);}return point;}
  if(record!=null){Point point=point(record);if(point.dimension().equals(dimension(world(p))))return point;}return null;
 }
 static Point startPosition(View v){Object saved=characterScene(v.player,v.character);int phase=stage(v.player);if(saved!=null&&str(saved,"owner","").equals(v.account.toString())&&integer(saved,"waypoint",-1)==v.waypoint&&integer(saved,"stage",-1)==phase){Object n=child(saved,"position");if(n!=null){Point at=point(n);if(at.dimension().equals(dimension(v.world)))return at;}}
  if(phase>=30&&phase<100){Point at=routePoint(v.player,ROUTE.get(Math.max(0,Math.min(21,v.waypoint))));if(at!=null)return at;}return null;
 }
 static void savePosition(View v){Actor a=v.actors.get("aurele");if(a==null)return;Point at=pos(a.entity,v.world);int s=same(v)?stage(v.player):v.lastStage;if(v.saved!=null&&v.saved.distance2(at)<0.01&&v.savedStage==s&&v.savedWaypoint==v.waypoint&&v.savedLore==v.lorePending)return;
  Object root=GuardNbt.compound();GuardNbt.string(root,"owner",v.account.toString());GuardNbt.integer(root,"waypoint",v.waypoint);GuardNbt.integer(root,"stage",s);GuardNbt.integer(root,"pending_lore",v.lorePending);GuardNbt.put(root,"position",pointNbt(at));GuardNbt.save(server(v.player),STATE+"/"+v.character,root);v.saved=at;v.savedStage=s;v.savedWaypoint=v.waypoint;v.savedLore=v.lorePending;
 }
 static void validateRoute(Object p,int phase){for(int i:Pq03Rules.route(phase))if(routePoint(p,ROUTE.get(i))==null)throw new IllegalStateException("Repère PQ03 absent/non mémorisé : "+ROUTE.get(i).toUpperCase(Locale.ROOT)+". Aucun avancement effectué.");}
 static void open(Object p,String role){
  if(!ready(p)||!role.equals("aurele"))return;ensure(p);int phase=stage(p);source(p,"aurele");View v=VIEWS.get(uuid(p));if(v!=null&&!same(v)){dispose(v.account);v=null;}Object other=current(p);
  if(other!=null&&(v==null||other!=v.session)){UUID id=(UUID)Pq01Client.field(other,"pnj");Object r=roleRecord(p,"aurele");Actor owned=v==null?null:v.actors.get("aurele");if((owned==null||!id.equals(owned.id))&&(r==null||!id.toString().equals(str(r,"uuid",""))))return;}
  if(score(p,"CAP_QUETEACTIVE")!=0&&score(p,"CAP_QUETEACTIVE")!=3)return;
  if(v==null)v=view(p);
  Actor actor=spawn(v,"aurele",startPosition(v));if(pos(actor.entity,v.world).distance2(pos(p,v.world))>144){say2(p,"Aurèle vous attend à son dernier point du parcours.");return;}
  pause(v);v.lastStage=phase;Pq03Data.Node remembered=Pq03Data.IDS.get(score(p,"CAP_PQ3_NODE"));String key=remembered!=null&&Pq03Rules.validNode(remembered.key,phase)?remembered.key:Pq03Rules.initial(phase,v.waypoint);
  if(phase>=100)key=Pq03Rules.greeting(score(p,"CAP_PQ3_IMPR"));if(Pq03Rules.introduction(phase)&&phase!=10)run(p,"function hc_quetes:migration/write/pq03/v1");show(v,key);
 }
 static boolean condition(View v,String name){return name==null||name.isEmpty()||switch(name){case "intro"->Pq03Rules.introduction(stage(v.player));case "rumble"->score(v.player,"CAP_PQ3_RUMBLE")>0;case "undecided"->score(v.player,"CAP_PQ3_DA")==0&&stage(v.player)<100;default->false;};}
 static String resolvedKey(View v,String key){
  if(key.equals("b_position")&&score(v.player,"CAP_PQ3_DA")>0)return "t_menu";
  if(key.equals("c_position")&&score(v.player,"CAP_PQ3_DB")>0)return List.of("c_thanks","c_neutral","c_critical","c_contempt").get(Math.min(4,score(v.player,"CAP_PQ3_DB"))-1);
  return key;
 }
 static void show(View v,String key){if(!same(v))return;if(key.startsWith("event:")){event2(v,key.substring(6));return;}
  key=resolvedKey(v,key);Pq03Data.Node n=Pq03Data.get(key);if(!Pq03Rules.validNode(key,stage(v.player))||!condition(v,n.condition))return;
  pause(v);v.node=n;v.baseToken=0;v.queuedChoice=0;score2(v.player,"CAP_PQ3_NODE",n.id);v.ui=true;Object focus=focus(v,n);v.pendingFocus=focus;v.pendingNode=key;v.frameAt=clock+1;
 }
 
 static Object focus(View v,Pq03Data.Node n){Actor actor=v.actors.get("aurele");if(actor==null)actor=spawn(v,"aurele",startPosition(v));faceEntity(actor.entity,pos(v.player,v.world));return actor.entity;}
 static String uuidArray(UUID id){long a=id.getMostSignificantBits(),b=id.getLeastSignificantBits();return "[I;"+(int)(a>>32)+","+(int)a+","+(int)(b>>32)+","+(int)b+"]";}
 static void faceEntity(Object e,Point to){Point at=pos(e,call(e,"getEntityServerLevel"));float yaw=(float)(Math.toDegrees(Math.atan2(to.z()-at.z(),to.x()-at.x()))-90);call(e,"method_36456",yaw);call(e,"method_5847",yaw);call(e,"method_5636",yaw);}
 static double cameraRange(){return ((Number)Pq01Client.field(stat("net.hautecapitale.dialogue.config.DialogueConfig","get"),"distance_ouverture")).doubleValue();}
 static boolean inCameraRange(View v,Object entity){double max=cameraRange();return pos(entity,v.world).distance2(pos(v.player,v.world))<=max*max;}
 static boolean openCamera(View v,Object focus){
  if(!inCameraRange(v,focus))return false;if(v.focus==focus&&v.session!=null&&v.session==current(v.player))return true;
  v.suppress=true;try{v.session=stat(DM,"ouvrir",v.player,focus,en("net.hautecapitale.dialogue.session.DialogueMode","CAPTURE"));v.focus=v.session==null?null:focus;}finally{v.suppress=false;}
  if(v.session!=null){v.acknowledgedSession=null;v.focusToken=1_000_000_000+((++nonce)%60_000_000)*16;v.focusSince=clock;v.focusRetry=clock;}return v.session!=null;
 }
 static boolean focusReady(View v){return same(v)&&v.session!=null&&v.session==current(v.player)&&v.acknowledgedSession==v.session;}
 static int acknowledgeFocus(Object player,int token){View v=VIEWS.get(uuid(player));if(v==null||!same(v)||v.pendingNode==null||v.session==null||v.session!=current(player)||v.focusToken!=token||!Pq03Rules.validNode(v.pendingNode,stage(player)))return 0;v.acknowledgedSession=v.session;return 1;}
 static void requestFocus(View v){if(v.session==null||clock<v.focusRetry)return;v.focusRetry=clock+20;int id=((Number)Pq01Client.field(v.session,"id")).intValue();UUID actor=(UUID)Pq01Client.field(v.session,"pnj");run(v.player,"tellraw @s {\"text\":\"\",\"insertion\":"+quote(Pq03Focus.encode(new Pq03Focus.Request(id,actor,v.focusToken)))+"}");}

 static void render(View v){if(!same(v)||v.node==null||!focusReady(v))return;Pq03Data.Node n=v.node;
  v.choices=n.choices.stream().filter(c->condition(v,c.condition)).toList();v.baseToken=1_000_000_000+((++nonce)%60_000_000)*16;
  int advance=n.next.isEmpty()?0:v.baseToken+14;List<String> texts=n.lines;String speaker=n.speaker;
  if(texts.isEmpty()){texts=v.lastLines;speaker=v.lastSpeaker;}else{v.lastLines=texts;v.lastSpeaker=speaker;}
  if(texts.isEmpty())texts=Pq03Data.get(Pq03Rules.initial(stage(v.player),v.waypoint)).lines;
  if(texts.isEmpty())texts=List.of(Pq03Data.get("b_walk").lines.get(1));
  int voiceTicks=voice(v,n.key,n.lines.isEmpty());v.voiceUntil=clock+voiceTicks;
  run(v.player,"tellraw @s {\"text\":\"\",\"insertion\":"+quote("indivis:pq03:v1:"+n.key+":"+speaker+":"+advance+":0:"+voiceTicks)+"}");
  String name="Aurèle";
  for(String text:texts)run(v.player,"tellraw @s [{\"text\":"+quote("["+name+"]")+",\"color\":\"yellow\"},{\"text\":"+quote(" : "+text)+",\"color\":\"white\"}]");
  for(int i=0;i<v.choices.size();i++){Pq03Data.Choice c=v.choices.get(i);run(v.player,"tellraw @s {\"text\":"+quote("["+c.text+"]")+",\"color\":"+quote(c.target.startsWith("event:")||Set.of("a08","m06","f06").contains(c.target)||Pq03Transitions.movementChoice(c.target)?"green":"white")+",\"click_event\":{\"action\":\"run_command\",\"command\":"+quote("/trigger Pq03Choix set "+(v.baseToken+i+1))+"}}");}
  String action=Pq03Transitions.label(n.next);boolean terminal=!action.isEmpty();run(v.player,"tellraw @s {\"text\":"+quote("["+(terminal?action:"Fermer la conversation")+"]")+",\"color\":"+quote(terminal?"green":"gray")+",\"click_event\":{\"action\":\"run_command\",\"command\":"+quote("/trigger Pq03Choix set "+(v.baseToken+(terminal?14:15)))+"}}");
  score2(v.player,"CAP_QDIALOG_OWNER",98);score2(v.player,"CAP_QDIALOG_KEEP",1);score2(v.player,"hcm_pq3_ui",1);score2(v.player,"Pq03Choix",0);run(v.player,"scoreboard players enable @s Pq03Choix");
  if(v.lorePending>=0){v.lorePending=-1;savePosition(v);}
 }
 static int voice(View v,String key,boolean menu){if(menu)return 0;Object r=child(child(config(v.player),"voices"),key);if(r==null)return 0;String sound=str(r,"sound","");int ticks=integer(r,"ticks",0);if(!sound.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")||ticks<1||ticks>12000)return 0;run(v.player,"playsound "+sound+" voice @s ~ ~ ~ 1 1");return ticks;}
 static int dispatch(View v){Object p=v.player;int token=score(p,"Pq03Choix");score2(p,"Pq03Choix",0);
  if(!same(v)||!v.ui||v.session==null||v.session!=current(p)||v.pendingNode!=null||v.node==null||score(p,"CAP_QDIALOG_OWNER")!=98||!Pq03Rules.validNode(v.node.key,stage(p)))return 0;
  int pick=token-v.baseToken;if(v.baseToken==0||pick<1||pick>15){run(p,"scoreboard players enable @s Pq03Choix");return 0;}
  if(pick==15){String agreed=Pq03Transitions.agreedExit(v.node.next,stage(p));if(!agreed.isEmpty()){event2(v,agreed);return 1;}closeUi(v);if(canResume(v))resume(v);else if(stage(p)>=100)dispose(v.account);return 1;}
  if(clock<v.voiceUntil){v.queuedChoice=token;run(p,"scoreboard players enable @s Pq03Choix");return 1;}
  if(pick==14){if(v.node.next.isEmpty())return 0;v.baseToken=0;show(v,v.node.next);retryFrame(v);return 1;}
  if(pick>v.choices.size())return 0;Pq03Data.Choice choice=v.choices.get(pick-1);if(!condition(v,choice.condition))return 0;
  if(!effect(v,choice.effect))return 0;v.baseToken=0;show(v,choice.target);retryFrame(v);return 1;
 }
 static void retryFrame(View v){if(v.ui&&v.session==current(v.player)&&v.pendingNode==null&&v.baseToken==0)render(v);}
 static void addSigned(Object p,String objective,int delta){if(delta!=0)run(p,"scoreboard players "+(delta<0?"remove":"add")+" @s "+objective+" "+Math.abs(delta));}
 static boolean effect(View v,String effect){if(effect==null||effect.isEmpty())return true;if(stage(v.player)>=100)return false;String[] parts=effect.split(":");String score=parts[0].equals("discipline")?"CAP_PQ3_DA":parts[0].equals("separation")?"CAP_PQ3_DB":"";if(score.isEmpty()||score(v.player,score)!=0)return false;int value=Integer.parseInt(parts[1]);if(value<1||value>(score.endsWith("DB")?4:3))return false;score2(v.player,score,value);applyDecision(v.player,score.endsWith("DB"));return true;}
 static void event2(View v,String name){Object p=v.player;if(!same(v))return;int phase=stage(p);
  switch(name){
   case "market"->{if(!Pq03Rules.introduction(phase))return;validateRoute(p,30);closeUi(v);run(p,"function hc_quetes:migration/write/pq03/v20");score2(p,"CAP_PQ3_BRANCH",1);begin(v,0);remark(v,"b_walk");}
   case "depth"->{if(!Pq03Rules.introduction(phase)&&phase!=50)return;validateRoute(p,60);closeUi(v);run(p,"function hc_quetes:migration/write/pq03/v40");score2(p,"CAP_PQ3_BRANCH",2);begin(v,0);remark(v,"b_walk");}
   case "return"->{if(phase!=30||v.waypoint!=8)return;closeUi(v);run(p,"function hc_quetes:migration/write/pq03/v25");score2(p,"CAP_GUIDE_LOCK",0);score2(p,"CAP_GUIDE_ID",0);begin(v,8);score2(p,"CAP_QUETEACTIVE",0);}
   case "later"->{if(Pq03Rules.introduction(phase))run(p,"function hc_quetes:migration/write/pq03/v10");score2(p,"CAP_PQ3_NODE",Pq03Data.get(phase==50?"b_reunion_routes":"a_routes").id);closeUi(v);pause(v);}
   case "resume"->{closeUi(v);if(canResume(v))resume(v);else if(phase>=100)dispose(v.account);}
   case "questions"->show(v,Pq03Rules.introduction(phase)?"a_questions":"t_menu");
   case "context"->{if(Pq03Rules.introduction(phase))show(v,"a_routes");else if(phase==50)show(v,"b_reunion_routes");else if(phase==90)show(v,"c_questions");else if(phase>=100){closeUi(v);if(v.retiring)resume(v);else dispose(v.account);}else {closeUi(v);if(canResume(v))resume(v);}}
   case "complete"->{if(phase==90&&v.node!=null&&v.node.key.equals("c_bye")){closeUi(v);v.finalizeAt=clock+1;}}
   default->throw new IllegalArgumentException(name);
  }
 }
 static boolean canResume(View v){int phase=stage(v.player);return v.retiring||phase==40&&v.waypoint>0||phase==30&&v.waypoint<8||phase==60&&v.waypoint<21;}
 static void begin(View v,int point){score2(v.player,"CAP_QUETEACTIVE",3);v.waypoint=point;score2(v.player,"CAP_PQ3_WP",point);score2(v.player,"CAP_PQ3_NODE",Pq03Data.get("t_menu").id);v.saved=null;resume(v);}
 static void complete(View v){Object p=v.player;if(stage(p)!=90||score(p,"CAP_PQ3_DB")==0||current(p)!=null)return;
  score2(p,"CAP_PQ3_END",1);run(p,"function capitale:pq03/complete_once_self");
  if(stage(p)>=100){v.finalizeAt=0;v.retiring=true;v.walking=true;score2(p,"CAP_PQ3_NODE",0);nextLeg(v);}else{v.finalizeAt=0;score2(p,"CAP_PQ3_END",0);say2(p,"La récompense ne peut pas être validée pour le moment. L'entretien reste disponible, sans perte de progression.");}
 }
 static void stopActor(Actor a){if(a==null)return;Object nav=call(a.entity,"getGroundPathNavigation");if(nav!=null)call(nav,"method_6340");call(a.entity,"method_18799",en("net.minecraft.class_243","field_1353"));}
 static void pause(View v){if(v.motion!=null){v.motion.pause();v.motion=null;}stopActor(v.actors.get("aurele"));v.walking=false;if(same(v))score2(v.player,"CAP_PQ3_PAUSE",1);savePosition(v);}
 static void resume(View v){if(!same(v)||!canResume(v))return;v.lorePending=-1;v.walking=true;v.far=false;v.failure="";score2(v.player,"CAP_PQ3_PAUSE",0);score2(v.player,"CAP_PQ3_NODE",Pq03Data.get("t_menu").id);savePosition(v);nextLeg(v);}
 static void lorePause(View v,int point){pause(v);v.lorePending=point;v.loreCueAt=clock;score2(v.player,"CAP_PQ3_NODE",Pq03Data.get(contextKey(point)).id);savePosition(v);offerLore(v);}
 static String contextKey(int point){return switch(point){case 3->"b_rumble";case 6->"b_circulation";case 13->"c_district";case 18->"c_approach";default->throw new IllegalArgumentException();};}
 static void offerLore(View v){if(v.lorePending<0||!same(v)||v.ui||!Set.of(30,60).contains(stage(v.player)))return;
  Actor actor=v.actors.get("aurele");if(actor==null)return;if(clock>=v.loreCueAt){run(v.player,"title @s actionbar {\"text\":\"Aurèle s'arrête et se tourne vers vous.\",\"color\":\"gold\"}");v.loreCueAt=clock+100;}
  faceEntity(actor.entity,pos(v.player,v.world));double range=Math.min(4,cameraRange());if(current(v.player)==null&&pos(actor.entity,v.world).distance2(pos(v.player,v.world))<=range*range)show(v,contextKey(v.lorePending));
 }
 static void nextLeg(View v){if(!v.walking||!same(v)||v.ui)return;Actor actor=v.actors.get("aurele");if(actor==null)actor=spawn(v,"aurele",startPosition(v));final Actor guide=actor;int phase=stage(v.player);List<Integer> route=v.retiring?Pq03Rules.retreat():Pq03Rules.route(phase);int current=route.indexOf(v.waypoint);
  if(current<0){v.walking=false;v.failure="Repère incompatible avec l'itinéraire";stopActor(actor);return;}if(current==route.size()-1){arrivedPhase(v);return;}final int target=route.get(current+1);Point point=routePoint(v.player,ROUTE.get(target));
  if(point==null){v.walking=false;v.failure="Repère absent/non mémorisé : "+ROUTE.get(target);stopActor(actor);score2(v.player,"CAP_PQ3_PAUSE",1);say2(v.player,v.failure+". Aucune progression de quête.");return;}
  if(!loaded(v.world,point)){stopActor(actor);v.failure="Passage temporairement non chargé ; Aurèle attend.";savePosition(v);return;}
  v.failure="";v.motion=new Pq02Motion(new Pq02Motion.Port(){
   public boolean valid(){return same(v)&&v.walking&&!v.ui&&!Boolean.TRUE.equals(call(guide.entity,"method_31481"));}public Point position(){return pos(guide.entity,v.world);}
   public boolean navigate(Point p){Object nav=call(guide.entity,"getGroundPathNavigation");if(nav==null)return false;if(Boolean.TRUE.equals(call(nav,"method_58160",p.x(),p.y(),p.z(),0,1.2d)))return true;return Math.abs(p.y()-Math.ceil(p.y()))>0.01&&Boolean.TRUE.equals(call(nav,"method_58160",p.x(),Math.ceil(p.y()),p.z(),0,1.2d));}
   public boolean navigating(){Object nav=call(guide.entity,"getGroundPathNavigation");return nav!=null&&!Boolean.TRUE.equals(call(nav,"method_6357"));}public void stop(){stopActor(guide);}
   public boolean stopOnArrival(){return target==route.get(route.size()-1)||contextStop(v,target);}
   public void arrived(){if(!same(v))return;v.motion=null;v.waypoint=target;score2(v.player,"CAP_PQ3_WP",target);v.saved=null;savePosition(v);
    if(target==route.get(route.size()-1))arrivedPhase(v);else if(contextStop(v,target)){if(target==3)score2(v.player,"CAP_PQ3_RUMBLE",1);lorePause(v,target);}else nextLeg(v);
   }
   public void failed(String reason){v.motion=null;v.walking=false;v.failure=reason;score2(v.player,"CAP_PQ3_PAUSE",1);savePosition(v);say2(v.player,"Aurèle attend : "+reason+". Reprendre la discussion pour réessayer.");}
  },point,integer(config(v.player),"move_timeout_ticks",2400));v.motion.start();
 }
 static boolean contextStop(View v,int point){if(v.retiring)return false;int phase=stage(v.player);return phase==30&&(point==6||point==3&&Boolean.TRUE.equals(call(config(v.player),"method_68566","rumble_at_d",false)))||phase==60&&(point==13||point==18);}

 static void arrivedPhase(View v){int phase=stage(v.player);pause(v);
  if(v.retiring){if(canRetire(v))dispose(v.account);return;}
  if(phase==30){score2(v.player,"CAP_PQ3_NODE",Pq03Data.get("b_market").id);show(v,"b_market");}
  else if(phase==40){run(v.player,"function hc_quetes:migration/write/pq03/v30");score2(v.player,"CAP_PQ3_NODE",Pq03Data.get("b_reunion").id);score2(v.player,"CAP_QUETEACTIVE",0);run(v.player,"title @s actionbar {\"text\":\"Aurèle vous attend au premier ascenseur.\",\"color\":\"gold\"}");savePosition(v);}
  else if(phase==60){run(v.player,"function hc_quetes:migration/write/pq03/v50");score2(v.player,"CAP_PQ3_NODE",Pq03Data.get("c03").id);score2(v.player,"CAP_GUIDE_LOCK",0);score2(v.player,"CAP_GUIDE_ID",0);run(v.player,"title @s actionbar {\"text\":\"Aurèle s'arrête près de l'ascenseur.\",\"color\":\"gold\"}");savePosition(v);}
 }
 static void remark(View v,String key){Pq03Data.Node n=Pq03Data.get(key);for(String text:n.lines)run(v.player,"tellraw @s [{\"text\":\"[Aurèle]\",\"color\":\"yellow\"},{\"text\":"+quote(" : "+text)+",\"color\":\"white\"}]");voice(v,key,false);}
 static void observe(View v){if(stage(v.player)!=90||score(v.player,"CAP_PQ3_RECALL")>0)return;Actor actor=v.actors.get("aurele");if(actor!=null&&pos(actor.entity,v.world).distance2(pos(v.player,v.world))>36){score2(v.player,"CAP_PQ3_RECALL",1);remark(v,"c_recall");run(v.player,"tellraw @s {\"text\":"+quote("[Objectif] "+Pq03Data.get("c_recall").objective)+",\"color\":\"gold\"}");}}
 static void closeUi(View v){v.suppress=true;try{if(v.session!=null&&v.session==current(v.player))stat(DM,"fermer",v.player,en("net.hautecapitale.dialogue.session.CloseReason","ECRAN_FERME"));}finally{v.suppress=false;}
  v.session=null;v.acknowledgedSession=null;v.focusToken=0;v.focus=null;v.pendingFocus=null;v.pendingNode=null;v.ui=false;v.baseToken=0;v.queuedChoice=0;
  if(same(v)){score2(v.player,"hcm_pq3_ui",0);score2(v.player,"Pq03Choix",0);if(score(v.player,"CAP_QDIALOG_OWNER")==98){score2(v.player,"CAP_QDIALOG_OWNER",0);score2(v.player,"CAP_QDIALOG_KEEP",0);}}
 }
 /** Le hook reçoit la véritable raison b10 : aucune reprise sur mort, remplacement ou déconnexion. */
 public static void onClosing(Object player,Object session,String reason){View v=VIEWS.get(uuid(player));if(v==null||v.suppress||v.session!=session)return;
  v.session=null;v.acknowledgedSession=null;v.focusToken=0;v.focus=null;v.pendingFocus=null;v.pendingNode=null;v.ui=false;v.baseToken=0;v.queuedChoice=0;
  if(!same(v))return;score2(player,"hcm_pq3_ui",0);score2(player,"Pq03Choix",0);if(score(player,"CAP_QDIALOG_OWNER")==98){score2(player,"CAP_QDIALOG_OWNER",0);score2(player,"CAP_QDIALOG_KEEP",0);}
  if(Pq03Transitions.playerClosed(reason)){
   if(stage(player)==90&&v.node!=null&&v.node.key.equals("c_bye")){v.finalizeAt=clock+1;pause(v);return;}
   String agreed=v.node==null?"":Pq03Transitions.agreedExit(v.node.next,stage(player));
   if(!agreed.isEmpty()){try{event2(v,agreed);}catch(RuntimeException failure){pause(v);v.failure=failure.getMessage();report2(failure);}return;}
   if(canResume(v)){resume(v);return;}
  }
  pause(v);if(stage(player)>=100)dispose(v.account);
 }
 public static void suspend(Object p){View v=VIEWS.get(uuid(p));if(Pq03ProfileCheckpoint.preserving(p)){if(v!=null)savePosition(v);return;}Pq03Entries.cancel(uuid(p));if(v==null)return;boolean moving=same(v)&&v.walking&&!v.ui&&!v.retiring;pause(v);if(moving)score2(p,"CAP_PQ3_PAUSE",0);savePosition(v);closeUi(v);dispose(v.account);}
 static void dispose(UUID account){Pq03Entries.cancel(account);View v=VIEWS.remove(account);if(v==null)return;v.disposed=true;v.walking=false;if(v.motion!=null)v.motion.pause();
  for(Actor a:v.actors.values()){stopActor(a);Pq01Actors.forget(a.goals);Pq01Actors.forget(a.targets);LIVE.remove(a.id);call(a.entity,"method_31472");}
  v.actors.clear();if(v.anchor!=null){LIVE.remove(uuid(v.anchor));call(v.anchor,"method_31472");}if(v.questMarker!=null){LIVE.remove(uuid(v.questMarker));call(v.questMarker,"method_31472");}HIDDEN.remove(account);DIRTY.put(account,v.player);
 }
 public static boolean visibleOriginal(class_1297 entity,class_3222 player){
  if(entity.method_5752().contains(TAG)){View v=VIEWS.get(player.method_5667());return v!=null&&same(v)&&LIVE.contains(entity.method_5667())&&entity.method_5752().contains("hcm_owner_"+v.account)&&entity.method_5752().contains("hcm_character_"+v.character);}
  Set<UUID> hidden=HIDDEN.get(player.method_5667());return hidden==null||!hidden.contains(entity.method_5667());
 }
 public static Optional<ResolvedTarget> waypoint(Object player,String tag){if(!tag.equals("hcm_pq3_aurele")||!ready(player))return Optional.empty();View v=VIEWS.get(uuid(player));Point at=null;if(v!=null&&same(v)&&v.actors.containsKey("aurele"))at=pos(v.actors.get("aurele").entity,v.world);else {Object record=characterScene(player,character(player));if(record!=null&&str(record,"owner","").equals(uuid(player).toString())&&integer(record,"stage",-1)==stage(player)&&child(record,"position")!=null)at=point(child(record,"position"));}
  if(at==null)return Optional.empty();return Optional.of(new ResolvedTarget(WaypointDef.Mode.EXACT,class_2960.method_60654(at.dimension()),at.x(),at.y(),at.z(),0,"Aurèle Veyrane"));
 }
 static void updateMarker(View v){int phase=stage(v.player);boolean useful=phase>=30&&phase<100&&same(v)&&v.actors.containsKey("aurele");if(!useful){if(v.questMarker!=null){LIVE.remove(uuid(v.questMarker));call(v.questMarker,"method_31472");v.questMarker=null;}return;}
  Point at=pos(v.actors.get("aurele").entity,v.world),above=new Point(at.dimension(),at.x(),at.y()+3.05,at.z());
  if(v.questMarker==null){UUID id=UUID.randomUUID();LIVE.add(id);run(v.player,"summon minecraft:text_display "+above.x()+" "+above.y()+" "+above.z()+" {UUID:"+uuidArray(id)+",Tags:[\"hcm_pq3_actor\",\"hcm_owner_"+v.account+"\",\"hcm_character_"+v.character+"\"],text:{text:\"?\",color:\"yellow\",bold:true},billboard:\"center\",background:0,see_through:1b,brightness:{sky:15,block:15}}");v.questMarker=call(v.world,"method_66347",id);}else if(pos(v.questMarker,v.world).distance2(above)>0.0025)call(v.questMarker,"method_5814",above.x(),above.y(),above.z());
 }
 static void tick(Object server){clock++;@SuppressWarnings("unchecked") List<Object> players=(List<Object>)call(call(server,"method_3760"),"method_14571");Set<UUID> online=new HashSet<>();for(Object p:players)online.add(uuid(p));
  Pq03Entries.tick(players);
  for(View v:new ArrayList<>(VIEWS.values()))try{
   if(!online.contains(v.account)||!same(v)||!Boolean.TRUE.equals(call(v.player,"method_5805"))){if(same(v))pause(v);savePosition(v);closeUi(v);dispose(v.account);continue;}
   v.lastStage=stage(v.player);
   Actor present=v.actors.get("aurele");if(present!=null&&Boolean.TRUE.equals(call(present.entity,"method_31481"))){savePosition(v);dispose(v.account);continue;}
   if(v.retiring&&!v.walking&&!v.ui&&clock%20==0&&canRetire(v)){dispose(v.account);continue;}
   if(v.pendingNode!=null&&clock>=v.frameAt){Object focus=v.pendingFocus;if(openCamera(v,focus)){
    if(focusReady(v)){v.pendingFocus=null;v.pendingNode=null;render(v);}
    else if(clock-v.focusSince>200){System.err.println("[PQ03 focus] confirmation absente : joueur="+v.account+", personnage="+v.character+", nœud="+v.pendingNode+", session="+Pq01Client.field(v.session,"id"));pause(v);closeUi(v);}
    else requestFocus(v);
   }else{v.pendingFocus=null;v.pendingNode=null;v.ui=false;pause(v);}}
   if(v.ui&&v.session!=null&&v.session!=current(v.player)){onClosing(v.player,v.session,"REMPLACEE");continue;}
   if(v.ui){int pick=score(v.player,"Pq03Choix");if(pick!=0)dispatch(v);else if(v.queuedChoice!=0&&clock>=v.voiceUntil){int value=v.queuedChoice;v.queuedChoice=0;score2(v.player,"Pq03Choix",value);dispatch(v);}if(clock%20==0&&v.session!=null)call(v.session,"toucher",((Number)call(call(v.world,"method_8503"),"method_3780")).longValue());}
   if(v.finalizeAt>0&&clock>=v.finalizeAt&&current(v.player)==null)complete(v);
   if(v.walking&&!v.ui){Actor a=v.actors.get("aurele");if(a!=null&&!v.retiring&&stage(v.player)!=40&&pos(a.entity,v.world).distance2(pos(v.player,v.world))>18*18){if(v.motion!=null){v.motion.pause();v.motion=null;}else stopActor(a);v.far=true;if(clock>=v.waitCueAt){v.waitCueAt=clock+200;run(v.player,"title @s actionbar {\"text\":\"Aurèle vous attend.\",\"color\":\"gold\"}");}}else{v.far=false;if(v.motion!=null)v.motion.tick();else nextLeg(v);}}
   if(clock%5==0)offerLore(v);
   if(clock%20==0){savePosition(v);observe(v);updateMarker(v);}
  }catch(Throwable t){report2(t);pause(v);closeUi(v);v.failure=t.getMessage()==null?t.toString():t.getMessage();say2(v.player,v.failure);}
  // Restauration d'un parcours existant ; aucun don. Seule une pause de lore encore
  // non présentée pourra s'ouvrir à proximité, sans rejouer les dialogues déjà lus.
  if(clock%20==0)for(Object p:players)try{if(!ready(p)||VIEWS.containsKey(uuid(p)))continue;int s=stage(p);if(!Set.of(30,40,50,60,90).contains(s))continue;ensure(p);View probe=new View(p);Point at=startPosition(probe);if(at!=null&&!loaded(world(p),at))continue;View v=view(p);spawn(v,"aurele",at);if(canResume(v)&&score(p,"CAP_PQ3_PAUSE")==0)resume(v);}catch(Throwable t){report2(t);dispose(uuid(p));}
  flush();
 }
 public static boolean ownsCheckpoint(Object p,Object dto){View v=VIEWS.get(uuid(p));Pq03Entries.Pending queued=Pq03Entries.PENDING.get(uuid(p));return java.util.Objects.equals(character(p),call(dto,"id"))&&(v!=null&&same(v)&&v.actors.containsKey("aurele")||queued!=null&&ready(p)&&queued.character().equals(character(p))&&queued.world()==world(p)&&queued.stage()==stage(p));}
 public static void beforeProfileSave(Object p,Object dto){if(!Pq03ProfileCheckpoint.preserving(p)&&ownsCheckpoint(p,dto))suspend(p);}
 static boolean loaded(Object world,Point at){return ((net.minecraft.class_3218)world).method_8393((int)Math.floor(at.x())>>4,(int)Math.floor(at.z())>>4);}
 static boolean canRetire(View v){Actor a=v.actors.get("aurele");return a==null||pos(a.entity,v.world).distance2(pos(v.player,v.world))>64*64||!Boolean.TRUE.equals(call(v.player,"method_6057",a.entity));}
 static void flush(){if(DIRTY.isEmpty())return;for(UUID id:new HashSet<>(DIRTY.keySet())){Object p=DIRTY.get(id);if(p==null)continue;stat("net.hautecapitale.quetes.visibility.QuestVisibility","refresh",p);}DIRTY.clear();}
 static void diagnostic(Object p){say2(p,"Personnage="+character(p)+", verrou="+score(p,"CAP_PROFILE_LOCK")+", PQ02="+score(p,"hcq_pq02")+", PQ03="+stage(p)+", point="+score(p,"CAP_PQ3_WP")+", pause="+score(p,"CAP_PQ3_PAUSE")+", décisions="+score(p,"CAP_PQ3_DA")+"/"+score(p,"CAP_PQ3_DB")+", impression="+score(p,"CAP_PQ3_IMPR")+", récompense="+score(p,"CAP_PQ3_REW")+".");Pq03Setup.summary(p);View v=VIEWS.get(uuid(p));if(v!=null)say2(p,"UI="+v.ui+", marche="+v.walking+", attente joueur="+v.far+", anomalie="+v.failure); }
}
