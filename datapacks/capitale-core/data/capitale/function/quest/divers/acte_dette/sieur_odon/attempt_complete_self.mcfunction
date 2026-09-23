
scoreboard players set @s CAP_FLAG 0
execute store result score @s CAP_FLAG run clear @s minecraft:paper[minecraft:custom_model_data={strings:['acte_dette_falsifie']}] 0
execute if score @s CAP_FLAG matches 1.. run function capitale:quest/divers/acte_dette/sieur_odon/complete_noble_self
execute unless score @s CAP_FLAG matches 1.. run function capitale:dialogue/sound/parole_quete_self
execute unless score @s CAP_FLAG matches 1.. run tellraw @s [{"text":"[Sieur Odon de Varenne]","color":"yellow"},{"text":" : Vous revenez sans l’acte. Je vous ai demandé un document, pas une promenade dans les Profondeurs.","color":"white"}]
scoreboard players set @s CAP_FLAG 0
