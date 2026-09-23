# Admin/operator only. Aucune commande trigger publique volontairement.
puffish_skills category unlock @s capskills:doctrine
puffish_skills points add @s capskills:doctrine 5
playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 0.8 1.35 0
tellraw @s {"text":"[CapSkills Admin] +5 points Pufferfish ajoutés dans doctrine.","color":"light_purple"}
