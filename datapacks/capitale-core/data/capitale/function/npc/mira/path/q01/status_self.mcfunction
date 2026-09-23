tellraw @s {"text":"--- Mira Q01 — statut route ---","color":"gold"}
execute if entity @e[type=armor_stand,tag=guide_mira_q01,limit=1] run tellraw @s {"text":"GUIDE : OK","color":"green"}
execute unless entity @e[type=armor_stand,tag=guide_mira_q01,limit=1] run tellraw @s {"text":"GUIDE : manquant","color":"red"}
execute if entity @e[tag=npc_mira_q01,limit=1] run tellraw @s {"text":"PNJ Mira tag npc_mira_q01 : OK","color":"green"}
execute unless entity @e[tag=npc_mira_q01,limit=1] run tellraw @s {"text":"PNJ Mira tag npc_mira_q01 : manquant","color":"red"}
execute if entity @e[type=marker,tag=wp_mira_q01_home,limit=1] run tellraw @s {"text":"HOME : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_home,limit=1] run tellraw @s {"text":"HOME : manquant","color":"red"}
execute if entity @e[type=marker,tag=wp_mira_q01_bump,limit=1] run tellraw @s {"text":"BUMP : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_bump,limit=1] run tellraw @s {"text":"BUMP : manquant","color":"red"}
execute if entity @e[type=marker,tag=wp_mira_q01_ruelle,limit=1] run tellraw @s {"text":"RUELLE : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_ruelle,limit=1] run tellraw @s {"text":"RUELLE : manquant","color":"red"}
execute if entity @e[type=marker,tag=wp_mira_q01_cache,limit=1] run tellraw @s {"text":"CACHE : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_cache,limit=1] run tellraw @s {"text":"CACHE : manquant","color":"red"}
execute if entity @e[type=armor_stand,tag=guide_mira_q01,tag=patrol_active,limit=1] run tellraw @s {"text":"ROUTE : active","color":"yellow"}
execute unless entity @e[type=armor_stand,tag=guide_mira_q01,tag=patrol_active,limit=1] run tellraw @s {"text":"ROUTE : inactive","color":"gray"}
tellraw @s {"text":"--- ROUTE DETAILLEE r01-r12 ---","color":"light_purple"}
execute if entity @e[type=marker,tag=wp_mira_q01_r01,limit=1] run tellraw @s {"text":"r01 : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_r01,limit=1] run tellraw @s {"text":"r01 : absent","color":"gray"}
execute if entity @e[type=marker,tag=wp_mira_q01_r02,limit=1] run tellraw @s {"text":"r02 : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_r02,limit=1] run tellraw @s {"text":"r02 : absent","color":"gray"}
execute if entity @e[type=marker,tag=wp_mira_q01_r03,limit=1] run tellraw @s {"text":"r03 : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_r03,limit=1] run tellraw @s {"text":"r03 : absent","color":"gray"}
execute if entity @e[type=marker,tag=wp_mira_q01_r04,limit=1] run tellraw @s {"text":"r04 : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_r04,limit=1] run tellraw @s {"text":"r04 : absent","color":"gray"}
execute if entity @e[type=marker,tag=wp_mira_q01_r05,limit=1] run tellraw @s {"text":"r05 : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_r05,limit=1] run tellraw @s {"text":"r05 : absent","color":"gray"}
execute if entity @e[type=marker,tag=wp_mira_q01_r06,limit=1] run tellraw @s {"text":"r06 : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_r06,limit=1] run tellraw @s {"text":"r06 : absent","color":"gray"}
execute if entity @e[type=marker,tag=wp_mira_q01_r07,limit=1] run tellraw @s {"text":"r07 : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_r07,limit=1] run tellraw @s {"text":"r07 : absent","color":"gray"}
execute if entity @e[type=marker,tag=wp_mira_q01_r08,limit=1] run tellraw @s {"text":"r08 : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_r08,limit=1] run tellraw @s {"text":"r08 : absent","color":"gray"}
execute if entity @e[type=marker,tag=wp_mira_q01_r09,limit=1] run tellraw @s {"text":"r09 : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_r09,limit=1] run tellraw @s {"text":"r09 : absent","color":"gray"}
execute if entity @e[type=marker,tag=wp_mira_q01_r10,limit=1] run tellraw @s {"text":"r10 : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_r10,limit=1] run tellraw @s {"text":"r10 : absent","color":"gray"}
execute if entity @e[type=marker,tag=wp_mira_q01_r11,limit=1] run tellraw @s {"text":"r11 : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_r11,limit=1] run tellraw @s {"text":"r11 : absent","color":"gray"}
execute if entity @e[type=marker,tag=wp_mira_q01_r12,limit=1] run tellraw @s {"text":"r12 : OK","color":"green"}
execute unless entity @e[type=marker,tag=wp_mira_q01_r12,limit=1] run tellraw @s {"text":"r12 : absent","color":"gray"}
execute if entity @e[type=marker,tag=wp_mira_q01_r01,limit=1] run tellraw @s {"text":"MODE prévu : route détaillée HOME -> r01.. -> CACHE -> HOME","color":"aqua"}
execute unless entity @e[type=marker,tag=wp_mira_q01_r01,limit=1] run tellraw @s {"text":"MODE prévu : ancienne route courte HOME -> BUMP -> RUELLE -> CACHE","color":"yellow"}
tellraw @s {"text":"Near Distance joueur sur Mira : function capitale:npc/mira/near_self, Execute as User ON. Déclenche Q01 si elle n’est pas commencée.","color":"aqua"}
tellraw @s {"text":"Si EasyNPC exécute un near côté PNJ uniquement : ajoute aussi function capitale:npc/mira/path/q01/near_npc côté PNJ, mais garde near_self côté joueur pour déclencher la quête.","color":"dark_aqua"}
tellraw @s [{"text":"UUID guide Mira Q01 / EasyNPC Follow by UUID : ","color":"gray"},{"text":"b1110000-0000-4010-8000-000000000901","color":"aqua","click_event":{"action":"copy_to_clipboard","value":"b1110000-0000-4010-8000-000000000901"}}]
execute if entity b1110000-0000-4010-8000-000000000901 run tellraw @s {"text":"[Mira Q01] Guide UUID fixe présent.","color":"green"}
execute unless entity b1110000-0000-4010-8000-000000000901 run tellraw @s {"text":"[Mira Q01] Guide UUID fixe absent : refaire [1 Guide].","color":"red"}
tellraw @s {"text":"Near Distance : si near_self ne déclenche rien, EasyNPC exécute probablement la proximité comme PNJ. Dans ce cas mettre function capitale:npc/mira/near_npc en Near Distance, Execute as User OFF / as NPC.","color":"gold"}
tellraw @s {"text":"Retour HOME : utilise maintenant un retour progressif r12->r01->HOME. Le retour instantané direct est évité car il mettait le guide trop loin de Mira.","color":"gold"}
