
function capitale:player/ensure_runtime_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_C matches 0 run function capitale:quest/journalieres/temoignages_quais/temoin_clerc/record_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_C matches 1.. run function capitale:dialogue/random/roll_2_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_C matches 1.. if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_C matches 1.. if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Clerc des Bordereaux]","color":"yellow"},{"text":" : Je vous ai déjà dit ce que j’avais vu. Que le Greffier range cela dans ses lignes.","color":"white"}]
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_C matches 1.. if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_C matches 1.. if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Clerc des Bordereaux]","color":"yellow"},{"text":" : Mon témoignage est consigné. Je n’ajouterai point de rumeur aux bordereaux.","color":"white"}]
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 run function capitale:dialogue/random/roll_2_self
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Clerc des Bordereaux]","color":"yellow"},{"text":" : Les quais parlent beaucoup. Aujourd’hui, je préfère garder mes lignes pour les registres.","color":"white"}]
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Clerc des Bordereaux]","color":"yellow"},{"text":" : Sans affaire ouverte, je n’ai rien à confier qui mérite d’être scellé.","color":"white"}]
