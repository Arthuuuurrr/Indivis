function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_QH_SUD 0
tag @s remove ACCESS_QUARTIERS_HAUTS_SUD
function capitale:dialogue/sound/parole_garde_self
tellraw @s {"text":"[Accès] Quartiers hauts sud révoqué.","color":"red"}
