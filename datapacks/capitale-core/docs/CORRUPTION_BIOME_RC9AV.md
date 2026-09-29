# Correction biome Corruption — capitale_core 1.5.6-RC9AV

## Problème

Des chunks existants contiennent l'identifiant de biome `indivis:corruption`, mais la lignée actuelle du datapack ne déclarait plus cette clé dans le registre `minecraft:worldgen/biome`.

Minecraft chargeait donc ces sections avec :

`Unknown registry key ... indivis:corruption -> using default`

## Cause

Le biome avait été ajouté le 23/09/2026 dans le commit historique `caf4326be5f78ed2c6e7e2f647f152101b099bd9` sur la branche `feature/indivis-corruption-biome`.

Cette modification n'a pas été reprise lorsque la lignée principale du core a ensuite évolué vers RC9AP puis RC9AU.

## Correction RC9AV

Restauration exacte de :

`data/indivis/worldgen/biome/corruption.json`

Le contenu est identique à la définition historique validée :
- aucune précipitation ;
- palette grisée/verdâtre de corruption ;
- musique vanilla neutralisée ;
- aucun spawn directement déclaré dans le JSON ;
- aucun carver ;
- aucune feature.

Les spawns spécifiques de la Corruption restent gérés par les systèmes dédiés du projet et ne sont pas déplacés dans cette définition.

## Compatibilité

La restauration de la clé permet aux chunks déjà enregistrés avec `indivis:corruption` de retrouver leur biome au prochain démarrage avec RC9AV chargé.

Aucun biome urbain, aucune règle de ville et aucune autre définition worldgen n'est modifiée.
