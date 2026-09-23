function capitale:player/ensure_runtime_self
execute if score @s QUEST_VN_CAPITAINE matches 20 run scoreboard players set @s QUEST_VN_CAPITAINE 30
execute if score @s QUEST_VN_CAPITAINE matches 30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_CAPITAINE matches 30 run tellraw @s {"text":"[Objectif mis à jour] Les hommes de Rask ont reculé. Reparlez à Maelor Veyne.","color":"yellow"}
execute if score @s QUEST_VN_CAPITAINE matches 30 run title @s times 5 50 15
execute if score @s QUEST_VN_CAPITAINE matches 30 run title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
execute if score @s QUEST_VN_CAPITAINE matches 30 run title @s subtitle {"text":"Reparlez à Maelor Veyne.","color":"white"}
execute if score @s QUEST_VN_CAPITAINE matches 30 at @s run playsound minecraft:entity.player.attack.crit master @s ~ ~ ~ 0.6 0.9
