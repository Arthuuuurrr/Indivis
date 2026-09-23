# Le clic gauche a été capturé ; on refait un raycast serveur depuis les yeux de l'attaquant.
scoreboard players set @s CAP_FIRE_RAY 0
execute at @s anchored eyes positioned ^ ^ ^0.25 run function capitale:adventure/fire_extinguish/bare_hand_hit_step
