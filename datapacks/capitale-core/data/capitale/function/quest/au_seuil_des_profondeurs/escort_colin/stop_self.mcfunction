tag @e[type=armor_stand,tag=guide_colin_profondeurs] remove escort_moving
tag @e[type=armor_stand,tag=guide_colin_profondeurs] remove escort_active
tag @e[type=armor_stand,tag=guide_colin_profondeurs] remove escort_returning
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Escorte Colin]","color":"gold"},{"text":" : Escorte arrêtée.","color":"white"}]
