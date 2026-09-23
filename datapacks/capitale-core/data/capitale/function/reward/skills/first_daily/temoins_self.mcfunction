scoreboard players add @s CAPSK_FIRST_TEMOINS 0
execute if score @s CAPSK_FIRST_TEMOINS matches 0 run function capskills:reward/daily/first_bonus_self
execute if score @s CAPSK_FIRST_TEMOINS matches 0 run function capitale:reward/xp_vanilla/first_daily/bonus_self
execute if score @s CAPSK_FIRST_TEMOINS matches 0 run scoreboard players set @s CAPSK_FIRST_TEMOINS 1
