package pre30test;
import java.util.*;
import net.minecraft.class_4587;
import net.minecraft.class_630;
import net.tompsen.nexuscharacters.*;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Test-only trace of PRE31 invalidations, attached exclusively to the world harness. */
@Mixin(value=PosedSurfaceSupport.class,remap=false)
public abstract class HandTraceMixin {
    private static int traces;
    @Inject(method="firstPerson(Ljava/lang/Object;[Ljava/lang/Object;Z)V",at=@At("HEAD"),require=1,remap=false)
    private static void trace(Object model,Object[] cooked,boolean slim,CallbackInfo ci) throws Exception {
        if(traces>=12)return;
        Object state=((Map<?,?>)SurfaceGeometry.field(PosedSurfaceSupport.class,"STATES")).get(model);
        List<String> reasons=new ArrayList<>();
        if(state==null)reasons.add("missing-world-state");
        else {
            String[] names={"field_3394","field_3483","field_3482","field_3479","field_3484","field_3486"};
            Object[] previous=(Object[])SurfaceGeometry.field(state,"template");
            Matrix4f[] relative=(Matrix4f[])SurfaceGeometry.field(state,"relatives");
            for(int p=0;p<6;p++) {
                if(previous==null||previous[p]!=cooked[slim&&p>=4?p+2:p])reasons.add("template:"+p);
                class_630 up=(class_630)SurfaceGeometry.field(model,names[p]);
                class_4587 stack=new class_4587();up.method_22703(stack);
                if(relative==null||!relative[p].equals(stack.method_23760().method_23761()))
                    reasons.add("relative:"+p+"/rot="+up.field_3654+","+up.field_3675+","+up.field_3674);
            }
        }
        if(!reasons.isEmpty())System.out.println("PRE32_BASELINE_HAND_INVALIDATION "+(++traces)+" model="+System.identityHashCode(model)+" "+reasons);
    }
}
