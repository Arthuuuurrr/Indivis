kill @e[type=marker,tag=wp_roch_profondeurs_m]
summon marker ~ ~ ~ {Tags:["wp_roch_profondeurs_m","wp_roch_profondeurs","quest_waypoint"]}
tellraw @s [{"text":"[Escorte Roch]","color":"gold"},{"text":" : Point M — arrivée défini ici.","color":"white"}]
