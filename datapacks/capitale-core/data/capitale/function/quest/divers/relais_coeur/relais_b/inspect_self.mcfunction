
scoreboard players set @s CAP_RELAIS_COEUR_B 1
function capitale:quest/divers/relais_coeur/count_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Relais des conduites]","color":"yellow"},{"text":" : Vous ajustez les cadrans visibles et laissez la borne retrouver un cycle plus régulier.","color":"white"}]
execute unless score @s CAP_RELAIS_COEUR_COUNT matches 3 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s CAP_RELAIS_COEUR_COUNT matches 3 run tellraw @s [{"text":"[Objectif mis à jour] Relais inspectés : ","color":"yellow"},{"score":{"name":"@s","objective":"CAP_RELAIS_COEUR_COUNT"},"color":"gold"},{"text":"/3.","color":"yellow"}]
execute unless score @s CAP_RELAIS_COEUR_COUNT matches 3 run title @s times 5 45 15
execute unless score @s CAP_RELAIS_COEUR_COUNT matches 3 run title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
execute unless score @s CAP_RELAIS_COEUR_COUNT matches 3 run title @s subtitle {"text":"Relais inspectés : progrès enregistré.","color":"white"}
execute unless score @s CAP_RELAIS_COEUR_COUNT matches 3 at @s run playsound minecraft:block.conduit.activate master @s ~ ~ ~ 0.55 0.9
execute if score @s CAP_RELAIS_COEUR_COUNT matches 3 run scoreboard players set @s QUEST_DIVERS_RELAIS_COEUR 50
execute if score @s CAP_RELAIS_COEUR_COUNT matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_RELAIS_COEUR_COUNT matches 3 run tellraw @s {"text":"[Objectif mis à jour] Les trois relais répondent. Retournez voir la Technicienne du Cœur.","color":"yellow"}
execute if score @s CAP_RELAIS_COEUR_COUNT matches 3 run title @s times 5 55 15
execute if score @s CAP_RELAIS_COEUR_COUNT matches 3 run title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
execute if score @s CAP_RELAIS_COEUR_COUNT matches 3 run title @s subtitle {"text":"Retournez voir la Technicienne du Cœur.","color":"white"}
execute if score @s CAP_RELAIS_COEUR_COUNT matches 3 at @s run playsound minecraft:block.beacon.activate master @s ~ ~ ~ 0.65 1.05
scoreboard players set @s CAP_RELAIS_COEUR_COUNT 0
