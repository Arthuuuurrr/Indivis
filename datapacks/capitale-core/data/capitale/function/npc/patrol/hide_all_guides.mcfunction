execute as @e[type=armor_stand,tag=patrol_guide] run data merge entity @s {Invisible:1b,Glowing:0b,CustomNameVisible:0b}
tellraw @s {"text":"[Patrouilles] Guides masqués.","color":"yellow"}
