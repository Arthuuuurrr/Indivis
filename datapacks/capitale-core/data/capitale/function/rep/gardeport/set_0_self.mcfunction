function capitale:player/ensure_runtime_self
scoreboard players set @s REP_GARDEPORT 0
function capitale:bounds/rep/gardeport_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Réputation] Garde du Port : 0","color":"light_purple"}
