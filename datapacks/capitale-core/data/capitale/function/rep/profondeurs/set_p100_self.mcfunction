function capitale:player/ensure_runtime_self
scoreboard players set @s REP_PROFONDEURS 100
function capitale:bounds/rep/profondeurs_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Réputation] Profondeurs : +100","color":"light_purple"}
