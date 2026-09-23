tellraw @s {"text":"[CapSkills] Guide des catalyseurs","color":"blue","bold":true}
tellraw @s [{"text":"[Redonner les catalyseurs]","color":"blue","click_event":{"action":"run_command","command":"/function capskills:give/mod/all_items_self"}},{"text":"  "},{"text":"[Rafraîchir mes catalyseurs]","color":"yellow","click_event":{"action":"run_command","command":"/function capskills:give/mod/refresh_all_items_self"}}]
tellraw @s {"text":"Baguette de Soins : clic droit = soin personnel ou ciblé ; sneak + clic droit = cercle de soin.","color":"aqua"}
tellraw @s {"text":"Relais du Rempart : clic droit = Rempart personnel ou Root ciblé ; sneak + clic droit = Égide ; clic gauche/hit = Provocation si débloquée.","color":"blue"}
tellraw @s {"text":"Baguette de Destruction : clic droit = Décharge ; sneak + clic droit = Marque corrosive ; clic gauche/hit = Onde de recul si débloquée.","color":"dark_purple"}
tellraw @s {"text":"Baguette d’Altération : clic droit = zone ou Aveuglement ; sneak + clic droit = Suspension.","color":"green"}
tellraw @s {"text":"Sceau des Ombres : clic droit en visant une entité = Pas déphasé si débloqué.","color":"dark_gray"}
tellraw @s {"text":"Les anciens catalyseurs gardent leur ancien texte. Utilise Rafraîchir mes catalyseurs pour les remplacer.","color":"gray","italic":true}
tellraw @s [{"text":"[Profil]","color":"aqua","click_event":{"action":"run_command","command":"/function capskills:debug/profile_self"}},{"text":"  "},{"text":"[Aide activations]","color":"light_purple","click_event":{"action":"run_command","command":"/function capskills:debug/trigger_help_self"}}]
