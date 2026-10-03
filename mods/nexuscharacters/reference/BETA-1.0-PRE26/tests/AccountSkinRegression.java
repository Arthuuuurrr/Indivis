import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;
import net.minecraft.*;
import net.tompsen.nexuscharacters.*;

public final class AccountSkinRegression {
  static class_8685 expected;
  static Field activeCache;
  static Object previous;

  static CharacterDto begin(class_310 c) throws Exception {
    activeCache = class_640.class.getDeclaredField("field_45607");
    activeCache.setAccessible(true);
    class_640 entry = c.method_1562().method_2871(c.field_1724.method_5667());
    previous = activeCache.get(entry);
    activeCache.set(entry, (Supplier<class_8685>) () -> expected);
    return new CharacterDto(
        c.field_1724.method_5667(),
        "Account fixture",
        "stale",
        null,
        "@nexus:account",
        0,
        false,
        "human",
        1,
        1,
        "none",
        "none");
  }

  static void finish(class_310 c) throws Exception {
    activeCache.set(c.method_1562().method_2871(c.field_1724.method_5667()), previous);
  }

  static void run(class_310 c) throws Exception {
    UUID player = c.field_1724.method_5667();
    class_640 entry = c.method_1562().method_2871(player);
    if (entry == null) throw new AssertionError("Missing real player-list entry");
    Field cache = class_640.class.getDeclaredField("field_45607");
    cache.setAccessible(true);
    Object original = cache.get(entry);
    Object selected = SubmittedGeometryAudit.field(NexusCharacters.class, "selectedCharacter");
    expected =
        new class_8685(
            new class_12079.class_12080(
                class_2960.method_60655("preview_harness", "account_skin.png"), null),
            null,
            null,
            class_7920.field_41122,
            true);
    try {
      cache.set(entry, (Supplier<class_8685>) () -> expected);
      CharacterDto dto =
          new CharacterDto(
              UUID.randomUUID(),
              "Preview account",
              "stale-texture-must-not-replace-account",
              null,
              "@nexus:account",
              0,
              false,
              "human",
              1,
              1,
              "none",
              "none");
      if (!expected.equals(PreviewDummyPlayerManager.skin(dto)))
        throw new AssertionError("Preview replaces loaded account skin");
      CharacterDto local =
          new CharacterDto(
              player,
              "Account",
              "stale-texture-must-not-replace-account",
              null,
              "@nexus:account",
              0,
              false,
              "human",
              1,
              1,
              "none",
              "none");
      Field chosen = NexusCharacters.class.getDeclaredField("selectedCharacter");
      chosen.setAccessible(true);
      chosen.set(null, local);
      if (!expected.equals(c.field_1724.method_52814()))
        throw new AssertionError("Actual local player replaces account skin");
      var renderer = c.method_1561().method_74405(c.field_1724);
      class_10055 state = renderer.method_62608();
      renderer.method_62604(c.field_1724, state, 0);
      if (!expected.equals(state.field_53520))
        throw new AssertionError("Render state replaces account skin");
      cache.set(entry, (Supplier<class_8685>) () -> class_1068.method_4648(player));
      if (!expected.equals(PreviewDummyPlayerManager.skin(dto)))
        throw new AssertionError("Async default evicts last loaded account skin");
      Method mode = AccountSkinSupport.class.getMethod("usesAccount", String.class, UUID.class);
      if (Boolean.TRUE.equals(mode.invoke(null, "__capitale_preset__:anything", player)))
        throw new AssertionError("Preset mistaken for account mode");
      System.out.println(
          "ACCOUNT_SKIN_PASS"
              + " cached-network+preview+actual-player+render-state+temporary-default+stale-snapshot");
    } finally {
      cache.set(entry, original);
      Field chosen = NexusCharacters.class.getDeclaredField("selectedCharacter");
      chosen.setAccessible(true);
      chosen.set(null, selected);
    }
  }
}

