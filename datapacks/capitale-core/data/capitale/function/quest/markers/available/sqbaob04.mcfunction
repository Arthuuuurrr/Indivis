# QMARK1 : visibilité personnelle des offres du core.
execute if predicate hc_quetes:migration/state/sqbaob04/0 unless predicate hc_quetes:migration/state/sqbaob03/40 run return 1
execute if predicate hc_quetes:migration/state/sqbaob04/0 if predicate hc_quetes:migration/state/sqbaob03/40 unless predicate hc_quetes:migration/state/sqbaob01/102 run return 1
return 0
