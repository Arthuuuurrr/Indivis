package net.hautecapitale.metiers.npc;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.craft.CraftEngine;
import net.hautecapitale.metiers.craft.CraftRecipe;
import net.hautecapitale.metiers.craft.Mastery;
import net.hautecapitale.metiers.craft.MasteryAttachment;
import net.hautecapitale.metiers.craft.XpFalloff;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.gadget.Gadgets;
import net.hautecapitale.metiers.hearth.HearthAttachment;
import net.hautecapitale.metiers.hearth.HearthLink;
import net.hautecapitale.metiers.profession.Profession;
import net.hautecapitale.metiers.repair.RepairData;
import net.hautecapitale.metiers.repair.RepairEngine;
import net.hautecapitale.metiers.profession.Rank;
import net.hautecapitale.metiers.profession.XpCurve;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Le seul chemin d'entrée dans une interface de métier.
 *
 * <p>Un clic sur un Easy NPC et la commande de repli aboutissent ici, ce qui
 * garantit qu'ils se comportent exactement pareil : mêmes contrôles, mêmes
 * messages, même écran. Si l'API d'Easy NPC changeait un jour, seule la façade
 * serait à refaire — pas l'interface.
 */
public final class RoleGate {

    private RoleGate() {
    }

    /**
     * Ouvre l'écran du rôle demandé.
     *
     * @param anchor position du PNJ, ou {@code null} pour une ouverture sans
     *               ancrage (commande d'administration)
     * @return {@code false} si le rôle est inconnu — le joueur a déjà été prévenu
     */
    public static boolean open(ServerPlayerEntity player, Identifier roleId, Vec3d anchor) {
        return open(player, roleId, anchor, null);
    }

    /**
     * @param npcName le nom du PNJ, qui nomme l'auberge chez un aubergiste ;
     *                {@code null} ou vide, c'est le titre du rôle qui sert
     */
    public static boolean open(ServerPlayerEntity player, Identifier roleId, Vec3d anchor, String npcName) {
        NpcRole role = HcmData.ROLES.get(roleId);
        if (role == null) {
            reportUnknownRole(player, roleId);
            return false;
        }

        if (role.hearth()) {
            // L'aubergiste lie le foyer avant d'ouvrir son registre. Sans ancrage —
            // commande d'administration — c'est la position du joueur qui sert.
            HearthLink.bind(player, role, anchor != null ? anchor : player.getEntityPos(),
                    npcName == null || npcName.isBlank() ? role.title().orElse("") : npcName);
        }
        ProfessionScreenData data = build(player, roleId, role);
        player.openHandledScreen(new Factory(data, anchor));
        return true;
    }

    /**
     * Renvoie au joueur un écran à jour, sans le refermer.
     *
     * <p>Utilisé après un apprentissage : rouvrir l'écran ferait un clignotement
     * et remettrait la liste au début.
     */
    public static void refresh(ServerPlayerEntity player) {
        if (!(player.currentScreenHandler instanceof ProfessionMenu menu)) {
            return;
        }
        NpcRole role = HcmData.ROLES.get(menu.role());
        if (role == null) {
            player.closeHandledScreen();
            return;
        }
        ProfessionNetwork.sendUpdate(player, build(player, menu.role(), role));
    }

    /** Ce que le joueur voit quand un PNJ porte un rôle qui n'existe pas. */
    public static void reportUnknownRole(ServerPlayerEntity player, Identifier roleId) {
        tell(player, Text.translatableWithFallback(
                "hcm.role.inconnu",
                "Ce personnage a un rôle que le serveur ne connaît pas (%s). Prévenez un administrateur.",
                Text.literal(String.valueOf(roleId)).formatted(Formatting.WHITE))
                .formatted(Formatting.RED));
    }

    /**
     * Parle au joueur s'il y a quelqu'un au bout du fil.
     *
     * <p>Un joueur peut avoir perdu sa connexion entre le clic et la réponse ; et
     * le diagnostic serveur travaille sur des joueurs en mémoire, qui n'en ont
     * jamais eu. Dans les deux cas, un message perdu ne doit pas lever
     * d'exception au milieu d'une action.
     */
    public static void tell(ServerPlayerEntity player, Text message) {
        if (player.networkHandler == null) {
            return;
        }
        player.sendMessage(message, false);
    }

    // ------------------------------------------------------------------

