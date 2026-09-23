# RC9z - Guide Mira Q01 avec UUID fixe pour EasyNPC Follow by UUID
# UUID à renseigner dans EasyNPC sur Mira : b1110000-0000-4010-8000-000000000901
kill @e[type=armor_stand,tag=guide_mira_q01]
kill b1110000-0000-4010-8000-000000000901
summon armor_stand ~ ~ ~ {UUID:[I;-1324285952,16400,-2147483648,2305],Tags:["guide_mira_q01","capitale_path_guide"],Invisible:1b,Marker:1b,NoGravity:1b,Invulnerable:1b,PersistenceRequired:1b,CustomName:'{"text":"Guide Mira Q01"}',CustomNameVisible:0b}
tellraw @s {"text":"[Mira Q01] Guide invisible recréé avec UUID fixe.","color":"green"}
tellraw @s [{"text":"UUID EasyNPC Follow by UUID : ","color":"gray"},{"text":"b1110000-0000-4010-8000-000000000901","color":"aqua","click_event":{"action":"copy_to_clipboard","value":"b1110000-0000-4010-8000-000000000901"},"hover_event":{"action":"show_text","value":"Cliquer pour copier l’UUID du guide Mira Q01."}}]
