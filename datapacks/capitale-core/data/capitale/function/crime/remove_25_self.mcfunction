function capitale:player/ensure_runtime_self
scoreboard players remove @s CAP_CRIME 25
function capitale:bounds/crime_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Statut légal] Criminalité -25","color":"green"}
function capitale:crime/current_self
