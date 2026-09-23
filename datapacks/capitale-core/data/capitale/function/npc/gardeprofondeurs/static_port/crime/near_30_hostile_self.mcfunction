execute unless score @s CAP_NEAR_REACT matches 1.. run function capitale:dialogue/sound/parole_garde_near_self
execute unless score @s CAP_NEAR_REACT matches 1.. run tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Recherché : les ruelles ne vous protégeront pas. Votre réputation rend le contrôle plus sévère.","color":"white"}]
execute unless score @s CAP_NEAR_REACT matches 1.. run function capitale:dialogue/sound/parole_garde_near_self
execute unless score @s CAP_NEAR_REACT matches 1.. run tellraw @s [{"text":"[Garde des Profondeurs]","color":"#FF8C00"},{"text":" : Une amende de 200 Martin d’Or est inscrite. Cette comparution ne sera pas différée sans justification solide.","color":"white"}]
execute unless score @s CAP_NEAR_REACT matches 1.. run effect give @s minecraft:glowing 5 0 true
execute unless score @s CAP_NEAR_REACT matches 1.. run effect give @s minecraft:slowness 4 1 true
execute unless score @s CAP_NEAR_REACT matches 1.. run scoreboard players set @s NPC_NEAR_CD 80
execute unless score @s CAP_NEAR_REACT matches 1.. run function capitale:justice/intercept/start_30_49_gardeprofondeurs_self
execute unless score @s CAP_NEAR_REACT matches 1.. run scoreboard players set @s CAP_NEAR_REACT 60
