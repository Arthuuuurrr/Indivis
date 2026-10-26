package fr.indivis.nexusnpc.client;
import fr.indivis.nexusnpc.*;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.data.skin.SkinDataEntry;
import de.markusbordihn.easynpc.client.renderer.entity.state.EasyNPCRenderStateExtension;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;
import java.util.*;

public final class NpcClientBridge {
 private record Plan(NpcPreset preset,class_8685 skin,int tone){}
 private static final Map<SkinDataEntry,NpcPreset> DECODED=new WeakHashMap<>();
 private static final Set<SkinDataEntry> INVALID=Collections.newSetFromMap(new WeakHashMap<>());
 private static final Map<NpcPreset,Plan> PLANS=new LinkedHashMap<>(32,.75f,true){protected boolean removeEldestEntry(Map.Entry<NpcPreset,Plan> e){return size()>128;}};
 public static long extracts,preparations,cacheHits,cosmeticDraws;
 private NpcClientBridge(){}
 public static void clear(){DECODED.clear();INVALID.clear();PLANS.clear();}
 public static void extract(class_1314 entity,class_10055 state){
  ((NpcRenderState)state).indivis$setNexusPreset(false);
  CosmeticRenderStateAccess cosmetics=(CosmeticRenderStateAccess)state;
  cosmetics.nexuscharacters$setEarStyle("none");cosmetics.nexuscharacters$setBeardStyle("none");
  MorphRenderStateAccess morph=(MorphRenderStateAccess)state;morph.nexuscharacters$setBuildScale(1);morph.nexuscharacters$setHeightScale(1);
  DwarfNoseRenderStateAccess nose=(DwarfNoseRenderStateAccess)state;nose.nexuscharacters$setDwarf(false);
  if(!(entity instanceof EasyNPC<?> npc)||!NpcAppearance.supported(entity))return;
  SkinDataEntry data=npc.getEasyNPCSkinData().getSkinDataEntry();
  if(data==null||data.content()==null||!data.content().startsWith(NpcAppearance.PREFIX)||INVALID.contains(data))return;
  try{
   NpcPreset preset=DECODED.get(data);
   if(preset==null){var saved=NpcAppearance.read(data);if(saved==null)return;preset=saved.preset();DECODED.put(data,preset);}
   Plan plan=PLANS.get(preset);
   if(plan==null){class_8685 skin=PreviewDummyPlayerManager.skin(preset.dto());if(skin==null)return;plan=new Plan(preset,skin,SkinToneSupport.toneForSkinTextures(skin));PLANS.put(preset,plan);preparations++;}else cacheHits++;
   state.field_53520=plan.skin();
   ((EasyNPCRenderStateExtension)state).setEasyNpcTexture(plan.skin().comp_1626().comp_3627());
   morph.nexuscharacters$setBuildScale(preset.build());morph.nexuscharacters$setHeightScale(preset.height());
   cosmetics.nexuscharacters$setEarStyle(preset.ears());cosmetics.nexuscharacters$setBeardStyle(preset.beard());
   nose.nexuscharacters$setDwarf(preset.race().equals("dwarf"));nose.nexuscharacters$setNoseTone(plan.tone());
   ((NpcRenderState)state).indivis$setNexusPreset(true);extracts++;
  }catch(Exception e){INVALID.add(data);System.err.println("[Indivis Nexus PNJ] Recette refusée : "+e);}
 }
}
