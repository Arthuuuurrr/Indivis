
function capitale:player/ensure_runtime_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_B matches 0 run function capitale:quest/journalieres/temoignages_quais/temoin_mariniere/record_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_B matches 1.. run function capitale:dialogue/random/roll_2_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_B matches 1.. if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_B matches 1.. if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Marinière]","color":"yellow"},{"text":" : J’ai déjà donné ma version. Que les bureaux en fassent bon usage.","color":"white"}]
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_B matches 1.. if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_B matches 1.. if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Marinière]","color":"yellow"},{"text":" : Mon récit est livré. Les rumeurs peuvent bien voguer sans moi.","color":"white"}]
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 run function capitale:dialogue/random/roll_2_self
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Marinière]","color":"yellow"},{"text":" : Les quais parlent beaucoup. Aujourd’hui, je préfère laisser passer le flot.","color":"white"}]
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Marinière]","color":"yellow"},{"text":" : Sans affaire ouverte, je garde le cap et mes paroles avec.","color":"white"}]
