# Séquence I — candidat SI1 — 20 octobre 2026

Suivi : [issue #172](https://github.com/Arthuuuurrr/Indivis/issues/172).

**MQ01–MQ03 et ouverture de MQ04 intégrées. Contrôles statiques et bancs réussis ; recette Minecraft/Fabric non effectuée.** Aucune installation en production n'est déclarée par cette documentation.

## Périmètre

- MQ01 : La Convocation ; MQ02 : Les Registres ; MQ03 : Le Journal de bord.
- MQ04 : ouverture et premier objectif du port de Havre-Fort seulement.
- Migration technique des 66 quêtes historiques vers la norme v2, soit 70 fiches au total.
- Journal, marqueurs privés, Xaero, profils Nexus et autorisations des Archives.
- Poses individuelles des PNJ sur place, sans coordonnées de carte inventées.

La base est constituée des quatre fichiers fournis par Arthur : core 1.5.7, hc_quetes b21 v16.1, journal b6 et Nexus PRE34. Les sources de génération et les binaires du candidat sont dans l'archive remise dans la conversation. Ils ne sont pas remplacés par les anciennes versions éventuellement présentes sur main.

## Documents

| Document | Contenu |
|---|---|
| [Installation et placement](INSTALLATION_ET_PLACEMENT.md) | Versions, quatre fichiers à installer ensemble, commandes de pose et configuration des zones |
| [Changements](CHANGEMENTS.md) | Migration, restauration des dépendances, classes modifiées et compatibilité |
| [Validation et limites](VALIDATION_ET_LIMITES.md) | Résultats statiques, bancs et limites des doubles d'API |
| [Recette et rollback](RECETTE_ROLLBACK.md) | Essais en jeu, profils A/B, multijoueur, gardes et restauration complète |
| [Artefacts et SHA-256](ARTIFACTS.json) | Archive livrée, empreintes des quatre fichiers et des entrées originales |
| [Registres](registries/) | 70 quêtes, étapes, choix/PNJ, objets et cibles |
| [Traces des bancs](audit/) | Résultats bruts, synthèse statique, vérification du bytecode et changements des JAR |

## Fichiers installables

Installer ensemble les deux datapacks et les deux JAR du lot. Les JAR sont à remplacer côté serveur et côté clients.

- `capitale_core_1.5.7_V2_SEQUENCE_I_SI1.zip`
- `hc_quetes_b22_V2_SEQUENCE_I_SI1.zip`
- `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I.jar`
- `NexusCharacters-Indivis-1.21.11-PRE34-SI1.jar`

L'archive `Indivis_Sequence_I_SI1_2026-10-20.zip` contient aussi les sources, l'installateur, les listes de chemins et les diffs complets dans `audit/core_changes.patch` et `audit/quests_changes.patch`.
Son SHA-256 est `1b15484af3562b50cb6546f493b1c9027540b8d2ea3b75aecd6a734e2a1820df`.
**Cette PR documente le candidat et ses empreintes ; elle ne publie pas ses binaires et ne modifie pas le manifeste de production.**

## État de validation

- Norme v2 : 70 fiches, zéro erreur statique.
- 7 004 contrôles statiques, dont 6 973 comparaisons des prédicats sur les états observés.
- 13 tests des fonctions natives et 4 tests de l'installateur.
- Banc Java : 8 193 assertions avec des points d'API Minecraft/Nexus doublés.
- Bytecode et intégrité des archives contrôlés, sans résolution réelle des API Minecraft.
- **Non effectué :** démarrage Fabric, NBT/PNJ en jeu, sauvegardes réelles, fonctionnement Xaero, multijoueur et mesures de performance.

Les 33 contenus de livres de quêtes et les quatre fichiers optimisés de tick des gardes sont inchangés par rapport aux fichiers fournis. Trois classes originales du journal et deux de Nexus sont modifiées ; les autres classes originales sont identiques.

## Précaution de migration

Le lot modifie des états persistants et 64 identifiants d'objets. Tester sur une copie et sauvegarder le dossier complet du serveur arrêté, monde et profils Nexus compris. Un retour aux anciens ZIP/JAR **ne suffit pas** : il faut restaurer aussi le monde et les profils pris avant migration.

La réussite des bancs ne garantit pas l'absence de régression en jeu. L'issue #172 reste ouverte jusqu'à la recette. Les arbitrages de lore de [#165](https://github.com/Arthuuuurrr/Indivis/issues/165) et la non-régression du prologue [#62](https://github.com/Arthuuuurrr/Indivis/issues/62) restent suivis séparément.
