execute if score @s CAP_QUETEACTIVE matches 0 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_QUETEACTIVE matches 0 run tellraw @s {"text":"[Quête] Aucune mission active à abandonner.","color":"gray"}
execute unless score @s CAP_QUETEACTIVE matches 0 run scoreboard players set @s CAP_QUETEACTIVE 0
execute unless score @s CAP_QUESTLOCK matches 2.. run scoreboard players set @s CAP_QUESTLOCK 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Mission abandonnée. Certaines conséquences RP peuvent rester actives.","color":"yellow"}
