scoreboard players set @s CAP_QUETEACTIVE 0
scoreboard players set @s CAP_QUESTLOCK 0
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Quête] Mission active clôturée. Vous pouvez accepter une nouvelle mission.","color":"green"}
