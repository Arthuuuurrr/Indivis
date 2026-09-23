
scoreboard players set @s CAP_FLAG 0
execute store result score @s CAP_FLAG run clear @s minecraft:paper[minecraft:custom_model_data={strings:['acte_dette_falsifie']}] 0
execute if score @s CAP_FLAG matches 1.. run function capitale:quest/divers/acte_dette/noble/complete_self
execute unless score @s CAP_FLAG matches 1.. run function capitale:dialogue/sound/parole_quete_self
execute unless score @s CAP_FLAG matches 1.. run tellraw @s [{"text":"[Dame Éléonore de Vaudrec]","color":"yellow"},{"text":" : Odon m’a annoncé un document, pas un récit. Revenez lorsque vous aurez l’acte en main.","color":"white"}]
scoreboard players set @s CAP_FLAG 0
