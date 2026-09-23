execute if score @s QUEST_DIVERS_LAMPE matches 20 run function capitale:quest/divers/lampe_manquante/veilleuse/complete_commit_self
execute unless score @s QUEST_DIVERS_LAMPE matches 20 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DIVERS_LAMPE matches 20 run tellraw @s [{"text":"[Veilleuse des Profondeurs]","color":"yellow"},{"text":" : Le Globe n’est pas encore juste. Revenez lorsque les huit lanternes du socle auront été recalibrées.","color":"white"}]
