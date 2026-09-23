function capitale:player/ensure_runtime_self
scoreboard players remove @s REP_COURONNE 30
function capitale:bounds/rep/couronne_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Réputation] Couronne -30","color":"red"}
function capitale:rep/couronne/current_self
