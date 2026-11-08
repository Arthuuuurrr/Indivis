# QMARK2 : visibilité personnelle des offres du core.
execute if predicate hc_quetes:migration/state/pq02/20 run return 0
execute if predicate hc_quetes:migration/state/pq03/20 run return 0
execute if predicate hc_quetes:migration/state/pq03/40 run return 0
execute if predicate hc_quetes:migration/state/pq04/20 run return 0
execute if predicate hc_quetes:migration/state/pq04/50 run return 0
execute if predicate hc_quetes:migration/state/pq02/50 run return 0
return 1
