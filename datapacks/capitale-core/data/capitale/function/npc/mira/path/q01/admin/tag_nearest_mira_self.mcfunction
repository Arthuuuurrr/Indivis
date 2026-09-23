# À utiliser debout près de Mira. Cible l’entité non-joueur la plus proche dans un rayon de 4 blocs.
# Si un marker/armor stand est plus proche, utilisez plutôt tag_self_npc depuis une action EasyNPC exécutée par Mira.
tag @e[tag=npc_mira_q01,distance=..24,type=!player] remove npc_mira_q01
tag @e[distance=0.1..4,sort=nearest,limit=1,type=!player,type=!armor_stand,type=!marker] add npc_mira_q01
tellraw @s {"text":"[Mira Q01] Entité non-joueur la plus proche taguée npc_mira_q01. Vérifiez avec Status.","color":"green"}
