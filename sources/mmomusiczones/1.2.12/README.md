# MMO Music Zones 1.2.12

Correction ciblée : le biome de corruption doit utiliser la banque **DONJON 01–09**.

Normalisation appliquée par `BiomeIdHelper` :
- `indivis:corruption` -> `capitale:donjon`
- `capitale:corruption` -> `capitale:donjon` (alias de compatibilité)
- `indivis:donjon` -> `capitale:donjon` (alias de compatibilité)
- `capitale:donjon` reste inchangé.

Le routage déjà présent dans `ZoneMusicPlayer.selectBiomeZone` associe `capitale:donjon` aux neuf pistes DONJON.

JAR complet validé : `MMOMusicZones_1.2.12_FULL.jar`
SHA-256 : `1f56301d51a3391a31c77ad5624c58f02e5216015b247865057b3cc2b596edb1`

Aucun changement sur VILLE, NATURE, COMBAT ou BOSS.
