function capitale:player/ensure_runtime_self
execute if score @s QUEST_DAILY_RESSORT_CLOCHER matches 30 run function capitale:quest/journalieres/ressort_clocher/horlogere/attempt_complete_self
execute if score @s QUEST_DAILY_RESSORT_CLOCHER matches 20 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_DAILY_RESSORT_CLOCHER matches 20 run tellraw @s [{"text":"[Horlogère du Clocher]","color":"yellow"},{"text":" : Vous arrivez du Port ? Prenez déjà vos repères dans le quartier ; ensuite seulement je saurai si c’est bien ma livraison.","color":"white"}]
execute unless score @s QUEST_DAILY_RESSORT_CLOCHER matches 20..30 run function capitale:dialogue/random/roll_2_self
execute unless score @s QUEST_DAILY_RESSORT_CLOCHER matches 20..30 if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DAILY_RESSORT_CLOCHER matches 20..30 if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Horlogère du Clocher]","color":"yellow"},{"text":" : Les vieux mécanismes n’ont rien contre les visiteurs, tant qu’ils ne confondent pas curiosité et tournevis.","color":"white"}]
execute unless score @s QUEST_DAILY_RESSORT_CLOCHER matches 20..30 if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DAILY_RESSORT_CLOCHER matches 20..30 if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Horlogère du Clocher]","color":"yellow"},{"text":" : Si vous entendez trois coups au lieu de deux, ne touchez à rien. Certains silences valent réparation.","color":"white"}]
