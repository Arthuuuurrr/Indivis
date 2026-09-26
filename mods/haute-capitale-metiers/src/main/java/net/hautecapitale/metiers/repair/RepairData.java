package net.hautecapitale.metiers.repair;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.List;

/**
 * L'onglet Réparation, tel que le serveur l'envoie : le solde du joueur, et
 * une ligne par objet réparable de son inventaire avec son prix. Le client
 * n'y ajoute rien : il affiche, et il clique.
 *
 * @param balance ce que le joueur a en Martins
 * @param currencyName le nom de la monnaie, pour l'afficher
 * @param items   les objets réparables, dans l'ordre de l'inventaire
 * @param total   le prix de « tout réparer »
 * @param suggested ouvrir l'écran sur cet onglet : le joueur n'exerce pas le métier de l'atelier,
 *                  c'est la réparation qu'il vient chercher
 */
public record RepairData(int balance, Text currencyName, List<RepairEntry> items, int total, boolean suggested) {

    public static final int MAX_ITEMS = 64;

    /**
     * Un objet réparable.
     *
     * @param slot      son emplacement dans l'inventaire — ce que le clic renvoie
     * @param label     son nom
     * @param damage    sa durabilité perdue
     * @param maxDamage sa durabilité totale
     * @param broken    est-il brisé ?
     * @param price     son prix, en Martins
     */
    public record RepairEntry(int slot, Text label, int damage, int maxDamage, boolean broken, int price) {
        public static final PacketCodec<RegistryByteBuf, RepairEntry> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.VAR_INT, RepairEntry::slot,
                TextCodecs.REGISTRY_PACKET_CODEC, RepairEntry::label,
                PacketCodecs.VAR_INT, RepairEntry::damage,
                PacketCodecs.VAR_INT, RepairEntry::maxDamage,
                PacketCodecs.BOOLEAN, RepairEntry::broken,
                PacketCodecs.VAR_INT, RepairEntry::price,
                RepairEntry::new);

        /** 0 à 100. */
        public int percent() {
            return maxDamage <= 0 ? 100 : (int) Math.round(100.0D * (maxDamage - damage) / maxDamage);
        }
    }

    public static final PacketCodec<RegistryByteBuf, RepairData> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, RepairData::balance,
            TextCodecs.REGISTRY_PACKET_CODEC, RepairData::currencyName,
            RepairEntry.PACKET_CODEC.collect(PacketCodecs.toList(MAX_ITEMS)), RepairData::items,
            PacketCodecs.VAR_INT, RepairData::total,
            PacketCodecs.BOOLEAN, RepairData::suggested,
            RepairData::new);

    public RepairData {
        items = items.size() > MAX_ITEMS ? List.copyOf(items.subList(0, MAX_ITEMS)) : List.copyOf(items);
    }
}
