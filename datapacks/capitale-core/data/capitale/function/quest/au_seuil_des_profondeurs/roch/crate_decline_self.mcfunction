function capitale:quest/dialogue/clear_self
scoreboard players set @s CAP_PROF_CAISSE 0
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Aucun tort. Je la ferai descendre autrement. Continuons.","color":"white"}]
tag @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] add escort_crate_h_resolved
