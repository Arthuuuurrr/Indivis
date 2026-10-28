# Nexus PRE35 SI1 OPT3 — vérifications du 28 octobre 2026

Version : `1.0.0-beta.35+indivis.1.21.11.SI1.OPT3`. Correctif cumulatif : OPT1 et OPT2 restent inclus.

## Installation

Fermer complètement Minecraft. Remplacer l’ancien JAR Nexus dans le dossier `mods` de l’instance réellement utilisée ; ne conserver qu’un seul JAR Nexus. Aucun nouveau fichier de configuration, preset ou asset à installer.
Conserver la liaison Indivis-Nexus-NPC PREVIEW1, EasyNPC et Skin Layers. Le correctif de rendu doit être chargé côté client. Aucun déploiement automatique sur le serveur.

Ligne attendue dans le journal du client après redémarrage :

```text
nexuscharacters 1.0.0-beta.35+indivis.1.21.11.SI1.OPT3
```

## Modifications conservées

- Volumes et cellules réutilisés par membre lorsque les matrices exactes, la base et l’identité de l’asset restent identiques. Cache propre à chaque espace de pose réentrant, limité à six entrées par membre et huit Mio estimés, avec éviction LRU. Les cas à rangs égaux gardent la construction d’origine.
- L’index reçoit directement les volumes déjà connus, sans reparcourir toutes les listes d’occupation pour les retrouver. Les mêmes volumes sont ensuite triés dans le même ordre de rang.
- Le découpeur conserve deux listes de sommets intérieurs temporaires dans un espace de travail propre au thread. Ces listes ne sont jamais retournées ; elles sont vidées après chaque appel pour ne pas retenir les sommets des personnages.
- Les fragments extérieurs retournés gardent leurs propres listes, dimensionnées au besoin. Aucune réutilisation d’une liste encore visible par un autre personnage ou une autre image.
- Même ordre des six plans, mêmes opérations flottantes JOML, seuils, normales, UV et rangs. Le découpeur OPT2 original est conservé dans le JAR comme oracle.
- Aucun LOD, retrait de relief 3D, saut d’animation, changement d’espacement ou modification des assets.

## Essais écartés

Un premier cache de deux Mio vidait trop de données ; il est écarté. Les volumes seuls et le découpeur seul ont ensuite été chronométrés séparément : ces variantes n’ont pas été retenues. Leur association avec éviction LRU donne les meilleurs résultats dans les essais animés effectués. Les mesures de chaque essai et les sources des variantes actuelles sont conservées pour traçabilité. Le résultat d’une combinaison ne se déduit pas de l’addition de deux micro-optimisations.
La préparation des entités masquées n’a pas été modifiée sans preuve du coût dans un profil actuel du pack complet.

## Vérifications sur les octets livrés

- Compilation déterministe avec Java Temurin 21.0.13+9 ; 4966 entrées d’assets identiques à OPT2 octet pour octet.
- 2400 cas de transformation, 28800 polygones ; 10368 cas de faces extérieures ; 5400 configurations de découpe.
- 6192 cas dédiés au découpeur compact : valeurs flottantes brutes, UV, normales, rangs, matrices singulières, valeurs non finies, grands polygones et seuils EPS exacts/adjacents. Les sorties conservées restent identiques après les appels suivants.
- 1035 cas de volumes, changements de pose/base/identité de forme et pression mémoire ; éviction et limite de huit Mio exercées.
- Oracle des grilles en client : 770 reconstructions comparées, 5511424 occupations de volumes identiques ; 2068 réutilisations exercées.
- Oracle dans le vrai client : 9521350 appels de découpe compacte et 10239800 fragments comparés au découpeur OPT2, sans différence.
- Oracle global : 9400 nettoyages / 12630284 polygones ; 4134855 polygones transformés comparés valeur par valeur.
- 216 poses animées PNJ/joueurs identiques entre processus OPT2 et OPT3 ; recalculs réellement déclenchés pendant les mesures.
- 23/23 captures de menus identiques pixel par pixel à OPT2, dont création, sélection, races et rotation.
- 466 états supplémentaires de géométrie/caches/bras identiques à la référence, corps normal et slim.
- Vrais PNJ EasyNPC, vrais modèles OtherClientPlayer, import natif, export par clic dans l’éditeur, réutilisation du modèle sans fuite sur un PNJ ordinaire.
- Coiffure, barbe classique et tenue portant les nouveaux IDs 930 ajoutées seulement dans les ressources du banc : découverte dynamique et rendu sur un véritable PNJ.
- Option Skin Layers changée à chaud 1.05 → 1.15 → 1.05 : rendu distinct puis restauration.
- Monde réellement fermé puis rouvert : dix PNJ, leurs UUID et recettes, et cinq tags témoins conservés. Vues face/dos contrôlées.

