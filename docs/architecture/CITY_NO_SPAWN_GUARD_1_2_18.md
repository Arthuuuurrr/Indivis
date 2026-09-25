# Capitale Creatures 1.2.18 — City no-spawn guard

## Cause vérifiée

Le contrôleur `CapitaleCreaturesDatapackSpawnController` 1.2.5 du bundle applique `clearSpawns()` uniquement à `#capitale_creatures:all_overworld`.

Ce tag contient uniquement les biomes vanilla. Les nouveaux biomes `indivis:*` ne sont donc jamais nettoyés en POST_PROCESSING. Les entrées de spawn enregistrées directement par les mods embarqués ou d'autres mods peuvent ainsi rester actives dans une ville même si le JSON du biome contient des tableaux `spawners` vides.

Cela explique le symptôme observé : orcs en groupes + autres mobs/animaux dans les biomes urbains custom.

## Correctif

1. Ajout de `#capitale_creatures:city_no_spawn` :
   - `capitale:capitale`
   - `indivis:lion_port`
   - `indivis:clairval`
   - `indivis:haute_rive`
   - `indivis:ilystara`
   - `indivis:sylvarhen`
   - `indivis:avaleiv`
   - `indivis:skarnfjord`
   - `indivis:durak_vor`

2. Le sélecteur utilisé pour le `clearSpawns()` devient :
   `#capitale_creatures:all_overworld OR #capitale_creatures:city_no_spawn`.

3. Les règles de réinjection de faune restent inchangées et ciblent toujours uniquement leurs tags vanilla existants. Les villes ne sont donc pas rajoutées à `all_overworld` et ne reçoivent pas la faune après le clear.

4. La protection runtime des patrouilles orques de 1.2.17 est conservée séparément.

## Exclusions volontaires

`capitale:donjon` n'est pas dans `city_no_spawn` car il doit conserver sa population d'orcs de donjon.

`indivis:corruption` n'est pas dans `city_no_spawn` car ce n'est pas un biome urbain.

## Livrables

- Bundle : `capitale_creatures_bundle-1.2.18-fabric-1.21.11-CITY-NO-SPAWN-GUARD.jar`
- SHA-256 : `a9b7b7508fbb314aa262b96daf48c85a59e11640bda662ed6deb678061e152ef`
- Datapack : `capitale_creatures_biomes_BETA_0_14.zip`
- SHA-256 : `4fbe2b971003bf1c125975d6d296a9a8fd159d27ef864908707fb26e8e2ba72c`

Le bundle 1.2.18 embarque aussi le tag `city_no_spawn`, afin que la protection ne dépende pas uniquement de la version externe du datapack.
