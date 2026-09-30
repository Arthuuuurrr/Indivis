# Haute Capitale Day Cycle 1.0.0

- Minecraft: 1.21.11
- Fabric Loader: >= 0.19.5
- Serveur uniquement
- JAR: `mods/HauteCapitale-DayCycle-1.21.11-1.0.0.jar`
- Taille: **2799 octets**
- SHA-256: `67a1feb3ee32c1d4d83714189989da1b1f447353be008c7454903ddbcaf558d2`

## Comportement validé

Le Mixin intervient uniquement sur l'argument de `ServerWorld.tickTime() -> ServerWorldProperties.setTimeOfDay(long)`.

- `timeOfDay` automatique: **0,5x**
- `gameTime`: **1,0x**
- cycle complet à 20 TPS: **~40 minutes**
- `doDaylightCycle`: inchangé
- `randomTickSpeed`: inchangé
- `/time` et les sauts de temps hors incrément automatique: non interceptés

Le test runtime GitHub Actions mesure simultanément `time query daytime` et `time query gametime` sur 10 secondes et exige un ratio compris entre 0,45 et 0,55.

Source: `sources/capitale-daycycle/`.
