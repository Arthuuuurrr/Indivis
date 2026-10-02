/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1068
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_745
 *  net.minecraft.class_8685
 */
package net.tompsen.nexuscharacters;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1068;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_745;
import net.minecraft.class_8685;

@Environment(value = EnvType.CLIENT)
public final class PreviewDummyPlayerManager {
  private static final Map<UUID, PreviewPlayer> CACHE = new HashMap<UUID, PreviewPlayer>();
  private static String accountSkinKey;
  private static Supplier<class_8685> accountSkinSupplier;
  private static boolean accountSkinFailureLogged;

  private PreviewDummyPlayerManager() {}

  public static class_745 get(CharacterDto characterDto) {
    class_638 class_6382 = class_310.method_1551().field_1687;
    if (class_6382 == null) {
      return null;
    }
    PreviewPlayer previewPlayer = CACHE.get(characterDto.id());
    if (previewPlayer != null && previewPlayer.method_73183() == class_6382) {
      previewPlayer.currentCharacter = characterDto;
      return previewPlayer;
    }
    PreviewPlayer previewPlayer2 =
        new PreviewPlayer(
            class_6382,
            new GameProfile(
                characterDto.id(), PreviewDummyPlayerManager.safeProfileName(characterDto.name())),
            characterDto);
    previewPlayer2.method_5814(0.0, 0.0, 0.0);
    previewPlayer2.field_6012 = 20;
    CACHE.put(characterDto.id(), previewPlayer2);
    return previewPlayer2;
  }

  private static String safeProfileName(String string) {
    if (string == null || string.isBlank()) {
      return "Apercu";
    }
    String string2 = string.replaceAll("&[0-9a-fk-orA-FK-OR]", "").trim();
    return string2.length() <= 16 ? string2 : string2.substring(0, 16);
  }

  public static class_8685 skin(CharacterDto characterDto) {
    Object object;
    class_310 class_3102 = class_310.method_1551();
    String string = characterDto.skinUsername();
    String string2 = class_3102.method_1548().method_1676();
    if ((string == null
            || string.isBlank()
            || string.equals("@nexus:account")
            || string.equalsIgnoreCase(string2))
        && (object = PreviewDummyPlayerManager.accountSkin(class_3102, string2)) != null) {
      return (class_8685) object;
    }
    object = PresetSkinSupport.markerId(string);
    class_8685 class_86852 = PresetSkinSupport.textures((String) object);
    if (class_86852 != null) {
      return class_86852;
    }
    String string3 = characterDto.skinValue();
    if (string3 != null && !string3.isBlank()) {
      try {
        GameProfile gameProfile =
            new GameProfile(
                characterDto.id(), PreviewDummyPlayerManager.safeProfileName(characterDto.name()));
        gameProfile
            .properties()
            .put(
                "textures",
                new Property(
                    "textures",
                    string3,
                    characterDto.skinSignature() == null ? "" : characterDto.skinSignature()));
        return (class_8685) class_3102.method_1582().method_73544(gameProfile, true).get();
      } catch (Throwable throwable) {
        NexusCharacters.LOGGER.warn(
            "[NexusCharacters] Impossible de charger le skin sauvegard\u00e9 de {}: {}",
            (Object) characterDto.name(),
            (Object) throwable.toString());
      }
    }
    return class_1068.method_4648((UUID) characterDto.id());
  }

  private static class_8685 accountSkin(class_310 client, String name) {
    return AccountSkinSupport.resolve(client.method_1548().method_44717());
  }

  private static final class PreviewPlayer extends class_745 implements NexusDummyEntity {
    private CharacterDto currentCharacter;

    private PreviewPlayer(
        class_638 class_6382, GameProfile gameProfile, CharacterDto characterDto) {
      super(class_6382, gameProfile);
      this.currentCharacter = characterDto;
    }

    public class_8685 method_52814() {
      return PreviewDummyPlayerManager.skin(this.currentCharacter);
    }

    public boolean method_7325() {
      return false;
    }

    public boolean method_68878() {
      return false;
    }
  }
}
