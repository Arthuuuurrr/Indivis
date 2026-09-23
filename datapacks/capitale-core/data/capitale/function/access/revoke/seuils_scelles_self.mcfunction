function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_SEUILS 0
tag @s remove ACCESS_SEUILS_SCELLES
function capitale:dialogue/sound/parole_garde_self
tellraw @s {"text":"[Accès] Portails scellés révoqué.","color":"red"}
