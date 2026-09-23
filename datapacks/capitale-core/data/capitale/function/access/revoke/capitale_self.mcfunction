function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_CAPITALE 0
tag @s remove ACCESS_CAPITALE
function capitale:dialogue/sound/parole_garde_self
tellraw @s {"text":"[Accès] Capitale révoqué.","color":"red"}
