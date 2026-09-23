function capitale:dialogue/random/roll_6_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Les relais vous attendent encore. Ne vous fiez pas à leur silence : c’est souvent là que les ennuis commencent.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Tant que les relevés ne sont pas faits, je travaille avec des soupçons. Les machines les entretiennent très bien ; moi, beaucoup moins.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Votre tâche n’est pas terminée. Trois relais, trois réponses, puis vous me rapportez ce que vous avez constaté.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 4 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 4 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Si votre accès tient encore, servez-vous-en. Les galeries intérieures n’apprécient pas les visites prolongées.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 5 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 5 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Un relais ignoré devient vite le problème de tout un passage. Retournez vérifier avant que le doute ne s’installe.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 6 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 6 run tellraw @s [{"text":"[Technicienne du Cœur]","color":"yellow"},{"text":" : Je n’ai pas besoin d’un récit héroïque. J’ai besoin de relevés propres.","color":"white"}]
