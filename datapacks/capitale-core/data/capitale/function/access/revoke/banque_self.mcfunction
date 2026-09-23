function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_BANQUE 0
tag @s remove ACCESS_BANQUE
function capitale:dialogue/sound/parole_garde_self
tellraw @s {"text":"[Accès] Banque révoqué.","color":"red"}
