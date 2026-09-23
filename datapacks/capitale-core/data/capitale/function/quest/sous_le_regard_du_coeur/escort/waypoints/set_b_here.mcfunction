kill @e[type=marker,tag=wp_aurele_coeur_b]
summon marker ~ ~ ~ {Tags:["wp_aurele_coeur_b","wp_aurele_coeur","quest_waypoint"]}
tellraw @s [{"text":"[Escorte Aurèle]","color":"gold"},{"text":" : Point B — point B défini ici.","color":"white"}]
