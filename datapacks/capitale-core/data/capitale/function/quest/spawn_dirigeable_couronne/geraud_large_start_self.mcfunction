
scoreboard players set @s QUEST_SPAWN 10
scoreboard players set @s QUEST_PROLOGUE 10
scoreboard players set @s CAP_QUETEACTIVE 1
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Quartier-maître Géraud Rivet]","color":"yellow"},{"text":" : Vous voilà sur vos jambes. Lorsque nous vous avons recueilli, j’aurais parié moins volontiers sur votre réveil.","color":"white"}]
scoreboard players set @s CAP_QSEQ 101
scoreboard players set @s CAP_QSEQ_TIMER 20
