function capitale:player/ensure_runtime_self
scoreboard players set @s ACCESS_PALAIS 1
tag @s add ACCESS_PALAIS
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Accès] Palais reconnu.","color":"green"}
