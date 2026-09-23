function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_TRIBUNAL 0
tag @s remove ACCESS_TRIBUNAL
function capitale:dialogue/sound/parole_garde_self
tellraw @s {"text":"[Accès] Tribunal révoqué.","color":"red"}
