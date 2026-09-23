function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_CAPITALE 1
tag @s add ACCESS_CAPITALE
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès] Capitale reconnu.","color":"green"}
