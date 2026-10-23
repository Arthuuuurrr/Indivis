# Correctif du démarrage — PQ01 R3-HF1 / Admin alpha.3

Le log du 23 octobre 2026 échoue pendant la préparation de `Pq01EntityMixin` : son annotation `@Mixin` n'est pas trouvée par `MixinInfo.readPriority`. Le premier chargement Axiom déclenche cette préparation globale ; la classe en défaut appartient au mod de quêtes.

La cause est dans les contrats de compilation ajoutés pour R3 et alpha.2 : `@Mixin` était déclaré `RUNTIME` au lieu de `CLASS`. Mixin recherche l'en-tête dans `RuntimeInvisibleAnnotations`. Les membres `Inject.at` et `ModifyVariable.at` étaient aussi déclarés comme un `At` unique, alors que la dépendance réelle du serveur les déclare `At[]`. Les trois mixins sont recompilés avec la vraie dépendance ; les contrats corrigés produisent les mêmes JAR. Les vérificateurs de construction refusent désormais ces deux formes incorrectes.

## Installation

- Serveur **et clients** : remplacer le journal R3 par `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF1.jar`.
- Serveur : remplacer le mod admin alpha.1/alpha.2 par `indivis-admin-0.1.0-alpha.3+1.21.11.jar`. Les clients multijoueur n'ont pas besoin du mod admin.
- Les deux datapacks R3 déjà fournis restent installés, sans remplacement : leurs SHA-256 sont identiques. Les commandes et points PNJ existants restent utilisables. Redémarrer puis se reconnecter.
- Le lot `Indivis_Correctif_Mixin_PQ01_R3_HF1_Admin_alpha3_2026-10-23.zip` contient ces deux JAR, le mémo et le guide texte. Aucun Nexus inchangé n'est redistribué.

## Périmètre vérifié

Dans le journal : seuls `fabric.mod.json` et les deux classes de mixin PQ01 changent ; **208 entrées sont identiques**. Dans l'admin : seuls la version, le message de démarrage de `FabricRuntime` et les annotations de `NexusProfileSaveMixin` changent ; **19 entrées sont identiques**. Le code et les sélecteurs des trois mixins restent identiques après normalisation des annotations corrigées. Aucun dialogue, déplacement, catalogue ni fonction de gestion n'est retiré.

Le test utilise le vrai `Annotations.getInvisible` du Sponge Mixin `0.17.4+mixin.0.8.7` du serveur. Il reproduit les deux en-têtes manquants et cinq formes `at` incorrectes sur R3, puis l'en-tête et la forme `at` incorrects sur alpha.2. Aucun de ces défauts sur les nouveaux JAR. Le contrôleur de marche repasse 17 cas/26 assertions ; les bancs admin repassent 19+21 assertions avec API doublées. ASM vérifie les deux archives. Les 53 fonctions du guide existent dans les datapacks R3.

**Aucun démarrage complet Minecraft/Fabric ni test du monde n'a été exécuté ici.** Le test du lecteur d'annotations cible exactement la cause du crash transmis ; il ne valide pas l'application complète des mixins du modpack, la navigation réelle ou le rendu en jeu. Les assertions historiques de R3 sur les datapacks ne sont pas présentées comme un test de démarrage.

## Empreintes

| Fichier | SHA-256 |
| --- | --- |
| Journal R3-HF1 | `3a530584447587f01fd4187adae32506a28a56cf2ca98057e636c76ef37901d3` |
| Admin alpha.3 | `1c11cdb7fb11bfdaf587d2d6492569ff79e155dd86ccccfaa4dbd60bade38281` |

## Reproduction

`tools/mixin_hotfix/verify.py` télécharge les dépendances publiques officielles avec SHA-256 figés dans `dependencies.json`, compile les deux bancs Java et confronte les anciens et nouveaux JAR. Les dépendances et classes de banc ne sont pas livrées dans les mods.

```bash
python3 tools/mixin_hotfix/verify.py --old-journal JOURNAL_R3.jar --journal JOURNAL_R3_HF1.jar --old-admin ADMIN_ALPHA2.jar --admin ADMIN_ALPHA3.jar
```

Les builds acceptent aussi `INDIVIS_MIXIN_CLASSPATH=/chemin/sponge-mixin-0.17.4+mixin.0.8.7.jar` pour compiler contre la dépendance réelle. Sans cette variable, les contrats corrigés sont utilisés avec les vérifications de rétention et de forme des points d'injection.

Références primaires : [annotation Mixin](https://github.com/SpongePowered/Mixin/blob/master/src/main/java/org/spongepowered/asm/mixin/Mixin.java), [MixinInfo](https://github.com/SpongePowered/Mixin/blob/master/src/main/java/org/spongepowered/asm/mixin/transformer/MixinInfo.java), [artefact Fabric du serveur](https://maven.fabricmc.net/net/fabricmc/sponge-mixin/0.17.4+mixin.0.8.7/).
