# Référence des biomes culturels — Haute Capitale

**Version :** 0.1  
**Date :** 24 septembre 2026  
**Statut :** document de référence en cours de validation  
**Portée :** datapack `capitale_core`, MMOMusicZones, terraforming, biomes urbains custom et règles de spawn des patrouilles orques.

## 1. Principes de conception

Le choix des biomes sert d'abord à fixer une identité environnementale stable pour chaque continent et à fournir des zones fiables au système musical.

Principes retenus :

- les zones naturelles utilisent autant que possible des biomes vanilla ;
- les villes utilisent des biomes custom `capitale:*` dédiés ;
- le terrain, la végétation et les décors sont terraformés : le nom ou la génération naturelle du biome ne contraignent donc pas l'apparence finale ;
- les biomes naturels sont peu nombreux afin d'éviter des changements de musique trop fréquents ;
- chaque biome retenu doit apporter une différence visuelle ou climatique suffisamment nette ;
- les marais, mangroves, déserts et badlands ne sont pas nécessairement rattachés à une musique continentale spécifique ;
- les biomes custom urbains permettront de régler séparément les couleurs d'herbe, de feuillage, d'eau et les autres paramètres environnementaux.

## 2. Continent impérial

Climat général : tempéré, avec une jungle chaude sur la bordure du territoire orc et des massifs montagneux froids en altitude.

| Fonction | Biome |
|---|---|
| Plaines tempérées | `minecraft:plains` |
| Forêts tempérées feuillues | `minecraft:forest` |
| Forêts tempérées de conifères | `minecraft:taiga` |
| Jungle de frontière méridionale / orque | `minecraft:jungle` |
| Montagnes non enneigées | `minecraft:windswept_gravelly_hills` |
| Montagnes enneigées | `minecraft:snowy_slopes` |
| Grande zone corrompue | `capitale:donjon` |

### Notes

- `minecraft:stony_peaks` n'est plus utilisé comme biome montagneux impérial : il est réservé au territoire nain.
- `minecraft:taiga` apporte une vraie variation forestière par rapport à `minecraft:forest`, contrairement aux variantes de bouleaux jugées trop proches visuellement.
- `capitale:donjon` reste une zone environnementale à part et ne doit pas être traitée comme une simple variante musicale impériale.

## 3. Continent elfique

Les Hauts-Elfes et les Elfes des Bois partagent une esthétique naturelle continentale commune. Leur différenciation principale se fera par l'architecture, le terraforming local, les villes et leurs biomes custom.

| Fonction | Biome |
|---|---|
| Grandes zones ouvertes | `minecraft:sunflower_plains` |
| Zones forestières terraformées | `minecraft:cherry_grove` |
| Reliefs, plateaux et montagnes douces | `minecraft:meadow` |

### Notes

- Le biome `minecraft:cherry_grove` peut recevoir n'importe quelle végétation après terraforming ; il n'impose pas l'usage de cerisiers.
- `minecraft:meadow` sert de biome de relief sans multiplier les variantes montagneuses.
- Aucun découpage naturel strict n'est prévu entre territoires haut-elfes et elfes des bois.

## 4. Territoires nordiques

Le territoire nordique est presque exclusivement froid et enneigé. Les biomes choisis privilégient l'identité neige/glace plutôt que l'identité haute montagne, afin de conserver une distinction nette avec les Nains.

| Fonction | Biome |
|---|---|
| Grandes étendues enneigées | `minecraft:snowy_plains` |
| Forêts de conifères enneigées | `minecraft:snowy_taiga` |
| Régions les plus glaciales / extrêmes | `minecraft:ice_spikes` |

### Notes

- Les reliefs nordiques peuvent être fortement terraformés sans utiliser de biome `*_peaks`.
- Les biomes montagneux rocheux ou enneigés sont prioritairement réservés aux Nains.
- `minecraft:grove` n'est pas attribué aux Nordiques afin de garder une signature distincte pour les zones froides naines.

## 5. Territoires nains

Le territoire nain combine plaines et vallées tempérées, zones froides enneigées et massifs montagneux importants.

