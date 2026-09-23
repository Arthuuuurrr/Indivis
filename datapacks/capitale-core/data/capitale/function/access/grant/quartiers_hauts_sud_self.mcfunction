function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_QH_SUD 1
tag @s add ACCESS_QUARTIERS_HAUTS_SUD
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès] Quartiers hauts sud reconnu.","color":"green"}
