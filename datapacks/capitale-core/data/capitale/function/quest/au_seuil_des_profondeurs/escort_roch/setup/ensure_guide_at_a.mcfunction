# Création automatique du guide de Roch au point A s'il est absent.
# Le guide est volontairement temporaire : il n'existe pas au repos, afin d'éviter
# que Roch tente de coller à une cible immobile hors escorte.
execute unless entity @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] at @e[type=marker,tag=wp_roch_profondeurs_a,limit=1] run summon armor_stand ~ ~ ~ {UUID:[I;-1270611968,16388,-2147483648,1537],Tags:["guide_roch_profondeurs","quest_guide"],Invisible:1b,NoGravity:1b,Invulnerable:1b,Silent:1b,Marker:1b,CustomName:'"Guide Escorte Roch"',CustomNameVisible:0b}
scoreboard players add @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] NPC_PATROL_STATE 0
scoreboard players add @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] NPC_PATROL_CD 0
