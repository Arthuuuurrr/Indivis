kill @e[type=marker,tag=wp_aurele_coeur_t]
summon marker ~ ~ ~ {Tags:["wp_aurele_coeur_t","wp_aurele_coeur","quest_waypoint"]}
tellraw @s [{"text":"[Escorte Aurèle]","color":"gold"},{"text":" : Point T — point T défini ici.","color":"white"}]
