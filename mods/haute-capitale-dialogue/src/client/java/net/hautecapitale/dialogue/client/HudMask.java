package net.hautecapitale.dialogue.client;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.hautecapitale.dialogue.HauteCapitaleDialogue;
import net.minecraft.util.Identifier;

import java.util.Map;

/**
 * Masque les éléments vanilla du HUD pendant une conversation.
 *
 * <p>Sans toucher à {@code options.hideGui} — Bosses'Rise se l'est réservé pour
 * ses cinématiques — chaque élément est enveloppé par le registre de Fabric :
 * l'original est dessiné tant qu'aucune conversation ne le cache, et rien
 * n'est à restaurer, puisque rien n'a été retiré.
 */
public final class HudMask {

    private static final Map<String, Identifier> ELEMENTS = Map.ofEntries(
            Map.entry("misc_overlays", VanillaHudElements.MISC_OVERLAYS),
            Map.entry("crosshair", VanillaHudElements.CROSSHAIR),
            Map.entry("hotbar", VanillaHudElements.HOTBAR),
            Map.entry("armor_bar", VanillaHudElements.ARMOR_BAR),
            Map.entry("health_bar", VanillaHudElements.HEALTH_BAR),
            Map.entry("food_bar", VanillaHudElements.FOOD_BAR),
            Map.entry("air_bar", VanillaHudElements.AIR_BAR),
            Map.entry("mount_health", VanillaHudElements.MOUNT_HEALTH),
            Map.entry("info_bar", VanillaHudElements.INFO_BAR),
            Map.entry("experience_level", VanillaHudElements.EXPERIENCE_LEVEL),
            Map.entry("held_item_tooltip", VanillaHudElements.HELD_ITEM_TOOLTIP),
            Map.entry("status_effects", VanillaHudElements.STATUS_EFFECTS),
            Map.entry("boss_bar", VanillaHudElements.BOSS_BAR),
            Map.entry("scoreboard", VanillaHudElements.SCOREBOARD),
            Map.entry("overlay_message", VanillaHudElements.OVERLAY_MESSAGE),
            Map.entry("title_and_subtitle", VanillaHudElements.TITLE_AND_SUBTITLE),
            Map.entry("chat", VanillaHudElements.CHAT),
            Map.entry("player_list", VanillaHudElements.PLAYER_LIST),
            Map.entry("subtitles", VanillaHudElements.SUBTITLES));

    private static boolean actif;

    private HudMask() {
    }

    static void init() {
        int installes = 0;
        for (String nom : DialogueClientSettings.get().hud_masque) {
            Identifier id = ELEMENTS.get(nom);
            if (id == null) {
                HauteCapitaleDialogue.LOGGER.warn("Élément de HUD inconnu dans hud_masque : {}", nom);
                continue;
            }
            try {
                HudElementRegistry.replaceElement(id, original -> (HudElement) (context, tickCounter) -> {
                    if (!actif) {
                        original.render(context, tickCounter);
                    }
                });
                installes++;
            } catch (Exception e) {
                HauteCapitaleDialogue.LOGGER.warn("Impossible d'envelopper l'élément de HUD {} : {}", nom, e.toString());
            }
        }
        HauteCapitaleDialogue.LOGGER.info("Masque de HUD prêt sur {} élément(s).", installes);
    }

    static void activer(boolean valeur) {
        actif = valeur;
    }

    public static boolean actif() {
        return actif;
    }
}
