# Contexte : exécuté at marker ou at joueur. Rayon fixe 800 blocs.
# Sécurité : seuls les hostiles vanilla listés dans #capitale:hostiles_natural sont visés.
# Les mobs de quête/boss/décor protégés par tag sont ignorés.
# Le mob est téléporté très bas, hors champ : il mourra naturellement dans le vide.
# Pas de suppression d'items au sol ; aucun loot de joueur n'est ciblé.
execute if entity @e[type=#capitale:hostiles_natural,tag=!cap_keep_mob,tag=!cap_quest_mob,tag=!cap_boss,tag=!cap_no_clean,distance=..800,limit=1] as @e[type=#capitale:hostiles_natural,tag=!cap_keep_mob,tag=!cap_quest_mob,tag=!cap_boss,tag=!cap_no_clean,distance=..800] at @s run tp @s ~ -1000 ~
