function hc_quetes:migration/ensure/arc_m
scoreboard players enable @s hcq_choix
execute if predicate hc_quetes:migration/state/arc_m/5 run return run function hc_quetes:pnj/aveline/premier
# Filet de secours de l'insigne : le joueur a tué Vasca (hcq_a>=6) mais n'a plus l'insigne —
# perdu, jeté au sol par un sac plein, ou mort avant de l'avoir ramassé. On le lui rend ici, avant
# le test ci-dessous : « give » agit dans le même tick, donc le pardon s'enchaîne aussitôt.
execute if predicate hc_quetes:migration/state/arc_m/6 if predicate hc_quetes:migration/state/arc_a/6_to_ run function hc_quetes:aurele/q8/rend_insigne
execute if predicate hc_quetes:migration/state/arc_m/6 if items entity @s container.* *[custom_data~{hc_quete:"arc_common_q8_insigne"}] run return run function hc_quetes:pnj/aveline/pardon
execute if predicate hc_quetes:migration/state/arc_m/6 run return run function hc_quetes:lib/dire {pnj:"La Mère Aveline",t:"Tu reviens avec les mains vides et la même bouche. Les mots des citadins sont légers : vous dites, et le feu continue derrière vous. Apporte-moi quelque chose de lourd qui appartenait à leur chef — quelque chose qu'il portait sur lui, pas une promesse — et je rappellerai mes bêtes."}
execute if predicate hc_quetes:migration/state/arc_m/7 run return run function hc_quetes:lib/dire {pnj:"La Mère Aveline",t:"Les mots sont finis. Ce qui te répondra maintenant, ce n'est plus une vieille femme : c'est la forêt. Derrière la cabane, dans la clairière. Je n'y serai pas sous cette figure-là."}
execute if predicate hc_quetes:migration/state/arc_m/8_to_ run return run function hc_quetes:lib/dire {pnj:"La Mère Aveline",t:"La fille Ravenel fait les nœuds trop serrés. Son père aussi, et je ne le lui ai jamais dit en face : il repartait toujours avant que j'aie fini ma phrase. Dis-le-lui encore, de ma part, jusqu'à ce qu'elle desserre."}
function hc_quetes:lib/dire {pnj:"La Mère Aveline",t:"Un citadin. Avec du fer. Ils viennent tous avec du fer, maintenant, et ils appellent ça se protéger. Je n'ai rien à vendre, rien à soigner et rien à te dire. Va-t'en pendant que mes bêtes ont encore le ventre plein."}
