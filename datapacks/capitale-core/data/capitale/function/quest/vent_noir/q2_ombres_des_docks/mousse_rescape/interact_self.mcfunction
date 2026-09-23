function capitale:player/ensure_runtime_self
execute if score @s QUEST_VN_DOCKS matches 40 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_DOCKS matches 40 run tellraw @s [{"text":"[Mousse rescapé]","color":"yellow"},{"text":" : Le Vent Noir n’est pas amarré. Rask le garde au-dessus des toits morts, derrière les grues de brume.","color":"white"}]
execute if score @s QUEST_VN_DOCKS matches 40 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_DOCKS matches 40 run tellraw @s [{"text":"[Mousse rescapé]","color":"yellow"},{"text":" : Il prépare un départ. Pas une fuite : un coup. Dites à Maelor qu’il n’a plus beaucoup de temps.","color":"white"}]
execute if score @s QUEST_VN_DOCKS matches 40 run scoreboard players set @s QUEST_VN_DOCKS 50
execute if score @s QUEST_VN_DOCKS matches 50 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_DOCKS matches 50 run tellraw @s {"text":"[Objectif mis à jour] Retournez voir Maelor Veyne.","color":"yellow"}
execute if score @s QUEST_VN_DOCKS matches 50 run title @s times 5 50 15
execute if score @s QUEST_VN_DOCKS matches 50 run title @s title {"text":"Objectif mis à jour","color":"gold","bold":true}
execute if score @s QUEST_VN_DOCKS matches 50 run title @s subtitle {"text":"Retournez voir Maelor Veyne.","color":"white"}
execute if score @s QUEST_VN_DOCKS matches 50 at @s run playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1.15 1.05
execute if score @s QUEST_VN_DOCKS matches 51..100 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_DOCKS matches 51..100 run tellraw @s [{"text":"[Mousse rescapé]","color":"yellow"},{"text":" : Je ne remonterai pas là-haut. Pas aujourd’hui. Peut-être jamais.","color":"white"}]
execute unless score @s QUEST_VN_DOCKS matches 40..100 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_VN_DOCKS matches 40..100 run tellraw @s [{"text":"[Mousse rescapé]","color":"yellow"},{"text":" : Je n’ai rien vu. Ou rien que je sois prêt à dire à un inconnu.","color":"white"}]
