function capitale:player/ensure_runtime_self
scoreboard players set @s REP_GARDECOEUR 50
function capitale:bounds/rep/gardecoeur_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Réputation] Gardes du Cœur : +50","color":"light_purple"}
