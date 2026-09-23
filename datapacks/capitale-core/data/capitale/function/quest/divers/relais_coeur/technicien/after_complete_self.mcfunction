function capitale:dialogue/random/roll_6_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Les relais ont déjà été revus grâce à vous. Le Cœur gronde toujours, mais au moins il gronde avec méthode.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Les relais tiennent mieux depuis votre passage. Le Cœur n’en est pas silencieux, mais il se montre moins capricieux.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Les relevés sont propres. Les machines restent anciennes, mais leur humeur du jour est moins douteuse.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 4 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 4 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Pour une fois, les galeries intérieures nous rendent des réponses lisibles. Je prends cela comme une victoire modeste.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 5 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 5 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Vous avez fait ce qui était demandé, sans transformer une inspection en catastrophe. C’est plus rare qu’on ne le pense.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 6 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 6 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Les relais n’ont pas besoin d’être admirés. Ils ont besoin qu’on les écoute au bon moment. Aujourd’hui, c’est fait.","color":"white"}]
