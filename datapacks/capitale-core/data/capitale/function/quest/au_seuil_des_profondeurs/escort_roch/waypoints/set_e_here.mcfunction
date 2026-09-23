kill @e[type=marker,tag=wp_roch_profondeurs_e]
summon marker ~ ~ ~ {Tags:["wp_roch_profondeurs_e","wp_roch_profondeurs","quest_waypoint"]}
tellraw @s [{"text":"[Escorte Roch]","color":"gold"},{"text":" : Point E — point E défini ici.","color":"white"}]
