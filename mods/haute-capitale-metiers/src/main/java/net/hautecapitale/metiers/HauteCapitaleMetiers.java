package net.hautecapitale.metiers;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.hautecapitale.metiers.command.MetiersCommands;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.craft.MasteryAttachment;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.entity.HcmEntities;
import net.hautecapitale.metiers.gadget.ChanneledTeleportItem;
import net.hautecapitale.metiers.gadget.GadgetComponents;
import net.hautecapitale.metiers.gadget.GadgetItems;
import net.hautecapitale.metiers.gadget.RepellentItem;
import net.hautecapitale.metiers.hearth.HearthAttachment;
import net.hautecapitale.metiers.hunt.HuntEngine;
import net.hautecapitale.metiers.hunt.OriginAttachment;
import net.hautecapitale.metiers.hunt.OriginMarker;
import net.hautecapitale.metiers.item.HcmItems;
import net.hautecapitale.metiers.meal.MealAttachment;
import net.hautecapitale.metiers.meal.MealConsumeEffect;
import net.hautecapitale.metiers.meal.MealEngine;
import net.hautecapitale.metiers.meal.PotRestriction;
import net.hautecapitale.metiers.node.NodeAttachment;
import net.hautecapitale.metiers.node.NodeEngine;
import net.hautecapitale.metiers.node.NodeTool;
import net.hautecapitale.metiers.npc.EasyNpcBridge;
import net.hautecapitale.metiers.npc.HcmScreens;
import net.hautecapitale.metiers.npc.ProfessionNetwork;
import net.hautecapitale.metiers.profession.Profession;
import net.hautecapitale.metiers.profession.ProfessionAttachments;
import net.hautecapitale.metiers.quality.Quality;
import net.hautecapitale.metiers.repair.BrokenComponent;
import net.hautecapitale.metiers.skin.SkinningEngine;
import net.hautecapitale.metiers.textile.CottonBlocks;
import net.hautecapitale.metiers.textile.TextileItems;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Haute Capitale — Métiers.
 *
 * <p>Étape 1 : le socle. Métiers, niveaux, XP, persistance, commandes
 * d'administration.
 *
 * <p>Étape 2 : le moteur de données. Le contenu du MMO — d'abord les fiches de
 * créature — vit dans des datapacks rechargeables à chaud, jamais dans ce code.
 *
 * <p>Étapes 3 à 6 : rôles de PNJ (Easy NPC), matières, atelier de fabrication,
 * et le moteur de nodes — les filons et plantes que l'on récolte sans jamais
 * creuser la carte.
 *
 * <p>Étapes 7 à 10 : chasse et origine des créatures, carcasses et dépeçage,
 * forge et réparation, gadgets de l'Ingénieur et Pierre de foyer.
 *
 * <p>Étape 11 : Couturier, Joaillier et Cuisinier — la qualité Excellent et les
 * buffs de repas, communs aux artisans.
 */
public class HauteCapitaleMetiers implements ModInitializer {

    public static final String MOD_ID = "haute_capitale_metiers";
    public static final Logger LOGGER = LoggerFactory.getLogger("Haute Capitale — Métiers");

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        MetiersConfig.load();
        ProfessionAttachments.init();
        MasteryAttachment.init();
        HcmItems.init();
        BrokenComponent.init();
        HcmData.init();
        HcmScreens.init();
        ProfessionNetwork.init();
        EasyNpcBridge.init();
        NodeAttachment.init();
        NodeTool.init();
        NodeEngine.init();
        OriginAttachment.init();
        OriginMarker.init();
        HuntEngine.init();
        HcmEntities.init();
        SkinningEngine.init();
        GadgetComponents.init();
        GadgetItems.init();
        HearthAttachment.init();
        RepellentItem.init();
        ChanneledTeleportItem.init();
        Quality.init();
        MealConsumeEffect.init();
        MealAttachment.init();
        MealEngine.init();
        PotRestriction.init();
        TextileItems.init();
        CottonBlocks.init();

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> MetiersCommands.register(dispatcher));

        LOGGER.info("Socle initialisé — {} métiers, niveau maximum {}.",
                Profession.values().length, MetiersConfig.get().niveau_max);
    }
}
