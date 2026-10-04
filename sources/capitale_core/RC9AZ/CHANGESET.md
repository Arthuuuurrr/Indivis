# Capitale Core 1.5.6 RC9AZ — fallback premier spawn Nexus

Base stricte : RC9AY.

## Observation runtime

Le log du 01/10 confirme qu'un nouveau slot Nexus arrive avec `playerNbt empty=true` mais rejoint le monde au worldspawn.
Aucun appel automatique à `capitale:spawn/start_prologue_self` n'est observé avant la téléportation manuelle.

## Correction

Ajout dans `capitale:core/tick` :

```mcfunction
execute as @a[tag=!capitale_init] unless score @s CAP_VERSION matches 1.. run function capitale:spawn/first_join_to_prologue_start_self
```

Le garde-fou `CAP_VERSION` protège les anciens profils. `player/init` ajoute `capitale_init` et met `CAP_VERSION=100`, donc le fallback est one-shot pour un nouveau personnage Nexus.

Aucun autre comportement de RC9AY n'est modifié.

SHA-256 du ZIP livré : `e6677b8eb6c7768ab0abe5225a4b1e2c6a75f43eaba945285bf092d949c89be8`.
