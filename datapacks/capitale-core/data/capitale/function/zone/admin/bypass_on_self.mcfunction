tag @s add cap_zone_bypass
execute if entity @s[tag=cap_zone_forced_adventure,gamemode=adventure] run function capitale:zone/protection/exit_survival_self
tellraw @s {"text":"[Zones protégées] Bypass activé pour vous.","color":"green"}