| Fonction | Biome |
|---|---|
| Plaines, vallées et contreforts tempérés | `minecraft:windswept_hills` |
| Plaines et vallées froides / enneigées | `minecraft:grove` |
| Montagnes rocheuses non enneigées | `minecraft:stony_peaks` |
| Hautes montagnes enneigées | `minecraft:jagged_peaks` |

### Notes

- `minecraft:stony_peaks` devient une signature claire des massifs nains.
- `minecraft:jagged_peaks` distingue les hautes montagnes naines des montagnes impériales enneigées en `minecraft:snowy_slopes`.
- `minecraft:grove` permet de représenter des zones naines enneigées sans reprendre `minecraft:snowy_plains`, réservé aux Nordiques.

## 6. Territoires orcs

**Statut : à définir.**

Les déserts et familles de badlands/mesa sont généralement considérés comme des territoires orcs, mais leur catalogue définitif n'est pas encore verrouillé.

La jungle impériale située au contact de la frontière orque reste pour l'instant `minecraft:jungle`.

## 7. Marais, mangroves, déserts et badlands

Ces biomes ne sont pas systématiquement rattachés aux banques musicales d'un continent.

- `minecraft:swamp` : ambiance environnementale commune ;
- `minecraft:mangrove_swamp` : ambiance environnementale commune ;
- familles `desert` et `badlands` : généralement associées aux territoires orcs, à préciser lors de la validation de leur palette.

## 8. Biomes urbains custom

Les villes doivent utiliser des biomes spécifiques `capitale:*` afin de contrôler précisément :

- couleur de l'herbe ;
- couleur du feuillage ;
- couleur de l'eau ;
- paramètres de ciel/brouillard si nécessaire ;
- température et précipitations ;
- musique urbaine ;
- règles de spawn.

Biomes urbains prévus :

- `capitale:haute_capitale`
- `capitale:ilystara`
- `capitale:sylvarhen`
- `capitale:avaleiv`
- `capitale:skarnfjord`
- `capitale:durak_vor`
- `capitale:urzak_tor`

Ces biomes sont distincts des biomes naturels listés plus haut.

## 9. Patrouilles orques

Règle retenue :

> Les patrouilles orques peuvent apparaître dans tous les biomes vanilla terrestres de l'Overworld, sauf dans les rivières et les océans.

### Biomes explicitement exclus

- `minecraft:river`
- `minecraft:frozen_river`
- `minecraft:ocean`
- `minecraft:cold_ocean`
- `minecraft:lukewarm_ocean`
- `minecraft:warm_ocean`
- `minecraft:frozen_ocean`
- `minecraft:deep_ocean`
- `minecraft:deep_cold_ocean`
- `minecraft:deep_lukewarm_ocean`
- `minecraft:deep_frozen_ocean`

Les plages restent autorisées.

### Implémentation recommandée

Le système doit reposer sur une whitelist de biomes autorisés ou, de manière équivalente, sur un contrôle explicite excluant les familles rivière/océan.

Les biomes custom `capitale:*` ne doivent pas être considérés comme autorisés automatiquement. Ils pourront être ajoutés explicitement si un besoin de gameplay le justifie.

Le contrôle doit idéalement être effectué :

1. au moment de déterminer qu'une patrouille peut être générée ;
2. à la position exacte retenue pour chaque spawn, afin d'éviter une apparition dans une zone interdite située juste à côté d'un joueur.

## 10. Synthèse actuelle

| Région | Biomes naturels validés |
|---|---|
| Empire | `plains`, `forest`, `taiga`, `jungle`, `windswept_gravelly_hills`, `snowy_slopes`, `capitale:donjon` |
| Elfes | `sunflower_plains`, `cherry_grove`, `meadow` |
| Nordiques | `snowy_plains`, `snowy_taiga`, `ice_spikes` |
| Nains | `windswept_hills`, `grove`, `stony_peaks`, `jagged_peaks` |
| Orcs | à définir |

## 11. Points restant à verrouiller

- palette naturelle définitive des Orcs ;
- paramètres environnementaux exacts des biomes urbains custom ;
- regroupement final des biomes dans MMOMusicZones ;
- implémentation effective des tags/conditions de spawn des patrouilles dans `capitale_core`.
