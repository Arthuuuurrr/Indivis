scoreboard players set @s CAP_RANGSOCIAL 100
function capitale:bounds/rangsocial_self
function capitale:logic/classify/base_self
function capitale:access/recalculate_by_rank_self
function capitale:display/prefix/sync_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s {"text":"[Rang social] Empereur.","color":"gold"}
