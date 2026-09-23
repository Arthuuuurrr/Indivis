# Vérifie seulement le dialogue ancré historique du capitaine (owner 1).
# Les quêtes plus récentes valident leur contexte par CAP_QDIALOG_OWNER/CAP_QDIALOG_KEEP.
execute if score @s CAP_QDIALOG_OWNER matches 1 run tag @s add cap_qdialog_check_player
execute if score @s CAP_QDIALOG_OWNER matches 1 run scoreboard players set #QDialogMatch CAP_QDIALOG_TOKEN 0
execute if score @s CAP_QDIALOG_OWNER matches 1 at @s as @e[type=marker,tag=cap_qdialog_anchor,distance=..8] if score @s CAP_QDIALOG_TOKEN = @a[tag=cap_qdialog_check_player,limit=1] CAP_QDIALOG_TOKEN run scoreboard players set #QDialogMatch CAP_QDIALOG_TOKEN 1
execute if score @s CAP_QDIALOG_OWNER matches 1 unless score #QDialogMatch CAP_QDIALOG_TOKEN matches 1 run function capitale:quest/dialogue/abort/capitaine_spawn_self
execute if score @s CAP_QDIALOG_OWNER matches 1 run tag @s remove cap_qdialog_check_player
