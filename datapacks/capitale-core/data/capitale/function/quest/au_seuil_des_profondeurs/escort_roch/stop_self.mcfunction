tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_moving
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_active
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_returning
kill @e[type=armor_stand,tag=guide_roch_profondeurs]
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Escorte Roch]","color":"gold"},{"text":" : Escorte arrêtée. Le guide temporaire a été retiré ; il sera recréé au prochain départ.","color":"white"}]
