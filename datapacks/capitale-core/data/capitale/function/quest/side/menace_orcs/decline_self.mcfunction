function capitale:quest/dialogue/clear_self
playsound minecraft:entity.villager.no neutral @s ~ ~ ~ 1.0 1.0
tellraw @s [{"text":"Aldren le Traqueur: ","color":"yellow","bold":true},{"text":"« Comme tu veux. Reviens me voir si tu changes d'avis. »","italic":true,"color":"white"}]
