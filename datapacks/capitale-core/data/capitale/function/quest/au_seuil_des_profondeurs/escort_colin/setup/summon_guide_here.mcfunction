# UUID Easy NPC : b5550000-0000-4005-8000-000000000701
kill @e[type=armor_stand,tag=guide_colin_profondeurs]
summon armor_stand ~ ~ ~ {UUID:[I;-1252720640,16389,-2147483648,1793],Tags:["guide_colin_profondeurs","quest_guide"],Invisible:1b,NoGravity:1b,Invulnerable:1b,Silent:1b,Marker:1b,CustomName:'"Guide Escorte Colin"',CustomNameVisible:0b}
scoreboard players add @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] NPC_PATROL_STATE 0
scoreboard players add @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] NPC_PATROL_CD 0
scoreboard players set @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] NPC_PATROL_STATE 0
scoreboard players set @e[type=armor_stand,tag=guide_colin_profondeurs,limit=1] NPC_PATROL_CD 0
tellraw @s [{"text":"[Escorte Colin]","color":"gold"},{"text":" : Guide créé. UUID EasyNPC :","color":"white"},{"text":"b5550000-0000-4005-8000-000000000701","color":"aqua"}]
