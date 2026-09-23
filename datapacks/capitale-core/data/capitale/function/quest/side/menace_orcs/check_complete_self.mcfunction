# appele apres chaque kill compte : fait passer la quete en "pret a rendre" quand les 4 objectifs sont atteints
execute if score @s QUEST_ALDREN_ORCS matches 10 if score @s CAP_ALDREN_WARR matches 6.. if score @s CAP_ALDREN_ARCH matches 6.. if score @s CAP_ALDREN_CHAMP matches 3.. if score @s CAP_ALDREN_MORGRA matches 1.. run function capitale:quest/side/menace_orcs/complete_self
