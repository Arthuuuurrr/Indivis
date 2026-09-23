function capitale:quest/guidage/sync_self
scoreboard players set @s CAP_FLAG 0
execute if score @s CAP_GUIDE_ID matches 1 run function capitale:quest/guidage/reset/leovic_self
execute if score @s CAP_GUIDE_ID matches 2 run function capitale:quest/guidage/reset/aurele_self
execute if score @s CAP_GUIDE_ID matches 3 run function capitale:quest/guidage/reset/roch_self
execute if score @s CAP_GUIDE_ID matches 4 run function capitale:quest/guidage/reset/colin_self
execute if score @s CAP_FLAG matches 0 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_FLAG matches 0 run tellraw @s {"text":"[Guidage] Aucune escorte active à réinitialiser.","color":"gray"}
