execute unless score @s CAP_NEAR_REACT matches 1.. run function capitale:dialogue/random/roll_3_self
execute unless score @s CAP_NEAR_REACT matches 1.. if score @s CAP_DLG_RNG matches 1 run function capitale:dialogue/sound/parole_garde_near_self
execute unless score @s CAP_NEAR_REACT matches 1.. if score @s CAP_DLG_RNG matches 1 run tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Avertissement inscrit : les rues basses vous reconnaissent.","color":"white"}]
execute unless score @s CAP_NEAR_REACT matches 1.. if score @s CAP_DLG_RNG matches 2 run function capitale:dialogue/sound/parole_garde_near_self
execute unless score @s CAP_NEAR_REACT matches 1.. if score @s CAP_DLG_RNG matches 2 run tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Avertissement inscrit : les rues basses vous reconnaissent ; dites l’essentiel.","color":"white"}]
execute unless score @s CAP_NEAR_REACT matches 1.. if score @s CAP_DLG_RNG matches 3 run function capitale:dialogue/sound/parole_garde_near_self
execute unless score @s CAP_NEAR_REACT matches 1.. if score @s CAP_DLG_RNG matches 3 run tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Avertissement inscrit : les rues basses vous reconnaissent. La garde veut des paroles nettes.","color":"white"}]
execute unless score @s CAP_NEAR_REACT matches 1.. run scoreboard players set @s NPC_NEAR_CD 80
execute unless score @s CAP_NEAR_REACT matches 1.. run scoreboard players set @s CAP_NEAR_REACT 60
