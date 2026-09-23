execute unless score @s CAP_QUETEACTIVE matches 0 run function capitale:quest/blocked_active_self
execute unless score @s CAP_QUETEACTIVE matches 0 run function capitale:quest/dialogue/clear_self
execute if score @s CAP_QUETEACTIVE matches 0 run function capitale:quest/vent_noir/q1_capitaine_sans_navire/maelor/accept_commit_self
