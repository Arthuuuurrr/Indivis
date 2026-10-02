import java.lang.reflect.*;
import java.util.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;

/** Checks every discovered cosmetic with the real Minecraft model and three posed transforms. */
public class CatalogRegression implements ClientModInitializer {
  static int ticks;
  static boolean started;

  public void onInitializeClient() {
    ClientTickEvents.END_CLIENT_TICK.register(
        c -> {
          try {
            if (!started) {
              if (c.method_18506() != null || ++ticks < 20) return;
              started = true;
              class_7712 config = class_7712.field_40260;
              class_1940 settings =
                  new class_1940(
                      "Catalog validation",
                      class_1934.field_9220,
                      false,
                      class_1267.field_5801,
                      true,
                      new class_1928(config.comp_1011()),
                      config);
              System.out.println("ACTUAL_START_WORLD catalog");
              c.method_41735()
                  .method_41895(
                      "pre23-catalog",
                      settings,
                      new class_5285(1, false, false),
                      class_5317::method_64225,
                      null);
              return;
            }
            if (c.field_1724 == null || c.field_1687 == null) return;
            Field selected = NexusCharacters.class.getDeclaredField("selectedCharacter");
            selected.setAccessible(true);
            Object previous = selected.get(null);
            List<int[]> configs = new ArrayList<>();
            Method outfits = DynamicAssetCatalog.class.getDeclaredMethod("outfitIds");
            outfits.setAccessible(true);
            for (int o : (int[]) outfits.invoke(null)) configs.add(new int[] {6, 5, o});
            Method hair = DynamicAssetCatalog.class.getDeclaredMethod("hairs");
            hair.setAccessible(true);
            for (Object h : ((Map<?, ?>) hair.invoke(null)).keySet())
              configs.add(new int[] {(Integer) h, 0, 23});
            Method beards = DynamicAssetCatalog.class.getDeclaredMethod("facials");
            beards.setAccessible(true);
            for (Object b : ((Map<?, ?>) beards.invoke(null)).keySet())
              configs.add(new int[] {9, (Integer) b, 20});
            int poses = 0;
            for (int[] config : configs) {
              if (System.getenv("CATALOG_ONLY_OUTFIT") != null
                  && config[2] != Integer.parseInt(System.getenv("CATALOG_ONLY_OUTFIT"))) continue;
              String id =
                  String.format(
                      Locale.ROOT,
                      "player_v69_b1_e1_ec0_ey2_h%02d_hc02_s02_o%02d_fh%d_fc00_mk0_mc0",
                      config[0],
                      config[2],
                      config[1]);
              CharacterDto dto =
                  new CharacterDto(
                      c.field_1724.method_5667(),
                      "Catalog",
                      null,
                      null,
                      "__capitale_preset__:" + id,
                      0,
                      false,
                      "human",
                      1,
                      1,
                      "none",
                      "none");
              selected.set(null, dto);
              var renderer = c.method_1561().method_74405(c.field_1724);
              var model = renderer.method_4038();
              for (int phase : new int[] {0, 15, 35}) {
                class_10055 state = renderer.method_62608();
                renderer.method_62604(c.field_1724, state, 0);
                ActualPlayerRegression.pose(state, phase);
                model.method_62110(state);
                int[] hits = PoseAudit.run(model);
                if (hits[0] + hits[1] != 0)
                  throw new AssertionError(
                      "Catalog geometry "
                          + id
                          + " phase="
                          + phase
                          + " overlaps="
                          + Arrays.toString(hits));
                poses++;
              }
              System.out.println("CATALOG_CASE_PASS " + id);
            }
            selected.set(null, previous);
            System.out.println(
                "CATALOG_RENDERER_PASS configurations=" + configs.size() + " poses=" + poses);
            System.out.println("ACTUAL_PLAYER_REGRESSION_PASS catalog");
            c.method_1490();
          } catch (Throwable e) {
            System.err.println("CATALOG_REGRESSION_FAILURE " + e);
            e.printStackTrace();
            c.method_1490();
          }
        });
  }
}
