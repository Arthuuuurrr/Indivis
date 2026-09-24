# MMO Music Zones — patch local 1.2.9 → 1.2.10

Ce patch évite de retransférer le JAR audio complet.

## Changement

- `indivis:avaleiv`
  - jour : VILLE 05 + 09
  - nuit : VILLE 13

Le patch conserve la 1.2.9 existante et produit :

`mmomusiczones-1.2.10-indivis-city-biomes-combat30s-avaleiv-FULL.jar`

Il ne remplace pas le JAR source.

## MusicZone manuelle

Pour identifier puis retirer une ancienne zone cubique qui masque le biome :

```mcfunction
/musiczone where
/musiczone list
/musiczone delete NOM_DE_LA_ZONE
```
