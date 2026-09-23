# Dispatch central des choix de quête via /trigger QuestChoix.
# RC9ae : prologue restauré depuis 1.5.5 stable, Bas-Anneaux conservés, vétéran conservé.
function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_FLAG 0

# Prologue historique — Capitaine Althéon Brumeforge — owner 1, choix 1-4
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 1 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 1 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 1 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 1 run function capitale:quest/spawn_dirigeable_couronne/capitaine_large/reponse_decouverte_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 1 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 2 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 1 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 2 run function capitale:quest/spawn_dirigeable_couronne/capitaine_large/reponse_mission_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 1 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 3 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 1 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 3 run function capitale:quest/spawn_dirigeable_couronne/capitaine_large/reponse_capitale_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 1 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 4 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 1 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 4 run function capitale:quest/spawn_dirigeable_couronne/capitaine_large/conclure_et_arriver_self

# Q01 — Mira — owner 20, choix 200-204
execute if score @s CAP_QDIALOG_OWNER matches 20 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 200 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 20 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 200 run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/accept_self
execute if score @s CAP_QDIALOG_OWNER matches 20 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 201 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 20 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 201 run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/ask_context_self
execute if score @s CAP_QDIALOG_OWNER matches 20 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 202 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 20 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 202 run function capitale:quest/dialogue/clear_self
execute if score @s CAP_QDIALOG_OWNER matches 20 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 203 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 20 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 203 run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/choice_help_mira_self
execute if score @s CAP_QDIALOG_OWNER matches 20 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 204 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 20 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 204 run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/choice_denounce_mira_self
# Q02 — Elias — owner 21, choix 210-214
execute if score @s CAP_QDIALOG_OWNER matches 21 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 210 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 21 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 210 run function capitale:quest/side/bas_anneaux/q02_marteau_elias/accept_self
execute if score @s CAP_QDIALOG_OWNER matches 21 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 211 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 21 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 211 run function capitale:quest/side/bas_anneaux/q02_marteau_elias/ask_context_self
execute if score @s CAP_QDIALOG_OWNER matches 21 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 212 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 21 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 212 run function capitale:quest/dialogue/clear_self
execute if score @s CAP_QDIALOG_OWNER matches 21 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 213 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 21 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 213 run function capitale:quest/side/bas_anneaux/q02_marteau_elias/complete_self
# Q03 — Lysandre — owner 22, choix 220-225
execute if score @s CAP_QDIALOG_OWNER matches 22 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 220 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 22 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 220 run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/accept_self
execute if score @s CAP_QDIALOG_OWNER matches 22 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 221 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 22 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 221 run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/ask_context_self
execute if score @s CAP_QDIALOG_OWNER matches 22 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 222 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 22 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 222 run function capitale:quest/dialogue/clear_self
execute if score @s CAP_QDIALOG_OWNER matches 22 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 225 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 22 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 225 run function capitale:quest/side/bas_anneaux/q03_marche_mille_voix/complete_self
# Q04 — Roland — owner 23, choix 230-235
execute if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 230 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 230 run function capitale:quest/side/bas_anneaux/q04_patrouille/accept_self
execute if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 231 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 231 run function capitale:quest/side/bas_anneaux/q04_patrouille/ask_context_self
execute if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 232 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 232 run function capitale:quest/dialogue/clear_self
execute if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 233 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 233 run function capitale:quest/side/bas_anneaux/q04_patrouille/choice_arrest_self
execute if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 234 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 234 run function capitale:quest/side/bas_anneaux/q04_patrouille/choice_warn_self
execute if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 235 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 235 run function capitale:quest/side/bas_anneaux/q04_patrouille/choice_balanced_self

# Prologue complet restauré — owners 2 à 7 depuis 1.5.5 stable
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 2 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 10 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 2 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 10 run function capitale:quest/le_registre_du_port/leovic/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 2 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 11 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 2 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 11 run function capitale:quest/le_registre_du_port/leovic/explain_registration_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 2 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 12 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 2 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 12 run function capitale:quest/le_registre_du_port/leovic/decline_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 3 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 30 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 3 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 30 run function capitale:quest/sous_le_regard_du_coeur/aurele/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 3 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 31 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 3 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 31 run function capitale:quest/sous_le_regard_du_coeur/aurele/explain_coeur_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 3 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 32 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 3 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 32 run function capitale:quest/sous_le_regard_du_coeur/aurele/decline_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 3 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 33 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 3 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 33 run function capitale:quest/sous_le_regard_du_coeur/aurele/lead_to_profondeurs_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 3 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 40 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 3 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 40 run function capitale:quest/sous_le_regard_du_coeur/aurele/continue_visit_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 3 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 41 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 3 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 41 run function capitale:quest/sous_le_regard_du_coeur/aurele/lead_to_profondeurs_self

