function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_CRIME 20
function capitale:bounds/crime_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Statut légal] Faute mineure (20)","color":"gold"}
