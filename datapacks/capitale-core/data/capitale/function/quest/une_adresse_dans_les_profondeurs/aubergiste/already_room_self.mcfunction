function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Aubergiste des Profondeurs]","color":"yellow"},{"text":" : Votre chambre est déjà retenue. Si c’est le registre qui vous manque, le magistrat vous attend.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Aubergiste des Profondeurs]","color":"yellow"},{"text":" : La chambre vous attend déjà. Pour l’inscription en bonne forme, c’est toujours le magistrat qu’il faut voir.","color":"white"}]
