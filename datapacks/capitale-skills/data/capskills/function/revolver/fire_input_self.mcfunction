scoreboard players set @s CAPREV_FIRED 0
tag @s remove capskills.revolver.fired_main
tag @s remove capskills.revolver.fired_off
scoreboard players add @s CAPREV_COOLDOWN 0
scoreboard players add @s CAPREV_RELOAD 0
scoreboard players add @s CAPREV_ALT 0
scoreboard players set @s CAPREV_STATE 0
execute if items entity @s weapon.mainhand #capskills:weapon/firearm_revolver run scoreboard players set @s CAPREV_STATE 1
execute if items entity @s weapon.offhand #capskills:weapon/firearm_revolver run scoreboard players add @s CAPREV_STATE 2
execute if score @s CAPREV_STATE matches 1.. run function capskills:revolver/ammo/read_main_self
execute if score @s CAPREV_STATE matches 2.. run function capskills:revolver/ammo/read_off_self
execute if score @s CAPREV_RELOAD matches 1.. run title @s actionbar {"text":"Rechargement en cours.","color":"yellow"}
execute if score @s CAPREV_STATE matches 1.. if score @s CAPREV_RELOAD matches 0 if score @s CAPREV_COOLDOWN matches 0 run function capskills:revolver/select_hand_self
