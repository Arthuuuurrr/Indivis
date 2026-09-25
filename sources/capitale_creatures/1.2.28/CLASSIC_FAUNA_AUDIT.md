# Classic fauna coverage audit

Compared `META-INF/capitale/spawn_inventory.csv` from bundle 1.2.27 against the active family/wildlife profiles before building 1.2.28.

Fully covered classic fauna modules:
- CubeAnimals: 0 unprofiled
- AnimalGarden Hippopotamus: 0 unprofiled
- AnimalGarden White Rhinoceros: 0 unprofiled
- AnimalGarden Western Gorilla: 0 unprofiled
- DeerMod: 0 unprofiled
- WanderingWildlife: 0 unprofiled
- HMobs: 0 unprofiled

Not automatically added:
- Autonomous Orc Mobs: specialized variants remain outside normal profiles; 1.2.28 deliberately adds only `female_orc_warrior_red` to corruption at weight 1.
- Chaos MMO AI: many unprofiled entries are named bosses, seasonal/event encounters, advanced undead or special forms. They require individual review instead of automatic natural spawning.
