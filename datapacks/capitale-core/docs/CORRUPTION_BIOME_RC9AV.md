# Correction biome Corruption — capitale_core 1.5.6-RC9AV

## Problème

Des chunks existants contiennent l'identifiant de biome `indivis:corruption`, mais la version récente du datapack chargée par le serveur ne déclarait plus cette clé dans le registre `minecraft:worldgen/biome`.

Minecraft chargeait donc ces sections avec :

`Unknown registry key ... indivis:corruption -> using default`

## Historique corrigé

Le biome avait bien été créé puis **livré dans un core complet** le 23/09/2026.

Source historique :
- commit `caf4326be5f78ed2c6e7e2f647f152101b099bd9`
- branche `feature/indivis-corruption-biome`
- fichier `data/indivis/worldgen/biome/corruption.json`

Une archive complète contenant `indivis:corruption` avait ensuite été fournie et utilisée. Le problème n'est donc pas « le biome n'a jamais été intégré ».

La régression vient du fait que cette modification livrée n'avait pas été consolidée dans la source principale utilisée par les reconstructions ultérieures du core. Une reconstruction postérieure des biomes urbains a repris une base ne contenant pas `corruption.json`, ce qui a provoqué un rétropédalage fonctionnel dans les versions suivantes. Le commit `a4816d92e4d1ab96fb1c086297331f9694ae5419` (26/09/2026, ajout des biomes urbains) est le premier point clairement identifié dans la lignée récente où le core reconstruit est fourni sans `indivis:corruption`; RC9AU a ensuite hérité de cette omission.

## Correction RC9AV

Restauration exacte de :

`data/indivis/worldgen/biome/corruption.json`

Le contenu est identique à la définition historique :
- aucune précipitation ;
- palette grisée/verdâtre de corruption ;
- musique vanilla neutralisée ;
- aucun spawn directement déclaré dans le JSON ;
- aucun carver ;
- aucune feature.

Les spawns spécifiques de la Corruption restent gérés par les systèmes dédiés du projet et ne sont pas déplacés dans cette définition.

## Compatibilité

La restauration de la clé permet aux chunks enregistrés avec `indivis:corruption` de retrouver une entrée de registre valide au prochain démarrage avec RC9AV chargé.

Aucun biome urbain, aucune règle de ville et aucune autre définition worldgen n'est modifiée par ce correctif.

## Leçon de versionnement

Une fonctionnalité livrée dans un ZIP de test ou une branche dédiée doit être intégrée à la source canonique avant toute reconstruction complète ultérieure. Les prochaines versions du core doivent partir du dernier état fonctionnel consolidé, pas d'un snapshot plus ancien de `main`.
