function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Les registres sont en ordre et vous savez désormais où commence vraiment la ville. Si les quais vous ramènent un jour vers moi, je trouverai sans doute de quoi occuper une paire de mains fiables.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Votre nom est désormais posé là où il faut. Revenez aux quais si vous cherchez plus tard un ouvrage honnête à rendre.","color":"white"}]
