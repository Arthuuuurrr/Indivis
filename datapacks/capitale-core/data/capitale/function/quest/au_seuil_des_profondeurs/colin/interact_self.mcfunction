function capitale:player/ensure_runtime_self
execute if score @s QUEST_PROFONDEURS matches ..34 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_PROFONDEURS matches ..34 run tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : Roch vous mènera jusqu’ici s’il vous prend en charge. Sans cela, je ne sais pas encore ce que vous attendez de moi.","color":"white"}]
execute if score @s QUEST_PROFONDEURS matches 35 if score @s CAP_PROF_CAISSE matches 1 run function capitale:quest/au_seuil_des_profondeurs/colin/receive_crate_attempt_self
execute if score @s QUEST_PROFONDEURS matches 35 unless score @s CAP_PROF_CAISSE matches 1 run function capitale:quest/au_seuil_des_profondeurs/colin/open_choices_self
execute if score @s QUEST_PROFONDEURS matches 40 run function capitale:quest/au_seuil_des_profondeurs/colin/open_choices_self
execute if score @s QUEST_PROFONDEURS matches 50 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_PROFONDEURS matches 50 run tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : Restez dans le passage. L’auberge est plus proche qu’elle ne paraît.","color":"white"}]
execute if score @s QUEST_PROFONDEURS matches 60 run function capitale:quest/au_seuil_des_profondeurs/colin/complete_self
execute if score @s QUEST_PROFONDEURS matches 100.. run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_PROFONDEURS matches 100.. run tellraw @s [{"text":"[Colin Férand]","color":"yellow"},{"text":" : L’auberge tient toujours debout, ce qui est déjà une forme de recommandation.","color":"white"}]
