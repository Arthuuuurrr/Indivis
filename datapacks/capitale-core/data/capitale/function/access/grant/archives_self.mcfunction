function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_ARCHIVES 1
tag @s add ACCESS_ARCHIVES
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès] Archives reconnu.","color":"green"}
