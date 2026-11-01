# Marqueur de aurele : 0 = rien, 1 = ! (quete a prendre), 2 = ? (etape ou fin a rendre).
# Genere depuis aurele/interact — meme ordre et memes conditions que le dialogue. Ne pas editer.
scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_a/10_to_ if predicate hc_quetes:migration/state/arc_n/0 unless score @s hcf_cqvcbb01_ref matches 1 run return run scoreboard players set @s hcq_mrk_aurele 1
execute if predicate hc_quetes:migration/state/arc_n/1 run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_n/2 run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_n/3_to_4 run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_n/5 run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_n/6_to_7 run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_n/1_to_8 run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_n/20_to_22 run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_n/23 unless items entity @s container.* *[custom_data~{hc_quete:"cqvcbb01_cote"}] run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_n/23 run return run scoreboard players set @s hcq_mrk_aurele 2
execute if predicate hc_quetes:migration/state/arc_n/40_to_41 run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_n/42 unless items entity @s container.* *[custom_data~{hc_quete:"cqvcbb01_cote"}] run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_n/42 unless items entity @s container.* *[custom_data~{hc_quete:"cqvcbb01_coffre"}] run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_n/42 run return run scoreboard players set @s hcq_mrk_aurele 2
execute if predicate hc_quetes:migration/state/arc_n/60_to_63 run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_n/64 unless items entity @s container.* *[custom_data~{hc_quete:"cqvcbb01_cote"}] run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_n/64 run return run scoreboard players set @s hcq_mrk_aurele 2
execute if predicate hc_quetes:migration/state/arc_n/30 run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_n/50 run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_n/70 run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_a/0 run return run scoreboard players set @s hcq_mrk_aurele 1
execute if predicate hc_quetes:migration/state/arc_a/1 run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_a/3 unless items entity @s container.* *[custom_data~{hc_quete:"sqvcep01_manifeste"}] run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_a/3 run return run scoreboard players set @s hcq_mrk_aurele 2
execute if predicate hc_quetes:migration/state/arc_a/4 unless predicate hc_quetes:migration/state/arc_h/7_to_ unless predicate hc_quetes:migration/state/sqvcso01/3_to_ run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_a/4 run return run scoreboard players set @s hcq_mrk_aurele 1
execute if predicate hc_quetes:migration/state/arc_a/5 if score @s hcq_cqvcep02_o1 matches 1 run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_a/6 if score @s hcq_cqvcep02_o1 matches 0 run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_a/6 unless items entity @s container.* *[custom_data~{hc_quete:"cqvcep02_boussole"}] run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_a/6 run return run scoreboard players set @s hcq_mrk_aurele 2
execute if predicate hc_quetes:migration/state/arc_a/7 unless predicate hc_quetes:migration/state/arc_h/15_to_ run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_a/7 run return run scoreboard players set @s hcq_mrk_aurele 1
execute if predicate hc_quetes:migration/state/arc_a/8 unless items entity @s container.* *[custom_data~{hc_quete:"sqvcep03_caisse"}] unless items entity @s container.* capitale_currency:martin_dor run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_a/8 unless items entity @s container.* *[custom_data~{hc_quete:"sqvcep03_caisse"}] run return run scoreboard players set @s hcq_mrk_aurele 0
execute if predicate hc_quetes:migration/state/arc_a/9 run return run scoreboard players set @s hcq_mrk_aurele 2