    public static ProfessionScreenData build(ServerPlayerEntity player, Identifier roleId, NpcRole role) {
        Text title = title(roleId, role);
        Optional<Text> greeting = role.greeting().map(Text::literal);

        List<ProfessionEntry> entries = new ArrayList<>();
        boolean offersLearning = false;

        switch (role.screen()) {
            case ATELIER -> {
                Profession profession = role.profession().orElseThrow();
                entries.add(progressLine(player, profession));
                entries.addAll(recipeLines(player, profession));
            }
            case FORMATION -> {
                Profession profession = role.profession().orElseThrow();
                entries.add(progressLine(player, profession));
                offersLearning = role.canTeach() && !Metiers.hasProfession(player, profession);
            }
            case REGISTRE -> {
                for (Profession profession : Profession.values()) {
                    entries.add(progressLine(player, profession));
                }
                if (role.hearth()) {
                    entries.addAll(innLines(player));
                }
            }
        }

        // La réparation ne regarde pas le métier : tout joueur y a droit, chez
        // tout rôle qui la propose.
        // Un joueur qui n'exerce pas le métier de l'atelier vient pour la réparation :
        // l'écran s'ouvre sur cet onglet.
        boolean suggestRepair = role.profession().map(profession -> !Metiers.hasProfession(player, profession)).orElse(true);
        Optional<RepairData> repair = role.repairs() ? Optional.of(RepairEngine.describe(player, suggestRepair)) : Optional.empty();

        return new ProfessionScreenData(roleId, role.screen(), title, greeting, offersLearning,
                MetiersConfig.get().maitrise_acquise, entries, repair);
    }

    /**
     * Chez l'aubergiste, les auberges que le joueur connaît : une ligne chacune,
     * avec sa distance, et un bouton « Choisir » — sauf pour le foyer actuel,
     * marqué. L'action {@code foyer/<indice>} revient par le même chemin qu'une
     * recette, et le serveur revérifie que l'écran est bien celui d'un aubergiste.
     */
    private static List<ProfessionEntry> innLines(ServerPlayerEntity player) {
        List<ProfessionEntry> lines = new ArrayList<>();
        HearthAttachment hearth = HearthAttachment.of(player);
        if (hearth == null) {
            return lines;
        }
        for (int i = 0; i < hearth.inns().size(); i++) {
            HearthAttachment.Inn inn = hearth.inns().get(i);
            boolean current = i == hearth.selected();
            MutableText detail = Text.literal(HearthLink.describe(inn.pos()) + " · ").append(HearthLink.distance(player, inn));
            if (current) {
                detail.append(Text.literal(" · ✦ ")).append(Gadgets.text("hcm.foyer.actuel", "foyer actuel"));
            }
            lines.add(new ProfessionEntry(
                    Gadgets.text("hcm.foyer.ligne", "Auberge : %s",
                            Text.literal(inn.name().isBlank() ? HearthLink.describe(inn.pos()) : inn.name())),
                    detail, 0, !current, Optional.of(HauteCapitaleMetiers.id("foyer/" + i)), 0, 0, 1));
        }
        return lines;
    }

    /** Une ligne « où en est le joueur » pour un métier donné. */
    private static ProfessionEntry progressLine(ServerPlayerEntity player, Profession profession) {
        Text label = Text.translatable(profession.getTranslationKey());
        if (!Metiers.hasProfession(player, profession)) {
            return new ProfessionEntry(
                    label,
                    Text.translatableWithFallback("hcm.metier.non_appris", "Métier non appris"),
                    0, false);
        }

        int level = Metiers.getLevel(player, profession);
        Rank rank = Metiers.getRank(player, profession);
        // « 15 / 75 XP » : l'XP acquise sur le coût entier du niveau, comme
        // « /metiers voir » — pas sur ce qui reste, qui donnait « 15 / 60 ».
        double required = XpCurve.xpToNextLevel(level);
        String progress = Double.isInfinite(required)
                ? "niveau maximum"
                : String.format(Locale.ROOT, "%.0f / %.0f XP",
                        Metiers.getXp(player, profession), required);

        Text detail = Text.translatableWithFallback(
                "hcm.metier.progression", "Niveau %s · %s · %s",
                Text.literal(String.valueOf(level)),
                Text.translatable(rank.getTranslationKey()),
                Text.literal(progress));

        return new ProfessionEntry(label, detail, level, true);
    }

    /**
     * Une ligne par recette du métier, toutes niveaux confondus : celles hors de
     * portée s'affichent grisées avec leur niveau, pour que le joueur sache ce
     * qui l'attend. Ce que dit chaque ligne est calculé ici, une fois, avec
     * l'inventaire réel du joueur ; le client n'a rien à compter.
     */
    private static List<ProfessionEntry> recipeLines(ServerPlayerEntity player, Profession profession) {
        List<ProfessionEntry> lines = new ArrayList<>();
        int playerLevel = Metiers.getLevel(player, profession);

        for (Map.Entry<Identifier, CraftRecipe> entry : HcmData.RECIPES.all().entrySet()) {
            CraftRecipe recipe = entry.getValue();
            if (recipe.profession() != profession) {
                continue;
            }
            Identifier id = entry.getKey();
            int crafts = MasteryAttachment.crafts(player, id);
            Mastery mastery = Mastery.of(crafts);
            boolean available = CraftEngine.check(player, id, 1) == null;

            lines.add(new ProfessionEntry(
                    recipeTitle(recipe),
                    recipeDetail(player, recipe, playerLevel),
                    recipe.level(),
                    available,
                    Optional.of(id),
                    mastery.ordinal(),
                    crafts,
                    mastery == Mastery.MASTERED ? 10 : 1));
        }
        return lines;
    }

