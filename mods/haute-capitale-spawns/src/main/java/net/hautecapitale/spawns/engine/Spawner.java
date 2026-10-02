package net.hautecapitale.spawns.engine;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.hautecapitale.spawns.HauteCapitaleSpawns;
import net.hautecapitale.spawns.config.SpawnsConfig;
import net.hautecapitale.spawns.data.Resolved;
import net.hautecapitale.spawns.data.SpawnPoint;
import net.hautecapitale.spawns.store.PointRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Fait apparaitre le mob d'un point, et le decore : marque persistante,
 * etiquettes de commande, persistance, zone de marche, nom.
 *
 * <p>La creation passe par {@code EntityType.create} + {@code MobEntity.initialize},
 * comme une apparition naturelle : le mod d'origine du mob applique son equipement
 * et ses attributs. Le gestionnaire ne touche a rien d'autre.
 */
public final class Spawner {

    /** Racine des etiquettes de commande : {@code hcspawn}, {@code hcspawn.zone.<z>}, ... */
    public static final String TAG_ROOT = "hcspawn";

    private Spawner() {
    }

    public record Result(Optional<LivingEntity> entity, String error) {
        static Result ok(LivingEntity entity) {
            return new Result(Optional.of(entity), "");
        }

        static Result fail(String error) {
            return new Result(Optional.empty(), error);
        }
    }

    public static boolean entityTypeExists(Identifier id) {
        return id != null && Registries.ENTITY_TYPE.containsId(id);
    }

    /**
     * Cree et decore le mob <b>sans l'ajouter au monde</b>. L'appelant enregistre
     * d'abord l'UUID et le jeton dans l'etat du point, puis appelle {@link #place} :
     * l'ajout au monde declenche aussitot l'evenement de chargement d'entite, et le
     * point doit deja reconnaitre son jeton a ce moment-la (sinon il prendrait son
     * propre mob pour un doublon).
     */
    public static Result prepare(ServerWorld world, PointRef ref, Resolved resolved, UUID token, SpawnsConfig config) {
        Identifier id = resolved.entity();
        if (!entityTypeExists(id)) {
            return Result.fail("type d'entite inconnu : " + id);
        }
        EntityType<?> type = Registries.ENTITY_TYPE.get(id);
        SpawnPoint point = ref.point();
        Entity created;
        try {
            created = type.create(world, SpawnReason.EVENT);
        } catch (Throwable t) {
            return Result.fail("creation impossible (" + t + ")");
        }
        if (created == null) {
            return Result.fail("le type " + id + " refuse d'etre cree");
        }
        if (!(created instanceof LivingEntity living)) {
            return Result.fail(id + " n'est pas une creature vivante");
        }
        living.refreshPositionAndAngles(point.x(), point.y(), point.z(), point.yaw(), point.pitch());
        if (living instanceof MobEntity mob && resolved.initialize()) {
            try {
                mob.initialize(world, world.getLocalDifficulty(mob.getBlockPos()), SpawnReason.EVENT, null);
            } catch (Throwable t) {
                HauteCapitaleSpawns.LOGGER.warn("{} : initialize() de {} a echoue, mob cree sans : {}", ref.fullId(), id, t.toString());
            }
        }
        if (resolved.nbt().isPresent()) {
            try {
                NbtCompound nbt = StringNbtReader.readCompound(resolved.nbt().get());
                living.readData(NbtReadView.create(ErrorReporter.EMPTY, world.getRegistryManager(), nbt));
                living.refreshPositionAndAngles(point.x(), point.y(), point.z(), point.yaw(), point.pitch());
            } catch (CommandSyntaxException e) {
                return Result.fail("NBT invalide : " + e.getMessage());
            } catch (Throwable t) {
                return Result.fail("NBT refuse par l'entite : " + t);
            }
        }
        decorate(living, ref, resolved, token, config);
        return Result.ok(living);
    }

    /** Ajoute au monde un mob prepare ; vide si reussi, sinon le message d'erreur. */
    public static Optional<String> place(ServerWorld world, LivingEntity entity) {
        boolean added;
        try {
            added = world.spawnNewEntityAndPassengers(entity);
        } catch (Throwable t) {
            return Optional.of("ajout au monde impossible (" + t + ")");
        }
        return added ? Optional.empty() : Optional.of("le monde a refuse l'entite");
    }

    /** Tout ce qui distingue un mob controle d'un mob quelconque du meme type. */
    public static void decorate(LivingEntity entity, PointRef ref, Resolved resolved, UUID token, SpawnsConfig config) {
        if (entity instanceof MobEntity mob) {
            mob.setPersistent();
            mob.setHeadYaw(ref.point().yaw());
            mob.setBodyYaw(ref.point().yaw());
            applyHome(mob, ref, resolved);
        }
        applyName(entity, resolved);
        applyStats(entity, resolved, config, true);
        for (String tag : tagsFor(ref, resolved, config)) {
            entity.addCommandTag(tag);
        }
        ControlledMarker.mark(entity, new ControlledMarker(ref.zone().id(), ref.point().id(), token));
    }

    /** Re-applique ce qui n'est pas sauvegarde avec l'entite (zone de marche, persistance) et les reglages edites. */
    public static void reapply(LivingEntity entity, PointRef ref, Resolved resolved, SpawnsConfig config) {
        if (entity instanceof MobEntity mob) {
            mob.setPersistent();
            applyHome(mob, ref, resolved);
        }
        applyStats(entity, resolved, config, false);
        applyName(entity, resolved);
        List<String> wanted = tagsFor(ref, resolved, config);
        List<String> stale = new ArrayList<>();
        for (String tag : entity.getCommandTags()) {
            if ((tag.startsWith(TAG_ROOT + ".level.") || tag.startsWith(TAG_ROOT + ".rank.")) && !wanted.contains(tag)) {
                stale.add(tag);
            }
        }
        stale.forEach(entity::removeCommandTag);
        for (String tag : wanted) {
            if (!entity.getCommandTags().contains(tag)) {
                entity.addCommandTag(tag);
            }
        }
    }

