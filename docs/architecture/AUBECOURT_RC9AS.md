# Aubecourt — RC9AS / BETA 0.18 / Creatures 1.2.24

## Identité

Nom : **Aubecourt**

Type : petit fief impérial rural, peu fortifié.

Orientation économique envisagée :
- agriculture locale ;
- élevage ovin ;
- laine potentiellement liée à la filière des dirigeables impériaux.

ID de biome :
`indivis:aubecourt`

## Biome

Le biome reste visuellement proche du vanilla :
- température : 0.75 ;
- downfall : 0.65 ;
- eau : `#3f76e4` ;
- ciel : `#78a7ff` ;
- aucune couleur forcée d'herbe ou de feuillage.

## Sécurité / spawn

Aubecourt est traité exactement comme les autres implantations impériales safe.

Dans le core :
- monster : []
- creature : []
- ambient : []
- axolotls : []
- underground_water_creature : []
- water_creature : []
- water_ambient : []
- misc : []
- spawn_costs : {}
- carvers : []
- features : []

Protections supplémentaires :
- ajout à `#indivis:city` ;
- ajout à `#capitale_creatures:city_no_spawn` ;
- ajout à `exclude_biomes` des patrouilles orques ;
- ajout au `BiomeSelectors.includeByKey(...)` exact du bundle ;
- ajout au garde-fou dur `hardNormalizeBiomeId`.

Le total passe de 14 à **15 biomes urbains / habités protégés**.

## Versions

Core :
`capitale_core_1.5.6-RC9AS_6_NEW_IMPERIAL_SETTLEMENTS_FULL.zip`
SHA-256 : `85708effcfd363a038fcacdbb9df71a06c5a087b27f6f3a9c2a170446329b348`

Creatures datapack :
`capitale_creatures_biomes_BETA_0_18.zip`
SHA-256 : `a5c6c470f6dd1f4ada8ff19661820b8efe2a65ca6a9f3cc68629b0d49cb37cd3`

Creatures bundle :
`capitale_creatures_bundle-1.2.24-fabric-1.21.11-AUBECOURT-15-CITY-SAFE.jar`
SHA-256 : `d997fcdbd5bc62f4ab3919a159626e70994753dd3a38a7d4caa68d6fe5d80e88`

## Héritage 1.2.23 / BETA 0.17

BETA 0.18 et le bundle 1.2.24 sont construits sur les versions 0.17 / 1.2.23.

Sont donc conservés :
- remapping culturel BETA 0.16 ;
- family_weight des variantes ;
- première passe du pool agressif ;
- rééquilibrage Maggot / Baby Spider / Corpse Fly ;
- passage des HMobs agressifs concernés en MONSTER.

Validation du bundle :
- ZIP integrity PASS ;
- Aubecourt présent dans les deux classes de garde-fou ;
- 11/11 JARs de faune imbriqués byte-identical à 1.2.23 ;
- seules les deux classes urbaines, les deux ressources de protection et fabric.mod.json changent parmi les entrées existantes.
