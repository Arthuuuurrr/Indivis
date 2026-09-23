scoreboard players set @s CAPREV_SELECTED 0
# Main uniquement.
execute if score @s CAPREV_STATE matches 1 if score @s CAPREV_MAIN matches 1.. run scoreboard players set @s CAPREV_SELECTED 1
# Offhand uniquement.
execute if score @s CAPREV_STATE matches 2 if score @s CAPREV_OFF matches 1.. run scoreboard players set @s CAPREV_SELECTED 2
# Double revolver : alternance, avec repli vers l'autre barillet s'il est vide.
execute if score @s CAPREV_STATE matches 3 if score @s CAPREV_ALT matches 0 if score @s CAPREV_MAIN matches 1.. run scoreboard players set @s CAPREV_SELECTED 1
execute if score @s CAPREV_STATE matches 3 if score @s CAPREV_ALT matches 1.. if score @s CAPREV_OFF matches 1.. run scoreboard players set @s CAPREV_SELECTED 2
execute if score @s CAPREV_STATE matches 3 if score @s CAPREV_SELECTED matches 0 if score @s CAPREV_MAIN matches 1.. run scoreboard players set @s CAPREV_SELECTED 1
execute if score @s CAPREV_STATE matches 3 if score @s CAPREV_SELECTED matches 0 if score @s CAPREV_OFF matches 1.. run scoreboard players set @s CAPREV_SELECTED 2
execute if score @s CAPREV_SELECTED matches 0 run function capskills:revolver/empty_self
execute if score @s CAPREV_SELECTED matches 1 run function capskills:revolver/fire_main_self
execute if score @s CAPREV_SELECTED matches 2 run function capskills:revolver/fire_off_self
