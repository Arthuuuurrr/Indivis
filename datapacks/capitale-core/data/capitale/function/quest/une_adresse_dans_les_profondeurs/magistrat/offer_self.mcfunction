scoreboard players set @s QUEST_RESIDENCE_PROF 1
function capitale:quest/set_active/residence_prof_self
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Vous cherchez à vous établir ? Ici, un nom ne suffit pas. Il faut une adresse réelle, même modeste.","color":"white"}]
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Magistrat des Profondeurs]","color":"yellow"},{"text":" : Louez d’abord une chambre à l’auberge. Revenez ensuite : l’inscription résidentielle coûte cent Martins d’Or.","color":"white"}]
function capitale:quest/une_adresse_dans_les_profondeurs/magistrat/open_choices_self
