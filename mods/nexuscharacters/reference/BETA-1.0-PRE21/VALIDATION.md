# NexusCharacters PRE21 — validation du rendu

## Problèmes reproduits dans PRE20

Les captures et le log client du 2 octobre 2026 indiquent PRE20, Minecraft 1.21.11, Skin Layers 1.10.2 et Sodium 0.8.7. Deux apparences sont identifiées dans le log : `b1/h07/s05/o01` et `b3/h05/s07/o19`.

Le banc reproduit un cache natif sans meshes mais avec l'identifiant de skin courant conservé. PRE20 laisse alors les six parties sans mesh corrigé malgré son message READY. Cette expérience prouve la défaillance de la condition `getInjectedMesh() != null` ; elle ne prouve pas à elle seule le déclencheur du cache vide sur le client de l'utilisateur.

Le contrôle du modèle inférieur trouve neuf recouvrements coplanaires de surface positive en pose neutre : cou, taille, bras/torse et jambes. PRE20 contrôlait uniquement la géométrie extérieure et ne détectait pas ces surfaces. Les fichiers `evidence/pre20-native-null.log` et `evidence/pre21-before-lower-fix.log` conservent les résultats avant correction.

Enfin, la composition de toutes les catégories dans un masque alpha unique supprime les parois aux limites cheveux/vêtements quand les pixels voisins sont opaques.

## Corrections

- La décision de rendre les couches Nexus repose sur la distance, les options activées et le masquage du casque, indépendamment du contenu du cache de skin natif.
- Les masques vêtements, cheveux et barbe classique restent distincts. La bibliothèque Skin Layers construit les voxels et leurs parois pour chaque masque. Une petite séparation géométrique empêche les catégories de partager les mêmes plans. Les empreintes cachées suivent l'ordre existant tenue/cheveux/barbe.
- Le modèle inférieur reste plat. Des copies de ses cubes séparent uniquement les limites aux raccords ; les UV sont conservés. Les cubes originaux sont restaurés pour une apparence qui n'est pas gérée par Nexus.
- Les caches de cubes Sodium sont également mis à jour : modifier seulement les polygones vanilla ne modifie pas le rendu rapide de Sodium.
- Le diagnostic indique PREPARED, plutôt que READY : préparer un mesh ne prouve pas sa soumission au rendu.

## Contrôles exécutés

Le banc lance réellement Minecraft avec Fabric Loader 0.19.5, Java 21, Skin Layers 1.10.2, Sodium 0.8.7 et ImmediatelyFast 1.14.2, via OpenGL logiciel Mesa. Il utilise le joueur client réel et l'extraction du render state, puis vérifie les meshes après `PlayerModel.setupAnim`.

Huit apparences incluent les deux cas des nouveaux logs, les cheveux longs 8/9/10 avec tenue 20 et barbes classiques, ainsi que des ajouts de coiffure, tenue, barbe et moustache. Trois angles par apparence produisent 24 captures. Les ajouts sont fournis exclusivement par le module de test ; aucune texture de test n'est ajoutée au mod livré.

Les contrôles des surfaces incluent les cubes inférieurs et les voxels supérieurs, avec les transformations parentes réelles des parties de Minecraft 1.21.11. Ils détectent les recouvrements coplanaires partiels de surface positive en pose neutre, et pas seulement les doublons de quatre sommets identiques.

Les UV vanilla et Sodium des copies du modèle inférieur sont comparés aux originaux. La restauration des cubes originaux est vérifiée. Chaque partie extérieure doit contenir exactement l'objet attendu pour l'apparence en cours, y compris lorsque le cache natif a été vidé.

L'audit de 1 496 combinaisons couvre 14 coiffures × 34 tenues et huit barbes classiques × 34 tenues, dans les deux représentations géométriques natives. Il contrôle les masques séparés, les bras larges et fins, les sommets finis, les faces dupliquées, les plans des faces inférieures et les limites de raccords.

Les résultats finaux et le SHA-256 du JAR sont consignés dans `evidence/results.txt`. Les captures de première et troisième personne proviennent du même JAR.

## Portée

Ce banc ne contient pas l'intégralité du modpack utilisateur. Le contrôle géométrique des raccords porte sur la pose neutre ; les captures montrent trois angles, mais ne constituent pas une validation de toutes les animations externes ou de tous les shaders. Le mode de compatibilité de Skin Layers est un mode de géométrie testé avec Sodium ; ce n'est pas un essai d'un shader Iris.

## Reproduction de l'assemblage

```
mkdir -p classes
javac -proc:none -cp PRE20.jar -d classes sources/GenericSkinLayerSupport.java sources/GenericLayerPixels.java sources/LowerJointSupport.java
python assemble.py PRE20.jar NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE21.jar
```

L'assembleur exige le SHA-256 de PRE20 vérifié et contrôle que tous ses assets restent identiques. Le workflow GitHub vérifie également le SHA-256 de sortie avant de conserver le binaire.
