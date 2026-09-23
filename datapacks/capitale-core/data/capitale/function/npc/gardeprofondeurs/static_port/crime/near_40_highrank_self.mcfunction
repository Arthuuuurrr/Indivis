execute unless score @s CAP_NEAR_REACT matches 1.. run function capitale:dialogue/sound/parole_garde_near_self
execute unless score @s CAP_NEAR_REACT matches 1.. run tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Menace prioritaire : le quartier se méfie de vous. Votre rang sera respecté, mais il ne supprime pas le dossier.","color":"white"}]
execute unless score @s CAP_NEAR_REACT matches 1.. run function capitale:dialogue/sound/parole_garde_near_self
execute unless score @s CAP_NEAR_REACT matches 1.. run tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Une arrestation judiciaire est signalée. La garde transmettra sans procédé sommaire.","color":"white"}]
execute unless score @s CAP_NEAR_REACT matches 1.. run effect give @s minecraft:glowing 5 0 true
execute unless score @s CAP_NEAR_REACT matches 1.. run scoreboard players set @s NPC_NEAR_CD 80
execute unless score @s CAP_NEAR_REACT matches 1.. run scoreboard players set @s CAP_NEAR_REACT 60
