function capitale:player/ensure_runtime_self
scoreboard players remove @s REP_PROFONDEURS 10
function capitale:bounds/rep/profondeurs_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Réputation] Profondeurs -10","color":"red"}
function capitale:rep/profondeurs/current_self
