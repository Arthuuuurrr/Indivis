package net.hautecapitale.metiers.npc;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.hautecapitale.metiers.hearth.HearthLink;
import net.hautecapitale.metiers.api.Metiers;
import net.hautecapitale.metiers.craft.CraftEngine;
import net.hautecapitale.metiers.craft.CraftFeedback;
import net.hautecapitale.metiers.craft.CraftRecipe;
import net.hautecapitale.metiers.data.HcmData;
import net.hautecapitale.metiers.repair.RepairEngine;
import net.hautecapitale.metiers.repair.RepairFeedback;
import net.hautecapitale.metiers.profession.Profession;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * Les trois messages de l'interface de métier.
 *
 * <p>Le client demande, le serveur décide. Un message venant du client n'est
 * jamais un ordre : on revérifie qu'il a bien cet écran ouvert, que le rôle
 * existe toujours, et que le PNJ enseigne réellement. Un client modifié ne peut
 * donc rien obtenir qu'un joueur honnête n'obtiendrait pas.
 */
public final class ProfessionNetwork {

    /** « J'ai cliqué sur Apprendre. » */
    public record LearnRequest(Identifier role) implements CustomPayload {
        public static final CustomPayload.Id<LearnRequest> ID =
                new CustomPayload.Id<>(HauteCapitaleMetiers.id("apprendre"));

