
clear @s capitale_currency:martin_dor 100
scoreboard players set @s CAP_JCOINS 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Magistrat du Port]","color":"yellow"},{"text":" : Les frais de registre sont réglés. Procédons à votre inscription.","color":"white"}]
function capitale:quest/le_registre_du_port/magistrat/register_commit_self
