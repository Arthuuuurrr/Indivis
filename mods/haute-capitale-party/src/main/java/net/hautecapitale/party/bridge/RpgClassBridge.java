package net.hautecapitale.party.bridge;

import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.party.HauteCapitaleParty;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Locale;
import java.util.Map;

/**
 * Le seul endroit qui connaisse le noyau RPG.
 *
 * <p>Dependance douce : le noyau est en {@code modCompileOnly}, et ce pont ne
 * l'appelle que s'il est charge. Il se coupe de lui-meme a la premiere erreur de
 * liaison — une signature qui aurait change dans une version future du noyau —
 * plutot que de faire tomber l'envoi du HUD a chaque tick.
 *
 * <p>Le libelle est construit ici, cote serveur, pour que le client n'ait rien a
 * savoir des classes : si le noyau en ajoute une, seul ce fichier change.
 */
public final class RpgClassBridge {

    private static final boolean PRESENT = FabricLoader.getInstance().isModLoaded("haute_capitale_rpg");
    private static volatile boolean broken = false;

    /** Libelles courts, pour tenir sur une ligne de HUD. Repli : l'identifiant capitalise. */
    private static final Map<String, String> LABELS = Map.ofEntries(
            Map.entry("sorcier", "Sorcier"),
            Map.entry("sorcier_elementaire", "Élémentaliste"),
            Map.entry("sorceleur", "Sorceleur"),
            Map.entry("paladin", "Paladin"),
            Map.entry("pretre", "Prêtre"),
            Map.entry("chevalier_de_la_mort", "Chev. de la mort"),
            Map.entry("berserker", "Berserker"),
            Map.entry("voleur", "Voleur"),
            Map.entry("forcemaster", "Forcemaster"),
            Map.entry("chasseur", "Chasseur"),
            Map.entry("barde", "Barde"));

    private RpgClassBridge() {
    }

    public static boolean present() {
        return PRESENT && !broken;
    }

    /** Le libelle de la classe du joueur, ou une chaine vide s'il n'en a pas ou si rien ne le sait. */
    public static String classLabel(ServerPlayerEntity player) {
        if (!present()) {
            return "";
        }
        try {
            return Impl.classLabel(player);
        } catch (Throwable throwable) {
            broken = true;
            HauteCapitaleParty.LOGGER.warn(
                    "Le noyau RPG ne repond plus comme attendu ({}). La classe ne sera plus affichee dans le HUD.",
                    throwable.toString());
            return "";
        }
    }

    static String labelOf(String classId) {
        if (classId == null || classId.isBlank()) {
            return "";
        }
        String known = LABELS.get(classId);
        if (known != null) {
            return known;
        }
        String cleaned = classId.replace('_', ' ');
        return cleaned.substring(0, 1).toUpperCase(Locale.ROOT) + cleaned.substring(1);
    }

    /**
     * Isole dans une classe imbriquee pour que les references au noyau ne soient
     * resolues qu'a son premier appel, jamais au chargement de ce pont.
     */
    private static final class Impl {
        private Impl() {
        }

        static String classLabel(ServerPlayerEntity player) {
            return net.hautecapitale.rpg.api.Classes.of(player)
                    .map(rpgClass -> labelOf(rpgClass.getId()))
                    .orElse("");
        }
    }
}
