function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Maître Lucain Perrin. Quartier des Vieilles Mécaniques. Près du clocher. Demandez l’acte, pas son histoire de vie, même s’il vous la servira probablement.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Odon Varenne]","color":"yellow"},{"text":" : Cherchez Lucain Perrin près du clocher des Vieilles Mécaniques. Prenez l’acte, pas tout son théâtre.","color":"white"}]
