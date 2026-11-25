# Nexus BB1.OPT1 — correctif et vérifications du 25 novembre 2026

## Installation

Fabric **1.21.11** : remplacer le précédent JAR Nexus par `Nexus-BB1-OPT1-1.21.11.jar`. Ne pas garder deux versions de Nexus dans `mods/`. Conserver le mod de liaison Indivis–Nexus–NPC pour EasyNPC. Aucun preset, monde ni fichier de personnage n’est à supprimer.

Version interne : `1.0.0-beta.35+indivis.1.21.11.SI1.OPT3.BB1.OPT1`. SHA-256 : `5017ec3f87d4d72053855341050ada3bd80f262e4238c32d0da1bc982842f5e3`.

## Ce qui change

- Les variantes de couleur réutilisent les préparations géométriques identiques, sans partager leurs textures ni leur identité. L’égalité vérifie positions, UV, normales, rangs et volumes, bit pour bit.
- Découpe et écriture des surfaces avec moins d’objets temporaires ; réutilisation des listes et wrappers de travail, sans réutiliser les buffers finaux du rendu différé.
- Cache de parties : au plus 32 profils / 16 Mio estimés par modèle, avec quatre variantes exactes par partie. Cache local canonique : 8 Mio par modèle ; interner géométrique : 256 clés / 8 Mio estimés. Ces plafonds sont des budgets de cache, pas des mesures de RAM totale.
- Invalidation conservée sur changement de corps/cubes, remplacement de forme et rechargement des ressources. Aucune liste de coiffures, barbes ou vêtements n’est codée en dur dans cette optimisation.
- Pas de réduction de 3D, de distance de détail, de cadence des animations ni de précision des poses. Les 5 191 entrées `assets/` de la BB1 sont identiques. Répartition des presets, couleurs et options des menus inchangées.
- Une mémoïsation globale des transformations a été essayée puis désactivée : sa gestion coûtait trop sur les poses distinctes. Les essais exploratoires ne sont pas mélangés aux mesures finales.

## Mesures finales A/B

Six clients réels, exécutés successivement A1/B1/B2/A2/A3/B3 : trois BB1 d’origine, trois JAR finaux strictement identiques à celui livré. **6240 images mesurées** après échauffement. Mêmes classes de mesure et mêmes dépendances, vérifiées par SHA-256. Aucun client concurrent.

Animations tête/membres distinctes par entité et caméra en orbite ; 0/3/8/24 PNJ EasyNPC, 3/8 joueurs OtherClientPlayer. Pour chaque image, le nombre d’appels de couche cosmétique doit être au moins égal au nombre de sujets prévu. Les recomputations continuent : les animations ne sont pas figées.

Deux familles de scènes, comparées chacune à rendu constant entre A et B : anciens assets, puis nouveaux modèles Blockbench chargés avec douze teintes. Cinq profils hérités : haut-elfe, elfe des bois, nordique, nain et naine ; ce ne sont pas cinq races distinctes.

Pour chaque scène : 36 images d’échauffement puis 96 mesurées ; pour 24 PNJ : 20 puis 40. Les chiffres ci-dessous sont les médianes des trois médianes ; le CSV conserve les P95 et les bornes entre exécutions.

**Temps CPU du thread de rendu, en millisecondes par image — pas des FPS prédits sur le PC du joueur.**

| Scène animée | Assets | BB1 (ms) | OPT1 (ms) | Baisse CPU | Allocations surfaces BB1 → OPT1 (Mio/image) |
|---|---|---:|---:|---:|---:|
| 3 PNJ | Origine | 23,68 | 23,51 | 0,70 % | 14,31 → 9,62 |
| 8 PNJ | Origine | 53,97 | 53,31 | 1,22 % | 38,70 → 26,10 |
| 24 PNJ | Origine | 156,57 | 151,30 | 3,36 % | 117,43 → 79,15 |
| 3 PNJ | Nouveaux modèles | 17,99 | 17,93 | 0,38 % | 10,84 → 7,26 |
| 8 PNJ | Nouveaux modèles | 40,71 | 41,92 | -2,97 % | 29,06 → 19,53 |
| 24 PNJ | Nouveaux modèles | 230,86 | 118,65 | 48,61 % | 197,68 → 63,98 |
| 3 joueurs | Origine | 22,41 | 22,20 | 0,93 % | 14,34 → 9,66 |
| 8 joueurs | Origine | 53,16 | 53,93 | -1,44 % | 38,77 → 26,20 |
| 3 joueurs | Nouveaux modèles | 17,98 | 17,86 | 0,66 % | 10,91 → 7,32 |
| 8 joueurs | Nouveaux modèles | 42,49 | 42,88 | -0,92 % | 30,89 → 21,37 |

