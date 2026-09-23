function capitale:dialogue/random/roll_2_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Vous avez l’air volontaire. Mais je ne confie pas une affaire de cette taille à un voyageur dont la Capitale ignore encore jusqu’au rang. Faites-vous d’abord reconnaître comme Résident.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Votre bonne volonté ne suffit pas encore. Sans inscription comme Résident, je ne vous confierai rien qui puisse troubler les quais.","color":"white"}]
