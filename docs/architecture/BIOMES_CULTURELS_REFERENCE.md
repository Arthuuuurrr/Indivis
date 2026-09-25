# Référence des biomes culturels — Haute Capitale

**Version :** 0.1  
**Date :** 24 septembre 2026  
**Statut :** document de référence en cours de validation  
**Portée :** datapack `capitale_core`, MMOMusicZones, terraforming, biomes urbains custom et règles de spawn des patrouilles orques.

## 1. Principes de conception

Le choix des biomes sert d'abord à fixer une identité environnementale stable pour chaque continent et à fournir des zones fiables au système musical.

Principes retenus :

- les zones naturelles utilisent autant que possible des biomes vanilla ;
- les nouveaux biomes custom utilisent le namespace `indivis:*` ; le biome historique de la Haute Capitale reste `capitale:capitale` ;
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
| Donjon / zone corrompue historique | `capitale:donjon` |
| Zone de corruption générique | `indivis:corruption` |

### Notes

- `minecraft:stony_peaks` n'est plus utilisé comme biome montagneux impérial : il est réservé au territoire nain.
- `minecraft:taiga` apporte une vraie variation forestière par rapport à `minecraft:forest`, contrairement aux variantes de bouleaux jugées trop proches visuellement.
- `capitale:donjon` reste une zone environnementale à part et conserve son ID historique.
- `indivis:corruption` reprend exactement les mêmes propriétés environnementales et de spawn que `capitale:donjon` afin de pouvoir identifier séparément les zones de corruption qui ne sont pas des donjons.
- `indivis:corruption` doit également être rattaché à la même zone musicale MMOMusicZones que `capitale:donjon`, afin de conserver les mêmes musiques de donjon.

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

Les nouvelles villes doivent utiliser des biomes spécifiques `indivis:*` afin de contrôler précisément :

- couleur de l'herbe ;
- couleur du feuillage ;
- couleur de l'eau ;
- paramètres de ciel/brouillard si nécessaire ;
- température et précipitations ;
- musique urbaine ;
- règles de spawn.

Biomes urbains :

- `capitale:capitale` — biome existant de la Haute Capitale, à conserver ;
- `indivis:lion_port` — ville portuaire, première ville rencontrée en quittant la Haute Capitale ;
- `indivis:clairval` — petit village impérial ;
- `indivis:haute_rive` — ville portuaire aux abords de la jungle ;
- `indivis:ilystara` — ville haut-elfe ;
- `indivis:sylvarhen` — ville des Elfes des Bois ;
- `indivis:avaleiv` — ville elfique maritime ;
- `indivis:skarnfjord` — ville nordique ;
- `indivis:durak_vor` — ville naine.

`indivis:urzak_tor` n'est pas créé à ce stade : la définition d'un biome urbain orc est jugée prématurée.

Ces biomes sont distincts des biomes naturels listés plus haut.

### Première passe environnementale des villes

Les biomes urbains doivent rester proches des biomes vanilla voisins. Sauf nécessité, les couleurs d'herbe et de feuillage ne sont pas forcées : elles sont dérivées de la température et du downfall. L'eau reçoit une variation légère. `indivis:sylvarhen` reprend explicitement les couleurs de végétation du `cherry_grove` vanilla afin de rester raccord avec cet environnement.

| Biome urbain | Température | Downfall | Eau | Ciel |
|---|---:|---:|---|---|
| `indivis:lion_port` | 0.8 | 0.4 | `#3d79df` | `#78a7ff` |
| `indivis:clairval` | 0.8 | 0.4 | `#3f76e4` | `#78a7ff` |
| `indivis:haute_rive` | 0.9 | 0.8 | `#3d82df` | `#77a8ff` |
| `indivis:ilystara` | 0.8 | 0.4 | `#4a86e4` | `#78a7ff` |
| `indivis:sylvarhen` | 0.5 | 0.8 | `#5db7ef` | `#7ba4ff` |
| `indivis:avaleiv` | 0.65 | 0.6 | `#4891e2` | `#7aa5ff` |
| `indivis:skarnfjord` | -0.5 | 0.4 | `#3d57d6` | `#839eff` |
| `indivis:durak_vor` | 0.2 | 0.3 | `#3d70d4` | `#7da2ff` |

### Règle anti-spawn urbaine

Tous les biomes de ville ont les catégories de spawners suivantes vides : `monster`, `creature`, `ambient`, `axolotls`, `underground_water_creature`, `water_creature`, `water_ambient` et `misc`.

Le tag `#indivis:city` regroupe les villes existantes et nouvelles. Les patrouilles orques doivent en plus exclure explicitement chaque biome urbain, indépendamment des spawners vanilla.

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

Les biomes custom `capitale:*` et `indivis:*` ne doivent pas être considérés comme autorisés automatiquement. Ils pourront être ajoutés explicitement si un besoin de gameplay le justifie.

Le contrôle doit idéalement être effectué :

1. au moment de déterminer qu'une patrouille peut être générée ;
2. à la position exacte retenue pour chaque spawn, afin d'éviter une apparition dans une zone interdite située juste à côté d'un joueur.

## 10. Synthèse actuelle

| Région | Biomes naturels validés |
|---|---|
| Empire | `plains`, `forest`, `taiga`, `jungle`, `windswept_gravelly_hills`, `snowy_slopes`, `capitale:donjon`, `indivis:corruption` |
| Elfes | `sunflower_plains`, `cherry_grove`, `meadow` |
| Nordiques | `snowy_plains`, `snowy_taiga`, `ice_spikes` |
| Nains | `windswept_hills`, `grove`, `stony_peaks`, `jagged_peaks` |
| Orcs | à définir |

## 11. Points restant à verrouiller

- palette naturelle définitive des Orcs ;
- paramètres environnementaux exacts des nouveaux biomes urbains custom ;
- mapping MMOMusicZones de `indivis:corruption` vers les musiques de `capitale:donjon` lorsque la configuration/source du mod sera disponible dans le dépôt ;
- regroupement final des biomes dans MMOMusicZones ;
- implémentation effective des tags/conditions de spawn des patrouilles dans `capitale_core`.