Une baisse négative signifie un ralentissement mesuré, pas un gain. Les scènes d’origine et de nouveaux modèles ne doivent pas être comparées entre elles pour attribuer un gain au correctif.

**Lecture des résultats : le gain CPU est surtout net pour les 24 PNJ aux recettes variées. Sur les petits groupes, les écarts restent faibles ou variables ; ce test ne démontre pas une amélioration CPU uniforme. La réduction des allocations de surfaces est en revanche constatée dans les deux familles de scènes.**

Aucun nouveau bake Blockbench ni aucune nouvelle texture recolorée pendant les plages mesurées après échauffement. Aucun des chronométrages n’utilise les oracles de vérification ni le contrôle diagnostique à poses figées.

## Vérifications du JAR final

- **116 042 comparaisons** : 20 000 polygones aléatoires, découpes six plans, transformations/écriture, 16 000 nettoyages indexés, cas dégénérés, seuils epsilon, signes de zéro, changements d’UV et de forme. Comparaisons exactes des bits flottants à la BB1.
- **411 états de géométrie identiques à la BB1** : 320 états de corps classique/slim et perspectives + 91 états de poses/invalidation ; 55 états de bras en première personne ; 23 écrans et contrôles des sliders, rotation, couleurs et HUD.
- Catalogue complet des **91 modèles**, 182 captures face/dos et 76 variantes de teintes dans le client réel ; inspection visuelle complémentaire des captures finales, sans rendu de substitution.
- PNJ : cinq nouveaux profils raciaux, cinq anciens profils et cinq presets EasyNPC natifs, export éditeur, conservation des champs/UUID/tags et rechargement d’une sauvegarde isolée de 15 PNJ.
- Nouveaux assets : trois PNG d’essai supplémentaires (coiffure, barbe classique et tenue, ID 930), découverts automatiquement puis affichés sur un vrai PNJ EasyNPC. Le JAR livré n’a pas été modifié pour ce test.
- Oracles supplémentaires activés dans un client séparé : **552 états PNJ/joueurs animés**, dont 14 585 042 comparaisons de découpe. Modification de la configuration Skin Layers 1,05 → 1,15 → 1,05 et purge des caches. Ces compteurs incluent les réexécutions de référence, pas autant de poses distinctes.
- API commune : **292 vérifications avec classes client interdites**. Il s’agit d’un contrôle de liaison de l’API, pas du démarrage complet d’un serveur dédié.
- Audit binaire : **273 classes inchangées**, 39 corps de méthodes préservés dans les classes ciblées ; aucune classe du banc de mesure ni de pose figée incluse dans le JAR. Les 5 191 entrées d’assets sont inchangées.

## Portée

Rendu OpenGL logiciel **llvmpipe**, Java 21, quatre processeurs déclarés à Java et quatre threads du moteur logiciel ; 800 × 450, distance de rendu 2, VSync désactivée, sans shaderpack. EntityCulling est exclu du banc pour ne pas masquer des sujets ; cette exclusion ne modifie pas le JAR livré.

Les gains ci-dessus portent sur la préparation/rendu des personnages dans ces scènes isolées. Ils ne couvrent pas le coût complet de Lion-Port, des sorts, des particules, des shaders ou du reste du modpack. Aucun FPS matériel n’est extrapolé. Les avertissements Mojang/Realms hors ligne et de persistance non configurée des mondes de test ne sont pas une validation du réseau de production.

Le ZIP contient le JAR installable, ce rapport, les CSV et les preuves synthétiques. Les profils bruts JVM et leurs variables d’environnement ne sont pas distribués.

Code, générateur et instantané du banc : [PR #188](https://github.com/Arthuuuurrr/Indivis/pull/188), laissée en brouillon pour revue ; aucun déploiement ni fusion automatique.
