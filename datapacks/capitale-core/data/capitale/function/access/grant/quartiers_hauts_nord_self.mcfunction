function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_QH_NORD 1
tag @s add ACCESS_QUARTIERS_HAUTS_NORD
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès] Quartiers hauts nord reconnu.","color":"green"}
