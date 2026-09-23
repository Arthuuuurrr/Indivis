# Admin/operator only. Aucune commande trigger publique volontairement.
puffish_skills category unlock @s capskills:doctrine
puffish_skills points add @s capskills:doctrine 1
playsound minecraft:entity.experience_orb.pickup player @s ~ ~ ~ 0.7 1.25 0
tellraw @s {"text":"[CapSkills Admin] +1 point Pufferfish ajouté dans doctrine.","color":"light_purple"}
