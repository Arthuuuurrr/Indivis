tag @e[tag=npc_colin_ferand] remove npc_colin_ferand
tag @e[type=!minecraft:player,type=!minecraft:armor_stand,type=!minecraft:marker,type=!minecraft:item,sort=nearest,limit=1,distance=..4] add npc_colin_ferand
effect give @e[tag=npc_colin_ferand,limit=1] minecraft:glowing 10 0 true
tellraw @s [{"text":"[Escorte Colin]","color":"gold"},{"text":" : PNJ associé à l’escorte. Il doit briller quelques secondes.","color":"white"}]
