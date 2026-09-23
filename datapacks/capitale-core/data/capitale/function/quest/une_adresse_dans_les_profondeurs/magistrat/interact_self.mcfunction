function capitale:player/ensure_runtime_self
# RC9ag — routeur Magistrat des Profondeurs compatible avec l'état d'attente QUEST_RESIDENCE_PROF=10.
scoreboard players set @s CAP_FLAG 0

execute if score @s CAP_FLAG matches 0 if score @s CAP_RANGSOCIAL matches 30.. run scoreboard players set @s CAP_FLAG 10
execute if score @s CAP_FLAG matches 10 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 10 run tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Votre citoyenneté est déjà portée aux registres. Les Profondeurs vous connaissent au moins assez pour vous laisser répondre de votre nom.","color":"white"}]

execute if score @s CAP_FLAG matches 0 if score @s CAP_RANGSOCIAL matches 20..29 if score @s QUEST_CITOYENNETE matches 0 run scoreboard players set @s CAP_FLAG 20
execute if score @s CAP_FLAG matches 20 run function capitale:quest/principal/citoyennete/start_self

execute if score @s CAP_FLAG matches 0 if score @s CAP_RANGSOCIAL matches 20..29 if score @s QUEST_CITOYENNETE matches 20 run scoreboard players set @s CAP_FLAG 21
execute if score @s CAP_FLAG matches 21 run function capitale:quest/principal/citoyennete/magistrat/open_payment_choices_self

execute if score @s CAP_FLAG matches 0 if score @s QUEST_PROFONDEURS matches ..99 if score @s CAP_RANGSOCIAL matches ..19 run scoreboard players set @s CAP_FLAG 30
execute if score @s CAP_FLAG matches 30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 30 run tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Prenez d’abord vos repères dans le quartier. Ceux qui arrivent ici sans savoir où poser leur sac repartent souvent plus vite qu’ils ne sont venus.","color":"white"}]

# 0 = ancien état libre ; 10 = état d'attente injecté par ensure_runtime après Au seuil des Profondeurs.
execute if score @s CAP_FLAG matches 0 if score @s QUEST_PROFONDEURS matches 100.. if score @s CAP_RANGSOCIAL matches ..19 if score @s QUEST_RESIDENCE_PROF matches 0 run scoreboard players set @s CAP_FLAG 40
execute if score @s CAP_FLAG matches 0 if score @s QUEST_PROFONDEURS matches 100.. if score @s CAP_RANGSOCIAL matches ..19 if score @s QUEST_RESIDENCE_PROF matches 10 run scoreboard players set @s CAP_FLAG 40
execute if score @s CAP_FLAG matches 40 run function capitale:quest/une_adresse_dans_les_profondeurs/magistrat/offer_self

execute if score @s CAP_FLAG matches 0 if score @s QUEST_RESIDENCE_PROF matches 1 run scoreboard players set @s CAP_FLAG 41
execute if score @s CAP_FLAG matches 41 run function capitale:quest/une_adresse_dans_les_profondeurs/magistrat/reoffer_self

execute if score @s CAP_FLAG matches 0 if score @s QUEST_RESIDENCE_PROF matches 20 run scoreboard players set @s CAP_FLAG 50
execute if score @s CAP_FLAG matches 50 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 50 run tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Une adresse d’abord. Louez une chambre à l’auberge, puis revenez me voir.","color":"white"}]

execute if score @s CAP_FLAG matches 0 if score @s QUEST_RESIDENCE_PROF matches 30 run scoreboard players set @s CAP_FLAG 60
execute if score @s CAP_FLAG matches 60 run function capitale:quest/une_adresse_dans_les_profondeurs/magistrat/open_payment_choices_self

execute if score @s CAP_FLAG matches 0 if score @s QUEST_RESIDENCE_PROF matches 100.. if score @s CAP_RANGSOCIAL matches ..19 run scoreboard players set @s CAP_FLAG 70
execute if score @s CAP_FLAG matches 70 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 70 run tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Vos droits de résidence sont consignés. Tâchez de faire honneur à l’adresse que vous avez donnée.","color":"white"}]
scoreboard players set @s CAP_FLAG 0
