package net.hautecapitale.metiers.repair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.component.type.WeaponComponent;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.Optional;

/**
 * La marque « brisé » d'un objet, posée à la place de sa destruction.
 *
 * <p>Elle garde ce que l'objet faisait avant de se briser — ses modificateurs
 * d'attribut, son outil, son arme, sa parade — pour le lui rendre à la
 * réparation. Tant qu'elle est là, l'objet ne sert à rien : il ne frappe, ne
 * creuse, ne protège ni ne pare. Il reste dans l'inventaire, avec un point de
 * durabilité, et attend un forgeron.
 *
 * @param attributes les modificateurs d'attribut d'avant, s'il y en avait
 * @param tool       l'outil d'avant
 * @param weapon     l'arme d'avant
 * @param blocks     la parade d'avant (boucliers)
 */
public record BrokenComponent(
        Optional<AttributeModifiersComponent> attributes,
        Optional<ToolComponent> tool,
        Optional<WeaponComponent> weapon,
        Optional<BlocksAttacksComponent> blocks
) {

    public static final Codec<BrokenComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AttributeModifiersComponent.CODEC.optionalFieldOf("attributs").forGetter(BrokenComponent::attributes),
            ToolComponent.CODEC.optionalFieldOf("outil").forGetter(BrokenComponent::tool),
            WeaponComponent.CODEC.optionalFieldOf("arme").forGetter(BrokenComponent::weapon),
            BlocksAttacksComponent.CODEC.optionalFieldOf("parade").forGetter(BrokenComponent::blocks)
    ).apply(instance, BrokenComponent::new));

    /** {@code haute_capitale_metiers:brise} — synchronisé au client, pour l'infobulle. */
    public static final ComponentType<BrokenComponent> TYPE = Registry.register(Registries.DATA_COMPONENT_TYPE,
            HauteCapitaleMetiers.id("brise"),
            ComponentType.<BrokenComponent>builder()
                    .codec(CODEC)
                    .packetCodec(PacketCodecs.registryCodec(CODEC))
                    .build());

    public static void init() {
        // La constante ci-dessus s'enregistre en se chargeant.
    }
}
