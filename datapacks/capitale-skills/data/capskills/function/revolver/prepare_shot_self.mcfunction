tag @s add capskills.revolver.shooter
tag @s remove capskills.revolver.dual
tag @s remove capskills.revolver.tier_iron
tag @s remove capskills.revolver.tier_gold
tag @s remove capskills.revolver.tier_diamond
tag @s remove capskills.revolver.tier_netherite
execute if score @s CAPREV_STATE matches 3 run tag @s add capskills.revolver.dual
# Le tier est lu sur la main réellement sélectionnée.
execute if score @s CAPREV_SELECTED matches 1 if items entity @s weapon.mainhand capitale_weapons_standalone:iron_revolver run tag @s add capskills.revolver.tier_iron
execute if score @s CAPREV_SELECTED matches 1 if items entity @s weapon.mainhand capitale_weapons_standalone:iron_gambler_revolver run tag @s add capskills.revolver.tier_iron
execute if score @s CAPREV_SELECTED matches 1 if items entity @s weapon.mainhand capitale_weapons_standalone:golden_revolver run tag @s add capskills.revolver.tier_gold
execute if score @s CAPREV_SELECTED matches 1 if items entity @s weapon.mainhand capitale_weapons_standalone:golden_gambler_revolver run tag @s add capskills.revolver.tier_gold
execute if score @s CAPREV_SELECTED matches 1 if items entity @s weapon.mainhand capitale_weapons_standalone:diamond_revolver run tag @s add capskills.revolver.tier_diamond
execute if score @s CAPREV_SELECTED matches 1 if items entity @s weapon.mainhand capitale_weapons_standalone:diamond_gambler_revolver run tag @s add capskills.revolver.tier_diamond
execute if score @s CAPREV_SELECTED matches 1 if items entity @s weapon.mainhand capitale_weapons_standalone:netherite_revolver run tag @s add capskills.revolver.tier_netherite
execute if score @s CAPREV_SELECTED matches 1 if items entity @s weapon.mainhand capitale_weapons_standalone:netherite_gambler_revolver run tag @s add capskills.revolver.tier_netherite
execute if score @s CAPREV_SELECTED matches 2 if items entity @s weapon.offhand capitale_weapons_standalone:iron_revolver run tag @s add capskills.revolver.tier_iron
execute if score @s CAPREV_SELECTED matches 2 if items entity @s weapon.offhand capitale_weapons_standalone:iron_gambler_revolver run tag @s add capskills.revolver.tier_iron
execute if score @s CAPREV_SELECTED matches 2 if items entity @s weapon.offhand capitale_weapons_standalone:golden_revolver run tag @s add capskills.revolver.tier_gold
execute if score @s CAPREV_SELECTED matches 2 if items entity @s weapon.offhand capitale_weapons_standalone:golden_gambler_revolver run tag @s add capskills.revolver.tier_gold
execute if score @s CAPREV_SELECTED matches 2 if items entity @s weapon.offhand capitale_weapons_standalone:diamond_revolver run tag @s add capskills.revolver.tier_diamond
execute if score @s CAPREV_SELECTED matches 2 if items entity @s weapon.offhand capitale_weapons_standalone:diamond_gambler_revolver run tag @s add capskills.revolver.tier_diamond
execute if score @s CAPREV_SELECTED matches 2 if items entity @s weapon.offhand capitale_weapons_standalone:netherite_revolver run tag @s add capskills.revolver.tier_netherite
execute if score @s CAPREV_SELECTED matches 2 if items entity @s weapon.offhand capitale_weapons_standalone:netherite_gambler_revolver run tag @s add capskills.revolver.tier_netherite
# Fallback si un nouveau revolver est ajouté au tag.
execute unless entity @s[tag=capskills.revolver.tier_iron] unless entity @s[tag=capskills.revolver.tier_gold] unless entity @s[tag=capskills.revolver.tier_diamond] unless entity @s[tag=capskills.revolver.tier_netherite] run tag @s add capskills.revolver.tier_iron
scoreboard players set @s CAPREV_COOLDOWN 10
scoreboard players set @s CAPREV_RAY 0
scoreboard players set @s CAPREV_HIT 0
# Portée par défaut : 18 blocs en double, 28 blocs en simple.
execute if entity @s[tag=capskills.revolver.dual] run scoreboard players set @s CAPREV_RANGE 72
execute unless entity @s[tag=capskills.revolver.dual] run scoreboard players set @s CAPREV_RANGE 112
# Maîtrises I–II : 20 blocs en double, 30 blocs en simple.
execute if entity @s[tag=capskills.firearm.mastery.r1,tag=capskills.revolver.dual] run scoreboard players set @s CAPREV_RANGE 80
execute if entity @s[tag=capskills.firearm.mastery.r1,tag=!capskills.revolver.dual] run scoreboard players set @s CAPREV_RANGE 120
execute if entity @s[tag=capskills.firearm.mastery.r2,tag=capskills.revolver.dual] run scoreboard players set @s CAPREV_RANGE 80
execute if entity @s[tag=capskills.firearm.mastery.r2,tag=!capskills.revolver.dual] run scoreboard players set @s CAPREV_RANGE 120
execute if entity @s[tag=capskills.revolver.dual,tag=capskills.firearm.mastery.r2] run function capskills:revolver/evaluate_stability_self
execute store result score @s CAPREV_SPREAD run random value 0..8
playsound minecraft:item.crossbow.shoot player @a[distance=..40] ~ ~ ~ 1 0.6
particle minecraft:flash{color:[1.0,0.72,0.22,1.0]} ^ ^1.45 ^0.8 0 0 0 0 1 force
execute if entity @s[tag=capskills.revolver.dual,tag=capskills.firearm.mastery.r2,tag=capskills.revolver.steady] run function capskills:revolver/ray/start_dual_mastery2_self
execute if entity @s[tag=capskills.revolver.dual,tag=capskills.firearm.mastery.r2,tag=!capskills.revolver.steady] run function capskills:revolver/ray/start_dual_mastery_self
execute if entity @s[tag=capskills.revolver.dual,tag=capskills.firearm.mastery.r1,tag=!capskills.firearm.mastery.r2] run function capskills:revolver/ray/start_dual_mastery_self
execute if entity @s[tag=capskills.revolver.dual,tag=!capskills.firearm.mastery.r1,tag=!capskills.firearm.mastery.r2] run function capskills:revolver/ray/start_dual_self
execute unless entity @s[tag=capskills.revolver.dual] run function capskills:revolver/ray/start_single_self
function capskills:revolver/show_ammo_self
tag @s remove capskills.revolver.shooter
tag @s remove capskills.revolver.dual
tag @s remove capskills.revolver.tier_iron
tag @s remove capskills.revolver.tier_gold
tag @s remove capskills.revolver.tier_diamond
tag @s remove capskills.revolver.tier_netherite
tag @s remove capskills.revolver.steady
