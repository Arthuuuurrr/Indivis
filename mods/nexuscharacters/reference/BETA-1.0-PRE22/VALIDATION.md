# PRE22 — correction du rendu des couches cosmétiques

## Pourquoi la validation PRE21 était insuffisante

Le contrôle précédent ignorait les paires de faces appartenant au même mesh. Le même JAR PRE21 livré, soumis au contrôle élargi avec Skin Layers 1.10.2, Sodium 0.8.7, EntityCulling 1.10.2 / TRender 1.0.13 et HUD 1.3.3, échoue sur quatre apparences : 38 + 44 + 35 + 31 = 148 recouvrements coplanaires de surface positive. Le log négatif est conservé dans `evidence/pre21-failed-expanded-audit.log`.

La condition de promotion des cosmétiques était également trop restrictive : une partie contenant ne serait-ce qu'un détail supérieur empêchait l'extrusion de tous ses pixels inférieurs. Un exemple d'asset de cheveux longs comporte 314 pixels inférieurs de tête mais seulement 111 pixels supérieurs. Préserver les fichiers de textures ne suffisait donc pas à préserver le résultat visuel attendu.

## Correction

`GenericLayerPixels` conserve les détails supérieurs existants et ajoute une empreinte supérieure pour chaque pixel cosmétique inférieur opaque qui n'a pas de détail supérieur correspondant. Cela concerne les cheveux, vêtements et barbes classiques, y compris les prochains assets reconnus par le catalogue dynamique. La composition, les couleurs et les barbes déjà modélisées ne sont pas remplacées.

`CoplanarSurfaceSupport` soustrait les intersections partielles entre faces coplanaires. Pour deux faces opposées, la surface commune interne est retirée des deux faces ; pour deux faces orientées dans le même sens, une seule surface est conservée. Les fragments conservent leurs coordonnées de texture par interpolation. Les faces adjacentes sans aire commune restent présentes.

Le mode natif utilise les faces nettoyées du renderer Skin Layers. Le mode de compatibilité utilise un cube vanilla à face unique par fragment : Sodium ne peut plus reconstruire une face agrandie à partir des limites d'un cube contenant plusieurs faces de tailles différentes. Le test compare les quatre positions et les quatre UV utilisés par Sodium à ceux du fragment, ainsi que son masque de faces.

Les raccords du modèle inférieur, les masques séparés par catégorie, le remplacement indépendant du cache natif et la restauration des cubes originaux sont conservés.

## Vérification du JAR livré

- Minecraft 1.21.11, Java 21, Fabric Loader 0.19.5.
- Skin Layers 1.10.2, Sodium 0.8.7, ImmediatelyFast 1.14.2, EntityCulling 1.10.2, TRender 1.0.13, TRansition 1.0.19, Capitale RP HUD 1.3.3.
- Douze apparences de joueur réel : les quatre combinaisons des nouveaux retours, les deux anciens cas, des cheveux longs avec tenues et barbes classiques, et des ajouts de coiffure, coiffure HD, tenue, barbe et moustache.
- 36 captures de joueur par mode ; première et troisième personne vérifiées.
- Aperçu Nexus d'origine : quatre apparences sous trois angles, sans monde puis avec monde, dans les deux modes ; 48 captures. Les deux modèles du widget (bras larges/fins) sont contrôlés.
- Total : 120 captures de scènes, plus les captures de première et troisième personne.
- Audit : 748 configurations dans deux modes, soit 1 496 cas géométriques. Le total de faces examinées par exécution est indiqué dans les logs.
- Tous les contrôles géométriques incluent désormais les conflits internes au même mesh. Les UV du modèle inférieur et sa restauration sont également vérifiés.
- Les 4 958 fichiers d'assets de PRE17 sont byte-identiques. Les fixtures de nouveaux assets appartiennent uniquement au module de test.
- GitHub Actions reproduit le SHA-256 du JAR testé.

Les résultats et le SHA-256 figurent dans `evidence/results.txt`.

## Portée

Client Minecraft réel sous OpenGL logiciel Mesa, avec les mods de rendu et le HUD listés ci-dessus. Ce n'est pas une exécution de l'intégralité du modpack sous Windows/NVIDIA. Les tests de surfaces portent sur les poses neutres ; les captures couvrent trois angles, pas toutes les animations externes. Le mode de compatibilité est testé avec Sodium, sans shader Iris installé.

## Assemblage

Avec Java 21 et le JAR PRE21 dont l'empreinte est contrôlée par `assemble.py` :

```sh
mkdir -p classes
javac -proc:none -cp PRE21.jar -d classes sources/*.java
python assemble.py PRE21.jar PRE22.jar
```

Les bancs `ActualPlayerRegression` et `ReportedRegression` fonctionnent dans le runtime Fabric de test. `build_test.py` et `launch.py` documentent les chemins et dépendances de ce runtime ; les jars tiers ne sont pas inclus dans le dépôt de référence.
