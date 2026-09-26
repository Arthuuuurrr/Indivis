package net.hautecapitale.metiers.command;

import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.npc.EasyNpcBridge;
import net.hautecapitale.metiers.npc.NpcRole;
import net.hautecapitale.metiers.npc.ProfessionEntry;
import net.hautecapitale.metiers.npc.ProfessionScreenData;
import net.hautecapitale.metiers.npc.RoleGate;
import net.hautecapitale.metiers.profession.Profession;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

/**
 * Diagnostic serveur des rôles de PNJ.
 *
 * <p>Ce que cette classe vérifie ne peut pas l'être hors du jeu : la
 * construction de l'écran demande un vrai joueur et les registres chargés. Elle
 * n'ouvre aucune interface — elle fabrique exactement les données que le serveur
 * enverrait, et les inspecte.
 *
 * <p>Le point le plus important est l'isolation : deux joueurs devant le même
 * PNJ doivent recevoir deux écrans différents. C'est ce qui distingue une
 * interface MMO d'un menu partagé, et c'est invisible tant qu'on ne teste qu'à
 * un seul joueur.
 */
final class RoleDiagnostic {

    private RoleDiagnostic() {
    }

    static List<String> run(MinecraftServer server, List<String> report) {
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        if (overworld == null) {
            report.add("FAIL  aucun monde principal disponible");
            return report;
        }

        ServerPlayerEntity novice = testPlayer(server, overworld, "RoleNovice");
        ServerPlayerEntity expert = testPlayer(server, overworld, "RoleExpert");
        Metiers.learn(expert, Profession.FORGERON);
        Metiers.setLevel(expert, Profession.FORGERON, 24);

        scenarioRoles(report);
        scenarioWorkshop(novice, expert, report);
        scenarioTraining(novice, expert, report);
        scenarioRegistry(novice, expert, report);
        scenarioUnknownRole(novice, report);

        return report;
    }

    private static ServerPlayerEntity testPlayer(MinecraftServer server, ServerWorld world, String name) {
        // Un FakePlayer : d'autres mods du pack envoient des paquets au joueur qui frappe, mange ou
        // réapparaît (zones musicales, attributs, accessoires) — un joueur en mémoire ordinaire ne peut pas les recevoir.
        return DiagnosticPlayer.create(world, name);
    }

    // ------------------------------------------------------------------

    private static void scenarioRoles(List<String> report) {
        section(report, "Rôles chargés");
        check(report, "les dix-neuf rôles prévus sont là", () -> HcmData.ROLES.size() == 19);
        // Un maître par métier, et il enseigne : sans lui, un métier ne s'apprend
        // que par commande — les ateliers ne forment jamais.
        for (Profession profession : Profession.values()) {
            check(report, "maître pour " + profession.getId(), () -> HcmData.ROLES.all().values().stream()
                    .anyMatch(role -> role.screen() == NpcRole.Interface.FORMATION && role.canTeach()
                            && role.profession().orElse(null) == profession));
        }
        check(report, "aucun problème au chargement des rôles",
                () -> HcmData.ROLES.report().isClean());
        check(report, "l'action Easy NPC est enregistrée", EasyNpcBridge::isActive);

        for (Profession profession : Profession.values()) {
            if (profession.getKind() != Profession.Kind.ARTISANAT) {
                continue;
            }
            check(report, "atelier pour " + profession.getId(), () -> HcmData.ROLES.all().values().stream()
                    .anyMatch(role -> role.screen() == NpcRole.Interface.ATELIER
                            && role.profession().orElse(null) == profession));
        }
    }

