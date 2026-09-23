function capitale:player/ensure_runtime_self
# RC9ag — routeur Aurèle compatible avec l'état d'attente QUEST_GARDECOEUR=10.
# Une seule branche par interaction pour éviter les cascades.
scoreboard players set @s CAP_FLAG 0

# Avant la fin de Léovic : rappel non progressif.
execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDEPORT matches ..99 run scoreboard players set @s CAP_FLAG 10
execute if score @s CAP_FLAG matches 10 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 10 run tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Les gardes du Port vous orienteront d’abord. Revenez vers le Cœur lorsque votre arrivée sera en ordre.","color":"white"}]

# 0 = ancien état libre ; 10 = état d'attente injecté par ensure_runtime après Le registre du Port.
execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDEPORT matches 100.. if score @s QUEST_GARDECOEUR matches 0 run scoreboard players set @s CAP_FLAG 20
execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDEPORT matches 100.. if score @s QUEST_GARDECOEUR matches 10 run scoreboard players set @s CAP_FLAG 20
execute if score @s CAP_FLAG matches 20 run function capitale:quest/sous_le_regard_du_coeur/aurele/offer_self

execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDECOEUR matches 1 run scoreboard players set @s CAP_FLAG 21
execute if score @s CAP_FLAG matches 21 run function capitale:quest/sous_le_regard_du_coeur/aurele/reoffer_self

execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDECOEUR matches 20 run scoreboard players set @s CAP_FLAG 30
execute if score @s CAP_FLAG matches 30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 30 run tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Restez près de moi. Les Cercles commerciaux ne sont plus loin.","color":"white"}]

execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDECOEUR matches 25 run scoreboard players set @s CAP_FLAG 35
execute if score @s CAP_FLAG matches 35 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 35 run tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Je retourne à la sortie du premier ascenseur. Retrouvez-moi là-bas lorsque vous serez prêt à descendre.","color":"white"}]

execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDECOEUR matches 30 run scoreboard players set @s CAP_FLAG 40
execute if score @s CAP_FLAG matches 40 run function capitale:quest/sous_le_regard_du_coeur/aurele/open_commerce_choices_self

execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDECOEUR matches 40 run scoreboard players set @s CAP_FLAG 50
execute if score @s CAP_FLAG matches 50 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 50 run tellraw @s [{"text":"[Aurèle Veyrane]","color":"yellow"},{"text":" : Encore un peu. L’ascenseur des Profondeurs se trouve plus loin sur l’axe autorisé.","color":"white"}]

execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDECOEUR matches 50 run scoreboard players set @s CAP_FLAG 60
execute if score @s CAP_FLAG matches 60 run function capitale:quest/sous_le_regard_du_coeur/aurele/complete_self

execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDECOEUR matches 100.. run scoreboard players set @s CAP_FLAG 70
execute if score @s CAP_FLAG matches 70 run function capitale:quest/sous_le_regard_du_coeur/aurele/postquest_self
scoreboard players set @s CAP_FLAG 0
