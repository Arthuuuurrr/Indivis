function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_SEUILS 1
tag @s add ACCESS_SEUILS_SCELLES
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès] Portails scellés reconnu.","color":"green"}
