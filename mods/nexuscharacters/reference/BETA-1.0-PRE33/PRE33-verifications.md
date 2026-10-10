# Indivis — PRE33 et HUD OPT1 — vérifications du 10 octobre 2026

La PRE33 conserve le calcul 3D de la PRE32 et étend la réutilisation de ses résultats. Le HUD OPT1 évite de rechercher à nouveau les mêmes méthodes, y compris les méthodes absentes. Les gains mesurés concernent les préparations retrouvées dans le cache ; une pose entièrement nouvelle garde le coût de découpe.

## Installation

Remplacer NexusCharacters PRE32 par `NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE33.jar`. Sur le client, remplacer le HUD `BETA_1_3_3_RACE_HEALTH_MODIFIER_COMPAT` par `capitale_rp_hud_BETA_1_3_3_OPT1_1.21.11.jar`. Garder un seul JAR par mod. Aucun fichier de configuration supplémentaire. Conserver les dépendances habituelles. Le HUD serveur peut conserver sa 1.3.3 : les classes serveur et le protocole réseau sont identiques.

## Ce qui change

- Le modèle complet conserve jusqu'à 64 préparations locales, indexées par partie, template, identité de la forme native et matrice relative exacte. Un détachement courant n'abandonne plus ces préparations.
- Jusqu'à 16 poses complètes sont conservées par modèle, avec un plafond de 16 Mio pour leurs tableaux de sommets. Les clés comprennent les templates, les formes, les 12 matrices et les 6 matrices relatives, ainsi que le contexte. Les matrices sont copiées dans les clés ; aucune rotation n'est arrondie.
- Les changements de pièces ou d'identités des cubes invalident la géométrie concernée. Une nouvelle forme sous un template existant ne récupère pas les anciennes surfaces. Un rechargement libère les préparations et détache les anciennes injections. Les enfants du modèle sont relus indépendamment des tableaux de sommets.
- LowerJointSupport mémorise la résolution des champs valides et absents, avec le même parcours des superclasses. Les lectures et écritures relisent les objets courants. Les adaptations de cubes et de Sodium restent intactes.
- Le HUD mémorise la résolution des méthodes sans argument, avec le même ordre déclaré/public/superclasses et le même comportement de repli. Les scores ne sont pas mémorisés.

Le cache indépendant des mains de la PRE32 est conservé octet pour octet. Le clipping, les espacements, les rangs, les UV, les normales, les assets et les interfaces restent ceux de la PRE32.

## Comparaisons et contrôles effectués

| Contrôle | Résultat |
| --- | --- |
| Surfaces de référence | 320 états classic/slim, nouvelles formes, poses animées et transitions première/troisième personne : empreintes identiques PRE32/PRE33 |
| Cache complet et invalidations | 91 états supplémentaires : empreintes identiques, dont détachements, évictions, remplacement/restauration de cubes, nouvelle forme sous le même template et remise à zéro |
| Menus | 23 captures, cinq races et plusieurs échelles d'interface : pixels identiques PRE32/PRE33 ; 688 assertions |
| Mains | Régression native : 654 assertions et 55 instantanés sur le candidat ; helper de production PRE32 identique dans le JAR |
| Nouveaux PNG hors JAR | Coiffure 930, barbe 930 et tenue 930 reconnues ; corps 2, classic/slim et surfaces natives : 23 assertions |
| Monde réel de test | 18 scénarios, 138 assertions sur chaque version ; 560 images de rendu contrôlées sur le candidat, 593 sur le témoin |
| Géométrie en monde | Cinq comparaisons par version entre l'état courant et une découpe refaite sans préparation conservée : identiques |
| Réflexion du HUD et des articulations | 2 518 assertions sur le candidat final : lectures/écritures changeantes, objets distincts, méthodes héritées/privées/d'interface, champs absents, repli sur exception et arguments nuls |
| Scoreboard Minecraft | Six changements de valeur puis suppression d'objectif : lectures identiques via un détenteur compatible ; repli du joueur natif identique |
| Audit du bytecode | 105 méthodes préservées ; huit points Nexus et un résolveur privé HUD ciblés ; calculs des surfaces et lectures de scores préservés |
| Archives | ZIP intègres ; reconstruction déterministe des deux JAR à partir de leur base et du patch de classes ; empreintes contrôlées |

Les scénarios en monde couvrent les mains ordinaires et animées, la troisième personne, l'inventaire, F1, une carte à deux mains, un rechargement réel, une passe de modèle supplémentaire, un détachement avant les mains, un changement de variante, le modèle complet de Player Animation Library, l'ouverture et la fermeture de la caméra réelle de dialogue, l'animation `off_hand_channeling` de Spell Engine et son arrêt. Le test de canalisation active le contrôleur et l'asset d'animation réels ; il ne simule pas un combat, des dégâts ou tous les effets de chaque sort.

