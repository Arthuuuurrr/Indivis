# Démarre la route Mira Q01 depuis la position actuelle de Mira, pas seulement depuis HOME.
# À utiliser en test admin ou via near-distance si Mira ne part pas.
execute unless entity @e[type=armor_stand,tag=guide_mira_q01,limit=1] run tellraw @s {"text":"[Mira Q01] Guide absent. Créez-le avec le menu de route.","color":"red"}
execute unless entity @e[tag=npc_mira_q01,limit=1] run tellraw @s {"text":"[Mira Q01] Aucun PNJ Mira tagué npc_mira_q01.","color":"red"}
execute if entity @e[type=armor_stand,tag=guide_mira_q01,limit=1] if entity @e[tag=npc_mira_q01,limit=1] run tp @e[type=armor_stand,tag=guide_mira_q01,limit=1] @e[tag=npc_mira_q01,sort=nearest,limit=1]
execute if entity @e[type=armor_stand,tag=guide_mira_q01,limit=1] if entity @e[tag=npc_mira_q01,limit=1] run function capitale:npc/mira/path/q01/start
execute if entity @e[type=armor_stand,tag=guide_mira_q01,limit=1] if entity @e[tag=npc_mira_q01,limit=1] run tellraw @s {"text":"[Mira Q01] Route démarrée depuis la position actuelle de Mira. Si elle ne bouge pas, vérifiez le Follow Entity EasyNPC vers guide_mira_q01.","color":"yellow"}
