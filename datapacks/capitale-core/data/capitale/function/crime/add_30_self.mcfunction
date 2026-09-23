function capitale:player/ensure_runtime_self
scoreboard players add @s CAP_CRIME 30
function capitale:bounds/crime_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Statut légal] Criminalité +30","color":"red"}
function capitale:crime/current_self
