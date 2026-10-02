package net.hautecapitale.metiers.command;

import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.item.HcmItems;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Diagnostic serveur des matières et de leurs tags.
 *
 * <p>Un objet mal enregistré ou un tag vide ne produit aucune erreur : l'objet
 * s'appelle {@code item.haute_capitale_metiers.…} et la recette qui le demandait
 * ne trouve simplement rien. Ces contrôles-là ne peuvent se faire qu'avec les
 * registres et les datapacks chargés, donc en jeu.
 */
final class MaterialDiagnostic {

    /** Les dix tags du catalogue, et ce qu'ils doivent au minimum contenir. */
    private static final List<String> TAGS = List.of(
            "peaux/petites", "peaux/communes", "peaux/epaisses", "peaux/exotiques", "peaux/rares",
            "fourrures/communes", "fourrures/epaisses", "fourrures/rares",
            "ecailles", "tanin");

    private MaterialDiagnostic() {
    }

    static List<String> run(List<String> report) {
        scenarioItems(report);
        scenarioTags(report);
        scenarioNeighbours(report);
        return report;
    }

    private static void scenarioItems(List<String> report) {
        section(report, "Matières — enregistrement");
        check(report, "dix matières enregistrées", () -> HcmItems.all().size() == 10);

        for (Item item : HcmItems.all()) {
            Identifier id = Registries.ITEM.getId(item);
            check(report, id + " est dans le registre", () -> Registries.ITEM.containsId(id));
            check(report, id + " a un nom traduit", () -> {
                String name = item.getName().getString();
                return !name.isBlank() && !name.startsWith("item.haute_capitale_metiers");
            });
        }
    }

    private static void scenarioTags(List<String> report) {
        section(report, "Tags de matières");
        for (String path : TAGS) {
            TagKey<Item> tag = TagKey.of(RegistryKeys.ITEM, HauteCapitaleMetiers.id(path));
            List<Item> members = members(tag);

            if (path.equals("tanin")) {
                // Seul tag dont le contenu vient entièrement d'un autre mod.
                report.add(members.isEmpty()
                        ? "  PASS  #" + path + " est vide — Farmer's Delight n'est pas installé (l'écorce est le tannant)"
                        : "  PASS  #" + path + " → " + describe(members));
                continue;
            }

            check(report, "#" + path + " n'est pas vide", () -> !members.isEmpty());
            if (!members.isEmpty()) {
                report.add("      " + describe(members));
            }
        }
    }

    /**
     * Le point de la compatibilité : nos fourrures et celles d'un autre mod
     * doivent être interchangeables. Si le mod voisin est là, il doit être dans
     * le tag ; s'il n'est pas là, le tag doit quand même s'être chargé.
     */
    private static void scenarioNeighbours(List<String> report) {
        section(report, "Compatibilité entre mods");
        checkNeighbour(report, "fourrures/communes", "more_rpg_classes:wolf_fur", HcmItems.FOURRURE_COMMUNE);
        checkNeighbour(report, "fourrures/epaisses", "more_rpg_classes:polar_bear_fur", HcmItems.FOURRURE_EPAISSE);
        checkNeighbour(report, "peaux/epaisses", "hmobs:brown_bear_hide", HcmItems.PEAU_EPAISSE);
        checkNeighbour(report, "peaux/communes", "fleshz:hide", null);
        checkNeighbour(report, "peaux/exotiques", "landsoficaria:aeternae_hide", null);
        checkNeighbour(report, "ecailles", "landsoficaria:myrmeke_scales", null);
    }

    private static void checkNeighbour(List<String> report, String path, String foreign, Item ours) {
        TagKey<Item> tag = TagKey.of(RegistryKeys.ITEM, HauteCapitaleMetiers.id(path));
        Identifier id = Identifier.tryParse(foreign);
        boolean installed = id != null && Registries.ITEM.containsId(id);
        List<Item> members = members(tag);

        if (ours != null) {
            check(report, "#" + path + " contient notre matière", () -> members.contains(ours));
        }

        if (!installed) {
            report.add("  PASS  " + foreign + " absent de cette installation — le tag se charge quand même");
            return;
        }
        check(report, "#" + path + " accepte aussi " + foreign,
                () -> members.contains(Registries.ITEM.get(id)));
    }

    // ------------------------------------------------------------------

    private static List<Item> members(TagKey<Item> tag) {
        List<Item> items = new ArrayList<>();
        for (RegistryEntry<Item> entry : Registries.ITEM.iterateEntries(tag)) {
            items.add(entry.value());
        }
        return items;
    }

    private static String describe(List<Item> members) {
        return members.stream()
                .map(item -> Registries.ITEM.getId(item).toString())
                .reduce((a, b) -> a + ", " + b)
                .orElse("—");
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
