package net.hautecapitale.metiers.node;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.data.HcmData;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/**
 * L'outil de l'administrateur : une baguette liée à un type de node.
 *
 * <p>Clic droit sur un bloc → un node de ce type y est posé. Clic droit sur un
 * node → sa fiche. Accroupi + clic droit sur un node → il est retiré. Le type
 * lié voyage avec l'objet, dans ses données : un administrateur peut garder une
 * baguette par type.
 *
 * <p>Réservé aux joueurs en créatif : un joueur qui tomberait sur l'objet ne
 * pourrait rien en faire.
 */
public final class NodeTool {

    public static final Identifier ID = HauteCapitaleMetiers.id("outil_node");
    public static final Item ITEM = register();

    private static final String TYPE_KEY = "hcm_node_type";

    private NodeTool() {
    }

    private static Item register() {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, ID);
        return Registry.register(Registries.ITEM, key, new Item(new Item.Settings().registryKey(key).maxCount(1)));
    }

    public static void init() {
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (!(player instanceof ServerPlayerEntity serverPlayer) || !(world instanceof ServerWorld serverWorld)) {
                return ActionResult.PASS;
            }
            ItemStack held = player.getStackInHand(hand);
            if (!held.isOf(ITEM)) {
                return ActionResult.PASS;
            }
            if (!player.isCreative()) {
                serverPlayer.sendMessage(Text.translatableWithFallback("hcm.outil.creatif",
                        "L'outil de node ne s'utilise qu'en mode créatif.").formatted(Formatting.RED), false);
                return ActionResult.FAIL;
            }
            use(serverPlayer, serverWorld, held, hit.getBlockPos());
            return ActionResult.SUCCESS;
        });
    }

    /** Une baguette neuve, liée au type donné. */
    public static ItemStack create(Identifier type) {
        ItemStack stack = new ItemStack(ITEM);
        bind(stack, type);
        return stack;
    }

    public static void bind(ItemStack stack, Identifier type) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString(TYPE_KEY, type.toString());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    /** Le type lié à cette baguette, ou {@code null}. */
    public static Identifier boundType(ItemStack stack) {
        NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (data == null) {
            return null;
        }
        return data.copyNbt().getString(TYPE_KEY).map(Identifier::tryParse).orElse(null);
    }

    // ------------------------------------------------------------------

    private static void use(ServerPlayerEntity player, ServerWorld world, ItemStack wand, BlockPos pos) {
        NodeStore store = NodeAttachment.of(world);

        if (player.isSneaking()) {
            if (NodeEngine.remove(world, pos)) {
                say(player, Text.literal("Node retiré en " + pos.toShortString() + ".").formatted(Formatting.YELLOW));
            } else {
                say(player, Text.literal("Aucun node ici.").formatted(Formatting.GRAY));
            }
            return;
        }

        NodeStore.Node existing = store.get(pos);
        if (existing != null) {
            say(player, NodeCommandsSupport.describe(pos, existing));
            return;
        }

        Identifier type = boundType(wand);
        if (type == null) {
            say(player, Text.literal("Cette baguette n'est liée à aucun type — « /metiers node lier <type> ».")
                    .formatted(Formatting.RED));
            return;
        }
        if (!HcmData.NODES.contains(type)) {
            say(player, Text.literal("Le type " + type + " n'existe plus dans les données.").formatted(Formatting.RED));
            return;
        }

        NodeEngine.PlaceRefusal refusal = NodeEngine.place(world, pos, type);
        if (refusal == null) {
            say(player, Text.literal("Node « " + type.getPath() + " » posé en " + pos.toShortString() + ".")
                    .formatted(Formatting.GREEN));
        } else {
            say(player, Text.literal("Impossible de poser : " + NodeCommandsSupport.explain(refusal))
                    .formatted(Formatting.RED));
        }
    }

    private static void say(ServerPlayerEntity player, Text text) {
        if (player.networkHandler != null) {
            player.sendMessage(text, false);
        }
    }
}
