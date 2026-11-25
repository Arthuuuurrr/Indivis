# Nexus ARM1 — personnages préparés et vérification

## Installation

Fabric **1.21.11** : remplacer le précédent JAR Nexus par `Nexus-ARM1-1.21.11.jar`. Une seule version de Nexus dans `mods/`. Conserver le mod de liaison Indivis–Nexus–NPC pour EasyNPC. Aucun monde, preset, personnage ou asset à supprimer. Le JAR est cumulatif : les correctifs OPT1 restent inclus.

Version interne : `1.0.0-beta.35+indivis.1.21.11.SI1.OPT3.BB1.OPT1.ARM1`. SHA-256 : `06534c00ad12c3f7fdf3e3f16495c71976e74e5bf31df6b4178c12d441ca4f84`.

## Comportement « comme une armure »

La géométrie locale des pièces du personnage est préparée au premier affichage d’une apparence, conservée et animée par les transformations ordinaires des os du modèle. Elle n’est pas régénérée intégralement à chaque pose. Cette préparation est côté client, sans nouveau format de personnage ni fichier de modèle à gérer.

Il s’agit d’un pipeline **hybride**, pas d’une armure rigide qui ignorerait les chevauchements : les faces pouvant toucher une autre partie conservent la découpe animée OPT1. Les segments rigides sont réutilisés, les segments de contact restent recalculés. C’est nécessaire ici pour préserver les raccords et l’occlusion aux épaules, hanches et cou. L’ordre de soumission des faces reste conservé.

- Hiérarchies spatiales locales préparées et volumes d’occlusion actualisés aux poses ; aucune fusion de volumes utilisée pour remplacer la découpe. Buffers finaux immuables, sûrs pour le rendu différé.
- Sources : cache partagé borné à 32 Mio estimés / 256 sources. Topologies d’occlusion : 16 Mio estimés / 32 recettes. Ce sont des budgets de cache, pas une mesure de la RAM totale. Une apparence peut être préparée à nouveau après éviction ou rechargement des ressources. Purge sur le rechargement natif ; le chemin OPT1 reste disponible pour les cas non pris en charge.
- Le mécanisme traite les surfaces natives : cheveux, vêtements, barbes classiques, corps et raccords. Les modèles Blockbench déjà préparés conservent leur pipeline. Aucune liste d’assets n’est codée en dur dans ARM1 ; les nouveaux assets reconnus par le catalogue existant en bénéficient.
- Aucun LOD, animation figée, réduction de fréquence ou suppression de détail. Menus, couleurs, répartition des presets et chemin dédié des bras en première personne inchangés. Les **5 191 entrées d’assets** sont byte pour byte identiques à l’OPT1.

## Mesures finales, OPT1 → ARM1

Six clients Minecraft réels, successifs A1/B1/B2/A2/A3/B3, trois exécutions par version et **6240 images mesurées** après échauffement. Même code du banc et mêmes dépendances vérifiés par SHA-256 ; pas de client concurrent, JFR, oracle de vérification ou diagnostic à poses figées dans ces chronométrages. Le binaire final livré est celui des trois exécutions B.

Caméra en orbite, têtes et membres animés distinctement par sujet. 0/3/8/24 PNJ EasyNPC ; 3/8 joueurs OtherClientPlayer. Chaque image contient au moins le nombre prévu d’appels de couche cosmétique. Les compteurs de préparation de pose continuent d’évoluer : les poses ne sont pas gelées.

36 images d’échauffement puis 96 mesurées par scène ; 20 puis 40 pour 24 PNJ. Valeurs ci-dessous : médiane de trois médianes. Les CSV/JSON incluent les P95 et bornes entre exécutions. Comparer chaque famille à elle-même, pas anciens assets et modèles Blockbench entre eux.

**Temps CPU du thread de rendu en ms/image, pas des FPS prédits sur le PC du joueur.**

| Scène animée | Assets | OPT1 (ms) | ARM1 (ms) | Baisse CPU | Allocations surfaces OPT1 → ARM1 (Mio/image) |
|---|---|---:|---:|---:|---:|
| 3 PNJ | Origine | 23,53 | 22,03 | 6,35 % | 9,62 → 8,77 |
| 8 PNJ | Origine | 56,60 | 50,22 | 11,26 % | 26,09 → 23,85 |
| 24 PNJ | Origine | 152,47 | 145,77 | 4,39 % | 79,13 → 72,60 |
| 3 PNJ | Modèles Blockbench | 17,97 | 17,19 | 4,35 % | 7,26 → 6,74 |
| 8 PNJ | Modèles Blockbench | 43,13 | 39,00 | 9,58 % | 19,52 → 18,12 |
| 24 PNJ | Modèles Blockbench | 115,11 | 112,71 | 2,08 % | 63,96 → 59,77 |
| 3 joueurs | Origine | 23,02 | 21,78 | 5,40 % | 9,66 → 8,78 |
| 8 joueurs | Origine | 52,73 | 49,49 | 6,15 % | 26,19 → 23,93 |
| 3 joueurs | Modèles Blockbench | 17,35 | 17,05 | 1,75 % | 7,31 → 6,78 |
| 8 joueurs | Modèles Blockbench | 41,61 | 41,72 | -0,26 % | 21,37 → 19,94 |

