# UUID Easy NPC : b2220000-0000-4002-8000-000000000401
kill @e[type=armor_stand,tag=guide_leovic_registre_port]
summon armor_stand ~ ~ ~ {UUID:[I;-1306394624,16386,-2147483648,1025],Tags:["guide_leovic_registre_port","guide_leovic_quest","quest_guide"],Invisible:1b,NoGravity:1b,Invulnerable:1b,Silent:1b,Marker:1b,CustomName:'"Guide Léovic — Registre du Port"',CustomNameVisible:0b}
scoreboard players add @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] NPC_PATROL_STATE 0
scoreboard players add @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] NPC_PATROL_CD 0
scoreboard players set @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] NPC_PATROL_STATE 0
scoreboard players set @e[type=armor_stand,tag=guide_leovic_registre_port,limit=1] NPC_PATROL_CD 0
tellraw @s [{"text":"[Escorte Léovic]","color":"gold"},{"text":" : Guide créé. UUID EasyNPC :","color":"white"},{"text":"b2220000-0000-4002-8000-000000000401","color":"aqua"}]
