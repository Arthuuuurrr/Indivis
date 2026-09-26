# Capitale Creatures 1.2.37 — clean rebuild du clic droit des œufs

Base binaire : **1.2.34**, volontairement choisie avant les patches cassés 1.2.35/1.2.36.

## Correctif

Récolte au clic droit :
- `cubeanimals:crocodile_egg` : donne l'œuf et conserve `Variant` via le même custom_data que le loot Silk Touch original ;
- `cubeanimals:komododragon_egg` : donne l'œuf sans accès à une propriété `VARIANT` inexistante ;
- inventaire plein : drop de l'ItemStack au sol ;
- retrait du bloc effectué côté serveur ;
- Silk Touch reste compatible.

Les appels inventaire/drop utilisent exactement les descripteurs déjà présents dans `EagleNest.method_55766`.

## Construction sûre

Contrairement à 1.2.36, le `ClassWriter` n'est pas initialisé avec l'ancien `ClassReader` : le constant pool est intégralement reconstruit à partir des nœuds vivants. Les entrées invalides/stales des versions cassées ne peuvent donc pas survivre.

## Héritage conservé
- nettoyage des œufs naturels et legacy de 1.2.34 ;
- `placed_by_player` et éclosion des œufs reposés ;
- cycle de vie `eagle_nest` de 1.2.33 ;
- tous les spawns/IA/équilibrages de 1.2.34.

## Validation approfondie
- 235 classes du JAR Cube Animals : **0 descripteur Methodref/InterfaceMethodref/Fieldref invalide** ;
- analyse ASM `BasicVerifier` : PASS sur les deux classes modifiées ;
- `CrocodileEgg.method_55766` référence uniquement `method_7270:(Lnet/minecraft/class_1799;)Z` ;
- `KomodoDragonEgg.method_55766` référence uniquement cette même signature correcte et **aucun champ VARIANT** ;
- comparaison Cube Animals 1.2.34 → 1.2.37 : exactement 2 classes modifiées, aucune ajoutée/supprimée ;
- comparaison bundle 1.2.34 → 1.2.37 : seuls `fabric.mod.json`, `META-INF/jars/cubeanimals.jar` changent + ajout de la note 1.2.37 ;
- ZIP/JAR intégral vérifié sans erreur.

## Empreintes
- bundle 1.2.37 SHA-256 : `7706186c882802b15bd7878521dc2e37d3ebb5ad1e9efd9400f6b4e8d7a4b856`
- Cube Animals imbriqué SHA-256 : `6a0d27282820916cac574306e4edb4a9f1be2eda7b2c8095356d35de2cb5c844`

Versions 1.2.35 et 1.2.36 marquées BROKEN dans Git.
