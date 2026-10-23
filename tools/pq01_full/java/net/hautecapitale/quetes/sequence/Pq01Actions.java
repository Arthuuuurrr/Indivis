package net.hautecapitale.quetes.sequence;

import java.lang.reflect.*;
import java.util.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.call;

/** Adaptation bornée à l'API d'actions EasyNPC 7.16.0. Aucune commande libre. */
final class Pq01Actions {
  static final String ID="haute_capitale_quetes:pq01";
  private static final String DATA="de.markusbordihn.easynpc.data.action.";
  private static final List<String> ROLES=List.of("geraud","altheon","gautier","hugues","anselme","perrin");
  private Pq01Actions(){}
  static boolean validKey(String key){
    int i=key.lastIndexOf('_');return i>0&&ROLES.contains(key.substring(0,i))&&List.of("large","quai").contains(key.substring(i+1));
  }
  static String command(String key,String event){
    if(!validKey(key)||!List.of("interact","near").contains(event)||event.equals("near")&&!List.of("geraud_large","altheon_large").contains(key))throw new IllegalArgumentException("Action PQ01 inconnue");
    return ID+" "+key+" "+event;
  }
  static String function(String key,String event){
    command(key,event);String role=key.substring(0,key.lastIndexOf('_')),phase=key.substring(key.lastIndexOf('_')+1);
    return event.equals("interact")?Pq01Bridge.hook(role,phase):"function capitale:quest/spawn_dirigeable_couronne/"+(role.equals("geraud")?"geraud_large_start_self":"capitaine_large/near_self");
  }
  static String normalizedCommand(String text){
    String s=text.strip().replaceFirst("^/","");
    return s.startsWith("capitale:")?"function "+s:s;
  }
  static boolean recognized(Object row,String key,String event){
    String kind=Pq01Bridge.str(row,"Type",""),text=Pq01Bridge.str(row,"Cmd","");
    if(kind.equals("CUSTOM"))return text.strip().equals(command(key,event));
    if(!kind.equals("COMMAND"))return false;
    text=normalizedCommand(text);
    if(text.equals(function(key,event)))return true;
    if(event.equals("interact")&&key.equals("geraud_large"))return text.equals("function capitale:quest/spawn_dirigeable_couronne/geraud_large_start_self");
    if(event.equals("interact")&&key.equals("geraud_quai"))return text.equals("function capitale:quest/spawn_dirigeable_couronne/geraud_quai_interact_self");
    return false;
  }
  static Object row(Object previous,String key,String event){
    Object n=previous==null?GuardNbt.compound():Pq01Nbt.copy(previous);
    GuardNbt.string(n,"Type","CUSTOM");GuardNbt.string(n,"Cmd",command(key,event));
    call(n,"method_10551","ExecAsUser");call(n,"method_10551","PermLevel");
    return n;
  }
  static Object dataSet(Object list){
    Object set=Pq01Bridge.make(DATA+"ActionDataSet");
    if(list!=null)call(set,"load",Pq01Nbt.copy(list));return set;
  }
  static Object normalized(Object list){
    if(list==null)return null;
    if(!(list instanceof List<?> input))throw new IllegalStateException("Actions EasyNPC non représentées par une liste");
    Object output=GuardNbt.list(List.of());call(dataSet(list),"save",output);
    if(((List<?>)output).size()!=input.size())throw new IllegalStateException("Une action EasyNPC existante est invalide ; aucune modification effectuée");
    return output;
  }
  static Object plan(Object previous,String key,String event){
    command(key,event);
    if(previous==null||previous instanceof List<?> l&&l.isEmpty())return normalized(GuardNbt.list(List.of(row(null,key,event))));
    if(!(previous instanceof List<?> rows))throw new IllegalStateException("Liste d'actions EasyNPC attendue");
    int matches=0;List<Object> result=new ArrayList<>();
    for(Object old:rows){
      if(recognized(old,key,event)){matches++;result.add(row(old,key,event));}
      else result.add(Pq01Nbt.copy(old));
    }
    if(matches!=1)throw new IllegalStateException("Liaison "+key+" refusée : "+matches+" déclencheur(s) PQ01 reconnu(s). Actions conservées : "+describe(previous));
    return normalized(GuardNbt.list(result));
  }
  static Object migrateNear(Object previous,String key){
    if(previous==null||previous instanceof List<?> l&&l.isEmpty())return plan(previous,key,"near");
    if(previous instanceof List<?> rows&&rows.stream().anyMatch(row->recognized(row,key,"near")))return plan(previous,key,"near");
    return null; // near personnalisé : le conserver intégralement.
  }
  static String describe(Object list){
    if(!(list instanceof List<?> rows))return String.valueOf(list);
    return rows.stream().map(row->Pq01Bridge.str(row,"Type","?")+"="+Pq01Bridge.str(row,"Cmd","")).toList().toString();
  }
  static boolean equivalent(Object a,Object b){
    if(a instanceof List<?> x&&x.isEmpty())a=null;
    if(b instanceof List<?> x&&x.isEmpty())b=null;
    return Objects.equals(a==null?null:normalized(a),b==null?null:normalized(b));
  }
  static void assertUnchanged(Object record,Object interaction,Object near){
    if(record==null)return;
    for(String event:List.of("interaction","near")){
      Object linked=Pq01Bridge.child(record,"linked_"+event),actual=event.equals("interaction")?interaction:near;
      // Une écriture interrompue peut encore présenter la version originale.
      // Toute troisième version reste une modification externe à préserver.
      if(linked!=null&&!equivalent(linked,actual)&&!equivalent(Pq01Bridge.child(record,"original_"+event),actual))throw new IllegalStateException("Actions "+event+" modifiées depuis la liaison ; aucun écrasement ni remplacement de leur sauvegarde");
    }
  }
  static void mutate(Object npc,String name,Object value){
    // L'API met à jour le serveur et le drapeau d'interaction synchronisé,
    // sans recharger l'entité complète par /data modify entity.
    Object capable=call(npc,"getEasyNPCActionEventData");
    Object current=call(capable,"getActionEventSet");
    Object replacement=current==null?Pq01Bridge.make(DATA+"ActionEventSet"):Pq01Bridge.make(DATA+"ActionEventSet",Pq01Nbt.copy(call(current,"createTag")));
    Object event=Pq01Bridge.en(DATA+"ActionEventType",name);
    call(replacement,"setActionEvent",event,dataSet(value));
    call(capable,"setActionEventSet",replacement);
    Object actual=Pq01Bridge.child(Pq01Bridge.child(call(call(capable,"getActionEventSet"),"createTag"),"ActionEventSet"),name);
    if(!equivalent(value,actual))throw new IllegalStateException("Écriture EasyNPC non confirmée : "+name);
  }
  static void register(){
    Class<?> executor=Pq01Bridge.type("de.markusbordihn.easynpc.api.action.CustomActionExecutor");
    Object proxy=Proxy.newProxyInstance(executor.getClassLoader(),new Class<?>[]{executor},(p,m,args)->{
      if(m.getDeclaringClass()==Object.class)return Pq01Bridge.objectMethod(p,m,args);
      if(!m.getName().equals("execute")||args==null||args.length!=4)throw new IllegalStateException("Signature CustomActionExecutor inattendue");
      Object player,words;
      if(args[2] instanceof List<?>){words=args[2];player=call(args[3],"initiator");}
      else {player=args[2];words=args[3];}
      if(player==null||args[1]==null)return null;
      if(words instanceof List<?> list&&list.size()==2&&list.get(0) instanceof String key&&list.get(1) instanceof String event){
        try{Pq01Bridge.interact(player,call(args[1],"getEntity"),key,event);}
        catch(RuntimeException e){Pq01Bridge.report(e);if(player!=null)Pq01Bridge.say(player,"Interaction PQ01 annulée : "+e.getMessage());}
      }
      return null;
    });
    Object id=Pq01Bridge.stat("net.minecraft.class_2960","method_60654",ID);
    Pq01Bridge.stat("de.markusbordihn.easynpc.api.action.ActionRegistry","register",id,proxy);
  }
}
