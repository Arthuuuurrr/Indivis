function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_CRIME 0
scoreboard players set @s CAP_AMENDE 0
scoreboard players set @s CAP_COMPARUTION 0
scoreboard players set @s CAP_FUITE_JUSTICE 0
scoreboard players set @s CAP_RETENTION_TRIBUNAL 0
scoreboard players set @s CAP_JUSTICE_LEVEL 0
scoreboard players set @s CAP_ETAT 0
scoreboard players set @s CAP_ALERT 0
scoreboard players set @s CAP_ARREST_CD 0
scoreboard players set @s CAP_ASSAULT_CD 0
function capitale:bounds/crime_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s {"text":"[Statut légal] Dossier criminel remis à zéro.","color":"green"}
function capitale:crime/current_self
