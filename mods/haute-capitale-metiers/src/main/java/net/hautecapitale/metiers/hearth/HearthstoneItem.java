package net.hautecapitale.metiers.hearth;

import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.gadget.ChanneledTeleportItem;
import net.hautecapitale.metiers.gadget.Gadgets;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * La Pierre de foyer : elle ramène à l'auberge que le joueur a choisie parmi
 * celles où il s'est lié, après une canalisation, avec un temps de recharge.
 * Accroupi + clic droit : la liste de ses auberges, pour en choisir une autre.
 * La version améliorée — une recette d'Ingénieur — recharge plus vite et porte
 * plus loin.
 *
 * <p>Idée reprise de la pierre énergisée de GAG (MIT, MaxNeedsSnacks) ; la
 * liaison par la foudre y est remplacée par l'aubergiste, et le code est
 * réécrit pour Fabric.
 */
public class HearthstoneItem extends ChanneledTeleportItem {

    private final boolean improved;

    public HearthstoneItem(boolean improved, Settings settings) {
        super(settings);
        this.improved = improved;
    }

    public boolean improved() {
        return improved;
    }

    private static MetiersConfig.Foyer config() {
        return MetiersConfig.get().foyer;
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (player.isSneaking()) {
            // Choisir plutôt que partir : la liste des auberges, cliquable.
            if (player instanceof ServerPlayerEntity serverPlayer) {
                HearthLink.list(serverPlayer);
            }
            return ActionResult.CONSUME;
        }
        return super.use(world, player, hand);
    }

    @Override
    protected GlobalPos destination(ServerPlayerEntity player, ItemStack stack) {
        HearthAttachment hearth = HearthAttachment.of(player);
        return hearth == null ? null : hearth.pos();
    }

    @Override
    protected int channelSeconds() {
        return config().canalisation_secondes;
    }

    @Override
    protected int cooldownSeconds() {
        return improved ? config().amelioree_recharge_secondes : config().recharge_secondes;
    }

    @Override
    protected double range(ItemStack stack) {
        double range = config().portee;
        return improved ? range * config().amelioree_portee_facteur : range;
    }

    @Override
    protected boolean crossesDimensions() {
        return config().autre_dimension;
    }

    @Override
    protected String messageKey() {
        return "hcm.foyer";
    }

    @Override
    public Text refusalText(Refusal refusal) {
        return switch (refusal) {
            case AUCUNE_DESTINATION -> Gadgets.text("hcm.foyer.aucune",
                    "Aucun foyer : parlez à un aubergiste pour vous lier").formatted(Formatting.RED);
            case AUTRE_DIMENSION -> Gadgets.text("hcm.foyer.dimension",
                    "Votre foyer est dans un autre monde").formatted(Formatting.RED);
            case TROP_LOIN -> Gadgets.text("hcm.foyer.trop_faible",
                    "La pierre est trop faible pour vous ramener d'ici").formatted(Formatting.RED);
        };
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent display,
                              Consumer<Text> tooltip, TooltipType type) {
        tooltip.accept(Gadgets.text("hcm.foyer.infobulle", "Maintenez le clic droit pour rentrer à votre auberge")
                .formatted(Formatting.GRAY));
        tooltip.accept(Gadgets.text("hcm.foyer.infobulle_choix", "Accroupi + clic droit : choisir une autre auberge")
                .formatted(Formatting.GRAY));
        tooltip.accept(Gadgets.text("hcm.foyer.infobulle_recharge", "Recharge : %s min",
                Text.literal(String.valueOf(cooldownSeconds() / 60))).formatted(Formatting.DARK_GRAY));
    }
}
