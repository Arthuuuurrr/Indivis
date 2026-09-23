function capitale:player/ensure_runtime_self
scoreboard players remove @s REP_GARDEPORT 10
function capitale:bounds/rep/gardeport_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Réputation] Garde du Port -10","color":"red"}
function capitale:rep/gardeport/current_self
