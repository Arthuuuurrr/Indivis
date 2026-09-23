function capitale:player/ensure_runtime_self
execute if score @s QUEST_VN_DOCKS matches 20 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_DOCKS matches 20 run tellraw @s [{"text":"[Guetteur des quais]","color":"yellow"},{"text":" : Rask n’amène jamais son équipage à découvert. Mais ses hommes achètent encore de l’huile de levage sous le vieux ponton.","color":"white"}]
execute if score @s QUEST_VN_DOCKS matches 20 run scoreboard players set @s QUEST_VN_DOCKS 30
execute if score @s QUEST_VN_DOCKS matches 30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_DOCKS matches 30 run tellraw @s {"text":"[Objectif mis à jour] Trouvez la Mécanicienne des haubans.","color":"yellow"}
execute if score @s QUEST_VN_DOCKS matches 30 run title @s times 5 50 15
execute if score @s QUEST_VN_DOCKS matches 30 run title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
execute if score @s QUEST_VN_DOCKS matches 30 run title @s subtitle {"text":"Trouvez la Mécanicienne des haubans.","color":"white"}
execute if score @s QUEST_VN_DOCKS matches 30 at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.2
execute if score @s QUEST_VN_DOCKS matches 31..100 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_DOCKS matches 31..100 run tellraw @s [{"text":"[Guetteur des quais]","color":"yellow"},{"text":" : Je vous ai donné ce que je savais. Le reste rouille chez ceux qui touchent aux moteurs.","color":"white"}]
execute unless score @s QUEST_VN_DOCKS matches 20..100 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_VN_DOCKS matches 20..100 run tellraw @s [{"text":"[Guetteur des quais]","color":"yellow"},{"text":" : Les quais voient beaucoup de visages. Je ne réponds pas à tous ceux qui cherchent des noms.","color":"white"}]
