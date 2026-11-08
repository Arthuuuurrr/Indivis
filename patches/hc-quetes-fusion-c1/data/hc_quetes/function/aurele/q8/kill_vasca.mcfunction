# DÉFAUT REMONTÉ EN JEU : « la quête la mère Aveline compliquée à finir en prenant l'insigne
# d'Orrin Vasca, car celui-ci ne drop pas son insigne à chaque fois, obligé d'attendre et de le
# re-tuer ».
#
# Deux verrous mesurés, et le second est sans issue :
#  1. l'horloge n'appelait ce fichier que pour « hcq_cqvcep02=5 » exactement. Vasca est un point
#     permanent : on peut le tuer AVANT d'accepter la quête. Dans ce cas rien ne se passe, et le
#     compteur de kill est purgé au tick suivant — d'où le « obligé de le re-tuer ».
#  2. après une première conversion réussie l'étape vaut 6, donc le re-tuer ne redonnait plus
#     RIEN. Si l'insigne avait été perdu entre-temps — et avec un sac plein « give » le jette au
#     sol sans un mot — il était perdu pour toujours, et la quête de Maëlle bloquée sans recours.
#
# Corrigé : l'horloge appelle maintenant pour « hcq_cqvcep02=5..6 », et ce fichier est rejouable. Les
# objets uniques ne sont donnés qu'à la première mort ; l'insigne, lui, est rendu à chaque mort
# tant que Maëlle peut encore en avoir besoin.
scoreboard players set @s hcc_vasca 0
# Deuxième mort ou plus : seul l'insigne est repris, et seulement s'il manque.
execute if predicate hc_quetes:migration/state/arc_a/6_to_ run return run function hc_quetes:aurele/q8/rend_insigne
function hc_quetes:migration/write/arc_a/v6
function hc_quetes:objets/q8_boussole
function hc_quetes:objets/q8_insigne
function hc_quetes:objets/q8_ordre
function hc_quetes:objets/q8_cle_bronze
function hc_quetes:lib/barre {t:"Orrin Vasca est tombé. Sur lui : une boussole de cuivre, un ordre signé, une clé de bronze."}
