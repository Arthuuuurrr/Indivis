# L'ordre a été inversé, et c'est la correction d'un défaut signalé en jeu : « la remise à Hector
# Valmont est impossible ». L'étape passait à 6 AVANT que l'insigne soit donné (il l'était deux
# lignes plus bas). Avec un sac plein, l'objet n'entrait jamais dans l'inventaire — et le bouton de
# remise, lui, exige l'insigne : il s'affichait et ne faisait rien, sans un mot d'explication.
# Maintenant : on donne, on vérifie que c'est bien entré, et l'étape n'avance qu'après.
function hc_quetes:objets/q5_insigne
execute unless items entity @s container.* *[custom_data~{hc_quete:"sqvccr02_insigne"}] run return run function hc_quetes:lib/note {t:"Votre sac est plein : l'insigne est tombé à vos pieds. Faites de la place, ramassez-le, puis touchez le corps à nouveau."}
function hc_quetes:migration/write/arc_h/v6
scoreboard players set @s hcq_sqvccr02_o2 4
function hc_quetes:lib/note {t:"Erwann Brieuc, vingt ans, 3e patrouille. Son nom est frappé à la pointe sous le col de son tabard, comme on apprend à le faire le premier jour. Une note pliée serrée dans le poing droit, l'insigne encore accroché à la ceinture : les autres sont partis sans y toucher."}
function hc_quetes:objets/q5_note
function hc_quetes:lib/objectif {t:"Rapporter le registre et l'insigne à Hector."}
