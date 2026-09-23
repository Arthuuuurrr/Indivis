# Fin de retour progressif au point A.
tag @s remove escort_returning
tag @s remove escort_moving
tag @s remove escort_pause_d_done
tag @s remove escort_pause_g_done
tag @s remove escort_pause_n_done
tag @s remove escort_pause_s_done
scoreboard players set @s NPC_PATROL_CD 0
scoreboard players set @s NPC_RETURN_TIMER 0
scoreboard players set @s NPC_PATROL_STATE 0

# Si le retour vient de la visite du Cercle marchand, valider l’étape seulement une fois Aurèle revenue au point A.
execute as @a[scores={QUEST_GARDECOEUR=25}] run function capitale:quest/sous_le_regard_du_coeur/aurele/return_to_a_complete_self
