scoreboard players set @s CAP_JCOINS 0
execute store result score @s CAP_JCOINS run clear @s capitale_currency:martin_dor 0
execute if score @s CAP_JCOINS matches 20.. run function capitale:quest/une_adresse_dans_les_profondeurs/aubergiste/rent_success_self
execute unless score @s CAP_JCOINS matches 20.. run function capitale:quest/une_adresse_dans_les_profondeurs/aubergiste/rent_fail_self
scoreboard players set @s CAP_JCOINS 0
