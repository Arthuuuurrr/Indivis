function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_DIVERS_ACTE_DETTE 2
function capitale:dialogue/random/roll_3_self
function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Sieur Odon de Varenne]","color":"yellow"},{"text":" : Voilà qui confirme qu’on trouve rarement des gens utiles au premier appel. Revenez si votre curiosité s’accompagne enfin d’un peu d’efficacité.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Sieur Odon de Varenne]","color":"yellow"},{"text":" : Fort bien. Les dettes attendent mieux que les hommes, mais elles ne deviennent jamais plus aimables.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Sieur Odon de Varenne]","color":"yellow"},{"text":" : Refuser est encore un choix. Revenez quand votre prudence aura trouvé quelque utilité.","color":"white"}]
