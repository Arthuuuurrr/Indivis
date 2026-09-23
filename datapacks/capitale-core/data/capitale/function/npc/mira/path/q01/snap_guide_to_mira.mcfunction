# Recale le guide Mira Q01 sur le PNJ Mira tagué. Utile si le guide a été lancé depuis HOME mais que Mira n'est pas assez proche pour démarrer.
execute if entity @e[type=armor_stand,tag=guide_mira_q01,limit=1] if entity @e[tag=npc_mira_q01,limit=1] run tp @e[type=armor_stand,tag=guide_mira_q01,limit=1] @e[tag=npc_mira_q01,sort=nearest,limit=1]
