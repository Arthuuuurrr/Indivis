# Arrivée optionnelle au Cercle marchand : arrêt net, explication, puis retour au point A après une courte pause.
scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_moving
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_active
tag @e[type=armor_stand,tag=guide_aurele_coeur] remove escort_returning
scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur] NPC_PATROL_CD 999
scoreboard players set @e[type=armor_stand,tag=guide_aurele_coeur] NPC_PATROL_STATE 8
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Voici le Cercle marchand. Les contrats, les taxes et les rumeurs y circulent plus vite que les chariots. Regardez bien : c’est ici que la Capitale compte ce qu’elle possède, ce qu’elle doit, et ce qu’elle promet.","color":"white"}]
scoreboard players set @s CAP_QSEQ 741
scoreboard players set @s CAP_QSEQ_TIMER 60
