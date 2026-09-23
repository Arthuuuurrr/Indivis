kill @e[type=marker,tag=wp_aurele_coeur_l]
summon marker ~ ~ ~ {Tags:["wp_aurele_coeur_l","wp_aurele_coeur","quest_waypoint"]}
tellraw @s [{"text":"[Escorte Aurèle]","color":"gold"},{"text":" : Point L — point L défini ici.","color":"white"}]
