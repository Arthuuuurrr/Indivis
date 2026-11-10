package net.hautecapitale.quetes.sequence;
import java.util.*;import java.util.function.*;import java.lang.reflect.*;
import net.fabricmc.api.ModInitializer;import net.minecraft.class_1297;import net.minecraft.class_3222;import net.minecraft.class_2960;
import net.hautecapitale.quetes.waypoint.ResolvedTarget;import net.hautecapitale.quetes.data.WaypointDef;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import net.hautecapitale.quetes.sequence.Pq01Motion.Point;
/** PQ04/PQ05 : scènes propriétaires, transactions uniques et itinéraires historiques. */
public final class Pq45Runtime implements ModInitializer {
 static final String SETUP="capitale:pq45_setup",STATE="capitale:pq45_scene",DM="net.hautecapitale.dialogue.session.DialogueManager",TAG="hcm_pq45_actor";
 static final Map<String,String> NAMES=Map.of("roch","Roch Vallet","colin","Colin Férand","aubergiste","Aubergiste des Profondeurs","magistrat","Magistrat des Profondeurs");
 static final Map<UUID,View> VIEWS=new HashMap<>();static final Set<UUID> LIVE=new HashSet<>();static final Map<UUID,Object> DIRTY=new LinkedHashMap<>();static final Map<UUID,Set<UUID>> HIDDEN=new HashMap<>();static final Set<String> ERRORS=new HashSet<>();
 static int clock,nonce=new java.security.SecureRandom().nextInt(50_000_000);static boolean initialized,teamReady;
 static final class Actor {final String role;final UUID id,source;final Object entity,goals,targets;Actor(String r,Object e,UUID source,Object g,Object t){role=r;entity=e;id=uuid(e);this.source=source;goals=g;targets=t;}}
 static final class Retreat {final Actor actor;int wp;Pq02Motion motion;Retreat(Actor a,int point){actor=a;wp=point;}}
 static final class View {
  final Object player,world;final UUID account,character;final Map<String,Actor> actors=new HashMap<>();final Set<UUID> hidden=new HashSet<>();final List<Retreat> retreats=new ArrayList<>();
  String role="roch",pendingNode,lastSpeaker="roch",failure="";Object session,focus,pendingFocus,acknowledgedSession,questMarker;Pq45Data.Node node;List<Pq45Data.Choice> choices=List.of();List<String> lastLines=List.of();
  int focusToken,focusSince,focusRetry,baseToken,voiceUntil,queuedChoice,frameAt,waypoint=-1,lastStage,waitCueAt,lorePending=-1,loreCueAt;boolean ui,suppress,walking,far,disposed;Pq02Motion motion;
  View(Object p){player=p;world=world(p);account=uuid(p);character=character(p);lastStage=stage(p);}
 }
 public void onInitialize(){init();}
 static boolean ready(Object p){return available(p)&&score(p,"hcq_pq03")>=100;}
 static int stage(Object p){int s=score(p,"hcq_pq04");return s<100?s:100+score(p,"hcq_pq05");}
 static Object config(Object p){return storage(p,SETUP);}
 static Object roleRecord(Object p,String role){return child(child(config(p),"roles"),role);}
 static Object characterScene(Object p,UUID id){return storage(p,STATE+"/"+id);}
 static String nodeScore(String role){return switch(role){case "roch"->"CAP_P4_RNODE";case "colin"->"CAP_P4_CNODE";case "aubergiste"->"CAP_P5_ANODE";default->"CAP_P5_MNODE";};}
 static String wpScore(String role){return role.equals("roch")?"CAP_P4_RWP":"CAP_P4_CWP";}
 static boolean guide(String role){return role.equals("roch")||role.equals("colin");}
 static int routeSize(String role){return role.equals("roch")?13:26;}
 static String routeTag(String role,int index){return "wp_"+role+"_profondeurs_"+(char)('a'+index);}
 static Point routePoint(Object p,String role,int i){Object record=child(child(config(p),"points"),role+"_"+(char)('a'+i));if(record==null)return null;Point at=point(record);return at.dimension().equals(dimension(world(p)))?at:null;}
 static void ensure(Object p){
  for(String s:Pq45Rules.SCORES)run(p,"scoreboard players add @s "+s+" 0");
  if(score(p,"CAP_P45_VER")==0){GuardNbt.save(server(p),STATE+"/"+character(p),GuardNbt.compound());int s=score(p,"hcq_pq04");score2(p,"CAP_P45_PAUSE",1);score2(p,"CAP_P4_RWP",s>=40?12:s==30?nearest(p,"roch"):-1);score2(p,"CAP_P4_CWP",s>=90?25:s==70?nearest(p,"colin"):-1);
   if(score(p,"CAP_PROF_CAISSE")==1){score2(p,"CAP_P4_RBOX",1);score2(p,"CAP_P4_LEGACY",1);}else if(score(p,"CAP_PROF_CAISSE")>=2){score2(p,"CAP_P4_RBOX",1);score2(p,"CAP_P4_PAID",1);}
   else if(s>30||s==30&&score(p,"CAP_P4_RWP")>7)score2(p,"CAP_P4_RBOX",2);
   if(s>=50)score2(p,"CAP_P4_RBYE",2);if(s>=100){score2(p,"CAP_P4_CBYE",2);score2(p,"CAP_P4_REWARD",1);score2(p,"CAP_P4_PASS",7);}
   if(s==70){int wp=score(p,"CAP_P4_CWP");score2(p,"CAP_P4_PASS",(wp>=3?1:0)|(wp>=7?2:0)|(wp>=23?4:0));}
   score2(p,"CAP_P45_VER",1);
  }
  if(score(p,"hcq_pq05")==90&&score(p,"CAP_CHAMBRE_PROF")==0)score2(p,"CAP_CHAMBRE_PROF",1);
  if(score(p,"hcq_pq04")>=100&&score(p,"hcq_pq05")<100){if(score(p,"CAP_RANGSOCIAL")>=20)finishResidence(p);else if(score(p,"CAP_CHAMBRE_PROF")>=1&&score(p,"hcq_pq05")!=90)run(p,"function hc_quetes:migration/write/pq05/v30");}
 }
 static int nearest(Object p,String role){int best=0;double d=Double.MAX_VALUE;for(int i=0;i<routeSize(role);i++){Point q=routePoint(p,role,i);if(q!=null&&q.dimension().equals(dimension(world(p)))){double n=q.distance2(pos(p,world(p)));if(n<d){d=n;best=i;}}}return best;}
 public static void init(){if(initialized)return;initialized=true;
  event("net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback","EVENT",a->{register(a[0]);return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick","END_SERVER_TICK",a->{tick(a[0]);return null;});
  event("net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents$Disconnect","DISCONNECT",a->{suspend(call(a[0],"method_32311"));flush();return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStopping","SERVER_STOPPING",a->{Pq45Entries.clear();for(View v:new ArrayList<>(VIEWS.values()))suspend(v.player);flush();return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStopped","SERVER_STOPPED",a->{Pq45Entries.clear();VIEWS.clear();LIVE.clear();HIDDEN.clear();DIRTY.clear();ERRORS.clear();clock=0;teamReady=false;return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents$Load","ENTITY_LOAD",a->{Object e=a[0];if(tags(e).contains(TAG)&&!LIVE.contains(uuid(e)))call(e,"method_31472");return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$EndDataPackReload","END_DATA_PACK_RELOAD",a->{for(View v:new ArrayList<>(VIEWS.values()))suspend(v.player);return null;});
  registerPublicClicks();customActions();
 }
 static void register(Object dispatcher){Object root=stat("com.mojang.brigadier.builder.LiteralArgumentBuilder","literal","indivispq45");
  for(String action:List.of("roch","colin","aubergiste","magistrat","dispatch","guide_valid","suspend","pause","restore","status","points","prepare_route","bind_roch","bind_colin","bind_aubergiste","bind_magistrat","test_roch","test_roch_h","test_colin","test_colin_d","test_colin_h","test_place","test_pq05")){
   Object node=stat("com.mojang.brigadier.builder.LiteralArgumentBuilder","literal",action);if(!Set.of("roch","colin","aubergiste","magistrat","dispatch","guide_valid","pause").contains(action))call(node,"requires",stat("net.minecraft.class_2170","method_71774",en("net.minecraft.class_2170","field_31839")));
   Class<?> c=type("com.mojang.brigadier.Command");Object cmd=Proxy.newProxyInstance(c.getClassLoader(),new Class<?>[]{c},(p,m,a)->m.getDeclaringClass()==Object.class?objectMethod(p,m,a):execute2(call(call(a[0],"getSource"),"method_9207"),action));call(node,"executes",cmd);
   if(action.startsWith("bind_")){
    Object id=stat("com.mojang.brigadier.builder.RequiredArgumentBuilder","argument","uuid",stat("com.mojang.brigadier.arguments.StringArgumentType","word"));
    call(id,"executes",Proxy.newProxyInstance(c.getClassLoader(),new Class<?>[]{c},(p,m,a)->m.getDeclaringClass()==Object.class?objectMethod(p,m,a):execute2(call(call(a[0],"getSource"),"method_9207"),action+" "+stat("com.mojang.brigadier.arguments.StringArgumentType","getString",a[0],"uuid"))));call(node,"then",id);
   }
   call(root,"then",node);
  }
  Object ready=stat("com.mojang.brigadier.builder.LiteralArgumentBuilder","literal","ready"),token=stat("com.mojang.brigadier.builder.RequiredArgumentBuilder","argument","token",stat("com.mojang.brigadier.arguments.IntegerArgumentType","integer",1));Class<?> c=type("com.mojang.brigadier.Command");call(token,"executes",Proxy.newProxyInstance(c.getClassLoader(),new Class<?>[]{c},(p,m,a)->m.getDeclaringClass()==Object.class?objectMethod(p,m,a):acknowledgeFocus(call(call(a[0],"getSource"),"method_9207"),((Number)stat("com.mojang.brigadier.arguments.IntegerArgumentType","getInteger",a[0],"token")).intValue())));call(ready,"then",token);call(root,"then",ready);call(dispatcher,"register",root);
 }
 public static int execute2(Object p,String action){try{
  if(action.equals("guide_valid"))return ready(p)&&Set.of(30,70).contains(score(p,"hcq_pq04"))?1:0;
  if(action.equals("status")){say2(p,"PQ04="+score(p,"hcq_pq04")+", PQ05="+score(p,"hcq_pq05")+", personnage="+character(p)+", caisse="+score(p,"CAP_PROF_CAISSE")+", chambre="+score(p,"CAP_CHAMBRE_PROF")+", rang="+score(p,"CAP_RANGSOCIAL")+", Roch="+score(p,"CAP_P4_RWP")+", Colin="+score(p,"CAP_P4_CWP"));return 1;}
  if(action.equals("prepare_route"))return Pq45Setup.prepare(p);if(action.equals("points")){Pq45Setup.describe(p);return 1;}
  if(action.startsWith("bind_")){String[] args=action.substring(5).split(" ",2);bind(p,args[0],args.length==2?UUID.fromString(args[1]):null);return 1;}if(action.startsWith("test_"))return Pq45Setup.test(p,action);
  if(action.equals("suspend")||action.equals("restore")){suspend(p);return 1;}if(action.equals("pause")){View v=VIEWS.get(uuid(p));if(v!=null)pause(v);return 1;}
  return Pq45Entries.submit(p,action);
 }catch(Throwable failure){report2(failure);say2(p,failure.getMessage());return 0;}}
 static boolean publicNpc(Object e){return e!=null&&type("de.markusbordihn.easynpc.entity.easynpc.EasyNPC").isInstance(e)&&!tags(e).contains(TAG)&&!tags(e).contains("hcm_private");}
 static String npcName(Object e){return String.valueOf(call(call(e,"method_5477"),"getString"));}
 static String normalizedName(String text){return java.text.Normalizer.normalize(text,java.text.Normalizer.Form.NFD).replaceAll("(?i)[§&][0-9a-fk-orx]","").replaceAll("\\p{M}+","").strip().replaceAll("\\s+"," ").toLowerCase(Locale.ROOT);}
 static boolean matchesRole(Object e,String role){
  String tag=switch(role){case "roch"->"npc_roch_vallet";case "colin"->"npc_colin_ferand";default->"npc_"+role+"_profondeurs";};
  Set<String> t=tags(e);if(t.contains(tag)||t.contains("hcq_pnj_"+role)||t.contains("cap_qgiver_"+role))return true;
  String name=normalizedName(npcName(e));if(name.equals(normalizedName(NAMES.get(role)))||name.equals(role))return true;
  String actions=actionText(e);String path="quest/"+(guide(role)?"au_seuil_des_profondeurs":"une_adresse_dans_les_profondeurs")+"/"+role+"/";
  return actions.contains(path)||actions.contains("capitale:npc/"+role+"/")||role.equals("colin")&&actions.contains("capitale:npc/colin_ferand/");
 }
 static Object source(Object p,String role){Object record=roleRecord(p,role);if(record!=null){if(!dimension(world(p)).equals(str(record,"dimension","")))return null;Object found=call(world(p),"method_66347",UUID.fromString(str(record,"uuid","")));if(publicNpc(found))return found;}
  List<Object> found=local(p,pos(p,world(p)),128,e->publicNpc(e)&&matchesRole(e,role));
  if(found.size()>1)throw new IllegalStateException("Liaison ambiguë : "+role+". Aucun PNJ choisi au hasard.");if(found.isEmpty())return null;saveBinding(p,role,found.get(0));return found.get(0);
 }
 static void bind(Object p,String role){bind(p,role,null);}
 static void bind(Object p,String role,UUID explicit){
  if(!NAMES.containsKey(role))throw new IllegalArgumentException("Rôle inconnu : "+role);
  Point origin=pos(p,world(p));List<Object> nearby=local(p,origin,4,e->publicNpc(e)&&pos(e,world(p)).distance2(origin)<=16);
  List<Object> found=explicit==null?nearby.stream().filter(e->matchesRole(e,role)).toList():nearby.stream().filter(e->uuid(e).equals(explicit)).toList();
  if(explicit==null&&found.isEmpty())found=nearby;
  if(found.size()!=1)throw new IllegalStateException(found.isEmpty()?"PNJ absent à moins de quatre blocs. Approcher le PNJ public et réessayer.":"Plusieurs PNJ possibles pour "+NAMES.get(role)+". Cibler son UUID : /indivispq45 bind_"+role+" <UUID>. PNJ proches : "+nearby.stream().map(e->npcName(e)+" ("+uuid(e)+")").toList());
  Object npc=found.get(0);UUID id=uuid(npc);
  for(View v:VIEWS.values()){Actor a=v.actors.get(role);if(a!=null&&!a.source.equals(id))throw new IllegalStateException("Suspendre les scènes de "+NAMES.get(role)+" avant de changer son PNJ source.");}
  saveBinding(p,role,npc);say2(p,"Liaison enregistrée : "+NAMES.get(role)+" ("+id+"). Apparence et actions publiques conservées.");
 }
 static void registerPublicClicks(){
  Class<?> contract=type("net.fabricmc.fabric.api.event.player.UseEntityCallback");Object event=en(contract.getName(),"EVENT"),phase=stat("net.minecraft.class_2960","method_60654","indivis:pq45_colin"),normal=en("net.fabricmc.fabric.api.event.Event","DEFAULT_PHASE");
  call(event,"addPhaseOrdering",phase,normal);
  Object listener=Proxy.newProxyInstance(contract.getClassLoader(),new Class<?>[]{contract},(p,m,a)->m.getDeclaringClass()==Object.class?objectMethod(p,m,a):publicClick(a[0],a[3],a[2]));
  call(event,"register",phase,listener);
 }
 static Object publicClick(Object p,Object e,Object hand){
  Object pass=en("net.minecraft.class_1269","field_5811");
  try{
   // Les copies privées gardent leur action propriétaire. Les autres quêtes et l'édition accroupie gardent leur interaction.
   if(!type("net.minecraft.class_3222").isInstance(p)||hand!=en("net.minecraft.class_1268","field_5808")||Boolean.TRUE.equals(call(p,"method_5715"))||!publicNpc(e)||!ready(p)||score(p,"hcq_pq04")<50)return pass;
   int active=score(p,"CAP_QUETEACTIVE");if(active!=0&&active!=5&&active!=6||pos(e,world(p)).distance2(pos(p,world(p)))>64)return pass;
   Object record=roleRecord(p,"colin");
   if(record!=null){if(!dimension(world(p)).equals(str(record,"dimension",""))||!uuid(e).toString().equals(str(record,"uuid","")))return pass;}
   else if(!matchesRole(e,"colin"))return pass;
   View v=VIEWS.get(uuid(p));Object other=current(p);if(other!=null&&(v==null||other!=v.session))return pass;
   if(record==null)saveBinding(p,"colin",e);
   // Préserver l'événement de conversation du journal habituellement émis dans la phase par défaut.
   try{stat("net.hautecapitale.quetes.interaction.NpcUseGuard","noteTalk",p);((Optional<?>)stat("net.hautecapitale.quetes.engine.QuestEngine","optional")).ifPresent(engine->call(engine,"onInteract",p,e));}catch(Throwable failure){report2(failure);}
   return Pq45Entries.submit(p,"colin")==1?en("net.minecraft.class_1269","field_21466"):pass;
  }catch(Throwable failure){report2(failure);say2(p,"Interaction Colin interrompue : "+failure.getMessage());return pass;}
 }
 static Actor spawn(View v,String role,Point at){Actor old=v.actors.get(role);if(old!=null&&!Boolean.TRUE.equals(call(old.entity,"method_31481")))return old;Object src=source(v.player,role),record=roleRecord(v.player,role);if(record==null||src==null&&(!guide(role)||at==null))throw new IllegalStateException("PNJ source absent/non chargé : "+role+". Faire sa liaison près du PNJ existant.");
  Object template=Pq01Nbt.copy(src==null?child(record,"template"):GuardNbt.entity(src));UUID original=src==null?UUID.fromString(str(record,"uuid","")):uuid(src);if(at==null)at=pos(src,v.world);if(!at.dimension().equals(dimension(v.world)))throw new IllegalStateException("Le PNJ appartient à une autre dimension.");
  for(String field:List.of("UUID","UUIDMost","UUIDLeast","Pos","Motion","Rotation","Passengers","Leash","Brain","Tags","Home","ActionData","ObjectiveData","TradingData"))call(template,"method_10551",field);
  UUID fresh=UUID.randomUUID();stat("de.markusbordihn.easynpc.utils.CompoundTagUtils","writeUUID",template,"UUID",fresh);
  GuardNbt.put(template,"Tags",GuardNbt.list(List.of(TAG,"hcm_private","hcm_pq45_"+role,"hcm_owner_"+v.account,"hcm_character_"+v.character,"indivis_no_hard_despawn").stream().map(GuardNbt::stringValue).toList()));
  call(template,"method_10556","Invulnerable",true);call(template,"method_10556","Silent",true);call(template,"method_10556","NoAI",!guide(role));call(template,"method_10556","NoGravity",false);call(template,"method_10556","CanPickUpLoot",false);GuardNbt.string(template,"DeathLootTable","minecraft:empty");
  LIVE.add(fresh);Object e=null,g=null,t=null;try{run(v.player,"summon "+str(record,"type","")+" "+at.x()+" "+at.y()+" "+at.z()+" "+template);e=call(v.world,"method_66347",fresh);if(e==null)throw new IllegalStateException("Copie EasyNPC non créée : "+role);configureActor(e,guide(role));call(e,"method_5665",stat("net.minecraft.class_2561","method_43470",NAMES.get(role)));
   g=call(e,"getEntityGoalSelector");t=call(e,"getEntityTargetSelector");Pq01Actors.protect(g);Pq01Actors.protect(t);call(g,"method_35113",(Predicate<Object>)goal->true);call(t,"method_35113",(Predicate<Object>)goal->true);
   Object row=GuardNbt.compound();GuardNbt.string(row,"Type","CUSTOM");GuardNbt.string(row,"Cmd","indivis:pq45 "+role);Pq01Actions.mutate(e,"ON_INTERACTION",GuardNbt.list(List.of(row)));if(guide(role))run(v.player,"attribute "+fresh+" minecraft:movement_speed base set 0.25");
   if(!teamReady){run(v.player,"team add indivis_pq45");run(v.player,"team modify indivis_pq45 collisionRule never");teamReady=true;}run(v.player,"team join indivis_pq45 "+fresh);
   Actor actor=new Actor(role,e,original,g,t);v.actors.put(role,actor);v.hidden.add(original);DIRTY.put(v.account,v.player);return actor;
  }catch(Throwable failure){LIVE.remove(fresh);if(g!=null)Pq01Actors.forget(g);if(t!=null)Pq01Actors.forget(t);if(e!=null)call(e,"method_31472");throw failure;}
 }
 static void customActions(){Class<?> c=type("de.markusbordihn.easynpc.api.action.CustomActionExecutor");Object cb=Proxy.newProxyInstance(c.getClassLoader(),new Class<?>[]{c},(proxy,m,a)->{if(m.getDeclaringClass()==Object.class)return objectMethod(proxy,m,a);if(!m.getName().equals("execute")||a==null||a.length!=4)return null;Object p=call(a[3],"initiator");if(p==null||!(a[2] instanceof List<?> words)||words.size()!=1)return null;String role=String.valueOf(words.get(0));View v=VIEWS.get(uuid(p));Object e=call(a[1],"getEntity");if(v!=null&&same(v)&&v.actors.containsKey(role)&&v.actors.get(role).entity==e&&pos(e,v.world).distance2(pos(p,v.world))<=64)open(p,role);return null;});stat("de.markusbordihn.easynpc.api.action.ActionRegistry","register",stat("net.minecraft.class_2960","method_60654","indivis:pq45"),cb);}
 static void report2(Throwable t){String msg=t.toString();if(ERRORS.add(msg)){System.err.println("[Indivis PQ04/PQ05 1.0] "+msg);t.printStackTrace(System.err);}}

 static void say2(Object p,String s){run(p,"tellraw @s "+quote("[PQ04/PQ05] "+s));}

 static boolean same(View v){return !v.disposed&&v.character.equals(character(v.player))&&v.world==world(v.player)&&score(v.player,"CAP_PROFILE_LOCK")==0;}

 static Object current(Object p){return ((Optional<?>)stat(DM,"session",p)).orElse(null);}

 static void score2(Object p,String name,int value){run(p,"scoreboard players set @s "+name+" "+value);}

 @SuppressWarnings("unchecked") static List<Object> local(Object p,Point at,double radius,Predicate<Object> filter){
  Object box=new net.minecraft.class_238(at.x()-radius,at.y()-32,at.z()-radius,at.x()+radius,at.y()+32,at.z()+radius);
  return (List<Object>)call(world(p),"method_8333",p,box,filter);
 }

 static Object unique(Object p,String kind,String tag,double radius,boolean required){
  List<Object> found=local(p,pos(p,world(p)),radius,e->tags(e).contains(tag)&&(kind.equals("marker")?Pq01EntityFactory.registryId(e).equals("minecraft:marker"):type("de.markusbordihn.easynpc.entity.easynpc.EasyNPC").isInstance(e)));
  if(found.size()>1)throw new IllegalStateException("Plusieurs entités portent "+tag+" ; aucune position choisie au hasard");
  if(found.isEmpty()&&required)throw new IllegalStateException("Entité absente/non chargée : "+tag);return found.isEmpty()?null:found.get(0);
 }

 static String actionText(Object e){Object set=call(call(e,"getEasyNPCActionEventData"),"getActionEventSet");return set==null?"":String.valueOf(call(set,"createTag"));}

 static void saveBinding(Object p,String role,Object npc){Object root=Pq01Nbt.copy(config(p)),r=GuardNbt.compound();GuardNbt.string(r,"uuid",uuid(npc).toString());GuardNbt.string(r,"dimension",dimension(world(p)));GuardNbt.string(r,"type",Pq01EntityFactory.registryId(npc));GuardNbt.put(r,"template",GuardNbt.entity(npc));GuardNbt.put(GuardNbt.childOrCreate(root,"roles"),role,r);GuardNbt.save(server(p),SETUP,root);}

 static View view(Object p){View old=VIEWS.get(uuid(p));if(old!=null&&!same(old)){dispose(old.account);old=null;}if(old!=null)return old;View v=new View(p);VIEWS.put(v.account,v);HIDDEN.put(v.account,v.hidden);return v;}
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

 static void requestFocus(View v){if(v.session==null||clock<v.focusRetry)return;v.focusRetry=clock+20;int id=((Number)Pq01Client.field(v.session,"id")).intValue();UUID actor=(UUID)Pq01Client.field(v.session,"pnj");run(v.player,"tellraw @s {\"text\":\"\",\"insertion\":"+quote(Pq45Focus.encode(new Pq45Focus.Request(id,actor,v.focusToken)))+"}");}

 static int voice(View v,String key,boolean menu){if(menu)return 0;Object r=child(child(config(v.player),"voices"),key);if(r==null)return 0;String sound=str(r,"sound","");int ticks=integer(r,"ticks",0);if(!sound.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")||ticks<1||ticks>12000)return 0;run(v.player,"playsound "+sound+" voice @s ~ ~ ~ 1 1");return ticks;}

 static void stopActor(Actor a){if(a==null)return;Object nav=call(a.entity,"getGroundPathNavigation");if(nav!=null)call(nav,"method_6340");call(a.entity,"method_18799",en("net.minecraft.class_243","field_1353"));}

 static boolean loaded(Object world,Point at){return ((net.minecraft.class_3218)world).method_8393((int)Math.floor(at.x())>>4,(int)Math.floor(at.z())>>4);}

 static void flush(){if(DIRTY.isEmpty())return;for(UUID id:new HashSet<>(DIRTY.keySet())){Object p=DIRTY.get(id);if(p==null)continue;stat("net.hautecapitale.quetes.visibility.QuestVisibility","refresh",p);}DIRTY.clear();}
 static void open(Object p,String role){if(!ready(p)||!NAMES.containsKey(role))return;ensure(p);int s=score(p,"hcq_pq04");
  if(role.equals("roch")&&s>=50){View old=VIEWS.get(uuid(p));if(old!=null&&old.retreats.stream().anyMatch(r->r.actor.role.equals("roch")))return;}
  if(role.equals("colin")&&s<50)return;if(!guide(role)&&s<100&&role.equals("aubergiste"))return;
  if(role.equals("magistrat")&&s>=100&&score(p,"hcq_pq05")>=100&&score(p,"CAP_RANGSOCIAL")>=20&&score(p,"CAP_RANGSOCIAL")<30&&score(p,"hcq_sqhcci01")==10){run(p,"function capitale:quest/une_adresse_dans_les_profondeurs/magistrat/legacy_interact_self");return;}
  View v=VIEWS.get(uuid(p));if(v!=null&&!same(v)){dispose(v.account);v=null;}Object src=source(p,role);Object binding=roleRecord(p,role);if(binding!=null&&!dimension(world(p)).equals(str(binding,"dimension","")))return;if(src==null&&(!guide(role)||roleRecord(p,role)==null))return;
  if(role.equals("colin")&&(s<70||s>=100)&&src!=null&&Math.abs(pos(src,world(p)).y()-pos(p,world(p)).y())>3)return;
  Object other=current(p);if(other!=null&&(v==null||other!=v.session)){UUID id=(UUID)Pq01Client.field(other,"pnj");if(!id.equals(src==null?UUID.fromString(str(roleRecord(p,role),"uuid","")):uuid(src)))return;}
  int active=score(p,"CAP_QUETEACTIVE");if(active!=0&&active!=5&&active!=6)return;
  if(v==null)v=view(p);if(v.ui&&!v.role.equals(role))closeUi(v);
  if(!v.role.equals(role)){pause(v);v.lastLines=List.of();}v.role=role;v.waypoint=guide(role)?score(p,wpScore(role)):-1;
  Actor a=v.actors.get(role);Point start=guide(role)?startPosition(v):null;
  if(a==null&&(src==null||pos(src,v.world).distance2(pos(p,v.world))>144)&&(start==null||start.distance2(pos(p,v.world))>144)){say2(p,NAMES.get(role)+" vous attend à son dernier point du parcours.");return;}
  a=spawn(v,role,start);if(pos(a.entity,v.world).distance2(pos(p,v.world))>144){say2(p,NAMES.get(role)+" vous attend à son dernier point du parcours.");return;}
  pause(v);v.lastStage=stage(p);Pq45Data.Node remembered=Pq45Data.IDS.get(score(p,nodeScore(role)));String key=remembered!=null&&Pq45Rules.validNode(remembered.key,p,role)?remembered.key:Pq45Rules.initial(p,role,v.waypoint);
  if(role.equals("roch")&&s<30&&s!=10)run(p,"function hc_quetes:migration/write/pq04/v1");
  if(!guide(role)&&score(p,"hcq_pq04")>=100&&score(p,"hcq_pq05")<100)score2(p,"CAP_QUETEACTIVE",6);
  if(role.equals("aubergiste")&&score(p,"hcq_pq05")<90)run(p,"function hc_quetes:migration/write/pq05/v20");
  show(v,key);
 }
 static Object focus(View v){Actor a=v.actors.get(v.role);if(a==null)a=spawn(v,v.role,guide(v.role)?startPosition(v):null);faceEntity(a.entity,pos(v.player,v.world));return a.entity;}
 static boolean condition(View v,String name){return name==null||name.isEmpty()||switch(name){case "crate"->hasCrate(v.player);case "view_undecided"->score(v.player,"CAP_P4_CVIEW")==0;case "view_decided"->score(v.player,"CAP_P4_CVIEW")>0;default->false;};}
 static String resolve(View v,String key){Object p=v.player;if(key.equals("r_crate")&&score(p,"CAP_P4_RBOX")>0)return "r_box"+Math.min(4,score(p,"CAP_P4_RBOX"));if(key.equals("r_final")&&score(p,"CAP_P4_RBYE")>0)return "r_bye"+Math.min(3,score(p,"CAP_P4_RBYE"));if(key.equals("c_final")&&score(p,"CAP_P4_CBYE")>0)return "c_bye"+Math.min(4,score(p,"CAP_P4_CBYE"));if(key.equals("a_paid")&&score(p,"CAP_CHAMBRE_PROF")==0)return "a_rent";if(key.equals("a_rent")&&score(p,"CAP_CHAMBRE_PROF")>0)return "a_room";if(key.equals("m_fee")&&score(p,"hcq_pq05")>=100)return "m_done";return key;}
 static void show(View v,String key){if(!same(v))return;if(key.startsWith("event:")){event2(v,key.substring(6));return;}key=resolve(v,key);Pq45Data.Node n=Pq45Data.get(key);if(!n.speaker.equals(v.role)||!Pq45Rules.validNode(key,v.player,v.role))return;pause(v);v.node=n;v.baseToken=0;v.queuedChoice=0;score2(v.player,nodeScore(v.role),n.id);v.ui=true;v.pendingFocus=focus(v);v.pendingNode=key;v.frameAt=clock+1;}
 static int acknowledgeFocus(Object p,int token){View v=VIEWS.get(uuid(p));if(v==null||!same(v)||v.pendingNode==null||v.session==null||v.session!=current(p)||v.focusToken!=token)return 0;v.acknowledgedSession=v.session;return 1;}
 static List<String> lines(View v){List<String> lines=new ArrayList<>(v.node.lines);if(v.node.key.equals("r_final")&&score(v.player,"CAP_PROF_CAISSE")==1)lines.add("Et pensez à lui remettre la caisse. Je doute qu'il apprécie de devoir remonter la chercher.");
  if(Set.of("r_intro","c_intro","a_intro").contains(v.node.key)){String variant=Pq45Rules.greeting(v.player,v.role);if(!variant.isEmpty()){if(v.role.equals("roch")&&lines.size()>1)lines.set(1,variant);else if(!lines.isEmpty())lines.set(0,variant);}}
  if(lines.isEmpty())lines=v.lastSpeaker.equals(v.role)&&!v.lastLines.isEmpty()?v.lastLines:List.of(v.role.equals("roch")?"Je vous écoute.":v.role.equals("colin")?"Vous aviez une question ?":"Que puis-je pour vous ?");return lines;
 }
 static void render(View v){if(!same(v)||v.node==null||!focusReady(v))return;var n=v.node;v.choices=n.choices.stream().filter(c->condition(v,c.condition)).toList();v.baseToken=1_000_000_000+((++nonce)%60_000_000)*16;int advance=n.next.isEmpty()?0:v.baseToken+14;List<String> texts=lines(v);v.lastLines=texts;v.lastSpeaker=v.role;int ticks=voice(v,n.key,n.lines.isEmpty());v.voiceUntil=clock+ticks;
  run(v.player,"tellraw @s {\"text\":\"\",\"insertion\":"+quote("indivis:pq45:v1:"+n.key+":"+v.role+":"+advance+":0:"+ticks)+"}");
  for(String line:texts)run(v.player,"tellraw @s [{\"text\":"+quote("["+NAMES.get(v.role)+"]")+",\"color\":\"yellow\"},{\"text\":"+quote(" : "+line)+",\"color\":\"white\"}]");
  for(int i=0;i<v.choices.size();i++){var c=v.choices.get(i);String color=c.target.startsWith("event:")||Set.of("r_start","c_start","a_paid","m_paid").contains(c.target)?"green":"white";run(v.player,"tellraw @s {\"text\":"+quote("["+c.text+"]")+",\"color\":"+quote(color)+",\"click_event\":{\"action\":\"run_command\",\"command\":"+quote("/trigger Pq45Choix set "+(v.baseToken+i+1))+"}}");}
  // La suite se lit à la demande ; aucune fermeture automatique ni minuterie de lecture.
  if(n.next.startsWith("event:"))run(v.player,"tellraw @s {\"text\":"+quote("["+Pq45Rules.eventLabel(n.next)+"]")+",\"color\":\"green\",\"click_event\":{\"action\":\"run_command\",\"command\":"+quote("/trigger Pq45Choix set "+(v.baseToken+14))+"}}");
  run(v.player,"tellraw @s {\"text\":\"[Fermer la conversation]\",\"color\":\"gray\",\"click_event\":{\"action\":\"run_command\",\"command\":"+quote("/trigger Pq45Choix set "+(v.baseToken+15))+"}}");
  if(v.role.equals("colin")&&v.lorePending>=0&&v.lorePending<25){int bit=v.lorePending==3?1:v.lorePending==7?2:4;score2(v.player,"CAP_P4_PASS",score(v.player,"CAP_P4_PASS")|bit);if(v.lorePending==3||v.lorePending==7)call(v.actors.get("colin").entity,"method_5780",v.lorePending==3?"escort_pause_d_done":"escort_pause_h_done");v.lorePending=-1;savePosition(v);}
  score2(v.player,"CAP_QDIALOG_OWNER",99);score2(v.player,"CAP_QDIALOG_KEEP",1);score2(v.player,"hcm_pq45_ui",1);score2(v.player,"Pq45Choix",0);run(v.player,"scoreboard players enable @s Pq45Choix");
 }
 static int dispatch(View v){Object p=v.player;int token=score(p,"Pq45Choix");score2(p,"Pq45Choix",0);if(!same(v)||!v.ui||v.session!=current(p)||v.pendingNode!=null||v.node==null||score(p,"CAP_QDIALOG_OWNER")!=99)return 0;int pick=token-v.baseToken;
  if(v.baseToken==0||pick<1||pick>15){run(p,"scoreboard players enable @s Pq45Choix");return 0;}
  if(pick==15){String agreed=v.node.next;if(agreed.startsWith("event:")&&Set.of("roch_start","colin_start","roch_end","colin_end","citizenship").contains(agreed.substring(6)))event2(v,agreed.substring(6));else{closeUi(v);if(canResume(v)&&v.lorePending<0)resume(v);else if(!guide(v.role)||stage(p)>=200)releaseConversation(v);}return 1;}
  if(clock<v.voiceUntil){v.queuedChoice=token;run(p,"scoreboard players enable @s Pq45Choix");return 1;}
  if(pick==14){if(v.node.next.isEmpty()){run(p,"scoreboard players enable @s Pq45Choix");return 0;}show(v,v.node.next);return 1;}
  if(pick>v.choices.size()){run(p,"scoreboard players enable @s Pq45Choix");return 0;}var c=v.choices.get(pick-1);if(!condition(v,c.condition)){render(v);return 0;}
  String failure=effect(v,c.effect);show(v,failure.isEmpty()?c.target:failure);return 1;
 }
 static void addSigned(Object p,String objective,int delta){if(delta!=0)run(p,"scoreboard players "+(delta<0?"remove":"add")+" @s "+objective+" "+Math.abs(delta));}
 static void rep(Object p,String impression,int delta,boolean faction){if(faction){int before=score(p,"REP_GARDEPROFONDEURS");addSigned(p,"REP_GARDEPROFONDEURS",delta);run(p,"function capitale:bounds/reputation_all_self");QuestReputationNotice.changed(p,"profondeurs",before,score(p,"REP_GARDEPROFONDEURS"));}addSigned(p,impression,delta);}
 static String effect(View v,String effect){if(effect==null||effect.isEmpty())return "";Object p=v.player;String[] parts=effect.split(":");switch(parts[0]){
  case "crate"->{int value=Integer.parseInt(parts[1]);if(score(p,"hcq_pq04")!=30||v.waypoint!=7||score(p,"CAP_P4_RBOX")!=0)return "r_travel";if(value==1){if(!Pq45Items.freeSlot(p))return "r_inventory";score2(p,"CAP_P4_RBOX",1);score2(p,"CAP_PROF_CAISSE",1);run(p,"give @s "+Pq45Items.crate(p)+" 1");}else{score2(p,"CAP_P4_RBOX",value);if(value==4)rep(p,"CAP_ROCH_IMPR",-1,true);}call(v.actors.get("roch").entity,"method_5780","escort_crate_h_resolved");v.lorePending=-1;}
  case "roch_bye"->{if(score(p,"hcq_pq04")!=40||score(p,"CAP_P4_RBYE")>0)return "r_final";int value=Integer.parseInt(parts[1]);score2(p,"CAP_P4_RBYE",value);if(value==1)rep(p,"CAP_ROCH_IMPR",1,true);}
  case "colin_view"->{if(score(p,"hcq_pq04")>=100||score(p,"CAP_P4_CVIEW")>0)return "c_questions";int value=Integer.parseInt(parts[1]);score2(p,"CAP_P4_CVIEW",value);if(value==1)rep(p,"CAP_COLIN_IMPR",1,false);if(value==4)rep(p,"CAP_COLIN_IMPR",-1,true);}
  case "colin_bye"->{if(score(p,"hcq_pq04")!=90||score(p,"CAP_P4_CBYE")>0)return "c_final";int value=Integer.parseInt(parts[1]);score2(p,"CAP_P4_CBYE",value);if(value==1)rep(p,"CAP_COLIN_IMPR",1,false);}
  case "deliver"->{if(!hasCrate(p))return score(p,"hcq_pq04")>=100?"c_post":"c_offer";if(!Pq45Items.freeSlot(p))return "c_inventory";if(Pq45Items.removeCrate(p)!=1)return "c_offer";score2(p,"CAP_P4_PAID",1);score2(p,"CAP_PROF_CAISSE",2);run(p,"give @s capitale_currency:martin_dor 25");money(p,25);rep(p,"CAP_COLIN_IMPR",1,true);run(p,"function capitale:rewards/daily_bonus/roll/profondeurs_self");run(p,"function capitale:reward/skills/core/seuil_profondeurs_self");}
  case "rent"->{if(score(p,"hcq_pq04")<100)return "a_later";if(score(p,"CAP_CHAMBRE_PROF")>0)return "a_room";if(!Pq45Items.debit(p,20))return "a_funds";score2(p,"CAP_CHAMBRE_PROF",1);run(p,"function hc_quetes:migration/write/pq05/v30");money(p,-20);objective(p,"Présenter votre hébergement au magistrat des Profondeurs.");}
  case "register"->{if(score(p,"CAP_RANGSOCIAL")>=20||score(p,"hcq_pq05")>=100){finishResidence(p);return "m_done";}if(score(p,"CAP_CHAMBRE_PROF")==0)return "m_early";if(!Pq45Items.debit(p,100))return "m_funds";score2(p,"CAP_P5_PAID",1);finishResidence(p);money(p,-100);}
  default->throw new IllegalArgumentException(effect);
 }savePosition(v);return "";}
 static void money(Object p,int delta){QuestReputationNotice.changed(p,"mdi",0,delta);}
 static boolean hasCrate(Object p){return score(p,"CAP_PROF_CAISSE")==1&&score(p,"CAP_P4_PAID")==0&&Pq45Items.crateCount(p)>0;}
 static void finishResidence(Object p){if(score(p,"hcq_pq05")>=100)return;if(score(p,"CAP_RANGSOCIAL")<20)score2(p,"CAP_RANGSOCIAL",20);run(p,"function capitale:bounds/rangsocial_self");run(p,"function capitale:access/recalculate_by_rank_self");run(p,"function capitale:display/prefix/sync_self");run(p,"function hc_quetes:migration/write/pq05/v100");score2(p,"QUEST_PROLOGUE",100);if(score(p,"CAP_QUETEACTIVE")==5||score(p,"CAP_QUETEACTIVE")==6)score2(p,"CAP_QUETEACTIVE",0);}
 static void objective(Object p,String text){run(p,"tellraw @s {\"text\":"+quote("[Objectif] "+text)+",\"color\":\"gold\"}");}
 static void event2(View v,String event){Object p=v.player;if(!same(v))return;int s=score(p,"hcq_pq04");switch(event){
  case "roch_start"->{if(s>=30)return;validateRoute(p,"roch");closeUi(v);run(p,"function hc_quetes:migration/write/pq04/v20");v.waypoint=-1;score2(p,"CAP_P4_RWP",-1);score2(p,"CAP_QUETEACTIVE",5);resume(v);remark(v,"r_walk");objective(p,"Suivre Roch jusqu'à l'ascenseur inférieur.");}
  case "colin_start"->{if(s<50||s>=70)return;validateRoute(p,"colin");closeUi(v);run(p,"function hc_quetes:migration/write/pq04/v50");v.waypoint=-1;score2(p,"CAP_P4_CWP",-1);score2(p,"CAP_QUETEACTIVE",5);resume(v);}
  case "resume"->{closeUi(v);v.lorePending=-1;resume(v);}
  case "colin_context"->{if(s>=90&&s<100)show(v,"c_final");else if(s>=100)show(v,"c_post");else if(s==70){closeUi(v);v.lorePending=-1;resume(v);}else show(v,"c_offer");}
  case "roch_end"->{if(s!=40||score(p,"CAP_P4_RBYE")==0)return;closeUi(v);run(p,"function hc_quetes:migration/write/pq04/v35");score2(p,nodeScore("roch"),Pq45Data.get("r_post").id);unlock(p);objective(p,"Descendre au poste inférieur et parler à Colin Férand.");startRetreat(v,"roch",12);}
  case "colin_end"->{if(s!=90||score(p,"CAP_P4_CBYE")==0)return;closeUi(v);score2(p,"CAP_P4_REWARD",1);run(p,"function hc_quetes:migration/write/pq04/v100");score2(p,"QUEST_PROLOGUE",50);score2(p,"CAP_QUETEACTIVE",0);unlock(p);run(p,"function capitale:dialogue/sound/gain_quete_self");objective(p,"Parler à l'aubergiste.");score2(p,nodeScore("colin"),Pq45Data.get("c_post").id);startRetreat(v,"colin",25);}
  case "citizenship"->{closeUi(v);if(score(p,"hcq_pq05")>=100&&score(p,"CAP_RANGSOCIAL")>=20&&score(p,"CAP_RANGSOCIAL")<30&&score(p,"hcq_sqhcci01")==0)run(p,"function capitale:quest/principal/citoyennete/start_self");releaseConversation(v);}
  case "leave"->{closeUi(v);if(!guide(v.role))releaseConversation(v);}
  default->throw new IllegalArgumentException(event);
 }}
 static void unlock(Object p){score2(p,"CAP_GUIDE_LOCK",0);score2(p,"CAP_GUIDE_ID",0);score2(p,"CAP_GUIDE_MISS_T",0);}
 static void remark(View v,String key){for(String text:Pq45Data.get(key).lines)run(v.player,"tellraw @s [{\"text\":"+quote("["+NAMES.get(v.role)+"]")+",\"color\":\"yellow\"},{\"text\":"+quote(" : "+text)+",\"color\":\"white\"}]");voice(v,key,false);}
 static boolean canResume(View v){return v.role.equals("roch")&&score(v.player,"hcq_pq04")==30&&(v.waypoint!=7||score(v.player,"CAP_P4_RBOX")>0)||v.role.equals("colin")&&score(v.player,"hcq_pq04")==70;}
 static Point startPosition(View v){int phase=score(v.player,"hcq_pq04");if(v.role.equals("roch")&&phase>=50||v.role.equals("colin")&&(phase<70||phase>=100))return null;Object row=child(characterScene(v.player,v.character),v.role);if(row!=null&&str(row,"owner","").equals(v.account.toString())&&integer(row,"stage",-1)==score(v.player,"hcq_pq04")&&integer(row,"waypoint",-2)==v.waypoint){Object n=child(row,"position");if(n!=null){Point at=point(n);if(at.dimension().equals(dimension(v.world)))return at;}}
  if(v.waypoint>=0)return routePoint(v.player,v.role,v.waypoint);return null;}
 static void savePosition(View v){if(v.disposed||!guide(v.role))return;Actor a=v.actors.get(v.role);if(a==null)return;Object root=Pq01Nbt.copy(characterScene(v.player,v.character)),row=GuardNbt.compound();GuardNbt.string(row,"owner",v.account.toString());GuardNbt.integer(row,"stage",same(v)?score(v.player,"hcq_pq04"):v.lastStage);GuardNbt.integer(row,"waypoint",v.waypoint);GuardNbt.integer(row,"lore",v.lorePending);GuardNbt.put(row,"position",pointNbt(pos(a.entity,v.world)));if(row.equals(child(root,v.role)))return;GuardNbt.put(root,v.role,row);GuardNbt.save(server(v.player),STATE+"/"+v.character,root);}
 static void pause(View v){for(Retreat r:v.retreats){if(r.motion!=null){r.motion.pause();r.motion=null;}stopActor(r.actor);}if(v.motion!=null){v.motion.pause();v.motion=null;}stopActor(v.actors.get(v.role));v.walking=false;if(same(v))score2(v.player,"CAP_P45_PAUSE",1);savePosition(v);}
 static void resume(View v){if(!same(v)||!canResume(v))return;v.walking=true;v.far=false;v.failure="";v.lorePending=-1;score2(v.player,"CAP_P45_PAUSE",0);score2(v.player,nodeScore(v.role),Pq45Data.get(v.role.equals("roch")?"r_travel":"c_travel").id);savePosition(v);nextLeg(v);}
 static void validateRoute(Object p,String role){for(int i=0;i<routeSize(role);i++)if(routePoint(p,role,i)==null)throw new IllegalStateException("Repère absent/non mémorisé : "+role+" "+(char)('A'+i)+". Exécuter la préparation du parcours sans déplacer les repères.");}
 static boolean contextStop(View v,int point){if(v.role.equals("roch"))return point==7&&score(v.player,"CAP_P4_RBOX")==0;if(point==3)return (score(v.player,"CAP_P4_PASS")&1)==0;if(point==7)return (score(v.player,"CAP_P4_PASS")&2)==0;if(point==23)return (score(v.player,"CAP_P4_PASS")&4)==0;return false;}
 static String contextKey(String role,int point){return role.equals("roch")?"r_crate":point==3?"c_central":point==7?"c_bridges":"c_outer";}
 static void nextLeg(View v){if(!same(v)||!v.walking||v.ui)return;Actor guide=v.actors.get(v.role);if(guide==null)return;int target=v.waypoint+1;if(target>=routeSize(v.role)){arrived(v);return;}Point point=routePoint(v.player,v.role,target);if(point==null||!loaded(v.world,point)){stopActor(guide);v.failure="Passage absent ou non chargé ; le guide attend.";if(clock>=v.waitCueAt){v.waitCueAt=clock+200;say2(v.player,v.failure);}return;}
  v.failure="";v.motion=motion(v,guide,point,()->same(v)&&v.walking&&!v.ui,()->target==routeSize(v.role)-1||contextStop(v,target),()->{v.motion=null;v.waypoint=target;score2(v.player,wpScore(v.role),target);if(target==routeSize(v.role)-1)arrived(v);else if(contextStop(v,target)){pause(v);v.lorePending=target;v.loreCueAt=clock;score2(v.player,nodeScore(v.role),Pq45Data.get(contextKey(v.role,target)).id);savePosition(v);offerLore(v);}else nextLeg(v);},reason->{v.motion=null;pause(v);v.failure=reason;say2(v.player,NAMES.get(v.role)+" attend. Reprendre la discussion pour réessayer : "+reason);});v.motion.start();
 }
 static Pq02Motion motion(View v,Actor a,Point point,BooleanSupplier valid,BooleanSupplier stop,Runnable arrived,Consumer<String> fail){return new Pq02Motion(new Pq02Motion.Port(){public boolean valid(){return valid.getAsBoolean()&&!Boolean.TRUE.equals(call(a.entity,"method_31481"));}public Point position(){return pos(a.entity,v.world);}public boolean navigate(Point p){Object nav=call(a.entity,"getGroundPathNavigation");if(nav==null)return false;if(Boolean.TRUE.equals(call(nav,"method_58160",p.x(),p.y(),p.z(),0,1.2d)))return true;return Math.abs(p.y()-Math.ceil(p.y()))>0.01&&Boolean.TRUE.equals(call(nav,"method_58160",p.x(),Math.ceil(p.y()),p.z(),0,1.2d));}public boolean navigating(){Object nav=call(a.entity,"getGroundPathNavigation");return nav!=null&&!Boolean.TRUE.equals(call(nav,"method_6357"));}public void stop(){stopActor(a);}public boolean stopOnArrival(){return stop.getAsBoolean();}public void arrived(){arrived.run();}public void failed(String reason){fail.accept(reason);}},point,2400);}
 static void arrived(View v){pause(v);Object p=v.player;if(v.role.equals("roch")){run(p,"function hc_quetes:migration/write/pq04/v30");v.lastStage=stage(p);v.lorePending=12;score2(p,nodeScore(v.role),Pq45Data.get("r_final").id);}else{run(p,"function hc_quetes:migration/write/pq04/v60");v.lastStage=stage(p);v.lorePending=25;unlock(p);score2(p,nodeScore(v.role),Pq45Data.get("c_final").id);}savePosition(v);offerLore(v);}
 static void offerLore(View v){if(v.lorePending<0||!same(v)||v.ui)return;Actor a=v.actors.get(v.role);if(a==null)return;if(clock>=v.loreCueAt){v.loreCueAt=clock+100;run(v.player,"title @s actionbar {\"text\":"+quote(NAMES.get(v.role)+" s'arrête et se tourne vers vous.")+",\"color\":\"gold\"}");}faceEntity(a.entity,pos(v.player,v.world));double range=Math.min(4,cameraRange());if(current(v.player)==null&&pos(a.entity,v.world).distance2(pos(v.player,v.world))<=range*range){String key=score(v.player,"hcq_pq04")==40?"r_final":score(v.player,"hcq_pq04")==90?"c_final":contextKey(v.role,v.lorePending);show(v,key);}}
 static void startRetreat(View v,String role,int wp){Actor a=v.actors.get(role);if(a!=null)v.retreats.add(new Retreat(a,wp));v.walking=false;v.lorePending=-1;}
 static void retireTick(View v,Retreat r){if(v.ui)return;Actor a=r.actor;if(r.wp<=0||pos(a.entity,v.world).distance2(pos(v.player,v.world))>64*64&&!Boolean.TRUE.equals(call(v.player,"method_6057",a.entity))){removeActor(v,a.role);v.retreats.remove(r);return;}
  if(r.motion!=null){r.motion.tick();return;}Point target=routePoint(v.player,a.role,r.wp-1);if(target==null||!loaded(v.world,target))return;r.motion=motion(v,a,target,()->same(v)&&!v.ui,()->r.wp==1,()->{r.motion=null;r.wp--;},reason->{r.motion=null;});r.motion.start();}
 static void removeActor(View v,String role){Actor a=v.actors.remove(role);if(a==null)return;stopActor(a);Pq01Actors.forget(a.goals);Pq01Actors.forget(a.targets);LIVE.remove(a.id);call(a.entity,"method_31472");v.hidden.remove(a.source);DIRTY.put(v.account,v.player);}
 static void releaseConversation(View v){if(!guide(v.role)||!canResume(v)&&v.lorePending<0&&!Set.of(40,90).contains(score(v.player,"hcq_pq04"))&&v.retreats.stream().noneMatch(r->r.actor.role.equals(v.role)))removeActor(v,v.role);if(v.actors.isEmpty())dispose(v.account);}
 static void closeUi(View v){v.suppress=true;try{if(v.session!=null&&v.session==current(v.player))stat(DM,"fermer",v.player,en("net.hautecapitale.dialogue.session.CloseReason","ECRAN_FERME"));}finally{v.suppress=false;}clearUi(v);}
 static void clearUi(View v){v.session=null;v.acknowledgedSession=null;v.focusToken=0;v.focus=null;v.pendingFocus=null;v.pendingNode=null;v.ui=false;v.baseToken=0;v.queuedChoice=0;if(same(v)){score2(v.player,"hcm_pq45_ui",0);score2(v.player,"Pq45Choix",0);if(score(v.player,"CAP_QDIALOG_OWNER")==99){score2(v.player,"CAP_QDIALOG_OWNER",0);score2(v.player,"CAP_QDIALOG_KEEP",0);}}}
 public static void onClosing(Object p,Object session,String reason){View v=VIEWS.get(uuid(p));if(v==null||v.suppress||v.session!=session)return;clearUi(v);if(!same(v))return;if(Pq02Transitions.playerClosed(reason)){String next=v.node==null?"":v.node.next;if(next.startsWith("event:")&&Set.of("roch_start","colin_start","roch_end","colin_end","citizenship").contains(next.substring(6))){event2(v,next.substring(6));return;}if(canResume(v)&&v.lorePending<0){resume(v);return;}}pause(v);releaseConversation(v);}
 public static void suspend(Object p){View v=VIEWS.get(uuid(p));if(Pq45ProfileCheckpoint.preserving(p)){if(v!=null)savePosition(v);return;}Pq45Entries.cancel(uuid(p));if(v==null)return;boolean moving=same(v)&&v.walking&&!v.ui;pause(v);if(moving)score2(p,"CAP_P45_PAUSE",0);savePosition(v);closeUi(v);dispose(v.account);}
 static void dispose(UUID id){Pq45Entries.cancel(id);View v=VIEWS.remove(id);if(v==null)return;if(v.motion!=null)v.motion.pause();for(Retreat r:v.retreats)if(r.motion!=null)r.motion.pause();for(String role:new ArrayList<>(v.actors.keySet()))removeActor(v,role);v.disposed=true;if(v.questMarker!=null){LIVE.remove(uuid(v.questMarker));call(v.questMarker,"method_31472");}HIDDEN.remove(id);DIRTY.put(id,v.player);}
 public static boolean visibleOriginal(class_1297 e,class_3222 p){if(e.method_5752().contains(TAG)){View v=VIEWS.get(p.method_5667());return v!=null&&same(v)&&LIVE.contains(e.method_5667())&&e.method_5752().contains("hcm_owner_"+v.account)&&e.method_5752().contains("hcm_character_"+v.character);}Set<UUID> hidden=HIDDEN.get(p.method_5667());return hidden==null||!hidden.contains(e.method_5667());}
 public static Optional<ResolvedTarget> waypoint(Object p,String tag){String role=tag.substring("hcm_pq45_".length());if(!guide(role)||!ready(p))return Optional.empty();View v=VIEWS.get(uuid(p));Point at=null;if(v!=null&&same(v)&&v.actors.containsKey(role))at=pos(v.actors.get(role).entity,v.world);else{Object row=child(characterScene(p,character(p)),role);if(row!=null&&str(row,"owner","").equals(uuid(p).toString())&&integer(row,"stage",-1)==score(p,"hcq_pq04")&&child(row,"position")!=null)at=point(child(row,"position"));}if(at==null){Object src=source(p,role);if(src!=null)at=pos(src,world(p));}return at==null?Optional.empty():Optional.of(new ResolvedTarget(WaypointDef.Mode.EXACT,class_2960.method_60654(at.dimension()),at.x(),at.y(),at.z(),0,NAMES.get(role)));}
 static void updateMarker(View v){Actor a=guide(v.role)?v.actors.get(v.role):null;boolean useful=a!=null&&!v.retreats.stream().anyMatch(r->r.actor==a)&&score(v.player,"hcq_pq04")<100;if(!useful){if(v.questMarker!=null){LIVE.remove(uuid(v.questMarker));call(v.questMarker,"method_31472");v.questMarker=null;}return;}Point at=pos(a.entity,v.world);if(v.questMarker==null){UUID id=UUID.randomUUID();LIVE.add(id);run(v.player,"summon minecraft:text_display "+at.x()+" "+(at.y()+3.05)+" "+at.z()+" {UUID:"+uuidArray(id)+",Tags:[\"hcm_pq45_actor\",\"hcm_owner_"+v.account+"\",\"hcm_character_"+v.character+"\"],text:{text:\"?\",color:\"yellow\",bold:true},billboard:\"center\",background:0,see_through:1b,brightness:{sky:15,block:15}}");v.questMarker=call(v.world,"method_66347",id);}else call(v.questMarker,"method_5814",at.x(),at.y()+3.05,at.z());}
 static void tick(Object server){clock++;@SuppressWarnings("unchecked") List<Object> players=(List<Object>)call(call(server,"method_3760"),"method_14571");Set<UUID> online=new HashSet<>();for(Object p:players)online.add(uuid(p));Pq45Entries.tick(players);
  for(View v:new ArrayList<>(VIEWS.values()))try{if(!online.contains(v.account)||!same(v)||!Boolean.TRUE.equals(call(v.player,"method_5805"))){suspend(v.player);continue;}v.lastStage=stage(v.player);
   if(v.pendingNode!=null&&clock>=v.frameAt){if(openCamera(v,v.pendingFocus)){if(focusReady(v)){v.pendingFocus=null;v.pendingNode=null;render(v);}else if(clock-v.focusSince>200){say2(v.player,"Capture non confirmée : vérifier la même version du mod de quêtes côté client.");pause(v);closeUi(v);}else requestFocus(v);}else{v.pendingFocus=null;v.pendingNode=null;v.ui=false;pause(v);}}
   if(v.ui&&v.session!=null&&v.session!=current(v.player)){onClosing(v.player,v.session,"REMPLACEE");continue;}if(v.ui){if(score(v.player,"Pq45Choix")!=0)dispatch(v);else if(v.queuedChoice!=0&&clock>=v.voiceUntil){int pick=v.queuedChoice;v.queuedChoice=0;score2(v.player,"Pq45Choix",pick);dispatch(v);}if(clock%20==0&&v.session!=null)call(v.session,"toucher",((Number)call(call(v.world,"method_8503"),"method_3780")).longValue());}
   if(v.walking&&!v.ui){Actor a=v.actors.get(v.role);if(a!=null&&pos(a.entity,v.world).distance2(pos(v.player,v.world))>18*18){if(v.motion!=null){v.motion.pause();v.motion=null;}stopActor(a);v.far=true;if(clock>=v.waitCueAt){v.waitCueAt=clock+200;run(v.player,"title @s actionbar {\"text\":"+quote(NAMES.get(v.role)+" vous attend.")+",\"color\":\"gold\"}");}}else{v.far=false;if(v.motion!=null)v.motion.tick();else nextLeg(v);}}
   for(Retreat r:new ArrayList<>(v.retreats))retireTick(v,r);if(clock%5==0)offerLore(v);if(clock%20==0){savePosition(v);updateMarker(v);}if(v.actors.isEmpty())dispose(v.account);
  }catch(Throwable failure){report2(failure);pause(v);closeUi(v);say2(v.player,failure.getMessage());}
  if(clock%20==0)for(Object p:players)try{if(!ready(p)||VIEWS.containsKey(uuid(p)))continue;int s=score(p,"hcq_pq04");if(!Set.of(30,40,50,60,70,90).contains(s))continue;ensure(p);String role=s<50?"roch":s>=70?"colin":"";if(role.isEmpty())continue;View probe=new View(p);probe.role=role;probe.waypoint=score(p,wpScore(role));Point at=startPosition(probe);if(at==null||!loaded(world(p),at)||at.distance2(pos(p,world(p)))>128*128)continue;View v=view(p);v.role=role;v.waypoint=probe.waypoint;spawn(v,role,at);Object row=child(characterScene(p,v.character),role);v.lorePending=integer(row,"lore",-1);if(canResume(v)&&score(p,"CAP_P45_PAUSE")==0)resume(v);}catch(Throwable failure){report2(failure);dispose(uuid(p));}
  flush();
 }
 public static boolean ownsCheckpoint(Object p,Object dto){View v=VIEWS.get(uuid(p));Pq45Entries.Pending queued=Pq45Entries.PENDING.get(uuid(p));return Objects.equals(character(p),call(dto,"id"))&&(v!=null&&same(v)||queued!=null&&ready(p)&&queued.character().equals(character(p))&&queued.world()==world(p)&&queued.stage()==stage(p));}
 public static void beforeProfileSave(Object p,Object dto){if(!Pq45ProfileCheckpoint.preserving(p)&&ownsCheckpoint(p,dto))suspend(p);}

}
