
scoreboard players set @s CAP_FLAG 0
execute store result score @s CAP_FLAG run clear @s minecraft:paper[minecraft:custom_model_data={strings:['acte_dette_falsifie']}] 0
execute if score @s CAP_FLAG matches 1.. run function capitale:quest/divers/acte_dette/lucain/open_return_choices_self
execute unless score @s CAP_FLAG matches 1.. run function capitale:quest/divers/acte_dette/lucain/reissue_self
scoreboard players set @s CAP_FLAG 0
