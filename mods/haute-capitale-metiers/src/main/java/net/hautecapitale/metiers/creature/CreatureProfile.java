package net.hautecapitale.metiers.creature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hautecapitale.metiers.creature.CreatureEnums.Category;
import net.hautecapitale.metiers.creature.CreatureEnums.Rarity;
import net.hautecapitale.metiers.creature.CreatureEnums.SpawnOrigin;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Fiche d'une créature : tout ce que le MMO doit savoir d'elle.
 *
 * <p>C'est la source de vérité unique promise par le cahier des charges. Aucune
 * créature n'est décidée en Java : le crocodile est dépeçable et le saumon ne
 * l'est pas parce que leurs fichiers le disent. Changer un niveau, une
 * quantité ou une éligibilité ne demande qu'un {@code /reload}.
 *
 * <p>Le fichier {@code data/cubeanimals/hcm/creatures/roedeer.json} décrit
 * {@code cubeanimals:roedeer} — le chemin est la clé.
 */
public record CreatureProfile(
        Category category,
        Rarity rarity,
        Optional<HunterEntry> hunter,
        Optional<SkinningEntry> skinning,
        Optional<DropEntry> meat,
        List<DropEntry> combatLoot,
        List<SpawnOrigin> xpOrigins,
        Optional<String> comment
) {

    /** Ce que le Chasseur gagne à abattre cette créature. */
    public record HunterEntry(int level, double xp) {
        public static final Codec<HunterEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("niveau", 1).forGetter(HunterEntry::level),
                Codec.DOUBLE.optionalFieldOf("xp", 0.0D).forGetter(HunterEntry::xp)
        ).apply(instance, HunterEntry::new));

        public HunterEntry {
            if (level < 1) {
                level = 1;
            }
            if (xp < 0.0D || !Double.isFinite(xp)) {
                xp = 0.0D;
            }
        }
    }

    /**
     * Ce que le Dépeceur peut tirer de sa carcasse.
     *
     * @param level           niveau requis
     * @param xp              XP à niveau égal ; la décote s'applique ensuite
     * @param carcassSeconds  durée de vie de la carcasse au sol
     * @param channelSeconds  durée du dépeçage lui-même, pendant laquelle il faut rester
     * @param tool            famille d'outils exigée en main ; absente = celle de la configuration
     * @param material        la matière principale
     * @param secondary       ce qui peut venir en plus — tendon, os, corne
     */
    public record SkinningEntry(
            int level,
            double xp,
            int carcassSeconds,
            int channelSeconds,
            Optional<Identifier> tool,
            DropEntry material,
            List<DropEntry> secondary
    ) {
        public static final Codec<SkinningEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("niveau", 1).forGetter(SkinningEntry::level),
                Codec.DOUBLE.optionalFieldOf("xp", 0.0D).forGetter(SkinningEntry::xp),
                Codec.INT.optionalFieldOf("duree_carcasse", 60).forGetter(SkinningEntry::carcassSeconds),
                Codec.INT.optionalFieldOf("canalisation", 3).forGetter(SkinningEntry::channelSeconds),
                Identifier.CODEC.optionalFieldOf("outil").forGetter(SkinningEntry::tool),
                DropEntry.CODEC.fieldOf("matiere").forGetter(SkinningEntry::material),
                DropEntry.CODEC.listOf().optionalFieldOf("secondaires", List.of())
                        .forGetter(SkinningEntry::secondary)
        ).apply(instance, SkinningEntry::new));

        /** Forme courte, pour les tests et le diagnostic. */
        public SkinningEntry(int level, double xp, int carcassSeconds, DropEntry material, List<DropEntry> secondary) {
            this(level, xp, carcassSeconds, 3, Optional.empty(), material, secondary);
        }

        public SkinningEntry {
            if (level < 1) {
                level = 1;
            }
            if (xp < 0.0D || !Double.isFinite(xp)) {
                xp = 0.0D;
            }
            if (carcassSeconds < 1) {
                carcassSeconds = 1;
            }
            if (channelSeconds < 0) {
                channelSeconds = 0;
            }
            secondary = List.copyOf(secondary);
        }

        public long carcassMillis() {
            return carcassSeconds * 1000L;
        }

        public long channelMillis() {
            return channelSeconds * 1000L;
        }
    }

    public static final Codec<CreatureProfile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Category.CODEC.optionalFieldOf("categorie", Category.AUTRE).forGetter(CreatureProfile::category),
            Rarity.CODEC.optionalFieldOf("rarete", Rarity.COMMUNE).forGetter(CreatureProfile::rarity),
            HunterEntry.CODEC.optionalFieldOf("chasseur").forGetter(CreatureProfile::hunter),
            SkinningEntry.CODEC.optionalFieldOf("depecage").forGetter(CreatureProfile::skinning),
            DropEntry.CODEC.optionalFieldOf("viande").forGetter(CreatureProfile::meat),
            DropEntry.CODEC.listOf().optionalFieldOf("loot_combat", List.of())
                    .forGetter(CreatureProfile::combatLoot),
            SpawnOrigin.CODEC.listOf().optionalFieldOf("origines_xp", SpawnOrigin.DEFAUT)
                    .forGetter(CreatureProfile::xpOrigins),
            Codec.STRING.optionalFieldOf("commentaire").forGetter(CreatureProfile::comment)
    ).apply(instance, CreatureProfile::new));

    public CreatureProfile {
        combatLoot = List.copyOf(combatLoot);
        xpOrigins = xpOrigins.isEmpty() ? List.copyOf(SpawnOrigin.DEFAUT) : List.copyOf(xpOrigins);
    }

    public boolean isHuntable() {
        return hunter.isPresent();
    }

    public boolean isSkinnable() {
        return skinning.isPresent();
    }

    /** Cette origine d'apparition donne-t-elle droit à l'XP de métier ? */
    public boolean grantsXpFrom(SpawnOrigin origin) {
        return xpOrigins.contains(origin);
    }
}
