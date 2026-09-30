# Capitale Creatures 1.2.40 — compatibilité solo sans datapacks Haute Capitale

Base : `capitale_creatures_bundle 1.2.39`.

## Régression corrigée
Les tags de biomes embarqués contenaient encore des références directes obligatoires vers des biomes qui n'existent que lorsque les datapacks Haute Capitale sont chargés.

Cela concernait :
- `capitale:donjon`
- `capitale:capitale`
- `indivis:corruption`
- les biomes de villes `indivis:*`

Sans ces datapacks, Minecraft pouvait refuser le chargement des tags/registries du monde solo.

## Correction
Toutes les références custom des quatre tags concernés utilisent désormais des entrées objet avec `"required": false`.

Tags :
- `orc_dungeon.json`
- `corruption_only.json`
- `culture_corruption.json`
- `city_no_spawn.json`

Les biomes vanilla restent inchangés et obligatoires.

Le contrôleur de spawn garde son fonctionnement actuel :
- il utilise le datapack externe s'il est présent ;
- sinon il peut utiliser les ressources embarquées ;
- l'absence des biomes Haute Capitale n'est plus fatale au chargement des tags.

Aucune classe Java, aucun JAR imbriqué, aucun poids de spawn, aucune IA et aucun réglage deep-ocean n'est modifié par 1.2.40.

## Empreintes
- bundle 1.2.40 SHA-256 : `2c3e5c334bf0b63c070e7a0b5ed05598bd09e31ee80663803054a83eee24005f`
- datapack BETA 0.28 SHA-256 : `cfb01e45c28d8827b5fc0544abf030b13fa9e1c0dbe471afa2541b4cacb318be`

Issue : #129.
