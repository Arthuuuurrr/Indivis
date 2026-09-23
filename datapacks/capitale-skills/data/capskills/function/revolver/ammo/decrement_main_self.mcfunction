# Une cartouche est consommée dès que le tir part, touche ou rate.
execute if score @s CAPREV_MAIN matches 1.. run scoreboard players remove @s CAPREV_MAIN 1
