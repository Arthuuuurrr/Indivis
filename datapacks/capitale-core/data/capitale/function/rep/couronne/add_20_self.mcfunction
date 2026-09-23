function capitale:player/ensure_runtime_self
scoreboard players add @s REP_COURONNE 20
function capitale:bounds/rep/couronne_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Réputation] Couronne +20","color":"green"}
function capitale:rep/couronne/current_self
