package net.hautecapitale.rpg;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.rpg.command.RpgCommands;
import net.hautecapitale.rpg.config.RpgConfig;
import net.hautecapitale.rpg.content.EquipmentLock;
import net.hautecapitale.rpg.content.SpellLock;
import net.hautecapitale.rpg.rpgclass.ClassAttachments;
import net.hautecapitale.rpg.rpgclass.RpgClass;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Haute Capitale — RPG.
 *
 * <p>Étape 1 : le noyau. Classes, données joueur, configuration, commandes.
 *
 * <p>Ce mod se pose <b>à côté</b> des mods de classe existants, il ne les
 * remplace pas. Il n'enregistre aucun objet, aucun sort, aucune entité, et
 * n'installe aucun mixin : il ne revendique donc aucun des 1321 identifiants de
 * registre déjà en service. Le retirer ramène le serveur exactement à son état
 * antérieur.
 */
public class HauteCapitaleRpg implements ModInitializer {

    public static final String MOD_ID = "haute_capitale_rpg";
    public static final Logger LOGGER = LoggerFactory.getLogger("Haute Capitale — RPG");

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        RpgConfig.load();
        ClassAttachments.init();
        EquipmentLock.init();

        // Spell Engine est facultatif : la classe qui le référence n.est chargée que
        // s.il est présent, sinon le noyau tournerait au-dessus du vide.
        if (FabricLoader.getInstance().isModLoaded("spell_engine")) {
            SpellLock.init();
            LOGGER.info("Verrou de sorts branché sur Spell Engine.");
            net.hautecapitale.rpg.ability.AbilityFeature.init();
        } else {
            LOGGER.info("Spell Engine absent : verrou de sorts et capacités inactifs.");
        }

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> RpgCommands.register(dispatcher));

        LOGGER.info("Noyau initialisé — {} classes. La progression vient de Pufferfish.",
                RpgClass.values().length);

        if (RpgConfig.get().journaliser_detection) {
            reportDetection();
        }
    }

    /**
     * Signale quelles classes disposent réellement de leur mod d'origine.
     *
     * <p>Purement informatif : une classe dont le mod est absent reste
     * sélectionnable, elle n'a simplement aucun contenu derrière elle. Mieux vaut
     * le voir au démarrage que le découvrir en jeu.
     */
    private void reportDetection() {
        List<String> manquantes = new ArrayList<>();
        for (RpgClass rpgClass : RpgClass.values()) {
            boolean present = rpgClass.namespaces().stream()
                    .anyMatch(ns -> FabricLoader.getInstance().isModLoaded(ns));
            if (!present) {
                manquantes.add(rpgClass.getId() + " (" + String.join(", ", rpgClass.namespaces()) + ")");
            }
        }
        if (manquantes.isEmpty()) {
            LOGGER.info("Les {} classes ont leur mod de contenu.", RpgClass.values().length);
        } else {
            LOGGER.warn("Classes sans mod de contenu installé : {}", String.join(" · ", manquantes));
        }
    }
}
