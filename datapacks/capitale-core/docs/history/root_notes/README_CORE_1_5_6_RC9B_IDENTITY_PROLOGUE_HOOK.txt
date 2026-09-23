Capitale Core 1.5.6 RC9b — Identity Prologue Hook

Base : RC9 biome capitale safe.

Ajouts :
- data/capitale/function/spawn/character_first_join_to_prologue_start_self.mcfunction
- data/capitale/function/spawn/identity_open_mandatory_self.mcfunction
- hook conditionnel au début de capitale:spawn/start_prologue_self

Le prologue classique reste inchangé pour les joueurs/admins normaux. Le menu d'identité n'est forcé que si le joueur porte le tag capitale_identity_mandatory, ajouté par le mod RP HUD lors du chargement d'un nouveau slot.

La quête n'est pas réécrite : scoreboard, téléportation, spawnpoint et dialogues de start_prologue_self restent ceux de la RC9.
