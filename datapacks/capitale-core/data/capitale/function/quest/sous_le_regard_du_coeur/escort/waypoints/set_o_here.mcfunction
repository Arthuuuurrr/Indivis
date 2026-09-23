kill @e[type=marker,tag=wp_aurele_coeur_o]
summon marker ~ ~ ~ {Tags:["wp_aurele_coeur_o","wp_aurele_coeur","quest_waypoint"]}
tellraw @s [{"text":"[Escorte Aurèle]","color":"gold"},{"text":" : Point O — point O défini ici.","color":"white"}]
