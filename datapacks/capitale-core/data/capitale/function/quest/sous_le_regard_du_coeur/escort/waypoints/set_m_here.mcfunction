kill @e[type=marker,tag=wp_aurele_coeur_m]
summon marker ~ ~ ~ {Tags:["wp_aurele_coeur_m","wp_aurele_coeur","quest_waypoint"]}
tellraw @s [{"text":"[Escorte Aurèle]","color":"gold"},{"text":" : Point M — point M défini ici.","color":"white"}]