Une baisse négative indique un ralentissement mesuré. Les variations entre exécutions doivent être consultées avant d’interpréter un petit écart comme un gain robuste. Ces résultats sont des gains additionnels à OPT1 ; les anciens pourcentages BB1 → OPT1 ne sont pas réattribués à ARM1.

Dans les **30 plages animées avec personnages du candidat**, après échauffement : **aucune reconstruction de source ni de topologie d’occlusion**, aucun recours nouveau au chemin de secours. Aucun nouveau bake Blockbench ni recoloration de texture pendant les plages mesurées. La proportion de polygones conservés rigides varie de **63,64 à 68,11 %** selon la scène. Le reste demeure dynamique : ARM1 ne supprime pas tout le coût de découpe.

## Vérifications du même JAR

- **6 266 comparaisons numériques**, dont 3 000 cas aléatoires de 32 polygones, 128 poses réutilisant effectivement le même tableau rigide, 128 poses à 144 volumes d’occlusion, grilles modifiées et seuils. Topologie, ordre et UV contrôlés.
- **411 états géométriques** (320 corps/perspectives + 91 poses/invalidation), 23 captures des menus. Les 126 lignes de première personne sont identiques en bits à l’OPT1. Les surfaces mondiales sont comparées en cours d’exécution au chemin OPT1 conservé.
- **91 modèles**, 182 captures face/dos, variantes de couleurs et 328 557 assertions dans le client réel. Inspection visuelle des neuf planches et de captures pleine résolution, dont les cheveux longs dans le dos et les PNJ face/dos.
- PNJ classiques/slim, cinq profils raciaux nouveaux et cinq hérités, cinq presets EasyNPC natifs ; sauvegarde puis recharge réelle de **15 PNJ** avec UUID, tags, recettes et champs d’éditeur conservés. Retour à un PNJ ordinaire vérifié.
- **552 états animés PNJ/joueurs** en vérification séparée ; 9900 préparations comparées au résultat OPT1. Test de configuration Skin Layers 1,05 → 1,15 → 1,05 et purge des caches.
- **Nouveaux assets testés sans modifier le JAR** : coiffure, barbe classique et tenue ID 930 découvertes automatiquement et rendues sur un vrai PNJ EasyNPC.
- Audit binaire : **297 classes existantes inchangées**, 26 corps de méthodes préservés dans les deux classes ciblées ; seuls les points de préparation/purge sont redirigés. Aucune classe du banc ou de diagnostic incluse dans le JAR. API commune : **292 contrôles** avec classes client interdites ; ce n’est pas un démarrage complet de serveur dédié.

## Précision du rendu

Les portions rigides évitent l’ancien aller-retour monde/inverse. Leurs coordonnées locales peuvent donc changer de quelques arrondis flottants : ce n’est pas une égalité bit pour bit pour l’ensemble des 411 états. Le contrat vérifié est : même nombre/ordre de quads, UV exacts en bits, positions et normales à `2e-6 + 8 ulp` par composante. Aucun arrondi ni quantification des poses n’est ajouté. Les zones de contact gardent l’algorithme original.

Écarts maximaux observés dans le test aléatoire : position `2,98e-7`, normale `2,15e-7`. Dans le client animé avec PNJ : position `2.980232238769531E-7`, normale `1.1920927533992653E-7`. Les images fixes ne suffisent pas à démontrer l’absence de scintillement pour tous les shaders et toutes les combinaisons possibles ; elles complètent les contrôles géométriques animés.

## Portée et limites

OpenGL logiciel **llvmpipe**, Java 21, quatre processeurs déclarés à Java et quatre threads de rendu logiciel, 800 × 450, distance de rendu 2, VSync désactivée, sans shaderpack. EntityCulling est exclu du banc afin de garder tous les sujets visibles ; cette exclusion ne modifie pas le JAR livré.

Les tests isolent la préparation/rendu des personnages et ne reproduisent pas tout Lion-Port, la canalisation des sorts, les particules ou le reste du modpack. Les tableaux portent sur le régime chaud : ils ne mesurent pas le coût du tout premier affichage ou d’un rechargement complet des ressources. Aucun gain de FPS matériel ni suppression universelle des ralentissements n’est affirmé. L’architecture est modifiée et vérifiée dans le périmètre ci-dessus ; elle ne rend pas gratuites les zones de contact encore dynamiques.

Code, générateur, tests et instantané du banc : [PR #190](https://github.com/Arthuuuurrr/Indivis/pull/190), brouillon pour revue. Pas de fusion ni déploiement automatique. Le ZIP contient le JAR identique, ce rapport, les mesures et des preuves de contrôle ; aucun profil JVM brut ni variable d’environnement.
