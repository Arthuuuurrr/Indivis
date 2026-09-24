package net.hautecapitale.metiers.hearth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.GlobalPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Les auberges d'un joueur : toutes celles où il s'est lié, dans l'ordre de la
 * première visite, et celle qu'il a choisie — c'est là que sa Pierre de foyer
 * le ramène.
 *
 * <p>Attaché au joueur, sauvegardé, gardé à la mort, envoyé au client (pour
 * l'infobulle de la pierre). Une auberge s'ajoute en parlant à un aubergiste —
 * un Easy NPC dont le rôle porte {@code "foyer": true} — et se choisit chez
 * n'importe quel aubergiste, ou accroupi + clic droit sur la pierre.
 *
 * <p>Les sauvegardes d'avant la b9 portaient un foyer unique ({@code position},
 * {@code nom}) : il est relu comme une liste d'une seule auberge.
 *
 * @param inns     les auberges connues
 * @param selected l'indice de l'auberge choisie dans {@code inns}
 */
public record HearthAttachment(List<Inn> inns, int selected) {

    /** Une auberge : où se tenait l'aubergiste, et le nom qu'il portait. */
    public record Inn(GlobalPos pos, String name) {
        public static final Codec<Inn> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                GlobalPos.CODEC.fieldOf("position").forGetter(Inn::pos),
                Codec.STRING.optionalFieldOf("nom", "").forGetter(Inn::name)
        ).apply(instance, Inn::new));
    }

    /** La forme écrite : la liste et le choix — ou, dans une vieille sauvegarde, le foyer unique. */
    private record Saved(List<Inn> inns, int selected, Optional<GlobalPos> legacyPos, String legacyName) {
        static final Codec<Saved> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Inn.CODEC.listOf().optionalFieldOf("auberges", List.of()).forGetter(Saved::inns),
                Codec.INT.optionalFieldOf("choisie", 0).forGetter(Saved::selected),
                GlobalPos.CODEC.optionalFieldOf("position").forGetter(Saved::legacyPos),
                Codec.STRING.optionalFieldOf("nom", "").forGetter(Saved::legacyName)
        ).apply(instance, Saved::new));

        HearthAttachment read() {
            if (inns.isEmpty() && legacyPos.isPresent()) {
                return new HearthAttachment(legacyPos.get(), legacyName);
            }
            return new HearthAttachment(inns, selected);
        }
    }

    public static final Codec<HearthAttachment> CODEC = Saved.CODEC.xmap(Saved::read,
            hearth -> new Saved(hearth.inns(), hearth.selected(), Optional.empty(), ""));

    public static final AttachmentType<HearthAttachment> FOYER =
            AttachmentRegistry.create(HauteCapitaleMetiers.id("foyer"), builder -> builder
                    .persistent(CODEC)
                    .copyOnDeath()
                    .syncWith(PacketCodecs.registryCodec(CODEC), AttachmentSyncPredicate.targetOnly()));

    public HearthAttachment {
        inns = List.copyOf(inns);
        selected = inns.isEmpty() ? 0 : Math.clamp(selected, 0, inns.size() - 1);
    }

    /** Une seule auberge, choisie. */
    public HearthAttachment(GlobalPos pos, String name) {
        this(List.of(new Inn(pos, name)), 0);
    }

    public static void init() {
        // La constante ci-dessus s'enregistre en se chargeant.
    }

    public static HearthAttachment of(ServerPlayerEntity player) {
        return player.getAttached(FOYER);
    }

    public static void set(ServerPlayerEntity player, HearthAttachment hearth) {
        player.setAttached(FOYER, hearth);
    }

    public static void clear(ServerPlayerEntity player) {
        player.removeAttached(FOYER);
    }

    // ------------------------------------------------------------------

    /** L'auberge choisie, ou {@code null} s'il n'y en a aucune. */
    public Inn current() {
        return inns.isEmpty() ? null : inns.get(selected);
    }

    /** Où la pierre ramène — l'auberge choisie. */
    public GlobalPos pos() {
        Inn current = current();
        return current == null ? null : current.pos();
    }

    /** Le nom de l'auberge choisie, ou vide. */
    public String name() {
        Inn current = current();
        return current == null ? "" : current.name();
    }

    /** L'indice de l'auberge à cet endroit, ou −1. */
    public int indexOf(GlobalPos pos) {
        for (int i = 0; i < inns.size(); i++) {
            if (inns.get(i).pos().equals(pos)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Cette auberge, choisie : ajoutée si elle est nouvelle — au-delà de
     * {@code max} auberges (0 = sans limite), la plus ancienne est oubliée.
     */
    public HearthAttachment with(Inn inn, int max) {
        int known = indexOf(inn.pos());
        if (known >= 0) {
            List<Inn> updated = new ArrayList<>(inns);
            updated.set(known, inn);
            return new HearthAttachment(updated, known);
        }
        List<Inn> updated = new ArrayList<>(inns);
        while (max > 0 && updated.size() >= max) {
            updated.remove(0);
        }
        updated.add(inn);
        return new HearthAttachment(updated, updated.size() - 1);
    }

    /** L'auberge d'indice {@code index}, choisie. */
    public HearthAttachment choose(int index) {
        return new HearthAttachment(inns, index);
    }
}
