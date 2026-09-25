# Capitale Creatures 1.2.34 — Cube Animals egg lifecycle

Base binaire : `capitale_creatures_bundle-1.2.33-fabric-1.21.11-CUBEANIMALS-NEST-LIFECYCLE.jar`.

## Diagnostic
- `cubeanimals:crocodile_egg` et `cubeanimals:komododragon_egg` ne donnent leur bloc qu'avec **Silk Touch** ;
- les œufs posés par un joueur programment déjà un tick à 6000 ticks et éclosent ;
- les œufs pondus naturellement étaient posés sans tick programmé et pouvaient rester indéfiniment.

## Correctif 1.2.34
- les deux œufs possèdent désormais `placed_by_player` ;
- ponte naturelle : `false`, nettoyage programmé après **6000 ticks chargés** (~5 min), sans éclosion ;
- pose depuis l'inventaire : `true`, comportement original conservé avec éclosion à 6000 ticks ;
- les deux blocs utilisent aussi les random ticks comme **migration des œufs legacy** : un ancien œuf rencontré dans une zone active programme son nettoyage 6000 ticks plus tard ;
- les anciens blockstates ne permettaient pas de savoir s'ils avaient été posés par un joueur : les œufs déjà présents avant 1.2.34 sont donc traités comme naturels lors de la migration.

Ce choix empêche à la fois l'accumulation des blocs et la multiplication automatique de bébés sauvages.

## Portée du patch
Classes Cube Animals modifiées :
- `net/suprk/ufauna/block/ModBlocks.class`
- `net/suprk/ufauna/block/custom/CrocodileEgg.class`
- `net/suprk/ufauna/block/custom/KomodoDragonEgg.class`
- `net/suprk/ufauna/entity/custom/CrocodileEntity.class`
- `net/suprk/ufauna/entity/custom/KomodoDragonEntity.class`

Le correctif `EagleNest` de 1.2.33 reste inchangé.

## Artefacts
- bundle 1.2.34 SHA-256 : `789ff3a190f371ff60117dd9ff277d591a481b4f70ac9c9e9c0069086f28d7cf`
- Cube Animals imbriqué patché SHA-256 : `c2b94da043981cb877dcf83c463558645a222c9647e28ad9414a134ba3ea8093`

## Validation statique
Comparaison bundle 1.2.33 → 1.2.34 :
- seules trois entrées externes changent : `fabric.mod.json`, `META-INF/jars/cubeanimals.jar` et la note META-INF 1.2.34 ;
- dans Cube Animals, exactement 5 classes changent ;
- bytecode vérifié : propriété joueur, tick naturel 6000, random-tick de migration et suppression sans hatch ;
- archive JAR vérifiée sans erreur.

Test serveur requis avant fusion.
