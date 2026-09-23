function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_TRIBUNAL 1
tag @s add ACCESS_TRIBUNAL
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès] Tribunal reconnu.","color":"green"}
