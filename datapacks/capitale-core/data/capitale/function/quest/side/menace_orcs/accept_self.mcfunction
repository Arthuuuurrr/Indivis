function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_ALDREN_ORCS 10
scoreboard players set @s CAP_ALDREN_WARR 0
scoreboard players set @s CAP_ALDREN_ARCH 0
scoreboard players set @s CAP_ALDREN_CHAMP 0
scoreboard players set @s CAP_ALDREN_MORGRA 0
playsound minecraft:entity.villager.yes neutral @s ~ ~ ~ 1.0 1.0
tellraw @s [{"text":"Aldren le Traqueur: ","color":"yellow","bold":true},{"text":"« Bien. Que les dieux guident ta lame, voyageur. »","italic":true,"color":"white"}]
