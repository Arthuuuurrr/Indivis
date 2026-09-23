# Menu admin / intégration — ne pas donner aux joueurs. Les quêtes publiques passent par EasyNPC.
function capitale:player/ensure_runtime_self
scoreboard players add @s QUEST_SIDE_BA_Q01 0
scoreboard players add @s QUEST_SIDE_BA_Q02 0
scoreboard players add @s QUEST_SIDE_BA_Q03 0
scoreboard players add @s QUEST_SIDE_BA_Q04 0
tellraw @s [{"text":"[Quêtes Bas-Anneaux]","color":"gold"},{"text":" Menu admin Q01-Q04","color":"white"}]
tellraw @s [{"text":"[Statut joueur]","color":"green","click_event":{"action":"run_command","command":"/function capitale:quest/side/bas_anneaux/status_self"},"hover_event":{"action":"show_text","value":"Afficher les scores de ce bloc."}},{"text":"  ","color":"gray"},{"text":"[Setup PNJ/points]","color":"light_purple","click_event":{"action":"run_command","command":"/function capitale:quest/side/bas_anneaux/setup/menu_self"},"hover_event":{"action":"show_text","value":"Configurer les anchors des étapes et la route de Mira."}}]
tellraw @s {"text":"Les fonctions de progression ne sont plus exposées ici : elles doivent être appelées par les bons PNJ/points EasyNPC.","color":"gray"}