    private static void scenarioWorkshop(ServerPlayerEntity novice, ServerPlayerEntity expert,
                                         List<String> report) {
        section(report, "Atelier — deux joueurs, deux écrans");
        Identifier id = role("forgeron");

        ProfessionScreenData forNovice = build(novice, id);
        ProfessionScreenData forExpert = build(expert, id);

        check(report, "le titre vient du fichier de rôle",
                () -> forExpert.title().getString().equals("Forge"));
        check(report, "l'atelier n'enseigne pas", () -> !forExpert.offersLearning());

        report.add("      débutant : « " + line(forNovice, 0).detail().getString()
                + " », accessible = " + line(forNovice, 0).available());
        check(report, "le débutant voit son métier non appris",
                () -> !line(forNovice, 0).available() && line(forNovice, 0).requiredLevel() == 0);
        check(report, "l'expert voit son niveau",
                () -> line(forExpert, 0).requiredLevel() == 24 && line(forExpert, 0).available());
        check(report, "les deux écrans diffèrent",
                () -> !line(forNovice, 0).detail().getString()
                        .equals(line(forExpert, 0).detail().getString()));
        // Depuis l'étape 9, la forge a ses recettes : la progression, puis une ligne
        // par recette chargée sur cette installation.
        long recipes = HcmData.RECIPES.all().values().stream()
                .filter(recipe -> recipe.profession() == Profession.FORGERON).count();
        check(report, "la progression, puis une ligne par recette de forge chargée (" + recipes + ")",
                () -> forExpert.entries().size() == 1 + recipes);
        check(report, "l'onglet Réparation accompagne la forge, débutant ou expert",
                () -> forNovice.repair().isPresent() && forExpert.repair().isPresent());
    }

    private static void scenarioTraining(ServerPlayerEntity novice, ServerPlayerEntity expert,
                                         List<String> report) {
        section(report, "Formation — apprendre auprès d'un maître");
        Identifier id = role("maitre_mineur");

        check(report, "le maître propose d'apprendre à qui ne sait pas",
                () -> build(novice, id).offersLearning());

        Metiers.learn(novice, Profession.MINEUR);
        check(report, "il ne le propose plus une fois le métier appris",
                () -> !build(novice, id).offersLearning());
        check(report, "et l'écran montre alors la progression",
                () -> build(novice, id).entries().get(0).available());

        check(report, "un maître qui n'enseigne pas ne propose rien",
                () -> HcmData.ROLES.all().values().stream()
                        .filter(role -> !role.canTeach())
                        .noneMatch(role -> role.screen() == NpcRole.Interface.FORMATION
                                && role.canTeach()));

        Metiers.forget(novice, Profession.MINEUR);
    }

    private static void scenarioRegistry(ServerPlayerEntity novice, ServerPlayerEntity expert,
                                         List<String> report) {
        section(report, "Registre — la vue d'ensemble");
        Identifier id = role("aubergiste");

        ProfessionScreenData forExpert = build(expert, id);
        check(report, "les onze métiers sont listés", () -> forExpert.entries().size() == 11);
        check(report, "un seul est accessible pour cet expert",
                () -> forExpert.entries().stream().filter(ProfessionEntry::available).count() == 1);
        check(report, "le registre n'enseigne pas", () -> !forExpert.offersLearning());
        check(report, "le débutant voit onze métiers, aucun appris",
                () -> build(novice, id).entries().stream().noneMatch(ProfessionEntry::available));
    }

    private static void scenarioUnknownRole(ServerPlayerEntity player, List<String> report) {
        section(report, "Rôle inconnu");
        Identifier ghost = role("marchand_de_tapis");
        check(report, "le registre ne connaît pas ce rôle", () -> HcmData.ROLES.get(ghost) == null);
        check(report, "ouvrir un rôle inconnu échoue proprement",
                () -> !RoleGate.open(player, ghost, null));
    }

    // ------------------------------------------------------------------

    private static Identifier role(String path) {
        return Identifier.of("haute_capitale_metiers", path);
    }

    private static ProfessionScreenData build(ServerPlayerEntity player, Identifier id) {
        return RoleGate.build(player, id, HcmData.ROLES.get(id));
    }

    private static ProfessionEntry line(ProfessionScreenData data, int index) {
        return data.entries().get(index);
    }

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
}
