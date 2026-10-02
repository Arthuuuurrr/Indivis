package net.hautecapitale.rpg.ability;

import net.hautecapitale.rpg.HauteCapitaleRpg;
import net.minecraft.util.Identifier;
import net.puffish.skillsmod.api.SkillsAPI;
import net.puffish.skillsmod.api.json.BuiltinJson;
import net.puffish.skillsmod.api.json.JsonElement;
import net.puffish.skillsmod.api.reward.Reward;
import net.puffish.skillsmod.api.reward.RewardConfigContext;
import net.puffish.skillsmod.api.reward.RewardDisposeContext;
import net.puffish.skillsmod.api.reward.RewardUpdateContext;
import net.puffish.skillsmod.api.util.Problem;
import net.puffish.skillsmod.api.util.Result;

import java.util.List;

/**
 * La récompense d'arbre qui accorde des capacités.
 *
 * <pre>
 * { "type": "haute_capitale_rpg:abilities",
 *   "data": { "abilities": ["capitale:whirlwind", "capitale:charge"] } }
 * </pre>
 *
 * <p>Un nœud peut donc accorder plusieurs capacités, et plusieurs nœuds peuvent accorder
 * la même : le cache est un ensemble, pas un compteur, donc verrouiller l'un des chemins
 * ne retire pas ce que l'autre accorde encore — la reconstruction complète tranche.
 *
 * <p><b>Pourquoi une récompense et non un événement.</b> Les événements
 * {@code SkillUnlock} / {@code SkillLock} de Pufferfish ne transportent pas le joueur
 * concerné : leur signature se limite à la catégorie et à l'identifiant du nœud. Ils sont
 * donc structurellement incapables de dire <i>qui</i> vient de débloquer quoi. La
 * récompense, elle, reçoit le joueur, le nombre de rangs acquis, et est rejouée par
 * Pufferfish à chaque changement — y compris au respec, où le compte tombe à zéro. Ce qui
 * ressemblait à un pis-aller est en réalité le seul crochet correct, et il rend la
 * reconstruction idempotente sans effort.
 */
public final class AbilityReward implements Reward {

    public static final Identifier ID = HauteCapitaleRpg.id("abilities");

    private final List<Identifier> abilities;

    private AbilityReward(List<Identifier> abilities) {
        this.abilities = abilities;
    }

    public static void register() {
        SkillsAPI.registerReward(ID, AbilityReward::parse);
        HauteCapitaleRpg.LOGGER.info("[Capacités] Récompense d'arbre « {} » enregistrée.", ID);
    }

    private static Result<AbilityReward, Problem> parse(RewardConfigContext context) {
        return context.getData()
                .andThen(JsonElement::getAsObject)
                .andThen(object -> object.getArray("abilities"))
                .andThen(array -> array
                        .getAsList((index, element) -> BuiltinJson.parseIdentifier(element))
                        .mapFailure(Problem::combine))
                .mapSuccess(AbilityReward::new);
    }

    @Override
    public void update(RewardUpdateContext context) {
        var player = context.getPlayer();
        if (context.getCount() > 0) {
            LearnedAbilities.grant(player, abilities);
        } else {
            LearnedAbilities.revoke(player, abilities);
        }
        // On ne reconstruit pas ici : un respec rejoue toutes les récompenses d'affilée, et
        // reconstruire à chaque passage enverrait autant de paquets que de nœuds. Le drapeau
        // est relevé, la reconstruction a lieu une fois, en fin de tick.
        AbilityFeature.markDirty(player);
    }

    @Override
    public void dispose(RewardDisposeContext context) {
        // Rien à libérer : cette récompense ne pose ni modificateur d'attribut ni objet.
        // L'effet est entièrement porté par le cache, que la reconstruction remet à plat.
    }
}
