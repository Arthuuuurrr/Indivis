# Déclencheur RP de Q01 : vol / bousculade / fuite.
# À appeler depuis le Near Distance de Mira, exécuté comme le joueur.
function capitale:player/ensure_runtime_self
scoreboard players add @s QUEST_SIDE_BA_Q01 0
scoreboard players add @s QUEST_SIDE_BA_Q03 0
# Ne pas déclencher Q01 pendant une livraison active de Lysandre : cela changerait la branche finale de Q03.
execute unless score @s QUEST_SIDE_BA_Q03 matches 20..99 at @s if score @s QUEST_SIDE_BA_Q01 matches 0 if entity @e[type=armor_stand,tag=guide_mira_q01,limit=1] if entity @e[tag=npc_mira_q01,distance=..10,limit=1] unless entity @e[type=armor_stand,tag=guide_mira_q01,tag=patrol_active,limit=1] run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/near_trigger_apply_self
# Si la scène est déjà active pour un autre joueur, ce joueur rejoint l'étape marchand sans relancer Mira.
execute unless score @s QUEST_SIDE_BA_Q03 matches 20..99 at @s if score @s QUEST_SIDE_BA_Q01 matches 0 if entity @e[type=armor_stand,tag=guide_mira_q01,tag=patrol_active,limit=1] run function capitale:quest/side/bas_anneaux/q01_quartier_oublies/near_trigger_join_self
