# 0.8.25 — détection vanilla des minerais minés. Les scores sont remis à zéro même sans perk pour éviter les vieux cumuls.
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_COAL matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/coal_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_COAL matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/coal_self
scoreboard players set @s CAPSK_MINE_COAL 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DCOAL matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/coal_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DCOAL matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/coal_self
scoreboard players set @s CAPSK_MINE_DCOAL 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_COPPER matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/copper_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_COPPER matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/copper_self
scoreboard players set @s CAPSK_MINE_COPPER 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DCOPPER matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/copper_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DCOPPER matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/copper_self
scoreboard players set @s CAPSK_MINE_DCOPPER 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_IRON matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/iron_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_IRON matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/iron_self
scoreboard players set @s CAPSK_MINE_IRON 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DIRON matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/iron_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DIRON matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/iron_self
scoreboard players set @s CAPSK_MINE_DIRON 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_GOLD matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/gold_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_GOLD matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/gold_self
scoreboard players set @s CAPSK_MINE_GOLD 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DGOLD matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/gold_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DGOLD matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/gold_self
scoreboard players set @s CAPSK_MINE_DGOLD 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_REDS matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/redstone_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_REDS matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/redstone_self
scoreboard players set @s CAPSK_MINE_REDS 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DREDS matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/redstone_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DREDS matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/redstone_self
scoreboard players set @s CAPSK_MINE_DREDS 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_LAPIS matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/lapis_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_LAPIS matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/lapis_self
scoreboard players set @s CAPSK_MINE_LAPIS 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DLAPIS matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/lapis_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DLAPIS matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/lapis_self
scoreboard players set @s CAPSK_MINE_DLAPIS 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DIAM matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/diamond_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DIAM matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/diamond_self
scoreboard players set @s CAPSK_MINE_DIAM 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DDIAM matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/diamond_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DDIAM matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/diamond_self
scoreboard players set @s CAPSK_MINE_DDIAM 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_EMER matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/emerald_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_EMER matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/emerald_self
scoreboard players set @s CAPSK_MINE_EMER 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DEMER matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/emerald_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_DEMER matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/emerald_self
scoreboard players set @s CAPSK_MINE_DEMER 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_QUARTZ matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/quartz_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_QUARTZ matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/quartz_self
scoreboard players set @s CAPSK_MINE_QUARTZ 0
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_NGOLD matches 1.. if items entity @s weapon.mainhand #minecraft:pickaxes run function capskills:mechanics/mining/ore/nether_gold_self
execute if entity @s[tag=capskills.mineur.bonus_ores.r1] if score @s CAPSK_MINE_NGOLD matches 1.. if items entity @s weapon.mainhand #minecraft:shovels run function capskills:mechanics/mining/ore/nether_gold_self
scoreboard players set @s CAPSK_MINE_NGOLD 0
