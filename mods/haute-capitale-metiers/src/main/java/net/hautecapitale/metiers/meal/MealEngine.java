package net.hautecapitale.metiers.meal;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.quality.Quality;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.LongSupplier;

/**
 * Les buffs de repas : un seul à la fois, remplacé par le plat suivant,
 * retiré à la mort.
 *
 * <p>Le buff est un modificateur d'attribut temporaire sur le joueur — jamais
 * sauvegardé par Minecraft, réappliqué à la connexion depuis notre
 * attachement — doublé d'un effet de statut « Repas » qui ne fait rien d'autre
 * que se voir : une icône et un compte à rebours dans l'interface. Boire du
 * lait retire l'effet, et le moteur retire alors le buff avec lui : c'est
 * cohérent, et c'est gratuit.
 */
public final class MealEngine {

    /** L'effet visible. Il ne porte aucun modificateur : c'est le moteur qui les pose. */
    public static final class MealEffect extends StatusEffect {
        MealEffect() {
            super(StatusEffectCategory.BENEFICIAL, 0xE0A040);
        }
    }

    public static final RegistryEntry<StatusEffect> REPAS = Registry.registerReference(
            Registries.STATUS_EFFECT, HauteCapitaleMetiers.id("repas"), new MealEffect());

    /** L'identifiant du modificateur : un seul par joueur, remplacé à chaque repas. */
    public static final Identifier MODIFIER_ID = HauteCapitaleMetiers.id("repas");

    private static LongSupplier clock = System::currentTimeMillis;

