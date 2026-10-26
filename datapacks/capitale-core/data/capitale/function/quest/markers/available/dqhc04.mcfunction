# QMARK2 : visibilité personnelle des offres du core.
execute unless function capitale:quest/markers/guide_free run return 0
execute unless predicate hc_quetes:migration/state/dqhc04/20_to_30 unless score @s hcd_dqhc04 matches 1.. run return 1
return 0
