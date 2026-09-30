# Haute Capitale — Cycle 40 min

Mod serveur Fabric 1.21.11.

## Contrat

Le mod double uniquement la durée du cycle jour/nuit vanilla :
- `timeOfDay` automatique : **0,5x** ;
- journée complète : **~40 minutes** à 20 TPS.

Il ne modifie pas :
- le tickrate serveur ;
- `gameTime` ;
- `doDaylightCycle` ;
- `randomTickSpeed` ;
- météo ou timers météo ;
- ticks de blocs/fluides ;
- redstone ;
- IA / vitesse des mobs ;
- cooldowns ;
- effets ;
- scheduled ticks.

Les mécaniques qui consultent volontairement l'heure du monde suivent naturellement
la nouvelle heure (soleil/lune, niveau de lumière céleste, horaires villageois,
détection jour/nuit, musiques jour/nuit, etc.).

Les commandes `/time` et les sauts dus au sommeil restent immédiats : seule
l'incrémentation automatique de `ServerWorld.tickTime()` est divisée par deux.

## Compatibilité calendrier

À utiliser avec `Typewriter-Calendar-HauteCapitale-1.21.11-HC40M-1.1.0.jar`.
Le calendrier lit `time query day` et suit donc naturellement cette cadence.
