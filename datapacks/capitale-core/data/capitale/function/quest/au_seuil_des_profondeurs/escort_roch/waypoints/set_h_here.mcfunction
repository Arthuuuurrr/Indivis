kill @e[type=marker,tag=wp_roch_profondeurs_h]
summon marker ~ ~ ~ {Tags:["wp_roch_profondeurs_h","wp_roch_profondeurs","quest_waypoint"]}
tellraw @s [{"text":"[Escorte Roch]","color":"gold"},{"text":" : Point H — pause ou événement défini ici.","color":"white"}]
