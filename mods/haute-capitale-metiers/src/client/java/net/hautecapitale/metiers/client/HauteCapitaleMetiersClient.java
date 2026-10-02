package net.hautecapitale.metiers.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.registry.Registries;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.hautecapitale.metiers.entity.HcmEntities;
import net.hautecapitale.metiers.hearth.HearthAttachment;
import net.hautecapitale.metiers.hearth.HearthLink;
import net.hautecapitale.metiers.hearth.HearthstoneItem;
import net.minecraft.client.MinecraftClient;
import net.hautecapitale.metiers.npc.HcmScreens;
import net.hautecapitale.metiers.npc.ProfessionNetwork;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.meal.MealConsumeEffect;
import net.hautecapitale.metiers.meal.MealEngine;
import net.hautecapitale.metiers.quality.Quality;
import net.hautecapitale.metiers.repair.Breakage;
import net.hautecapitale.metiers.textile.CottonBlocks;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Le côté client du mod : un écran, de quoi le tenir à jour, et le rendu des
 * carcasses.
 *
 * <p>Tout le reste — progression, droits, contenu des listes — est décidé par le
 * serveur. Ce code n'existe même pas sur un serveur dédié.
 */
public class HauteCapitaleMetiersClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(HcmScreens.PROFESSION, ProfessionScreen::new);
        EntityRendererFactories.register(HcmEntities.CARCASS, CarcassRenderer::new);
        EntityRendererFactories.register(HcmEntities.DRONE, MiningDroneRenderer::new);
        // Les cotonniers sont des plantes en croix : sans cette ligne, vanilla les rend SOLID (rectangles noirs).
        BlockRenderLayerMap.putBlocks(BlockRenderLayer.CUTOUT, CottonBlocks.COTONNIER, CottonBlocks.COTONNIER_JEUNE);

        // Un objet brisé se voit : nom en rouge, et la mention dans l'infobulle.
        // Un objet Excellent aussi : nom doré (posé par le serveur), et sa ligne.
        // Un plat cuisiné dit le buff qu'il porte.
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, type, lines) -> {
            if (Breakage.isBroken(stack) && !lines.isEmpty()) {
                lines.set(0, lines.get(0).copy().formatted(Formatting.RED));
                lines.add(1, Text.translatableWithFallback("hcm.reparation.infobulle",
                        "BRISÉ — un forgeron peut le réparer").formatted(Formatting.RED, Formatting.BOLD));
            }
            int at = 1;
            if (Quality.isExcellent(stack) && !lines.isEmpty()) {
                lines.add(Math.min(at, lines.size()), Quality.label());
                at++;
            }
            // Chaque objet du mod peut porter une description dans les fichiers de
            // langue — item.<id>.desc, une ligne par retour à la ligne. Les gadgets
            // ont déjà la leur ; matières, textiles et baguette passent par ici.
            if (!lines.isEmpty() && Registries.ITEM.getId(stack.getItem()).getNamespace().equals(HauteCapitaleMetiers.MOD_ID)) {
                String key = stack.getItem().getTranslationKey() + ".desc";
                if (I18n.hasTranslation(key)) {
                    for (String line : I18n.translate(key).split("\n")) {
                        lines.add(Math.min(at, lines.size()), Text.literal(line).formatted(Formatting.GRAY));
                        at++;
                    }
                }
            }
            // La Pierre de foyer dit où elle ramène : l'auberge choisie, lue dans
            // l'attachement que le serveur pousse au client.
            if (stack.getItem() instanceof HearthstoneItem && !lines.isEmpty()
                    && MinecraftClient.getInstance().player != null) {
                HearthAttachment hearth = MinecraftClient.getInstance().player.getAttached(HearthAttachment.FOYER);
                if (hearth != null && hearth.current() != null) {
                    lines.add(Math.min(at, lines.size()), Text.translatableWithFallback("hcm.foyer.infobulle_actuel",
                            "Foyer : %s", Text.literal(HearthLink.describe(hearth.current()))).formatted(Formatting.AQUA));
                    at++;
                }
            }
            MealConsumeEffect meal = MealEngine.effectOf(stack);
            if (meal != null && !lines.isEmpty()) {
                double value = Quality.isExcellent(stack)
                        ? meal.value() * MetiersConfig.get().repas.excellent_facteur : meal.value();
                lines.add(Math.min(at, lines.size()), MealEngine.describe(meal, value).formatted(Formatting.GOLD));
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(ProfessionNetwork.ScreenUpdate.ID,
                (payload, context) -> context.client().execute(() -> {
                    if (context.client().currentScreen instanceof ProfessionScreen screen) {
                        screen.accept(payload.data());
                    }
                }));
    }
}
