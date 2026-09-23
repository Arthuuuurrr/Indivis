execute as @e[type=armor_stand,tag=patrol_guide] run data merge entity @s {Invisible:0b,Glowing:1b,CustomNameVisible:1b}
tellraw @s {"text":"[Patrouilles] Guides rendus visibles.","color":"yellow"}
