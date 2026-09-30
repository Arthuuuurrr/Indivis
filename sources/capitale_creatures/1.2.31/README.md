# Capitale Creatures 1.2.31 / BETA 0.25

## Orc density correction
- Corruption keeps a rare singleton natural orc entry.
- Runtime cap: 4 unnamed Autonomous Orc Mobs within 96 blocks/player in indivis:corruption.
- Dungeon local/runtime cap: 10 unnamed Autonomous Orc Mobs within 96 blocks/player.
- Dungeon profile weights: Archer 3, Warrior 4, Female Warrior 3, Warlock 1, Champion 1, Female Elite 1.
- Excess persistent unnamed orcs are culled furthest-first every 40 ticks.
- Named/scripted orcs are preserved when Minecraft exposes a custom name.
- 1.2.30 marine AI changes are retained unchanged.

## Miniboss investigation
The uploaded rogues-fabric-3.1.2.002+1.21.11-mmo-nostructures-HC-FR-NATIF1 JAR does not contain Oyra Ona, Aurelio Voidsinger or Dena Lorenni and does not register humanoid miniboss entities. Do not patch that mod for this issue.
