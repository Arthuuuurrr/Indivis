tp @s 93 228 82
spawnpoint @s 93 228 82
execute at @s run playsound minecraft:block.piston.extend master @s ~ ~ ~ 3.2 0.55
scoreboard players set @s QUEST_SPAWN 40
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"Les vibrations se calment. À travers la cabine, le souffle du large cède peu à peu au tumulte du Port.","color":"gray","italic":true}]
scoreboard players set @s CAP_QSEQ 303
scoreboard players set @s CAP_QSEQ_TIMER 20
