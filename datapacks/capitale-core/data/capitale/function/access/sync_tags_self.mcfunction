# Synchronise les tags ACCESS_* avec les scores ACCESS_* pour conserver la compatibilité.
execute if entity @s[tag=ACCESS_CAPITALE] if score @s ACCESS_CAPITALE matches 0 run scoreboard players set @s ACCESS_CAPITALE 1
execute if entity @s[tag=ACCESS_QUARTIERS_HAUTS_NORD] if score @s ACCESS_QH_NORD matches 0 run scoreboard players set @s ACCESS_QH_NORD 1
execute if entity @s[tag=ACCESS_QUARTIERS_HAUTS_SUD] if score @s ACCESS_QH_SUD matches 0 run scoreboard players set @s ACCESS_QH_SUD 1
execute if entity @s[tag=ACCESS_PALAIS] if score @s ACCESS_PALAIS matches 0 run scoreboard players set @s ACCESS_PALAIS 1
execute if entity @s[tag=ACCESS_ARCHIVES] if score @s ACCESS_ARCHIVES matches 0 run scoreboard players set @s ACCESS_ARCHIVES 1
execute if entity @s[tag=ACCESS_COEUR] if score @s ACCESS_COEUR matches 0 run scoreboard players set @s ACCESS_COEUR 1
execute if entity @s[tag=ACCESS_SEUILS_SCELLES] if score @s ACCESS_SEUILS matches 0 run scoreboard players set @s ACCESS_SEUILS 1
execute if entity @s[tag=ACCESS_BANQUE] if score @s ACCESS_BANQUE matches 0 run scoreboard players set @s ACCESS_BANQUE 1
execute if entity @s[tag=ACCESS_TRIBUNAL] if score @s ACCESS_TRIBUNAL matches 0 run scoreboard players set @s ACCESS_TRIBUNAL 1
execute if score @s ACCESS_CAPITALE matches 1.. run tag @s add ACCESS_CAPITALE
execute if score @s ACCESS_CAPITALE matches 0 run tag @s remove ACCESS_CAPITALE
execute if score @s ACCESS_QH_NORD matches 1.. run tag @s add ACCESS_QUARTIERS_HAUTS_NORD
execute if score @s ACCESS_QH_NORD matches 0 run tag @s remove ACCESS_QUARTIERS_HAUTS_NORD
execute if score @s ACCESS_QH_SUD matches 1.. run tag @s add ACCESS_QUARTIERS_HAUTS_SUD
execute if score @s ACCESS_QH_SUD matches 0 run tag @s remove ACCESS_QUARTIERS_HAUTS_SUD
execute if score @s ACCESS_PALAIS matches 1.. run tag @s add ACCESS_PALAIS
execute if score @s ACCESS_PALAIS matches 0 run tag @s remove ACCESS_PALAIS
execute if score @s ACCESS_ARCHIVES matches 1.. run tag @s add ACCESS_ARCHIVES
execute if score @s ACCESS_ARCHIVES matches 0 run tag @s remove ACCESS_ARCHIVES
execute if score @s ACCESS_COEUR matches 1.. run tag @s add ACCESS_COEUR
execute if score @s ACCESS_COEUR matches 0 run tag @s remove ACCESS_COEUR
execute if score @s ACCESS_SEUILS matches 1.. run tag @s add ACCESS_SEUILS_SCELLES
execute if score @s ACCESS_SEUILS matches 0 run tag @s remove ACCESS_SEUILS_SCELLES
execute if score @s ACCESS_BANQUE matches 1.. run tag @s add ACCESS_BANQUE
execute if score @s ACCESS_BANQUE matches 0 run tag @s remove ACCESS_BANQUE
execute if score @s ACCESS_TRIBUNAL matches 1.. run tag @s add ACCESS_TRIBUNAL
execute if score @s ACCESS_TRIBUNAL matches 0 run tag @s remove ACCESS_TRIBUNAL
