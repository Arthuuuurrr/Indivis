function capitale:player/ensure_runtime_self
# RC9af — routeur Magistrat du Port protégé : une seule branche de dialogue par interaction.
# L’enregistrement assisté par Léovic prime sur le rang initialisé par le prologue.
scoreboard players set @s CAP_FLAG 0

execute if score @s CAP_FLAG matches 0 if score @s QUEST_GARDEPORT matches 30 run scoreboard players set @s CAP_FLAG 30
execute if score @s CAP_FLAG matches 30 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 30 run tellraw @s [{"text":"[Magistrat du Port]","color":"yellow"},{"text":" : Le garde Léovic se porte garant de votre arrivée. Je porte votre passage au registre du Port.","color":"white"}]
execute if score @s CAP_FLAG matches 30 run function capitale:quest/le_registre_du_port/magistrat/register_commit_self

execute if score @s CAP_FLAG matches 0 if score @s CAP_RANGSOCIAL matches 30.. run scoreboard players set @s CAP_FLAG 40
execute if score @s CAP_FLAG matches 40 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 40 run tellraw @s [{"text":"[Magistrat du Port]","color":"yellow"},{"text":" : Votre citoyenneté figure déjà aux registres de la Capitale. Les quais ne sauraient prétendre l’ignorer.","color":"white"}]
execute if score @s CAP_FLAG matches 0 if score @s CAP_RANGSOCIAL matches 20..29 if score @s QUEST_CITOYENNETE matches 0 run scoreboard players set @s CAP_FLAG 50
execute if score @s CAP_FLAG matches 50 run function capitale:quest/principal/citoyennete/start_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_RANGSOCIAL matches 20..29 if score @s QUEST_CITOYENNETE matches 20 run scoreboard players set @s CAP_FLAG 51
execute if score @s CAP_FLAG matches 51 run function capitale:quest/principal/citoyennete/magistrat/open_payment_choices_self
execute if score @s CAP_FLAG matches 0 if score @s CAP_RANGSOCIAL matches 10..19 run scoreboard players set @s CAP_FLAG 60
execute if score @s CAP_FLAG matches 60 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_FLAG matches 60 run tellraw @s [{"text":"[Magistrat du Port]","color":"yellow"},{"text":" : Votre nom figure déjà dans les registres utiles à votre circulation dans la Capitale.","color":"white"}]
execute if score @s CAP_FLAG matches 0 if score @s CAP_RANGSOCIAL matches ..9 run scoreboard players set @s CAP_FLAG 70
execute if score @s CAP_FLAG matches 70 run function capitale:quest/le_registre_du_port/magistrat/register_paid_self
scoreboard players set @s CAP_FLAG 0
