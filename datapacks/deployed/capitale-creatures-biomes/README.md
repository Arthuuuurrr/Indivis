# Capitale Creatures — Biomes BETA 0.10

Minecraft 1.21.11 / Fabric.

## Nouveautés

- HMobs 1.3.0 est intégré au bundle. Ses quatre espèces conservent leurs habitats et poids/groupes d'origine via le datapack autoritaire : Brown Bear (forêts + taïgas), Lion/Hyena/Zebra (savanes).
- `orc_patrol_rules.json` ajoute des patrouilles d'orcs rares en surface, sans ajouter les orcs aux tables naturelles générales.
- `capitale:donjon` reçoit une population naturelle d'orcs au sol uniquement : Archer, Warrior, Female Orc Warrior, Warlock, Champion et Female Orc Elite.

## Patrouille d'orcs

Composition par défaut :
- 2 `autonomous_orc_mobs:orc_archer`
- 3 `autonomous_orc_mobs:orc_warrior`
- 2 `autonomous_orc_mobs:female_orc_warrior`
- 1 `autonomous_orc_mobs:orc_warlock`

Le contrôleur vérifie une fois par minute un joueur aléatoire de l'Overworld, avec 10 % de chance, puis impose 10 minutes de cooldown après une patrouille réussie. Il cherche une position chargée à 28–44 blocs, refuse l'eau et exclut `capitale:donjon`. Ces valeurs sont modifiables dans `orc_patrol_rules.json`.

## Architecture existante

- `biome_assignments.json` reste autoritaire pour les spawns naturels terrestres, Thornshell et requin.
- `aquatic_runtime_rules.json` reste autoritaire pour les nageurs stricts.
- `spawn_profiles_active.json` reste la source des poids et tailles de groupe.
- `disabled_natural_dino_variants.json` reste autoritaire pour les variantes désactivées.
- Les réglages aquatiques BETA 0.9 sont inchangés.
