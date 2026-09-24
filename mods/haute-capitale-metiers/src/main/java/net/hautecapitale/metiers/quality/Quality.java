package net.hautecapitale.metiers.quality;

import com.mojang.serialization.Codec;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.util.Vocabulary;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

/**
 * La qualité d'un objet fabriqué : Normal, ou Excellent.
 *
 * <p>Un composant de données sur la pile, jamais un second objet : un ragoût
 * excellent reste un ragoût, avec le même identifiant, mais les deux piles ne se
 * mélangent pas. Seul Excellent est écrit — Normal, c'est l'absence de marque.
 * L'idée vient de Weaver's Paradise ; l'échelle 0-10 y est ramenée à deux
 * valeurs, et la chance dérive du niveau de l'artisan, pas d'un mini-jeu.
 */
public enum Quality implements StringIdentifiable {
    NORMAL("normal"),
    EXCELLENT("excellent");

    public static final Codec<Quality> CODEC = Vocabulary.of("qualite", Quality::values);

    /** Le composant, présent seulement sur les objets Excellent. */
    public static final ComponentType<Quality> COMPONENT = Registry.register(Registries.DATA_COMPONENT_TYPE,
            HauteCapitaleMetiers.id("qualite"), ComponentType.<Quality>builder()
                    .codec(CODEC)
                    .packetCodec(PacketCodecs.codec(CODEC))
                    .build());

    private final String id;

    Quality(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return id;
    }

    public static void init() {
        // La constante ci-dessus s'enregistre en se chargeant.
    }

    public static Quality of(ItemStack stack) {
        Quality quality = stack.get(COMPONENT);
        return quality == null ? NORMAL : quality;
    }

    public static boolean isExcellent(ItemStack stack) {
        return of(stack) == EXCELLENT;
    }

    /**
     * Marque la pile Excellent : le composant, et le nom en couleur — le même
     * nom, traduit comme d'habitude, simplement doré.
     */
    public static void markExcellent(ItemStack stack) {
        stack.set(COMPONENT, EXCELLENT);
        stack.set(net.minecraft.component.DataComponentTypes.ITEM_NAME,
                Text.translatable(stack.getItem().getTranslationKey()).formatted(Formatting.GOLD));
    }

    /** La ligne d'infobulle. */
    public static Text label() {
        return Text.translatableWithFallback("hcm.qualite.excellent", "✦ Excellent").formatted(Formatting.GOLD);
    }
}
