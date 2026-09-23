function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_COEUR 0
tag @s remove ACCESS_COEUR
function capitale:dialogue/sound/parole_garde_self
tellraw @s {"text":"[Accès] Cœur révoqué.","color":"red"}
