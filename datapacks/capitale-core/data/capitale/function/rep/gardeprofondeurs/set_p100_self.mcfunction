function capitale:player/ensure_runtime_self
scoreboard players set @s REP_GARDEPROFONDEURS 100
function capitale:bounds/rep/gardeprofondeurs_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Réputation] Gardes des Profondeurs : +100","color":"light_purple"}
