scoreboard players add @s CAPREV_COOLDOWN 0
scoreboard players add @s CAPREV_RELOAD 0
scoreboard players add @s CAPREV_ALT 0
execute if score @s CAPREV_COOLDOWN matches 1.. run scoreboard players remove @s CAPREV_COOLDOWN 1
execute if score @s CAPREV_RELOAD matches 1.. run scoreboard players remove @s CAPREV_RELOAD 1
execute if score @s CAPREV_RELOAD matches 0 if entity @s[tag=capskills.revolver.reloading] at @s run function capskills:revolver/reload_complete_self
