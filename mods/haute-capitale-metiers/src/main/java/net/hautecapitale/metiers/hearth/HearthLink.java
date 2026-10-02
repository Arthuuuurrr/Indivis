package net.hautecapitale.metiers.hearth;

import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.gadget.GadgetItems;
import net.hautecapitale.metiers.gadget.Gadgets;
import net.hautecapitale.metiers.npc.NpcRole;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;

/**
 * La liaison du foyer : parler à un aubergiste ajoute son auberge à celles que
 * le joueur connaît et en fait son foyer ; le joueur choisit ensuite, parmi ses
 * auberges, celle où la Pierre le ramène. La première visite offre la Pierre.
 *
 * <p>Pas de bloc, pas de lit, pas de foudre : l'aubergiste est un Easy NPC dont
 * le rôle porte {@code "foyer": true}, et c'est sa position — et son nom — qui
 * sont retenus.
 */
public final class HearthLink {

    /** Ce qu'une visite a changé. */
    public enum Result {
        /** Nouvelle auberge, ou foyer déplacé vers une auberge déjà connue. */
        LIE,
        /** Le joueur avait déjà ce foyer. */
        DEJA_LA
    }

    private HearthLink() {
    }

    private static MetiersConfig.Foyer config() {
        return MetiersConfig.get().foyer;
    }

    /** Lie le joueur à l'auberge de ce rôle, à cet endroit, sous le titre du rôle. */
    public static Result bind(ServerPlayerEntity player, NpcRole role, Vec3d anchor) {
        return bind(player, role, anchor, role.title().orElse(""));
    }

