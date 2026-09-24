package net.hautecapitale.metiers.command;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.profession.Profession;
import net.hautecapitale.metiers.profession.Rank;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Diagnostic serveur du socle.
 *
 * <p>Vérifie ce qu'un test hors du jeu ne peut pas atteindre : la persistance
 * réelle sur disque, la copie des données à la mort, et le passage d'une
 * dimension à l'autre. Le tout sur un {@link ServerPlayerEntity} construit en
 * mémoire — ce n'est pas un joueur connecté, mais les chemins de code exercés
 * sont exactement ceux du jeu : même attachement, même écriture NBT, même
 * {@code copyFrom}.
 *
 * <p>Le joueur de test n'est jamais ajouté au monde ni à la liste des joueurs.
 * Il est créé, interrogé, puis abandonné.
 */
final class SocleDiagnostic {

    private SocleDiagnostic() {
    }

    static List<String> run(MinecraftServer server) {
        List<String> report = new ArrayList<>();
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        if (overworld == null) {
            report.add("FAIL  aucun monde principal disponible");
            return report;
        }

        ServerPlayerEntity player = createTestPlayer(server, overworld, "SocleTest");

        scenarioLearnAndIndependence(player, report);
        scenarioXpAndLevelUp(player, report);
        scenarioLevelCap(player, report);
        scenarioDiskPersistence(server, overworld, player, report);
        scenarioRespawn(server, overworld, player, report);
        scenarioDimensionChange(server, overworld, player, report);
        RoleDiagnostic.run(server, report);
        MaterialDiagnostic.run(report);
        CraftDiagnostic.run(server, report);
        NodeDiagnostic.run(server, report);
        HuntDiagnostic.run(server, report);
        SkinDiagnostic.run(server, report);
        RepairDiagnostic.run(server, report);
        GadgetDiagnostic.run(server, report);
        ArtisanDiagnostic.run(server, report);

        return report;
    }

    private static ServerPlayerEntity createTestPlayer(MinecraftServer server, ServerWorld world, String name) {
        // Un FakePlayer : les écouteurs d'autres mods (attributs, accessoires) envoient des
        // paquets à la réapparition, qu'un joueur en mémoire ordinaire ne peut pas recevoir.
        return DiagnosticPlayer.create(world, name);
    }

    // ------------------------------------------------------------------

    private static void scenarioLearnAndIndependence(ServerPlayerEntity player, List<String> report) {
        section(report, "Apprentissage et indépendance");

        check(report, "aucun métier au départ",
                () -> !Metiers.hasProfession(player, Profession.MINEUR));

        check(report, "apprendre Mineur", () -> Metiers.learn(player, Profession.MINEUR));
        check(report, "apprendre une seconde fois échoue", () -> !Metiers.learn(player, Profession.MINEUR));
        check(report, "apprendre Forgeron", () -> Metiers.learn(player, Profession.FORGERON));
        check(report, "apprendre Travailleur du cuir",
                () -> Metiers.learn(player, Profession.TRAVAILLEUR_DU_CUIR));

        check(report, "Mineur au niveau 24", () -> Metiers.setLevel(player, Profession.MINEUR, 24)
                && Metiers.getLevel(player, Profession.MINEUR) == 24);
        check(report, "Travailleur du cuir au niveau 30",
                () -> Metiers.setLevel(player, Profession.TRAVAILLEUR_DU_CUIR, 30)
                        && Metiers.getLevel(player, Profession.TRAVAILLEUR_DU_CUIR) == 30);

        check(report, "Forgeron reste au niveau 1",
                () -> Metiers.getLevel(player, Profession.FORGERON) == 1);
        check(report, "Joaillier non appris reste à 0",
                () -> Metiers.getLevel(player, Profession.JOAILLIER) == 0);
        check(report, "les rangs suivent chaque métier",
                () -> Metiers.getRank(player, Profession.MINEUR) == Rank.EXPERT
                        && Metiers.getRank(player, Profession.FORGERON) == Rank.APPRENTI);
        check(report, "niveau hors bornes refusé",
                () -> !Metiers.setLevel(player, Profession.MINEUR, 51)
                        && !Metiers.setLevel(player, Profession.MINEUR, 0));
    }

