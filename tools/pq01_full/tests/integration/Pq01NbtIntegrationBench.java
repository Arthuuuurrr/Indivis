package net.hautecapitale.quetes.sequence;

import java.lang.reflect.*;
import java.util.*;
import static net.hautecapitale.quetes.sequence.SequenceBridge.call;

/** Vraies classes Minecraft 1.21.11 et EasyNPC 7.16.0 ; aucun monde créé. */
public final class Pq01NbtIntegrationBench {
  static int checks;
  static void check(boolean v,String message){checks++;if(!v)throw new AssertionError(message);}
  static Object row(String type,String command){Object n=GuardNbt.compound();GuardNbt.string(n,"Type",type);GuardNbt.string(n,"Cmd",command);return n;}
  static Object list(Object...rows){return GuardNbt.list(Arrays.asList(rows));}
  static List<?> rows(Object list){return (List<?>)list;}
  static Object entry(Object list,int index){return rows(list).get(index);}
  static String text(Object n,String key){return Pq01Bridge.str(n,key,"");}
  static void denied(Runnable run,String message){boolean denied=false;try{run.run();}catch(IllegalStateException|IllegalArgumentException e){denied=true;}check(denied,message);}

  public static final class Holder {
    public Object events;int updates;
    Holder(Object e){events=e;}
    public Holder getEasyNPCActionEventData(){return this;}
    public Object getActionEventSet(){return events;}
    public void setActionEventSet(Object e){events=e;updates++;}
  }
  public static void main(String[] args)throws Exception {
    Class<?> element=Class.forName("net.minecraft.class_2520");
    check(element.getProtectionDomain().getCodeSource().getLocation().toString().contains("minecraft-intermediary-1.21.11.jar"),"classes NBT de Minecraft réel");
    check(Pq01Actions.class.getProtectionDomain().getCodeSource().getLocation().toString().contains(System.getProperty("indivis.expected.jar","PQ01-R3-HF2.jar")),"code de production du JAR attendu");
    check(GuardNbt.class.getProtectionDomain().getCodeSource().getLocation().equals(Pq01Actions.class.getProtectionDomain().getCodeSource().getLocation()),"helper historique réel du même JAR");
    Object compound=GuardNbt.compound();GuardNbt.string(compound,"name","Althéon Brumeforge");GuardNbt.integer(compound,"value",42);
    Object strings=list(GuardNbt.stringValue("Géraud"),GuardNbt.stringValue("Althéon"));
    Object mutable=list(compound,strings);Object copied=Pq01Nbt.copy(mutable);
    check(mutable.equals(copied)&&mutable!=copied,"copie d'une liste NBT réelle");
    check(entry(mutable,0)!=entry(copied,0)&&entry(mutable,1)!=entry(copied,1),"copie profonde des valeurs mutables");
    GuardNbt.string(entry(copied,0),"name","modifié");check(text(compound,"name").equals("Althéon Brumeforge"),"isolation après modification de la copie");
    check(Pq01Nbt.copy(GuardNbt.read(compound,"name")).equals(GuardNbt.read(compound,"name")),"copie du nom NbtString");
    check(Pq01Nbt.copy(GuardNbt.read(compound,"value")).equals(GuardNbt.read(compound,"value")),"copie d'un entier NBT");
    check(Pq01Nbt.copy(compound).equals(compound)&&Pq01Nbt.copy(null)==null,"composé et valeur absente");
    denied(()->GuardNbt.copy(mutable),"ancien appel Compound.copy reproduit l'erreur sur NbtList");
    denied(()->GuardNbt.copy(GuardNbt.read(compound,"name")),"même erreur ancienne sur NbtString");

    String bare="capitale:quest/spawn_dirigeable_couronne/capitaine_large/interact_self";
    Object legacy=Pq01Actions.normalized(list(row("COMMAND","hcd_interpellation nearest_named Capitaine Althéon Brumeforge"),row("COMMAND",bare),row("CUSTOM","thirdparty:cosmetic hello")));
    Object backup=Pq01Nbt.copy(legacy),planned=Pq01Actions.plan(legacy,"altheon_large","interact");
    check(legacy.equals(backup),"préparation sans écriture dans l'original");
    check(rows(planned).size()==3,"liste multiple conservée");
    check(entry(planned,0).equals(entry(legacy,0))&&entry(planned,2).equals(entry(legacy,2)),"ordre et autres actions conservés");
    check(text(entry(planned,1),"Type").equals("CUSTOM")&&text(entry(planned,1),"Cmd").equals(Pq01Actions.command("altheon_large","interact")),"ancien chemin nu remplacé par l'action native");
    check(Pq01Actions.equivalent(planned,Pq01Actions.normalized(planned)),"aller-retour sérialisation EasyNPC réel");
    check(Pq01Actions.plan(planned,"altheon_large","interact").equals(planned),"liaison répétable sans duplication");
    Object saved=GuardNbt.compound();GuardNbt.put(saved,"original_interaction",Pq01Nbt.copy(backup));GuardNbt.put(saved,"linked_interaction",Pq01Nbt.copy(planned));
    Pq01Actions.assertUnchanged(saved,planned,null);check(true,"liaison existante inchangée autorisée");
    Pq01Actions.assertUnchanged(saved,backup,null);check(true,"ancienne écriture interrompue peut être reprise");
    Object edited=Pq01Nbt.copy(planned);call(edited,"add",row("CUSTOM","thirdparty:new_after_binding"));
    denied(()->Pq01Actions.assertUnchanged(saved,edited,null),"réédition externe protégée lors d'une nouvelle liaison");
    check(Pq01Bridge.child(saved,"original_interaction").equals(backup),"la sauvegarde d'origine n'est pas réécrite");
    Object unknown=list(row("COMMAND","say ne_pas_effacer"));Object unknownCopy=Pq01Nbt.copy(unknown);
    denied(()->Pq01Actions.plan(unknown,"altheon_large","interact"),"aucun remplacement d'interaction étrangère sans hook connu");
    check(unknown.equals(unknownCopy),"refus sans mutation de l'interaction étrangère");
    denied(()->Pq01Actions.plan(list(row("COMMAND",bare),row("COMMAND","function "+bare)),"altheon_large","interact"),"deux hooks refusés");
    denied(()->Pq01Actions.plan(GuardNbt.stringValue("invalid"),"altheon_large","interact"),"type d'actions invalide refusé");
    check(text(entry(Pq01Actions.plan(list(row("COMMAND","/function "+bare)),"altheon_large","interact"),0),"Type").equals("CUSTOM"),"ancienne syntaxe /function reconnue");
    check(text(entry(Pq01Actions.plan(list(row("COMMAND","capitale:quest/spawn_dirigeable_couronne/geraud_large_start_self")),"geraud_large","interact"),0),"Type").equals("CUSTOM"),"ancien démarrage Géraud reconnu");
    check(text(entry(Pq01Actions.plan(list(row("COMMAND","capitale:quest/spawn_dirigeable_couronne/geraud_quai_interact_self")),"geraud_quai","interact"),0),"Type").equals("CUSTOM"),"ancien quai Géraud reconnu");
    check(Pq01Actions.migrateNear(unknown,"altheon_large")==null,"near personnalisé préservé");
    check(Pq01Actions.migrateNear(null,"altheon_large")!=null,"near absent créé");
    Object near=list(row("COMMAND","capitale:quest/spawn_dirigeable_couronne/capitaine_large/near_self"));
    check(text(entry(Pq01Actions.migrateNear(near,"altheon_large"),0),"Type").equals("CUSTOM"),"near historique converti");

    for(String role:List.of("geraud","altheon","gautier","hugues","anselme","perrin"))for(String phase:List.of("large","quai")){
      String key=role+"_"+phase;Object value=Pq01Actions.plan(null,key,"interact");
      check(rows(value).size()==1&&text(entry(value,0),"Type").equals("CUSTOM"),"action native "+key);
      Object parsed=Pq01Bridge.stat("de.markusbordihn.easynpc.data.action.CustomActionCommand","parse",text(entry(value,0),"Cmd"));
      check(Boolean.TRUE.equals(call(parsed,"isValid"))&&call(parsed,"arguments").equals(List.of(key,"interact")),"parseur EasyNPC réel "+key);
      check(Pq01Actions.equivalent(value,Pq01Actions.normalized(value)),"sérialisation stable "+key);
    }
    denied(()->Pq01Actions.command("geraud_large","op"),"événement non autorisé");
    denied(()->Pq01Actions.command("geraud_large;op","interact"),"rôle non autorisé");
    denied(()->Pq01Actions.command("gautier_quai","near"),"near non autorisé");

    // Le conteneur de test remplace uniquement le stockage de l'entité ;
    // ActionEventSet, ActionDataSet et leur sérialisation sont ceux du JAR EasyNPC.
    Object eventTag=GuardNbt.compound(),all=GuardNbt.compound();
    GuardNbt.put(all,"ON_INTERACTION",legacy);GuardNbt.put(all,"ON_DISTANCE_NEAR",Pq01Actions.normalized(near));
    GuardNbt.put(all,"ON_INTERVAL_LONG",Pq01Actions.normalized(list(row("CUSTOM","thirdparty:interval preserved"))));
    GuardNbt.put(eventTag,"ActionEventSet",all);
    Holder holder=new Holder(Pq01Bridge.make("de.markusbordihn.easynpc.data.action.ActionEventSet",eventTag));
    Object before=call(holder.events,"createTag"),beforeCopy=Pq01Nbt.copy(before);
    Pq01Actions.mutate(holder,"ON_INTERACTION",planned);
    Object after=Pq01Bridge.child(call(holder.events,"createTag"),"ActionEventSet");
    check(holder.updates==1,"API de mise à jour appelée");
    check(before.equals(beforeCopy),"ancien ensemble non muté par référence");
    check(Pq01Actions.equivalent(Pq01Bridge.child(after,"ON_INTERACTION"),planned),"interaction modifiée et relue");
    check(Pq01Bridge.child(after,"ON_DISTANCE_NEAR").equals(Pq01Bridge.child(Pq01Bridge.child(before,"ActionEventSet"),"ON_DISTANCE_NEAR")),"autre événement near conservé");
    check(Pq01Bridge.child(after,"ON_INTERVAL_LONG").equals(Pq01Bridge.child(Pq01Bridge.child(before,"ActionEventSet"),"ON_INTERVAL_LONG")),"autre événement intervalle conservé");
    Pq01Actions.mutate(holder,"ON_INTERACTION",backup);
    check(Pq01Actions.equivalent(Pq01Bridge.child(Pq01Bridge.child(call(holder.events,"createTag"),"ActionEventSet"),"ON_INTERACTION"),legacy),"restauration de toutes les anciennes actions");
    Pq01Actions.mutate(holder,"ON_INTERACTION",null);
    check(Pq01Bridge.child(Pq01Bridge.child(call(holder.events,"createTag"),"ActionEventSet"),"ON_INTERACTION")==null,"restauration d'un événement absent");

    Pq01Actions.register();
    Object id=Pq01Bridge.stat("net.minecraft.class_2960","method_60654",Pq01Actions.ID);
    Object executor=Pq01Bridge.stat("de.markusbordihn.easynpc.api.action.ActionRegistry","get",id);
    check(executor!=null,"action inscrite dans le vrai registre EasyNPC");
    Class<?> contract=Class.forName("de.markusbordihn.easynpc.api.action.CustomActionExecutor");
    for(Method method:contract.getMethods())if(method.getName().equals("execute")){
      Object[] values=method.getParameterTypes()[2]==List.class?new Object[]{null,null,List.of("geraud_large","interact"),Pq01Bridge.en("de.markusbordihn.easynpc.data.action.ActionContext","EMPTY")}:new Object[]{null,null,null,List.of("geraud_large","interact")};
      method.invoke(executor,values);check(true,"signature réelle "+method.toGenericString());
    }
    System.out.println("PASS "+checks+" assertions avec NBT Minecraft 1.21.11 et actions/registre EasyNPC 7.16.0 réels. Stockage d'entité doublé ; aucun monde ni serveur démarré.");
  }
}
