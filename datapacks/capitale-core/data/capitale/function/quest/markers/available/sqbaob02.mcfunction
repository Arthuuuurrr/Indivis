# QMARK2 : visibilité personnelle des offres du core.
execute if predicate hc_quetes:migration/state/sqbaob02/0 unless predicate hc_quetes:migration/state/sqbaob03/20 run return 1
return 0
