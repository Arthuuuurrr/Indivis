function capitale:quest/dialogue/clear_self
scoreboard players add @s QUEST_SIDE_BA_Q04 0
tag @s remove cap_apply_accept
execute if score @s QUEST_SIDE_BA_Q04 matches 0 run tag @s add cap_apply_accept
execute unless entity @s[tag=cap_apply_accept] run tellraw @s {"text":"[Quête] Cette patrouille n’est plus disponible.","color":"red"}
execute if entity @s[tag=cap_apply_accept] run scoreboard players set @s QUEST_SIDE_BA_Q04 20
execute if entity @s[tag=cap_apply_accept] run scoreboard players set @s CAP_QUETEACTIVE 9104
execute if entity @s[tag=cap_apply_accept] run playsound minecraft:block.note_block.pling master @s ~ ~ ~ 0.45 1.15
execute if entity @s[tag=cap_apply_accept] run title @s title {"text":"La Patrouille","color":"gold"}
execute if entity @s[tag=cap_apply_accept] run title @s subtitle {"text":"Accompagner Roland dans sa ronde","color":"yellow"}
execute if entity @s[tag=cap_apply_accept] run tellraw @s [{"text":"[Objectif]","color":"yellow"},{"text":" Inspecter le point de ronde.","color":"white"}]
tag @s remove cap_apply_accept
