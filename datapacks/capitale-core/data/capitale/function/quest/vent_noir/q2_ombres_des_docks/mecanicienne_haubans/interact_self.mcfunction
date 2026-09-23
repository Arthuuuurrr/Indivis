function capitale:player/ensure_runtime_self
execute if score @s QUEST_VN_DOCKS matches 30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_DOCKS matches 30 run tellraw @s [{"text":"[Mécanicienne des haubans]","color":"yellow"},{"text":" : Trois régulateurs de poussée ont disparu. Pas du matériel pour une barque de ruelle.","color":"white"}]
execute if score @s QUEST_VN_DOCKS matches 30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_DOCKS matches 30 run tellraw @s [{"text":"[Mécanicienne des haubans]","color":"yellow"},{"text":" : Le gamin qui les a portés tremblait plus que mes chaudières. Un mousse, ou ce qu’il en reste.","color":"white"}]
execute if score @s QUEST_VN_DOCKS matches 30 run scoreboard players set @s QUEST_VN_DOCKS 40
execute if score @s QUEST_VN_DOCKS matches 40 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_DOCKS matches 40 run tellraw @s {"text":"[Objectif mis à jour] Retrouvez le Mousse rescapé.","color":"yellow"}
execute if score @s QUEST_VN_DOCKS matches 40 run title @s times 5 50 15
execute if score @s QUEST_VN_DOCKS matches 40 run title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
execute if score @s QUEST_VN_DOCKS matches 40 run title @s subtitle {"text":"Retrouvez le Mousse rescapé.","color":"white"}
execute if score @s QUEST_VN_DOCKS matches 40 at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.15
execute if score @s QUEST_VN_DOCKS matches 41..100 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_DOCKS matches 41..100 run tellraw @s [{"text":"[Mécanicienne des haubans]","color":"yellow"},{"text":" : Je ne répare pas les consciences. Cherchez celui qui a vu le pont du Vent Noir.","color":"white"}]
execute unless score @s QUEST_VN_DOCKS matches 30..100 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_VN_DOCKS matches 30..100 run tellraw @s [{"text":"[Mécanicienne des haubans]","color":"yellow"},{"text":" : Si vous n’avez ni pièce à régler ni moteur à faire taire, épargnez mes heures.","color":"white"}]