## CPU et allocations

Mesure du thread de rendu : extraction de l’état et préparation native groupée des modèles. Ce n’est ni le temps de rendu complet ni une mesure de FPS de Lion-Port. Le dessin GPU, le monde et la soumission des accessoires ne sont pas chronométrés ici.
Processus séparés dans le même environnement ; mêmes recettes et poses. Échauffement global de 320 passages à trois entités, puis 1500 passages statiques ou 64 animés par cas. Médiane de cinq séries de 24 passages. L’oracle est désactivé pendant les chronométrages. Le nombre de recalculs est vérifié : 0 statique, 24 × nombre d’entités pour chaque série animée.
Tous les résultats sont fournis, y compris les cas statiques défavorables. Les variations d’un processus à l’autre ne constituent pas une amélioration de FPS garantie.

| Type | Pose | Entités | OPT2 CPU ms | OPT3 CPU ms | OPT2 Mio alloués | OPT3 Mio alloués |
| --- | --- | ---: | ---: | ---: | ---: | ---: |
| PNJ | Immobile | 1 | 0.030 | 0.032 | 0.009 | 0.009 |
| PNJ | Immobile | 3 | 0.077 | 0.077 | 0.028 | 0.028 |
| PNJ | Immobile | 8 | 0.173 | 0.178 | 0.075 | 0.075 |
| PNJ | Tête mobile | 1 | 3.500 | 2.826 | 3.292 | 2.752 |
| PNJ | Tête mobile | 3 | 13.667 | 11.429 | 13.140 | 10.885 |
| PNJ | Tête mobile | 8 | 36.171 | 32.276 | 35.791 | 29.642 |
| PNJ | Marche | 1 | 4.135 | 3.858 | 3.985 | 3.587 |
| PNJ | Marche | 3 | 16.750 | 15.456 | 15.998 | 14.335 |
| PNJ | Marche | 8 | 45.459 | 42.286 | 43.311 | 38.723 |
| Joueur | Immobile | 1 | 0.119 | 0.149 | 0.038 | 0.038 |
| Joueur | Immobile | 3 | 0.377 | 0.326 | 0.111 | 0.111 |
| Joueur | Immobile | 8 | 0.751 | 0.808 | 0.291 | 0.291 |
| Joueur | Tête mobile | 1 | 3.402 | 2.862 | 3.305 | 2.755 |
| Joueur | Tête mobile | 3 | 13.983 | 11.998 | 13.239 | 10.956 |
| Joueur | Tête mobile | 8 | 42.212 | 37.659 | 39.947 | 33.171 |
| Joueur | Marche | 1 | 4.360 | 4.196 | 4.010 | 3.615 |
| Joueur | Marche | 3 | 17.402 | 16.771 | 16.106 | 14.451 |
| Joueur | Marche | 8 | 47.020 | 43.340 | 43.540 | 38.990 |

## Portée

Client Minecraft 1.21.11 / Fabric Loader 0.19.5, rendu logiciel llvmpipe ; EasyNPC 7.16.0, liaison PREVIEW1 inchangée, Skin Layers, Sodium, ImmediatelyFast, Iris, PAL, owo, Cloth Config, Pufferfish Skills et HUD OPT1.
Le modpack de production complet, les vrais sorts et caméra de dialogue, ainsi qu’un compte Mojang authentifié ne sont pas reproduits dans cette session. La baisse de FPS complète restante doit être localisée avec un profil du client chargeant effectivement OPT3.
Les avertissements préexistants de vault hors ligne (manifeste propriétaire v0.7.1 absent) restent présents. La persistance des personnages joueurs n’est pas déclarée validée ; son code reste identique à OPT2. La persistance des vrais PNJ est testée séparément.
Les nouveaux assets restent soumis au format et au catalogue Nexus. Le test des trois nouveaux IDs n’est pas une validation de PNG arbitraires mal formés.
Aucune fusion dans main, aucun déploiement automatique sur le serveur.

## Identité

SHA-256 OPT3 : `de06ecab0f5cf7ccee29043bff2ccb5fd2b7425c173da145c1f27559b49def7b`
SHA-256 OPT2 : `7491ec3feb751b37ff0fe3773b4bb6d75c631220913c97b8e22143d9356fc116`
