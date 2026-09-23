tellraw @s {"text":"--- UUID EasyNPC — Mira Q01 ---","color":"gold"}
tellraw @s [{"text":"Guide Mira Q01 : ","color":"gray"},{"text":"b1110000-0000-4010-8000-000000000901","color":"aqua","click_event":{"action":"copy_to_clipboard","value":"b1110000-0000-4010-8000-000000000901"},"hover_event":{"action":"show_text","value":"Copier puis coller dans EasyNPC > Follow by UUID de Mira."}}]
tellraw @s {"text":"À mettre dans Mira : Follow by UUID / Follow entity UUID. Le datapack déplace ce guide ; EasyNPC doit faire suivre Mira à cet UUID.","color":"gray"}
execute unless entity b1110000-0000-4010-8000-000000000901 run tellraw @s {"text":"[Mira Q01] Aucun guide avec cet UUID n’existe actuellement. Lance d’abord [1 Guide].","color":"red"}
execute if entity b1110000-0000-4010-8000-000000000901 run tellraw @s {"text":"[Mira Q01] Guide UUID fixe présent.","color":"green"}