        public static final PacketCodec<RegistryByteBuf, LearnRequest> CODEC =
                PacketCodec.tuple(Identifier.PACKET_CODEC, LearnRequest::role, LearnRequest::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /** « J'ai cliqué sur Fabriquer, tant de fois. » */
    public record CraftRequest(Identifier recipe, int times) implements CustomPayload {
        public static final CustomPayload.Id<CraftRequest> ID =
                new CustomPayload.Id<>(HauteCapitaleMetiers.id("fabriquer"));

        public static final PacketCodec<RegistryByteBuf, CraftRequest> CODEC = PacketCodec.tuple(
                Identifier.PACKET_CODEC, CraftRequest::recipe,
                PacketCodecs.INTEGER, CraftRequest::times,
                CraftRequest::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /** « J'ai cliqué sur Réparer » — un emplacement d'inventaire, ou -1 pour tout réparer. */
    public record RepairRequest(int slot) implements CustomPayload {
        public static final CustomPayload.Id<RepairRequest> ID =
                new CustomPayload.Id<>(HauteCapitaleMetiers.id("reparer"));

        public static final PacketCodec<RegistryByteBuf, RepairRequest> CODEC =
                PacketCodec.tuple(PacketCodecs.INTEGER, RepairRequest::slot, RepairRequest::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    /** « Voici l'écran à jour. » Envoyé après un changement, sans refermer. */
    public record ScreenUpdate(ProfessionScreenData data) implements CustomPayload {
        public static final CustomPayload.Id<ScreenUpdate> ID =
                new CustomPayload.Id<>(HauteCapitaleMetiers.id("ecran_metier"));

        public static final PacketCodec<RegistryByteBuf, ScreenUpdate> CODEC =
                PacketCodec.tuple(ProfessionScreenData.PACKET_CODEC, ScreenUpdate::data, ScreenUpdate::new);

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    private ProfessionNetwork() {
    }

    public static void init() {
        PayloadTypeRegistry.playC2S().register(LearnRequest.ID, LearnRequest.CODEC);
        PayloadTypeRegistry.playC2S().register(CraftRequest.ID, CraftRequest.CODEC);
        PayloadTypeRegistry.playC2S().register(RepairRequest.ID, RepairRequest.CODEC);
        PayloadTypeRegistry.playS2C().register(ScreenUpdate.ID, ScreenUpdate.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(LearnRequest.ID,
                (payload, context) -> context.server().execute(
                        () -> learn(context.player(), payload.role())));
        ServerPlayNetworking.registerGlobalReceiver(CraftRequest.ID,
                (payload, context) -> context.server().execute(
                        () -> craft(context.player(), payload.recipe(), payload.times())));
        ServerPlayNetworking.registerGlobalReceiver(RepairRequest.ID,
                (payload, context) -> context.server().execute(
                        () -> repair(context.player(), payload.slot())));
    }

    /**
     * Fabriquer chez un artisan.
     *
     * <p>Le clic n'est qu'une demande. On vérifie que l'écran est bien ouvert
     * sur un atelier de ce métier — sans quoi n'importe quel client pourrait
     * fabriquer n'importe où — puis le moteur revérifie tout le reste.
     */
    private static void craft(ServerPlayerEntity player, Identifier recipeId, int times) {
        if (!(player.currentScreenHandler instanceof ProfessionMenu menu)) {
            return;
        }
        NpcRole role = HcmData.ROLES.get(menu.role());
        if (recipeId.getNamespace().equals(HauteCapitaleMetiers.MOD_ID) && recipeId.getPath().startsWith("foyer/")) {
            // Choisir une auberge dans le registre d'un aubergiste : même porte
            // qu'une recette, même garde — l'écran doit être celui d'un aubergiste.
            if (role != null && role.hearth()) {
                int index;
                try {
                    index = Integer.parseInt(recipeId.getPath().substring("foyer/".length()));
                } catch (NumberFormatException e) {
                    return;
                }
                HearthLink.choose(player, index + 1);
                RoleGate.refresh(player);
            }
            return;
        }
        CraftRecipe recipe = HcmData.RECIPES.get(recipeId);
        if (role == null || recipe == null || role.screen() != NpcRole.Interface.ATELIER
                || role.profession().orElse(null) != recipe.profession()) {
            return;
        }

        CraftEngine.Outcome outcome = CraftEngine.craft(player, recipeId, times);
        RoleGate.tell(player, CraftFeedback.describe(recipe, outcome));
        RoleGate.refresh(player);
    }

    /**
     * Réparer chez un forgeron.
     *
     * <p>Aucun métier requis, mais l'écran doit être ouvert sur un rôle qui
     * propose la réparation : c'est la seule porte. Le moteur revérifie la
     * durabilité et le solde, puis débite et répare dans le même geste.
     */
    private static void repair(ServerPlayerEntity player, int slot) {
        if (!(player.currentScreenHandler instanceof ProfessionMenu menu)) {
            return;
        }
        NpcRole role = HcmData.ROLES.get(menu.role());
        if (role == null || !role.repairs()) {
            return;
        }
        RepairEngine.Outcome outcome = slot < 0 ? RepairEngine.repairAll(player) : RepairEngine.repair(player, slot);
        RoleGate.tell(player, RepairFeedback.describe(outcome));
        RoleGate.refresh(player);
    }

    public static void sendUpdate(ServerPlayerEntity player, ProfessionScreenData data) {
        ServerPlayNetworking.send(player, new ScreenUpdate(data));
    }

    // ------------------------------------------------------------------

    /**
     * Apprendre un métier auprès d'un maître.
     *
     * <p>Chaque condition est revérifiée ici : c'est le serveur qui accorde le
     * métier, l'écran ne fait que proposer le bouton.
     */
    private static void learn(ServerPlayerEntity player, Identifier roleId) {
        if (!(player.currentScreenHandler instanceof ProfessionMenu menu)
                || !menu.role().equals(roleId)) {
            // L'écran a été fermé entre-temps, ou n'a jamais été ouvert.
            return;
        }

        NpcRole role = HcmData.ROLES.get(roleId);
        if (role == null || role.screen() != NpcRole.Interface.FORMATION || !role.canTeach()) {
            return;
        }

        Profession profession = role.profession().orElse(null);
        if (profession == null) {
            return;
        }

        if (Metiers.hasProfession(player, profession)) {
            return;
        }

        if (Metiers.learn(player, profession)) {
            RoleGate.tell(player, Text.translatableWithFallback(
                    "hcm.metier.appris", "Vous apprenez le métier : %s",
                    Text.translatable(profession.getTranslationKey()))
                    .formatted(Formatting.GREEN));
        } else {
            RoleGate.tell(player, Text.translatableWithFallback(
                    "hcm.metier.plafond_metiers",
                    "Vous connaissez déjà le nombre maximum de métiers.")
                    .formatted(Formatting.RED));
        }

        RoleGate.refresh(player);
    }
}
