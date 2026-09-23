function capitale:quest/dialogue/clear_self
scoreboard players set @s CAP_PROF_CAISSE -1
scoreboard players remove @s REP_GARDEPROFONDEURS 3
function capitale:bounds/reputation_all_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Je vois. Dans les Profondeurs, on apprend aussi vite qui tend la main et qui la repousse. Continuons.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Réputation] Gardes des Profondeurs −3.","color":"red"}
tag @e[type=armor_stand,tag=guide_roch_profondeurs,limit=1] add escort_crate_h_resolved
