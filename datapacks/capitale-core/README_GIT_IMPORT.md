# Snapshot Git
Base de sécurité : **capitale_core 1.5.6-RC9AO** vérifiée.
Extensions reprises de **RC9AR** : biomes `indivis:*` et tag de villes, sans supprimer les fonctions de debug présentes dans RC9AO.

## Version de travail
**capitale_core 1.5.6-RC9AS** — issue #116.

Changements :
- conserve les fonctions et quêtes RC9AO ;
- conserve les biomes/villes ajoutés jusqu'à RC9AR ;
- ajoute `cubeanimals:eagle_nest`, `cubeanimals:crocodile_egg` et `cubeanimals:komododragon_egg` aux blocs cassables en mode Aventure ;
- conserve `minecraft:fire` et `minecraft:soul_fire` ;
- migre uniquement le `can_break` historique exact feu/soul fire afin de ne pas écraser les composants personnalisés.
