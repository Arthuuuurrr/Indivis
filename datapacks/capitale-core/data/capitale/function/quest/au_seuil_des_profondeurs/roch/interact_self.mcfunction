function capitale:player/ensure_runtime_self
# RC9ag — routeur Roch compatible avec l'état d'attente QUEST_PROFONDEURS=10.
scoreboard players set @s CAP_FLAG 0

execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDECOEUR matches ..99 run scoreboard players set @s CAP_FLAG 10
execute if score @s CAP_FLAG matches 10 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 10 run tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Si vous venez d’en haut, achevez d’abord ce que le garde du Cœur a commencé avec vous.","color":"white"}]

# 0 = ancien état libre ; 10 = état d'attente injecté par ensure_runtime après Sous le regard du Cœur.
execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDECOEUR matches 100.. if score @s QUEST_PROFONDEURS matches 0 run scoreboard players set @s CAP_FLAG 20
execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDECOEUR matches 100.. if score @s QUEST_PROFONDEURS matches 10 run scoreboard players set @s CAP_FLAG 20
execute if score @s CAP_FLAG matches 20 run function capitale:quest/au_seuil_des_profondeurs/roch/offer_self

execute if score @s CAP_FLAG matches 0 if score @s QUEST_PROFONDEURS matches 1 run scoreboard players set @s CAP_FLAG 21
execute if score @s CAP_FLAG matches 21 run function capitale:quest/au_seuil_des_profondeurs/roch/reoffer_self

execute if score @s CAP_FLAG matches 0 if score @s QUEST_PROFONDEURS matches 20 if entity @e[type=armor_stand,tag=guide_roch_profondeurs,tag=escort_crate_h_done,tag=!escort_crate_h_resolved,limit=1] run scoreboard players set @s CAP_FLAG 30
execute if score @s CAP_FLAG matches 30 run function capitale:quest/au_seuil_des_profondeurs/roch/open_crate_choices_self

execute if score @s CAP_FLAG matches 0 if score @s QUEST_PROFONDEURS matches 20 unless entity @e[type=armor_stand,tag=guide_roch_profondeurs,tag=escort_crate_h_done,tag=!escort_crate_h_resolved,limit=1] run scoreboard players set @s CAP_FLAG 31
execute if score @s CAP_FLAG matches 31 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 31 run tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Restez dans mes pas. Le poste inférieur n’est plus très loin.","color":"white"}]

execute if score @s CAP_FLAG matches 0 if score @s QUEST_PROFONDEURS matches 30 run scoreboard players set @s CAP_FLAG 40
execute if score @s CAP_FLAG matches 40 run function capitale:quest/au_seuil_des_profondeurs/roch/conclude_to_colin_self

execute if score @s CAP_FLAG matches 0 if score @s QUEST_PROFONDEURS matches 100.. run scoreboard players set @s CAP_FLAG 50
execute if score @s CAP_FLAG matches 50 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 50 run tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Vous avez trouvé vos premiers repères. Dans les Profondeurs, c’est déjà beaucoup.","color":"white"}]
scoreboard players set @s CAP_FLAG 0
