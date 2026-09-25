# Capitale Creatures 1.2.34 — Cube Animals egg lifecycle

Base binaire : `capitale_creatures_bundle-1.2.33-fabric-1.21.11-CUBEANIMALS-NEST-LIFECYCLE.jar`.

## Diagnostic

Dans Cube Animals :
- `cubeanimals:crocodile_egg` et `cubeanimals:komododragon_egg` ne donnent leur bloc qu'avec **Silk Touch** ;
- les œufs placés par un joueur programment déjà un tick à 6000 ticks et éclosent ;
- les œufs pondus naturellement étaient posés sans aucun tick programmé : ils pouvaient donc rester indéfiniment dans le monde.

## Correctif 1.2.34

Les deux blocs d'œuf reçoivent la propriété `placed_by_player`.

- œuf naturel : `placed_by_player=false` ;
- après la ponte naturelle : tick programmé à **6000 ticks chargés** (~5 minutes) ;
- au tick : si `placed_by_player=false`, le bloc est supprimé sans éclosion ;
- œuf récupéré avec Silk Touch puis reposé : `placed_by_player=true` ;
- au tick normal à 6000 : l'œuf joueur garde le comportement Cube Animals et éclot.

Ce choix empêche l'accumulation d'œufs naturels sans transformer toutes les pontes sauvages en nouveaux mobs.

## Portée du patch

Classes Cube Animals modifiées :
- `net/suprk/ufauna/block/custom/CrocodileEgg.class`
- `net/suprk/ufauna/block/custom/KomodoDragonEgg.class`
- `net/suprk/ufauna/entity/custom/CrocodileEntity.class`
- `net/suprk/ufauna/entity/custom/KomodoDragonEntity.class`

Le correctif `EagleNest` de 1.2.33 reste inchangé.

## Artefacts

- bundle 1.2.34 SHA-256 : `162e042fe1d979531ee3300f887b75d24cd5b5e27924e22178f05373a8aeecd0`
- Cube Animals imbriqué patché SHA-256 : `09e821c7f25c1bba5ddea533526e11c4b72ff84366102ec1c9a2e1fff2f69cad`

## Validation statique

Comparaison bundle 1.2.33 → 1.2.34 :
- seuls `fabric.mod.json`, `META-INF/jars/cubeanimals.jar` et la note META-INF 1.2.34 changent ;
- dans le JAR Cube Animals imbriqué, exactement 4 classes changent ;
- le bytecode désassemblé confirme les marqueurs joueur, les ticks naturels à 6000 et la suppression des œufs naturels.

Test serveur requis avant fusion.
