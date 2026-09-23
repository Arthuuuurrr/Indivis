function capitale:player/ensure_runtime_self
scoreboard players set @s REP_COURONNE -50
function capitale:bounds/rep/couronne_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Réputation] Couronne : -50","color":"light_purple"}
