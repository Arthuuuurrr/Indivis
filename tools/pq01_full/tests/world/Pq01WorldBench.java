package net.hautecapitale.quetes.sequence;
import net.fabricmc.api.ModInitializer;
import java.util.*;
import java.lang.reflect.*;
import java.nio.file.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.*;
import static net.hautecapitale.quetes.sequence.Pq01Bridge.*;

/** Monde Fabric réel ; identité Nexus et émission de visibilité doublées seulement. */
public final class Pq01WorldBench implements ModInitializer {
  static Object server,world,player,view,source,perrinSource,actor,perrin;
  static Object gaRecord,perrinRecord;static int ticks,stage,checks;static boolean failed;
  static final List<String> evidence=new ArrayList<>();
  public void onInitialize(){
    event("net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback","EVENT",a->{register(a[0]);stub(a[0],"indivissequence");stub(a[0],"hcd_interpellation");return null;});
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
    Object type=stat("de.markusbordihn.easynpc.entity.ModEntityType","getEntityType",en("de.markusbordihn.easynpc.entity.ModNPCEntityType","HUMANOID"));
    Object e=call(type,"method_5883",world,en("net.minecraft.class_3730","field_16462"));
    call(e,"method_5814",x,64d,z);call(e,"method_5977",true);check(Boolean.TRUE.equals(call(world,"method_8649",e)),"source NPC inserted");return e;
  }
  static Object bindingFor(Object npc){Object n=GuardNbt.compound();GuardNbt.string(n,"uuid",uuid(npc).toString());GuardNbt.string(n,"dimension",dimension(world));GuardNbt.put(n,"post",pointNbt(pos(npc,world)));return n;}
  static Object spawn(String role)throws Exception{return invoke("spawn",new Class<?>[]{view.getClass(),String.class,Pq01Motion.Point.class},view,role,null);}
  static void start(Object s)throws Exception{
    server=s;world=call(s,"method_30002");
    Object profile=make("com.mojang.authlib.GameProfile",UUID.randomUUID(),"PQ01WorldTest");
    Object options=stat("net.minecraft.class_8791","method_53821");
    player=make("net.minecraft.class_3222",server,world,profile,options);call(player,"method_5814",0.5d,64d,1.5d);
    command("forceload add -16 -16 32 16");command("fill -12 63 -12 20 63 12 minecraft:stone");
    source=npc(0.5,0.5);perrinSource=npc(0.5,4.5);
    Object skin=stat("de.markusbordihn.easynpc.data.skin.SkinDataEntry","createResourceLocationSkin",stat("net.minecraft.class_2960","method_60654","minecraft:textures/entity/player/wide/steve.png"));
    call(call(source,"getEasyNPCSkinData"),"setSkinDataEntry",skin);
    call(call(source,"getEasyNPCModelData"),"setModelPoseName","hf4_copy_probe");
    command("item replace entity "+uuid(source)+" weapon.mainhand with minecraft:stick");
    call(source,"method_5665",stat("net.minecraft.class_2561","method_43470","Gautier source test"));
    Object data=GuardNbt.compound(),bindings=GuardNbt.childOrCreate(data,"bindings");
    GuardNbt.put(bindings,"gautier_large",bindingFor(source));GuardNbt.put(bindings,"perrin_large",bindingFor(perrinSource));GuardNbt.save(server,"capitale:pq01_setup",data);
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
      check(true,"actual navigation leg "+stage+" arrived");ticks=-1000;
    },(java.util.function.Consumer<String>)reason->{throw new AssertionError("navigation leg "+stage+": "+reason);});
  }
  static void tick()throws Exception{
    if(server==null||failed)return;
    if(stage==0){if(++ticks>=20){
      Object actions=call(call(actor,"getEasyNPCActionEventData"),"getActionEventSet");Object row=child(child(call(actions,"createTag"),"ActionEventSet"),"ON_INTERACTION");
      check(Pq01Actions.equivalent(row,Pq01Actions.plan(null,"gautier_large","interact")),"actor custom interaction retained after 20 real ticks");
      begin(gaRecord,new Pq01Motion.Point(dimension(world),8.5,64,0.5));
    }return;}
    Object a=stage<=2?gaRecord:perrinRecord;
    Pq01Motion motion=(Pq01Motion)reflect(a,"motion");if(motion!=null&&!motion.done())motion.tick();
    if(ticks<0){
      if(stage==1)begin(gaRecord,new Pq01Motion.Point(dimension(world),0.5,64,0.5));
      else if(stage==2)begin(perrinRecord,new Pq01Motion.Point(dimension(world),8.5,64,4.5));
      else if(stage==3)begin(perrinRecord,new Pq01Motion.Point(dimension(world),0.5,64,4.5));
      else {
        check(pos(source,world).distance2(new Pq01Motion.Point(dimension(world),0.5,64,0.5))<0.001,"Gautier original never moved");
        check(pos(perrinSource,world).distance2(new Pq01Motion.Point(dimension(world),0.5,64,4.5))<0.001,"Perrin original never moved");
        invoke("close",new Class<?>[]{Object.class,boolean.class},player,false);
        check(Boolean.TRUE.equals(call(actor,"method_31481"))&&Boolean.TRUE.equals(call(perrin,"method_31481")),"actors discarded during production cleanup");
        Files.writeString(Path.of("world-test-result.txt"),"PASS "+checks+" checks\n"+String.join("\n",evidence)+"\nReal Fabric 1.21.11 world and EasyNPC 7.16.0 navigation. Nexus selection and visibility networking doubled; no real client camera tested.\n");
        failed=true;command("stop");
      }
    }else if(++ticks>400)throw new AssertionError("world test timeout leg "+stage);
  }
  static void fail(Throwable e){failed=true;e.printStackTrace();try{Files.writeString(Path.of("world-test-result.txt"),"FAIL "+e+"\n"+String.join("\n",evidence));if(server!=null)command("stop");}catch(Throwable ignored){}}
}
