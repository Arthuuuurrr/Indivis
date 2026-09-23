scoreboard players set @s CAP_FLAG 0
execute store result score @s CAP_FLAG run clear @s minecraft:written_book[minecraft:custom_model_data={strings:['quest_libraire_exemplaire_noble']}] 0
execute if score @s CAP_FLAG matches 1.. run function capitale:quest/divers/exemplaire_noble/noble/receive_success_self
execute unless score @s CAP_FLAG matches 1.. run function capitale:quest/divers/exemplaire_noble/noble/missing_book_self
scoreboard players set @s CAP_FLAG 0
