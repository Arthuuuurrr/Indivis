# Tirage 1..4 avec anti-répétition immédiate
execute store result score @s CAP_DLG_RNG run random value 1..4
execute if score @s CAP_DLG_RNG = @s CAP_DLG_LAST run scoreboard players add @s CAP_DLG_RNG 1
execute if score @s CAP_DLG_RNG matches 5.. run scoreboard players set @s CAP_DLG_RNG 1
scoreboard players operation @s CAP_DLG_LAST = @s CAP_DLG_RNG
