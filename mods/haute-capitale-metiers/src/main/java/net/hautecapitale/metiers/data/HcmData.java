package net.hautecapitale.metiers.data;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.hautecapitale.metiers.config.MetiersConfig;
import net.hautecapitale.metiers.craft.CraftRecipe;
import net.hautecapitale.metiers.meal.MealBuff;
import net.hautecapitale.metiers.creature.CreatureProfile;
import net.hautecapitale.metiers.creature.DropEntry;
import net.hautecapitale.metiers.node.NodeType;
import net.hautecapitale.metiers.npc.NpcRole;
import net.hautecapitale.metiers.profession.Profession;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Les registres de données du mod.
 *
 * <p>Point d'entrée unique pour tout ce que le MMO lit dans des fichiers.
 * Une étape suivante y ajoute son domaine en trois lignes : déclarer le
 * registre, écrire son validateur, l'enregistrer dans {@link #init()}.
 *
 * <p>Les fichiers vivent dans les datapacks, sous
 * {@code data/<namespace>/hcm/<domaine>/}. Ils se rechargent avec {@code /reload}
 * ou {@code /metiers recharger}, sans redémarrage.
 */
public final class HcmData {

    /**
     * Fiches de créature : qui est chassable, qui est dépeçable, à quel niveau,
     * pour quelles matières, et quelles origines d'apparition donnent de l'XP.
     */
    public static final DataRegistry<CreatureProfile> CREATURES =
            new DataRegistry<>("creatures", CreatureProfile.CODEC, HcmData::validateCreature);

    /**
     * Rôles de PNJ : ce qu'un Easy NPC sait faire pour un joueur, et quelle
     * interface son action ouvre.
     */
    public static final DataRegistry<NpcRole> ROLES =
            new DataRegistry<>("roles", NpcRole.CODEC, HcmData::validateRole);

    /** Recettes de métier : ce que chaque artisan fabrique, à quel niveau, pour quel prix. */
    public static final DataRegistry<CraftRecipe> RECIPES =
            new DataRegistry<>("recipes", CraftRecipe.CODEC, HcmData::validateRecipe);

    /** Types de node : filons, plantes — ce qu'on récolte, à quel niveau, et ce que ça donne. */
    public static final DataRegistry<NodeType> NODES =
            new DataRegistry<>("nodes", NodeType.CODEC, HcmData::validateNode);

    /** Buffs de repas : ce qu'un plat du Cuisinier donne à qui le mange. */
    public static final DataRegistry<MealBuff> BUFFS =
            new DataRegistry<>("buffs", MealBuff.CODEC, HcmData::validateBuff);

    /** Tous les registres, dans l'ordre d'affichage du résumé. */
    public static final List<DataRegistry<?>> ALL = List.of(CREATURES, ROLES, RECIPES, NODES, BUFFS);

    /**
     * Qui prévenir à la fin du prochain rechargement.
     *
     * <p>Un rechargement de datapack est asynchrone : la commande rend la main
     * bien avant que les fichiers soient relus. Sans ce relais, l'administrateur
     * lirait le compte-rendu de l'état précédent. Une seule attente à la fois :
     * le dernier demandeur remplace celui qui patientait.
     */
    private static final AtomicReference<Consumer<Boolean>> PENDING = new AtomicReference<>();

    private HcmData() {
    }

