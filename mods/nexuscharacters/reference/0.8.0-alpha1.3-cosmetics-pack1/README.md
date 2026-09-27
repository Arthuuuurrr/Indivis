# NexusCharacters HC 0.8.0-alpha1.3 — Cosmetics Pack 1

Base binaire utilisée : `NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.2-HEALTH-MODIFIER-COMPAT.jar`

SHA-256 base : `ab6cbe03ce2ce29378c6b43759546790c6d0128fecc650113b94c283098a5ad1`

Build produit : `NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.3-COSMETICS-PACK1.jar`

SHA-256 build : `512b92cc9796f22487aedd59a7896716f06a4816adcca312cf31ab7453a80da2`

## Modifications

- 15 tenues supplémentaires : slots 21 à 35.
- 2 coiffures supplémentaires : slots 11 et 12.
- 2 styles de pilosité supplémentaires, sans réaffecter les anciens IDs :
  - slot 4 : `Moustache 1` ;
  - slot 5 : `Barbe légère`.
- Les anciens slots 0..3 restent inchangés pour les personnages existants.
- Le marqueur d'apparence v69 accepte désormais les coiffures 00..12, les tenues 01..35 et la pilosité 0..5.
- La création de personnage boucle sur 12 coiffures, 35 tenues et 6 états de pilosité (Aucune incluse).
- Le champ tenue du marqueur v69 est réécrit depuis la valeur UI directe uniquement pour contourner la limite historique 20 tenues du `ModularSkinSupport`, sans modifier l'encodage legacy.

## Compatibilité

Le schéma legacy `ModularSkinSupport` reste strictement inchangé (20 tenues / 10 coiffures) afin que les anciens index 2000..21999 gardent exactement leur signification.

Les marqueurs v69 déjà existants restent valides. La persistance, les vaults, l'autorité serveur et le correctif de santé d'alpha1.2 ne sont pas modifiés.

Les nouvelles coiffures sont stockées techniquement comme `hair_long_06.png` et `hair_long_07.png` parce que le mapper historique traduit tout index supérieur à 5 vers `hair_long_(index-5)`. Les visuels fournis sont conservés tels quels.

## Sources de patch

- `PatchCosmeticsPack1.java` : extension coiffures/tenues + hook du champ tenue v69.
- `PatchFacialHairAdditive.java` : extension additive de la pilosité 4/5.
- `CosmeticsPack1Support.java` : compatibilité tenue + labels/ressources de pilosité.
- `hc_cosmetics_pack1.json` : mapping stable des slots et noms de fichiers.

## Validation effectuée

- archive JAR : `unzip -t` sans erreur ;
- classes patchées lisibles par `javap` ;
- ancien marqueur v69 hair=10 / outfit=20 / facial=2 toujours accepté ;
- nouveau marqueur v69 hair=12 / outfit=35 / facial=5 accepté et parsé ;
- aucune entrée supprimée de l'archive de base ;
- les anciennes textures de moustache restent bit-à-bit celles d'alpha1.2.

Validation en lancement Minecraft client + serveur encore requise avant passage en build canonique.
