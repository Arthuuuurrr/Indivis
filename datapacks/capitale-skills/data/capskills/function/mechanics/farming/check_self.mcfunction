# 0.8.29 — bonus de récolte pour branche Agriculteur.
execute if entity @s[tag=capskills.farming.r1] if score @s CAPSK_F_WHEAT matches 1.. run function capskills:mechanics/farming/proc/wheat_self
scoreboard players set @s CAPSK_F_WHEAT 0
execute if entity @s[tag=capskills.farming.r1] if score @s CAPSK_F_CARROTS matches 1.. run function capskills:mechanics/farming/proc/carrots_self
scoreboard players set @s CAPSK_F_CARROTS 0
execute if entity @s[tag=capskills.farming.r1] if score @s CAPSK_F_POTATOES matches 1.. run function capskills:mechanics/farming/proc/potatoes_self
scoreboard players set @s CAPSK_F_POTATOES 0
execute if entity @s[tag=capskills.farming.r1] if score @s CAPSK_F_BEETS matches 1.. run function capskills:mechanics/farming/proc/beetroots_self
scoreboard players set @s CAPSK_F_BEETS 0
execute if entity @s[tag=capskills.farming.r1] if score @s CAPSK_F_NWART matches 1.. run function capskills:mechanics/farming/proc/nether_wart_self
scoreboard players set @s CAPSK_F_NWART 0
execute if entity @s[tag=capskills.farming.r1] if score @s CAPSK_F_COCOA matches 1.. run function capskills:mechanics/farming/proc/cocoa_self
scoreboard players set @s CAPSK_F_COCOA 0
execute if entity @s[tag=capskills.farming.r1] if score @s CAPSK_F_MELON matches 1.. run function capskills:mechanics/farming/proc/melon_self
scoreboard players set @s CAPSK_F_MELON 0
execute if entity @s[tag=capskills.farming.r1] if score @s CAPSK_F_PUMPKIN matches 1.. run function capskills:mechanics/farming/proc/pumpkin_self
scoreboard players set @s CAPSK_F_PUMPKIN 0
