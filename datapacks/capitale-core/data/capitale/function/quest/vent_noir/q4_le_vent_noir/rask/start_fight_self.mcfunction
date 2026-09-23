function capitale:player/ensure_runtime_self
execute if score @s QUEST_VN_NAVIRE matches 30 run scoreboard players set @s QUEST_VN_NAVIRE 40
execute if score @s QUEST_VN_NAVIRE matches 40 run title @s times 10 70 20
execute if score @s QUEST_VN_NAVIRE matches 40 run title @s title {"text":"Rask la Balafre","color":"dark_red","bold":true}
execute if score @s QUEST_VN_NAVIRE matches 40 run title @s subtitle {"text":"Chef des Écorcheurs du Ciel","color":"white"}
execute if score @s QUEST_VN_NAVIRE matches 40 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_NAVIRE matches 40 run tellraw @s [{"text":"[Rask la Balafre]","color":"red"},{"text":" : Les rois sont tombés. Les empires tomberont aussi. Toi, tu tomberas avant eux.","color":"white"}]
execute if score @s QUEST_VN_NAVIRE matches 40 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_VN_NAVIRE matches 40 run tellraw @s {"text":"[Objectif mis à jour] Vainquez Rask la Balafre.","color":"yellow"}
execute if score @s QUEST_VN_NAVIRE matches 40 at @s run playsound minecraft:entity.wither.spawn master @s ~ ~ ~ 0.55 1.35
