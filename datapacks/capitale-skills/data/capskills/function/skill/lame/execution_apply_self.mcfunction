# CapSkills 0.9.4 — effet volontairement modéré : finisher conditionnel, pas burst permanent.
function capskills:visual/activation/lame_execution_self
damage @e[tag=capskills.lame_target,limit=1,sort=nearest] 3 minecraft:generic by @s
effect give @e[tag=capskills.lame_target,limit=1,sort=nearest] minecraft:weakness 4 0 true
effect give @e[tag=capskills.lame_target,limit=1,sort=nearest] minecraft:glowing 2 0 true
scoreboard players set @s CAPSK_EXEC_CD 14
title @s actionbar {"text":"Sentence d’exécution portée. Recharge : 14 s.","color":"red"}
