execute unless score @s QUEST_DAILY_BANQUE matches 10 run tellraw @s {"text":"Aucun pli bancaire actif à lire.","color":"red"}
execute unless score @s QUEST_DAILY_BANQUE matches 10 run scoreboard players set @s CAP_READ_BANQUE 0
execute if score @s QUEST_DAILY_BANQUE matches 10 run scoreboard players set @s CAP_BANQUE_SCEAU 1
execute if score @s QUEST_DAILY_BANQUE matches 10 run clear @s minecraft:paper[minecraft:custom_model_data={strings:['documents_banque_scelles']}] 1
execute if score @s QUEST_DAILY_BANQUE matches 10 run give @s minecraft:paper[minecraft:custom_model_data={strings:['documents_banque_lus']},custom_name=[{"text":"Documents bancaires ouverts","italic":false,"color":"red"}],lore=[[{"text":"Le sceau est rompu.","italic":false,"color":"red"}],[{"text":"Quelques lignes mêlent dépôts, signatures et mentions de garantie.","italic":false,"color":"gray"}]]] 1
execute if score @s QUEST_DAILY_BANQUE matches 10 run function capitale:dialogue/sound/reponse_attendue_self
execute if score @s QUEST_DAILY_BANQUE matches 10 run tellraw @s [{"text":"[Sceau rompu] ","color":"red","bold":true},{"text":"Vous avez lu le pli. La Banque refusera désormais la remise.","color":"white"}]
scoreboard players set @s CAP_READ_BANQUE 0
scoreboard players enable @s CAP_READ_BANQUE
