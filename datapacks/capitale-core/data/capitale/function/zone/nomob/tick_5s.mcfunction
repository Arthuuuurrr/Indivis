# Nettoyage anti-spawn — appelé toutes les 5 secondes depuis core/tick.
# Ne lance le nettoyage d’une zone que si un joueur est assez proche pour charger/jouer la Capitale.
execute as @e[type=marker,tag=cap_nomob_zone,tag=cap_nomob_active] at @s if entity @a[distance=..900] run function capitale:zone/nomob/clean_marker_r800
