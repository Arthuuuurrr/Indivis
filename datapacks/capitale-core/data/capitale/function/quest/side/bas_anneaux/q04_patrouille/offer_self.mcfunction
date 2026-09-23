function capitale:quest/dialogue/clear_self
scoreboard players set @s CAP_QDIALOG_OWNER 23
scoreboard players set @s CAP_QDIALOG_KEEP 1
function capitale:quest/dialogue/anchor/create_self
function capitale:dialogue/sound/parole_quete_self
scoreboard players enable @s QuestChoix
tellraw @s [{"text":"[Roland]","color":"aqua"},{"text":" : Les gardes ne servent pas seulement à manier une épée. Ils doivent connaître les rues et ceux qui y survivent.","color":"white"}]
execute if score @s QUEST_SIDE_BA_Q01 matches 101 run tellraw @s [{"text":"[Roland]","color":"aqua"},{"text":" : La gamine semble te faire confiance. Ce n’est pas forcément une mauvaise chose, mais garde les yeux ouverts.","color":"white"}]
execute if score @s QUEST_SIDE_BA_Q01 matches 102 run tellraw @s [{"text":"[Roland]","color":"aqua"},{"text":" : Tu as choisi l’ordre avec Mira. Dans les Bas-Anneaux, ce choix a toujours un prix.","color":"white"}]
tellraw @s [{"text":"[Quête]","color":"gold"},{"text":" Secondaire Bas-Anneaux Q04 — La Patrouille","color":"white"}]
tellraw @s [{"text":"[Choix] ","color":"gold"},{"text":"[Accompagner Roland]","color":"green","click_event":{"action":"run_command","command":"/trigger QuestChoix set 230"}},{"text":"  ","color":"gray"},{"text":"[Demander le trajet]","color":"yellow","click_event":{"action":"run_command","command":"/trigger QuestChoix set 231"}},{"text":"  ","color":"gray"},{"text":"[Pas maintenant]","color":"red","click_event":{"action":"run_command","command":"/trigger QuestChoix set 232"}}]
