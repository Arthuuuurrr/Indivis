scoreboard players set @s CAP_GLOBE_L6 1
scoreboard players add @s CAP_GLOBE_DONE 1
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Lanterne 6 du Globe]","color":"yellow"},{"text":" : Vous réglez la lanterne 6. Sa flamme se clarifie et rejoint le rythme du socle.","color":"white"}]
execute if score @s CAP_GLOBE_DONE matches ..7 run function capitale:quest/divers/lampe_manquante/globe/progress_self
execute if score @s CAP_GLOBE_DONE matches 8.. run function capitale:quest/divers/lampe_manquante/globe/all_recalibrated_self
