# NexusCharacters — BETA 1.0 PRE17

Base exacte : **BETA 1.0 PRE16**.

## Pourquoi PRE17 existe

L'audit approfondi demandé après PRE16 a confirmé que la correction de l'écran
de création est branchée sur le bon chemin : le `PlayerSkinWidget` worldless
utilise bien les meshes Skin Layers injectés directement dans ses modèles wide
et slim, et le renderer de cheveux séparé restitue HEAD + BODY.

L'audit a cependant trouvé une erreur distincte dans le renderer joueur normal,
héritée de PRE15/PRE16 :

- `field_3391` = **body**
- `field_3394` = **hat**

`LongHair3DRenderSupport.render(...)` utilisait encore `field_3394` comme
parent du mesh BODY. Les cheveux longs du torse auraient donc suivi la
transformation du chapeau dans le monde normal.

PRE17 remplace uniquement cet ancrage par `field_3391`.

## Vérifications renforcées

Le build PRE17 valide notamment :

- SHA exact de PRE16 avant patch ;
- seules `LongHair3DRenderSupport.class` et `fabric.mod.json` changent ;
- les classes worldless de PRE16 restent byte-identiques ;
- les 13 textures hair 3D sont en 64×64 ;
- seules les coiffures **6, 7, 8, 9, 10** contiennent réellement des pixels BODY ;
- les **247** textures teintées conservent exactement le même masque alpha ;
- les mappings Yarn 1.21.11 confirment `field_3391=body` et `field_3394=hat` ;
- le source 1.21.11 de `PlayerSkinWidget` confirme que le widget soumet
  directement le `PlayerModel` à `submitSkinRenderState`, donc les meshes
  injectés restent utilisés au rendu ;
- le mixin ModelPart de 3D Skin Layers 1.10.2 confirme qu'un mesh injecté
  remplace la géométrie plate de la couche et applique son `OffsetProvider` ;
- harness worldless : profondeur des six couches externes et HEAD/BODY h08 ;
- harness renderer normal : le second submit de h08 est ancré sur BODY, jamais HAT.

PRE17 conserve donc la correction visuelle PRE16 de l'écran de création tout en
corrigeant le dernier mauvais ancrage découvert pendant l'audit.
