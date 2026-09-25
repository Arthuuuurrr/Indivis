# MMO Music Zones — configuration Haute Capitale

## Version actuelle validée

Version : **1.2.13-indivis-ost-expansion-70-ville-40-nature**

SHA-256 du JAR complet :
`d703bd3fd4c4e62529a4194650200adedd9916126f401ce9486ea92cca298da7`

### Historique récent

- **1.2.11** : correction du `StackMapTable` de `ZoneMusicPlayer.selectBiomeZone`.
- **1.2.12** : routage du biome de corruption vers la banque DONJON.
- **1.2.13** : ajout de **VILLE 42–70** et **NATURE 37–40** sans modifier les affectations de villes existantes.

## Biomes DONJON / corruption

Les identifiants suivants utilisent **DONJON 01–09** :

- `capitale:donjon`
- `indivis:corruption`
- `capitale:corruption` (alias de compatibilité)
- `indivis:donjon` (alias de compatibilité)

## Playlists de villes actuellement affectées

| Ville / biome | Jour | Nuit |
| --- | --- | --- |
| Haute Capitale — `capitale:capitale` | 39 | 38 |
| Ilystara — `indivis:ilystara` | 40 | 40 |
| Lion-Port — `indivis:lion_port` | 36, 35 | 27 |
| Clairval — `indivis:clairval` | 37 | 18 |
| Haute-Rive — `indivis:haute_rive` | 41 | 15 |
| Sylvharen — `indivis:sylvarhen` | 34, 1 | 8 |
| Skarnfjord — `indivis:skarnfjord` | 33, 29 | 30, 24, 23 |
| Dûrak-Vor — `indivis:durak_vor` | 26, 25 | 22, 23 |
| Avaleiv — `indivis:avaleiv` | 5, 9 | 13 |

## Villes / zones en attente d'affectation musicale

- Urzak-Tor
- Blanche-Flèche
- Port-Levant
- Sillons-d’Or
- Havre-Fort
- Le Clos des Ormes
- Château d’Aubecourt
- village portuaire à nommer
- ville de construction de dirigeables à nommer

Les pistes **VILLE 42–70** sont enregistrées dans le mod et disponibles pour ces affectations.

## Bibliothèque audio

- NATURE : **40 pistes** (01–40)
- DONJON : **9 pistes**
- COMBAT : **16 pistes**
- BOSS : **14 pistes**
- VILLE : **70 pistes** (01–70)

## Timers

- Combat : **600 ticks = 30 secondes** après le dernier événement de combat.
- Fade combat : **40 ticks = 2 secondes**.
- Boss : **18 secondes**.

## Priorité audio

`Boss > Combat > MusicZone manuelle > biome`

Une ancienne MusicZone cubique masque donc la playlist définie par biome.
