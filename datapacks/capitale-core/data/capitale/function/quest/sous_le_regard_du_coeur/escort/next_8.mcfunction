# I — arrivée au Cercle marchand pour la branche de visite. Stop complet : aucune poursuite automatique vers l’ascenseur.
execute positioned as @e[tag=npc_aurele_veyrane,limit=1] if entity @a[scores={QUEST_GARDECOEUR=20},distance=..24] as @a[scores={QUEST_GARDECOEUR=20},distance=..24,sort=nearest,limit=1] run function capitale:quest/sous_le_regard_du_coeur/aurele/arrive_commerces_self
execute positioned as @e[tag=npc_aurele_veyrane,limit=1] if entity @a[scores={QUEST_GARDECOEUR=20},distance=..24] run tag @s remove escort_active
execute positioned as @e[tag=npc_aurele_veyrane,limit=1] if entity @a[scores={QUEST_GARDECOEUR=20},distance=..24] run tag @s remove escort_moving
execute positioned as @e[tag=npc_aurele_veyrane,limit=1] if entity @a[scores={QUEST_GARDECOEUR=20},distance=..24] run scoreboard players set @s NPC_PATROL_CD 999
execute positioned as @e[tag=npc_aurele_veyrane,limit=1] if entity @a[scores={QUEST_GARDECOEUR=20},distance=..24] run scoreboard players set @s NPC_PATROL_STATE 8
# La branche vers les Profondeurs ne se lance que si le joueur a reparlé à Aurèle au point A et choisi cette suite.
execute positioned as @e[tag=npc_aurele_veyrane,limit=1] if entity @a[scores={QUEST_GARDECOEUR=40},distance=..24] if entity @e[type=marker,tag=wp_aurele_coeur_j,limit=1] run tag @s add escort_moving
execute positioned as @e[tag=npc_aurele_veyrane,limit=1] if entity @a[scores={QUEST_GARDECOEUR=40},distance=..24] if entity @e[type=marker,tag=wp_aurele_coeur_j,limit=1] run scoreboard players set @s NPC_PATROL_STATE 9
execute positioned as @e[tag=npc_aurele_veyrane,limit=1] unless entity @a[scores={QUEST_GARDECOEUR=20..40},distance=..24] run function capitale:quest/sous_le_regard_du_coeur/escort/wait_if_far_self
