
function capitale:dialogue/sound/parole_quete_self
tellraw @s [{"text":"[Magistrat]","color":"yellow"},{"text":" : Votre dossier de Résident est en règle. Pour être reconnu Citoyen de la Capitale, les droits civiques s’élèvent à cinq cents Martins d’Or.","color":"white"}]
scoreboard players enable @s QuestChoix
tellraw @s [{"text":"[Choix] ","color":"gray"},{"text":"[Déposer 500 Martins d’Or.]","color":"green","click_event":{"action":"run_command","command":"/trigger QuestChoix set 230"},"hover_event":{"action":"show_text","value":"Régler les droits de citoyenneté."}},{"text":"   ","color":"gray"},{"text":"[Je reviendrai avec la somme.]","color":"yellow","click_event":{"action":"run_command","command":"/trigger QuestChoix set 231"},"hover_event":{"action":"show_text","value":"Reporter le paiement."}}]
