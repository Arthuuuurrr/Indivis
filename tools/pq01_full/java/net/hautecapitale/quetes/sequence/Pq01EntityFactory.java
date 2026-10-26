package net.hautecapitale.quetes.sequence;
import java.util.UUID;
import static net.hautecapitale.quetes.sequence.SequenceBridge.call;
/** Création hors monde puis chargement NBT ; aucune commande /summon silencieuse. */
final class Pq01EntityFactory {
  private Pq01EntityFactory(){}
  static Object create(Object source,Object world,Object template,UUID id,Pq01Motion.Point at){
    Object entityType=call(source,"method_5864");
    Object entity=call(entityType,"method_5883",world,Pq01Bridge.en("net.minecraft.class_3730","field_16462"));
    if(entity==null)throw new IllegalStateException("Le type de PNJ n'a pas pu créer son acteur.");
    try{
      call(entity,"method_5826",id);
      Pq01Bridge.stat("de.markusbordihn.easynpc.utils.CompoundTagUtils","writeUUID",template,"UUID",id);
      Object input=Pq01Bridge.stat("net.minecraft.class_11352","method_71417",Pq01Bridge.en("net.minecraft.class_8942","field_60348"),call(world,"method_30349"),template);
      call(entity,"method_5651",input);call(entity,"method_5826",id);
      call(entity,"method_5814",at.x(),at.y(),at.z());
      if(!id.equals(SequenceBridge.uuid(entity))||!Pq01Bridge.actor(entity))throw new IllegalStateException("Identité d'acteur incohérente après copie.");
      return entity;
    }catch(RuntimeException failure){call(entity,"method_31472");throw failure;}
  }
  static String registryId(Object entity){return String.valueOf(Pq01Bridge.stat("net.minecraft.class_1299","method_5890",call(entity,"method_5864")));}
}
