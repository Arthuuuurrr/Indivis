# QMARK2 : visibilité personnelle des offres du core.
execute unless function capitale:quest/markers/guide_free run return 0
execute unless predicate hc_quetes:migration/state/rqhc02/20_to_50 unless score @s hcd_rqhc02 matches 1.. run return 1
return 0
