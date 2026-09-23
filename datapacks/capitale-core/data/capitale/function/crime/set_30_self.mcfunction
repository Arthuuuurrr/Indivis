function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_CRIME 30
function capitale:bounds/crime_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Statut légal] Recherché (30)","color":"gold"}
