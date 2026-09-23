
function capitale:player/ensure_runtime_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_A matches 0 run function capitale:quest/journalieres/temoignages_quais/temoin_debardeur/record_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_A matches 1.. run function capitale:dialogue/random/roll_2_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_A matches 1.. if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_A matches 1.. if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Débardeur des Quais]","color":"yellow"},{"text":" : Je vous ai déjà dit ce que j’avais vu. Si le Greffier veut plus, qu’il vienne porter les caisses.","color":"white"}]
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_A matches 1.. if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_TEMOIN_QUAI_A matches 1.. if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Débardeur des Quais]","color":"yellow"},{"text":" : Mon mot est donné. Les quais ajoutent assez de bruit sans que j’en rajoute.","color":"white"}]
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 run function capitale:dialogue/random/roll_2_self
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Débardeur des Quais]","color":"yellow"},{"text":" : Les quais parlent beaucoup. Aujourd’hui, je garde mes forces pour les charges.","color":"white"}]
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DAILY_TEMOIGNAGES_QUAIS matches 20 if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Débardeur des Quais]","color":"yellow"},{"text":" : Sans demande du Greffier, je n’ai rien à déposer. Les caisses, elles, n’attendent pas.","color":"white"}]
