function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : Roch m’a parlé d’une caisse, mais je ne la vois pas entre vos mains. Peu importe : l’auberge reste à portée, et vous aurez d’autres occasions de vous rendre utile.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : Roch m’a parlé d’une caisse, mais je ne la vois pas entre vos mains. Peu importe : l’auberge reste à portée, et vous aurez d’autres occasions de vous rendre utile .","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : Écoutez bien. Roch m’a parlé d’une caisse, mais je ne la vois pas entre vos mains. Peu importe : l’auberge reste à portée, et vous aurez d’autres occasions de vous rendre utile.","color":"white"}]
scoreboard players set @s QUEST_PROFONDEURS 40
function capitale:quest/au_seuil_des_profondeurs/colin/open_choices_self
