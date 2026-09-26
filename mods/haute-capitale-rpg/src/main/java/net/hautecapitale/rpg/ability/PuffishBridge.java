package net.hautecapitale.rpg.ability;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.puffish.skillsmod.api.SkillsAPI;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Le seul endroit du noyau qui connaisse Pufferfish.
 *
 * <p>Pufferfish est la source de vérité de la progression : ce fichier lit, il n'écrit
 * jamais. Aucune compétence n'est stockée de notre côté — la liste est reconstruite à
 * la demande, ce qui rend impossible la divergence entre l'arbre et nous.
 *
 * <p>Les événements {@code SkillUnlock} / {@code SkillLock} de l'API ne transportent
 * pas le joueur concerné (leur signature est {@code (Identifier catégorie, String
 * compétence)}), ils sont donc inutilisables pour réagir à un déblocage individuel.
 * Le crochet par joueur est une récompense personnalisée — voir {@link AbilityReward}.
 *
 * <p>Toutes les méthodes tolèrent l'absence du mod : le noyau doit rester chargeable
 * sur une instance qui n'a pas l'arbre.
 */
public final class PuffishBridge {

    private static final boolean PRESENT =
            FabricLoader.getInstance().isModLoaded("puffish_skills");

    private PuffishBridge() {
    }

    public static boolean present() {
        return PRESENT;
    }

    /**
     * Toutes les compétences débloquées d'un joueur, sous la forme {@code catégorie/compétence}.
     *
     * <p>L'identifiant de compétence n'est unique qu'à l'intérieur de sa catégorie ; le
     * préfixer évite qu'un nœud « execution » de l'arbre guerrier soit confondu avec un
     * homonyme de l'arbre roublard.
     */
    public static Set<String> unlockedSkills(ServerPlayerEntity player) {
        if (!PRESENT) {
            return Set.of();
        }
        var result = new LinkedHashSet<String>();
        SkillsAPI.streamCategories().forEach(category ->
                category.streamUnlockedSkills(player).forEach(skill ->
                        result.add(key(category.getId(), skill.getId()))));
        return result;
    }

    /** La clé sous laquelle une compétence est désignée dans nos définitions. */
    public static String key(Identifier categoryId, String skillId) {
        return categoryId + "/" + skillId;
    }
}
