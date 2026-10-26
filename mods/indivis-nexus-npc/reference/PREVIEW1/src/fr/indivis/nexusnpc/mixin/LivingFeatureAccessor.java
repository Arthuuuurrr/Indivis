package fr.indivis.nexusnpc.mixin;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(value=class_922.class,remap=false)
public interface LivingFeatureAccessor {
 @Invoker("method_4046") boolean indivis$addFeature(class_3887<?,?> layer);
}
