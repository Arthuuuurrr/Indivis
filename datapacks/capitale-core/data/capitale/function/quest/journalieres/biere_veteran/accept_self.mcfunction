function capitale:quest/dialogue/clear_self
scoreboard players set @s QUEST_DAILY_BEER_GC 10
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Vétéran de la Garde du Cœur]","color":"yellow"},{"text":" : Je savais que vous aviez l’allure de quelqu’un de raisonnable. Une vraie Bière, hein. Pas une flaque tiède dans un verre propre.","color":"white"}]
function capitale:quest/objective/acheter_biere_veteran_self