    public static void init() {
        CREATURES.register();
        ROLES.register();
        RECIPES.register();
        NODES.register();
        BUFFS.register();

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, success) -> {
            Consumer<Boolean> listener = PENDING.getAndSet(null);
            if (listener != null) {
                listener.accept(success);
            }
        });
    }

    /** Demande un compte-rendu à la fin du rechargement en cours ou à venir. */
    public static void reportNextReloadTo(Consumer<Boolean> listener) {
        PENDING.set(listener);
    }

    /** Toutes les erreurs et avertissements du dernier chargement, tous domaines confondus. */
    public static List<LoadReport.Entry> allIssues() {
        return ALL.stream().flatMap(registry -> registry.report().entries().stream()).toList();
    }

    // ------------------------------------------------------------------

    /**
     * Contrôles qu'un codec ne peut pas faire : cohérence entre champs, et
     * références vers des objets ou des entités qui n'existent pas.
     *
     * <p>Une référence inconnue est un <em>avertissement</em>, pas une erreur :
     * un datapack peut légitimement décrire une créature d'un mod optionnel,
     * absent de cette installation. En revanche un niveau hors bornes est une
     * erreur, parce qu'il rendrait la fiche injouable en silence.
     */
    private static void validateCreature(CreatureProfile profile, DataRegistry.Validator checks) {
        int maxLevel = MetiersConfig.get().niveau_max;

        if (!Registries.ENTITY_TYPE.containsId(checks.key())) {
            checks.warn("type d'entité inconnu : " + checks.key()
                    + " — la fiche est chargée mais ne servira que si le mod concerné est installé");
        }

        profile.hunter().ifPresent(hunter -> {
            if (hunter.level() > maxLevel) {
                checks.reject("niveau Chasseur " + hunter.level() + " au-dessus du maximum " + maxLevel);
            }
            if (hunter.xp() <= 0.0D) {
                checks.warn("créature déclarée chassable mais sans XP : elle ne fera pas progresser le Chasseur");
            }
        });

        profile.skinning().ifPresent(skinning -> {
            if (skinning.level() > maxLevel) {
                checks.reject("niveau Dépeceur " + skinning.level() + " au-dessus du maximum " + maxLevel);
            }
            if (skinning.xp() <= 0.0D) {
                checks.warn("créature déclarée dépeçable mais sans XP : elle ne fera pas progresser le Dépeceur");
            }
            if (skinning.carcassSeconds() > 3600) {
                checks.warn("carcasse de " + skinning.carcassSeconds()
                        + " s : une durée aussi longue encombrera le monde");
            }
            checkItem(skinning.material(), "matiere", checks);
            skinning.secondary().forEach(entry -> checkItem(entry, "secondaires", checks));
        });

        profile.meat().ifPresent(meat -> checkItem(meat, "viande", checks));
        profile.combatLoot().forEach(entry -> checkItem(entry, "loot_combat", checks));

        if (profile.xpOrigins().isEmpty()) {
            checks.warn("aucune origine d'apparition ne donne d'XP : la créature ne fera jamais progresser un métier");
        }
    }

    /**
     * Un rôle mal réglé n'a pas de conséquence silencieuse : le joueur clique et
     * ne voit rien. On préfère donc écarter ce qui n'a aucun sens et signaler ce
     * qui n'en a qu'à moitié.
     */
    static void validateRole(NpcRole role, DataRegistry.Validator checks) {
        if (role.needsProfession() && role.profession().isEmpty()) {
            checks.reject("une interface « " + role.screen().asString()
                    + " » doit indiquer son « metier » — sinon le PNJ n'a rien à ouvrir");
            return;
        }

        if (!role.needsProfession() && role.profession().isPresent()) {
            checks.warn("le « metier » est ignoré par une interface « "
                    + role.screen().asString() + " », qui montre tous les métiers du joueur");
        }

        role.profession().ifPresent(profession -> {
            if (role.screen() == NpcRole.Interface.ATELIER
                    && profession.getKind() != Profession.Kind.ARTISANAT) {
                checks.warn("« " + profession.getId() + " » est un métier de récolte : son atelier"
                        + " restera vide. Pour un maître de métier, utilisez « interface »: « formation »");
            }
        });
    }

    /**
     * Une recette qui référence un objet absent est refusée, pas seulement
     * signalée : contrairement à une créature, elle serait proposée au joueur
     * et échouerait au clic. Une famille (tag) vide n'est qu'un avertissement —
     * le tag peut se remplir quand le mod qui le nourrit sera installé.
     */
    static void validateBuff(MealBuff buff, DataRegistry.Validator checks) {
        if (!Registries.ATTRIBUTE.containsId(buff.attribute())) {
            checks.warn("attribut inconnu : " + buff.attribute()
                    + " — le buff est chargé mais ne fera rien tant que le mod concerné n'est pas installé");
        }
        if (buff.value() == 0.0D) {
            checks.warn("valeur nulle : ce buff ne change rien");
        }
        if (buff.operation().isPercent() && Math.abs(buff.value()) > 1.0D) {
            checks.warn("valeur " + buff.value() + " pour une opération en pourcentage : 0.05 = +5 %, pas 5");
        }
    }

    static void validateRecipe(CraftRecipe recipe, DataRegistry.Validator checks) {
        int maxLevel = MetiersConfig.get().niveau_max;

        if (recipe.level() > maxLevel) {
            checks.reject("niveau " + recipe.level() + " au-dessus du maximum " + maxLevel);
            return;
        }
        if (recipe.profession().getKind() != Profession.Kind.ARTISANAT) {
            checks.warn("« " + recipe.profession().getId()
                    + " » est un métier de récolte : aucun atelier ne proposera cette recette");
        }
        if (recipe.ingredients().isEmpty()) {
            checks.reject("aucun ingrédient — une recette sans entrée n'a pas de sens");
            return;
        }
        if (recipe.xp() <= 0.0D) {
            checks.warn("recette sans XP : elle ne fera pas progresser le métier");
        }

        // Les familles (tags) ne sont pas contrôlées ici : pendant un
        // rechargement, les tags ne sont liés au registre qu'après tous les
        // chargeurs, et le nôtre verrait l'état précédent. Une famille vide se
        // voit dans « /metiers inspecter recettes », une fois tout chargé.
        for (CraftRecipe.Ingredient ingredient : recipe.ingredients()) {
            ingredient.item().ifPresent(id -> {
                if (!Registries.ITEM.containsId(id)) {
                    checks.reject("ingrédient inconnu : " + id);
                }
            });
        }

        if (!Registries.ITEM.containsId(recipe.result().item())) {
            checks.reject("résultat inconnu : " + recipe.result().item());
        }
        if (recipe.cost() > 0 && !Registries.ITEM.containsId(MetiersConfig.get().currency())) {
            checks.warn("la recette a un prix mais la monnaie « " + MetiersConfig.get().monnaie
                    + " » n'existe pas dans cette installation : elle ne pourra pas être payée");
        }
        recipe.meal().ifPresent(meal -> {
            // La fiche de buff est vérifiée plus tard, quand tous les registres sont
            // relus : voir « /metiers inspecter recettes ». Ici, seul le résultat compte.
            if (Registries.ITEM.containsId(recipe.result().item())
                    && !new ItemStack(Registries.ITEM.get(recipe.result().item())).contains(DataComponentTypes.CONSUMABLE)) {
                checks.warn("« repas » sur un résultat qui ne se mange pas : le buff ne s'appliquera jamais");
            }
        });
    }

    /**
     * Un type de node dont un bloc n'existe pas est refusé : on ne peut ni le
     * poser ni le faire repousser. Le reste se signale.
     */
    static void validateNode(NodeType type, DataRegistry.Validator checks) {
        int maxLevel = MetiersConfig.get().niveau_max;

        if (type.level() > maxLevel) {
            checks.reject("niveau " + type.level() + " au-dessus du maximum " + maxLevel);
            return;
        }
        if (type.fullState() == null) {
            checks.reject("« bloc_plein » inconnu ou mal écrit : " + type.fullBlock()
                    + " — attendu id ou id[propriete=valeur]");
            return;
        }
        if (type.emptyState() == null) {
            checks.reject("« bloc_vide » inconnu ou mal écrit : " + type.emptyBlock());
            return;
        }
        if (type.fullBlock().equals(type.emptyBlock())) {
            checks.warn("« bloc_plein » et « bloc_vide » sont identiques : on ne verra pas qu'il a été récolté");
        }
        if (type.profession().getKind() != Profession.Kind.RECOLTE) {
            checks.warn("« " + type.profession().getId() + " » est un métier d'artisanat : "
                    + "un node se récolte, il ne se fabrique pas");
        }
        if (type.xp() <= 0.0D) {
            checks.warn("node sans XP : il ne fera pas progresser le métier");
        }
        if (type.loot().isEmpty()) {
            checks.warn("node sans butin : le joueur récoltera de l'air");
        }
        type.loot().forEach(entry -> checkItem(entry, "loot", checks));
    }

    private static void checkItem(DropEntry entry, String field, DataRegistry.Validator checks) {
        Identifier id = entry.item();
        if (!Registries.ITEM.containsId(id)) {
            checks.warn("objet inconnu dans « " + field + " » : " + id
                    + " — vérifiez l'orthographe, ou que le mod est bien installé");
        }
        if (entry.chance() <= 0.0D) {
            checks.warn("« " + field + " » : " + id + " a une chance nulle, il ne tombera jamais");
        }
    }
}
