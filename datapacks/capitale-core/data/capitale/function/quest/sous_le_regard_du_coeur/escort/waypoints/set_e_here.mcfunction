kill @e[type=marker,tag=wp_aurele_coeur_e]
summon marker ~ ~ ~ {Tags:["wp_aurele_coeur_e","wp_aurele_coeur","quest_waypoint"]}
tellraw @s [{"text":"[Escorte Aurèle]","color":"gold"},{"text":" : Point E — point E défini ici.","color":"white"}]
