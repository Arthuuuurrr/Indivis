function capitale:dialogue/random/roll_6_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Les relais viennent d’être vérifiés. Laissez au Cœur le temps de reprendre son rythme avant de réclamer une nouvelle tournée.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Tout vient d’être contrôlé. Laissez les relais travailler avant de leur chercher de nouveaux défauts.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Pour aujourd’hui, les réponses sont assez nettes. Si elles changent, je le saurai avant vous.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 4 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 4 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Une inspection de plus ne rendrait pas les machines plus sages. Revenez quand le contrôle sera de nouveau utile.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 5 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 5 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Les galeries ont assez reçu de pas pour l’instant. Les relais tiennent.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 6 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 6 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Le Cœur gronde encore, mais ce n’est pas une raison pour accuser les mêmes bornes toutes les heures.","color":"white"}]
