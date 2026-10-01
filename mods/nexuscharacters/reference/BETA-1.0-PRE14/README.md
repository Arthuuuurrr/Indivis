# NexusCharacters — BETA 1.0 PRE14

Base exacte : **BETA 1.0 PRE13**.

PRE14 est issu d'un audit approfondi de PRE13 contre le JAR client
**3D Skin Layers 1.10.2 / Minecraft 1.21.11**.

## Ce qui a été confirmé

- `MeshHelper.create3DMesh` expose bien la signature utilisée par Nexus.
- Les paramètres HEAD `8×8×8 @ 32,0 / false / 0.6` et BODY
  `8×12×4 @ 16,32 / true / 0.0` sont exactement ceux utilisés par Skin Layers.
- `ModelPartInjector.setInjectedMesh(Mesh, OffsetProvider)` existe.
- Le mixin Skin Layers intercepte `ModelPart.render` à `HEAD`, donc le
  ModelPart vide utilisé par Nexus est un conteneur valide pour le mesh injecté.
- Le hook Nexus s'exécute dans le renderer joueur utilisé en jeu et dans l'aperçu.
- Les 13 meshes HEAD sont non vides ; les coiffures 06..10 ont bien leur mesh BODY.
- La coiffure 6/13 (hair_06) contient 184 pixels de torse : 94 dos, 22+22 côtés,
  18 devant, 28 dessus. Elle ne peut donc pas produire un mesh BODY vide.
- Le décalage BODY PRE13 place le mesh à environ X×1.134 et Z×1.426 avec les
  valeurs par défaut de Skin Layers, contre X×1.05 et Z×1.15 pour la veste.

## Deux défauts découverts pendant l'audit

1. **Couleur 3D trop sombre** : PRE13 appliquait la couleur via une multiplication
   GPU simple. Les textures ont une luminance moyenne ~169–175/255, donc le
   facteur réel était ~0.66–0.69, alors que le compositeur Nexus applique
   ~0.99–1.01. PRE14 génère des textures 3D pré-teintées avec exactement la même
   formule que `DynamicAppearanceSupport`, pour les palettes V69 et legacy.

2. **Fallback trop optimiste** : PRE13 retirait les pixels plats dès que les
   classes Skin Layers existaient. PRE14 ne choisit la variante dépouillée
   qu'après construction réussie du mesh et de son ModelPart injecté.

La géométrie et les offsets PRE13 restent inchangés.