# Au seuil des Profondeurs — Roch Vallet
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 4 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 50 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 4 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 50 run function capitale:quest/au_seuil_des_profondeurs/roch/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 4 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 51 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 4 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 51 run function capitale:quest/au_seuil_des_profondeurs/roch/explain_profondeurs_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 4 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 52 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 4 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 52 run function capitale:quest/au_seuil_des_profondeurs/roch/decline_self
# Caisse de Roch
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 4 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 55 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 4 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 55 run function capitale:quest/au_seuil_des_profondeurs/roch/crate_accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 4 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 56 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 4 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 56 run function capitale:quest/au_seuil_des_profondeurs/roch/crate_decline_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 4 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 57 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 4 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 57 run function capitale:quest/au_seuil_des_profondeurs/roch/crate_decline_offensive_self
# Colin Férand — départ vers auberge
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 5 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 60 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 5 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 60 run function capitale:quest/au_seuil_des_profondeurs/colin/lead_to_auberge_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 5 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 61 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 5 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 61 run function capitale:quest/au_seuil_des_profondeurs/colin/later_self
# Une adresse dans les Profondeurs — Magistrat
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 6 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 70 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 6 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 70 run function capitale:quest/une_adresse_dans_les_profondeurs/magistrat/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 6 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 71 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 6 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 71 run function capitale:quest/une_adresse_dans_les_profondeurs/magistrat/explain_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 6 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 72 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 6 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 72 run function capitale:quest/une_adresse_dans_les_profondeurs/magistrat/decline_self
# Aubergiste — louer une chambre
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 7 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 80 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 7 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 80 run function capitale:quest/une_adresse_dans_les_profondeurs/aubergiste/rent_room_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 7 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 81 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 7 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 81 run function capitale:quest/une_adresse_dans_les_profondeurs/aubergiste/later_self
# Magistrat — payer l'enregistrement résident
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 6 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 90 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 6 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 90 run function capitale:quest/une_adresse_dans_les_profondeurs/magistrat/pay_residence_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 6 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 91 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 6 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 91 run function capitale:quest/une_adresse_dans_les_profondeurs/magistrat/later_payment_self

# Journalière — Une bière pour la relève — vétéran de la Garde du Cœur

# RC9ad — Journalière bière vétéran owner 8
execute if score @s CAP_QDIALOG_OWNER matches 8 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 100 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 8 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 100 run function capitale:quest/journalieres/biere_veteran/accept_self
execute if score @s CAP_QDIALOG_OWNER matches 8 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 101 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 8 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 101 run function capitale:quest/journalieres/biere_veteran/decline_self
execute if score @s CAP_QDIALOG_OWNER matches 8 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 102 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 8 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 102 run function capitale:quest/journalieres/biere_veteran/story_listen_self
execute if score @s CAP_QDIALOG_OWNER matches 8 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 103 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 8 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 103 run function capitale:quest/journalieres/biere_veteran/story_decline_self
execute if score @s CAP_QDIALOG_OWNER matches 8 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 104 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 8 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 104 run function capitale:quest/journalieres/biere_veteran/story_already_told_self
execute if score @s CAP_QDIALOG_OWNER matches 8 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 105 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 8 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 105 run function capitale:quest/journalieres/biere_veteran/attempt_complete_self
execute if score @s CAP_QDIALOG_OWNER matches 8 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 106 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QDIALOG_OWNER matches 8 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 106 run function capitale:quest/journalieres/biere_veteran/not_yet_self

# RC9al — restauration des dispatchs des quêtes secondaires/journalières héritées.
# Owners 10–25 repris du socle stable pour les quêtes divers, journalières, Vent Noir et citoyenneté.
# Les owners 20–23 coexistent avec les Bas-Anneaux car les plages de QuestChoix sont distinctes.

# Libraire — menu boutique / commission noble — owner 10
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 10 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 120 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 10 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 120 run function capitale:npc/libraire/open_shop_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 10 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 121 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 10 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 121 run function capitale:npc/libraire/work_offer_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 10 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 122 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 10 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 122 run function capitale:npc/libraire/leave_self

# Vent Noir I — owner 11
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 11 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 140 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 11 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 140 run function capitale:quest/vent_noir/q1_capitaine_sans_navire/maelor/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 11 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 141 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 11 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 141 run function capitale:quest/vent_noir/q1_capitaine_sans_navire/maelor/explain_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 11 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 142 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 11 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 142 run function capitale:quest/vent_noir/q1_capitaine_sans_navire/maelor/decline_self

# Libraire — exemplaire noble — owner 12
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 12 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 150 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 12 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 150 run function capitale:quest/divers/exemplaire_noble/libraire/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 12 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 151 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 12 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 151 run function capitale:quest/divers/exemplaire_noble/libraire/explain_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 12 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 152 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 12 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 152 run function capitale:quest/divers/exemplaire_noble/libraire/decline_self

# Journalière — registre du Port — owner 13
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 13 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 160 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 13 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 160 run function capitale:quest/journalieres/registre_port/commis/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 13 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 161 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 13 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 161 run function capitale:quest/journalieres/registre_port/commis/explain_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 13 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 162 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 13 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 162 run function capitale:quest/journalieres/registre_port/commis/decline_self

