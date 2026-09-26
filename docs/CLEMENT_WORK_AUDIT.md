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
| hc_quetes (datapack) | **b10** | b8 recensé dans #72 | non | #33, #72 | ⛏️ |
| haute-capitale-spawns | b6 | non suivi | non | #72 | ⛏️ |
| haute-capitale-party | b9 | non suivi | non | #72 | ⛏️ |
| dungeonz (moteur d'instances) | b23 | non déployé | non | #32, #72 | ⛏️ |
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

### 2. Datapack `hc_quetes` — divergence b8 → b10

#72 recense `hc_quetes-0.1.0.b8.zip` dans les datapacks internes à inventorier. Le poste
de développement contient **b10**. Il s'agit donc d'un écart de deux builds à rattraper à
l'import, pas d'une absence de suivi.

`datapacks/` ne contient pas encore ce datapack (`capitale-abilities`, `capitale-core`,
`capitale-creatures-biomes`, `capitale-skills`, `capskills`, `deployed/`).

### 3. Moteur d'instances — deux noms pour un seul composant

#72 le liste sous `dungeon2-hc b23`, l'artefact local s'appelle
`dungeonz-hc-1.3.0+1.21.11.hc.b23.jar`. Vérification faite sur le `fabric.mod.json` du
JAR : `id = dungeonz`, `name = DungeonZ (portage Haute Capitale)`,
`version = 1.3.0+1.21.11.hc.b23`.

C'est bien **le même composant**, au même build. Nom canonique retenu : **`dungeonz`**,
artefact `dungeonz-hc-1.3.0+1.21.11.hc.b23.jar`. À uniformiser dans #72.

## Provenance technique des modules

Plusieurs modules sont des portages ou forks partis d'une base tierce, puis largement
modifiés, réécrits et adaptés pour Indivis : Spell Engine HC, Spell Power HC, Arsenal HC,
Hazennstuff HC, Haute Capitale RPG, Witcher Class HC, `dungeonz`,
`haute-capitale-pirates`, `hc-necromancer`.

Cette information est conservée à titre **technique** — elle aide à savoir où chercher un
comportement amont lors d'un débogage, et quelle version de base a servi de départ. Le
versionnement de ces sources dans le dépôt est arbitré au niveau projet.

## Suite proposée

1. uniformiser le nom du moteur d'instances sur `dungeonz` dans #72 ;
2. porter `hc_quetes` de b8 à b10 dans la checklist de #72 ;
3. importer les sources par lots, en commençant par les modules déjà construits et
   stables (`dialogue b7`, `spawns b6`, `party b9`, `metiers b12`, `fusils b3`) ;
4. déployer ou archiver `haute-capitale-rpg b4`, la production étant encore en b2.
