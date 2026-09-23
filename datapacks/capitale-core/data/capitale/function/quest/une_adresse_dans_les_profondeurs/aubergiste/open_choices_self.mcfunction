function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Aubergiste des Profondeurs]","color":"yellow"},{"text":" : Une chambre simple coûte vingt Martins d’Or. Rien de luxueux, mais la porte ferme et le lit ne mord pas.","color":"white"}]
function capitale:quest/dialogue/clear_self
scoreboard players set @s CAP_QDIALOG_OWNER 7
scoreboard players set @s CAP_QDIALOG_KEEP 1
function capitale:quest/dialogue/anchor/create_self
function capitale:quest/une_adresse_dans_les_profondeurs/aubergiste/show_choices_self
