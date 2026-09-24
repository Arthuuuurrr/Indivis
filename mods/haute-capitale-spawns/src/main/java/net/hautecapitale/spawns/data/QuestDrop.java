package net.hautecapitale.spawns.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

/**
 * Un objet de quete associe a un mob controle.
 *
 * <p>Ne passe pas par la table de butin vanilla du mob : c'est le moteur du
 * gestionnaire (ou un module externe, via l'evenement de mort) qui l'attribue.
 *
 * @param item identifiant de l'objet
 * @param count nombre d'exemplaires par attribution
 * @param chancePercent chance en pourcent (0-100)
 * @param personal si vrai, chaque joueur credite recoit son propre tirage directement en inventaire ;
 *                 sinon un seul tirage tombe au sol
 */
public record QuestDrop(Identifier item, int count, double chancePercent, boolean personal) {

    public static final Codec<QuestDrop> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("item").forGetter(QuestDrop::item),
            Codec.INT.optionalFieldOf("count", 1).forGetter(QuestDrop::count),
            Codec.DOUBLE.optionalFieldOf("chance", 100.0D).forGetter(QuestDrop::chancePercent),
            Codec.BOOL.optionalFieldOf("personal", true).forGetter(QuestDrop::personal)
    ).apply(instance, QuestDrop::new));

    public String describe() {
        return this.item + " x" + this.count + " (" + this.chancePercent + " %" + (this.personal ? ", personnel" : ", au sol") + ")";
    }
}
