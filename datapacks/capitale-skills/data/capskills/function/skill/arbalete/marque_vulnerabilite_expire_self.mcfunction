# @s = tireur ; la préparation du prochain carreau expire si elle n'est pas utilisée.
tag @s remove capskills.arbalete.vulnerabilite.loaded
scoreboard players set @s CAPSK_ARBA_MARK_T 0
title @s actionbar {"text":"Carreau de vulnérabilité dissipé.","color":"yellow"}
