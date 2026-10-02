package net.hautecapitale.metiers.meal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * L'effet « à l'ingestion » que le Cuisinier attache au plat qu'il fabrique.
 *
 * <p>C'est le mécanisme de Minecraft lui-même — la liste
 * {@code on_consume_effects} du composant {@code consumable} — étendu d'un type
 * à nous. Aucun mixin : le plat cuisiné par le PNJ porte l'effet, le même plat
 * sorti d'une marmite ne le porte pas.
 *
 * <p>L'effet transporte de quoi s'afficher — attribut, opération, durée, titre —
 * parce que le client n'a pas les fiches de buff du serveur. À l'ingestion, c'est
 * la fiche du serveur qui fait foi ; ces champs ne servent qu'à l'infobulle.
 *
 * @param buff      la fiche de buff
 * @param value     la valeur du buff pour ce plat (Normal ; Excellent renforce)
 * @param attribute l'attribut, pour l'infobulle
 * @param operation l'opération, pour l'infobulle
 * @param seconds   la durée, pour l'infobulle
 * @param title     le nom du buff, pour l'infobulle
 */
public record MealConsumeEffect(Identifier buff, double value, Identifier attribute, MealBuff.Operation operation,
                                int seconds, String title) implements ConsumeEffect {

    public static final MapCodec<MealConsumeEffect> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("buff").forGetter(MealConsumeEffect::buff),
            Codec.DOUBLE.fieldOf("valeur").forGetter(MealConsumeEffect::value),
            Identifier.CODEC.fieldOf("attribut").forGetter(MealConsumeEffect::attribute),
            MealBuff.Operation.CODEC.fieldOf("operation").forGetter(MealConsumeEffect::operation),
            Codec.INT.fieldOf("duree").forGetter(MealConsumeEffect::seconds),
            Codec.STRING.fieldOf("titre").forGetter(MealConsumeEffect::title)
    ).apply(instance, MealConsumeEffect::new));

    public static final PacketCodec<RegistryByteBuf, MealConsumeEffect> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, MealConsumeEffect::buff,
            PacketCodecs.DOUBLE, MealConsumeEffect::value,
            Identifier.PACKET_CODEC, MealConsumeEffect::attribute,
            PacketCodecs.codec(MealBuff.Operation.CODEC), MealConsumeEffect::operation,
            PacketCodecs.VAR_INT, MealConsumeEffect::seconds,
            PacketCodecs.STRING, MealConsumeEffect::title,
            MealConsumeEffect::new);

    public static final ConsumeEffect.Type<MealConsumeEffect> TYPE = Registry.register(Registries.CONSUME_EFFECT_TYPE,
            HauteCapitaleMetiers.id("repas"), new ConsumeEffect.Type<>(MAP_CODEC, PACKET_CODEC));

    public static void init() {
        // La constante ci-dessus s'enregistre en se chargeant.
    }

    /** L'effet d'un plat, d'après la fiche de buff et la valeur choisie pour ce plat. */
    public static MealConsumeEffect of(Identifier buffId, MealBuff buff, double value, int defaultSeconds) {
        return new MealConsumeEffect(buffId, value, buff.attribute(), buff.operation(),
                buff.seconds().orElse(defaultSeconds), buff.title().orElse(buffId.getPath()));
    }

    @Override
    public ConsumeEffect.Type<MealConsumeEffect> getType() {
        return TYPE;
    }

    @Override
    public boolean onConsume(World world, ItemStack stack, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) {
            return MealEngine.eat(player, stack, this);
        }
        return false;
    }
}
