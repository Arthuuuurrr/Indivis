package net.hautecapitale.metiers.meal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hautecapitale.metiers.util.Vocabulary;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

import java.util.Optional;

/**
 * Un buff de repas : ce qu'un plat du Cuisinier donne à qui le mange.
 *
 * <p>Le fichier {@code data/<ns>/hcm/buffs/vigueur.json} décrit
 * {@code <ns>:vigueur} — un attribut, une opération, une valeur, une durée.
 * L'attribut peut venir de n'importe quel mod (Puffish Attributes, Spell
 * Power, Minecraft) : s'il n'existe pas sur l'installation, le buff est
 * chargé mais ne fait rien, et le rapport de chargement le dit.
 *
 * <p>Un seul buff de repas à la fois : manger un autre plat remplace le
 * précédent. Les effets alimentaires des plats eux-mêmes — nourriture, confort
 * de Farmer's Delight — ne sont pas des buffs et restent tels quels.
 *
 * @param attribute l'attribut touché
 * @param operation comment : ajout, pourcentage de la base, pourcentage du total
 * @param value     de combien — 0.05 = +5 % pour une opération en pourcentage
 * @param seconds   durée, sinon celle de la configuration
 * @param title     nom lisible, montré au joueur
 */
public record MealBuff(
        Identifier attribute,
        Operation operation,
        double value,
        Optional<Integer> seconds,
        Optional<String> title,
        Optional<String> comment
) {

    /** Les trois opérations de Minecraft, nommées en clair dans les fichiers. */
    public enum Operation implements StringIdentifiable {
        AJOUT("ajout", EntityAttributeModifier.Operation.ADD_VALUE),
        POURCENT_BASE("pourcent_base", EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE),
        POURCENT_TOTAL("pourcent_total", EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        public static final Codec<Operation> CODEC = Vocabulary.of("operation", Operation::values);

        private final String id;
        public final EntityAttributeModifier.Operation vanilla;

        Operation(String id, EntityAttributeModifier.Operation vanilla) {
            this.id = id;
            this.vanilla = vanilla;
        }

        @Override
        public String asString() {
            return id;
        }

        public boolean isPercent() {
            return this != AJOUT;
        }
    }

    public static final Codec<MealBuff> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("attribut").forGetter(MealBuff::attribute),
            Operation.CODEC.optionalFieldOf("operation", Operation.POURCENT_BASE).forGetter(MealBuff::operation),
            Codec.DOUBLE.fieldOf("valeur").forGetter(MealBuff::value),
            Codec.INT.optionalFieldOf("duree").forGetter(MealBuff::seconds),
            Codec.STRING.optionalFieldOf("titre").forGetter(MealBuff::title),
            Codec.STRING.optionalFieldOf("commentaire").forGetter(MealBuff::comment)
    ).apply(instance, MealBuff::new));

    public MealBuff {
        if (!Double.isFinite(value)) {
            value = 0.0D;
        }
        seconds = seconds.filter(s -> s > 0);
    }
}