Le contrôle de scoreboard utilise le vrai `class_269` et ses vrais scores, avec un objet de test exposant le getter attendu par le HUD. Dans Minecraft 1.21.11, le joueur natif n'expose pas les deux noms de getter recherchés par la 1.3.3 ; son repli à zéro demeure identique. OPT1 optimise ce chemin existant et conserve la synchronisation réseau et la priorité des sources de valeurs. Il ne change pas ce mécanisme en introduisant une nouvelle source de scores.

## Mesures du calcul de surfaces

Même client logiciel et mêmes mods de compatibilité pour les deux mesures finales. Après échauffement, trois échantillons par cas, temps CPU médian du thread de rendu par appel, en millisecondes. Les appels incluent l'application des poses du banc. Le nombre de découpes indiqué est celui de chacun des trois échantillons PRE33.

| Cas | PRE32, ms/appel | PRE33, ms/appel | Découpes PRE33 par échantillon |
| --- | ---: | ---: | --- |
| Alternance de deux poses connues | 68.7189 | 0.0173 | 0,0,0 |
| Même pose après détachement du modèle | 63.7911 | 0.2675 | 0,0,0 |
| Alternance de quatre poses connues | 71.4897 | 0.0089 | 0,0,0 |
| Alternance de huit poses relatives connues | 75.1520 | 0.0092 | 0,0,0 |
| Poses de base continuellement nouvelles | 9.0771 | 10.1303 | 40,40,40 |
| Poses relatives continuellement nouvelles | 77.9220 | 77.0869 | 40,40,40 |

Sur les poses connues, le témoin refait 60 à 80 découpes par échantillon et le candidat zéro. Les tableaux sont réutilisés ; les cas alternant deux, quatre ou huit poses n'allouent plus de données de géométrie pendant leurs échantillons chauds. Après un détachement, le candidat recrée encore l'état d'injection, environ 50,6 ko par appel dans ce banc, tout en évitant la découpe.

Les poses relatives continuellement nouvelles restent autour de 78 ms/appel dans ce scénario dense. Le cas de poses de base nouvelles mesure environ 9,08 ms sur le témoin et 10,13 ms sur le candidat : ce cas n'est pas amélioré, et ces trois échantillons ne permettent pas d'attribuer cette différence à une variation de mesure ou au surcoût du cache. Ces chiffres sont conservés dans le rapport. Il n'y a pas de facteur de gain global de FPS déduit de ces mesures.

## Périmètre des fichiers

Nexus : 5213 entrées originales identiques, dont les 4966 assets. Quatre entrées modifiées : les métadonnées et trois classes ciblées. Six classes de support ajoutées. Aucune entrée supprimée.

HUD : 148 entrées originales identiques, dont les 34 assets. Seuls les métadonnées et le résolveur dans CapitaleRpHudClient changent ; deux classes ajoutées. Les autres classes client, classes serveur, mixins et messages réseau sont identiques.

| Fichier | SHA-256 |
| --- | --- |
| Nexus PRE32 original | `203fb5e54098836c34a375723a8ce234c5aa884739f4a41113120db5a5465032` |
| Nexus PRE33 livré | `98bd9018e05c5cd072bf62885f58a20602e81c664551dac807e5358cb365fcc6` |
| HUD 1.3.3 original | `f12179dd3f10d6870d28ad864e01f0e16bd5c9ef5156a346c77f3e10809de145` |
| HUD OPT1 livré | `ad50aa33f2767be60ff517d7c7b965961c4392dae29a13c03b3ea5404ee6adab` |

## Environnement et portée

Minecraft 1.21.11, Java Temurin 21.0.12.1, Fabric Loader 0.19.5, rendu Mesa llvmpipe OpenGL 4.5 et écran de test 1920×1080. Dépendances pertinentes : Fabric API 0.141.6, Skin Layers 1.10.2, Sodium 0.8.7, ImmediatelyFast 1.14.2, Iris 1.10.7 sans shaderpack, Accessories 1.4.3-beta, owo 0.13.0, EasyNPC 7.12.1, PAL 1.1.10, Spell Engine RC17, Spell Power RC9, RPG RC3 et dialogue b10. Les dépendances servent au banc ; elles ne sont pas remplacées par cette livraison.

Les captures vérifient un monde isolé et des personnages de test. Lion-Port, ses PNJ, ses armures personnalisées, Distant Horizons, le shaderpack et le modpack complet ne sont pas reproduits ici. Les gains de FPS pendant tes sorts précis restent à mesurer dans cet environnement. Les protections de rendu sont évaluées par conservation du bytecode, comparaison des géométries, comparaison des menus et vérifications des injections en jeu ; les tests ne présentent pas chaque vue de tous les futurs assets possibles.

Les sources, le banc, les CSV, les empreintes des captures et les patches reconstructibles sont conservés dans la branche PRE33 liée à l'issue #163, empilée sur la PRE32 (#162). Aucune fusion dans main.
