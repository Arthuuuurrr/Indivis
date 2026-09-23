
scoreboard players set @s CAP_FLAG 0
execute store result score @s CAP_FLAG run clear @s minecraft:tripwire_hook[minecraft:custom_model_data={strings:['ressort_clocher']}] 0
execute if score @s CAP_FLAG matches 1.. if score @s CAP_RESSORT_TIMER matches 1.. run function capitale:quest/journalieres/ressort_clocher/horlogere/complete_on_time_self
execute if score @s QUEST_DAILY_RESSORT_CLOCHER matches 30 if score @s CAP_FLAG matches 1.. unless score @s CAP_RESSORT_TIMER matches 1.. run function capitale:quest/journalieres/ressort_clocher/horlogere/complete_late_self
execute unless score @s CAP_FLAG matches 1.. run function capitale:quest/journalieres/ressort_clocher/horlogere/missing_item_self
scoreboard players set @s CAP_FLAG 0
