# PNJ Easy NPC Aurele Bonnaire (ON_INTERACTION, executeAsUser=true)
function hc_quetes:migration/ensure/arc_a
function hc_quetes:migration/ensure/arc_n
scoreboard players enable @s hcq_choix
# Q17 : La Bete blanche du Couperet (donnee par Aurele apres La route rouverte)
execute if predicate hc_quetes:migration/state/arc_a/10_to_ if predicate hc_quetes:migration/state/arc_n/neg1_to_0 run return run function hc_quetes:carrec/aurele/intro
execute if predicate hc_quetes:migration/state/arc_n/1_to_8 run return run function hc_quetes:carrec/aurele/en_cours
execute if predicate hc_quetes:migration/state/arc_n/20_to_22 run return run function hc_quetes:carrec/aurele/etat_b
execute if predicate hc_quetes:migration/state/arc_n/23 run return run function hc_quetes:carrec/aurele/b_retour
execute if predicate hc_quetes:migration/state/arc_n/40_to_41 run return run function hc_quetes:carrec/aurele/etat_a
execute if predicate hc_quetes:migration/state/arc_n/42 run return run function hc_quetes:carrec/aurele/a_retour
execute if predicate hc_quetes:migration/state/arc_n/60_to_63 run return run function hc_quetes:carrec/aurele/etat_c
execute if predicate hc_quetes:migration/state/arc_n/64 run return run function hc_quetes:carrec/aurele/c_retour
execute if predicate hc_quetes:migration/state/arc_n/30 run return run function hc_quetes:carrec/aurele/fini
execute if predicate hc_quetes:migration/state/arc_n/50 run return run function hc_quetes:carrec/aurele/fini
execute if predicate hc_quetes:migration/state/arc_n/70 run return run function hc_quetes:carrec/aurele/fini
execute if predicate hc_quetes:migration/state/arc_a/0 run function hc_quetes:aurele/q6/intro
execute if predicate hc_quetes:migration/state/arc_a/1_to_2 run function hc_quetes:aurele/q6/en_cours
execute if predicate hc_quetes:migration/state/arc_a/3 run function hc_quetes:aurele/q6/retour
execute if predicate hc_quetes:migration/state/arc_a/4 run function hc_quetes:aurele/q8/intro
execute if predicate hc_quetes:migration/state/arc_a/5 run function hc_quetes:aurele/q8/en_cours
execute if predicate hc_quetes:migration/state/arc_a/6 run function hc_quetes:aurele/q8/retour
execute if predicate hc_quetes:migration/state/arc_a/7 run function hc_quetes:aurele/q12/intro
execute if predicate hc_quetes:migration/state/arc_a/8 run function hc_quetes:aurele/q12/en_cours
execute if predicate hc_quetes:migration/state/arc_a/9 run function hc_quetes:aurele/q12/retour
execute if predicate hc_quetes:migration/state/arc_a/10_to_ run function hc_quetes:lib/dire {pnj:"Aurèle Bonnaire",t:"La route est rouverte, la cloche du comptoir sonne pour des caisses de bandes, et mon frère compte les marches dans la cour. J'ai repassé quarante sacs à la balance jurée ce matin : quarante. Déchargement, enregistrement, vente — trois moments, trois droits, et aucun des trois ne m'a volé cette fois. Je n'ai plus rien à vous demander. Mordez la pièce de temps en temps."}
