function capskills:revolver/ammo/decrement_main_self
scoreboard players set @s CAPREV_FIRED 1
tag @s add capskills.revolver.fired_main
scoreboard players set @s CAPREV_ALT 1
function capskills:revolver/prepare_shot_self
