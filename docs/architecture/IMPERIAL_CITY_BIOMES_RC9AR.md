# Cinq nouveaux biomes impériaux — RC9AR / BETA 0.15 / Creatures 1.2.22

## Biomes ajoutés

- Blanche-Flèche : `indivis:blanche_fleche`
- Port-Levant : `indivis:port_levant`
- Sillons-d’Or : `indivis:sillons_d_or`
- Sombreflèche : `indivis:sombrefleche`
- Le Clos des Ormes : `indivis:clos_des_ormes`

Tous sont des villes/villages impériaux.

## Politique de spawn

Chaque biome du core déclare explicitement vides :
- monster
- creature
- ambient
- axolotls
- underground_water_creature
- water_creature
- water_ambient
- misc

Et aussi :
- `spawn_costs: {}`
- `carvers: []`
- `features: []`

Les cinq IDs sont ajoutés à `#indivis:city` et `#capitale_creatures:city_no_spawn`.

Le bundle 1.2.22 les ajoute en plus au sélecteur runtime exact `BiomeSelectors.includeByKey(...)` utilisé par le `clearSpawns()` en POST_PROCESSING. Cette protection ne dépend donc pas seulement des JSON de biome ou du tag datapack.

Les cinq IDs sont également exclus des patrouilles orques :
- dans BETA 0.15 ;
- dans les règles embarquées du bundle ;
- dans le garde-fou dur `hardNormalizeBiomeId`.

## Couleurs

Aucune couleur d’herbe ou de feuillage n’est forcée : les cinq biomes restent proches des palettes vanilla et évitent les coutures visuelles fortes.

Les variations portent surtout sur température, humidité, eau et ciel :
- Blanche-Flèche : tempérée/fraîche ;
- Port-Levant : littorale tempérée ;
- Sillons-d’Or : chaude et agricole ;
- Sombreflèche : littorale tempérée/humide, eau légèrement plus sombre ;
- Clos des Ormes : forestière humide.

## Livrables

Core :
`capitale_core_1.5.6-RC9AR_5_NEW_IMPERIAL_CITIES_FULL.zip`
SHA-256 : `8cc7425e07e9d898f43260a2f65e88b45f69bc38a19a34e6a8740485e173b5eb`

Datapack créatures :
`capitale_creatures_biomes_BETA_0_15.zip`
SHA-256 : `7a2bc0dfefd82be0e825d4a4f2fa9c92de48b65049b42f14f37f442f4812a61a`

Bundle :
`capitale_creatures_bundle-1.2.22-fabric-1.21.11-14-CITY-SAFE.jar`
SHA-256 : `56b87dffc945dd049a8f578f5be155cefdbf0ae6a51c65e28052b2c978e5c987`

## Validation

- core ZIP : intégrité PASS ;
- cinq JSON : 8/8 catégories de spawn vides ;
- cinq IDs : présents dans le tag city ;
- BETA 0.15 : cinq IDs présents dans city_no_spawn et exclude_biomes ;
- bundle 1.2.22 : ZIP/JAR PASS ;
- ASM BasicVerifier : PASS sur DatapackSpawnController et OrcPatrol1212 ;
- 11/11 JARs de faune embarqués : byte-identical à 1.2.21.
