function capskills:revolver/ammo/decrement_off_self
scoreboard players set @s CAPREV_FIRED 2
tag @s add capskills.revolver.fired_off
scoreboard players set @s CAPREV_ALT 0
function capskills:revolver/prepare_shot_self
