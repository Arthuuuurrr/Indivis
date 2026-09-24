package net.hautecapitale.metiers.meal;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.hautecapitale.metiers.HauteCapitaleMetiers;
import net.minecraft.text.Text;

/**
 * La marmite de Farmer's Delight ne prépare plus les plats de niveau 20 et
 * plus : sinon un joueur contournerait son niveau de Cuisinier en posant une
 * marmite chez lui.
 *
 * <p>Le moyen : un datapack intégré au jar, activé par défaut, qui remplace
 * chacune de ces recettes de marmite par un fichier portant une condition de
 * chargement toujours fausse — Fabric l'écarte, la recette disparaît, la
 * marmite reste pour les plats simples et la décoration. L'administrateur qui
 * veut la marmite complète désactive le pack : {@code /datapack disable}.
 */
public final class PotRestriction {

    public static final String PACK = "marmite";

    private PotRestriction() {
    }

    public static void init() {
        FabricLoader.getInstance().getModContainer(HauteCapitaleMetiers.MOD_ID).ifPresent(container ->
                ResourceManagerHelper.registerBuiltinResourcePack(HauteCapitaleMetiers.id(PACK), container,
                        Text.literal("Haute Capitale — marmite bridée"), ResourcePackActivationType.DEFAULT_ENABLED));
    }
}
