tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_moving
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_active
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_returning
scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur] NPC_PATROL_CD 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Escorte Aurèle]","color":"gold"},{"text":" : Escorte stoppée.","color":"white"}]
