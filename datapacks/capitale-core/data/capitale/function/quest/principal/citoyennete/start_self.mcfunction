
execute if score @s QUEST_CITOYENNETE matches 0 run scoreboard players set @s QUEST_CITOYENNETE 20
execute if score @s QUEST_CITOYENNETE matches 20 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_CITOYENNETE matches 20 run tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Vous êtes désormais Résident inscrit. C’est assez pour demeurer ici, pas encore pour peser dans la cité. La citoyenneté vous ouvrira un autre seuil — contre cinq cents Martins d’Or de droits civiques.","color":"white"}]
execute if score @s QUEST_CITOYENNETE matches 20 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_CITOYENNETE matches 20 run tellraw @s {"text":"[Objectif principal] Obtenir la citoyenneté de la Capitale.","color":"gold"}
execute if score @s QUEST_CITOYENNETE matches 20 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_CITOYENNETE matches 20 run tellraw @s {"text":"[Objectif mis à jour] Rassemblez 500 Martins d’Or et déposez-les auprès d’un magistrat.","color":"yellow"}
execute if score @s QUEST_CITOYENNETE matches 20 run title @s times 10 70 20
execute if score @s QUEST_CITOYENNETE matches 20 run title @s title {"text":"Objectif principal","color":"gold","bold":true}
execute if score @s QUEST_CITOYENNETE matches 20 run title @s subtitle {"text":"Obtenir la citoyenneté de la Capitale","color":"white"}
execute if score @s QUEST_CITOYENNETE matches 20 at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 0.7 1.0
