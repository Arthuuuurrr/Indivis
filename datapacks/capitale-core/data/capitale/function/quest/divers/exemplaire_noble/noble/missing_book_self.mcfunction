function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Dame Yselle de Verceuil]","color":"yellow"},{"text":" : Vous venez du libraire ? Alors vous devriez avoir un exemplaire à me remettre. Sans lui, je ne puis accuser réception de quoi que ce soit.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Dame Yselle de Verceuil]","color":"yellow"},{"text":" : Je ne signe pas une réception sur de simples paroles. Revenez avec l’exemplaire du libraire.","color":"white"}]
