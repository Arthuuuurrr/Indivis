package net.indivis.admin;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;

public final class FabricRuntime {
  private record Request(Object player,String mode,String argument) {}
  private static final Map<UUID,Request> PENDING=new LinkedHashMap<>();
  private static final AdminController CONTROLLER=new AdminController();
  private static boolean initialized;
  private static Object objectMethod(Object p,Method m,Object[] a) {
    return switch(m.getName()){case "toString"->"IndivisAdminCallback";case "hashCode"->System.identityHashCode(p);case "equals"->p==a[0];default->null;};
  }
  private static Object proxy(String contract,Function<Object[],Object> callback) {
    Class<?> type=Reflect.type(contract);
    return Proxy.newProxyInstance(type.getClassLoader(),new Class<?>[]{type},(p,m,a)->m.getDeclaringClass()==Object.class?objectMethod(p,m,a):callback.apply(a));
  }
  private static void event(String owner,String listener,String field,Function<Object[],Object> callback) {
    Object event=Reflect.field(owner,field);Object adapter=proxy(listener,callback);
    Reflect.call(event,"register",adapter);
  }
  public static void init() {
    if(initialized)return;initialized=true;
    event("net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback","net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback","EVENT",a->{register(a[0]);return null;});
    event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents","net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick","END_SERVER_TICK",a->{tick(a[0]);return null;});
    event("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents","net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStopped","SERVER_STOPPED",a->{PENDING.clear();CONTROLLER.clear();return null;});
    System.out.println("[Indivis Admin] 0.1.0-alpha.3 — /indivisadmin — "+Catalog.ACTIONS.size()+" fonctions cataloguées ; annotations Mixin corrigées");
  }
  private static Object literal(String name){return Reflect.stat("com.mojang.brigadier.builder.LiteralArgumentBuilder","literal",name);}
  private static void execute(Object node,String mode,String argumentName) {
    Reflect.call(node,"executes",proxy("com.mojang.brigadier.Command",a->{
      try {
        Object context=a[0],source=Reflect.call(context,"getSource"),player=Reflect.call(source,"method_9207");
        String value=argumentName==null?"":(String)Reflect.stat("com.mojang.brigadier.arguments.StringArgumentType","getString",context,argumentName);
        UUID id=(UUID)Reflect.call(player,"method_5667");PENDING.putIfAbsent(id,new Request(player,mode,value));return 1;
      }catch(RuntimeException e){System.err.println("[Indivis Admin] Commande refusée : "+e.getMessage());return 0;}
    }));
  }
  private static void register(Object dispatcher) {
    Object root=literal("indivisadmin");
    Reflect.call(root,"requires",Reflect.stat("net.minecraft.class_2170","method_71774",Reflect.field("net.minecraft.class_2170","field_31839")));
    execute(root,"open",null);
    for(String mode:List.of("ui","joueur")) {
      String key=mode.equals("ui")?"token":"nom";Object child=literal(mode);
      Object argument=Reflect.stat("com.mojang.brigadier.builder.RequiredArgumentBuilder","argument",key,Reflect.stat("com.mojang.brigadier.arguments.StringArgumentType","word"));
      execute(argument,mode,key);Reflect.call(child,"then",argument);Reflect.call(root,"then",child);
    }
    Reflect.call(dispatcher,"register",root);
  }
  private static void tick(Object server) {
    if(PENDING.isEmpty())return;List<Request> requests=new ArrayList<>(PENDING.values());PENDING.clear();
    for(Request request:requests)try {
      UUID id=(UUID)Reflect.call(request.player,"method_5667");
      if(Reflect.call(Reflect.call(server,"method_3760"),"method_14602",id)!=request.player)continue;
      MinecraftPort port=new MinecraftPort(server,request.player);
      switch(request.mode){case "open"->CONTROLLER.open(port);case "joueur"->CONTROLLER.selectByName(port,request.argument);default->CONTROLLER.click(port,request.argument);}
    }catch(RuntimeException e){System.err.println("[Indivis Admin] Échec de l’interface : "+e.getMessage());e.printStackTrace(System.err);}
  }
  private FabricRuntime(){}
}
