scoreboard players set @s CAP_GUIDE_LOCK 0
scoreboard players set @s CAP_GUIDE_ID 0
execute if predicate hc_quetes:migration/state/pq02/20 run scoreboard players set @s CAP_GUIDE_LOCK 1
execute if predicate hc_quetes:migration/state/pq02/20 run scoreboard players set @s CAP_GUIDE_ID 1
execute if predicate hc_quetes:migration/state/pq03/20 run scoreboard players set @s CAP_GUIDE_LOCK 1
execute if predicate hc_quetes:migration/state/pq03/20 run scoreboard players set @s CAP_GUIDE_ID 2
execute if predicate hc_quetes:migration/state/pq03/40 run scoreboard players set @s CAP_GUIDE_LOCK 1
execute if predicate hc_quetes:migration/state/pq03/40 run scoreboard players set @s CAP_GUIDE_ID 2
execute if predicate hc_quetes:migration/state/pq04/20 run scoreboard players set @s CAP_GUIDE_LOCK 1
execute if predicate hc_quetes:migration/state/pq04/20 run scoreboard players set @s CAP_GUIDE_ID 3
execute if predicate hc_quetes:migration/state/pq04/50 run scoreboard players set @s CAP_GUIDE_LOCK 1
execute if predicate hc_quetes:migration/state/pq04/50 run scoreboard players set @s CAP_GUIDE_ID 4

# PQ02-R1 : courte escorte après inscription.
execute if predicate hc_quetes:migration/state/pq02/50 run scoreboard players set @s CAP_GUIDE_LOCK 1
execute if predicate hc_quetes:migration/state/pq02/50 run scoreboard players set @s CAP_GUIDE_ID 1
