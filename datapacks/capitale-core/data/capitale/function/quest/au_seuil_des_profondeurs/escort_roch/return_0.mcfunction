execute if entity @e[type=marker,tag=wp_roch_profondeurs_a,limit=1] run tp @s @e[type=marker,tag=wp_roch_profondeurs_a,limit=1]
tag @s remove escort_returning
scoreboard players set @s NPC_PATROL_CD 0
scoreboard players set @s NPC_RETURN_TIMER 0
kill @s
