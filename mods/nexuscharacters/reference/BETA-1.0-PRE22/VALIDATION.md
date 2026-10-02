# PRE22 — correction du rendu des couches cosmétiques

> **Validation fonctionnelle retirée le 2 octobre 2026 après le retour en jeu.** Les résultats PASS ci-dessous décrivent le banc de test, pas une version acceptée. Le retour utilisateur signale un relief toujours absent, des conflits aux membres et un skin de compte remplacé par Steve. Voir `REJET_PRE22.md`.

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
- Le contrôle des intersections partielles, y compris internes à un même mesh et entre éléments, porte sur les douze apparences du joueur et les quatre apparences des aperçus. Le grand audit de 1 496 cas vérifie les doublons exacts, les limites des articulations, les plans de base et les coordonnées finies ; il ne recherche pas toutes les intersections partielles entre catégories. Les UV du modèle inférieur et sa restauration sont également vérifiés.
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

## Revérification demandée le 2 octobre 2026

- JAR distribué et JAR exécuté identiques, SHA-256 inchangé : `6876ffd02b93399defae6b58b53b86e6b494b9581f647098f06bdcd614da7f78`. Archive intègre et version 1.0.0-beta.22+hc.1.21.11 confirmées.
- Nouvelle exécution Minecraft en mode natif : sortie 0, marqueur final PASS, 12 apparences, 36 captures, zéro mesh manquant et zéro recouvrement détecté. Audit des 1 496 cas et première personne réussis.
- Nouvelle exécution Minecraft en mode compatibilité : mêmes résultats, contrôle des fragments Sodium et des UV réussi.
- Deux tentatives préalables n'ont pas atteint les tests : une est restée au démarrage et a été interrompue ; une autre s'est fermée sans marqueur PASS. Elles ne sont pas comptées comme réussies. Les relances ont utilisé une limite de temps ; la dernière relance a également limité ses threads pour réduire la charge du rendu logiciel.
- Test supplémentaire du découpage natif directement depuis le JAR : 3 000 paires de rectangles, trois axes, normales identiques/opposées, couverture attendue et UV conservés. Configuration minimale réservée à ce test isolé ; ce résultat ne remplace pas les tests Minecraft/Sodium.
- Assets PRE17 : comparaison répétée, 4 958 fichiers identiques. Aucun changement apporté au JAR de production durant cette revérification.
- Inspection visuelle des captures des cheveux longs, des barbes classiques et de la moustache, de face et de dos et sous angles élevés/bas. Une image fixe ne permet pas de mesurer un scintillement temporel.

Logs supplémentaires : `evidence/reverification-native.log`, `evidence/reverification-compatibility.log`, `evidence/reverification-unit.log`. Les sources du contrôle isolé sont dans `tests/reverification/`. La portée reste celle décrite plus haut : poses neutres, sans animations externes ni shader Iris, sans exécution du modpack Windows/NVIDIA complet.
