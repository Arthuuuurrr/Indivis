# 0.9.38 — remplace les anciens catalyseurs CapSkills par les versions au lore à jour.
clear @s capitale_skills_items:sceau_secours
clear @s capitale_skills_items:alambic_seuils
clear @s capitale_skills_items:focus_coeur
clear @s capitale_skills_items:relais_rempart
clear @s capitale_skills_items:sceau_ombres
clear @s minecraft:carrot_on_a_stick[minecraft:custom_data={capitale_skills:{item:"fetiche_ancetres"}}]
function capskills:give/mod/all_items_self
tellraw @s {"text":"[CapSkills] Anciens catalyseurs retirés puis redonnés avec les descriptions 0.9.38.","color":"gold"}

# 0.9.40 : rafraîchit aussi le Fétiche des Ancêtres si nécessaire.
execute if entity @s[tag=capskills.skill.chaman_totem_protection_1] run function capskills:give/mod/fetiche_ancetres_self
