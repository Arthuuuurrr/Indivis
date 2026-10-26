# Nexus PRE35 SI1 OPT1 — vérifications

Correctif du calcul des surfaces 3D partagé par les joueurs et les PNJ Nexus/EasyNPC.
Le rendu 3D n’est ni désactivé ni remplacé par une version plate. Aucun LOD, saut d’animation ou arrondi de pose ajouté.

## Installation

Remplacer l’ancien JAR Nexus PRE35 SI1 par le JAR OPT1, sans garder deux Nexus dans `mods`.
Conserver la liaison Indivis-Nexus-NPC PREVIEW1, EasyNPC et 3D Skin Layers. Aucun nouveau fichier de configuration requis.
Les presets et assets existants sont conservés. Ce correctif n’ajoute pas les nouveaux presets de morphologie encore en préparation.

## Ce qui change

- Index des volumes candidats par grille ; fin des collectes/tri répétés pour chaque polygone.
- Bornes des fragments calculées une fois ; exclusion exacte des cellules qui ne peuvent rien recouper.
- Réutilisation par partie du corps seulement si les entrées et dépendances sont strictement identiques.
- Projections de sommets réutilisées pendant le test des six faces d’un même volume.
- Cache par workspace borné à six profils et 8 Mio estimés ; invalidation avec les états PRE35.

## Contrôles terminés

- 5 400 cas de découpe contre le code PRE35 conservé, y compris les rangs égaux et les ajouts à une grille.
- 10 368 tests de faces coplanaires/proches des seuils, normales inversées et transformations non uniformes.
- 16 320 138 polygones comparés en jeu sur les mêmes entrées, sans différence de topologie, coordonnées, normales ou UV.
- 9 244 réutilisations vérifiées par cet oracle exact.
- 216 états de PNJ/joueurs identiques entre processus PRE35 et OPT1, dont des poses continuellement nouvelles.
- 320 états de géométrie + 91 états d’invalidation/cache + 55 états de bras : identiques à PRE35.
- 23/23 captures de menus identiques pixel par pixel (création/sélection, cinq races, plusieurs échelles et rotations).
- Corps normal/slim, remplacement de cubes/shapes, changement de perspective et vidage des caches contrôlés.
- Nouveaux identifiants 930 de coiffure, barbe et tenue découverts et rendus sur un véritable EasyNPC (ressources ajoutées seulement pour le test).
- Rechargement d’un vrai monde de test : dix PNJ, UUID, recettes et cinq tags conservés ; vues de face/dos contrôlées.
- 4966 entrées d’assets inchangées octet pour octet. Classes réseau, persistance, UI et cosmétiques spécialisées inchangées.

## Mesures CPU

Temps du thread de rendu pour extraire l’état et préparer les modèles natifs. La colonne « entités » est le coût total du groupe par itération, pas un coût par individu.
Après 320 échauffements à trois entités en poses changeantes, puis 32 échauffements par cas : médiane de cinq séries de 24 itérations. Poses nouvelles, mêmes recettes et mêmes entrées avant/après.
Sans oracle ni enregistreur pendant le chronométrage final. Les allocations désignent les octets créés par itération, pas la mémoire conservée.
Ce ne sont pas des FPS : le dessin GPU, le reste du modpack et le coût d’une scène complète sont hors de cette mesure. Le client de test utilise un rendu logiciel.

| Type | Pose | Entités | PRE35 CPU (ms) | OPT1 CPU (ms) | Réduction CPU |
| --- | --- | ---: | ---: | ---: | ---: |
| PNJ | Immobile | 1 | 0.066 | 0.053 | 19.7 % |
| PNJ | Immobile | 3 | 0.164 | 0.147 | 9.9 % |
| PNJ | Immobile | 8 | 0.404 | 0.405 | -0.1 % |
| PNJ | Tête mobile | 1 | 10.591 | 5.281 | 50.1 % |
| PNJ | Tête mobile | 3 | 45.006 | 21.384 | 52.5 % |
| PNJ | Tête mobile | 8 | 134.670 | 57.019 | 57.7 % |
| PNJ | Marche | 1 | 9.711 | 6.087 | 37.3 % |
| PNJ | Marche | 3 | 53.955 | 24.255 | 55.0 % |
| PNJ | Marche | 8 | 126.075 | 71.859 | 43.0 % |
| Joueur | Immobile | 1 | 0.349 | 0.249 | 28.7 % |
| Joueur | Immobile | 3 | 0.853 | 0.753 | 11.7 % |
| Joueur | Immobile | 8 | 1.996 | 2.169 | -8.7 % |
| Joueur | Tête mobile | 1 | 32.451 | 5.208 | 84.0 % |
| Joueur | Tête mobile | 3 | 64.139 | 21.711 | 66.2 % |
| Joueur | Tête mobile | 8 | 153.306 | 70.185 | 54.2 % |
| Joueur | Marche | 1 | 19.627 | 6.611 | 66.3 % |
| Joueur | Marche | 3 | 50.862 | 30.667 | 39.7 % |
| Joueur | Marche | 8 | 251.278 | 83.478 | 66.8 % |

Tous les cas et les allocations figurent dans MESURES_CPU.csv ; les résultats moins favorables ne sont pas omis.

## Portée et limites

Les tests portent sur Minecraft 1.21.11, Fabric 0.19.5, Nexus PRE35 SI1/OPT1, la liaison PREVIEW1 et EasyNPC 7.16.0, avec les dépendances du banc de test.
Le modpack de production complet, les véritables sorts et la caméra de dialogue n’ont pas été reproduits ici. Le chemin partagé des joueurs a été testé avec de vrais OtherClientPlayer de test, sans connexion Mojang authentifiée.
Le compte hors ligne synthétique produit les avertissements PRE35 de manifest/owner du vault v0.7.1 : la persistance des personnages de production n’est donc pas déclarée validée. Le rechargement NBT EasyNPC est, lui, testé séparément.
Aucun déploiement sur le serveur de production ni aucune fusion dans main. Les gains de ce banc ne signifient pas disparition de tous les ralentissements du modpack.

## Identité du fichier

SHA-256 OPT1 : `bdb1525d5e4448cb8127e38bdcdce063d629b7039a856bf9ea74be5ea06dea11`
SHA-256 PRE35 SI1 : `399a4c64a7672e60ebbaaa52044cb547f5c41576039fe5aafe2c42485e2e8a8a`
