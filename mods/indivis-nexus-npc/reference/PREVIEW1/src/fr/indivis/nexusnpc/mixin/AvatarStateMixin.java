package fr.indivis.nexusnpc.mixin;
import fr.indivis.nexusnpc.client.NpcRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
@Mixin(targets="net.minecraft.class_10055",remap=false)
public class AvatarStateMixin implements NpcRenderState {
 @Unique private boolean indivis$nexusNpc;
 public boolean indivis$hasNexusPreset(){return indivis$nexusNpc;}
 public void indivis$setNexusPreset(boolean value){indivis$nexusNpc=value;}
}
