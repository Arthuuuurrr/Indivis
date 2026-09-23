function capitale:logic/classify/gardeprofondeurs_self
execute if score @s CAP_RANGSOCIAL matches 90.. run function capitale:npc/gardeprofondeurs/static_port/crime/near_50_highrank_self
execute unless score @s CAP_RANGSOCIAL matches 90.. if score @s CAP_REPCLASSE matches 0..10 run function capitale:npc/gardeprofondeurs/static_port/crime/near_50_hostile_self
execute unless score @s CAP_RANGSOCIAL matches 90.. unless score @s CAP_REPCLASSE matches 0..10 run function capitale:npc/gardeprofondeurs/static_port/crime/near_50_default_self
