package net.hautecapitale.rpg.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Ce qu'une capacité <i>est</i>, indépendamment de la manière dont on l'obtient.
 *
 * <p>La séparation est délibérée : cette définition suit l'équilibrage, tandis que le
 * lien compétence → capacité suit la forme de l'arbre et vit donc dans l'arbre
 * Pufferfish lui-même, sous la récompense {@link AbilityReward}. Deux fichiers qui
 * changent pour deux raisons différentes n'ont pas à n'en faire qu'un.
 *
 * <p>Le champ {@code spell} désigne un sort Spell Engine existant. Le mot « sort » est
 * ici un terme technique : un coup d'épée tournoyant, une volée de carreaux et une
 * boule de feu sont tous des sorts pour le moteur.
 *
 * <p>Rien dans cette définition ne nomme un mod en particulier. Une capacité dont le
 * sort provient d'un mod absent est signalée au chargement puis ignorée, sans faire
 * tomber le serveur.
 */
public record AbilityDefinition(
        int schema_version,
        Identifier spell,
        List<Identifier> categories,
        WeaponRequirement weapon,
        boolean requires_skill) {

    /** Version de format actuellement comprise. Une définition plus récente est refusée plutôt que mal lue. */
    public static final int SCHEMA = 1;

    public static final Codec<AbilityDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("schema_version", SCHEMA).forGetter(AbilityDefinition::schema_version),
            Identifier.CODEC.fieldOf("spell").forGetter(AbilityDefinition::spell),
            Identifier.CODEC.listOf().optionalFieldOf("ability_categories", List.of()).forGetter(AbilityDefinition::categories),
            WeaponRequirement.CODEC.optionalFieldOf("weapon_requirements", WeaponRequirement.ANY).forGetter(AbilityDefinition::weapon),
            Codec.BOOL.optionalFieldOf("requires_skill", true).forGetter(AbilityDefinition::requires_skill)
    ).apply(instance, AbilityDefinition::new));

    /**
     * L'équipement qu'une capacité exige.
     *
     * <p>Les tags d'objets sont la voie normale — {@code #capitale:swords} vaut mieux que
     * trente identifiants recopiés — mais un objet précis reste nécessaire pour les cas
     * particuliers, comme une capacité liée à une seule arme légendaire. Les deux listes
     * s'additionnent : correspondre à l'une suffit.
     *
     * <p>{@code hands} n'est pas encore appliqué au-delà de la main principale ; il est
     * lu et conservé dès maintenant pour que les cas main gauche, deux mains et double
     * arme puissent arriver sans changer le format des fichiers déjà écrits.
     */
    public record WeaponRequirement(List<Identifier> allowed_tags, List<Identifier> allowed_items, Hand hands) {

        public enum Hand {
            MAIN_HAND, OFF_HAND, ANY;
            public static final Codec<Hand> CODEC = Codec.STRING.xmap(
                    s -> valueOf(s.toUpperCase()), h -> h.name().toLowerCase());
        }

        /** Aucune exigence : la capacité fonctionne quelle que soit l'arme tenue. */
        public static final WeaponRequirement ANY =
                new WeaponRequirement(List.of(), List.of(), Hand.MAIN_HAND);

        public static final Codec<WeaponRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.listOf().optionalFieldOf("allowed_tags", List.of()).forGetter(WeaponRequirement::allowed_tags),
                Identifier.CODEC.listOf().optionalFieldOf("allowed_items", List.of()).forGetter(WeaponRequirement::allowed_items),
                Hand.CODEC.optionalFieldOf("hands", Hand.MAIN_HAND).forGetter(WeaponRequirement::hands)
        ).apply(instance, WeaponRequirement::new));

        public boolean unrestricted() {
            return allowed_tags.isEmpty() && allowed_items.isEmpty();
        }

        public boolean matches(ItemStack stack) {
            if (unrestricted()) {
                return true;
            }
            if (stack.isEmpty()) {
                return false;
            }
            for (Identifier tagId : allowed_tags) {
                if (stack.isIn(TagKey.of(RegistryKeys.ITEM, tagId))) {
                    return true;
                }
            }
            Identifier itemId = stack.getRegistryEntry().getKey()
                    .map(key -> key.getValue()).orElse(null);
            return itemId != null && allowed_items.contains(itemId);
        }

        /** Description lisible, pour le message de refus et la commande de diagnostic. */
        public String describe() {
            if (unrestricted()) {
                return "aucune";
            }
            var parts = new java.util.ArrayList<String>();
            allowed_tags.forEach(id -> parts.add("#" + id));
            allowed_items.forEach(id -> parts.add(id.toString()));
            return String.join(", ", parts);
        }
    }
}
