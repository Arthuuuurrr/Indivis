# Audit inter-threads — capitale_core 1.5.6-RC9AW

## Objet

RC9AW est une consolidation explicite des changements `capitale_core` dispersés entre plusieurs threads, branches et PR afin d'éviter qu'une reconstruction complète reparte d'un snapshot incomplet.

## Régressions détectées pendant l'audit

### 1. `indivis:corruption`
Le biome avait déjà été livré dans les versions précédentes, mais sa définition n'était plus présente dans la source récente de `main`.

RC9AW conserve la définition historique restaurée :
`data/indivis/worldgen/biome/corruption.json`.

### 2. Interactions Cube Animals RC9AT — PR #117
La source récente avait reperdu :
- `#capitale:adventure_breakables` ;
- `cubeanimals:eagle_nest` ;
- `cubeanimals:crocodile_egg` ;
- `cubeanimals:komododragon_egg` ;
- la migration ciblée des anciens items dont le `can_break` était exactement `fire + soul_fire`.

RC9AW restaure ces éléments sans écraser les `can_break` personnalisés.

### 3. Raycast feu PERF1 — PR #134
Le numéro RC9AV avait déjà été utilisé le 27/09/2026 par la candidate PERF1, mais cette branche n'avait pas été fusionnée dans `main`.

RC9AW reprend :
- `#capitale:adventure_bare_hand_fire` = `fire` + `soul_fire` ;
- `bare_hand_scan_step` avec 2 tests par pas au lieu de 4 ;
- `bare_hand_hit_step` avec le même regroupement ;
- portée 5 blocs, pas 0,25 et cadence 20 Hz inchangés.

Cette optimisation reste **à confirmer en runtime** comme dans la PR #134.

## Biomes urbains / PR #100 / RC9AU

Le tag `#indivis:city` actuel contient :
- `capitale:capitale` ;
- les implantations historiques ;
- Aubecourt ;
- Havre-Fort ;
- Pointe-Rouge ;
- Haut-Arsenal ;
- Urzak-Tor ;
- l'alias legacy `indivis:sombrefleche`.

Toutes les définitions urbaines conservent leurs catégories de spawn naturel vides. Les quatre villes ajoutées en RC9AU et la palette verte de Sylvharen sont conservées.

## Cas volontairement sans biome urbain

**Aurelune** reste volontairement affectée à `capitale:donjon`. Aucun `indivis:aurelune` n'est attendu dans le core à ce stade.

## Contrat de consolidation

Toute future reconstruction complète de `capitale_core` doit prendre **RC9AW ou une version postérieure** comme base canonique. Elle ne doit plus repartir directement de RC9AO/RC9AT/RC9AU ni d'une branche de travail isolée.

## Validation statique exigée pour l'archive

- tous les JSON du core parsables ;
- toutes les entrées de `#indivis:city` résolvent vers un biome existant ;
- aucun spawn naturel dans les biomes urbains ;
- `indivis:corruption` présent ;
- `#capitale:adventure_breakables` contient les 5 blocs attendus ;
- item modifier Adventure pointe sur ce tag ;
- migration RC9AT présente ;
- tag PERF1 présent et utilisé dans les deux fonctions de raycast ;
- ZIP complet valide.

## Runtime encore requis

- chargement de chunks `indivis:corruption` sans `Unknown registry key` ;
- feu et soul fire à main nue ;
- blocage du raycast par un mur ;
- objets/outils Adventure inchangés ;
- interaction avec les trois blocs Cube Animals ;
- aucune régression sur les villes et Aubecourt.
