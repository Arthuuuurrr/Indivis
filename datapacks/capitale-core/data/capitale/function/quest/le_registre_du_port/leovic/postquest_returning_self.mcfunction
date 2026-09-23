function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Je regagne mon poste. Repassez me voir sur les quais lorsque j’y serai revenu.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Je reprends la route du Port. Si vous avez encore affaire à moi, attendez que je retrouve les quais.","color":"white"}]
