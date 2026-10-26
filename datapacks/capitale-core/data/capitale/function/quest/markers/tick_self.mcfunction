# QMARK2 : visibilité personnelle des offres du core.
$data modify storage capitale:quest_markers current set value {owner:"$(owner)",character:"$(character)"}
$tag @e[type=minecraft:text_display,tag=cap_qmark,tag=hcm_owner_$(owner)] remove cap_qmark_live
execute unless score @s CAP_PROFILE_LOCK matches 1.. at @s run function capitale:quest/markers/update_self with storage capitale:quest_markers current
$kill @e[type=minecraft:text_display,tag=cap_qmark,tag=hcm_owner_$(owner),tag=!cap_qmark_live]
