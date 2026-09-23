tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_moving
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_active
tag @e[type=armor_stand,tag=guide_roch_profondeurs] remove escort_returning
kill @e[type=armor_stand,tag=guide_roch_profondeurs]
tellraw @s [{"text":"[Escorte Roch]","color":"gold"},{"text":" : Guide de repos supprimé. Roch ne devrait plus chercher à se recaler hors escorte ; le guide sera recréé au départ.","color":"white"}]
