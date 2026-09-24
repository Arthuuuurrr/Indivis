# Audit du travail de Clément — 2026-09-24

Relevé des modules développés et maintenus par Clément, confronté à l'état déployé
(`server-manifest.yml`), au registre `docs/MODULES.md` et aux issues existantes.

Les numéros de build indiqués en colonne « Local » ont été **mesurés sur les artefacts
présents sur le poste de développement**, pas déclarés. Chemin de référence :
`C:\Users\denne\<projet>\` (`DELIVERY/`, `build/libs/`, `testserver/mods/`).

## Méthode

- inventaire des JAR/datapacks construits par projet, triés par date de modification ;
- comparaison avec `server-manifest.yml` (section `deployed`) et `mods/` ;
- recherche systématique d'une issue correspondante avant tout constat de « non suivi » ;
- aucun travail attribué sans artefact daté sur le poste.

## Tableau de correspondance

| Module | Local (mesuré) | Déployé | Dans `mods/` | Issue | État |
|---|---|---|---|---|---|
| haute-capitale-dialogue | b7 | b7 | non | #59, #20, #21, #72 | ⛏️ |
| haute-capitale-quetes (mod) | b3 | serveur b3 / client b2 | non | #33, #72 | ⛏️ |
| hc_quetes (datapack) | **b10** | non suivi | non | **aucune** | ⛏️ |
| haute-capitale-spawns | b6 | non suivi | non | #72 | ⛏️ |
| haute-capitale-party | b9 | non suivi | non | #72 | ⛏️ |
| dungeonz-hc (moteur d'instances) | **b23** | non suivi | non | #32 | ⛏️ |
| capitale-prison-glace | b6 | non suivi | non | #32 | ⛏️ |
| haute-capitale-metiers | b12 | non suivi | non | #72 | ⛏️ |
| haute_capitale_fusils | b3 | b3 | **oui** | #3, #4, #72 | ⛏️ |
| haute-capitale-rpg | **b4** | b2 (TEST3 RC2) | b2 | #53, #72 | ⛏️ |
| hc-necromancer | b3 | non suivi | non | #72 | ⛏️ |
| haute-capitale-pirates | b4 | serveur b4 / client b2 | non | #72 | ⛏️ |
| Arsenal (portage 1.21.11) | 1.5.1.002-mmo | PRIMARY ORDER1 | **oui** | #5, #53, #72 | ⛏️ |

## Divergences relevées

### 1. `haute-capitale-rpg` — deux builds d'avance sur la production

`server-manifest.yml` et `docs/MODULES.md` référencent **b2** (`TEST3 RC2`).
Le poste de développement contient **b3** (module caméra de dialogue, dossier
`DELIVERY-camera/`) et **b4** (ajout de la classe `necromancien`), tous deux construits
et non déployés.

Conséquence : l'écart entre la production et la source de référence est de deux builds,
et non « binaire hashé » comme indiqué actuellement dans le registre.

### 2. `dungeonz-hc b23` absent de la liste d'import de #72

Le moteur de donjons instanciés est suivi **en tant que fonctionnalité** par #32, mais
son artefact ne figure pas dans la checklist d'import de #72, contrairement à tous les
autres modules Haute Capitale. Il s'agit d'un oubli de recensement, pas d'un travail
inexistant.

### 3. Datapack `hc_quetes b10` non recensé

`datapacks/` contient `capitale-abilities`, `capitale-core`, `capitale-creatures-biomes`,
`capitale-skills`, `capskills` et `deployed/`. Le datapack de quêtes `hc_quetes`
(build b10) n'y figure pas et n'apparaît dans aucune issue — alors que le mod
`haute-capitale-quetes` qui le consomme est, lui, bien suivi.

## Préalable bloquant à #72

Le dépôt est **public** (`visibility: PUBLIC`) et ne contient **aucun fichier LICENSE**.

Or la checklist d'import de #72 mélange deux catégories juridiquement distinctes :

- les **modules Haute Capitale** écrits pour le projet — leur import ne pose aucun
  problème ;
- les **forks de mods tiers** (Spell Engine HC, Spell Power HC, Arsenal HC,
  Hazennstuff HC, Haute Capitale RPG, Witcher Class HC) — les mods amont
  correspondants sont sous licence *All Rights Reserved*. Importer leurs sources dans
  un dépôt public constitue une redistribution.

Le même point vaut pour certains modules Haute Capitale construits à partir de mods
tiers ARR (`haute-capitale-pirates`, `hc-necromancer`).

Ce constat ne remet pas en cause l'objectif de #72 ; il demande simplement de trancher
au préalable entre : passer le dépôt en privé, scinder les forks dans un dépôt privé
séparé, ou limiter l'import aux modules écrits pour le projet.

## Suite proposée

1. corriger `docs/MODULES.md` sur les trois divergences ci-dessus ;
2. ajouter `dungeonz-hc b23` et `hc_quetes b10` à la checklist de #72 ;
3. trancher la question de visibilité du dépôt avant tout import de sources de forks ;
4. importer en priorité les modules sans dépendance ARR.
