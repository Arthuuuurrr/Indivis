tag @e[tag=npc_leovic_registre_port] remove npc_leovic_registre_port
tag @e[type=!minecraft:player,type=!minecraft:armor_stand,type=!minecraft:marker,type=!minecraft:item,sort=nearest,limit=1,distance=..4] add npc_leovic_registre_port
effect give @e[tag=npc_leovic_registre_port,limit=1] minecraft:glowing 10 0 true
tellraw @s [{"text":"[Escorte Léovic]","color":"gold"},{"text":" : PNJ associé à l’escorte. Léovic doit briller quelques secondes.","color":"white"}]
