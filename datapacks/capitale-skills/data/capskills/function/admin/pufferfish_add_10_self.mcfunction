# Admin/operator only. Aucune commande trigger publique volontairement.
puffish_skills category unlock @s capskills:doctrine
puffish_skills points add @s capskills:doctrine 10
playsound minecraft:entity.player.levelup player @s ~ ~ ~ 0.8 1.5 0
tellraw @s {"text":"[CapSkills Admin] +10 points Pufferfish ajoutés dans doctrine.","color":"light_purple"}
