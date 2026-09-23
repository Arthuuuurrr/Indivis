scoreboard players add @s CAP_CRIME 0
scoreboard players operation @s CAP_RAW = @s CAP_CRIME
execute if score @s CAP_CRIME matches ..9 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_CRIME matches ..9 run tellraw @s [{"text":"[Statut légal] Niveau actuel : ","color":"gray"},{"text":"Dossier vierge","color":"green"},{"text":" (","color":"dark_gray"},{"score":{"name":"@s","objective":"CAP_RAW"},"color":"dark_gray"},{"text":")","color":"dark_gray"}]
execute if score @s CAP_CRIME matches 10..19 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_CRIME matches 10..19 run tellraw @s [{"text":"[Statut légal] Niveau actuel : ","color":"gray"},{"text":"Avertissement inscrit","color":"yellow"},{"text":" (","color":"dark_gray"},{"score":{"name":"@s","objective":"CAP_RAW"},"color":"dark_gray"},{"text":")","color":"dark_gray"}]
execute if score @s CAP_CRIME matches 20..29 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_CRIME matches 20..29 run tellraw @s [{"text":"[Statut légal] Niveau actuel : ","color":"gray"},{"text":"Amende de garde","color":"gold"},{"text":" (","color":"dark_gray"},{"score":{"name":"@s","objective":"CAP_RAW"},"color":"dark_gray"},{"text":")","color":"dark_gray"}]
execute if score @s CAP_CRIME matches 30..39 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_CRIME matches 30..39 run tellraw @s [{"text":"[Statut légal] Niveau actuel : ","color":"gray"},{"text":"Comparution exigée","color":"red"},{"text":" (","color":"dark_gray"},{"score":{"name":"@s","objective":"CAP_RAW"},"color":"dark_gray"},{"text":")","color":"dark_gray"}]
execute if score @s CAP_CRIME matches 40..49 run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_CRIME matches 40..49 run tellraw @s [{"text":"[Statut légal] Niveau actuel : ","color":"gray"},{"text":"Arrestation judiciaire","color":"dark_red"},{"text":" (","color":"dark_gray"},{"score":{"name":"@s","objective":"CAP_RAW"},"color":"dark_gray"},{"text":")","color":"dark_gray"}]
execute if score @s CAP_CRIME matches 50.. run function capitale:dialogue/sound/parole_simple_self
execute if score @s CAP_CRIME matches 50.. run tellraw @s [{"text":"[Statut légal] Niveau actuel : ","color":"gray"},{"text":"Ennemi de la Couronne","color":"dark_red","bold":true},{"text":" (","color":"dark_gray","bold":false},{"score":{"name":"@s","objective":"CAP_RAW"},"color":"dark_gray","bold":false},{"text":")","color":"dark_gray","bold":false}]
