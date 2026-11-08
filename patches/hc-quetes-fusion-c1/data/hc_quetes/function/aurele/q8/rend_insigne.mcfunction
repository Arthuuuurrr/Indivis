# Rend l'insigne d'Orrin Vasca quand il manque. Deux appelants, tous deux des ÉVÉNEMENTS :
#  · une mort de Vasca postérieure à la première (aurele/q8/kill_vasca) ;
#  · la Mère Aveline, quand le joueur arrive les mains vides alors que Vasca est bien mort
#    (pnj/aveline/interact).
# Jamais depuis l'horloge : avec un sac plein, « give » jette l'objet au sol, et une horloge
# jetterait un insigne aux pieds du joueur toutes les cinq secondes.
execute if items entity @s container.* *[custom_data~{hc_quete:"arc_common_q8_insigne"}] run return 0
# Plus personne n'en a besoin : la quête d'Aveline est terminée.
execute if predicate hc_quetes:migration/state/arc_m/8_to_ run return 0
function hc_quetes:objets/q8_insigne
# Sac plein : « give » a jeté l'insigne aux pieds du joueur. Le dire, sinon le symptôme est muet
# — c'est exactement le défaut qui avait cassé la remise à Hector Valmont.
execute unless items entity @s container.* *[custom_data~{hc_quete:"arc_common_q8_insigne"}] run return run function hc_quetes:lib/note {t:"Votre sac est plein : l'insigne d'Orrin Vasca est tombé à vos pieds. Faites de la place et ramassez-le."}
function hc_quetes:lib/note {t:"Vous reprenez l'insigne d'Orrin Vasca sur le corps : or mince sur une plaque de fer, un couperet sur une vague."}
