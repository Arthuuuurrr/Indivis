# QMARK2 : visibilité personnelle des offres du core.
$execute positioned ~ ~3.05 ~ unless entity @e[type=minecraft:text_display,tag=cap_qmark,tag=hcm_owner_$(owner),tag=hcm_character_$(character),tag=cap_qgiver_mira,limit=1] run summon minecraft:text_display ~ ~ ~ {Tags:["hcm_private","cap_qmark","cap_qmark_live","cap_qgiver_mira","hcm_owner_$(owner)","hcm_character_$(character)","cap_qmark_v2"],text:{text:"!",color:"yellow",bold:true},billboard:"center",shadow:1b,see_through:1b,brightness:{block:15,sky:15},background:0,line_width:64,view_range:0.4f,teleport_duration:20}
$execute as @e[type=minecraft:text_display,tag=cap_qmark,tag=hcm_owner_$(owner),tag=hcm_character_$(character),tag=cap_qgiver_mira,limit=1] unless entity @s[tag=cap_qmark_v2] run data merge entity @s {see_through:1b,brightness:{block:15,sky:15}}
$tag @e[type=minecraft:text_display,tag=cap_qmark,tag=hcm_owner_$(owner),tag=hcm_character_$(character),tag=cap_qgiver_mira,limit=1] add cap_qmark_v2
$tag @e[type=minecraft:text_display,tag=cap_qmark,tag=hcm_owner_$(owner),tag=hcm_character_$(character),tag=cap_qgiver_mira,limit=1] add cap_qmark_live
$execute positioned ~ ~3.05 ~ as @e[type=minecraft:text_display,tag=cap_qmark,tag=hcm_owner_$(owner),tag=hcm_character_$(character),tag=cap_qgiver_mira,limit=1] unless entity @s[distance=..0.05] run tp @s ~ ~ ~
return 1
