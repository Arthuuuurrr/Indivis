# Capitale Creatures 1.2.36 — correctif du clic droit des œufs

Base binaire : `capitale_creatures_bundle-1.2.35-fabric-1.21.11-EGG-RIGHTCLICK.jar`.

## Erreur corrigée

La 1.2.35 injectait un descripteur JVM invalide pour :
`Player.method_7270(ItemStack)`.

Descripteur erroné :
`(Lnet/minecraft/class_1799)Z`

Descripteur correct :
`(Lnet/minecraft/class_1799;)Z`

Le `;` final du type objet manquait, ce qui provoquait un `ClassFormatError` au chargement de `KomodoDragonEgg`/Cube Animals avant même le démarrage du serveur.

## Portée

Aucun changement de gameplay supplémentaire par rapport au comportement prévu en 1.2.35.

Les deux classes corrigées sont :
- `net/suprk/ufauna/block/custom/CrocodileEgg.class`
- `net/suprk/ufauna/block/custom/KomodoDragonEgg.class`

Le clic droit reste prévu pour :
- récupérer l'œuf sans Silk Touch ;
- conserver sa `Variant` ;
- ajouter l'item à l'inventaire ou le faire tomber si l'inventaire est plein ;
- retirer le bloc côté serveur.

Les cycles de vie 1.2.34 (œufs naturels) et 1.2.33 (eagle_nest) restent inchangés.

## Validation statique

Comparaison 1.2.35 → 1.2.36 :
- bundle externe : seuls `fabric.mod.json`, le JAR Cube Animals imbriqué et la note 1.2.36 changent ;
- Cube Animals imbriqué : exactement 2 classes changent ;
- `javap` confirme le descripteur correct `(Lnet/minecraft/class_1799;)Z` dans les deux classes ;
- archive JAR valide.

## Empreintes

- bundle 1.2.36 SHA-256 : `794d21d2bfb9517c31bb7c4da2c914e427b16f63d2f7af5221fa884c66c7c01f`
- Cube Animals imbriqué SHA-256 : `169a4b8cfeab9b239787d193d324b028ec97f97a47b656009aef76915a6adb42`

Test serveur requis avant fusion.
