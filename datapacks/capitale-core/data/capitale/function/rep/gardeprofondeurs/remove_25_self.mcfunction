function capitale:player/ensure_runtime_self
scoreboard players remove @s REP_GARDEPROFONDEURS 25
function capitale:bounds/rep/gardeprofondeurs_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Réputation] Gardes des Profondeurs -25","color":"red"}
function capitale:rep/gardeprofondeurs/current_self
