package net.tompsen.nexuscharacters;

import com.mojang.authlib.GameProfile;
import java.util.*;
import java.util.function.Supplier;
import net.minecraft.*;

/** Resolves the account's unmodified vanilla skin without recursing through Nexus overrides. */
public final class AccountSkinSupport {
  private static UUID account;
  private static Supplier<class_8685> loading;
  private static class_8685 lastLoaded;

  public static boolean usesAccount(String username, UUID player) {
    if (username != null && username.startsWith("__capitale_preset__:")) return false;
    if (username == null || username.isBlank() || username.equals("@nexus:account")) return true;
    class_310 c = class_310.method_1551();
    return c != null
        && Objects.equals(player, c.method_1548().method_44717())
        && username.equalsIgnoreCase(c.method_1548().method_1676());
  }

  public static class_8685 resolve(UUID player) {
    class_310 c = class_310.method_1551();
    if (c == null || player == null) return null;
    boolean local = Objects.equals(player, c.method_1548().method_44717());
    if (local && !Objects.equals(account, player)) {
      account = player;
      loading = null;
      lastLoaded = null;
    }
    class_634 connection = c.method_1562();
    class_640 entry = connection == null ? null : connection.method_2871(player);
    class_8685 vanilla = entry == null ? null : entry.method_52810();
    if (vanilla != null && !isDefault(vanilla)) {
      if (local) lastLoaded = vanilla;
      return vanilla;
    }
    if (!local) return vanilla;
    try {
      if (loading == null) {
        GameProfile profile = c.method_53462();
        if (profile != null && Objects.equals(profile.id(), player))
          loading = c.method_1582().method_73544(profile, true);
      }
      class_8685 fetched = loading == null ? null : loading.get();
      if (fetched != null && !isDefault(fetched)) {
        lastLoaded = fetched;
        return fetched;
      }
      if (lastLoaded != null) return lastLoaded;
      if (vanilla != null) return vanilla;
      if (fetched != null) return fetched;
    } catch (Throwable ignored) {
      if (lastLoaded != null) return lastLoaded;
    }
    return class_1068.method_4648(player);
  }

  static boolean isDefault(class_8685 skin) {
    return skin.comp_1626().comp_3627().toString().startsWith("minecraft:textures/entity/player/");
  }
}
