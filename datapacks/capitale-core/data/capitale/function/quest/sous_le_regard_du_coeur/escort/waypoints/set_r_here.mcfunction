kill @e[type=marker,tag=wp_aurele_coeur_r]
summon marker ~ ~ ~ {Tags:["wp_aurele_coeur_r","wp_aurele_coeur","quest_waypoint"]}
tellraw @s [{"text":"[Escorte Aurèle]","color":"gold"},{"text":" : Point R — point R défini ici.","color":"white"}]
