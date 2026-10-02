package net.hautecapitale.metiers.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hautecapitale.metiers.creature.DropEntry;
import net.hautecapitale.metiers.profession.Profession;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Un type de node : ce qu'est un filon de fer, un buisson de baies, une touffe
 * de pissenlits.
 *
 * <p>Le type vit dans les datapacks ; les nodes eux-mêmes — <em>où</em> ils sont
 * — vivent dans la sauvegarde du monde, posés un par un par l'administrateur.
 * Le fichier {@code data/<ns>/hcm/nodes/mineur/fer.json} décrit le type
 * {@code <ns>:mineur/fer} — le chemin est la clé.
 *
 * <p>Un node n'est jamais détruit : récolté, son bloc <em>plein</em> devient son
 * bloc <em>vide</em>, et le plein revient après le délai. C'est ce qui garantit
 * qu'aucune mécanique de métier ne creuse la carte.
 *
 * @param profession  le métier qui récolte
 * @param level       niveau requis — en dessous, le node est incassable
 * @param xp          XP à niveau égal ; la décote s'applique ensuite
 * @param fullBlock   le bloc quand le node est disponible, {@code id[prop=val]} accepté
 * @param emptyBlock  le bloc quand il vient d'être récolté
 * @param loot        ce que la récolte donne
 * @param respawnSeconds délai avant que le plein revienne
 * @param hits        coups nécessaires pour récolter
 * @param tool        famille d'outils exigée en main, s'il y en a une
 * @param title       libellé affiché, sinon le nom du bloc plein
 */
public record NodeType(
        Profession profession,
        int level,
        double xp,
        String fullBlock,
        String emptyBlock,
        List<DropEntry> loot,
        int respawnSeconds,
        int hits,
        Optional<TagKey<net.minecraft.item.Item>> tool,
        Optional<String> title,
        Optional<String> comment
) {

    private static final Codec<TagKey<net.minecraft.item.Item>> TOOL_CODEC =
            Identifier.CODEC.xmap(id -> TagKey.of(RegistryKeys.ITEM, id), TagKey::id);

    public static final Codec<NodeType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Profession.CODEC.fieldOf("metier").forGetter(NodeType::profession),
            Codec.INT.optionalFieldOf("niveau", 1).forGetter(NodeType::level),
            Codec.DOUBLE.optionalFieldOf("xp", 0.0D).forGetter(NodeType::xp),
            Codec.STRING.fieldOf("bloc_plein").forGetter(NodeType::fullBlock),
            Codec.STRING.fieldOf("bloc_vide").forGetter(NodeType::emptyBlock),
            DropEntry.CODEC.listOf().fieldOf("loot").forGetter(NodeType::loot),
            Codec.INT.optionalFieldOf("respawn", 300).forGetter(NodeType::respawnSeconds),
            Codec.INT.optionalFieldOf("coups", 3).forGetter(NodeType::hits),
            TOOL_CODEC.optionalFieldOf("outil").forGetter(NodeType::tool),
            Codec.STRING.optionalFieldOf("titre").forGetter(NodeType::title),
            Codec.STRING.optionalFieldOf("commentaire").forGetter(NodeType::comment)
    ).apply(instance, NodeType::new));

    public NodeType {
        if (level < 1) {
            level = 1;
        }
        if (xp < 0.0D || !Double.isFinite(xp)) {
            xp = 0.0D;
        }
        if (respawnSeconds < 1) {
            respawnSeconds = 1;
        }
        if (hits < 1) {
            hits = 1;
        }
        loot = List.copyOf(loot);
    }

    /**
     * L'état de bloc décrit par une chaîne {@code id} ou {@code id[prop=val,…]},
     * ou {@code null} si elle ne désigne rien. Même syntaxe que {@code /setblock}.
     */
    public static BlockState parseBlock(String text) {
        try {
            return BlockArgumentParser.block(Registries.BLOCK, text, false).blockState();
        } catch (Exception e) {
            return null;
        }
    }

    public BlockState fullState() {
        return parseBlock(fullBlock);
    }

    public BlockState emptyState() {
        return parseBlock(emptyBlock);
    }

    public long respawnMillis() {
        return respawnSeconds * 1000L;
    }
}
