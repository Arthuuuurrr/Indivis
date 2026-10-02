package net.hautecapitale.metiers.gadget;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Sel, onguent, baume sacrés : l'effet <em>Repoussant</em>, qui empêche les
 * monstres d'apparaître autour du porteur. Trois paliers — rayon et durée de
 * la configuration —, l'amplificateur de l'effet disant lequel.
 *
 * <p>Comment on repousse : Minecraft décide d'un spawn naturel ou de
 * générateur en initialisant le mob — le mixin d'origine passe par
 * {@link #onInitialize} — ; si un joueur repoussant est à portée, le mob est
 * noté, et retiré dès qu'il entre dans le monde. Rien d'autre n'est touché :
 * ni les œufs, ni les commandes, ni les invocations.
 *
 * <p>Idée reprise de GAG (MIT, MaxNeedsSnacks) ; réécrite pour Fabric.
 */
public class RepellentItem extends Item {

    public enum Kind {
        SEL(0),
        ONGUENT(1),
        BAUME(2);

        final int amplifier;

        Kind(int amplifier) {
            this.amplifier = amplifier;
        }
    }

    /** L'effet lui-même : sans particules, sans tick, il n'est qu'une marque sur le porteur. */
    public static final class RepellingEffect extends StatusEffect {
        RepellingEffect() {
            super(StatusEffectCategory.BENEFICIAL, 0xE8E0C0);
        }
    }

    public static final RegistryEntry<StatusEffect> REPOUSSANT = Registry.registerReference(
            Registries.STATUS_EFFECT, HauteCapitaleMetiers.id("repoussant"), new RepellingEffect());

    /** Les mobs décidés pendant qu'un joueur repoussait : à retirer à l'entrée dans le monde. */
    private static final Set<UUID> REPELLED = new HashSet<>();

    private final Kind kind;

    public RepellentItem(Kind kind, Settings settings) {
        super(settings);
        this.kind = kind;
    }

    public Kind kind() {
        return kind;
    }

    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (REPELLED.remove(entity.getUuid())) {
                entity.discard();
            }
        });
    }

    /** Appelé à l'initialisation d'un mob, avec sa raison d'apparition. */
    public static void onInitialize(MobEntity mob, SpawnReason reason) {
        if (mob.getEntityWorld() instanceof ServerWorld world && shouldRepel(mob, reason, world.getPlayers())) {
            REPELLED.add(mob.getUuid());
        }
    }

    /**
     * Un monstre qui apparaît de lui-même — nature, générateur, patrouille,
     * renfort — à portée d'un joueur protégé est repoussé. Les œufs, commandes
     * et invocations ne le sont jamais : ce sont des actes, pas le hasard.
     */
    public static boolean shouldRepel(MobEntity mob, SpawnReason reason, Iterable<? extends ServerPlayerEntity> players) {
        if (reason != SpawnReason.NATURAL && reason != SpawnReason.SPAWNER && reason != SpawnReason.TRIAL_SPAWNER
                && reason != SpawnReason.PATROL && reason != SpawnReason.REINFORCEMENT) {
            return false;
        }
        if (mob.getType().getSpawnGroup() != SpawnGroup.MONSTER) {
            return false;
        }
        for (ServerPlayerEntity player : players) {
            StatusEffectInstance effect = player.getStatusEffect(REPOUSSANT);
            if (effect == null) {
                continue;
            }
            int radius = radius(effect.getAmplifier());
            if (player.squaredDistanceTo(mob.getEntityPos()) <= (double) radius * radius) {
                return true;
            }
        }
        return false;
    }

    /** Pour le diagnostic : marque un mob comme repoussé, sans passer par l'initialisation. */
    public static void repel(UUID uuid) {
        REPELLED.add(uuid);
    }

    /** Le rayon d'un palier, depuis la configuration. */
    public static int radius(int amplifier) {
        return switch (amplifier) {
            case 0 -> Gadgets.config().sel_rayon;
            case 1 -> Gadgets.config().onguent_rayon;
            default -> Gadgets.config().baume_rayon;
        };
    }

    public static int seconds(int amplifier) {
        return switch (amplifier) {
            case 0 -> Gadgets.config().sel_secondes;
            case 1 -> Gadgets.config().onguent_secondes;
            default -> Gadgets.config().baume_secondes;
        };
    }

    /** Un mob est-il en attente de retrait ? Pour le diagnostic. */
    public static boolean isRepelled(UUID uuid) {
        return REPELLED.contains(uuid);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        }
        int seconds = seconds(kind.amplifier);
        player.addStatusEffect(new StatusEffectInstance(REPOUSSANT, seconds * 20, kind.amplifier, false, false, true));
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_SAND_BREAK, SoundCategory.PLAYERS, 0.8F, 1.4F);
        if (!player.isCreative()) {
            player.getStackInHand(hand).decrement(1);
        }
        Gadgets.overlay(player, Gadgets.text("hcm.gadget.repoussant.applique",
                "Repoussant : aucun monstre n'apparaîtra à moins de %s blocs pendant %s min",
                Text.literal(String.valueOf(radius(kind.amplifier))),
                Text.literal(String.valueOf(seconds / 60))).formatted(Formatting.GREEN));
        return ActionResult.CONSUME;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent display,
                              Consumer<Text> tooltip, TooltipType type) {
        tooltip.accept(Gadgets.text("hcm.gadget.infobulle.repoussant",
                "Empêche les monstres d'apparaître à moins de %s blocs, pendant %s min",
                Text.literal(String.valueOf(radius(kind.amplifier))),
                Text.literal(String.valueOf(seconds(kind.amplifier) / 60))).formatted(Formatting.GRAY));
    }
}
