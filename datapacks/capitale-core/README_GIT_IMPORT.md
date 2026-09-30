# Snapshot Git — capitale_core

Source canonique consolidée : **capitale_core 1.5.6-RC9AW**.

RC9AW consolide explicitement les changements qui avaient été répartis entre plusieurs threads/branches :
- base fonctionnelle RC9AU et ses biomes urbains ;
- restauration de `indivis:corruption` depuis la définition historique livrée ;
- interactions Adventure RC9AT pour `cubeanimals:eagle_nest`, `cubeanimals:crocodile_egg` et `cubeanimals:komododragon_egg` ;
- optimisation PERF1 du raycast feu issue de la PR #134 ;
- conservation de `minecraft:fire` et `minecraft:soul_fire` ;
- conservation d'Aubecourt, de Sylvharen et de tous les biomes urbains RC9AU.

Voir `docs/CROSS_THREAD_AUDIT_RC9AW.md` pour l'audit détaillé et les validations runtime encore requises.
