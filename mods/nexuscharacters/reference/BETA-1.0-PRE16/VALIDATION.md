# Validation BETA 1.0 PRE16

- JAR : `NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE16.jar`
- Taille : **21642261 octets**
- SHA-256 : `59e85a002bc03ed5c251f8531e0667474fd5f92894ebd3f27e14529666ecb44a`
- Base PRE15 SHA vérifiée.
- Cause PRE15 confirmée : l'aperçu worldless PlayerSkinWidget ne passe pas par CharacterCosmeticFeatureRenderer.
- Main PlayerSkinWidget : 6 couches outer injectées avec les meshes/OffsetProvider officiels de Skin Layers, wide + slim.
- Hair overlay : widget séparé, HEAD+BODY dédiés, y compris en rotation arrière.
- Harness h08 : BODY présent et offset extra 1.16 / 1.02 / 1.45 appliqué.
- Les 247 textures 3D teintées et tous les assets hair PRE15 sont byte-identiques.
- Barbe/oreilles, CharacterPreviewRenderer, DynamicAppearanceSupport, sauvegarde et ABI restent inchangés.
- Version : `1.0.0-beta.16+hc.1.21.11`.
