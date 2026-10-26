# QMARK1 : visibilité personnelle des offres du core.
execute unless function capitale:quest/markers/guide_free run return 0
execute unless predicate hc_quetes:migration/state/dqhc03/1_to_2 unless score @s hcd_dqhc03 matches 1.. run return 1
return 0
