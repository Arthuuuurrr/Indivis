package net.tompsen.nexuscharacters;
import net.minecraft.*;
import org.joml.Vector3f;
import org.joml.Quaternionf;

/** Shared feature submission for real players, NPCs and synthetic GUI states. */
public final class BlockbenchRenderSupport {
    public static long submissions;
    private static final boolean DIAGNOSTICS=Boolean.getBoolean("nexus.blockbench.diagnostics");
    public static long previewRenders;
    private static final java.util.Map<String,Long> rendered=DIAGNOSTICS?new java.util.HashMap<>():null;
    public static long renderCount(String id){return rendered==null?0:rendered.getOrDefault(id,0L);}
    public static final class PreviewState extends class_10055 {
        final CharacterDto dto;
        PreviewState(CharacterDto dto){this.dto=dto;}
    }
    private BlockbenchRenderSupport() {}
    static Appearance69Support.Params params(class_10055 state) {
        if(state instanceof PreviewState preview)return BlockbenchPresetCatalog.params(preview.dto.skinUsername());
        if(state.field_53520==null)return null;
        String path=state.field_53520.comp_1626().comp_3627().method_12832();
        return BlockbenchPresetCatalog.params(path);
    }
    public static void render(class_591 model,class_4587 matrices,class_11659 queue,int light,class_10055 state) {
        if(state.field_53333||state.field_53520==null)return;
        Appearance69Support.Params params=params(state);
        CosmeticRenderStateAccess styles=(CosmeticRenderStateAccess)state;
        String ear=PreviewCosmeticResolver.ear(styles),beard=PreviewCosmeticResolver.beard(styles);
        BlockbenchPresetCatalog.Entry hair=params==null?null:BlockbenchPresetCatalog.hair(params.hair());
        BlockbenchPresetCatalog.Entry ears=BlockbenchPresetCatalog.entry("oreilles",ear);
        BlockbenchPresetCatalog.Entry facial=BlockbenchPresetCatalog.entry("barbes",beard);
        if(hair==null&&ears==null&&facial==null)return;
        matrices.method_22903();model.field_3398.method_22703(matrices);
        try {
            if(hair!=null)submit(hair,params.hairColor()==0?0:Appearance69Support.hairColorArgb(params.hairColor()),matrices,queue,light,state);
            if(ears!=null)submit(ears,SkinToneSupport.toneForSkinTextures(state.field_53520),matrices,queue,light,state);
            if(facial!=null&&!facial.aliasNone())submit(facial,CosmeticColorSupport.colorArgb(beard),matrices,queue,light,state);
        }finally{matrices.method_22909();}
    }
    private static void submit(BlockbenchPresetCatalog.Entry entry,int tint,class_4587 matrices,class_11659 queue,int light,class_10055 state) {
        class_630 part=BlockbenchModelBaker.model(entry);class_2960 texture=BlockbenchTextureCache.texture(entry,tint);
        queue.method_73492(part,matrices,class_12249.method_75982(texture),light,class_922.method_23622(state,0f),null,-1,null);submissions++;
        if(DIAGNOSTICS)rendered.merge(entry.id(),1L,Long::sum);
    }
    /** Keep one real entity-render pass for body, hair, ears and beard, so the
     * GUI shares depth and does not paint a back strand over a face or torso. */
    public static void renderWidget(class_8765 widget,class_332 draw,int mouseX,int mouseY,float delta,CharacterDto dto) {
        if(DIAGNOSTICS)previewRenders++;
        if(!BlockbenchPresetCatalog.hasCustom(dto)){widget.method_25394(draw,mouseX,mouseY,delta);return;}
        PreviewState state=new PreviewState(dto);state.field_53520=PreviewDummyPlayerManager.skin(dto);
        state.field_53329=.6f;state.field_53330=1.8f;state.field_53331=1.62f;
        state.field_53542=false; // spectator states deliberately suppress features
        state.field_53543=state.field_53544=state.field_53545=state.field_53546=true;
        state.field_61820=0x00f000f0;
        ((MorphRenderStateAccess)(Object)state).nexuscharacters$setBuildScale(dto.characterRace().clampBuild(dto.buildScale()));
        ((MorphRenderStateAccess)(Object)state).nexuscharacters$setHeightScale(dto.characterRace().clampHeight(dto.heightScale()));
        DwarfNoseRenderStateAccess nose=(DwarfNoseRenderStateAccess)(Object)state;
        nose.nexuscharacters$setDwarf(dto.characterRace()==CharacterRace.DWARF);
        nose.nexuscharacters$setNoseTone(SkinToneSupport.toneForSkinTextures(state.field_53520));
        CosmeticRenderStateAccess styles=(CosmeticRenderStateAccess)(Object)state;
        styles.nexuscharacters$setEarStyle(CharacterCosmetics.normalizeEar(dto.characterRace(),dto.earStyle()));
        styles.nexuscharacters$setBeardStyle(CharacterCosmetics.normalizeBeard(dto.characterRace(),dto.beardStyle()));
        float yaw=210f+PreviewDragInput.getYawDegrees();
        float scale=.97f*widget.method_25364()/2.125f/dto.characterRace().clampHeight(dto.heightScale());
        Quaternionf rotation=new Quaternionf().rotationZ((float)Math.PI).rotateY((float)Math.toRadians(yaw));
        draw.method_70856(state,scale,new Vector3f(0,1.0625f,0),rotation,null,widget.method_46426(),widget.method_46427(),widget.method_55442(),widget.method_55443());
    }
}
