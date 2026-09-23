function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_COEUR 1
tag @s add ACCESS_COEUR
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès] Cœur reconnu.","color":"green"}
