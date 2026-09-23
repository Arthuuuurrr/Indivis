function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Roch Vallet]","color":"yellow"},{"text":" : Parlez-moi avant de descendre plus bas. Les Profondeurs se comprennent mieux avec un guide qu’avec de bonnes intentions.","color":"white"}]
scoreboard players set @s CAP_QSEQ 0
scoreboard players set @s CAP_QSEQ_TIMER 0
