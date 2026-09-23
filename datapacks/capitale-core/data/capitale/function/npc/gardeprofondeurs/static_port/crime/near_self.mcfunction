function capitale:logic/classify/gardeprofondeurs_self
execute if score @s CAP_CRIME matches 10..19 run function capitale:npc/gardeprofondeurs/static_port/crime/near_10_self
execute if score @s CAP_CRIME matches 20..29 run function capitale:npc/gardeprofondeurs/static_port/crime/near_20_self
execute if score @s CAP_CRIME matches 30..39 run function capitale:npc/gardeprofondeurs/static_port/crime/near_30_self
execute if score @s CAP_CRIME matches 40..49 run function capitale:npc/gardeprofondeurs/static_port/crime/near_40_self
execute if score @s CAP_CRIME matches 50.. run function capitale:npc/gardeprofondeurs/static_port/crime/near_50_self
