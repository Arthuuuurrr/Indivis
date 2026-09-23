# Admin/operator only. Aucune commande trigger publique volontairement.
puffish_skills category unlock @s capskills:doctrine
puffish_skills points set @s capskills:doctrine 0
tellraw @s {"text":"[CapSkills Admin] Points Pufferfish doctrine mis à 0. Les skills déjà débloqués ne sont pas forcément retirés : utiliser reset/erase si nécessaire.","color":"red"}
