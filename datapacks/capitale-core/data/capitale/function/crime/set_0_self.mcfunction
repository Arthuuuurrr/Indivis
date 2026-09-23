function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_CRIME 0
scoreboard players set @s CAP_AMENDE 0
scoreboard players set @s CAP_COMPARUTION 0
scoreboard players set @s CAP_FUITE_JUSTICE 0
scoreboard players set @s CAP_RETENTION_TRIBUNAL 0
scoreboard players set @s CAP_JUSTICE_LEVEL 0
scoreboard players set @s CAP_ETAT 0
function capitale:bounds/crime_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Statut légal] Dossier vierge (0)","color":"gold"}
