function capitale:quest/dialogue/clear_self
scoreboard players set @s CAP_BEER_STORY_HEARD 1
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Vétéran de la Garde du Cœur]","color":"yellow"},{"text":" : En 892, le Cœur ne grondait pas comme dans les livres. Il respirait. Les jeunes couraient vers les portes, les anciens restaient près des leviers, et personne ne savait si l’ordre suivant sauverait la ville ou l’ouvrirait en deux.","color":"white"}]
tellraw @s [{"text":"[Vétéran de la Garde du Cœur]","color":"yellow"},{"text":" : Voilà pourquoi je bois lentement. Les nuits qui tiennent debout méritent qu’on n’oublie pas leur goût.","color":"white"}]
