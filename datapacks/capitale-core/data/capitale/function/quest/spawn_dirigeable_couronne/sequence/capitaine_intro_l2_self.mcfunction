
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Capitaine Althéon Brumeforge]","color":"yellow"},{"text":" : Vous n’aviez ni escorte, ni insigne, ni nom que nous puissions saisir sur le moment. Le plus juste était de vous prendre à bord.","color":"white"}]
scoreboard players set @s CAP_QSEQ 202
scoreboard players set @s CAP_QSEQ_TIMER 20
