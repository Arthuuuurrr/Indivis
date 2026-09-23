execute unless score @s CAP_NEAR_REACT matches 1.. run function capitale:dialogue/sound/parole_garde_near_self
execute unless score @s CAP_NEAR_REACT matches 1.. run tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Faute récente : avancez avec prudence.","color":"white"}]
execute unless score @s CAP_NEAR_REACT matches 1.. run function capitale:dialogue/sound/parole_garde_near_self
execute unless score @s CAP_NEAR_REACT matches 1.. run tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Une amende de 100 Martin d’Or est désormais inscrite à votre dossier.","color":"white"}]
execute unless score @s CAP_NEAR_REACT matches 1.. run effect give @s minecraft:glowing 5 0 true
execute unless score @s CAP_NEAR_REACT matches 1.. run scoreboard players set @s NPC_NEAR_CD 80
execute unless score @s CAP_NEAR_REACT matches 1.. run scoreboard players set @s CAP_NEAR_REACT 60
