function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_ETAT 0
scoreboard players set @s CAP_QUESTLOCK 0
scoreboard players set @s CAP_SANCTIONLOCK 0
scoreboard players set @s CAP_ARREST_PENDING 0
scoreboard players set @s CAP_ARREST_TIMER 0
scoreboard players set @s CAP_ARREST_SOURCE 0
scoreboard players set @s CAP_ALERT 0
scoreboard players set @s CAP_ARREST_CD 0
effect clear @s minecraft:slowness
effect clear @s minecraft:weakness
effect clear @s minecraft:glowing
effect clear @s minecraft:nausea
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Capitale] Vous êtes libéré de la procédure en cours.","color":"green"}
