# PQ02-R1 — Inscription idempotente, Visiteur minimum, aucun rang abaissé
execute unless score @s hcq_pq02 matches 50 run return 0
execute unless score @s CAP_QDIALOG_OWNER matches 97 run return 0
execute if score @s CAP_PQ2_REG matches 1.. run return 0
scoreboard players set @s CAP_PQ2_REG 1
scoreboard players add @s CAP_RANGSOCIAL 0
execute if score @s CAP_RANGSOCIAL matches ..9 run scoreboard players set @s CAP_RANGSOCIAL 10
function capitale:bounds/rangsocial_self
function capitale:access/recalculate_by_rank_self
function capitale:display/prefix/sync_self
function hc_quetes:migration/write/pq02/v40
return 1
