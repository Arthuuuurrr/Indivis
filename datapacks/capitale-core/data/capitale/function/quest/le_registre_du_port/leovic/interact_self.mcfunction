function capitale:player/ensure_runtime_self
# RC9af — routeur Léovic protégé : une seule branche de dialogue par interaction.
# QUEST_GARDEPORT=10 = objectif récent « parler à Léovic sur les quais ».
# CAP_RANGSOCIAL ne bloque plus l’offre : le prologue récent peut déjà initialiser Visiteur (=10).
scoreboard players set @s CAP_FLAG 0

execute if score @s CAP_FLAG matches 0 if score @s QUEST_SPAWN matches ..99 run scoreboard players set @s CAP_FLAG 10
execute if score @s CAP_FLAG matches 10 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 10 run tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Nous parlerons quand vous aurez quitté le dirigeable pour de bon.","color":"white"}]

execute if score @s CAP_FLAG matches 0 if score @s QUEST_SPAWN matches 100 if score @s QUEST_GARDEPORT matches 0 run scoreboard players set @s CAP_FLAG 20
execute if score @s CAP_FLAG matches 0 if score @s QUEST_SPAWN matches 100 if score @s QUEST_GARDEPORT matches 10 run scoreboard players set @s CAP_FLAG 20
execute if score @s CAP_FLAG matches 20 run function capitale:quest/le_registre_du_port/leovic/offer_self
execute if score @s CAP_FLAG matches 0 if score @s QUEST_SPAWN matches 100 if score @s QUEST_GARDEPORT matches 1..2 run scoreboard players set @s CAP_FLAG 21
execute if score @s CAP_FLAG matches 21 run function capitale:quest/le_registre_du_port/leovic/reoffer_self

execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDEPORT matches 20 run scoreboard players set @s CAP_FLAG 30
execute if score @s CAP_FLAG matches 30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 30 run tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Restez près de moi. Le magistrat n’est plus très loin.","color":"white"}]
execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDEPORT matches 30 run scoreboard players set @s CAP_FLAG 40
execute if score @s CAP_FLAG matches 40 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 40 run tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Nous y sommes. Présentez-vous au magistrat du Port ; je vous attends ici.","color":"white"}]
execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDEPORT matches 40 run scoreboard players set @s CAP_FLAG 50
execute if score @s CAP_FLAG matches 50 run function capitale:quest/le_registre_du_port/leovic/lead_to_ascenseur_self
execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDEPORT matches 50 run scoreboard players set @s CAP_FLAG 60
execute if score @s CAP_FLAG matches 60 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 60 run tellraw @s [{"text":"[Garde Léovic]","color":"#FF8C00"},{"text":" : Encore quelques pas. L’ascenseur du Cercle commercial est juste devant nous.","color":"white"}]
execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDEPORT matches 60 run scoreboard players set @s CAP_FLAG 70
execute if score @s CAP_FLAG matches 70 run function capitale:quest/le_registre_du_port/leovic/complete_self
execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDEPORT matches 100.. if score @s CAP_LEOVIC_GIFT matches 0 if entity @e[type=armor_stand,tag=guide_leovic_registre_port,tag=escort_returning,limit=1] run scoreboard players set @s CAP_FLAG 80
execute if score @s CAP_FLAG matches 80 run function capitale:quest/le_registre_du_port/leovic/postquest_returning_self
execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDEPORT matches 100.. if score @s CAP_LEOVIC_GIFT matches 0 unless entity @e[type=armor_stand,tag=guide_leovic_registre_port,tag=escort_returning,limit=1] run scoreboard players set @s CAP_FLAG 81
execute if score @s CAP_FLAG matches 81 run function capitale:quest/le_registre_du_port/leovic/gift_after_quest_self
execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDEPORT matches 100.. if score @s CAP_LEOVIC_GIFT matches 1.. run scoreboard players set @s CAP_FLAG 82
execute if score @s CAP_FLAG matches 82 run function capitale:quest/le_registre_du_port/leovic/postquest_self
scoreboard players set @s CAP_FLAG 0
