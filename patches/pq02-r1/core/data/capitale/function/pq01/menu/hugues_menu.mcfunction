# PQ01 R3-HF4
$tellraw @s {"text":"[Qu'est-ce que vous transportiez ?]","color":"yellow","click_event":{"action":"run_command","command":"/trigger QuestChoix set $(b1)"}}
$tellraw @s {"text":"[Pourquoi avoir livré des canons à Avaleiv ?]","color":"yellow","click_event":{"action":"run_command","command":"/trigger QuestChoix set $(b2)"}}
$tellraw @s {"text":"[Vous n'aimez pas votre travail ?]","color":"yellow","click_event":{"action":"run_command","command":"/trigger QuestChoix set $(b3)"}}
$tellraw @s {"text":"[Vous connaissiez déjà Avaleiv ?]","color":"yellow","click_event":{"action":"run_command","command":"/trigger QuestChoix set $(b4)"}}
$tellraw @s {"text":"[Vous étiez là lorsqu'on m'a secouru ?]","color":"yellow","click_event":{"action":"run_command","command":"/trigger QuestChoix set $(b5)"}}
$tellraw @s {"text":"[Je vous laisse à vos caisses.]","color":"yellow","click_event":{"action":"run_command","command":"/trigger QuestChoix set $(b7)"}}
$tellraw @s {"text":"[Fermer la conversation]","color":"gray","click_event":{"action":"run_command","command":"/trigger QuestChoix set $(b15)"}}
