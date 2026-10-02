/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  net.minecraft.class_10055
 *  net.minecraft.class_1071
 *  net.minecraft.class_11890
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  net.minecraft.class_8685
 */
package net.tompsen.nexuscharacters;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.class_10055;
import net.minecraft.class_1071;
import net.minecraft.class_11890;
import net.minecraft.class_310;
import net.minecraft.class_746;
import net.minecraft.class_8685;

public final class RemoteAppearanceSupport {
  private static final ConcurrentMap<String, class_8685> SKINS =
      new ConcurrentHashMap<String, class_8685>();
  private static final Set<String> LOADING = ConcurrentHashMap.newKeySet();

  private RemoteAppearanceSupport() {}

  public static void reset() {
    SKINS.clear();
    LOADING.clear();
  }

  public static void apply(class_11890 class_118902, class_10055 class_100552) {
    CharacterAppearancePayload characterAppearancePayload =
        CharacterAppearanceClient.get(class_118902.method_5667());
    float f = 1.0f;
    float f2 = 1.0f;
    class_8685 class_86852 = null;
    CharacterDto characterDto = RemoteAppearanceSupport.localSelectedFor(class_118902);
    if (characterAppearancePayload != null && !characterAppearancePayload.removed()) {
      f = RemoteAppearanceSupport.clamp(characterAppearancePayload.buildScaleFloat(), 0.65f, 1.3f);
      f2 =
          RemoteAppearanceSupport.clamp(characterAppearancePayload.heightScaleFloat(), 0.65f, 1.3f);
      class_86852 = RemoteAppearanceSupport.resolvePayloadSkin(characterAppearancePayload);
      if (class_86852 == null) {
        class_86852 = RemoteAppearanceSupport.resolveEntityTagSkin(class_118902);
      }
      if (class_86852 == null && characterDto != null) {
        class_86852 =
            RemoteAppearanceSupport.resolveDtoSkin(characterDto, class_118902.method_5667());
        f = RemoteAppearanceSupport.clamp(characterDto.buildScale(), 0.65f, 1.3f);
        f2 = RemoteAppearanceSupport.clamp(characterDto.heightScale(), 0.65f, 1.3f);
      }
    } else {
      if (characterDto != null) {
        f = RemoteAppearanceSupport.clamp(characterDto.buildScale(), 0.65f, 1.3f);
        f2 = RemoteAppearanceSupport.clamp(characterDto.heightScale(), 0.65f, 1.3f);
        class_86852 =
            RemoteAppearanceSupport.resolveDtoSkin(characterDto, class_118902.method_5667());
      }
      if (class_86852 == null) {
        class_86852 = RemoteAppearanceSupport.resolveEntityTagSkin(class_118902);
      }
      f = RemoteAppearanceSupport.readBuildTag(class_118902, f);
    }
    if (class_86852 != null) {
      class_100552.field_53520 = class_86852;
    }
    MorphRenderStateAccess morphRenderStateAccess = (MorphRenderStateAccess) class_100552;
    morphRenderStateAccess.nexuscharacters$setBuildScale(f);
    morphRenderStateAccess.nexuscharacters$setHeightScale(f2);
  }

  private static CharacterDto localSelectedFor(class_11890 class_118902) {
    try {
      class_746 class_7462;
      class_310 class_3102 = class_310.method_1551();
      if (class_3102 != null && (class_7462 = class_3102.field_1724) != null) {
        UUID uUID = class_7462.method_5667();
        UUID uUID2 = class_118902.method_5667();
        if (uUID != null && uUID.equals(uUID2)) {
          return NexusCharacters.selectedCharacter;
        }
      }
    } catch (Throwable throwable) {
      // empty catch block
    }
    return null;
  }

  private static class_8685 resolvePayloadSkin(
      CharacterAppearancePayload characterAppearancePayload) {
    class_8685 class_86852;
    String string = PresetSkinSupport.markerId(characterAppearancePayload.skinUsername());
    if (string != null && (class_86852 = PresetSkinSupport.textures(string)) != null) {
      return class_86852;
    }
    if (AccountSkinSupport.usesAccount(
        characterAppearancePayload.skinUsername(), characterAppearancePayload.playerId()))
      return AccountSkinSupport.resolve(characterAppearancePayload.playerId());
    if (characterAppearancePayload.skinValue() != null
        && !characterAppearancePayload.skinValue().isEmpty()) {
      return RemoteAppearanceSupport.resolveRaw(
          characterAppearancePayload.playerId(),
          characterAppearancePayload.skinValue(),
          characterAppearancePayload.skinSignature());
    }
    return null;
  }

  private static class_8685 resolveDtoSkin(CharacterDto characterDto, UUID uUID) {
    class_8685 class_86852;
    String string = PresetSkinSupport.markerId(characterDto.skinUsername());
    if (string != null && (class_86852 = PresetSkinSupport.textures(string)) != null) {
      return class_86852;
    }
    if (AccountSkinSupport.usesAccount(characterDto.skinUsername(), uUID))
      return AccountSkinSupport.resolve(uUID);
    if (characterDto.skinValue() != null && !characterDto.skinValue().isEmpty()) {
      return RemoteAppearanceSupport.resolveRaw(
          uUID, characterDto.skinValue(), characterDto.skinSignature());
    }
    return null;
  }

  private static class_8685 resolveEntityTagSkin(class_11890 class_118902) {
    return PresetSkinSupport.textures(PresetSkinSupport.entityPresetId(class_118902));
  }

  private static float readBuildTag(class_11890 class_118902, float f) {
    float f2 = f;
    try {
      for (String string : class_118902.method_5752()) {
        if (string == null || !string.startsWith("nexuscharacters.build.")) continue;
        f2 = Float.parseFloat(string.substring("nexuscharacters.build.".length())) / 100.0f;
      }
    } catch (Throwable throwable) {
      // empty catch block
    }
    return RemoteAppearanceSupport.clamp(f2, 0.65f, 1.3f);
  }

  private static class_8685 resolveRaw(UUID uUID, String string, String string2) {
    String string3 = string2 == null ? "" : string2;
    String string4 = string + ":" + string3;
    class_8685 class_86852 = (class_8685) SKINS.get(string4);
    if (class_86852 != null) {
      return class_86852;
    }
    if (!LOADING.add(string4)) {
      return null;
    }
    try {
      GameProfile gameProfile = new GameProfile(uUID, "NexusRemote");
      gameProfile
          .properties()
          .put("textures", new Property("textures", string, string3.isEmpty() ? null : string3));
      class_310 class_3102 = class_310.method_1551();
      class_1071 class_10712 = class_3102.method_1582();
      class_10712
          .method_52863(gameProfile)
          .thenAccept(
              optional -> {
                try {
                  if (optional != null && optional.isPresent()) {
                    SKINS.put(string4, (class_8685) optional.get());
                  }
                } finally {
                  LOADING.remove(string4);
                }
              });
    } catch (Throwable throwable) {
      LOADING.remove(string4);
    }
    return null;
  }

  private static float clamp(float f, float f2, float f3) {
    return Math.max(f2, Math.min(f3, f));
  }
}