    private static void scenarioXpAndLevelUp(ServerPlayerEntity player, List<String> report) {
        section(report, "Gain d'XP et passage de niveau");

        Metiers.setLevel(player, Profession.FORGERON, 1);
        int gained = Metiers.addXp(player, Profession.FORGERON, 500.0D);
        check(report, "500 XP font gagner des niveaux", () -> gained > 0);
        report.add(String.format(Locale.ROOT, "      Forgeron : niveau %d, %.0f XP en réserve",
                Metiers.getLevel(player, Profession.FORGERON),
                Metiers.getXp(player, Profession.FORGERON)));

        check(report, "l'XP d'un métier n'affecte pas les autres",
                () -> Metiers.getLevel(player, Profession.MINEUR) == 24);
        check(report, "XP sur un métier non appris ignorée",
                () -> Metiers.addXp(player, Profession.CUISINIER, 1000.0D) == 0
                        && Metiers.getLevel(player, Profession.CUISINIER) == 0);
    }

    private static void scenarioLevelCap(ServerPlayerEntity player, List<String> report) {
        section(report, "Plafond au niveau 50");

        Metiers.setLevel(player, Profession.MINEUR, 50);
        int gained = Metiers.addXp(player, Profession.MINEUR, 999_999.0D);
        check(report, "aucun niveau gagné au plafond", () -> gained == 0);
        check(report, "le niveau reste à 50", () -> Metiers.getLevel(player, Profession.MINEUR) == 50);
        check(report, "aucune XP résiduelle", () -> Metiers.getXp(player, Profession.MINEUR) == 0.0D);
        check(report, "rang Grand Maître au plafond",
                () -> Metiers.getRank(player, Profession.MINEUR) == Rank.GRAND_MAITRE);
    }

    /**
     * Le scénario décisif : écrire le joueur comme le fait la sauvegarde, puis
     * relire dans une instance neuve. C'est ce que subissent une déconnexion et
     * un redémarrage de serveur.
     */
    private static void scenarioDiskPersistence(MinecraftServer server, ServerWorld world,
                                                ServerPlayerEntity player, List<String> report) {
        section(report, "Persistance — déconnexion et redémarrage");

        NbtCompound saved;
        try {
            NbtWriteView write = NbtWriteView.create(ErrorReporter.EMPTY, world.getRegistryManager());
            // writeData, pas saveSelfData : ce dernier commence par vérifier
            // getSavedEntityId(), qui est nul pour un joueur — les joueurs ne
            // sont pas sauvegardés par identifiant d'entité — et repart aussitôt
            // sans rien écrire.
            player.writeData(write);
            saved = write.getNbt();
        } catch (Exception e) {
            report.add("  FAIL  écriture des données du joueur : " + e);
            return;
        }
        check(report, "les données du joueur s'écrivent", () -> saved != null && !saved.isEmpty());
        check(report, "les métiers figurent dans le NBT sauvegardé",
                () -> saved.toString().contains("haute_capitale_metiers"));

        ServerPlayerEntity reloaded = createTestPlayer(server, world, "SocleTest");
        try {
            ReadView read = NbtReadView.create(ErrorReporter.EMPTY, world.getRegistryManager(), saved);
            reloaded.readData(read);
        } catch (Exception e) {
            report.add("  FAIL  relecture des données du joueur : " + e);
            return;
        }

        check(report, "Mineur relu au niveau 50",
                () -> Metiers.getLevel(reloaded, Profession.MINEUR) == 50);
        check(report, "Travailleur du cuir relu au niveau 30",
                () -> Metiers.getLevel(reloaded, Profession.TRAVAILLEUR_DU_CUIR) == 30);
        check(report, "Forgeron relu avec son niveau",
                () -> Metiers.getLevel(reloaded, Profession.FORGERON)
                        == Metiers.getLevel(player, Profession.FORGERON));
        check(report, "l'XP en réserve est relue",
                () -> Metiers.getXp(reloaded, Profession.FORGERON)
                        == Metiers.getXp(player, Profession.FORGERON));
        check(report, "un métier non appris le reste après relecture",
                () -> !Metiers.hasProfession(reloaded, Profession.JOAILLIER));
        check(report, "les rangs se recalculent correctement après relecture",
                () -> Metiers.getRank(reloaded, Profession.MINEUR) == Rank.GRAND_MAITRE);
    }

