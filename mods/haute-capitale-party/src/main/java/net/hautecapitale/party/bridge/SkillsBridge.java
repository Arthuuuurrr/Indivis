package net.hautecapitale.party.bridge;

import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.party.HauteCapitaleParty;
import net.hautecapitale.party.config.PartyConfig;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Le seul endroit qui connaisse Pufferfish Skills.
 *
 * <p>C'est la seule source de niveau de la pile : le noyau RPG n'en tient plus.
 * Pufferfish peut porter plusieurs categories, chacune avec son experience et son
 * niveau ; laquelle fait foi est un choix de serveur, d'ou le reglage
 * {@code levelCategory}. Sans reglage, on prend le plus haut niveau parmi toutes,
 * ce qui ne peut jamais sous-estimer un joueur.
 *
 * <p>Meme regle de prudence que pour le noyau : appel garde par la presence du
 * mod, et coupure definitive a la premiere erreur de liaison.
 */
public final class SkillsBridge {

    private static final boolean PRESENT = FabricLoader.getInstance().isModLoaded("puffish_skills");
    private static volatile boolean broken = false;

    /** Valeur rendue quand aucun niveau n'est connu. */
    public static final int UNKNOWN = -1;

    private SkillsBridge() {
    }

    public static boolean present() {
        return PRESENT && !broken;
    }

    public static int level(ServerPlayerEntity player) {
        if (!present()) {
            return UNKNOWN;
        }
        try {
            return Impl.level(player, PartyConfig.get().levelCategory);
        } catch (Throwable throwable) {
            broken = true;
            HauteCapitaleParty.LOGGER.warn(
                    "Pufferfish Skills ne repond plus comme attendu ({}). Le niveau ne sera plus affiche dans le HUD.",
                    throwable.toString());
            return UNKNOWN;
        }
    }

    private static final class Impl {
        private Impl() {
        }

        static int level(ServerPlayerEntity player, String categoryId) {
            if (categoryId != null && !categoryId.isBlank()) {
                Identifier id = Identifier.tryParse(categoryId);
                if (id != null) {
                    return net.puffish.skillsmod.api.SkillsAPI.getCategory(id)
                            .flatMap(net.puffish.skillsmod.api.Category::getExperience)
                            .map(experience -> experience.getLevel(player))
                            .orElse(UNKNOWN);
                }
            }
            return net.puffish.skillsmod.api.SkillsAPI.streamCategories()
                    .map(category -> category.getExperience()
                            .map(experience -> experience.getLevel(player))
                            .orElse(UNKNOWN))
                    .max(Integer::compare)
                    .orElse(UNKNOWN);
        }
    }
}
