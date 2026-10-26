package fr.indivis.nexusnpc.client;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;
/** Use the actual Nexus cosmetic renderers, not a reconstruction of their geometry. */
public final class NpcCosmeticLayer extends class_3887<class_10055,class_591> {
 public static long registered,calls;
 private final CharacterCosmeticFeatureRenderer cosmetics;
 private final DwarfNoseFeatureRenderer nose;
 public NpcCosmeticLayer(class_3883<class_10055,class_591> renderer){super(renderer);cosmetics=new CharacterCosmeticFeatureRenderer(renderer);nose=new DwarfNoseFeatureRenderer(renderer);registered++;}
 @Override public void method_4199(class_4587 matrices,class_11659 queue,int light,class_10055 state,float limb,float angle){
  calls++;
  if(!((NpcRenderState)state).indivis$hasNexusPreset()||state.field_53333)return;
  cosmetics.render(matrices,queue,light,state,limb,angle);nose.render(matrices,queue,light,state);NpcClientBridge.cosmeticDraws++;
 }
}
