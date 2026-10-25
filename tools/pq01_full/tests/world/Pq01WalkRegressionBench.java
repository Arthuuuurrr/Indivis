package net.hautecapitale.quetes.sequence;

import net.fabricmc.api.ModInitializer;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;

/** Navigation réelle : source décorative lente, aller/retour, puis 24 blocs. */
public final class Pq01WalkRegressionBench implements ModInitializer {
  static Object server,player,world,view,record,entity;
  static int ticks,legTicks,stage,checks;static boolean finished;
  static Pq01Motion.Point start,previous,target;static double maxStep;
  static final List<String> evidence=new ArrayList<>();
  public void onInitialize(){
    event("net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback","EVENT",a->{register(a[0]);Pq01WorldBench.stub(a[0],"hcd_interpellation");Pq01WorldBench.stub(a[0],"indivissequence");return null;});
    event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStarted","SERVER_STARTED",a->{try{server=a[0];Pq01WorldBench.start(server);player=Pq01WorldBench.player;world=Pq01WorldBench.world;}catch(Throwable t){fail(t);}return null;});
    event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick","END_SERVER_TICK",a->{try{tick();}catch(Throwable t){fail(t);}return null;});
  }
  static void check(boolean ok,String message){checks++;if(!ok)throw new AssertionError(message);evidence.add(message);System.out.println("WALK CHECK "+message);}
  static void set(Object object,String name,Object value)throws Exception{Field f=object.getClass().getDeclaredField(name);f.setAccessible(true);f.set(object,value);}
  static Object invoke(String name,Class<?>[] args,Object...values)throws Exception{return Pq01WorldBench.invoke(name,args,values);}
  static Pq01Motion motion()throws Exception{return (Pq01Motion)Pq01WorldBench.reflect(record,"motion");}
  static int speed(Object npc){run(player,"execute store result score @s hcm_pq1_temp run attribute "+uuid(npc)+" minecraft:movement_speed base get 1000");return score(player,"hcm_pq1_temp");}
  static void initialize()throws Exception{
    run(player,"function capitale:pq01/load");
    for(String key:List.of("CAP_PROFILE_LOCK","CAP_QDIALOG_OWNER","hcq_pq01"))run(player,"scoreboard objectives add "+key+" dummy");
    run(player,"scoreboard players set @s hcq_pq01 0");run(player,"scoreboard players set @s CAP_QDIALOG_OWNER 91");run(player,"scoreboard players set @s hcm_pq1_active 1");run(player,"scoreboard players set @s hcm_pq1_node 2");
    run(player,"attribute "+uuid(Pq01WorldBench.source)+" minecraft:movement_speed base set 0.02");
    Pq01WorldBench.initActors();view=Pq01WorldBench.view;record=Pq01WorldBench.gaRecord;entity=Pq01WorldBench.actor;
    set(view,"scene",true);set(view,"cameraRole","geraud");
    target=new Pq01Motion.Point(dimension(world),9.5,64.5,0.5);set(view,"approach",target);
    start=pos(entity,world);previous=start;
    check(speed(Pq01WorldBench.source)==20,"source movement speed deliberately 0.02");
    check(Integer.valueOf(1).equals(invoke("startApproach",new Class<?>[]{Object.class},player)),"real production approach starts during a2");
    legTicks=0;stage=1;
  }
  @SuppressWarnings("unchecked") static void productionTick(){
    List<Object> online=(List<Object>)call(call(server,"method_3760"),"method_14571");online.add(player);
    try{Pq01Bridge.tick(server);}finally{online.remove(player);}
  }
  static void travel(Pq01Motion.Point point)throws Exception{
    target=point;legTicks=0;set(view,"pendingCamera",null);set(view,"pendingNode",null);
    invoke("travel",new Class<?>[]{view.getClass(),record.getClass(),Pq01Motion.Point.class,Runnable.class,java.util.function.Consumer.class},view,record,target,(Runnable)()->{},(java.util.function.Consumer<String>)reason->{});
  }
  static void tick()throws Exception{
    if(server==null||finished)return;ticks++;
    if(stage==0){if(ticks==20)initialize();return;}
    productionTick();legTicks++;
    Pq01Motion.Point now=pos(entity,world);maxStep=Math.max(maxStep,Math.sqrt(now.distance2(previous)));previous=now;
    if(!motion().done())return;
    double distance=Math.sqrt(now.distance2(target));
    check(distance<=1.5,"leg "+stage+" reaches destination before timeout; ticks="+legTicks+" remaining="+distance+" actorBase="+speed(entity));
    if(stage==1){
      check(legTicks<200,"slow source does not cause crawling for more than ten seconds");
      check(speed(Pq01WorldBench.source)==20,"source speed remains 0.02 after private walk");
      check(speed(entity)>=250,"private walking actor has usable speed floor");
      travel(start);stage=2;
    }else if(stage==2){
      check(maxStep<1.3,"outbound and return use native walking without jumps");
      invoke("close",new Class<?>[]{Object.class,boolean.class},player,false);
      // Nouvelle liaison de banc, sur un couloir plat distinct ; aucune mutation de décor utilisateur.
      Pq01WorldBench.command("fill -1 63 3 28 63 6 minecraft:stone");Pq01WorldBench.command("fill -1 64 3 28 67 6 minecraft:air");
      Object data=GuardNbt.compound(),bindings=GuardNbt.childOrCreate(data,"bindings");
      GuardNbt.put(bindings,"gautier_large",Pq01WorldBench.bindingFor(Pq01WorldBench.perrinSource));GuardNbt.save(server,"capitale:pq01_setup",data);
      view=invoke("view",new Class<?>[]{Object.class},player);set(view,"scene",true);record=invoke("spawn",new Class<?>[]{view.getClass(),String.class,Pq01Motion.Point.class},view,"gautier",null);entity=Pq01WorldBench.reflect(record,"entity");
      start=pos(entity,world);previous=start;maxStep=0;travel(new Pq01Motion.Point(dimension(world),start.x()+24,start.y(),start.z()));stage=3;
    }else if(stage==3){travel(start);stage=4;
    }else{
      check(maxStep<1.3,"24-block outbound and return never teleport");
      check(pos(Pq01WorldBench.source,world).distance2(new Pq01Motion.Point(dimension(world),0.5,64,0.5))<0.001,"original source never moved");
      invoke("close",new Class<?>[]{Object.class,boolean.class},player,false);
      finished=true;Files.writeString(Path.of("world-test-result.txt"),"PASS "+checks+" movement regression checks\n"+String.join("\n",evidence)+"\nReal Fabric world, production tick/startApproach/travel, EasyNPC native navigation on stairs/slabs and full 24-block route. Source speed 0.02, private copy and source compared; connections, Nexus and capture display synthetic.\n");Pq01WorldBench.command("stop");
    }
  }
  static void fail(Throwable t){if(finished)return;finished=true;t.printStackTrace();try{java.io.StringWriter s=new java.io.StringWriter();t.printStackTrace(new java.io.PrintWriter(s));Files.writeString(Path.of("world-test-result.txt"),"FAIL "+s+"\n"+String.join("\n",evidence));if(server!=null)Pq01WorldBench.command("stop");}catch(Throwable ignored){}}
}
