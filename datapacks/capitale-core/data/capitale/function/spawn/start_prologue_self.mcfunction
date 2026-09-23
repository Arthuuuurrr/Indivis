# Téléporte le joueur au vrai début du prologue et fixe son spawn personnel sur place.
# Usage admin pour un joueur existant : /execute as <joueur> run function capitale:spawn/start_prologue_self
# Hook optionnel utilisé par capitale_rp_hud pour les nouveaux personnages multi-slots.
# RC9h : l'ouverture identité n'est plus appelée avant le TP/ensure_runtime.
function capitale:player/ensure_runtime_self
scoreboard players set @s CAP_FIRST_JOIN_DONE 1
scoreboard players set @s QUEST_SPAWN 0
scoreboard players set @s QUEST_PROLOGUE 0
scoreboard players set @s CAP_QUETEACTIVE 0
scoreboard players set @s CAP_QSEQ 0
scoreboard players set @s CAP_QSEQ_TIMER 0
scoreboard players set @s CAP_QDIALOG_OWNER 0
scoreboard players set @s CAP_QDIALOG_KEEP 0
scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
scoreboard players set @s CAP_GUIDE_MISS_T 0
execute in minecraft:overworld run tp @s 764 299 1101
execute in minecraft:overworld run spawnpoint @s 764 299 1101
function capitale:dialogue/sound/parole_simple_self
tellraw @s [{"text":"Le vent frappe l’avant du pont. Devant vous, le bâtiment de la Couronne tient encore sa route vers la Haute Capitale.","color":"gray","italic":true}]
tellraw @s [{"text":"[Objectif] ","color":"gold"},{"text":"avancez sur le pont et parlez au quartier-maître Géraud Rivet.","color":"white"}]
# L'ouverture obligatoire est armée après TP/spawnpoint, puis retentée temporairement par core/tick.
execute if entity @s[tag=capitale_identity_mandatory] run function capitale:spawn/identity_open_mandatory_self
