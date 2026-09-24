# MMO Music Zones — configuration Haute Capitale

## Version actuelle validée

Version : **1.2.11-indivis-city-biomes-combat30s-avaleiv-stackmapfix**

SHA-256 du JAR complet :
`75a8234825b1c5c4ae6631d3d76b89527889cf793c195c72d473afb23e68366a`

### Important

La version **1.2.10** est **défectueuse et ne doit plus être utilisée**.

Cause observée sur le client : `java.lang.VerifyError: Expecting a stackmap frame at branch target 78` dans
`ZoneMusicPlayer.selectBiomeZone`.

La **1.2.11** recalcule correctement le `StackMapTable`. Validation effectuée avec Java 21 via
`-Xverify:all` sur le JAR final réassemblé, en plus du contrôle ZIP et du SHA-256.

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

Exemple :

```mcfunction
/musiczone delete capitale
```

La suppression doit être sauvegardée immédiatement dans `zones.json` et synchronisée aux joueurs ; aucun `/reload` ne doit être nécessaire.
