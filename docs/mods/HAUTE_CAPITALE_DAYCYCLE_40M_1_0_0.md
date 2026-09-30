# Haute Capitale Day Cycle 1.0.0

- Minecraft: 1.21.11
- Fabric Loader: >= 0.19.5
- Serveur uniquement
- JAR: `mods/HauteCapitale-DayCycle-1.21.11-1.0.0.jar`
- Taille: **2799 octets**
- SHA-256: `67a1feb3ee32c1d4d83714189989da1b1f447353be008c7454903ddbcaf558d2`

## Comportement validé

Le Mixin encadre uniquement `ServerWorld.tickTime()` :
- à l'entrée, il mémorise `timeOfDay` ;
- vanilla exécute ensuite intégralement `tickTime()` ;
- à la sortie, si et seulement si vanilla a effectué l'incrément automatique exact `+1`, un incrément sur deux est neutralisé.

Il ne remplace donc pas `tickTime()` et ne bloque aucun autre travail effectué par cette méthode.

## Mesure runtime réelle

Test serveur Fabric 1.21.11 / Loader 0.19.5 sur 10 secondes :
- `gameTime` : **+200**
- `timeOfDay` : **+100**
- ratio : **0,500x**

Un passage précédent a également mesuré **101 / 200 = 0,505x** selon l'alignement exact des requêtes RCON.

## Invariants vérifiés

- cycle automatique complet à 20 TPS : **~40 minutes**
- `gameTime` : **1,0x**
- gamerule 1.21.11 `minecraft:advance_time` : inchangée à `true`
- `minecraft:random_tick_speed` : inchangée à `3`
- aucune erreur Mixin au démarrage
- arrêt serveur propre
- `/time` et les sauts de temps hors incrément automatique ne sont pas interceptés

Les ticks serveur, scheduled ticks, météo, random ticks, redstone, IA, cooldowns et effets ne sont pas ralentis par ce mod.

Source : `sources/capitale-daycycle/`.
