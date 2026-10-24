package net.hautecapitale.quetes.sequence;
import net.fabricmc.api.ModInitializer;
import java.util.*;
import java.lang.reflect.*;
import java.nio.file.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;

/** Monde et navigation réels ; contexte Nexus, visibilité, caméra et panneau doublés. */
public final class Pq01WorldBench implements ModInitializer {
  static Object server,world,player,view,source,perrinSource,geraudSource,actor,perrin;static String pipeline;
  static Object gaRecord,perrinRecord;static int ticks,stage,checks;static boolean failed;
  static final List<String> evidence=new ArrayList<>();
  static boolean stairsSeen,slabSeen;
  public void onInitialize(){
    event("net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback","EVENT",a->{register(a[0]);Pq01PipelineBench.registerOverrides(a[0]);stub(a[0],"indivissequence");stub(a[0],"hcd_interpellation");return null;});
    event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStarted","SERVER_STARTED",a->{try{start(a[0]);}catch(Throwable e){fail(e);}return null;});
    event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick","END_SERVER_TICK",a->{try{tick();}catch(Throwable e){fail(e);}return null;});
  }
  static void stub(Object dispatcher,String name){
    Object root=stat("com.mojang.brigadier.builder.LiteralArgumentBuilder","literal",name);
    Object arg=stat("com.mojang.brigadier.builder.RequiredArgumentBuilder","argument","args",stat("com.mojang.brigadier.arguments.StringArgumentType","greedyString"));
    Class<?> command=type("com.mojang.brigadier.Command");Object run=Proxy.newProxyInstance(command.getClassLoader(),new Class<?>[]{command},(p,m,a)->m.getDeclaringClass()==Object.class?objectMethod(p,m,a):1);
    call(arg,"executes",run);call(root,"then",arg);call(dispatcher,"register",root);
  }
  static void check(boolean ok,String label){checks++;if(!ok)throw new AssertionError(label);evidence.add(label);System.out.println("WORLD CHECK "+label);}
  static Object reflect(Object o,String field)throws Exception{Field f=o.getClass().getDeclaredField(field);f.setAccessible(true);return f.get(o);}
  static Object invoke(String name,Class<?>[] params,Object...args)throws Exception{Method m=Pq01Bridge.class.getDeclaredMethod(name,params);m.setAccessible(true);return m.invoke(null,args);}
  static void command(String text){call(call(server,"method_3734"),"method_44252",call(server,"method_3739"),text);}
  static Object npc(double x,double z){
    return npc(x,64,z);
  }
  static Object npc(double x,double y,double z){
    Object type=stat("de.markusbordihn.easynpc.entity.ModEntityType","getEntityType",en("de.markusbordihn.easynpc.entity.ModNPCEntityType","HUMANOID"));
    Object e=call(type,"method_5883",world,en("net.minecraft.class_3730","field_16462"));
    call(e,"method_5814",x,y,z);call(e,"method_5977",true);check(Boolean.TRUE.equals(call(world,"method_8649",e)),"source NPC inserted");return e;
  }
  static Object bindingFor(Object npc){Object n=GuardNbt.compound();GuardNbt.string(n,"uuid",uuid(npc).toString());GuardNbt.string(n,"dimension",dimension(world));GuardNbt.put(n,"post",pointNbt(pos(npc,world)));return n;}
  static Object spawn(String role)throws Exception{return invoke("spawn",new Class<?>[]{view.getClass(),String.class,Pq01Motion.Point.class},view,role,null);}
  static void start(Object s)throws Exception{
    server=s;world=call(s,"method_30002");
    Object profile=make("com.mojang.authlib.GameProfile",UUID.randomUUID(),"PQ01WorldTest");
    Object options=stat("net.minecraft.class_8791","method_53821");
    player=make("net.minecraft.class_3222",server,world,profile,options);call(player,"method_5814",0.5d,64d,1.5d);
    command("forceload add -16 -16 32 16");command("fill -12 63 -12 20 63 12 minecraft:stone");
    command("fill -12 64 -12 20 70 12 minecraft:air");
    // Couloir sans contournement : montée, palier, descente puis dalles basses.
    command("fill -1 64 -1 11 66 -1 minecraft:stone");command("fill -1 64 1 11 66 1 minecraft:stone");
    command("fill -1 64 -1 -1 66 1 minecraft:stone");command("fill 11 64 -1 11 66 1 minecraft:stone");
    command("setblock 3 64 0 minecraft:oak_stairs[facing=east,half=bottom]");
    command("fill 4 64 0 6 64 0 minecraft:oak_planks");
    command("setblock 7 64 0 minecraft:oak_stairs[facing=west,half=bottom]");
    command("fill 8 64 0 10 64 0 minecraft:oak_slab[type=bottom]");
    source=npc(0.5,0.5);perrinSource=npc(0.5,4.5);geraudSource=npc(9.5,64.5,0.5);
    Object skin=stat("de.markusbordihn.easynpc.data.skin.SkinDataEntry","createResourceLocationSkin",stat("net.minecraft.class_2960","method_60654","minecraft:textures/entity/player/wide/steve.png"));
    call(call(source,"getEasyNPCSkinData"),"setSkinDataEntry",skin);
    call(call(source,"getEasyNPCModelData"),"setModelPoseName","hf4_copy_probe");
    command("item replace entity "+uuid(source)+" weapon.mainhand with minecraft:stick");
    call(source,"method_5665",stat("net.minecraft.class_2561","method_43470","Gautier source test"));
    stage=-1;ticks=0;
  }
  static void initActors()throws Exception{
    check(call(world,"method_66347",uuid(source))==source&&call(world,"method_66347",uuid(perrinSource))==perrinSource,"source UUIDs ready after world ticks");
    Object data=GuardNbt.compound(),bindings=GuardNbt.childOrCreate(data,"bindings");
    GuardNbt.put(bindings,"gautier_large",bindingFor(source));GuardNbt.put(bindings,"perrin_large",bindingFor(perrinSource));GuardNbt.put(bindings,"geraud_large",bindingFor(geraudSource));GuardNbt.save(server,"capitale:pq01_setup",data);
    view=invoke("view",new Class<?>[]{Object.class},player);
    gaRecord=spawn("gautier");actor=reflect(gaRecord,"entity");perrinRecord=spawn("perrin");perrin=reflect(perrinRecord,"entity");
    check(!uuid(actor).equals(uuid(source))&&!uuid(perrin).equals(uuid(perrinSource)),"fresh UUIDs for both actors");
    check(call(world,"method_66347",uuid(actor))==actor&&call(world,"method_66347",uuid(perrin))==perrin,"actors present in actual ServerWorld");
    check(tags(actor).contains(Pq01Actors.TAG)&&tags(actor).contains("hcm_owner_"+uuid(player)),"private actor tags loaded");
    check(Boolean.FALSE.equals(call(actor,"method_5987")),"Gautier AI enabled");
    Object srcTag=GuardNbt.entity(source),copyTag=GuardNbt.entity(actor);
    check(!Objects.equals(child(srcTag,"SkinData"),GuardNbt.compound()),"source has a non-default skin payload");
    for(String key:List.of("SkinData","ModelData","EquipmentData","equipment","HandItems"))if(child(srcTag,key)!=null)check(Objects.equals(child(srcTag,key),child(copyTag,key)),"appearance copied: "+key);
    check(Pq01Actors.protectedSelector(call(actor,"getEntityGoalSelector")),"production goal protection active");
    stage=0;ticks=0; // laisser les acteurs rejoindre le sol avant le premier calcul de chemin.
  }
  static void begin(Object a,Pq01Motion.Point target)throws Exception{
    stage++;ticks=0;
    invoke("travel",new Class<?>[]{view.getClass(),a.getClass(),Pq01Motion.Point.class,Runnable.class,java.util.function.Consumer.class},view,a,target,(Runnable)()->{
      if(stage==5)throw new AssertionError("blocked destination must not be reached");
      check(true,"actual navigation leg "+stage+" arrived");ticks=-20;
    },(java.util.function.Consumer<String>)reason->{if(stage==5){check(true,"native blocked path ends with bounded failure");check(pos(perrin,world).distance2(target)>1.25*1.25,"blocked actor never teleported through wall");ticks=-20;}else throw new AssertionError("navigation leg "+stage+": "+reason);});
  }
  static void tick()throws Exception{
    if(server==null||failed)return;
    if(stage==6){String scene=Pq01SceneWorldBench.tick();if(scene!=null){pipeline+=scene;Pq01AmbientWorldBench.start(player);stage=7;}return;}
    if(stage==7){String ambient=Pq01AmbientWorldBench.tick();if(ambient!=null){Files.writeString(Path.of("world-test-result.txt"),"PASS "+checks+" checks\n"+String.join("\n",evidence)+"\nReal Fabric 1.21.11 world and EasyNPC 7.16.0 navigation (stairs, corridor, slabs, outbound/return). Nexus selection and visibility networking doubled; no real client camera tested.\n"+pipeline+ambient);failed=true;command("stop");}return;}
    if(stage==-1){if(++ticks>=20)initActors();return;}
    if(stage==0){if(++ticks>=20){
      Object actions=call(call(actor,"getEasyNPCActionEventData"),"getActionEventSet");Object row=child(child(call(actions,"createTag"),"ActionEventSet"),"ON_INTERACTION");
      check(Pq01Actions.equivalent(row,Pq01Actions.plan(null,"gautier_large","interact")),"actor custom interaction retained after 20 real ticks");
      begin(gaRecord,new Pq01Motion.Point(dimension(world),9.5,64.5,0.5));
    }return;}
    Object a=stage<=2?gaRecord:perrinRecord;
    if(stage==1){Pq01Motion.Point at=pos(actor,world);if(at.x()>3.5&&at.x()<7&&at.y()>64.9&&Math.abs(at.z()-0.5)<0.5)stairsSeen=true;if(at.x()>=8&&Math.abs(at.y()-64.5)<0.1)slabSeen=true;}
    Pq01Motion motion=(Pq01Motion)reflect(a,"motion");if(motion!=null&&!motion.done())motion.tick();
    if(ticks<0){
      if(++ticks<0)return;
      if(stage==1){check(stairsSeen&&slabSeen,"Gautier actually crossed raised stair deck and fractional slab floor");begin(gaRecord,new Pq01Motion.Point(dimension(world),0.5,64,0.5));}
      else if(stage==2)begin(perrinRecord,new Pq01Motion.Point(dimension(world),8.5,64,4.5));
      else if(stage==3)begin(perrinRecord,new Pq01Motion.Point(dimension(world),0.5,64,4.5));
      else if(stage==4){
        command("fill 7 64 3 10 67 6 minecraft:stone");Object data=storage(player,"capitale:pq01_setup");
        data=GuardNbt.copy(data);GuardNbt.integer(data,"move_timeout_ticks",80);GuardNbt.save(server,"capitale:pq01_setup",data);
        begin(perrinRecord,new Pq01Motion.Point(dimension(world),8.5,64,4.5));
      }else {
        check(pos(source,world).distance2(new Pq01Motion.Point(dimension(world),0.5,64,0.5))<0.001,"Gautier original never moved");
        check(pos(perrinSource,world).distance2(new Pq01Motion.Point(dimension(world),0.5,64,4.5))<0.001,"Perrin original never moved");
        invoke("close",new Class<?>[]{Object.class,boolean.class},player,false);
        check(Boolean.TRUE.equals(call(actor,"method_31481"))&&Boolean.TRUE.equals(call(perrin,"method_31481")),"actors discarded during production cleanup");
        pipeline=Pq01PipelineBench.runAll(player);Pq01SceneWorldBench.start(player);stage=6;
      }
    }else if(++ticks>400)throw new AssertionError("world test timeout leg "+stage);
  }
  static void fail(Throwable e){failed=true;e.printStackTrace();try{java.io.StringWriter trace=new java.io.StringWriter();e.printStackTrace(new java.io.PrintWriter(trace));Files.writeString(Path.of("world-test-result.txt"),"FAIL "+trace+"\n"+String.join("\n",evidence));if(server!=null)command("stop");}catch(Throwable ignored){}}
}
