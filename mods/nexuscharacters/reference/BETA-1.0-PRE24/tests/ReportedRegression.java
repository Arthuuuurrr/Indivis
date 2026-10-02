import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;

/**
 * Exercises the original Nexus menu preview, both worldless widget models and world preview
 * entities.
 */
public class ReportedRegression implements ClientModInitializer {
  static int ticks, wt, overlaps, captures;
  static boolean world;
  static final String[] ids = {
    "player_v69_b1_e1_ec0_ey2_h06_hc02_s02_o01_fh0_fc00_mk0_mc0",
    "player_v69_b1_e1_ec0_ey2_h07_hc02_s02_o01_fh0_fc00_mk0_mc0",
    "player_v69_b1_e1_ec0_ey2_h08_hc02_s05_o01_fh1_fc00_mk0_mc0",
    "player_v69_b1_e1_ec0_ey2_h08_hc02_s05_o01_fh2_fc00_mk0_mc0"
  };

  static CharacterDto dto(int i) {
    return new CharacterDto(
        UUID.fromString("00000000-0000-0000-0000-000000000002"),
        "Reported",
        null,
        null,
        "__capitale_preset__:" + ids[i],
        0,
        false,
        "human",
        1f,
        1f,
        i < 2 ? "long" : "none",
        "none");
  }

  public void onInitializeClient() {
    EvidenceLog.install();
    ClientTickEvents.END_CLIENT_TICK.register(
        c -> {
          try {
            if (ticks == 0)
              dev.tr7zw.skinlayers.SkinLayersModBase.config.irisCompatibilityMode =
                  Boolean.getBoolean("compatibilityTest");
            if (c.method_18506() != null) return;
            if (world && c.field_1724 == null) return;
            int t = world ? ++wt : ++ticks;
            if (t == 20) c.method_1507(new Scene());
            if (t >= 20 && t < 260) {
              int i = (t - 20) / 60, phase = (t - 20) % 60;
              if (phase == 10 || phase == 30 || phase == 50) {
                int angle = phase / 20;
                class_318.method_1663(
                    c.method_1522(),
                    im -> {
                      try {
                        im.method_4314(
                            Path.of(
                                "pre24/evidence/preview/"
                                    + (world ? "preview-world" : "preview-widget")
                                    + "-"
                                    + i
                                    + "-"
                                    + angle
                                    + ".png"));
                      } catch (Exception e) {
                        throw new RuntimeException(e);
                      } finally {
                        im.close();
                      }
                    });
                captures++;
                System.out.println(
                    "REPORTED_CAPTURE world="
                        + world
                        + " case="
                        + i
                        + " angle="
                        + angle
                        + " drawnOuter="
                        + GenericSkinLayerSupport.drawnOuter);
              }
              if (phase == 20) {
                if (world) {
                  var dummy = PreviewDummyPlayerManager.get(dto(i));
                  var renderer = c.method_1561().method_74405(dummy);
                  class_10055 state = renderer.method_62608();
                  renderer.method_62604(dummy, state, 0);
                  var model = renderer.method_4038();
                  model.method_62110(state);
                  int[] result = PoseAudit.run(model);
                  overlaps += result[0] + result[1];
                } else {
                  Object widget =
                      SubmittedGeometryAudit.field(CharacterPreviewRenderer.class, "skinWidget");
                  for (String name : new String[] {"field_59834", "field_59835"}) {
                    Object model = SubmittedGeometryAudit.field(widget, name);
                    int[] result = PoseAudit.run(model);
                    overlaps += result[0] + result[1];
                  }
                }
              }
            }
            if (t == 260) {
              if (world) {
                if (overlaps != 0 || captures != 24)
                  throw new AssertionError(
                      "Preview failed overlaps=" + overlaps + " captures=" + captures);
                System.out.println(
                    "REPORTED_PREVIEW_PASS captures="
                        + captures
                        + " overlaps="
                        + overlaps
                        + " wide+slim=true");
                System.out.println("ACTUAL_PLAYER_REGRESSION_PASS reported menu previews");
                c.method_1490();
                return;
              }
              world = true;
              class_7712 config = class_7712.field_40260;
              class_1940 settings =
                  new class_1940(
                      "Reported regression",
                      class_1934.field_9220,
                      false,
                      class_1267.field_5801,
                      true,
                      new class_1928(config.comp_1011()),
                      config);
              System.out.println("ACTUAL_START_WORLD");
              c.method_41735()
                  .method_41895(
                      "reported-regression",
                      settings,
                      new class_5285(1, false, false),
                      class_5317::method_64225,
                      null);
            }
          } catch (Throwable e) {
            System.err.println("REPORTED_REGRESSION_FAILURE " + e);
            e.printStackTrace();
            c.method_1490();
          }
        });
  }

  static class Scene extends class_437 {
    Scene() {
      super(class_2561.method_43470("Reported regressions"));
    }

    public void method_25394(class_332 d, int mx, int my, float delta) {
      try {
        d.method_25294(0, 0, field_22789, field_22790, 0xffcccccc);
        int t = world ? wt : ticks;
        int i = Math.min(3, Math.max(0, (t - 20) / 60)),
            angle = Math.min(2, Math.max(0, ((t - 20) % 60) / 20));
        float yaw = new float[] {30, 90, 180}[angle];
        Field y = PreviewDragInput.class.getDeclaredField("yawDegrees");
        y.setAccessible(true);
        y.setFloat(null, yaw);
        Object widget = SubmittedGeometryAudit.field(CharacterPreviewRenderer.class, "skinWidget");
        if (widget != null) {
          Field r = widget.getClass().getDeclaredField("field_46006");
          r.setAccessible(true);
          r.setFloat(widget, yaw);
        }
        CharacterPreviewRenderer.draw(
            d, dto(i), field_22789 / 2, 15, field_22790 - 15, field_22789 / 2, field_22790 / 2);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
