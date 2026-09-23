# Active une visualisation temporaire des points Mira et des anchors Bas-Anneaux.
scoreboard players set @s CAP_PATH_DEBUG 200
effect give @e[tag=npc_mira_q01,limit=1] minecraft:glowing 10 0 true
tellraw @s {"text":"[Mira Q01] Visualisation activée 10 secondes : end_rod=route, flame=guide, happy_villager=anchors.","color":"light_purple"}
