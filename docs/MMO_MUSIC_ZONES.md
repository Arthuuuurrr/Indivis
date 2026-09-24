# MMO Music Zones — configuration Haute Capitale

## Version cible

Prochaine version : **1.2.10**

Base attendue : **1.2.9-indivis-city-biomes-combat30s**

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
- Boss : **18 secondes** pour l'instant.

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

Exemple :

```mcfunction
/musiczone delete capitale
```

La suppression doit être sauvegardée immédiatement dans `zones.json` et synchronisée aux joueurs ; aucun `/reload` ne doit être nécessaire.
