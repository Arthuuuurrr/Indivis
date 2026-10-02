# PRE19 — correction et contrôles du rendu

## Problèmes corrigés

La PRE18 fusionnait des pixels de la couche de base et de la surcouche dans les mêmes UV. Ses tests prouvaient la création des maillages, sans prouver leur affichage. Dans le monde, le personnage d'aperçu avait aussi ses six surcouches invisibles et subissait la limite de distance de Skin Layers depuis sa position à l'origine du monde.

La PRE19 :
- conserve la composition originale des textures de la PRE17 et leurs deux jeux d'UV ;
- construit des voxels séparés pour les pixels cosmétiques de base et les pixels de surcouche, avec deux profondeurs distinctes ;
- utilise le vrai `SkinLayersAPI.MeshHelper` pour les cheveux, barbes classiques et vêtements, sans liste d'indices de rendu ;
- évite le gonflement global des bras et jambes ; limite les surfaces aux jonctions et éloigne les faces bouchées des faces plates ;
- active les couches du mannequin d'aperçu sans lui appliquer la limite de distance du monde ;
- conserve les contrôles natifs de distance, de casque et d'activation des parties pour les joueurs ordinaires ;
- applique le même rendu aux manches en première personne ;
- ignore les modèles d'armure ; les barbes culturelles déjà modélisées gardent leur chemin de rendu ;
- accepte la lecture des textures 128 × 128 en la normalisant à la grille 64 × 64 du maillage.

Tous les fichiers `assets/` de la PRE17 sont conservés octet pour octet. Les fichiers PNG ne sont pas redessinés.

## Environnement de contrôle

Minecraft 1.21.11, Java 21, Fabric Loader 0.19.5, Fabric API 0.141.6+1.21.11, 3d-Skin-Layers 1.10.2, Sodium 0.8.7+mc1.21.11 et ImmediatelyFast 1.14.2+1.21.11. Le client est lancé avec un vrai contexte OpenGL hors écran.

Ce contrôle reproduit les composants de rendu essentiels, pas la totalité des mods et packs de ressources du poste de l'utilisateur. Les captures ne permettent pas de garantir l'absence de tout défaut avec toute pose, texture ou combinaison future de mods.

## Contrôles effectués

`tools/RealGeometryTest.java` utilise les vraies classes Minecraft et Skin Layers :
- les 13 coiffures, 6 barbes classiques et 33 tenues actuelles ;
- les 98 combinaisons de cheveux et barbe, et les géométries de bras larges et fins ;
- des UV de base et de surcouche de couleurs différentes : aucune fusion, couleurs conservées et faces à des profondeurs différentes ;
- les limites des maillages aux jonctions des membres et l'absence de sommets non finis ;
- la lecture d'une texture 128 × 128 ;
- le rétablissement de `fastRender`, la reconstruction du mode de compatibilité et le rechargement des ressources ;
- les identifiants stables et non consécutifs de nouveaux assets, les barbes modélisées et les contraintes des nains.

Résultat :
```text
UV_LAYERS_AND_HD_PASS
REAL_GEOMETRY_PASS hairs=13 classicBeards=6 outfits=33 checks=4529626 vertices=1365696
```

`tests/PreviewHarness.java` lance le client réellement transformé par Fabric. Il capture neuf combinaisons depuis trois angles, puis neuf aperçus dans un monde intégré, le joueur en vue troisième personne et sa manche en première personne. Les compteurs vérifient que les deux couches sont effectivement dessinées après l'entrée dans le monde ; un simple message de préparation ne suffit plus.

Les données de test ajoutent des coiffures 14 et 1006, des barbes 7 et 9 et une tenue 39 dans les ressources :
```text
DYNAMIC_CATALOG_PASS hair=15 facial=8 outfits=34
RESOURCE_RELOAD_PASS
CREATION_UI_PASS hair=1006 facial=9 outfit=39
PREVIEW_HARNESS_PASS captures=27
```

Le contrôle de l'ordre des mixins confirme que notre installation suit celle de Skin Layers. Le hook de première personne intervient après la préparation native et avant la soumission du bras.

Les captures sélectionnées sont dans `evidence/`. Le rechargement et les boutons réels de création sont testés avec les nouveaux identifiants, et non seulement avec un appel direct au catalogue.

## Construction et ajout d'assets

Le workflow `.github/workflows/nexus-pre19-build.yml` compile les sources, applique les changements de bytecode à la PRE17 exacte, vérifie l'intégrité du JAR et publie la PRE19. Le script d'assemblage vérifie que les assets sont identiques à ceux de la base.

Les conventions et conditions d'ajout sont dans [ADDING_ASSETS.md](ADDING_ASSETS.md). Les nouveaux PNG compatibles sont sélectionnables et passent par ces mêmes maillages après redémarrage ou F3+T. Ils doivent être distribués aux clients.

SHA-256 du JAR :
```text
99a1129b4d38b09d9073a13180cb86332c7d58718a104bfa89a29e034605090e
```
