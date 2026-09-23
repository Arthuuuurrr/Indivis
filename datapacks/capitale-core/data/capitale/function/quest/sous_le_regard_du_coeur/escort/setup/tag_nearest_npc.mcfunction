tag @e[tag=npc_aurele_veyrane] remove npc_aurele_veyrane
tag @e[type=!minecraft:player,type=!minecraft:armor_stand,type=!minecraft:marker,type=!minecraft:item,sort=nearest,limit=1,distance=..4] add npc_aurele_veyrane
effect give @e[tag=npc_aurele_veyrane,limit=1] minecraft:glowing 10 0 true
tellraw @s [{"text":"[Escorte Aurèle]","color":"gold"},{"text":" : PNJ associé à l’escorte. Aurèle doit briller quelques secondes.","color":"white"}]
