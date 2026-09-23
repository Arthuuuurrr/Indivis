function capitale:player/ensure_runtime_self
scoreboard players add @s REP_GARDECOEUR 20
function capitale:bounds/rep/gardecoeur_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Réputation] Gardes du Cœur +20","color":"green"}
function capitale:rep/gardecoeur/current_self
