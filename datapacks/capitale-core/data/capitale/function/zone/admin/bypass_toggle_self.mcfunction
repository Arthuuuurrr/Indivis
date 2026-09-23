scoreboard players set @s CAP_ZONE_TMP 0
execute if entity @s[tag=cap_zone_bypass] run scoreboard players set @s CAP_ZONE_TMP 1
execute if score @s CAP_ZONE_TMP matches 1 run function capitale:zone/admin/bypass_off_self
execute if score @s CAP_ZONE_TMP matches 0 run function capitale:zone/admin/bypass_on_self