    /**
     * Lie le joueur à l'auberge de ce rôle, à cet endroit.
     *
     * @param anchor où se tient l'aubergiste
     * @param name   le nom de l'auberge — celui du PNJ, d'ordinaire
     */
    public static Result bind(ServerPlayerEntity player, NpcRole role, Vec3d anchor, String name) {
        GlobalPos pos = GlobalPos.create(player.getEntityWorld().getRegistryKey(), BlockPos.ofFloored(anchor));
        String label = name == null || name.isBlank() ? role.title().orElse("") : name;
        HearthAttachment before = HearthAttachment.of(player);
        Result result;
        if (before != null && pos.equals(before.pos())) {
            result = Result.DEJA_LA;
        } else {
            HearthAttachment.Inn inn = new HearthAttachment.Inn(pos, label);
            boolean known = before != null && before.indexOf(pos) >= 0;
            int max = config().auberges_max;
            if (!known && before != null && max > 0 && before.inns().size() >= max) {
                Gadgets.say(player, Gadgets.text("hcm.foyer.plafond",
                        "Vous ne pouvez retenir que %s auberges : la plus ancienne est oubliée",
                        Text.literal(String.valueOf(max))).formatted(Formatting.GRAY));
            }
            HearthAttachment after = before == null ? new HearthAttachment(pos, label) : before.with(inn, max);
            HearthAttachment.set(player, after);
            Gadgets.say(player, (known
                    ? Gadgets.text("hcm.foyer.retour", "Vous voilà de retour : votre foyer est de nouveau %s",
                            Text.literal(describe(inn)).formatted(Formatting.YELLOW))
                    : Gadgets.text("hcm.foyer.lie", "Votre foyer est désormais ici : %s",
                            Text.literal(describe(inn)).formatted(Formatting.YELLOW))).formatted(Formatting.GREEN));
            if (after.inns().size() > 1) {
                Gadgets.say(player, Gadgets.text("hcm.foyer.connues",
                        "%s auberges connues — accroupi + clic droit sur la pierre pour choisir",
                        Text.literal(String.valueOf(after.inns().size()))).formatted(Formatting.GRAY));
            }
            player.getEntityWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN,
                    SoundCategory.PLAYERS, 0.8F, 1.2F);
            result = Result.LIE;
        }
        if (config().pierre_offerte && !hasStone(player)) {
            offerStone(player);
        }
        return result;
    }

    /**
     * Choisit une auberge connue par son numéro, de 1 à n — celui de la liste.
     *
     * @return {@code false} si le numéro ne correspond à rien
     */
    public static boolean choose(ServerPlayerEntity player, int number) {
        HearthAttachment hearth = HearthAttachment.of(player);
        if (hearth == null || number < 1 || number > hearth.inns().size()) {
            Gadgets.say(player, Gadgets.text("hcm.foyer.numero_inconnu", "Aucune auberge n° %s",
                    Text.literal(String.valueOf(number))).formatted(Formatting.RED));
            return false;
        }
        HearthAttachment.set(player, hearth.choose(number - 1));
        HearthAttachment.Inn inn = hearth.inns().get(number - 1);
        Gadgets.say(player, Gadgets.text("hcm.foyer.choisi", "Foyer choisi : %s",
                Text.literal(describe(inn)).formatted(Formatting.YELLOW)).formatted(Formatting.GREEN));
        return true;
    }

    /** La liste des auberges du joueur dans le chat, chacune cliquable pour la choisir. */
    public static void list(ServerPlayerEntity player) {
        HearthAttachment hearth = HearthAttachment.of(player);
        if (hearth == null || hearth.inns().isEmpty()) {
            Gadgets.say(player, Gadgets.text("hcm.foyer.aucune", "Aucun foyer : parlez à un aubergiste pour vous lier")
                    .formatted(Formatting.RED));
            return;
        }
        player.sendMessage(Gadgets.text("hcm.foyer.liste", "Vos auberges (%s) — cliquez sur l'une d'elles pour la choisir :",
                Text.literal(String.valueOf(hearth.inns().size()))).formatted(Formatting.GOLD), false);
        for (int i = 0; i < hearth.inns().size(); i++) {
            HearthAttachment.Inn inn = hearth.inns().get(i);
            boolean current = i == hearth.selected();
            int number = i + 1;
            MutableText line = Text.literal("  " + number + ". ").formatted(Formatting.GRAY)
                    .append(Text.literal(describe(inn)).styled(style -> style
                            .withColor(current ? Formatting.GREEN : Formatting.AQUA)
                            .withUnderline(!current)
                            .withClickEvent(new ClickEvent.RunCommand("/metiers auberge choisir " + number))
                            .withHoverEvent(new HoverEvent.ShowText(
                                    Gadgets.text("hcm.foyer.choisir_survol", "Choisir cette auberge")))));
            if (current) {
                line.append(Text.literal("  ✦ ").formatted(Formatting.GREEN))
                        .append(Gadgets.text("hcm.foyer.actuel", "foyer actuel").formatted(Formatting.GREEN));
            }
            player.sendMessage(line, false);
        }
    }

    /** Le joueur porte-t-il déjà une Pierre, simple ou améliorée ? */
    public static boolean hasStone(ServerPlayerEntity player) {
        for (ItemStack stack : player.getInventory().getMainStacks()) {
            if (stack.getItem() instanceof HearthstoneItem) {
                return true;
            }
        }
        return player.getOffHandStack().getItem() instanceof HearthstoneItem;
    }

    /** Une Pierre de foyer dans le sac — ou aux pieds, si le sac est plein. */
    public static void offerStone(ServerPlayerEntity player) {
        ItemStack stone = new ItemStack(GadgetItems.PIERRE_DE_FOYER);
        if (!player.getInventory().insertStack(stone) || !stone.isEmpty()) {
            player.dropItem(stone, false);
        }
        Gadgets.say(player, Gadgets.text("hcm.foyer.offerte", "L'aubergiste vous confie une Pierre de foyer")
                .formatted(Formatting.GOLD));
    }

    /** « Nom (x y z) », ou « x y z » sans nom — pour dire au joueur où est une auberge. */
    public static String describe(HearthAttachment.Inn inn) {
        return inn.name().isBlank() ? describe(inn.pos()) : inn.name() + " (" + describe(inn.pos()) + ")";
    }

    /** « x y z ». */
    public static String describe(GlobalPos pos) {
        return pos.pos().getX() + " " + pos.pos().getY() + " " + pos.pos().getZ();
    }

    /** La distance qui sépare le joueur d'une auberge, en blocs — ou « autre monde ». */
    public static Text distance(ServerPlayerEntity player, HearthAttachment.Inn inn) {
        if (!inn.pos().dimension().equals(player.getEntityWorld().getRegistryKey())) {
            return Gadgets.text("hcm.foyer.autre_monde", "autre monde");
        }
        int blocks = (int) Math.sqrt(player.squaredDistanceTo(Vec3d.ofCenter(inn.pos().pos())));
        return Gadgets.text("hcm.foyer.blocs", "%s blocs", Text.literal(String.valueOf(blocks)));
    }
}
