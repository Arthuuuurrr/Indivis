package fr.indivis.nexusnpc;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.*;
import java.util.*;

public final class NexusNpc implements ModInitializer {
 public static final String VERSION="0.1.0-PREVIEW1";
 private static final Map<UUID,Long> UPLOAD_TIMES=new HashMap<>();
 public static boolean admin(class_2168 source){return class_2170.field_31841.method_75022(source.method_75037());}
 public static void feedback(class_2168 s,String text){s.method_9226(()->class_2561.method_43470(text),false);}
 public void onInitialize(){
  PayloadTypeRegistry.playC2S().register(UploadPreset.ID,UploadPreset.CODEC);
  ServerPlayNetworking.registerGlobalReceiver(UploadPreset.ID,(payload,ctx)->ctx.server().execute(()->{
   class_2168 source=ctx.player().method_64396();
   if(!admin(source)){source.method_9213(class_2561.method_43470("Publication de presets réservée aux administrateurs OP 4"));return;}
   long now=System.currentTimeMillis(),last=UPLOAD_TIMES.getOrDefault(ctx.player().method_5667(),0L);
   if(now-last<1000){source.method_9213(class_2561.method_43470("Attendre une seconde entre deux publications"));return;}
   UPLOAD_TIMES.put(ctx.player().method_5667(),now);
   try{NpcPreset p=NpcPreset.parse(payload.json());PresetStore.saveNew(p);feedback(source,"Preset PNJ publié : "+p.id());}
   catch(Exception e){source.method_9213(class_2561.method_43470("Preset refusé : "+e.getMessage()));}
  }));
  CommandRegistrationCallback.EVENT.register((dispatcher,access,environment)->dispatcher.register(class_2170.method_9247("nexusnpc").requires(NexusNpc::admin)
   .then(class_2170.method_9247("list").executes(c->execute(c.getSource(),()->{feedback(c.getSource(),String.join(", ",PresetStore.list()));return 1;})))
   .then(class_2170.method_9247("apply").then(class_2170.method_9244("npc",class_2186.method_9309()).then(class_2170.method_9244("preset",StringArgumentType.word()).suggests((c,b)->class_2172.method_9265(suggestions(),b)).executes(c->execute(c.getSource(),()->{class_1297 entity=class_2186.method_9313(c,"npc");NpcAppearance.apply(entity,PresetStore.load(StringArgumentType.getString(c,"preset")));feedback(c.getSource(),"Apparence Nexus appliquée, UUID conservé : "+entity.method_5667());return 1;})))))
   .then(class_2170.method_9247("reset").then(class_2170.method_9244("npc",class_2186.method_9309()).executes(c->execute(c.getSource(),()->{boolean changed=NpcAppearance.reset(class_2186.method_9313(c,"npc"));feedback(c.getSource(),changed?"Apparence précédente restaurée":"Ce PNJ n’a pas de preset Nexus");return changed?1:0;}))))
   .then(class_2170.method_9247("spawn").then(class_2170.method_9244("preset",StringArgumentType.word()).suggests((c,b)->class_2172.method_9265(suggestions(),b)).executes(c->execute(c.getSource(),()->{spawn(c.getSource(),PresetStore.load(StringArgumentType.getString(c,"preset")));return 1;}))))));
 }
 public static List<String> suggestions(){try{return PresetStore.list();}catch(Exception e){return PresetStore.BUILTIN;}}
 public interface Operation{int run()throws Exception;}
 public static int execute(class_2168 s,Operation op){try{return op.run();}catch(Exception e){s.method_9213(class_2561.method_43470("Nexus PNJ : "+e.getMessage()));return 0;}}
 public static class_1297 spawn(class_2168 source,NpcPreset p)throws Exception{
  String id=p.slim()?"easy_npc:humanoid_slim":"easy_npc:humanoid";class_1299<?> type=null;
  for(class_1299<?> candidate:class_7923.field_41177)if(class_1299.method_5890(candidate).toString().equals(id)){type=candidate;break;}
  if(type==null)throw new IllegalStateException("Type EasyNPC absent");
  class_1297 entity=type.method_5883(source.method_9225(),class_3730.field_16460);
  if(entity==null)throw new IllegalStateException("Création du PNJ refusée");
  var pos=source.method_9222();entity.method_5808(pos.field_1352,pos.field_1351,pos.field_1350,source.method_9210().field_1342,0);
  NpcAppearance.apply(entity,p);entity.method_5665(class_2561.method_43470(p.name()));entity.method_5880(true);
  if(!source.method_9225().method_8649(entity))throw new IllegalStateException("Le monde a refusé le PNJ");
  feedback(source,"PNJ "+p.id()+" créé : "+entity.method_5667());return entity;
 }
}
