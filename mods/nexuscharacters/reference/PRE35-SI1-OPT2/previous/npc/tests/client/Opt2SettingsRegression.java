import java.lang.reflect.*;
import java.util.*;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.client.renderer.entity.standard.PlayerRenderer;
import fr.indivis.nexusnpc.NpcAppearance;

/** A cached Class must never freeze the mutable Skin Layers config values. */
final class Opt2SettingsRegression {
    static void run(class_310 client)throws Exception {
        class_1314 target=null;
        for(var e:client.field_1687.method_18112())if(e instanceof class_1314 mob&&e instanceof EasyNPC<?> npc
            &&NpcAppearance.read(npc.getEasyNPCSkinData().getSkinDataEntry())!=null){target=mob;break;}
        NpcIntegrationTest.check(target!=null,"No live model for settings test");
        var renderer=(PlayerRenderer)client.method_1561().method_3953(target);
        Object model=renderer.method_4038();
        Class<?> joint=Class.forName("net.tompsen.nexuscharacters.LowerJointSupport");
        Method apply=joint.getMethod("apply",Object.class,boolean.class);
        Object config=SurfaceGeometry.field(Class.forName("dev.tr7zw.skinlayers.SkinLayersModBase"),"config");
        Field width=config.getClass().getField("bodyVoxelWidthSize");
        Object old=width.get(config),part=SurfaceGeometry.field(model,"field_3397");
        try {
            width.set(config,1.05f);apply.invoke(null,model,true);Object first=SurfaceGeometry.field(part,"field_3663");
            width.set(config,1.15f);apply.invoke(null,model,true);Object second=SurfaceGeometry.field(part,"field_3663");
            width.set(config,1.05f);apply.invoke(null,model,true);Object restored=SurfaceGeometry.field(part,"field_3663");
            NpcIntegrationTest.check(first!=second,"Class cache froze bodyVoxelWidthSize");
            NpcIntegrationTest.check(first==restored,"Settings roundtrip did not reuse the correct variant");
        } finally {width.set(config,old);apply.invoke(null,model,false);NpcClientClear();}
        NpcIntegrationTest.results.add("OPT2_LIVE_CONFIG_PASS bodyVoxelWidthSize 1.05 -> 1.15 -> 1.05 on actual EasyNPC model");
    }
    static void NpcClientClear(){fr.indivis.nexusnpc.client.NpcClientBridge.clear();GenericSkinLayerSupport.clearCaches();}
}
