scoreboard players add @s CAPSK_FIRST_REGISTRE 0
execute if score @s CAPSK_FIRST_REGISTRE matches 0 run function capskills:reward/daily/first_bonus_self
execute if score @s CAPSK_FIRST_REGISTRE matches 0 run function capitale:reward/xp_vanilla/first_daily/bonus_self
execute if score @s CAPSK_FIRST_REGISTRE matches 0 run scoreboard players set @s CAPSK_FIRST_REGISTRE 1
