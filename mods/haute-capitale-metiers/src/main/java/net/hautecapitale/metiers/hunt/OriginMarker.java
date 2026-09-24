package net.hautecapitale.metiers.hunt;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.creature.CreatureEnums.SpawnOrigin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;

/**
 * Qui décide de l'origine d'une créature, et quand.
 *
 * <p>Trois sources, par ordre de priorité :
 * <ol>
 *   <li><b>Une étiquette de commande</b> {@code hcm_origine_<origine>} sur
 *       l'entité — {@code /summon … {Tags:["hcm_origine_spawn_mmo"]}}. C'est le
 *       contrat offert aux systèmes du MMO et aux administrateurs : déclarer
 *       une origine sans une ligne de code. Relue à chaque fois, elle l'emporte
 *       toujours.</li>
 *   <li><b>La raison d'apparition de Minecraft</b>, captée à l'initialisation
 *       du mob (naturelle, générateur, œuf, commande…). C'est le seul moment
 *       où le jeu la connaît ; elle n'est écrite nulle part ensuite.</li>
 *   <li><b>Une déduction au chargement</b>, pour ce qui n'est jamais passé par
 *       l'initialisation : un petit né d'une reproduction est un élevage ; le
 *       reste — typiquement la faune d'avant l'installation du mod — reçoit
 *       l'origine par défaut de la configuration.</li>
 * </ol>
 *
 * <p>Une origine écrite n'est jamais réécrite par une source de priorité plus
 * faible : un mob de générateur qui se recharge reste un mob de générateur.
 */
public final class OriginMarker {

    /** Préfixe des étiquettes de commande : {@code hcm_origine_naturelle}, etc. */
    public static final String TAG_PREFIX = "hcm_origine_";

    private OriginMarker() {
    }

    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(entity instanceof MobEntity mob)) {
                return;
            }
            SpawnOrigin tagged = fromCommandTags(mob);
            if (tagged != null) {
                OriginAttachment.mark(mob, tagged);
                return;
            }
            if (OriginAttachment.isMarked(mob)) {
                return;
            }
            // Jamais initialisé par Minecraft : un petit issu d'une reproduction
            // (AnimalEntity.breed n'appelle pas initialize), ou une entité qui
            // existait avant le mod.
            boolean bredBaby = mob instanceof PassiveEntity passive && passive.isBaby();
            OriginAttachment.mark(mob, bredBaby ? SpawnOrigin.ELEVAGE : MetiersConfig.get().defaultOrigin());
        });

        // Un zombie noyé reste ce qu'était le zombie ; un villageois zombifié
        // reste ce qu'était le villageois.
        ServerLivingEntityEvents.MOB_CONVERSION.register((previous, converted, context) -> {
            if (OriginAttachment.isMarked(previous)) {
                OriginAttachment.mark(converted, OriginAttachment.of(previous));
            }
        });
    }

    /**
     * Appelé par le mixin, à l'initialisation du mob — avant qu'il soit ajouté
     * au monde. La raison d'apparition n'existe qu'ici.
     */
    public static void onInitialize(MobEntity mob, SpawnReason reason) {
        if (mob.getEntityWorld() == null || mob.getEntityWorld().isClient() || OriginAttachment.isMarked(mob)) {
            return;
        }
        SpawnOrigin origin = fromReason(reason);
        if (origin != null) {
            OriginAttachment.mark(mob, origin);
        }
    }

    /** La raison de Minecraft → notre vocabulaire ; {@code null} = ne rien décider ici. */
    public static SpawnOrigin fromReason(SpawnReason reason) {
        if (reason == null) {
            return null;
        }
        return switch (reason) {
            case NATURAL, CHUNK_GENERATION, STRUCTURE, PATROL, EVENT, TRIGGERED, REINFORCEMENT, JOCKEY ->
                    SpawnOrigin.NATURELLE;
            case SPAWNER, TRIAL_SPAWNER -> SpawnOrigin.SPAWNER;
            case BREEDING -> SpawnOrigin.ELEVAGE;
            case SPAWN_ITEM_USE, DISPENSER, BUCKET -> SpawnOrigin.OEUF;
            case COMMAND -> SpawnOrigin.COMMANDE;
            case MOB_SUMMONED -> SpawnOrigin.INVOCATION;
            // Conversion : l'origine se copie depuis l'entité d'avant (voir init).
            // Chargement, changement de dimension : rien de neuf n'apparaît.
            case CONVERSION, LOAD, DIMENSION_TRAVEL -> null;
        };
    }

    /** L'origine déclarée par une étiquette de commande, ou {@code null}. */
    public static SpawnOrigin fromCommandTags(Entity entity) {
        for (String tag : entity.getCommandTags()) {
            if (tag.startsWith(TAG_PREFIX)) {
                SpawnOrigin origin = byId(tag.substring(TAG_PREFIX.length()));
                if (origin != null) {
                    return origin;
                }
            }
        }
        return null;
    }

    public static SpawnOrigin byId(String id) {
        for (SpawnOrigin origin : SpawnOrigin.values()) {
            if (origin.asString().equals(id)) {
                return origin;
            }
        }
        return null;
    }

    /** L'origine effective d'une entité : l'étiquette si elle existe, sinon la marque. */
    public static SpawnOrigin resolve(Entity entity) {
        SpawnOrigin tagged = fromCommandTags(entity);
        return tagged != null ? tagged : OriginAttachment.of(entity);
    }
}
