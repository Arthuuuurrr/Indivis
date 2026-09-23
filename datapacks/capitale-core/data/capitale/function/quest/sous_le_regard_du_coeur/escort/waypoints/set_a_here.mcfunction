kill @e[type=marker,tag=wp_aurele_coeur_a]
summon marker ~ ~ ~ {Tags:["wp_aurele_coeur_a","wp_aurele_coeur","quest_waypoint"]}
tellraw @s [{"text":"[Escorte Aurèle]","color":"gold"},{"text":" : Point A — départ d’Aurèle défini ici.","color":"white"}]
