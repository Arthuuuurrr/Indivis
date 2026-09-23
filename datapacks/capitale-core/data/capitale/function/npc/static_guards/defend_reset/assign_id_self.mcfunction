# Assigne un ID stable au PNJ statique si nécessaire.
execute unless score @s CAP_STATIC_GUARD_ID matches 1.. run scoreboard players add #next CAP_STATIC_GUARD_ID 1
execute unless score @s CAP_STATIC_GUARD_ID matches 1.. run scoreboard players operation @s CAP_STATIC_GUARD_ID = #next CAP_STATIC_GUARD_ID
