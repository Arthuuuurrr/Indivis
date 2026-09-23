scoreboard players add @s CAPREV_RELOAD 0
scoreboard players set @s CAPREV_STATE 0
execute if items entity @s weapon.mainhand #capskills:weapon/firearm_revolver run scoreboard players set @s CAPREV_STATE 1
execute if items entity @s weapon.offhand #capskills:weapon/firearm_revolver run scoreboard players add @s CAPREV_STATE 2
execute if score @s CAPREV_STATE matches 0 run title @s actionbar {"text":"Aucun revolver tenu.","color":"dark_gray"}
execute if score @s CAPREV_STATE matches 1 if score @s CAPREV_RELOAD matches 0 run scoreboard players set @s CAPREV_RELOAD 40
execute if score @s CAPREV_STATE matches 2 if score @s CAPREV_RELOAD matches 0 run scoreboard players set @s CAPREV_RELOAD 40
execute if score @s CAPREV_STATE matches 3 if score @s CAPREV_RELOAD matches 0 run scoreboard players set @s CAPREV_RELOAD 55
# Maîtrise I : rechargement plus propre, sans modifier la cadence de tir.
execute if entity @s[tag=capskills.firearm.mastery.r1] if score @s CAPREV_STATE matches 1..2 if score @s CAPREV_RELOAD matches 40 run scoreboard players set @s CAPREV_RELOAD 36
execute if entity @s[tag=capskills.firearm.mastery.r1] if score @s CAPREV_STATE matches 3 if score @s CAPREV_RELOAD matches 55 run scoreboard players set @s CAPREV_RELOAD 50
# Maîtrise II : environ 15 % plus rapide que le rang I, sans réinitialiser un rechargement déjà commencé.
execute if entity @s[tag=capskills.firearm.mastery.r2] if score @s CAPREV_STATE matches 1..2 if score @s CAPREV_RELOAD matches 36 run scoreboard players set @s CAPREV_RELOAD 31
execute if entity @s[tag=capskills.firearm.mastery.r2] if score @s CAPREV_STATE matches 1..2 if score @s CAPREV_RELOAD matches 40 run scoreboard players set @s CAPREV_RELOAD 31
execute if entity @s[tag=capskills.firearm.mastery.r2] if score @s CAPREV_STATE matches 3 if score @s CAPREV_RELOAD matches 50 run scoreboard players set @s CAPREV_RELOAD 43
execute if entity @s[tag=capskills.firearm.mastery.r2] if score @s CAPREV_STATE matches 3 if score @s CAPREV_RELOAD matches 55 run scoreboard players set @s CAPREV_RELOAD 43
execute if score @s CAPREV_STATE matches 1.. if score @s CAPREV_RELOAD matches 1.. unless entity @s[tag=capskills.revolver.reloading] run tag @s add capskills.revolver.reloading
execute if entity @s[tag=capskills.revolver.reloading] run playsound minecraft:block.iron_trapdoor.close player @s ~ ~ ~ 0.7 1.25
execute if score @s CAPREV_STATE matches 1.. if entity @s[tag=capskills.revolver.reloading] run title @s actionbar [{"text":"Rechargement… ","color":"yellow"},{"score":{"name":"@s","objective":"CAPREV_RELOAD"},"color":"gold"},{"text":" ticks","color":"gray"}]
