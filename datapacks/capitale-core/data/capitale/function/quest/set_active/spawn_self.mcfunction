function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_QUETEACTIVE 1
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Mission active : Sous pavillon de la Couronne","color":"gold"}
