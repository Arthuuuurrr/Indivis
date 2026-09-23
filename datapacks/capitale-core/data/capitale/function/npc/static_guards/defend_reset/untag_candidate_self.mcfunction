# Retire le statut resettable du PNJ EasyNPC courant et supprime son ancre si elle existe.
execute if score @s CAP_STATIC_GUARD_ID matches 1.. run scoreboard players operation #current CAP_STATIC_GUARD_ID = @s CAP_STATIC_GUARD_ID
execute if score @s CAP_STATIC_GUARD_ID matches 1.. as @e[type=minecraft:marker,tag=cap_static_guard_anchor] if score @s CAP_STATIC_GUARD_ID = #current CAP_STATIC_GUARD_ID run kill @s
tag @s remove cap_static_guard_resettable
tag @s remove cap_static_guard_active
scoreboard players set @s CAP_STATIC_GUARD_RESET 0
scoreboard players set @s CAP_STATIC_GUARD_ID 0
particle minecraft:smoke ~ ~1.8 ~ 0.2 0.2 0.2 0.01 4 force