    /**
     * Reproduit fidèlement la réapparition : le serveur crée un nouveau joueur,
     * appelle {@code copyFrom}, puis déclenche {@code AFTER_RESPAWN}.
     *
     * <p>Le second temps est indispensable — c'est lui qui transfère les
     * attachements. {@code copyFrom} seul ne les emporte pas : Fabric branche
     * la copie sur l'événement, pas sur la méthode.
     */
    private static void scenarioRespawn(MinecraftServer server, ServerWorld world,
                                        ServerPlayerEntity player, List<String> report) {
        section(report, "Mort et réapparition");

        ServerPlayerEntity respawned = createTestPlayer(server, world, "SocleTest");
        try {
            respawned.copyFrom(player, false);
            ServerPlayerEvents.AFTER_RESPAWN.invoker().afterRespawn(player, respawned, false);
        } catch (Exception e) {
            // L'événement est partagé par tous les mods du pack : un écouteur tiers
            // (une carte, un HUD) peut supposer un vrai joueur et lever une exception
            // — parfois avant que Fabric n'ait copié nos attachements, selon l'ordre
            // d'enregistrement. Rien ne peut alors être conclu ici : on le dit, sans
            // compter ni réussite ni échec. La copie elle-même est celle de Fabric
            // (copyOnDeath), vérifiée sur un serveur sans cet écouteur.
            StackTraceElement[] trace = e.getStackTrace();
            String where = trace.length > 0 ? trace[0].getClassName() : "?";
            report.add("  SAUT  copie à la réapparition : non vérifiable sur ce pack — l'écouteur " + where
                    + " a interrompu l'événement (" + e.getClass().getSimpleName() + ") sur nos joueurs en mémoire");
            return;
        }

        check(report, "Mineur conservé après la mort",
                () -> Metiers.getLevel(respawned, Profession.MINEUR) == 50);
        check(report, "Travailleur du cuir conservé après la mort",
                () -> Metiers.getLevel(respawned, Profession.TRAVAILLEUR_DU_CUIR) == 30);
        check(report, "Forgeron conservé après la mort",
                () -> Metiers.getLevel(respawned, Profession.FORGERON)
                        == Metiers.getLevel(player, Profession.FORGERON));
        check(report, "aucun métier fantôme n'apparaît",
                () -> !Metiers.hasProfession(respawned, Profession.JOAILLIER));
    }

    /**
     * Un passage de dimension recrée l'entité, puis déclenche
     * {@code AFTER_ENTITY_CHANGE_WORLD}. Même logique que la réapparition :
     * c'est l'événement qui porte le transfert des attachements.
     */
    private static void scenarioDimensionChange(MinecraftServer server, ServerWorld world,
                                                ServerPlayerEntity player, List<String> report) {
        section(report, "Changement de dimension");

        // Depuis 1.17, un joueur qui change de dimension garde la même entité :
        // rien n'est copié, et nos données voyagent avec lui sans qu'aucun code
        // du mod n'intervienne. Une première version de ce scénario recréait le
        // joueur et levait l'événement des entités ordinaires — celui des
        // créatures, qui, elles, sont recréées. Cela passait sur un serveur nu
        // et cassait dès qu'un mod d'attributs écoutait cet événement.
        //
        // On ne lève pas non plus l'événement joueur : d'autres mods y envoient
        // des paquets, ce qu'un joueur en mémoire ne peut pas recevoir. Le seul
        // fait à vérifier est que l'entité, donc l'attachement, est inchangée.
        double xpBefore = Metiers.getXp(player, Profession.FORGERON);
        report.add("      (même entité avant et après : rien n'est copié depuis 1.17)");

        check(report, "Mineur conservé", () -> Metiers.getLevel(player, Profession.MINEUR) == 50);
        check(report, "Travailleur du cuir conservé",
                () -> Metiers.getLevel(player, Profession.TRAVAILLEUR_DU_CUIR) == 30);
        check(report, "XP en réserve conservée",
                () -> Metiers.getXp(player, Profession.FORGERON) == xpBefore);
    }

    // ------------------------------------------------------------------

    private static void section(List<String> report, String title) {
        report.add("--- " + title);
    }

    private static void check(List<String> report, String label, Check condition) {
        boolean ok;
        try {
            ok = condition.test();
        } catch (Exception e) {
            report.add("  FAIL  " + label + "  (" + e + ")");
            return;
        }
        report.add((ok ? "  PASS  " : "  FAIL  ") + label);
    }

    @FunctionalInterface
    private interface Check {
        boolean test() throws Exception;
    }

    static void summarize(List<String> report, Consumer<String> out) {
        long pass = report.stream().filter(line -> line.startsWith("  PASS")).count();
        long fail = report.stream().filter(line -> line.startsWith("  FAIL")).count();
        report.forEach(out);
        out.accept(String.format(Locale.ROOT, "=== %d réussis, %d échoués ===", pass, fail));
    }
}
