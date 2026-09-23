
execute at @s run playsound minecraft:block.piston.extend master @s ~ ~ ~ 3.2 0.55
execute at @s run playsound minecraft:block.piston.contract master @s ~ ~ ~ 2.8 0.45
effect give @s minecraft:nausea 5 3 true
effect give @s minecraft:blindness 2 0 true
title @s actionbar {"text":"Le dirigeable gagne les quais de la Haute Capitale…","color":"gray","italic":true}
scoreboard players set @s CAP_QSEQ 302
scoreboard players set @s CAP_QSEQ_TIMER 10