    private MealEngine() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % 20 != 0) {
                return;
            }
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                tick(player);
            }
        });
        // Mourir retire le repas : l'attachement n'est pas copié à la réapparition,
        // et les modificateurs temporaires meurent avec l'entité. Reste l'effet
        // visible, que Minecraft efface aussi. Rien à faire ici que le dire.
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof ServerPlayerEntity player) {
                clear(player);
            }
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> restore(handler.getPlayer()));
    }

    /** Pour le diagnostic : une horloge que l'on peut avancer. */
    public static void setClock(LongSupplier supplier) {
        clock = supplier == null ? System::currentTimeMillis : supplier;
    }

    public static long now() {
        return clock.getAsLong();
    }

    // ------------------------------------------------------------------

    /**
     * Le joueur vient de manger un plat cuisiné : le buff qu'il porte remplace
     * celui en cours. Excellent renforce d'un tiers.
     */
    public static boolean eat(ServerPlayerEntity player, ItemStack stack, MealConsumeEffect effect) {
        MealBuff buff = HcmData.BUFFS.get(effect.buff());
        if (buff == null) {
            return false;
        }
        double value = effect.value();
        if (Quality.isExcellent(stack)) {
            value *= MetiersConfig.get().repas.excellent_facteur;
        }
        int seconds = buff.seconds().orElse(MetiersConfig.get().repas.duree_secondes);
        apply(player, effect.buff(), value, seconds);
        if (player.networkHandler != null) {
            player.sendMessage(describe(effect, value).formatted(Formatting.GOLD), true);
        }
        return true;
    }

    /** Pose le buff — et retire d'abord celui en cours, quel qu'il soit. */
    public static void apply(ServerPlayerEntity player, Identifier buffId, double value, int seconds) {
        clear(player);
        MealBuff buff = HcmData.BUFFS.get(buffId);
        if (buff == null) {
            return;
        }
        long expiresAt = now() + seconds * 1000L;
        player.setAttached(MealAttachment.REPAS, new MealAttachment(buffId, value, expiresAt));
        addModifier(player, buff, value);
        player.addStatusEffect(new StatusEffectInstance(REPAS, seconds * 20, 0, false, false, true));
    }

    /** Retire le buff en cours, s'il y en a un : modificateur, effet, attachement. */
    public static void clear(ServerPlayerEntity player) {
        MealAttachment current = MealAttachment.of(player);
        if (current != null) {
            MealBuff buff = HcmData.BUFFS.get(current.buff());
            if (buff != null) {
                removeModifier(player, buff);
            }
            player.removeAttached(MealAttachment.REPAS);
        } else {
            // Un modificateur orphelin — fiche disparue entre-temps — ne doit pas rester.
            for (RegistryEntry<EntityAttribute> attribute : Registries.ATTRIBUTE.getIndexedEntries()) {
                EntityAttributeInstance instance = player.getAttributeInstance(attribute);
                if (instance != null && instance.hasModifier(MODIFIER_ID)) {
                    instance.removeModifier(MODIFIER_ID);
                }
            }
        }
        if (player.hasStatusEffect(REPAS)) {
            player.removeStatusEffect(REPAS);
        }
    }

    /** Chaque seconde : l'échéance, ou l'effet visible parti (lait, commande) → fin du repas. */
    public static void tick(ServerPlayerEntity player) {
        MealAttachment current = MealAttachment.of(player);
        if (current == null) {
            return;
        }
        if (current.expired(now()) || !player.hasStatusEffect(REPAS)) {
            clear(player);
        }
    }

    /** À la connexion : le modificateur temporaire n'est pas sauvegardé, on le repose depuis l'attachement. */
    public static void restore(ServerPlayerEntity player) {
        MealAttachment current = MealAttachment.of(player);
        if (current == null) {
            return;
        }
        if (current.expired(now())) {
            clear(player);
            return;
        }
        MealBuff buff = HcmData.BUFFS.get(current.buff());
        if (buff == null) {
            clear(player);
            return;
        }
        addModifier(player, buff, current.value());
        if (!player.hasStatusEffect(REPAS)) {
            player.addStatusEffect(new StatusEffectInstance(REPAS, current.remainingSeconds(now()) * 20, 0, false, false, true));
        }
    }

    private static void addModifier(ServerPlayerEntity player, MealBuff buff, double value) {
        EntityAttributeInstance instance = instance(player, buff);
        if (instance == null) {
            return;
        }
        instance.removeModifier(MODIFIER_ID);
        instance.addTemporaryModifier(new EntityAttributeModifier(MODIFIER_ID, value, buff.operation().vanilla));
    }

    private static void removeModifier(ServerPlayerEntity player, MealBuff buff) {
        EntityAttributeInstance instance = instance(player, buff);
        if (instance != null) {
            instance.removeModifier(MODIFIER_ID);
        }
    }

    /** L'instance d'attribut du joueur, ou {@code null} si l'attribut n'existe pas ici. */
    public static EntityAttributeInstance instance(ServerPlayerEntity player, MealBuff buff) {
        return Registries.ATTRIBUTE.getEntry(buff.attribute())
                .map(player::getAttributeInstance)
                .orElse(null);
    }

    // ------------------------------------------------------------------

    /** Le plat fabriqué par le Cuisinier reçoit son effet, à côté de ceux qu'il a déjà. */
    public static void attach(ItemStack stack, Identifier buffId, MealBuff buff, double value) {
        ConsumableComponent consumable = stack.get(DataComponentTypes.CONSUMABLE);
        if (consumable == null) {
            return;
        }
        List<ConsumeEffect> effects = new ArrayList<>();
        for (ConsumeEffect effect : consumable.onConsumeEffects()) {
            if (!(effect instanceof MealConsumeEffect)) {
                effects.add(effect);
            }
        }
        effects.add(MealConsumeEffect.of(buffId, buff, value, MetiersConfig.get().repas.duree_secondes));
        stack.set(DataComponentTypes.CONSUMABLE, new ConsumableComponent(consumable.consumeSeconds(),
                consumable.useAction(), consumable.sound(), consumable.hasConsumeParticles(), effects));
    }

    /** L'effet de repas porté par une pile, ou {@code null}. */
    public static MealConsumeEffect effectOf(ItemStack stack) {
        ConsumableComponent consumable = stack.get(DataComponentTypes.CONSUMABLE);
        if (consumable == null) {
            return null;
        }
        for (ConsumeEffect effect : consumable.onConsumeEffects()) {
            if (effect instanceof MealConsumeEffect meal) {
                return meal;
            }
        }
        return null;
    }

    /** « Vigueur : +5 % Vie maximale, 15 min ». */
    public static MutableText describe(MealConsumeEffect effect, double value) {
        return describe(effect.title(), effect.attribute(), effect.operation(), value, effect.seconds());
    }

    public static MutableText describe(String title, Identifier attributeId, MealBuff.Operation operation, double value, int seconds) {
        Text attribute = Registries.ATTRIBUTE.getEntry(attributeId)
                .map(entry -> (Text) Text.translatable(entry.value().getTranslationKey()))
                .orElse(Text.literal(attributeId.toString()));
        String amount = operation.isPercent()
                ? String.format(Locale.ROOT, "%+.0f %%", value * 100.0D)
                : String.format(Locale.ROOT, "%+.1f", value);
        return Text.translatableWithFallback("hcm.repas.buff", "%s : %s %s, %s min",
                Text.literal(title), Text.literal(amount), attribute, Text.literal(String.valueOf(seconds / 60)));
    }
}
