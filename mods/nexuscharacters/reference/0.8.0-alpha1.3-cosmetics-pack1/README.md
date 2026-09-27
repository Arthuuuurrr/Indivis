# NexusCharacters HC 0.8.0-alpha1.3 — Cosmetics Pack 1

Base binaire utilisée : `NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.2-HEALTH-MODIFIER-COMPAT.jar`

SHA-256 base : `ab6cbe03ce2ce29378c6b43759546790c6d0128fecc650113b94c283098a5ad1`

Build produit : `NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.3-COSMETICS-PACK1.jar`

SHA-256 build : `8c617643ae8a99dff842b8d6b18192b7f019529da326d2e3bb4ba144f515081d`

## Modifications

- 15 tenues supplémentaires : slots 21 à 35.
- 2 coiffures supplémentaires : slots 11 et 12.
- Les deux anciens emplacements de moustache sont conservés pour ne pas étendre le schéma de persistance :
  - slot 2 : nouvelle moustache fournie ;
  - slot 3 : barbe légère fournie.
- Le marqueur d'apparence v69 accepte désormais les coiffures 00..12 et les tenues 01..35.
- La création de personnage boucle sur 12 coiffures et 35 tenues.
- Le champ tenue du marqueur v69 est réécrit depuis la valeur UI directe uniquement pour contourner la limite historique 20 tenues du `ModularSkinSupport`, sans modifier l'encodage legacy.

## Compatibilité

Le schéma legacy `ModularSkinSupport` reste strictement inchangé (20 tenues / 10 coiffures) afin que les anciens index 2000..21999 gardent exactement leur signification.

Les marqueurs v69 déjà existants restent valides. La persistance, les vaults, l'autorité serveur et le correctif de santé d'alpha1.2 ne sont pas modifiés.

Les fichiers `PatchCosmeticsPack1.java`, `CosmeticsPack1Support.java` et `hc_cosmetics_pack1.json` décrivent la transformation exacte. Les PNG source proviennent des assets fournis pour ce passage ; ils sont intégrés au JAR de build et référencés par leur nom d'origine dans le manifest.

## Validation effectuée

- archive JAR : `unzip -t` sans erreur ;
- classes patchées lisibles par `javap` ;
- ancien marqueur v69 10/20 toujours accepté ;
- nouveau marqueur v69 hair=12 / outfit=35 accepté et parsé ;
- aucune entrée supprimée de l'archive de base ;
- diff binaire limité aux ressources cosmétiques, `fabric.mod.json`, deux classes patchées et le helper ajouté.

Validation en lancement Minecraft client + serveur encore requise avant passage en build canonique.
