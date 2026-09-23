function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_CRIME 50
function capitale:bounds/crime_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Statut légal] Ennemi de la Couronne (50)","color":"gold"}
