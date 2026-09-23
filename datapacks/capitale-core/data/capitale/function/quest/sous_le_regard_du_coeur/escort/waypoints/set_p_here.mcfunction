kill @e[type=marker,tag=wp_aurele_coeur_p]
summon marker ~ ~ ~ {Tags:["wp_aurele_coeur_p","wp_aurele_coeur","quest_waypoint"]}
tellraw @s [{"text":"[Escorte Aurèle]","color":"gold"},{"text":" : Point P — point P défini ici.","color":"white"}]
