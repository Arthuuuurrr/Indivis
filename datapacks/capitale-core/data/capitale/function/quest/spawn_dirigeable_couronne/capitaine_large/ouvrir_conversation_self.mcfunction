
scoreboard players set @s QUEST_SPAWN 20
scoreboard players set @s CAP_QDIALOG_OWNER 1
scoreboard players set @s CAP_QDIALOG_KEEP 0
function capitale:quest/dialogue/anchor/create_self
scoreboard players set @s CAP_QDIALOG_KEEP 1
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Nous revenions vers la Haute Capitale lorsque la vigie vous a aperçu, seul, loin des routes que nous reconnaissons.","color":"white"}]
scoreboard players set @s CAP_QSEQ 201
scoreboard players set @s CAP_QSEQ_TIMER 20
