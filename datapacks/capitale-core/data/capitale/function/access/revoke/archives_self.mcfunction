function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_ARCHIVES 0
tag @s remove ACCESS_ARCHIVES
function capitale:dialogue/sound/parole_garde_self
tellraw @s {"text":"[Accès] Archives révoqué.","color":"red"}
