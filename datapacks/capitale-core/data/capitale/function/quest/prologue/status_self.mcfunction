function capitale:quest/prologue/normalize_self
function capitale:dialogue/sound/parole_simple_self
tellraw @s [{"text":"--- État prologue normalisé ---","color":"gold"}]
tellraw @s [{"text":"QUEST_PROLOGUE : ","color":"yellow"},{"score":{"name":"@s","objective":"QUEST_PROLOGUE"},"color":"white"},{"text":"  (index global)","color":"gray"}]
tellraw @s [{"text":"QUEST_SPAWN : ","color":"yellow"},{"score":{"name":"@s","objective":"QUEST_SPAWN"},"color":"white"},{"text":"  — Géraud / Althéon","color":"gray"}]
tellraw @s [{"text":"QUEST_GARDEPORT : ","color":"yellow"},{"score":{"name":"@s","objective":"QUEST_GARDEPORT"},"color":"white"},{"text":"  — Léovic / registre du Port","color":"gray"}]
tellraw @s [{"text":"QUEST_GARDECOEUR : ","color":"yellow"},{"score":{"name":"@s","objective":"QUEST_GARDECOEUR"},"color":"white"},{"text":"  — Aurèle / regard du Cœur","color":"gray"}]
tellraw @s [{"text":"QUEST_PROFONDEURS : ","color":"yellow"},{"score":{"name":"@s","objective":"QUEST_PROFONDEURS"},"color":"white"},{"text":"  — Roch / Colin","color":"gray"}]
tellraw @s [{"text":"QUEST_RESIDENCE_PROF : ","color":"yellow"},{"score":{"name":"@s","objective":"QUEST_RESIDENCE_PROF"},"color":"white"},{"text":"  — magistrat / auberge","color":"gray"}]
tellraw @s [{"text":"Dialogue : owner=","color":"dark_gray"},{"score":{"name":"@s","objective":"CAP_QDIALOG_OWNER"},"color":"gray"},{"text":" keep=","color":"dark_gray"},{"score":{"name":"@s","objective":"CAP_QDIALOG_KEEP"},"color":"gray"},{"text":" qseq=","color":"dark_gray"},{"score":{"name":"@s","objective":"CAP_QSEQ"},"color":"gray"}]
