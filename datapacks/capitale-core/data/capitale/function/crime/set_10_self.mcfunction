function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_CRIME 10
function capitale:bounds/crime_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Statut légal] Avertissement inscrit (10)","color":"gold"}
