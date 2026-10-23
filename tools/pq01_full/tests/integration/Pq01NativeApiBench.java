package net.hautecapitale.quetes.sequence;

import java.lang.reflect.*;
import java.util.*;

/** Vérifie les membres publics des vraies classes, sans instancier de monde. */
public final class Pq01NativeApiBench {
  static int checks,failures;
  static final Set<String> COVERED=new TreeSet<>();
  static Class<?> type(String name)throws ClassNotFoundException{return Class.forName(name,false,Pq01NativeApiBench.class.getClassLoader());}
  static void methods(String owner,int argc,String...names){
    for(String name:names){checks++;COVERED.add(name);
      try{
        var found=Arrays.stream(type(owner).getMethods()).filter(m->m.getName().equals(name)&&m.getParameterCount()==argc).toList();
        if(found.isEmpty())throw new NoSuchMethodException(owner+"#"+name+"/"+argc);
        System.out.println("API OK "+owner+"#"+name+"/"+argc+" "+found);
      }catch(Throwable e){failures++;System.out.println("API FAIL "+owner+"#"+name+"/"+argc+" "+e);}
    }
  }
  public static void main(String[] args)throws Exception {
    methods("net.minecraft.class_1297",0,"method_23317","method_23318","method_23321","method_5752","method_31472","method_31481","method_36454","method_36455","method_5797","method_5829","method_73183");
    methods("net.minecraft.class_1297",1,"method_5780","method_5665","method_5684","method_5803","method_36456","method_36457","method_18799");
    methods("net.minecraft.class_3222",0,"method_51469");
    methods("net.minecraft.class_1937",0,"method_27983");
    methods("net.minecraft.class_5321",0,"method_29177");
    methods("net.minecraft.class_1937",3,"method_8333");
    methods("net.minecraft.class_3218",1,"method_66347");
    methods("net.minecraft.class_238",1,"method_1014");
    methods("net.minecraft.class_2487",2,"method_68564","method_68083","method_10549","method_10556");
    methods("net.minecraft.class_2487",1,"method_10551","method_10580");
    methods("net.minecraft.class_2520",0,"method_10707");
    methods("net.minecraft.class_4565",1,"method_22546");
    methods("net.minecraft.server.MinecraftServer",0,"method_22827","method_3760");
    methods("net.minecraft.class_3324",0,"method_14571");
    methods("net.minecraft.class_2561",1,"method_43470");
    methods("net.minecraft.class_2960",1,"method_60654");
    methods("net.minecraft.class_2170",1,"method_71774");
    methods("net.minecraft.class_2168",0,"method_9207");
    methods("net.minecraft.class_1309",1,"method_5847","method_5636","method_6104");
    methods("net.minecraft.class_1308",1,"method_5977","method_5875");
    methods("net.minecraft.class_1355",1,"method_35113");
    methods("net.minecraft.class_1408",0,"method_6340");
    methods("net.minecraft.class_1408",4,"method_6337");
    methods("net.minecraft.class_1297",1,"method_30949","method_5697");
    methods("net.minecraft.class_1297",0,"method_5696","method_21749","method_21750","method_33189","method_49108","method_5732","method_5810");
    methods("net.minecraft.class_1355",2,"method_6277");
    String easy="de.markusbordihn.easynpc.";
    methods(easy+"entity.easynpc.EasyNPC",0,"getEntity","getEntityTypeId","getEasyNPCActionEventData","getEasyNPCAttributeData");
    methods(easy+"entity.easynpc.data.ActionEventDataCapable",0,"getActionEventSet");
    methods(easy+"entity.easynpc.data.ActionEventDataCapable",1,"setActionEventSet");
    methods(easy+"data.action.ActionEventSet",0,"createTag");
    methods(easy+"data.action.ActionEventSet",2,"setActionEvent");
    methods(easy+"data.action.ActionDataSet",1,"load","save");
    methods(easy+"data.action.ActionContext",0,"initiator");
    methods(easy+"api.action.ActionRegistry",2,"register");
    methods(easy+"api.action.ActionRegistry",1,"get");
    methods(easy+"entity.easynpc.data.NavigationDataCapable",0,"getGroundPathNavigation","refreshNavigation");
    methods(easy+"entity.easynpc.EasyNPC",0,"getEntityGoalSelector","getEntityTargetSelector");
    methods(easy+"entity.easynpc.data.AttributeDataCapable",0,"getEntityAttributes");
    methods(easy+"entity.easynpc.data.AttributeDataCapable",1,"setEntityAttributes");
    methods(easy+"data.attribute.EntityAttributes",0,"createTag","getMovementAttributes","getInteractionAttributes","getInventoryAttributes");
    methods(easy+"data.attribute.EntityAttributes",1,"setMovementAttributes","setInteractionAttributes","setInventoryAttributes");
    methods(easy+"data.attribute.MovementAttributes",1,"withIsImmovable","withCanJump","withCanPassDoor","withCanOpenDoor","withCanCloseDoor","withCanUseNetherPortal","withNavigationType");
    methods(easy+"data.attribute.InteractionAttributes",1,"withIsPushable","withPushEntities","withCanBeHitByProjectile","withCanBeLeashed","withBlockVehicleMounting","withCanSitOnShoulder");
    methods(easy+"data.attribute.InventoryAttributes",1,"withDropOnDeath","withPickUpItems");
    String dm="net.hautecapitale.dialogue.session.DialogueManager";
    methods(dm,1,"onFermeture","sondeMouvement","session");methods(dm,2,"pnjOccupe","fermer");methods(dm,3,"ouvrir");
    methods(dm,1,"enSession");methods(dm,2,"refus","dire");
    methods("net.hautecapitale.dialogue.config.DialogueConfig",0,"get");
    methods("net.hautecapitale.dialogue.config.DialogueConfig",1,"capture");
    if(args.length>0){
      String source=java.nio.file.Files.readString(java.nio.file.Path.of(args[0]));var matcher=java.util.regex.Pattern.compile("\"(method_\\d+)\"").matcher(source);
      Set<String> missing=new TreeSet<>();while(matcher.find())if(!COVERED.contains(matcher.group(1)))missing.add(matcher.group(1));
      if(!missing.isEmpty()){failures++;System.out.println("UNCOVERED Minecraft methods: "+missing);}
    }
    System.out.println("REAL_API_LOOKUP checks="+checks+" failures="+failures+"; no Minecraft server/world created; existence/arity/public access checked, runtime behavior not covered.");
    if(failures!=0)throw new AssertionError("Native API mismatches: "+failures);
  }
}
