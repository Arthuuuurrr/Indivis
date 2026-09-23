execute if items entity @s weapon.mainhand #capskills:weapon/firearm_revolver run scoreboard players set @s CAPREV_MAIN 6
execute if items entity @s weapon.offhand #capskills:weapon/firearm_revolver run scoreboard players set @s CAPREV_OFF 6
tag @s remove capskills.revolver.reloading
playsound minecraft:item.crossbow.loading_end player @s ~ ~ ~ 0.8 0.75
title @s actionbar {"text":"Revolver rechargé : 6 coups par barillet.","color":"green"}