# Vent Noir II — owner 14
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 14 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 170 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 14 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 170 run function capitale:quest/vent_noir/q2_ombres_des_docks/maelor/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 14 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 171 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 14 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 171 run function capitale:quest/vent_noir/q2_ombres_des_docks/maelor/explain_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 14 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 172 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 14 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 172 run function capitale:quest/vent_noir/q2_ombres_des_docks/maelor/decline_self

# Vent Noir III — owner 15
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 15 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 180 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 15 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 180 run function capitale:quest/vent_noir/q3_ascension/maelor/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 15 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 181 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 15 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 181 run function capitale:quest/vent_noir/q3_ascension/maelor/explain_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 15 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 182 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 15 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 182 run function capitale:quest/vent_noir/q3_ascension/maelor/decline_self

# Vent Noir IV — owner 16
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 16 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 190 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 16 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 190 run function capitale:quest/vent_noir/q4_le_vent_noir/maelor/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 16 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 191 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 16 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 191 run function capitale:quest/vent_noir/q4_le_vent_noir/maelor/explain_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 16 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 192 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 16 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 192 run function capitale:quest/vent_noir/q4_le_vent_noir/maelor/decline_self

# Divers — Relais du Cœur — owner 17
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 17 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 200 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 17 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 200 run function capitale:quest/divers/relais_coeur/technicien/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 17 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 201 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 17 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 201 run function capitale:quest/divers/relais_coeur/technicien/explain_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 17 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 202 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 17 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 202 run function capitale:quest/divers/relais_coeur/technicien/decline_self

# Divers — Acte de dette — Odon owner 18 / Lucain owner 19
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 18 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 210 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 18 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 210 run function capitale:quest/divers/acte_dette/odon/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 18 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 211 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 18 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 211 run function capitale:quest/divers/acte_dette/odon/explain_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 18 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 212 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 18 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 212 run function capitale:quest/divers/acte_dette/odon/decline_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 19 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 220 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 19 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 220 run function capitale:quest/divers/acte_dette/lucain/return_owner_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 19 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 221 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 19 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 221 run function capitale:quest/divers/acte_dette/lucain/return_later_self

# Citoyenneté — owner 20, plage 230–231
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 20 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 230 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 20 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 230 run function capitale:quest/principal/citoyennete/magistrat/pay_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 20 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 231 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 20 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 231 run function capitale:quest/principal/citoyennete/magistrat/later_self

# Journalière — Ressort du clocher — owner 21, plage 240–242
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 21 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 240 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 21 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 240 run function capitale:quest/journalieres/ressort_clocher/donneur/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 21 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 241 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 21 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 241 run function capitale:quest/journalieres/ressort_clocher/donneur/explain_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 21 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 242 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 21 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 242 run function capitale:quest/journalieres/ressort_clocher/donneur/decline_self

# Journalière — Témoignages des quais — owner 22/23, plages 250–252 et 260–261
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 22 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 250 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 22 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 250 run function capitale:quest/journalieres/temoignages_quais/greffier/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 22 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 251 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 22 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 251 run function capitale:quest/journalieres/temoignages_quais/greffier/explain_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 22 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 252 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 22 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 252 run function capitale:quest/journalieres/temoignages_quais/greffier/decline_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 260 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 260 run function capitale:quest/journalieres/temoignages_quais/greffier/complete_partial_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 261 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 23 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 261 run function capitale:quest/journalieres/temoignages_quais/greffier/wait_last_self

# Journalière — Banque — owner 24
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 24 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 270 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 24 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 270 run function capitale:quest/journalieres/banque/employe/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 24 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 271 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 24 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 271 run function capitale:quest/journalieres/banque/employe/explain_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 24 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 272 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 24 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 272 run function capitale:quest/journalieres/banque/employe/decline_self

# Divers — Lampe manquante — owner 25
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 25 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 280 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 25 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 280 run function capitale:quest/divers/lampe_manquante/veilleuse/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 25 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 281 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 25 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 281 run function capitale:quest/divers/lampe_manquante/veilleuse/explain_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 25 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 282 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 25 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 282 run function capitale:quest/divers/lampe_manquante/veilleuse/decline_self

# Aldren le Traqueur - quete "La menace des Orcs" - owner 26
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 26 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 1 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 26 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 1 run function capitale:quest/side/menace_orcs/accept_self
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 26 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 2 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_QSEQ matches 0 if score @s CAP_QDIALOG_OWNER matches 26 if score @s CAP_QDIALOG_KEEP matches 1.. if score @s QuestChoix matches 2 run function capitale:quest/side/menace_orcs/decline_self

# Si aucun contexte valide n'a consommé le choix, prévenir sans effet.
execute if score @s CAP_FLAG matches 0 if score @s QuestChoix matches 1.. run tellraw @s {"text":"[Quête] Ce choix n’est plus lié à un dialogue actif.","color":"gray"}
scoreboard players set @s QuestChoix 0
scoreboard players enable @s QuestChoix
