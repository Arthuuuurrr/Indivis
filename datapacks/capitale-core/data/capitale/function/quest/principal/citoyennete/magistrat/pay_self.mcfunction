
scoreboard players set @s CAP_JCOINS 0
execute store result score @s CAP_JCOINS run clear @s capitale_currency:martin_dor 0
execute if score @s CAP_JCOINS matches 500.. run function capitale:quest/principal/citoyennete/magistrat/pay_success_self
execute unless score @s CAP_JCOINS matches 500.. run function capitale:quest/principal/citoyennete/magistrat/pay_fail_self
scoreboard players set @s CAP_JCOINS 0
