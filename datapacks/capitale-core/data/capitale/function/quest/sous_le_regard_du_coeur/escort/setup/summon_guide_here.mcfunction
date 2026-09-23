# UUID Easy NPC : b3330000-0000-4003-8000-000000000501
kill @e[type=armor_stand,tag=guide_aurele_coeur]
summon armor_stand ~ ~ ~ {UUID:[I;-1288503296,16387,-2147483648,1281],Tags:["guide_aurele_coeur","guide_aurele_quest","quest_guide"],Invisible:1b,NoGravity:1b,Invulnerable:1b,Silent:1b,Marker:1b,CustomName:'"Guide Aurèle — Cœur"',CustomNameVisible:0b}
scoreboard players add @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] NPC_PATROL_STATE 0
scoreboard players add @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] NPC_PATROL_CD 0
scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] NPC_PATROL_STATE 0
scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur,limit=1] NPC_PATROL_CD 0
tellraw @s [{"text":"[Escorte Aurèle]","color":"gold"},{"text":" : Guide créé. UUID EasyNPC :","color":"white"},{"text":"b3330000-0000-4003-8000-000000000501","color":"aqua"}]
