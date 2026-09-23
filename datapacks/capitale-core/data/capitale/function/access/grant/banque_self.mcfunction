function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_BANQUE 1
tag @s add ACCESS_BANQUE
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès] Banque reconnu.","color":"green"}
