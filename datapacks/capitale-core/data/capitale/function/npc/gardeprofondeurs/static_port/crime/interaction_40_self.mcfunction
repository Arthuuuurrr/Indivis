function capitale:logic/classify/gardeprofondeurs_self
execute if score @s CAP_RANGSOCIAL matches 90.. run function capitale:npc/gardeprofondeurs/static_port/crime/interaction_40_highrank_self
execute unless score @s CAP_RANGSOCIAL matches 90.. if score @s CAP_REPCLASSE matches 0..10 run function capitale:npc/gardeprofondeurs/static_port/crime/interaction_40_hostile_self
execute unless score @s CAP_RANGSOCIAL matches 90.. unless score @s CAP_REPCLASSE matches 0..10 run function capitale:npc/gardeprofondeurs/static_port/crime/interaction_40_default_self
