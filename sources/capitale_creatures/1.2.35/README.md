# Capitale Creatures 1.2.35 — récolte des œufs au clic droit

Base binaire : `capitale_creatures_bundle-1.2.34-fabric-1.21.11-EGG-LIFECYCLE.jar`.

## Comportement
Les blocs suivants peuvent désormais être récoltés directement au clic droit :
- `cubeanimals:crocodile_egg`
- `cubeanimals:komododragon_egg`

Le clic droit :
1. crée l'ItemStack de l'œuf ;
2. recopie la propriété `Variant` dans le composant custom data, exactement comme le chemin Silk Touch amont ;
3. tente de placer l'œuf dans l'inventaire du joueur ;
4. si l'inventaire est plein, dépose l'œuf au sol ;
5. remplace le bloc par de l'air côté serveur ;
6. renvoie un résultat d'interaction réussi.

Silk Touch reste compatible mais n'est plus nécessaire pour la méthode normale de récolte.

## Compatibilité
- conserve le nettoyage des œufs naturels/legacy de 1.2.34 ;
- conserve `placed_by_player` et l'éclosion des œufs reposés ;
- conserve le cycle de vie `eagle_nest` de 1.2.33 ;
- aucune modification des entités, pools de spawn ou IA dans 1.2.35.

## Portée binaire
Dans Cube Animals, seules deux classes changent :
- `net/suprk/ufauna/block/custom/CrocodileEgg.class`
- `net/suprk/ufauna/block/custom/KomodoDragonEgg.class`

Dans le bundle externe, seules trois entrées changent :
- `fabric.mod.json`
- `META-INF/jars/cubeanimals.jar`
- `META-INF/CAPITALE_CREATURES_1.2.35_EGG_RIGHTCLICK.txt`

## Empreintes
- bundle 1.2.35 SHA-256 : `1cb083b716368ef0bc5714f4ba312936a612901726be781741e3d3a7888c8739`
- Cube Animals imbriqué SHA-256 : `0687924bca417926663b72cbe05a60b8e06702578d39281f34613835434e0351`

Validation statique effectuée ; test serveur requis avant fusion.
