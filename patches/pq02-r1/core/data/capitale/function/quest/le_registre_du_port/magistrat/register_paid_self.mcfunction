# PQ02-R1 — PQ02 : entretien assisté prioritaire, autres inscriptions et citoyenneté inchangées
execute if score @s hcq_pq02 matches 40..90 run return run function capitale:pq02/open_magistrat

scoreboard players set @s CAP_JCOINS 0
execute store result score @s CAP_JCOINS run clear @s capitale_currency:martin_dor 0
execute if score @s CAP_JCOINS matches 100.. run function capitale:quest/le_registre_du_port/magistrat/register_paid_success_self
execute unless score @s CAP_JCOINS matches 100.. run function capitale:dialogue/sound/parole_quete_self
execute unless score @s CAP_JCOINS matches 100.. run tellraw @s [{"text":"[Magistrat du Port]","color":"yellow"},{"text":" : Les frais ordinaires d’inscription s’élèvent à","color":"white"},{"text":"100 Martin d’Or","color":"yellow"},{"text":". Revenez avec la somme requise.","color":"white"}]
scoreboard players set @s CAP_JCOINS 0
