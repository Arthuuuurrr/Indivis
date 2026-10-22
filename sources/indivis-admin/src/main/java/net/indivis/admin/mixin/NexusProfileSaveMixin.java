package net.indivis.admin.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Compatibilité PRE34 : ne remplace ni la sauvegarde, ni ses données, ni ses erreurs. */
@Pseudo
@Mixin(targets="net.tompsen.nexuscharacters.ProfileStateBridge",remap=false)
public abstract class NexusProfileSaveMixin {
  @ModifyVariable(method="runHookIfCorePresent(Lnet/minecraft/class_3222;Lnet/minecraft/class_269;Ljava/lang/String;)V",at=@At("HEAD"),argsOnly=true,ordinal=0,remap=false)
  private static String indivisBeforeSaveHook(String command){
    return "function capitale:profiles/before_save_self".equals(command)?"function indivis_admin:profile/before_save_self":command;
  }
}
