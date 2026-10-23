package net.hautecapitale.quetes.sequence;
import java.lang.reflect.*;
import java.util.*;
/** Vrai dispatcher EasyNPC et proxy PQ01. Joueur non nul alloué sans monde. */
public final class Pq01CallbackBench {
  static int calls,checks;
  static void check(boolean ok,String message){checks++;if(!ok)throw new AssertionError(message);}
  public static void main(String[] args)throws Exception {
    Class.forName("net.minecraft.class_155").getMethod("method_36208").invoke(null);
    Class.forName("net.minecraft.class_2966").getMethod("method_12851").invoke(null);
    Pq01Actions.register();
    Class<?> unsafeType=Class.forName("sun.misc.Unsafe");Field field=unsafeType.getDeclaredField("theUnsafe");field.setAccessible(true);Object unsafe=field.get(null);
    Object player=unsafeType.getMethod("allocateInstance",Class.class).invoke(unsafe,Class.forName("net.minecraft.class_3222"));
    check(player!=null,"joueur de type Minecraft réel non nul");
    Class<?> easy=Class.forName("de.markusbordihn.easynpc.entity.easynpc.EasyNPC");
    Object npc=Proxy.newProxyInstance(easy.getClassLoader(),new Class<?>[]{easy},(p,m,a)->{
      if(m.getDeclaringClass()==Object.class)return Pq01Bridge.objectMethod(p,m,a);
      if(m.getName().equals("getEntity")){calls++;return null;} // Stop avant tout accès au monde absent.
      throw new AssertionError("Unexpected EasyNPC method: "+m);
    });
    Object set=Pq01Actions.dataSet(Pq01Actions.plan(null,"geraud_large","interact"));
    Object row=((Set<?>)SequenceBridge.call(set,"getEntries")).iterator().next();
    Object context=Pq01Bridge.stat("de.markusbordihn.easynpc.data.action.ActionContext","of",Pq01Bridge.en("de.markusbordihn.easynpc.data.action.ActionEventType","ON_INTERACTION"),player);
    Pq01Bridge.stat("de.markusbordihn.easynpc.entity.easynpc.handlers.action.executor.CustomActionDispatcher","execute",row,npc,context);
    check(calls==1,"dispatcher natif -> proxy PQ01 -> initiateur non nul -> getEntity");
    Object id=Pq01Bridge.stat("net.minecraft.class_2960","method_60654",Pq01Actions.ID);
    Object executor=Pq01Bridge.stat("de.markusbordihn.easynpc.api.action.ActionRegistry","get",id);
    for(Method method:Class.forName("de.markusbordihn.easynpc.api.action.CustomActionExecutor").getMethods())if(method.getName().equals("execute")&&method.getParameterTypes()[2]!=List.class){
      method.invoke(executor,row,npc,player,List.of("geraud_large","interact"));
    }
    check(calls==2,"ancienne signature avec joueur explicite également dispatchée");
    System.out.println("PASS "+checks+" assertions ; vrai CustomActionDispatcher EasyNPC 7.16.0 et proxy PQ01. Joueur réel alloué sans constructeur ; getEntity doublé à null ; aucun monde ni paquet de clic exécuté.");
  }
}
