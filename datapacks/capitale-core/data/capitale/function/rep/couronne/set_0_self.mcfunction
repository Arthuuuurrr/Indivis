function capitale:player/ensure_runtime_self
scoreboard players set @s REP_COURONNE 0
function capitale:bounds/rep/couronne_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Réputation] Couronne : 0","color":"light_purple"}
