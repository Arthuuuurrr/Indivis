# 0.9.0-rc4b — messages de connexion conditionnels.
# Les boutons /trigger n'apparaissent que si la récupération est réellement disponible.
# 0.10.18g — racine de combat toujours débloquée.
# La commande Puffish est forcée et ne dépense aucun point ; le tag sert de marqueur de synchronisation.
puffish_skills category unlock @s capskills:doctrine
puffish_skills skills unlock @s capskills:doctrine doctrine_root
tag @s add capskills.skill.doctrine_root

function capskills:integration/ensure_current_self
execute store result score #now CAPSK_TIME run time query gametime
scoreboard players operation @s CAPSK_ALCH_ELAPSED = #now CAPSK_TIME
scoreboard players operation @s CAPSK_ALCH_ELAPSED -= @s CAPSK_ALCH_LAST
scoreboard players operation @s CAPSK_SHADOW_ELAPSED = #now CAPSK_TIME
scoreboard players operation @s CAPSK_SHADOW_ELAPSED -= @s CAPSK_SHADOW_LAST

# Alchimie : bouton uniquement si jamais récupéré ou si le délai de 20 h serveur est terminé.
execute if entity @s[tag=capskills.alchimiste.kit.r1] unless entity @s[tag=capskills.alch.kit.claimed] run tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"Matériel d’alchimie disponible : ","color":"dark_green"},{"text":"[Récupérer]","color":"green","bold":true,"click_event":{"action":"run_command","command":"/trigger CAPSK_ALCH_CLAIM set 1"},"hover_event":{"action":"show_text","value":{"text":"Récupérer le kit d’alchimie quotidien.","color":"gray"}}}]
execute if entity @s[tag=capskills.alchimiste.kit.r1,tag=capskills.alch.kit.claimed] if score @s CAPSK_ALCH_ELAPSED matches 1440000.. run tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"Matériel d’alchimie disponible : ","color":"dark_green"},{"text":"[Récupérer]","color":"green","bold":true,"click_event":{"action":"run_command","command":"/trigger CAPSK_ALCH_CLAIM set 1"},"hover_event":{"action":"show_text","value":{"text":"Récupérer le kit d’alchimie quotidien.","color":"gray"}}}]

# Cache de l’Ombre : bouton uniquement si jamais récupérée ou si le délai de 20 h serveur est terminé.
execute if entity @s[tag=capskills.ombre.cache.r1] unless entity @s[tag=capskills.shadow.kit.claimed] run tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"Cache de l’Ombre disponible : ","color":"dark_gray"},{"text":"[Récupérer]","color":"gray","bold":true,"click_event":{"action":"run_command","command":"/trigger CAPSK_SHADOW_CLAIM set 1"},"hover_event":{"action":"show_text","value":{"text":"Récupérer la Cache de l’Ombre quotidienne.","color":"gray"}}}]
execute if entity @s[tag=capskills.ombre.cache.r1,tag=capskills.shadow.kit.claimed] if score @s CAPSK_SHADOW_ELAPSED matches 1440000.. run tellraw @s [{"text":"[CapSkills] ","color":"gold"},{"text":"Cache de l’Ombre disponible : ","color":"dark_gray"},{"text":"[Récupérer]","color":"gray","bold":true,"click_event":{"action":"run_command","command":"/trigger CAPSK_SHADOW_CLAIM set 1"},"hover_event":{"action":"show_text","value":{"text":"Récupérer la Cache de l’Ombre quotidienne.","color":"gray"}}}]
