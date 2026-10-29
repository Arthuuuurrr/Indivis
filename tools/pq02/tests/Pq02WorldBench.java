package net.hautecapitale.quetes.sequence;
import java.util.*;import java.lang.reflect.*;import java.nio.file.*;
import net.fabricmc.api.ModInitializer;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
import net.hautecapitale.quetes.sequence.Pq01Motion.Point;
import static net.hautecapitale.quetes.sequence.Pq02Runtime.*;
/** Vrai serveur et navigation EasyNPC, caméras/sessions/visibilité natifs ; connexions/profils synthétiques. */
public final class Pq02WorldBench implements ModInitializer {
 static boolean autoFocusAck=true;static Object srv,w,leo,mag,sentinel,captain;static Object a,b,observer;static int t,checks,phase,wait;static boolean finished,foreign;
 static Point publicStart,pauseAt,disconnectedAt;static Object retiredB;static int disconnectedWp;static UUID charA=UUID.randomUUID(),charB=UUID.randomUUID();static final List<String> evidence=new ArrayList<>();
 static final Map<Object,Object> owners=new IdentityHashMap<>();static final Map<Object,Set<Integer>> tracked=new IdentityHashMap<>();static final Map<Object,List<String>> messages=new IdentityHashMap<>();
 static final List<Object> players=new ArrayList<>();static final Map<Object,Integer> frames=new IdentityHashMap<>();static final Map<Object,Map<Integer,Integer>> spawnCounts=new IdentityHashMap<>();
 public void onInitialize(){
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick","END_SERVER_TICK",x->{if(srv!=null&&!finished)((List<Object>)call(call(srv,"method_3760"),"method_14571")).addAll(players);return null;});
  SequenceBridge.init();Pq02Runtime.init();Pq01Bridge.init();
  net.hautecapitale.dialogue.config.DialogueConfig.load();net.hautecapitale.dialogue.network.DialogueNetwork.init();
  event("net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback","EVENT",x->{stub(x[0],"hcd_interpellation");stub(x[0],"name");return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStarted","SERVER_STARTED",x->{try{start(x[0]);}catch(Throwable e){fail(e);}return null;});
  event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick","END_SERVER_TICK",x->{try{testTick();}catch(Throwable e){fail(e);}finally{if(srv!=null)((List<?>)call(call(srv,"method_3760"),"method_14571")).clear();}return null;});
 }
 static void stub(Object d,String name){Object r=stat("com.mojang.brigadier.builder.LiteralArgumentBuilder","literal",name),arg=stat("com.mojang.brigadier.builder.RequiredArgumentBuilder","argument","arg",stat("com.mojang.brigadier.arguments.StringArgumentType","greedyString"));Class<?> c=type("com.mojang.brigadier.Command");call(arg,"executes",Proxy.newProxyInstance(c.getClassLoader(),new Class<?>[]{c},(p,m,a)->m.getDeclaringClass()==Object.class?objectMethod(p,m,a):1));call(r,"then",arg);call(d,"register",r);}
 static void ok(boolean b,String s){checks++;if(!b)throw new AssertionError(s+" [t="+t+",phase="+phase+"]");evidence.add(s);System.out.println("PQ02 CHECK "+s);}
 static Object get(Object o,String name)throws Exception{for(Class<?> c=o.getClass();c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(name);f.setAccessible(true);return f.get(o);}catch(NoSuchFieldException e){}throw new NoSuchFieldException(name);}
 static void set(Object o,String name,Object v)throws Exception{for(Class<?> c=o.getClass();c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(name);f.setAccessible(true);f.set(o,v);return;}catch(NoSuchFieldException e){}throw new NoSuchFieldException(name);}
 static void cmd(String s){call(call(srv,"method_3734"),"method_44252",call(srv,"method_3739"),s);}
 static Object player(String name,UUID character)throws Exception{Object p=make("net.minecraft.class_3222",srv,w,make("com.mojang.authlib.GameProfile",UUID.randomUUID(),name),stat("net.minecraft.class_8791","method_53821"));call(p,"method_5814",0.5d,64d,2.5d);var f=Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");f.setAccessible(true);Object sink=call(f.get(null),"allocateInstance",type("net.hautecapitale.quetes.sequence.Pq02PacketSink"));set(sink,"field_14140",p);set(sink,"field_45026",type("net.minecraft.class_8608").getConstructor(boolean.class).newInstance(false));set(p,"field_13987",sink);call(p,"method_52373",stat("net.minecraft.class_8603","method_52362",call(p,"method_31476"),8));
  net.tompsen.nexuscharacters.NexusCharacters.select(p,character);players.add(p);owners.put(sink,p);tracked.put(p,new HashSet<>());messages.put(p,new ArrayList<>());spawnCounts.put(p,new HashMap<>());frames.put(p,0);
  Object manager=get(call(w,"method_14178"),"field_17254");Method add=manager.getClass().getDeclaredMethod("method_18714",type("net.minecraft.class_3222"),boolean.class);add.setAccessible(true);add.invoke(manager,p,true);return p;
 }
 public static void packet(Object sink,Object packet){Object p=owners.get(sink);if(p==null)return;String n=packet.getClass().getName();
  if(n.equals("net.minecraft.class_8042")){for(Object part:(Iterable<?>)call(packet,"method_48324"))packet(sink,part);}
  if(n.equals("net.minecraft.class_2604")){int id=((Number)call(packet,"method_11167")).intValue();tracked.get(p).add(id);spawnCounts.get(p).merge(id,1,Integer::sum);Object e=call(w,"method_66347",call(packet,"method_11164"));if(e!=null&&tags(e).contains(TAG)&&!tags(e).contains("hcm_owner_"+uuid(p)))foreign=true;}
  if(n.equals("net.minecraft.class_2716"))for(Object id:(Iterable<?>)call(packet,"method_36548"))tracked.get(p).remove(((Number)id).intValue());
  if(n.equals("net.minecraft.class_7439")){Object text=call(packet,"comp_763");messages.get(p).add(String.valueOf(call(text,"getString")));String in=(String)call(call(text,"method_10866"),"method_10955");if(in!=null&&in.startsWith(Pq02Focus.PREFIX)&&autoFocusAck){var request=Pq02Focus.parse(in);if(request!=null)acknowledgeFocus(p,request.token());}if(in!=null&&in.startsWith("indivis:pq02:v1:"))frames.merge(p,1,Integer::sum);Pq02CaptureBench.packet(p,text);}
 }
 static int entityId(Object e){return ((Number)call(e,"method_5628")).intValue();}
 static void trackers()throws Exception{Object manager=get(call(w,"method_14178"),"field_17254");for(Object p:players)call(get(get(p,"field_13987"),"field_45026"),"method_52387",p,call(leo,"method_31476"));for(Object tr:((Map<?,?>)get(manager,"field_18242")).values())for(Object p:players)call(tr,"method_18736",p);}
 static Object npc(double x,double z,String tag){Object type=stat("de.markusbordihn.easynpc.entity.ModEntityType","getEntityType",en("de.markusbordihn.easynpc.entity.ModNPCEntityType","HUMANOID"));Object e=call(type,"method_5883",w,en("net.minecraft.class_3730","field_16462"));call(e,"method_5814",x,64d,z);call(e,"method_5977",true);call(e,"method_5780",tag);call(w,"method_8649",e);return e;}
 static void start(Object s)throws Exception{srv=s;w=call(s,"method_30002");cmd("forceload add -16 -16 48 32");cmd("fill -12 63 -12 40 63 24 minecraft:stone");cmd("fill -12 64 -12 40 70 24 minecraft:air");
  leo=npc(0.5,0.5,"npc_leovic_registre_port");mag=npc(20.5,5.5,"fixture_magistrat");sentinel=npc(4.5,5.5,"hcq_pnj_sentinelle_archives");captain=npc(0.5,8.5,"hcq_pnj_altheon_brumeforge");publicStart=pos(leo,w);call(leo,"method_5977",true);call(mag,"method_5977",true);
  Object skin=stat("de.markusbordihn.easynpc.data.skin.SkinDataEntry","createResourceLocationSkin",stat("net.minecraft.class_2960","method_60654","minecraft:textures/entity/player/wide/steve.png"));call(call(leo,"getEasyNPCSkinData"),"setSkinDataEntry",skin);call(call(leo,"getEasyNPCModelData"),"setModelPoseName","pq02-preserved");
  Object row=GuardNbt.compound();GuardNbt.string(row,"Type","COMMAND");GuardNbt.string(row,"Cmd","function capitale:quest/le_registre_du_port/leovic/interact_self");Pq01Actions.mutate(leo,"ON_INTERACTION",GuardNbt.list(List.of(row)));
  row=GuardNbt.compound();GuardNbt.string(row,"Type","COMMAND");GuardNbt.string(row,"Cmd","function capitale:quest/le_registre_du_port/magistrat/register_self");Pq01Actions.mutate(mag,"ON_INTERACTION",GuardNbt.list(List.of(row)));
  for(int i=0;i<ROUTE.size();i++){double x=i<=14?0.5+i*1.35:19.4+(i-14)*1.3;double z=i<=14?0.5:0.5+(i-14)*0.9;cmd("summon minecraft:marker "+x+" 64 "+z+" {Tags:[\"wp_leovic_registre_port_"+ROUTE.get(i)+"\"]}");}
  a=player("PQ02_A",charA);b=player("PQ02_B",UUID.randomUUID());observer=player("PQ02_Observer",UUID.randomUUID());

 }
 static void seed(Object p,int s){run(p,"function fixture:objectives");score2(p,"hcq_pq01",100);score2(p,"CAP_PROFILE_LOCK",0);score2(p,"hcq_pq02",s);score2(p,"CAP_PQ2_VER",1);score2(p,"CAP_QDIALOG_OWNER",0);score2(p,"CAP_PQ2_PAUSE",1);}
 static View v(Object p){return VIEWS.get(uuid(p));}
 static void settle(Object p){View v=v(p);if(v!=null&&v.pendingNode!=null){clock++;v.frameAt=clock;Object f=v.pendingFocus;ok(openCamera(v,f),"native DialogueManager opens camera for "+v.node.key);if(!focusReady(v)){requestFocus(v);if(autoFocusAck)acknowledgeFocus(p,v.focusToken);}ok(focusReady(v),"client readiness acknowledged for "+v.node.key);v.pendingFocus=null;v.pendingNode=null;render(v);}}
 static void showKey(Object p,String key){show(v(p),key);settle(p);ok(v(p).node.key.equals(key),"node rendered "+key);}
 static void pick(Object p,int n){View v=v(p);int previous=v.baseToken;run(p,"trigger Pq02Choix set "+(previous+n));ok(dispatch(v)==1,"native trigger accepted "+v.node.key+" #"+n);settle(p);}
 static void advance(Object p){pick(p,14);}
 @SuppressWarnings("unchecked") static void production(){List<Object> online=(List<Object>)call(call(srv,"method_3760"),"method_14571");Pq02Runtime.tick(srv);}
 static void testTick()throws Exception{if(srv==null||finished)return;t++;if(t<20)return;
  if(t==20&&Boolean.getBoolean("pq02.prologue.only")){seed(a,30);seed(b,30);seed(observer,100);run(a,"function capitale:pq02/load");Pq01AuditBench.start(a,observer);phase=8;return;}
  if(t==20){seed(a,30);seed(b,30);seed(observer,100);run(a,"function capitale:pq02/load");if(Boolean.getBoolean("pq02.repro.entry")){Pq02EntryBench.reproduce();return;}if(Boolean.getBoolean("pq02.repro.screen")){Pq02ScreenBench.reproduce();finished=true;Files.writeString(Path.of("world-test-result.txt"),"PASS REPRODUCED_HF2_ABSENT_SCREEN_ECRAN_FERME\n"+String.join("\n",evidence));cmd("stop");return;}Pq02SetupBench.run();Pq02EntryBench.verify();Pq02FocusBench.verify();
   // Reproduce auto-capture before first role binding (original b10 behavior).
   stat(DM,"ouvrir",a,leo,en("net.hautecapitale.dialogue.session.DialogueMode","CAPTURE"));open(a,"leovic");settle(a);ok(v(a).node.key.equals("a01"),"first interaction auto-binds even after original capture");
   open(b,"leovic");settle(b);pick(a,1);pick(b,3);ok(score(a,"REP_GARDEPORT")==1&&score(a,"CAP_PQ2_IMPR")==1&&score(b,"REP_GARDEPORT")==0,"arrival effects are private");
   int token=v(a).baseToken;score2(a,"Pq02Choix",token-32);ok(dispatch(v(a))==0,"stale token rejected");
   showKey(a,"a08");pick(a,1);advance(a);showKey(b,"a08");pick(b,1);advance(b);ok(stage(a)==40&&stage(b)==40,"explicit acceptance starts both escort routes");phase=1;
  }
  if(phase==1){production();trackers();if(t==60){View va=v(a),vb=v(b);Object x=va.actors.get("leovic").entity,y=vb.actors.get("leovic").entity;ok(!uuid(x).equals(uuid(y)),"one native EasyNPC actor per character");
    ok(tracked.get(a).contains(entityId(x))&&!tracked.get(b).contains(entityId(x))&&!tracked.get(observer).contains(entityId(x)),"native entity tracker delivers A actor only to A");
    ok(tracked.get(b).contains(entityId(y))&&!tracked.get(a).contains(entityId(y))&&!tracked.get(observer).contains(entityId(y)),"native entity tracker delivers B actor only to B");
    ok(!tracked.get(a).contains(entityId(leo))&&tracked.get(observer).contains(entityId(leo)),"original hidden for owner and retained for spectator");
    for(String key:List.of("SkinData","ModelData"))ok(Objects.equals(child(GuardNbt.entity(leo),key),child(GuardNbt.entity(x),key)),"source appearance copied: "+key);
    ok(pos(x,w).distance2(publicStart)>0.5,"real pathfinding moved actor without teleport");open(a,"leovic");settle(a);pauseAt=pos(x,w);retiredB=y;disconnectedAt=pos(y,w);disconnectedWp=vb.waypoint;players.remove(b);phase=2;wait=0;
   }return;
  }
  if(phase==2){production();if(++wait==1)ok(v(b)==null&&Boolean.TRUE.equals(call(retiredB,"method_31481"))&&v(a)!=null,"disconnect cleans B actor and leaves A scene active");if(wait==220){Object x=v(a).actors.get("leovic").entity;ok(pos(x,w).distance2(pauseAt)<0.001,"actor remains stopped through long optional menu");ok(stage(a)==40&&v(a).ui&&v(a).session==current(a),"open menu remains active and never advances quest");
    showKey(a,"t04b");pick(a,1);ok(score(a,"REP_GARDEPORT")==2&&score(a,"CAP_PQ2_IMPR")==2,"guard decision once");show(v(a),"t04b");settle(a);ok(v(a).node.key.equals("t04b_a"),"repeated guard topic skips spent decision");
    stat(DM,"fermer",a,en("net.hautecapitale.dialogue.session.CloseReason","ECRAN_FERME"));ok(v(a).walking&&!v(a).ui,"native close mixin resumes escort");players.add(b);open(b,"leovic");settle(b);ok(v(b).waypoint==disconnectedWp&&pos(v(b).actors.get("leovic").entity,w).distance2(disconnectedAt)<0.01,"reconnect restores saved waypoint and exact scene position");stat(DM,"fermer",b,en("net.hautecapitale.dialogue.session.CloseReason","ECRAN_FERME"));phase=3;wait=0;}return;}
  if(phase==3){Object x=v(a).actors.get("leovic").entity;Point p=pos(x,w);call(a,"method_5814",p.x(),p.y(),p.z()+2);Object y=v(b).actors.get("leovic").entity;p=pos(y,w);call(b,"method_5814",p.x(),p.y(),p.z()+2);production();if(++wait>1600)throw new AssertionError("escort did not reach M: "+v(a).failure);
   if(stage(a)==50){settle(a);ok(v(a).waypoint==14,"first leg visits existing M endpoint");Object saved=characterScene(a,charA);savePosition(v(a));saved=characterScene(a,charA);ok(integer(saved,"stage",0)==50,"arrival saves new stage even without position change");phase=4;}
   return;
  }
  if(phase==4){showKey(a,"t07");pick(a,3);advance(a);ok(v(a).anchor!=null&&v(a).focus==v(a).anchor,"native three-person camera anchor selected");advance(a);advance(a);advance(a);advance(a);
   ok(v(a).node.key.equals("m02"),"magistrate introduction has all four speaker blocks");pick(a,2);pick(a,1);pick(a,1);advance(a);advance(a);ok(score(a,"CAP_PQ2_IMPR")==3&&score(a,"REP_GARDEPORT")==2,"magistrate positive affects only personal impression");
   showKey(a,"m06");advance(a);ok(stage(a)==60&&score(a,"CAP_RANGSOCIAL")==10,"magistrate registers minimum Visitor once");pick(a,3);advance(a);pick(a,1);advance(a);ok(stage(a)==70,"M to elevator uses actual second escort phase");phase=5;wait=0;return;}
  if(phase==5){Point p=pos(v(a).actors.get("leovic").entity,w);call(a,"method_5814",p.x(),p.y(),p.z()+2);production();if(++wait>500)throw new AssertionError("elevator leg timeout "+v(a).failure);if(stage(a)==90){settle(a);ok(v(a).waypoint==17,"short escort reaches existing P elevator endpoint");phase=6;}return;}
  if(phase==6){showKey(a,"f06");pick(a,4);advance(a);advance(a);pick(a,1);advance(a);ok(stage(a)==100&&score(a,"REP_GARDEPORT")==1&&score(a,"CAP_PQ2_IMPR")==2,"final contempt sums with previous positives and completes quest; scores="+stage(a)+"/"+score(a,"REP_GARDEPORT")+"/"+score(a,"CAP_PQ2_IMPR")+" reg="+score(a,"CAP_PQ2_REG")+" dd="+score(a,"CAP_PQ2_DD")+" rew="+score(a,"CAP_PQ2_REW")+" dep="+score(a,"hcm_pq2_check")+" owner="+score(a,"CAP_QDIALOG_OWNER"));
   ok(integer(GuardNbt.entity(a),"XpTotal",0)==165,"actual original vanilla XP wrapper grants 165 points once");ok(score(a,"CAP_LEOVIC_GIFT")==0&&score(a,"fixture_bonus")==1&&score(a,"fixture_skills")==1,"refusal gives no gift and preserves completion rewards once");
   call(a,"method_5814",0.5d,64d,2.5d);open(a,"leovic");settle(a);ok(v(a).node.key.equals("post_friendly"),"return greeting uses global impression after contempt");showKey(a,"post_gift");pick(a,1);run(a,"execute store result score @s fixture_money run clear @s minecraft:gold_nugget 0");ok(score(a,"fixture_money")==50,"gift actually gives exactly 50 currency substitute items");ok(score(a,"CAP_LEOVIC_GIFT")==1&&score(a,"fixture_bonus")==2&&score(a,"REP_GARDEPORT")==1&&score(a,"CAP_PQ2_IMPR")==2,"later gift adds second existing bonus without reputation replay");
   int bonus=score(a,"fixture_bonus");run(a,"function capitale:pq02/gift_once_self");run(a,"function capitale:pq02/complete_once_self");run(a,"execute store result score @s fixture_money run clear @s minecraft:gold_nugget 0");ok(score(a,"fixture_money")==50,"replayed gift does not duplicate money");ok(score(a,"fixture_bonus")==bonus&&score(a,"fixture_skills")==1,"repeated gift and completion calls remain idempotent");
   Map<String,Integer> savedScores=new LinkedHashMap<>();for(String k:List.of("hcq_pq02","CAP_PQ2_VER","CAP_PQ2_DA","CAP_PQ2_DG","CAP_PQ2_DM","CAP_PQ2_DD","CAP_PQ2_IMPR","CAP_LEOVIC_GIFT","CAP_PQ2_OFFER","CAP_PQ2_REG","CAP_PQ2_REW","REP_GARDEPORT"))savedScores.put(k,score(a,k));
   suspend(a);net.tompsen.nexuscharacters.NexusCharacters.select(a,charB);for(String k:savedScores.keySet())score2(a,k,0);seed(a,30);open(a,"leovic");settle(a);ok(v(a).node.key.equals("a01")&&score(a,"CAP_LEOVIC_GIFT")==0&&score(a,"CAP_PQ2_IMPR")==0,"same account second character has independent story and gift");pick(a,2);suspend(a);
   net.tompsen.nexuscharacters.NexusCharacters.select(a,charA);for(var row:savedScores.entrySet())score2(a,row.getKey(),row.getValue());open(a,"leovic");settle(a);ok(v(a).node.key.equals("post_friendly")&&score(a,"CAP_LEOVIC_GIFT")==1,"synthetic A-B-A profile restore returns first character memory");
   // Legacy migration is conservative: no invented decision bonus, preserve previous gift.
   suspend(observer);seed(observer,100);score2(observer,"CAP_PQ2_VER",0);score2(observer,"REP_GARDEPORT",12);score2(observer,"CAP_LEOVIC_GIFT",1);ensure(observer);ok(score(observer,"REP_GARDEPORT")==12&&score(observer,"CAP_PQ2_REW")==1&&score(observer,"CAP_LEOVIC_GIFT")==1,"legacy completed/gifted profile keeps original reputation and awards");
   suspend(a);suspend(b);Pq02CaptureBench.runAll(a);HuguesBench.runAll(a);Mq03Bench.runAll(a,observer);trackers();ok(pos(leo,w).distance2(publicStart)<0.001,"public Leovic never moved");ok(!foreign,"no foreign private entity spawn packets during two escorts");ok(ERRORS.isEmpty(),"no PQ02 runtime exception");
   for(Object p:players)ok(!messages.get(p).contains("..."),"no placeholder dialogue sent");
   Pq01GautierBench.start(a,observer);phase=7;return;
  }
  if(phase==7&&Pq01GautierBench.tick()){if(Boolean.getBoolean("pq02.prologue.audit")){Pq01AuditBench.start(a,observer);phase=8;}else finish();}
  if(phase==8&&Pq01AuditBench.tick())finish();
 }
 static void finish()throws Exception{
   ((List<?>)call(call(srv,"method_3760"),"method_14571")).clear();finished=true;Files.writeString(Path.of("world-test-result.txt"),"PASS "+checks+" native checks\n"+String.join("\n",evidence)+"\nFrame counts="+frames+"\nNative Fabric 1.21.11 / EasyNPC 7.16, HF12 classes preserved except the two authorized Gautier timing classes; production PQ02 JAR+mixins. Connections/chunk receipt and Nexus selection synthetic; currency replaced by gold nuggets, external CapSkills and bonus pools counted by fixtures. No rendered camera, real client, FPS or user map tested.\n");cmd("stop");
 }
 static void fail(Throwable e){if(finished)return;finished=true;e.printStackTrace();try{var sw=new java.io.StringWriter();e.printStackTrace(new java.io.PrintWriter(sw));Files.writeString(Path.of("world-test-result.txt"),"FAIL "+sw+"\n"+String.join("\n",evidence));if(srv!=null){((List<?>)call(call(srv,"method_3760"),"method_14571")).clear();cmd("stop");};}catch(Exception ignored){}}
}
