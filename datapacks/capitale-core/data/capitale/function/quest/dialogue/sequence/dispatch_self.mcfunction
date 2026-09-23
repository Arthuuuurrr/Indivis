# Dispatcher de séquences dialogue/quête avec verrou anti-cascade.
# RC9ae : séquences prologue dirigeable + Léovic/Aurèle/Roch restaurées.
scoreboard players set @s CAP_FLAG 0
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 101 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 101 run function capitale:quest/spawn_dirigeable_couronne/sequence/geraud_large_l2_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 102 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 102 run function capitale:quest/spawn_dirigeable_couronne/sequence/geraud_large_l3_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 103 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 103 run function capitale:quest/spawn_dirigeable_couronne/sequence/geraud_large_objectif_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 201 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 201 run function capitale:quest/spawn_dirigeable_couronne/sequence/capitaine_intro_l2_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 202 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 202 run function capitale:quest/spawn_dirigeable_couronne/sequence/capitaine_intro_l3_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 203 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 203 run function capitale:quest/spawn_dirigeable_couronne/sequence/capitaine_intro_choix_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 204 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 204 run function capitale:quest/spawn_dirigeable_couronne/sequence/capitaine_near_intro_l1_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 211 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 211 run function capitale:quest/spawn_dirigeable_couronne/sequence/capitaine_decouverte_l2_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 212 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 212 run function capitale:quest/spawn_dirigeable_couronne/sequence/capitaine_reafficher_choix_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 221 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 221 run function capitale:quest/spawn_dirigeable_couronne/sequence/capitaine_mission_l2_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 222 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 222 run function capitale:quest/spawn_dirigeable_couronne/sequence/capitaine_reafficher_choix_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 231 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 231 run function capitale:quest/spawn_dirigeable_couronne/sequence/capitaine_capitale_l2_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 232 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 232 run function capitale:quest/spawn_dirigeable_couronne/sequence/capitaine_reafficher_choix_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 301 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 301 run function capitale:quest/spawn_dirigeable_couronne/sequence/arrivee_prepare_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 302 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 302 run function capitale:quest/spawn_dirigeable_couronne/sequence/arrivee_tp_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 303 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 303 run function capitale:quest/spawn_dirigeable_couronne/sequence/arrivee_objectif_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 401 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 401 run function capitale:quest/spawn_dirigeable_couronne/sequence/geraud_quai_l2_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 402 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 402 run function capitale:quest/spawn_dirigeable_couronne/sequence/geraud_quai_completion_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 403 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 403 run function capitale:quest/spawn_dirigeable_couronne/sequence/geraud_quai_completion_final_self

# RC9ae — séquences de prologue restaurées depuis 1.5.5 stable
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 501 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 501 run function capitale:quest/le_registre_du_port/sequence/leovic_reafficher_choix_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 602 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 602 run function capitale:quest/le_registre_du_port/sequence/leovic_return_after_completion_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 601 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 601 run function capitale:quest/le_registre_du_port/sequence/leovic_completion_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 611 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 611 run function capitale:quest/le_registre_du_port/sequence/leovic_to_ascenseur_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 701 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 701 run function capitale:quest/sous_le_regard_du_coeur/sequence/aurele_offer_l2_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 711 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 711 run function capitale:quest/sous_le_regard_du_coeur/sequence/aurele_explain_coeur_l2_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 712 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 712 run function capitale:quest/sous_le_regard_du_coeur/sequence/aurele_reafficher_choix_intro_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 722 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 722 run function capitale:quest/sous_le_regard_du_coeur/sequence/aurele_return_after_completion_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 721 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 721 run function capitale:quest/sous_le_regard_du_coeur/sequence/aurele_completion_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 741 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 741 run function capitale:quest/sous_le_regard_du_coeur/sequence/aurele_commerce_return_to_ascenseur_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 731 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 731 run function capitale:quest/sous_le_regard_du_coeur/sequence/aurele_near_intro_l2_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 732 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 732 run function capitale:quest/sous_le_regard_du_coeur/sequence/aurele_near_intro_l3_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 831 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 831 run function capitale:quest/au_seuil_des_profondeurs/sequence/roch_near_intro_l2_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 832 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 832 run function capitale:quest/au_seuil_des_profondeurs/sequence/roch_near_intro_l3_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 502 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 502 run function capitale:quest/le_registre_du_port/sequence/leovic_offer_l2_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 512 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 512 run function capitale:quest/le_registre_du_port/sequence/leovic_accept_l2_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_QSEQ matches 833 run scoreboard players set @s CAP_FLAG 1
execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches 833 run function capitale:quest/au_seuil_des_profondeurs/sequence/roch_offer_l2_self
