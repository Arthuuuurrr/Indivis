function capitale:player/ensure_runtime_self
scoreboard players remove @s REP_GARDECOEUR 30
function capitale:bounds/rep/gardecoeur_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Réputation] Gardes du Cœur -30","color":"red"}
function capitale:rep/gardecoeur/current_self
