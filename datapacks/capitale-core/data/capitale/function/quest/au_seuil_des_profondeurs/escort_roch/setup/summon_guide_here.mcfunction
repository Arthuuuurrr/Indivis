# UUID Easy NPC : b4440000-0000-4004-8000-000000000601
kill @e[type=armor_stand,tag=guide_roch_profondeurs]
summon armor_stand ~ ~ ~ {UUID:[I;-1270611968,16388,-2147483648,1537],Tags:["guide_roch_profondeurs","quest_guide"],Invisible:1b,NoGravity:1b,Invulnerable:1b,Silent:1b,Marker:1b,CustomName:'"Guide Escorte Roch"',CustomNameVisible:0b}
scoreboard players add @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] NPC_PATROL_STATE 0
scoreboard players add @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] NPC_PATROL_CD 0
scoreboard players set @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] NPC_PATROL_STATE 0
scoreboard players set @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] NPC_PATROL_CD 0
execute if entity @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] if entity @e[type=marker,tag=wp_roch_profondeurs_a,limit=1] run tp @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] @e[type=marker,tag=wp_roch_profondeurs_a,limit=1]
tellraw @s [{"text":"[Escorte Roch]","color":"gold"},{"text":" : Guide créé pour l’association EasyNPC. UUID :","color":"white"},{"text":"b4440000-0000-4004-8000-000000000601","color":"aqua"},{"text":" — une fois le PNJ configuré, utilisez [Nettoyer guide au repos] ; le guide sera recréé automatiquement au départ de la quête.","color":"white"}]
