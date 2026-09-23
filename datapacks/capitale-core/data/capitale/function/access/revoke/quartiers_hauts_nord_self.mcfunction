function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_QH_NORD 0
tag @s remove ACCESS_QUARTIERS_HAUTS_NORD
function capitale:dialogue/sound/parole_garde_self
tellraw @s {"text":"[Accès] Quartiers hauts nord révoqué.","color":"red"}
