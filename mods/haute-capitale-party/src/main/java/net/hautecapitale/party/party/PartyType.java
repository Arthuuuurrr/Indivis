package net.hautecapitale.party.party;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

import java.util.Locale;
import java.util.Optional;

/**
 * Nature d'un groupe.
 *
 * <p>Le systeme est ecrit pour les donjons a cinq, mais rien dans le modele ne
 * suppose le chiffre cinq : la taille vient d'ici (regle §1). Ajouter un contenu
 * a dix joueurs se fait en ajoutant une constante, pas en touchant au reste.
 */
public enum PartyType implements StringIdentifiable {
    /** Donjon standard : cinq joueurs, ni plus ni moins a l'entree (regle §25). */
    DUNGEON_5("dungeon_5", "Donjon", 5),
    RAID("raid", "Raid", 10),
    EVENT("event", "Evenement", 20);

    /** Codec tolerant : un type inconnu redevient DUNGEON_5 plutot que de perdre le groupe. */
    public static final Codec<PartyType> CODEC = Codec.STRING.xmap(
            name -> parse(name).orElse(DUNGEON_5),
            PartyType::asString);

    private final String id;
    private final String label;
    private final int maxSize;

    PartyType(String id, String label, int maxSize) {
        this.id = id;
        this.label = label;
        this.maxSize = maxSize;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public String label() {
        return this.label;
    }

    /** Plafond dur du groupe. La config peut le baisser, jamais le depasser. */
    public int maxSize() {
        return this.maxSize;
    }

    public static Optional<PartyType> parse(String name) {
        if (name == null) {
            return Optional.empty();
        }
        String lower = name.toLowerCase(Locale.ROOT);
        for (PartyType type : values()) {
            if (type.id.equals(lower)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
