function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Lysandre]","color":"aqua"},{"text":" : Un colis à Elias, un registre au greffier, puis une dernière remise selon ce que le quartier pense déjà de toi. Rien de compliqué, si tu sais écouter.","color":"white"}]
scoreboard players enable @s QuestChoix
