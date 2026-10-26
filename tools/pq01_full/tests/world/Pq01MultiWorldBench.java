package net.hautecapitale.quetes.sequence;
import net.fabricmc.api.ModInitializer;
import java.util.*;
import java.lang.reflect.*;
import java.nio.file.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;
/** Vrai suivi réseau Minecraft/QuestVisibility ; connexions et profils Nexus sans client graphique. */
public final class Pq01MultiWorldBench implements ModInitializer {
  static boolean active,finished;static Object server,world;static final List<Object> players=new ArrayList<>();
  static final Map<Object,Set<Integer>> live=new IdentityHashMap<>(),spawns=new IdentityHashMap<>();
  static final Map<Object,Object> owners=new IdentityHashMap<>();static int ticks,checks,phase;static final List<String> evidence=new ArrayList<>();
  static Object v1,v2,g1,g2,a1,a2,perrin1,perrin2;static Pq01Motion.Point start1,start2;
  static final Map<String,Integer> passes=new LinkedHashMap<>();static final Map<Object,Map<Integer,Integer>> spawnCounts=new IdentityHashMap<>();
  static boolean foreignSpawn;static Object restarted,retired;static Object disconnected;static final List<Integer> allActors=new ArrayList<>();
  public static void visibilityPass(Object player){if(active)passes.merge(ticks+":"+players.indexOf(player),1,Integer::sum);}
  public void onInitialize(){
    event("net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback","EVENT",a->{register(a[0]);Pq01WorldBench.stub(a[0],"hcd_interpellation");Pq01WorldBench.stub(a[0],"indivissequence");return null;});
    event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStarted","SERVER_STARTED",a->{try{start(a[0]);}catch(Throwable t){fail(t);}return null;});
    event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick","END_SERVER_TICK",a->{try{tick();}catch(Throwable t){fail(t);}return null;});
  }
  static void check(boolean ok,String text){checks++;if(!ok)throw new AssertionError(text);evidence.add(text);System.out.println("MULTI CHECK "+text);}
  static Object get(Object o,String key)throws Exception{for(Class<?> c=o.getClass();c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(key);f.setAccessible(true);return f.get(o);}catch(NoSuchFieldException ignored){}throw new NoSuchFieldException(key);}
  static void set(Object o,String key,Object value)throws Exception{for(Class<?> c=o.getClass();c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(key);f.setAccessible(true);f.set(o,value);return;}catch(NoSuchFieldException ignored){}throw new NoSuchFieldException(key);}
  static Object invoke(String method,Class<?>[] types,Object...args)throws Exception{return Pq01WorldBench.invoke(method,types,args);}
  static void initConnection(Object p)throws Exception{
    Class<?> u=type("sun.misc.Unsafe");Field f=u.getDeclaredField("theUnsafe");f.setAccessible(true);Object unsafe=f.get(null);
    Object sink=call(unsafe,"allocateInstance",type("net.hautecapitale.quetes.sequence.Pq01PacketSink"));set(sink,"field_14140",p);
    set(sink,"field_45026",type("net.minecraft.class_8608").getConstructor(boolean.class).newInstance(false));set(p,"field_13987",sink);
    call(p,"method_52373",stat("net.minecraft.class_8603","method_52362",call(p,"method_31476"),8));
    owners.put(sink,p);live.put(p,new HashSet<>());spawns.put(p,new HashSet<>());spawnCounts.put(p,new HashMap<>());
    net.tompsen.nexuscharacters.NexusCharacters.select(p,UUID.randomUUID());
    Object manager=get(call(world,"method_14178"),"field_17254");Method add=manager.getClass().getDeclaredMethod("method_18714",type("net.minecraft.class_3222"),boolean.class);add.setAccessible(true);add.invoke(manager,p,true);
    call(p,"method_52373",stat("net.minecraft.class_8603","method_52362",call(p,"method_31476"),8));
  }
  public static void packet(Object connection,Object packet){
    if(!active||!owners.containsKey(connection))return;
    String type=packet.getClass().getName();Object p=owners.get(connection);
    if(type.equals("net.minecraft.class_8042")){for(Object part:(Iterable<?>)call(packet,"method_48324"))packet(connection,part);}
    else if(type.equals("net.minecraft.class_2604")){
      int id=((Number)call(packet,"method_11167")).intValue();live.get(p).add(id);spawns.get(p).add(id);spawnCounts.get(p).merge(id,1,Integer::sum);
      Object entity=call(world,"method_66347",call(packet,"method_11164"));
      if(entity!=null&&actor(entity)&&!tags(entity).contains("hcm_owner_"+uuid(p)))foreignSpawn=true;
    }
    else if(type.equals("net.minecraft.class_2716")){for(Object id:(Iterable<?>)call(packet,"method_36548"))live.get(p).remove(((Number)id).intValue());}
  }
  static void score(Object p,String key,int n){run(p,"scoreboard players set @s "+key+" "+n);}
  static void start(Object s)throws Exception{
    server=s;Pq01WorldBench.start(s);world=Pq01WorldBench.world;
    players.add(Pq01WorldBench.player);
    for(int i=1;i<3;i++)players.add(make("net.minecraft.class_3222",server,world,make("com.mojang.authlib.GameProfile",UUID.randomUUID(),"PQ01Multi"+i),stat("net.minecraft.class_8791","method_53821")));
    for(int i=0;i<3;i++){Object p=players.get(i);call(p,"method_5814",5.5d,64d,2.5d+i);initConnection(p);}
    active=true;
  }
  static void setup(){
    Object data=GuardNbt.compound(),bindings=GuardNbt.childOrCreate(data,"bindings"),points=GuardNbt.childOrCreate(data,"points");
    GuardNbt.put(bindings,"gautier_large",Pq01WorldBench.bindingFor(Pq01WorldBench.source));GuardNbt.put(bindings,"geraud_large",Pq01WorldBench.bindingFor(Pq01WorldBench.geraudSource));GuardNbt.put(bindings,"perrin_large",Pq01WorldBench.bindingFor(Pq01WorldBench.perrinSource));
    GuardNbt.put(points,"gautier_poste",pointNbt(pos(Pq01WorldBench.source,world)));GuardNbt.put(points,"gautier_approche",pointNbt(new Pq01Motion.Point(dimension(world),8.5,64.5,0.5)));
    GuardNbt.put(points,"perrin_a",pointNbt(pos(Pq01WorldBench.perrinSource,world)));GuardNbt.put(points,"perrin_b",pointNbt(new Pq01Motion.Point(dimension(world),4.5,64,4.5)));GuardNbt.save(server,"capitale:pq01_setup",data);
    Object p=players.get(0);run(p,"function capitale:pq01/load");
    for(String key:List.of("CAP_PROFILE_LOCK","CAP_QDIALOG_OWNER","CAP_QDIALOG_KEEP","CAP_QSEQ","hcq_pq01"))run(p,"scoreboard objectives add "+key+" dummy");
    run(p,"attribute "+uuid(Pq01WorldBench.source)+" minecraft:movement_speed base set 0.02");
    run(p,"attribute "+uuid(Pq01WorldBench.perrinSource)+" minecraft:movement_speed base set 0");
    for(Object x:players){score(x,"hcq_pq01",0);score(x,"CAP_PROFILE_LOCK",0);score(x,"hcm_pq1_active",0);score(x,"CAP_QDIALOG_OWNER",0);}
    score(players.get(2),"hcq_pq01",100);
  }
  static Object begin(Object p)throws Exception{
    score(p,"hcm_pq1_active",1);score(p,"CAP_QDIALOG_OWNER",91);score(p,"hcm_pq1_node",2);
    Object v=invoke("view",new Class<?>[]{Object.class},p);set(v,"scene",true);set(v,"cameraRole","geraud");set(v,"approach",new Pq01Motion.Point(dimension(world),8.5,64.5,0.5));
    invoke("spawn",new Class<?>[]{v.getClass(),String.class,Pq01Motion.Point.class},v,"geraud",null);
    invoke("spawn",new Class<?>[]{v.getClass(),String.class,Pq01Motion.Point.class},v,"gautier",null);
    startApproach(p);return v;
  }
  static Object actorIn(Object view,String role)throws Exception{return get(((Map<?,?>)get(view,"actors")).get(role),"entity");}
  static int id(Object e){return ((Number)call(e,"method_5628")).intValue();}
  static Object tracker(Object e)throws Exception{return ((Map<?,?>)get(get(call(world,"method_14178"),"field_17254"),"field_18242")).get(id(e));}
  static void updateTrackers()throws Exception{
    Object manager=get(call(world,"method_14178"),"field_17254");
    // Le banc n'a pas de client chargé de recevoir les chunks : les déclarer reçus
    // avant le suivi, sans remplacer ni le tracker ni son contrôle de visibilité.
    for(Object p:players)call(get(get(p,"field_13987"),"field_45026"),"method_52387",p,call(g1,"method_31476"));
    for(Object tracker:((Map<?,?>)get(manager,"field_18242")).values())for(Object p:players)call(tracker,"method_18736",p);
  }
  static void matrix()throws Exception{
    for(Object p:players){
      boolean first=p==players.get(0),second=p==players.get(1);
      check(live.get(p).contains(id(g1))==first,"A Gautier tracked only by A for observer "+players.indexOf(p));
      check(live.get(p).contains(id(g2))==second,"B Gautier tracked only by B for observer "+players.indexOf(p));
      check(!spawns.get(p).contains(id(first?g2:g1)),"foreign actor never spawned for observer "+players.indexOf(p));
    }
  }
  @SuppressWarnings("unchecked") static void productionTick()throws Exception{
    List<Object> online=(List<Object>)call(call(server,"method_3760"),"method_14571");online.addAll(players);
    try{Pq01Bridge.tick(server);if(ticks%100==0){Method m=SequenceBridge.class.getDeclaredMethod("cleanupPrivate",Object.class,List.class);m.setAccessible(true);m.invoke(null,server,players);}}
    finally{online.removeAll(players);}
  }
  static void tick()throws Exception{
    if(!active||finished)return;ticks++;
    if(ticks==20){setup();v1=begin(players.get(0));g1=actorIn(v1,"gautier");a1=actorIn(v1,"geraud");start1=pos(g1,world);
      v2=begin(players.get(1));g2=actorIn(v2,"gautier");a2=actorIn(v2,"geraud");start2=pos(g2,world);
    }
    if(ticks>=20)productionTick();
    if(ticks>=35)updateTrackers();
    if(ticks==80){Object late=make("net.minecraft.class_3222",server,world,make("com.mojang.authlib.GameProfile",UUID.randomUUID(),"PQ01Late"),stat("net.minecraft.class_8791","method_53821"));call(late,"method_5814",5.5d,64d,5.5d);initConnection(late);players.add(late);score(late,"hcq_pq01",100);score(late,"CAP_PROFILE_LOCK",0);updateTrackers();check(!live.get(late).contains(id(g1))&&!live.get(late).contains(id(g2))&&live.get(late).contains(id(Pq01WorldBench.source)),"late observer receives originals and no private Gautier");}
    if(ticks==45){matrix();perrin1=actorIn(v1,"perrin");perrin2=actorIn(v2,"perrin");
      for(Object e:List.of(g1,g2,a1,a2,perrin1,perrin2))allActors.add(id(e));
      check(!live.get(players.get(0)).contains(id(Pq01WorldBench.source))&&!live.get(players.get(1)).contains(id(Pq01WorldBench.source))&&live.get(players.get(2)).contains(id(Pq01WorldBench.source)),"original hidden only for scene owners, visible to spectator");
      check(live.get(players.get(0)).contains(id(a1))&&!live.get(players.get(1)).contains(id(a1))&&!live.get(players.get(2)).contains(id(a1)),"Geraud copy private to A");
      check(live.get(players.get(1)).contains(id(a2))&&!live.get(players.get(0)).contains(id(a2))&&!live.get(players.get(2)).contains(id(a2)),"Geraud copy private to B");
    }
    if(ticks==180){matrix();check(pos(g1,world).distance2(start1)>16&&pos(g2,world).distance2(start2)>16,"two overlapping native walks actually progressed");check(!Boolean.TRUE.equals(call(g1,"method_31481"))&&!Boolean.TRUE.equals(call(g2,"method_31481")),"legacy private cleanup retains both actors");
      Pq01Motion.Point origin=pos(Pq01WorldBench.perrinSource,world);
      check(pos(perrin1,world).distance2(origin)>1&&pos(perrin2,world).distance2(origin)>1,"both Perrin copies physically walk while two Gautier scenes run");
      for(Object p:players){check(live.get(p).contains(id(perrin1))==(p==players.get(0))&&live.get(p).contains(id(perrin2))==(p==players.get(1)),"Perrin copies private for observer "+players.indexOf(p));}
      check(spawnCounts.get(players.get(0)).get(id(g1))==1&&spawnCounts.get(players.get(1)).get(id(g2))==1,"stable Gautier actors spawned once, no visibility churn");
      invoke("close",new Class<?>[]{Object.class,boolean.class},players.get(0),false);score(players.get(0),"hcq_pq01",100);score(players.get(0),"hcm_pq1_active",0);score(players.get(0),"CAP_QDIALOG_OWNER",0);
    }
    if(ticks==200){updateTrackers();check(!Boolean.TRUE.equals(call(g2,"method_31481")),"closing A does not remove B actor");check(live.get(players.get(1)).contains(id(g2)),"B keeps its actor after A closes");check(live.get(players.get(0)).contains(id(Pq01WorldBench.source)),"A original restored after close");invoke("close",new Class<?>[]{Object.class,boolean.class},players.get(1),false);score(players.get(1),"hcq_pq01",100);score(players.get(1),"hcm_pq1_active",0);score(players.get(1),"CAP_QDIALOG_OWNER",0);}
    if(ticks==210){score(players.get(0),"hcq_pq01",0);restarted=begin(players.get(0));retired=actorIn(restarted,"gautier");}
    if(ticks==212){net.tompsen.nexuscharacters.NexusCharacters.select(players.get(0),UUID.randomUUID());updateTrackers();check(!live.get(players.get(0)).contains(id(retired)),"character switch immediately rejects previous character actor");}
    if(ticks==214){check(Boolean.TRUE.equals(call(retired,"method_31481")),"character switch removes old actors on production tick");disconnected=players.remove(1);}
    if(ticks==215){score(disconnected,"hcq_pq01",0);restarted=begin(disconnected);retired=actorIn(restarted,"gautier");}
    if(ticks==217){check(Boolean.TRUE.equals(call(retired,"method_31481")),"disconnected owner actor cleaned by production tick");players.add(1,disconnected);score(disconnected,"hcq_pq01",100);score(players.get(0),"hcq_pq01",100);}
    if(ticks==220){
      updateTrackers();check(Boolean.TRUE.equals(call(g1,"method_31481"))&&Boolean.TRUE.equals(call(g2,"method_31481")),"all initial Gautier actors removed after close");
      check(pos(Pq01WorldBench.source,world).distance2(new Pq01Motion.Point(dimension(world),0.5,64,0.5))<0.001,"public original never moved");
      check(!foreignSpawn,"zero foreign PQ01 actor spawn packets across full lifecycle");
      for(Object p:players)check(allActors.stream().noneMatch(live.get(p)::contains),"no initial private actor remains tracked for observer "+players.indexOf(p));
      run(players.get(0),"scoreboard objectives add hf10_probe dummy");score(players.get(0),"hf10_probe",1);score(players.get(1),"hf10_probe",0);
      call(Pq01WorldBench.geraudSource,"method_5780","hcqvis.hf10_probe.1");
      check(Boolean.TRUE.equals(stat("net.hautecapitale.quetes.visibility.QuestVisibility","isVisibleTo",Pq01WorldBench.geraudSource,players.get(0)))&&!Boolean.TRUE.equals(stat("net.hautecapitale.quetes.visibility.QuestVisibility","isVisibleTo",Pq01WorldBench.geraudSource,players.get(1))),"historical score visibility rule still enforced for ordinary NPC");
      int max=passes.values().stream().mapToInt(Integer::intValue).max().orElse(0);
      if(!System.getenv().getOrDefault("PQ01_JAR","").contains("HF9"))check(max<=1&&max>0,"at most one actual PQ01 visibility refresh per owner per tick");
      finished=true;Files.writeString(Path.of("world-test-result.txt"),"PASS "+checks+" multiplayer checks\n"+String.join("\n",evidence)+"\nNative visibility refresh counts="+passes+" max="+max+"\nReal QuestVisibility body (refresh entry instrumented only), original EntityTrackerMixin, native tracker listeners and spawn/destroy packets, native legacy cleanup, two overlapping EasyNPC walks plus spectator and concurrent Perrin. Connections, chunk receipt and Nexus profiles synthetic; no rendered FPS measured.\n");Pq01WorldBench.command("stop");
    }
  }
  static void fail(Throwable t){if(finished)return;finished=true;t.printStackTrace();try{java.io.StringWriter s=new java.io.StringWriter();t.printStackTrace(new java.io.PrintWriter(s));Files.writeString(Path.of("world-test-result.txt"),"FAIL "+s+"\n"+String.join("\n",evidence));if(server!=null)Pq01WorldBench.command("stop");}catch(Throwable ignored){}}
}
