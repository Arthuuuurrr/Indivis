package fr.indivis.nexusnpc.mixin;
import fr.indivis.nexusnpc.client.NpcEditor;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(targets="net.tompsen.nexuscharacters.IndivisMenus",remap=false)
public class EditorTitleMixin {
 @ModifyArg(method="render",at=@At(value="INVOKE",target="Lnet/tompsen/nexuscharacters/IndivisTheme;title(Lnet/minecraft/class_332;Lnet/minecraft/class_327;ILjava/lang/String;)V"),index=3,remap=false)
 private static String indivis$title(String original){return NpcEditor.active(class_310.method_1551().field_1755)?"Créer un preset de PNJ":original;}
}
