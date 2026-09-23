function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Roland]","color":"aqua"},{"text":" : Un point de ronde, une dispute, un marchand inquiet. Tu observes, tu interviens si nécessaire, et tu reviens vers moi avant d’agir trop vite.","color":"white"}]
scoreboard players enable @s QuestChoix
