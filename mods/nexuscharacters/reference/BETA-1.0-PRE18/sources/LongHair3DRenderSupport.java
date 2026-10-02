package net.tompsen.nexuscharacters;

import java.util.Locale;

/** Stable ABI for older compositor/feature hooks; PRE18 renders all layers in the main model. */
public final class LongHair3DRenderSupport {
    private LongHair3DRenderSupport() {}
    public static String hairAssetName(int hairIndex) {
        return hairIndex<=5 ? String.format(Locale.ROOT,"hair_short_%02d.png",hairIndex)
                : String.format(Locale.ROOT,"hair_long_%02d.png",hairIndex-5);
    }
    public static void render(Object renderer,Object matrices,Object queue,int light,Object state) {}
    public static Object[] previewParts(int hairIndex){return null;}
}
