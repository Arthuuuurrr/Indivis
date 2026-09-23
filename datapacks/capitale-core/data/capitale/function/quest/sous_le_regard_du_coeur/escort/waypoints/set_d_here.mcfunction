kill @e[type=marker,tag=wp_aurele_coeur_d]
summon marker ~ ~ ~ {Tags:["wp_aurele_coeur_d","wp_aurele_coeur","quest_waypoint"]}
tellraw @s [{"text":"[Escorte Aurèle]","color":"gold"},{"text":" : Point D — pause lore 1 — Cœur défini ici.","color":"white"}]
