# Capitale Creatures 1.2.21 — hard despawn global avec protection POI/boss

## Nouvelle exemption : mobs de point d'intérêt

Le module `haute_capitale_spawns 0.1.0+1.21.11.b6` marque chaque mob qu'il contrôle avec le command tag `hcspawn` et avec un `ControlledMarker` persistant.

Le hard despawn 1.2.21 exclut donc immédiatement toute entité portant `hcspawn`.

Conséquence : les 23 spawn nodes / 11 pools actuellement chargés par le serveur restent entièrement sous l'autorité de `haute_capitale_spawns` et ne peuvent pas être supprimés par le nettoyage global à 128 blocs.

## Protection des boss

Sont aussi exclus :

Command tags exacts :
- `boss`
- `miniboss`
- `indivis_boss`
- `hc_boss`
- `indivis_no_hard_despawn`

Préfixes de catalogue reconnus, insensibles à la casse :
- `hcspawn.rank.boss*`
- `hcspawn.rank.miniboss*`
- `indivis_boss*`
- `indivis.boss*`
- `hc_boss*`
- `hc.boss*`
- `boss:*`
- `boss.*`
- `miniboss:*`
- `miniboss.*`

Namespaces dédiés :
- `rpg-minibosses:*`
- `block_factorys_bosses:*`
- `hc_necromancer:*`

Boss vanilla explicitement préservés :
- `minecraft:ender_dragon`
- `minecraft:wither`
- `minecraft:warden`

## Exemptions 1.2.20 conservées

Joueurs, armor stands, EasyNPC, entités nommées, Tameable avec propriétaire, montures Dino non hostiles, autonomous colored horses, villageois/traders/golems/allays/happy ghasts.

`PersistenceRequired` reste volontairement ignoré comme exemption générique.

## Artifact

`capitale_creatures_bundle-1.2.21-fabric-1.21.11-POI-BOSS-SAFE-DESPAWN.jar`

SHA-256 :
`5d09f1ba40acae925e143c90b32aae38a97b164d348c618529715f03b517ec14`

Validation :
- ZIP integrity PASS ;
- ASM BasicVerifier PASS sur Runtime124, DatapackSpawnController et OrcPatrol1212 ;
- 11/11 nested fauna JARs inchangés par rapport à 1.2.20 ;
- seules les entrées existantes `CapitaleCreaturesRuntime124.class` et `fabric.mod.json` changent.
