function capitale:player/ensure_runtime_self
scoreboard players set @s REP_GARDEPORT -50
function capitale:bounds/rep/gardeport_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Réputation] Garde du Port : -50","color":"light_purple"}
