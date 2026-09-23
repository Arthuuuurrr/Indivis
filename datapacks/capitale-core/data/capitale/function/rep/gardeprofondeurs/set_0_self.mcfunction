function capitale:player/ensure_runtime_self
scoreboard players set @s REP_GARDEPROFONDEURS 0
function capitale:bounds/rep/gardeprofondeurs_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Réputation] Gardes des Profondeurs : 0","color":"light_purple"}
