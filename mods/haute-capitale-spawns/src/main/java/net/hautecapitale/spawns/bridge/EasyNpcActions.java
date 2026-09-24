package net.hautecapitale.spawns.bridge;

import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.spawns.HauteCapitaleSpawns;
import net.hautecapitale.spawns.api.OpResult;
import net.hautecapitale.spawns.api.Spawns;
import net.hautecapitale.spawns.text.Msg;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Actions personnalisees Easy NPC, pour piloter les spawns depuis un PNJ sans
 * passer par une commande.
 *
 * <p>Dans l'editeur du PNJ : action {@code CUSTOM}, commande
 * {@code haute_capitale_spawns:enable orc_camp_01.chief} (ou {@code disable},
 * {@code reset}, {@code spawn}, {@code zone_enable}, {@code zone_disable},
 * {@code zone_reset}). Le joueur qui clique recoit le compte rendu.
 *
 * <p>Les commandes {@code /mmospawn enable|disable|reset ...} restent utilisables
 * par les actions de type commande (executeAsUser=false, niveau 2).
 */
public final class EasyNpcActions {

    private static final Map<String, Function<String, OpResult>> OPS = Map.of(
            "enable", Spawns::enablePoint,
            "disable", Spawns::disablePoint,
            "reset", Spawns::resetPoint,
            "spawn", Spawns::spawnNow,
            "zone_enable", Spawns::enableZone,
            "zone_disable", Spawns::disableZone,
            "zone_reset", Spawns::resetZone);

    private static boolean registered;

    private EasyNpcActions() {
    }

    public static void init() {
        if (registered || !FabricLoader.getInstance().isModLoaded("easy_npc")) {
            return;
        }
        try {
            Impl.register();
            registered = true;
            HauteCapitaleSpawns.LOGGER.info("Actions Easy NPC enregistrees : {}", String.join(", ", OPS.keySet()));
        } catch (Throwable t) {
            HauteCapitaleSpawns.LOGGER.warn("Enregistrement des actions Easy NPC impossible ; commandes seules : {}", t.toString());
        }
    }

    static void run(String op, ServerPlayerEntity player, List<String> arguments) {
        Function<String, OpResult> function = OPS.get(op);
        if (function == null) {
            return;
        }
        if (arguments.isEmpty()) {
            HauteCapitaleSpawns.LOGGER.warn("Action Easy NPC {} sans argument (identifiant attendu)", op);
            Msg.tell(player, Msg.error("action " + op + " : identifiant manquant"));
            return;
        }
        OpResult result = function.apply(arguments.get(0));
        if (!result.ok()) {
            HauteCapitaleSpawns.LOGGER.warn("Action Easy NPC {} {} : {}", op, arguments.get(0), result.message());
        }
        Msg.tell(player, result.ok() ? Msg.ok(result.message()) : Msg.error(result.message()));
    }

    private static final class Impl {
        static void register() {
            for (String op : OPS.keySet()) {
                Identifier id = HauteCapitaleSpawns.id(op);
                de.markusbordihn.easynpc.api.action.ActionRegistry.register(id,
                        (entry, npc, player, arguments) -> run(op, player, arguments));
            }
        }
    }
}
