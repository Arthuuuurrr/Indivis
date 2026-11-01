# Chaque ligne re-verifie l'etat (et les objets portes) : les messages cliquables restent dans le tchat.
execute if score @s hcq_choix matches 1101 if predicate hc_quetes:migration/state/arc_n/neg1_to_0 if predicate hc_quetes:migration/state/arc_a/10_to_ run function hc_quetes:carrec/aurele/accepter
execute if score @s hcq_choix matches 1102 if predicate hc_quetes:migration/state/arc_n/neg1_to_0 run function hc_quetes:carrec/aurele/refus
execute if score @s hcq_choix matches 1103 if predicate hc_quetes:migration/state/arc_n/neg1_to_0 run function hc_quetes:carrec/aurele/question
execute if score @s hcq_choix matches 1104 if predicate hc_quetes:migration/state/arc_n/42 if items entity @s container.* *[custom_data~{hc_quete:"cqvcbb01_coffre"}] if items entity @s container.* *[custom_data~{hc_quete:"cqvcbb01_cote"}] run function hc_quetes:carrec/aurele/a_recompense
execute if score @s hcq_choix matches 1105 if predicate hc_quetes:migration/state/arc_n/23 if items entity @s container.* *[custom_data~{hc_quete:"cqvcbb01_cote"}] run function hc_quetes:carrec/aurele/b_recompense
execute if score @s hcq_choix matches 1106 if predicate hc_quetes:migration/state/arc_n/64 if items entity @s container.* *[custom_data~{hc_quete:"cqvcbb01_cote"}] run function hc_quetes:carrec/aurele/c_recompense
execute if score @s hcq_choix matches 1111 if predicate hc_quetes:migration/state/arc_n/2 run function hc_quetes:carrec/vannec/r_1111
execute if score @s hcq_choix matches 1112 if predicate hc_quetes:migration/state/arc_n/2 if predicate hc_quetes:migration/state/arc_v/30_to_ run function hc_quetes:carrec/vannec/r_1112
execute if score @s hcq_choix matches 1113 if predicate hc_quetes:migration/state/arc_n/2 run function hc_quetes:carrec/vannec/r_1113
execute if score @s hcq_choix matches 1121 if predicate hc_quetes:migration/state/arc_n/2 run function hc_quetes:carrec/tobie/r_1121
execute if score @s hcq_choix matches 1122 if predicate hc_quetes:migration/state/arc_n/2 run function hc_quetes:carrec/tobie/r_1122
execute if score @s hcq_choix matches 1131 if predicate hc_quetes:migration/state/arc_n/4 run function hc_quetes:carrec/gwenn/r_1131
execute if score @s hcq_choix matches 1132 if predicate hc_quetes:migration/state/arc_n/4_to_6 run function hc_quetes:carrec/gwenn/r_1132
execute if score @s hcq_choix matches 1133 if predicate hc_quetes:migration/state/arc_n/4_to_6 run function hc_quetes:carrec/gwenn/r_1133
execute if score @s hcq_choix matches 1134 if predicate hc_quetes:migration/state/arc_n/7 if items entity @s container.* *[custom_data~{hc_quete:"cqvcbb01_role"}] if items entity @s container.* *[custom_data~{hc_quete:"cqvcbb01_cloche"}] run function hc_quetes:carrec/gwenn/r_1134
execute if score @s hcq_choix matches 1135 if predicate hc_quetes:migration/state/arc_n/7 run function hc_quetes:carrec/gwenn/r_1135
execute if score @s hcq_choix matches 1136 if predicate hc_quetes:migration/state/arc_n/22 if items entity @s container.* *[custom_data~{hc_quete:"arc_common_q17_part"}] run function hc_quetes:carrec/gwenn/r_1136
execute if score @s hcq_choix matches 1141 if predicate hc_quetes:migration/state/arc_n/8 if items entity @s container.* *[custom_data~{hc_quete:"cqvcbb01_coffre"}] if function hc_quetes:carrec/verrou run function hc_quetes:carrec/carn/marche
execute if score @s hcq_choix matches 1141 if predicate hc_quetes:migration/state/arc_n/8 if items entity @s container.* *[custom_data~{hc_quete:"cqvcbb01_coffre"}] unless function hc_quetes:carrec/verrou run function hc_quetes:carrec/carn/verrou_non
execute if score @s hcq_choix matches 1142 if predicate hc_quetes:migration/state/arc_n/8 if function hc_quetes:carrec/verrou run function hc_quetes:carrec/carn/acier
execute if score @s hcq_choix matches 1142 if predicate hc_quetes:migration/state/arc_n/8 unless function hc_quetes:carrec/verrou run function hc_quetes:carrec/carn/verrou_non
execute if score @s hcq_choix matches 1143 if predicate hc_quetes:migration/state/arc_n/8 run function hc_quetes:carrec/carn/garde
execute if score @s hcq_choix matches 1144 if predicate hc_quetes:migration/state/arc_n/8 run function hc_quetes:carrec/carn/malaquin
execute if score @s hcq_choix matches 1146 if predicate hc_quetes:migration/state/arc_n/21 if items entity @s container.* *[custom_data~{hc_quete:"cqvcbb01_coffre"}] run function hc_quetes:carrec/carn/paie_ok
execute if score @s hcq_choix matches 1151 if predicate hc_quetes:migration/state/arc_n/60 run function hc_quetes:carrec/isaure/r_1151
execute if score @s hcq_choix matches 1152 if predicate hc_quetes:migration/state/arc_n/60 run function hc_quetes:carrec/isaure/r_1152
execute if score @s hcq_choix matches 1153 if predicate hc_quetes:migration/state/arc_n/63 run function hc_quetes:carrec/isaure/r_1153
execute if score @s hcq_choix matches 1171 if predicate hc_quetes:migration/state/arc_n/62 if score @s hcq_cqvcbb01_o3 matches 1 if items entity @s container.* *[custom_data~{hc_quete:"cqvcbb01_coffre"}] run function hc_quetes:carrec/rozec/r_1171