    private static Text recipeTitle(CraftRecipe recipe) {
        if (recipe.title().isPresent()) {
            return Text.literal(recipe.title().get());
        }
        Identifier item = recipe.result().item();
        return Registries.ITEM.containsId(item)
                ? Registries.ITEM.get(item).getName().copy()
                : Text.literal(item.toString());
    }

    /**
     * « Peau commune 5/3 · Écorce 0/1 · 2 Martins · +39 XP (100 %) » — ce qu'il
     * faut, ce qu'on a, ce que ça coûte, ce que ça rapporte à ce niveau.
     */
    private static Text recipeDetail(ServerPlayerEntity player, CraftRecipe recipe, int playerLevel) {
        MutableText detail = Text.empty();
        PlayerInventory inventory = player.getInventory();

        boolean first = true;
        if (playerLevel < recipe.level()) {
            // Une recette hors de portée dit d'abord ce qu'elle attend.
            detail.append(Text.translatableWithFallback("hcm.recette.niveau_requis", "Niveau %s requis",
                    Text.literal(String.valueOf(recipe.level()))).formatted(Formatting.RED));
            first = false;
        }
        for (CraftRecipe.Ingredient ingredient : recipe.ingredients()) {
            if (!first) {
                detail.append(" · ");
            }
            first = false;
            int have = CraftEngine.count(inventory, ingredient);
            detail.append(ingredientName(ingredient))
                    .append(Text.literal(" " + Math.min(have, 99) + "/" + ingredient.count())
                            .formatted(have >= ingredient.count() ? Formatting.GREEN : Formatting.RED));
        }

        if (recipe.cost() > 0) {
            Item currency = CraftEngine.currency();
            int have = currency == null ? 0 : CraftEngine.count(inventory, currency);
            detail.append(" · ").append(Text.literal(recipe.cost() + " M")
                    .formatted(have >= recipe.cost() ? Formatting.GOLD : Formatting.RED));
        }

        if (playerLevel >= recipe.level()) {
            double xp = XpFalloff.apply(recipe.xp(), playerLevel, recipe.level());
            detail.append(Text.literal(String.format(Locale.ROOT, " · +%.0f XP (%s)",
                    xp, XpFalloff.describe(playerLevel, recipe.level())))
                    .formatted(xp > 0.0D ? Formatting.AQUA : Formatting.DARK_GRAY));
        }
        return detail;
    }

    /**
     * Le nom d'un ingrédient : celui du fichier s'il en donne un, sinon celui du
     * premier objet de la famille, sinon l'identifiant. Jamais une clé brute.
     */
    static Text ingredientName(CraftRecipe.Ingredient ingredient) {
        if (ingredient.name().isPresent()) {
            return Text.literal(ingredient.name().get());
        }
        if (ingredient.item().isPresent()) {
            Identifier id = ingredient.item().get();
            return Registries.ITEM.containsId(id)
                    ? Registries.ITEM.get(id).getName().copy()
                    : Text.literal(id.toString());
        }
        var tag = ingredient.tag().orElseThrow();
        var members = Registries.ITEM.iterateEntries(tag).iterator();
        if (members.hasNext()) {
            return members.next().value().getName().copy();
        }
        return Text.literal("#" + tag.id().getPath());
    }

    /**
     * Le titre affiché en haut de l'écran. Le fichier de rôle gagne toujours ;
     * sinon on retombe sur une traduction, et pour finir sur l'identifiant mis en
     * forme — jamais sur une clé de traduction brute.
     */
    private static Text title(Identifier roleId, NpcRole role) {
        if (role.title().isPresent()) {
            return Text.literal(role.title().get());
        }
        String fallback = role.profession()
                .map(profession -> (Text) Text.translatable(profession.getTranslationKey()))
                .map(Text::getString)
                .orElseGet(() -> pretty(roleId.getPath()));
        return Text.translatableWithFallback(
                "hcm.role." + roleId.getNamespace() + "." + roleId.getPath(), fallback);
    }

    private static String pretty(String path) {
        String text = path.replace('_', ' ');
        return text.isEmpty() ? text : Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    // ------------------------------------------------------------------

    /** Ce que Minecraft demande pour ouvrir un écran : un titre et un menu. */
    private record Factory(ProfessionScreenData data, Vec3d anchor)
            implements ExtendedScreenHandlerFactory<ProfessionScreenData> {

        @Override
        public ProfessionScreenData getScreenOpeningData(ServerPlayerEntity player) {
            return data;
        }

        @Override
        public Text getDisplayName() {
            return data.title();
        }

        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory inventory, net.minecraft.entity.player.PlayerEntity player) {
            return anchor == null
                    ? ProfessionMenu.anywhere(syncId, data)
                    : new ProfessionMenu(syncId, data, anchor);
        }
    }
}
