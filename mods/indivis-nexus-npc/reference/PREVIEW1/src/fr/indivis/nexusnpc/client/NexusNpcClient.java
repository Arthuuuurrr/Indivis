package fr.indivis.nexusnpc.client;
import fr.indivis.nexusnpc.*;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.*;

public final class NexusNpcClient implements ClientModInitializer {
 @SuppressWarnings("unchecked") public void onInitializeClient(){
  LivingEntityFeatureRendererRegistrationCallback.EVENT.register((type,renderer,helper,context)->{
   if(renderer instanceof de.markusbordihn.easynpc.client.renderer.entity.standard.PlayerRenderer){
    helper.register(new NpcCosmeticLayer((class_3883<class_10055,class_591>)(Object)renderer));
   }
  });
  ClientCommandRegistrationCallback.EVENT.register((d,access)->d.register(ClientCommandManager.literal("nexusnpc_local")
   .then(ClientCommandManager.literal("editor").then(ClientCommandManager.argument("id",StringArgumentType.word()).executes(c->{try{NpcEditor.open(StringArgumentType.getString(c,"id"));return 1;}catch(Exception e){c.getSource().sendError(class_2561.method_43470(e.getMessage()));return 0;}})))
   .then(ClientCommandManager.literal("push").then(ClientCommandManager.argument("id",StringArgumentType.word()).executes(c->{try{
    if(!ClientPlayNetworking.canSend(UploadPreset.ID))throw new IllegalStateException("Module Indivis Nexus PNJ absent du serveur");
    ClientPlayNetworking.send(new UploadPreset(PresetStore.loadLocal(StringArgumentType.getString(c,"id")).json()));c.getSource().sendFeedback(class_2561.method_43470("Preset envoyé ; attendre la confirmation du serveur."));return 1;
   }catch(Exception e){c.getSource().sendError(class_2561.method_43470(e.getMessage()));return 0;}})))));
 }
}
