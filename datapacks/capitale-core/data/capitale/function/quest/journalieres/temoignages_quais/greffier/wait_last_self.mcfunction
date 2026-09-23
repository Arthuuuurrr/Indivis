function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Greffier des Quais]","color":"yellow"},{"text":" : Il me manque encore un témoignage. Un rapport presque complet reste un rapport incomplet.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Greffier des Quais]","color":"yellow"},{"text":" : Revenez avec la dernière voix. Trois témoins, c’est fragile ; deux, c’est une invitation à contester.","color":"white"}]
