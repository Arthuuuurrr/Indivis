function capitale:player/ensure_runtime_self
scoreboard players add @s CAP_CRIME 15
function capitale:bounds/crime_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Statut légal] Criminalité +15","color":"red"}
function capitale:crime/current_self
