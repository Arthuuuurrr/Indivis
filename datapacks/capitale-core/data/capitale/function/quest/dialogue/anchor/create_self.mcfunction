# Crée un point d'ancrage de conversation à la position du joueur.
# Utilisé par le prologue historique du capitaine ; compatible avec les quêtes récentes QuestChoix.
function capitale:quest/dialogue/anchor/clear_self
execute store result score @s CAP_QDIALOG_TOKEN run random value 1..2147483647
execute at @s run summon marker ~ ~ ~ {Tags:["cap_qdialog_anchor","cap_qdialog_anchor_new"]}
execute at @s run scoreboard players operation @e[type=marker,tag=cap_qdialog_anchor_new,limit=1,sort=nearest,distance=..2] CAP_QDIALOG_TOKEN = @s CAP_QDIALOG_TOKEN
execute at @s run tag @e[type=marker,tag=cap_qdialog_anchor_new,limit=1,sort=nearest,distance=..2] remove cap_qdialog_anchor_new
scoreboard players enable @s QuestChoix
