function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Horlogère du Clocher]","color":"yellow"},{"text":" : Sans ressort calibré, je ne peux rien remonter. Vérifiez votre sac avant de me faire perdre une minute de plus.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Horlogère du Clocher]","color":"yellow"},{"text":" : Je ne remonte pas un clocher avec des mains vides. Apportez le ressort calibré, puis nous parlerons.","color":"white"}]
