scoreboard players add @s REP_GARDECOEUR 0
scoreboard players operation @s CAP_RAW = @s REP_GARDECOEUR
execute if score @s REP_GARDECOEUR matches ..-70 run function capitale:dialogue/sound/parole_quete_self
execute if score @s REP_GARDECOEUR matches ..-70 run tellraw @s [{"text":"[Réputation] ","color":"dark_gray"},{"text":"Gardes du Cœur : ","color":"gray"},{"text":"Hostilité ouverte","color":"dark_red"},{"text":" (","color":"dark_gray"},{"score":{"name":"@s","objective":"CAP_RAW"},"color":"dark_gray"},{"text":")","color":"dark_gray"}]
execute if score @s REP_GARDECOEUR matches -69..-40 run function capitale:dialogue/sound/parole_quete_self
execute if score @s REP_GARDECOEUR matches -69..-40 run tellraw @s [{"text":"[Réputation] ","color":"dark_gray"},{"text":"Gardes du Cœur : ","color":"gray"},{"text":"Forte méfiance","color":"red"},{"text":" (","color":"dark_gray"},{"score":{"name":"@s","objective":"CAP_RAW"},"color":"dark_gray"},{"text":")","color":"dark_gray"}]
execute if score @s REP_GARDECOEUR matches -39..-10 run function capitale:dialogue/sound/parole_quete_self
execute if score @s REP_GARDECOEUR matches -39..-10 run tellraw @s [{"text":"[Réputation] ","color":"dark_gray"},{"text":"Gardes du Cœur : ","color":"gray"},{"text":"Mauvaise réputation","color":"gold"},{"text":" (","color":"dark_gray"},{"score":{"name":"@s","objective":"CAP_RAW"},"color":"dark_gray"},{"text":")","color":"dark_gray"}]
execute if score @s REP_GARDECOEUR matches -9..9 run function capitale:dialogue/sound/parole_quete_self
execute if score @s REP_GARDECOEUR matches -9..9 run tellraw @s [{"text":"[Réputation] ","color":"dark_gray"},{"text":"Gardes du Cœur : ","color":"gray"},{"text":"Neutre","color":"gray"},{"text":" (","color":"dark_gray"},{"score":{"name":"@s","objective":"CAP_RAW"},"color":"dark_gray"},{"text":")","color":"dark_gray"}]
execute if score @s REP_GARDECOEUR matches 10..39 run function capitale:dialogue/sound/parole_simple_self
execute if score @s REP_GARDECOEUR matches 10..39 run tellraw @s [{"text":"[Réputation] ","color":"dark_gray"},{"text":"Gardes du Cœur : ","color":"gray"},{"text":"Apprécié","color":"green"},{"text":" (+","color":"dark_gray"},{"score":{"name":"@s","objective":"CAP_RAW"},"color":"dark_gray"},{"text":")","color":"dark_gray"}]
execute if score @s REP_GARDECOEUR matches 40..69 run function capitale:dialogue/sound/parole_simple_self
execute if score @s REP_GARDECOEUR matches 40..69 run tellraw @s [{"text":"[Réputation] ","color":"dark_gray"},{"text":"Gardes du Cœur : ","color":"gray"},{"text":"Reconnu","color":"aqua"},{"text":" (+","color":"dark_gray"},{"score":{"name":"@s","objective":"CAP_RAW"},"color":"dark_gray"},{"text":")","color":"dark_gray"}]
execute if score @s REP_GARDECOEUR matches 70.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s REP_GARDECOEUR matches 70.. run tellraw @s [{"text":"[Réputation] ","color":"dark_gray"},{"text":"Gardes du Cœur : ","color":"gray"},{"text":"Très estimé","color":"light_purple"},{"text":" (+","color":"dark_gray"},{"score":{"name":"@s","objective":"CAP_RAW"},"color":"dark_gray"},{"text":")","color":"dark_gray"}]
