
scoreboard players set @s CAP_FLAG 0
execute store result score @s CAP_FLAG run clear @s minecraft:paper[minecraft:custom_model_data={strings:['acte_dette_falsifie']}] 0
execute if score @s CAP_FLAG matches 1.. run function capitale:dialogue/random/roll_3_self
execute if score @s CAP_FLAG matches 1.. if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 1.. if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Maître Lucain Perrin]","color":"yellow"},{"text":" : Allez voir Odon. S’il vous paraît trop sûr de lui, demandez-vous seulement qui profite de cette certitude.","color":"white"}]
execute if score @s CAP_FLAG matches 1.. if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 1.. if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Maître Lucain Perrin]","color":"yellow"},{"text":" : Allez voir Odon. S’il vous paraît trop sûr de lui, demandez-vous seulement qui profite de cette certitude .","color":"white"}]
execute if score @s CAP_FLAG matches 1.. if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 1.. if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Maître Lucain Perrin]","color":"yellow"},{"text":" : Écoutez bien. Allez voir Odon. S’il vous paraît trop sûr de lui, demandez-vous seulement qui profite de cette certitude.","color":"white"}]
execute unless score @s CAP_FLAG matches 1.. run function capitale:quest/divers/acte_dette/lucain/reissue_self
scoreboard players set @s CAP_FLAG 0
