
function capitale:player/ensure_runtime_self
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 20 run function capitale:quest/divers/acte_dette/lucain/discover_self
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 30 run function capitale:quest/divers/acte_dette/lucain/reissue_or_wait_self
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 40 run function capitale:quest/divers/acte_dette/lucain/open_return_choices_self
execute unless score @s QUEST_DIVERS_ACTE_DETTE matches 20..40 if score @s CAP_DETTE_CHOIX matches 0 run function capitale:dialogue/sound/parole_quete_self
execute unless score @s QUEST_DIVERS_ACTE_DETTE matches 20..40 if score @s CAP_DETTE_CHOIX matches 0 run tellraw @s [{"text":"[Maître Lucain Perrin]","color":"yellow"},{"text":" : Si vous cherchez un ressort ou une roue dentée, parlez. Si vous cherchez autre chose, regardez au moins où vous mettez les pieds.","color":"white"}]
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 100.. if score @s CAP_DETTE_CHOIX matches 1 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 100.. if score @s CAP_DETTE_CHOIX matches 1 run tellraw @s [{"text":"[Maître Lucain Perrin]","color":"yellow"},{"text":" : Vous avez remis l’acte à ceux qui savent faire passer l’encre pour de la vérité. J’espère que votre bourse compense ce goût-là.","color":"white"}]
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 100.. if score @s CAP_DETTE_CHOIX matches 2 run function capitale:dialogue/sound/parole_quete_self
execute if score @s QUEST_DIVERS_ACTE_DETTE matches 100.. if score @s CAP_DETTE_CHOIX matches 2 run tellraw @s [{"text":"[Maître Lucain Perrin]","color":"yellow"},{"text":" : Mon atelier tient encore. Cela ne répare pas tout, mais parfois préserver une porte suffit pour sauver une maison.","color":"white"}]
