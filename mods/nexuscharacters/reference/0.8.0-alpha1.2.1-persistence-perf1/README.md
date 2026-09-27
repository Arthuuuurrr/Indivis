# NexusCharacters HC 0.8.0-alpha1.2.1 — Persistence PERF1

Issue : #125

## Base exacte

Artefact runtime profilé par Spark :
`NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.2-HEALTH-MODIFIER-COMPAT.jar`

SHA-256 :
`ab6cbe03ce2ce29378c6b43759546790c6d0128fecc650113b94c283098a5ad1`

Version Fabric :
`1.5.1-capitale-port-v0.8.0-alpha1.2-health-modifier-compat+1.21.11`

## Cause

`VaultManager.serializePlayerNbtTokenized` faisait auparavant :

`player -> NBT -> GZIP bytes -> NBT -> suppression tags temporaires -> GZIP bytes`

Le second passage NBT venait de `PlayerNbtSanitizerV0622.sanitize(byte[])`.

Le profil Spark montre ce chemin dans le coût de checkpoint Nexus sur le thread serveur.

## Correctif

Le patch conserve exactement la construction du NBT joueur et l'autorité de filtrage
`PlayerNbtSanitizerV0622.shouldStripTag`, mais applique ce filtrage directement au
`NbtCompound` avant la première et unique compression.

Préfixes supprimés inchangés :
- `CAP_RUNTIME_`
- `CAP_TMP_`
- `NPC_`
- `ACCESS_`

Le parcours des tags reste effectué de la fin vers le début, comme dans le sanitizer 0.6.22.

## Ce qui n'est volontairement PAS modifié

- fréquence du checkpoint : toujours 100 ticks / 5 secondes ;
- sauvegarde finale à la déconnexion ;
- sauvegarde à l'arrêt serveur ;
- `PersistenceQueueV0710` ;
- format des vaults ;
- autorité serveur ;
- `ProfileStateBridge` ;
- `PuffishSkillsBridge` ;
- apparence / race / santé / skins ;
- mixins et entrypoints.

Ce premier passage ne sacrifie donc pas la fréquence de sauvegarde pour gagner des performances.

## Build candidat

`NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.2.1-PERSISTENCE-PERF1.jar`

SHA-256 :
`1ca904d4a13a08638e6fcea274b1c49bc4927ba7b58570d52d57edb86e4f9e38`

Version Fabric :
`1.5.1-capitale-port-v0.8.0-alpha1.2.1-persistence-perf1+1.21.11`

Diff de l'archive par rapport à alpha1.2 :
- modifié : `net/tompsen/nexuscharacters/VaultManager.class`
- modifié : `fabric.mod.json`
- ajouté : `PATCH_NOTES_0_8_0_ALPHA1_2_1_PERSISTENCE_PERF1.md`

Aucune autre entrée n'est modifiée.

## Validation statique

- archive base : intègre ;
- archive candidate : intègre ;
- aucune signature JAR ;
- classe patchée lisible avec `javap` ;
- `serializePlayerNbtTokenized` contient une seule invocation de compression NBT ;
- la méthode n'appelle plus `serializePlayerNbt` puis `sanitize(byte[])` ;
- elle appelle toujours `PlayerNbtSanitizerV0622.shouldStripTag`.

## Validation runtime requise

Avant validation :
- sélection de personnage ;
- inventaire / XP / santé / position après déco-reco ;
- progression CapSkills après déco-reco ;
- changement de dimension ;
- arrêt serveur normal + redémarrage ;
- crash client / déconnexion brutale + reconnexion ;
- nouveau Spark comparable.

## Alpha1.3 Cosmetics Pack

Le chantier alpha1.3 est volontairement laissé séparé pour faciliter le diagnostic.
Le README alpha1.3 indique que persistance, vaults et autorité serveur ne sont pas modifiés ;
ce patch pourra donc être rebasé sur alpha1.3 après validation runtime de PERF1.
