package net.tompsen.nexuscharacters;
import java.util.*;

/** Narrow entry hooks; unmodified OPT3 bodies remain available for old IDs. */
public final class BlockbenchHooks {
    private BlockbenchHooks() {}
    public static String normalizeEar(CharacterRace race,String style) {
        return BlockbenchPresetCatalog.custom("oreilles",style)?BlockbenchPresetCatalog.normalizeEar(race,style):CharacterCosmetics.normalizeEarOPT3(race,style);
    }
    public static boolean styleAllowed(Object race,String style) {
        return BlockbenchPresetCatalog.custom("barbes",style)?BlockbenchPresetCatalog.beardAllowed(race,style):CulturalBeardSupport.styleAllowedOPT3(race,style);
    }
    public static String styleLabel(String style) {
        return BlockbenchPresetCatalog.custom("barbes",style)?BlockbenchPresetCatalog.label("barbes",style):CulturalBeardSupport.styleLabelOPT3(style);
    }
    public static boolean ornamentAllowed(Object race,String style,String ornament) {
        return BlockbenchPresetCatalog.custom("barbes",style)?"none".equals(CulturalBeardSupport.canonicalOrnament(ornament)):CulturalBeardSupport.ornamentAllowedOPT3(race,style,ornament);
    }
    public static String normalizeEarPreview(String style) {
        return BlockbenchPresetCatalog.custom("oreilles",style)?BlockbenchPresetCatalog.base(style):EarPreviewSelectionBridge.normalizeOPT3(style);
    }
    public static void cycleCosmetic(Object screen) {
        if(!BlockbenchPresetCatalog.cycleEar(screen))CulturalBeardUiSupport.cycleCosmeticOPT3(screen);
    }
    public static void refreshCosmetic(Object screen) {
        if(!BlockbenchPresetCatalog.refreshEar(screen))CulturalBeardUiSupport.refreshCosmeticButtonOPT3(screen);
    }
    public static int normalizeHair(int id){return BlockbenchPresetCatalog.hair(id)!=null?id:DynamicAssetCatalog.normalizeHairOPT3(id);}
    public static String hairAssetName(int id){return BlockbenchPresetCatalog.hair(id)!=null?"hair_"+id+".png":DynamicAssetCatalog.hairAssetNameOPT3(id);}
    public static void previewEars(boolean front,Object draw,Object dto,Object widget,int mx,int my,float delta) {
        if(dto instanceof CharacterDto character&&BlockbenchPresetCatalog.hasCustom(character))return;
        WorldlessEarWidgetRenderer.renderPhaseOPT3(front,draw,dto,widget,mx,my,delta);
    }
    public static void previewBeard(Object draw,Object dto,Object widget,int x,int y,int w,int h,int mx,int my,float delta) {
        if(dto instanceof CharacterDto character&&BlockbenchPresetCatalog.hasCustom(character))return;
        BeardPreviewVisibilitySupport.renderOPT3(draw,dto,widget,x,y,w,h,mx,my,delta);
    }
    public static void clear() {
        BlockbenchPresetCatalog.clearHairChoices();
        // The shared catalogue is used by dedicated servers too. Client-only
        // classes are never linked or initialised on a server.
        if(net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType()==net.fabricmc.api.EnvType.CLIENT)
            BlockbenchClient.clear();
    }
}
