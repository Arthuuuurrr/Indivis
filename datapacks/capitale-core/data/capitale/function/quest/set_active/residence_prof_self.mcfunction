function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_QUETEACTIVE 6
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Quête] Mission active : Une adresse dans les Profondeurs","color":"gold"}
