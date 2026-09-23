# 1) Traite les clics gauche enregistrés sur les détecteurs du tick précédent.
execute as @e[type=minecraft:interaction,tag=cap_fire_bare_detector] at @s on attacker if entity @s[gamemode=adventure] unless items entity @s weapon.mainhand * run function capitale:adventure/fire_extinguish/bare_hand_hit_start
# 2) Purge les détecteurs éphémères.
kill @e[type=minecraft:interaction,tag=cap_fire_bare_detector]
# 3) Crée un détecteur seulement si un joueur Aventure à main nue vise réellement un feu accessible.
execute as @a[gamemode=adventure] at @s unless items entity @s weapon.mainhand * run function capitale:adventure/fire_extinguish/bare_hand_scan_start
