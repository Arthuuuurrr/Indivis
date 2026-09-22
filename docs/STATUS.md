# Statuts et priorités

## Statuts importés depuis Discord

| Symbole | Signification | GitHub |
|---|---|---|
| ✅ | Réglé | archivé dans `docs/RESOLVED.md` ou issue fermée |
| ⛏️ | En travail | issue ouverte avec mention **WIP** |
| 🔴 | Pas traité | issue ouverte avec mention **TODO** |
| ⚪ | Pas de réaction visible | issue ouverte avec mention **TRIAGE** |
| 🧪 | Correction probable, tests manquants | issue ouverte avec mention **VERIFY** |

Le symbole ⛏️ ne donne **aucune information fiable sur le pourcentage d'avancement**.

## Priorités

| Priorité | Définition |
|---|---|
| P0 | crash, perte/corruption de progression, serveur inutilisable |
| P1 | axe actuellement prioritaire, progression/système majeur ou dépendance structurante |
| P2 | bug important, équilibrage ou fonctionnalité notable |
| P3 | polish, confort, cosmétique, dette technique |

## Axes P1 actuels

### Monde

- biomes de référence par culture ;
- biomes de ville safe ;
- musique dédiée par ville/culture ;
- variations de spawns par biome/culture/zone.

### Combat / équipement

- système unifié spells/skills ;
- intégration des armes ;
- intégration des armures/accessoires ;
- reset et persistance ;
- affichage HUD des compétences ;
- validation anti-dédoublement multi-catégories.

## Préfixes d'issues

- `[P0][BUG][TRIAGE]`
- `[P1][BUG][WIP]`
- `[P1][WORLD][WIP]`
- `[P1][INTEGRATION][WIP]`
- `[P1][BUG][VERIFY]`
- `[P2][ROADMAP][TODO]`
- `[P3][POLISH][TODO]`

Les labels GitHub standards (`bug`, `enhancement`) complètent ces préfixes. Les préfixes restent la référence pour la priorité et le statut tant qu'un jeu de labels personnalisés n'est pas disponible via l'intégration.
