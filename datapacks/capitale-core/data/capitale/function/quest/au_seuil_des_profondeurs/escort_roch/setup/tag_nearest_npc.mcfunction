tag @e[tag=npc_roch_vallet] remove npc_roch_vallet
tag @e[type=!minecraft:player,type=!minecraft:armor_stand,type=!minecraft:marker,type=!minecraft:item,sort=nearest,limit=1,distance=..4] add npc_roch_vallet
effect give @e[tag=npc_roch_vallet,limit=1] minecraft:glowing 10 0 true
tellraw @s [{"text":"[Escorte Roch]","color":"gold"},{"text":" : PNJ associé à l’escorte. Il doit briller quelques secondes.","color":"white"}]
