package net.hautecapitale.party.party;

import com.mojang.serialization.Codec;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

import java.util.Locale;
import java.util.Optional;

/**
 * Role d'un membre dans un groupe.
 *
 * <p>Affichage seul pour l'instant : aucune composition n'est imposee a l'entree
 * d'un donjon (regle §26). Un groupe de cinq DPS est parfaitement valide. Le jour
 * ou un contenu voudra imposer une composition, il lira ces valeurs sans que le
 * modele change.
 */
public enum Role implements StringIdentifiable {
    UNSET("aucun", "—", Formatting.GRAY),
    TANK("tank", "■", Formatting.GOLD),
    HEAL("heal", "✚", Formatting.GREEN),
    DPS("dps", "⚔", Formatting.RED);

    /** Codec tolerant : un role inconnu redevient UNSET plutot que de casser la sauvegarde. */
    public static final Codec<Role> CODEC = Codec.STRING.xmap(
            name -> parse(name).orElse(UNSET),
            Role::asString);

    private final String id;
    private final String symbol;
    private final Formatting color;

    Role(String id, String symbol, Formatting color) {
        this.id = id;
        this.symbol = symbol;
        this.color = color;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public String symbol() {
        return this.symbol;
    }

    public Formatting color() {
        return this.color;
    }

    /** Libelle affiche a l'ecran. */
    public String label() {
        return switch (this) {
            case UNSET -> "aucun";
            case TANK -> "Tank";
            case HEAL -> "Soigneur";
            case DPS -> "DPS";
        };
    }

    public static Optional<Role> parse(String name) {
        if (name == null) {
            return Optional.empty();
        }
        String lower = name.toLowerCase(Locale.ROOT);
        for (Role role : values()) {
            if (role.id.equals(lower)) {
                return Optional.of(role);
            }
        }
        return Optional.empty();
    }
}