    /**
     * Nom affiche au-dessus du mob. Pose le nom du point s'il y en a un ; a l'inverse,
     * si le point n'en a plus et que le mob porte encore le nom qu'on lui avait donne, on
     * le retire (mais on ne touche jamais a un nom venu d'ailleurs, ex. du mod d'origine).
     */
    public static void applyName(LivingEntity entity, Resolved resolved) {
        if (resolved.customName().isPresent()) {
            entity.setCustomName(Text.literal(resolved.customName().get()));
            entity.setCustomNameVisible(true);
        } else if (entity.getCommandTags().contains(TAG_ROOT + ".named")) {
            entity.setCustomName(null);
            entity.setCustomNameVisible(false);
        }
        if (resolved.customName().isPresent()) {
            entity.addCommandTag(TAG_ROOT + ".named");
        } else {
            entity.removeCommandTag(TAG_ROOT + ".named");
        }
    }

    public static final Identifier LEVEL_HEALTH_MODIFIER = HauteCapitaleSpawns.id("level_health");
    public static final Identifier LEVEL_ATTACK_MODIFIER = HauteCapitaleSpawns.id("level_attack");

    /**
     * Attributs imposes (valeurs de base) puis niveau (modificateurs multiplicatifs, persistants
     * donc sauves avec l'entite, re-poses a l'identique a chaque passage). Idempotent : peut etre
     * rejoue sur un mob vivant apres une modification en jeu ; la vie est alors ajustee en
     * proportion (un mob a moitie reste a moitie), sauf a l'apparition ou elle est mise au maximum.
     */
    public static void applyStats(LivingEntity entity, Resolved resolved, SpawnsConfig config, boolean fresh) {
        float ratio = entity.getMaxHealth() > 0 ? entity.getHealth() / entity.getMaxHealth() : 1.0F;
        for (Map.Entry<String, Double> attribute : resolved.attributes().entrySet()) {
            Identifier id = Identifier.tryParse(attribute.getKey());
            if (id == null) {
                continue;
            }
            var entry = Registries.ATTRIBUTE.getEntry(id);
            if (entry.isEmpty()) {
                HauteCapitaleSpawns.LOGGER.warn("attribut inconnu ignore : {}", id);
                continue;
            }
            EntityAttributeInstance instance = entity.getAttributeInstance(entry.get());
            if (instance == null) {
                HauteCapitaleSpawns.LOGGER.warn("{} n'a pas l'attribut {}", Registries.ENTITY_TYPE.getId(entity.getType()), id);
                continue;
            }
            instance.setBaseValue(attribute.getValue());
        }
        double levelBonus = (resolved.level() - 1) * config.levelHealthFactor;
        levelModifier(entity, EntityAttributes.MAX_HEALTH, LEVEL_HEALTH_MODIFIER, levelBonus);
        double attackBonus = (resolved.level() - 1) * config.levelAttackFactor;
        levelModifier(entity, EntityAttributes.ATTACK_DAMAGE, LEVEL_ATTACK_MODIFIER, attackBonus);
        entity.setHealth(fresh ? entity.getMaxHealth() : Math.max(1.0F, entity.getMaxHealth() * ratio));
    }

    private static void levelModifier(LivingEntity entity, RegistryEntry<EntityAttribute> attribute, Identifier id, double bonus) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        if (instance == null) {
            return;
        }
        instance.removeModifier(id);
        if (bonus > 0) {
            instance.addPersistentModifier(new EntityAttributeModifier(id, bonus, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    public static void applyHome(MobEntity mob, PointRef ref, Resolved resolved) {
        if (resolved.wanderRadius() > 0) {
            mob.setPositionTarget(ref.homePos(), resolved.wanderRadius());
        } else {
            mob.clearPositionTarget();
        }
    }

    public static List<String> tagsFor(PointRef ref, Resolved resolved, SpawnsConfig config) {
        List<String> tags = new ArrayList<>();
        tags.add(TAG_ROOT);
        tags.add(TAG_ROOT + ".zone." + ref.zone().id());
        tags.add(TAG_ROOT + ".point." + ref.zone().id() + "." + ref.point().id());
        tags.add(TAG_ROOT + ".rank." + resolved.rank());
        tags.add(TAG_ROOT + ".level." + resolved.level());
        for (Map.Entry<String, String> tag : resolved.tags().entrySet()) {
            tags.add(TAG_ROOT + ".tag." + tag.getKey() + "." + tag.getValue());
        }
        if (config.extraCommandTags != null) {
            tags.addAll(config.extraCommandTags);
        }
        return tags;
    }

    /** Retire la marque et les etiquettes : l'entite redevient un mob ordinaire. */
    public static void release(Entity entity) {
        ControlledMarker.unmark(entity);
        List<String> toRemove = new ArrayList<>();
        for (String tag : entity.getCommandTags()) {
            if (tag.startsWith(TAG_ROOT)) {
                toRemove.add(tag);
            }
        }
        for (String tag : toRemove) {
            entity.removeCommandTag(tag);
        }
        if (entity instanceof MobEntity mob) {
            mob.clearPositionTarget();
        }
    }
}
