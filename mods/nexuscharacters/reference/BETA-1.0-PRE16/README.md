# NexusCharacters — BETA 1.0 PRE16

Base exacte : **BETA 1.0 PRE15**.

## Cause du test PRE15

Le log client PRE15 confirme que la création de personnage passe par le rendu
**worldless `PlayerSkinWidget`**. Cette voie appelle :

1. `WorldlessCosmeticPreview.apply(...)`
2. le `PlayerSkinWidget` principal
3. les overlays oreilles/barbe

mais **n'appelle jamais `CharacterCosmeticFeatureRenderer`**. La passe
`LongHair3DRenderSupport.render(...)` de PRE15 était donc correcte pour le
renderer joueur, mais inaccessible dans cet aperçu.

En parallèle, `hairAssetName()` retirait déjà les pixels de cheveux des UV
outer pour éviter le double rendu. Résultat dans le PlayerSkinWidget :
- cheveux du torse supprimés ;
- surcouche de cheveux de tête supprimée ;
- aucune passe hair-only pour les remplacer ;
- les autres couches outer restaient plates car Skin Layers ne configure pas
  automatiquement un PlayerSkinWidget sans entité.

Cela correspond exactement aux captures PRE15.

## Correction PRE16

PRE16 traite séparément les deux problèmes.

### 1. Profondeur de toutes les couches outer dans l'aperçu

`WorldlessSkinLayersPreviewSupport` récupère la vraie texture dynamique Nexus
déjà enregistrée, la passe à **`SkinUtil.getTexture` officiel de
3D Skin Layers 1.10.2**, puis recrée les mêmes meshes que le mod :

- hat : 8×8×8 @ 32,0
- jacket : 8×12×4 @ 16,32
- manches larges : 4×12×4 @ 48,48 / 40,32
- manches slim : 3×12×4
- pantalons : 4×12×4 @ 0,48 / 0,32

Ces meshes sont injectés directement dans les modèles wide et slim du
PlayerSkinWidget avec les `OffsetProvider` officiels.

### 2. Cheveux séparés de la tenue

`WorldlessHairWidgetRenderer` reproduit le mécanisme du renderer de barbe qui
fonctionne déjà dans cet écran : un second PlayerSkinWidget transparent ne
rend que les meshes HEAD/BODY des cheveux.

Les cheveux utilisent toujours la séparation PRE15 :
- extra X = 1.16
- extra Y = 1.02
- extra Z = 1.45

soit, après l'offset BODY de Skin Layers, environ :
- X = 1.218
- Y = 1.0557
- Z = 1.6675

La jacket principale reste à environ Z = 1.15. Les cheveux du torse ne partagent
donc plus le plan de la tenue.

L'overlay hair-only est rendu à toutes les rotations. La barbe conserve son
comportement front-only inchangé.

## Diagnostic

Deux lignes uniques permettent de confirmer le chemin réellement actif :

`[NexusCharacters][SkinLayersPreview] READY ...`

`[NexusCharacters][HairPreview3D] READY h=... hc=... head=true body=...`
