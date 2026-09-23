
scoreboard players set @s CAP_TEMOIGNAGES_COUNT 0
execute if score @s CAP_TEMOIN_QUAI_A matches 1.. run scoreboard players add @s CAP_TEMOIGNAGES_COUNT 1
execute if score @s CAP_TEMOIN_QUAI_B matches 1.. run scoreboard players add @s CAP_TEMOIGNAGES_COUNT 1
execute if score @s CAP_TEMOIN_QUAI_C matches 1.. run scoreboard players add @s CAP_TEMOIGNAGES_COUNT 1
