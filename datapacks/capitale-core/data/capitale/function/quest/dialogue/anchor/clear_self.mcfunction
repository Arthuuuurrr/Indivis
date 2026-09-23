# Supprime l'ancre de conversation liée au joueur courant.
tag @s add cap_qdialog_clear_player
execute as @e[type=marker,tag=cap_qdialog_anchor] if score @s CAP_QDIALOG_TOKEN = @a[tag=cap_qdialog_clear_player,limit=1] CAP_QDIALOG_TOKEN run kill @s
tag @s remove cap_qdialog_clear_player
scoreboard players set @s CAP_QDIALOG_TOKEN 0
