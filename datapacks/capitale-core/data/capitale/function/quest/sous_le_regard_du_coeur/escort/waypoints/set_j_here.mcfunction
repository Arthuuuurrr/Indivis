kill @e[type=marker,tag=wp_aurele_coeur_j]
summon marker ~ ~ ~ {Tags:["wp_aurele_coeur_j","wp_aurele_coeur","quest_waypoint"]}
tellraw @s [{"text":"[Escorte Aurèle]","color":"gold"},{"text":" : Point J — départ vers les Profondeurs défini ici.","color":"white"}]
