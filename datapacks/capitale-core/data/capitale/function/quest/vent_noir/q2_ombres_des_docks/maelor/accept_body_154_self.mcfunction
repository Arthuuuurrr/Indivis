function capitale:quest/dialogue/clear_self
execute if score @s QUEST_VN_CAPITAINE matches 100 if score @s QUEST_VN_DOCKS matches 0 if score @s CAP_QUETEACTIVE matches 0 run function capitale:quest/vent_noir/q2_ombres_des_docks/start_commit_self
execute unless score @s QUEST_VN_CAPITAINE matches 100 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_VN_CAPITAINE matches 100 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Nous n’en sommes pas là. Les choses se prennent dans l’ordre, même quand on a vécu comme un pirate.","color":"white"}]
execute if score @s CAP_QUETEACTIVE matches 1.. unless score @s CAP_QUETEACTIVE matches 8 run function capitale:dialogue/sound/parole_quete_self
execute if score @s CAP_QUETEACTIVE matches 1.. unless score @s CAP_QUETEACTIVE matches 8 run tellraw @s [{"text":"[Maelor Veyne]","color":"yellow"},{"text":" : Vous portez déjà une autre affaire. Revenez lorsque votre attention vous appartiendra de nouveau.","color":"white"}]
