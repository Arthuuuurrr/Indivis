function capitale:dialogue/random/roll_3_self
execute if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Entrez donc. Le quartier-maître a dû vous prévenir : j’aimerais vous parler avant l’accostage. Adressez-vous à moi lorsque vous serez prêt.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Entrez, je vous prie. Avant que nous touchions les quais, il nous faut échanger quelques mots. Venez me parler lorsque vous serez prêt.","color":"white"}]
execute if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Nous approchons de la Haute Capitale. Avant l’accostage, je veux entendre ce que vous pouvez dire de votre situation. Parlez-moi dès que vous êtes disposé.","color":"white"}]
scoreboard players set @s CAP_QSEQ 0
scoreboard players set @s CAP_QSEQ_TIMER 0
