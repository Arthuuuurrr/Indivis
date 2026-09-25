# MMO Music Zones — configuration Haute Capitale

## Version actuelle validée

Version : **1.2.12-indivis-city-biomes-corruption-dungeon**

SHA-256 du JAR complet :
`1f56301d51a3391a31c77ad5624c58f02e5216015b247865057b3cc2b596edb1`

### Important

La version **1.2.10** est défectueuse et ne doit plus être utilisée.

La **1.2.11** corrigeait le `StackMapTable` de `ZoneMusicPlayer.selectBiomeZone`.
La **1.2.12** conserve ce correctif et ajoute le routage du biome de corruption vers la banque DONJON.

## Biomes DONJON / corruption

Les identifiants suivants utilisent **DONJON 01–09** :

- `capitale:donjon`
- `indivis:corruption`
- `capitale:corruption` (alias de compatibilité)
- `indivis:donjon` (alias de compatibilité)

Le biome de corruption ne doit donc pas retomber sur une playlist NATURE ou continentale.

## Playlists de villes par biome

| Ville / biome | Jour | Nuit |
| --- | --- | --- |
| Capitale — `capitale:capitale` | 39 | 38 |
| Ilystara — `indivis:ilystara` | 40 | 40 |
| Lion-Port — `indivis:lion_port` | 36, 35 | 27 |
| Clairval — `indivis:clairval` | 37 | 18 |
| Haute-Rive — `indivis:haute_rive` | 41 | 15 |
| Sylvharen — `indivis:sylvarhen` | 34, 1 | 8 |
| Skarnfjord — `indivis:skarnfjord` | 33, 29 | 30, 24, 23 |
| Durak-Vor — `indivis:durak_vor` | 26, 25 | 22, 23 |
| Avaleiv — `indivis:avaleiv` | **5, 9** | **13** |

## Timers

- Combat : **600 ticks = 30 secondes** après le dernier événement de combat.
- Fade combat : **40 ticks = 2 secondes**.
- Boss : **18 secondes**.

## Bibliothèque audio

- NATURE : **36 pistes**
- DONJON : **9 pistes**
- COMBAT : **16 pistes**
- BOSS : **14 pistes**
- VILLE : **41 pistes**

## Priorité audio

`Boss > Combat > MusicZone manuelle > biome`

Une ancienne MusicZone cubique masque donc la playlist définie par biome.

## Identifier et supprimer une MusicZone

À l'endroit concerné :

```mcfunction
/musiczone where
```

Pour afficher toutes les zones existantes :

```mcfunction
/musiczone list
```

Puis supprimer la zone concernée avec son nom exact :

```mcfunction
/musiczone delete NOM_DE_LA_ZONE
```

La suppression doit être sauvegardée immédiatement dans `zones.json` et synchronisée aux joueurs ; aucun `/reload` ne doit être nécessaire.
