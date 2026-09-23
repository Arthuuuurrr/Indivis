kill @e[type=marker,tag=wp_aurele_coeur_q]
summon marker ~ ~ ~ {Tags:["wp_aurele_coeur_q","wp_aurele_coeur","quest_waypoint"]}
tellraw @s [{"text":"[Escorte Aurèle]","color":"gold"},{"text":" : Point Q — point Q défini ici.","color":"white"}]
