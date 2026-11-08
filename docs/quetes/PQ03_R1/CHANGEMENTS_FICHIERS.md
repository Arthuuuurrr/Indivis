# PQ03 R1 HF1 — Changements exhaustifs

Comparaison avec les archives initiales HF8/Fusion C1 et le JAR HF9. `hf1_diff_r1.json` isole les différences face à la livraison R1 du 7 novembre. Aucun fichier supprimé.

| Archive | Chemin | Opération | Motif |
|---|---|---|---|
| core | `data/capitale/function/core/load.mcfunction` | modified | Déclaration PQ03 |
| core | `data/capitale/function/core/tick.mcfunction` | modified | Retrait des deux appels du guide mondial Aurèle ; tous les autres appels conservés |
| core | `data/capitale/function/npc/aurele/interact_self.mcfunction` | modified | Contexte exact de chaque joueur ; proximité bornée à trois blocs |
| core | `data/capitale/function/npc/aurele/near_distance_npc.mcfunction` | modified | Contexte exact de chaque joueur ; proximité bornée à trois blocs |
| core | `data/capitale/function/npc/aurele/near_distance_self.mcfunction` | modified | Contexte exact de chaque joueur ; proximité bornée à trois blocs |
| core | `data/capitale/function/npc/aurele/near_npc.mcfunction` | modified | Contexte exact de chaque joueur ; proximité bornée à trois blocs |
| core | `data/capitale/function/npc/aurele/near_self.mcfunction` | modified | Contexte exact de chaque joueur ; proximité bornée à trois blocs |
| core | `data/capitale/function/profiles/after_load_self.mcfunction` | modified | Scène privée suspendue/restaurée pour le seul personnage, checkpoint périodique préservé |
| core | `data/capitale/function/profiles/before_save_self.mcfunction` | modified | Scène privée suspendue/restaurée pour le seul personnage, checkpoint périodique préservé |
| core | `data/capitale/function/quest/guidage/lost/second_self.mcfunction` | modified | Surveillance Aurèle par personnage ; aucun nearest ni réinitialisation après éloignement |
| core | `data/capitale/function/quest/guidage/reset/aurele_self.mcfunction` | modified | Suspension privée sans téléportation ni retour forcé à un ancien état |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/accept_body_154_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/accept_commit_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/accept_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/arrive_commerces_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/arrive_profondeurs_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/complete_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/continue_visit_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/decline_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/explain_coeur_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/interact_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/lead_to_profondeurs_commit_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/lead_to_profondeurs_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/near_intro_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/near_offer_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/offer_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/open_choices_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/open_commerce_choices_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/postquest_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/reoffer_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/return_to_a_complete_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/show_choices_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/aurele/show_commerce_choices_self.mcfunction` | modified | Entrée historique conservée ; scène privée du personnage actif |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/failsafe_to_a.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/failsafe_to_a_admin_self.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/move_tick.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_0.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_1.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_10.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_11.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_12.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_13.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_14.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_15.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_16.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_17.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_18.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_19.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_2.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_20.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_21.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_3.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_4.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_5.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_6.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_7.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_8.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/next_9.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_0.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_1.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_10.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_11.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_12.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_13.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_14.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_15.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_16.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_17.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_18.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_19.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_2.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_20.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_21.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_3.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_4.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_5.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_6.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_7.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_8.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_9.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/return_start.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/setup/kill_guide.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/setup/summon_guide_here.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/setup/tag_nearest_npc.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/setup/uuid_guide.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/start_to_commerces_self.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/start_to_profondeurs_self.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/stop_self.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/tick.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/wait_if_far_self.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/escort/wait_message_self.mcfunction` | modified | Remplacement du guide mondial par le pathfinding privé ; aucun sélecteur de propriétaire nearest |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/sequence/aurele_commerce_return_to_ascenseur_self.mcfunction` | modified | Ancien minuteur neutralisé ; progression par le dialogue privé explicite |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/sequence/aurele_completion_self.mcfunction` | modified | Ancien minuteur neutralisé ; progression par le dialogue privé explicite |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/sequence/aurele_explain_coeur_l2_self.mcfunction` | modified | Ancien minuteur neutralisé ; progression par le dialogue privé explicite |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/sequence/aurele_near_intro_l2_self.mcfunction` | modified | Ancien minuteur neutralisé ; progression par le dialogue privé explicite |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/sequence/aurele_near_intro_l3_self.mcfunction` | modified | Ancien minuteur neutralisé ; progression par le dialogue privé explicite |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/sequence/aurele_offer_l2_self.mcfunction` | modified | Ancien minuteur neutralisé ; progression par le dialogue privé explicite |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/sequence/aurele_reafficher_choix_intro_self.mcfunction` | modified | Ancien minuteur neutralisé ; progression par le dialogue privé explicite |
| core | `data/capitale/function/quest/sous_le_regard_du_coeur/sequence/aurele_return_after_completion_self.mcfunction` | modified | Ancien minuteur neutralisé ; progression par le dialogue privé explicite |
| core | `pack.mcmeta` | modified | Métadonnées de version ou extension PQ03 du JAR |
| core | `data/capitale/function/pq03/admin/afficher_points.mcfunction` | added | Visualisation existante ; vrais repères conservés |
| core | `data/capitale/function/pq03/admin/confirmer_grondement.mcfunction` | added | Administration ponctuelle sans progression ni récompense |
| core | `data/capitale/function/pq03/admin/desactiver_grondement.mcfunction` | added | Administration ponctuelle sans progression ni récompense |
| core | `data/capitale/function/pq03/admin/diagnostic.mcfunction` | added | Administration ponctuelle sans progression ni récompense |
| core | `data/capitale/function/pq03/admin/diagnostic_points.mcfunction` | added | Administration ponctuelle sans progression ni récompense |
| core | `data/capitale/function/pq03/admin/masquer_points.mcfunction` | added | Visualisation existante ; vrais repères conservés |
| core | `data/capitale/function/pq03/admin/nettoyer_scenes.mcfunction` | added | Administration ponctuelle sans progression ni récompense |
| core | `data/capitale/function/pq03/admin/preparer_parcours.mcfunction` | added | Administration ponctuelle sans progression ni récompense |
| core | `data/capitale/function/pq03/complete_once_self.mcfunction` | added | Clôture effective C06 ; réputation déjà appliquée aux choix ; autres récompenses conservées |
| core | `data/capitale/function/pq03/liaison/aurele.mcfunction` | added | Administration ponctuelle sans progression ni récompense |
| core | `data/capitale/function/pq03/load.mcfunction` | added | Scores CAP_ persistants par Nexus ; aucune réinitialisation de joueur |
| core | `data/capitale/function/pq03/open_self.mcfunction` | added | PQ02 doit être terminée ; aucune progression anticipée |
| core | `data/capitale/function/pq03/reputation/instant_ready_self.mcfunction` | added | Contrat HF1 : gains immédiats, sans versement de réputation à la conclusion |
| quests | `data/hc_quetes/hcq_quests/pq_03_sous_le_regard_du_coeur.json` | modified | Deux routes explicites ; récompense exacte ; cible privée ; ID/états/objectifs conservés |
| quests | `pack.mcmeta` | modified | Métadonnées de version ou extension PQ03 du JAR |
| mod | `fabric.mod.json` | modified | Métadonnées de version ou extension PQ03 du JAR |
| mod | `indivis_pq02_graph.json` | modified | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq02ProfileCheckpoint.class` | modified | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq02Runtime.class` | modified | Métadonnées de version ou extension PQ03 du JAR |
| mod | `indivis_pq03.mixins.json` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `indivis_pq03_graph.json` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/pq03/mixin/Pq03DialogueCloseMixin.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/pq03/mixin/Pq03EntityMixin.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/pq03/mixin/Pq03NexusSaveMixin.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/pq03/mixin/Pq03ScreenLifecycleMixin.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/pq03/mixin/Pq03VisibilityMixin.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/pq03/mixin/Pq03WaypointMixin.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Client.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Data$Choice.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Data$Graph.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Data$Node.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Data.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Entries$Pending.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Entries.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Focus$Request.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Focus.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03FocusClient.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Pacing$Frame.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Pacing.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03ProfileCheckpoint$Scope.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03ProfileCheckpoint.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Rules.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Runtime$1.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Runtime$Actor.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Runtime$Near.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Runtime$View.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Runtime.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Setup$Scan.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Setup.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/Pq03Transitions.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/QuestReputationNotice$Notice.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/QuestReputationNotice.class` | added | Métadonnées de version ou extension PQ03 du JAR |
| mod | `net/hautecapitale/quetes/sequence/QuestReputationNoticeClient.class` | added | Métadonnées de version ou extension PQ03 du JAR |
