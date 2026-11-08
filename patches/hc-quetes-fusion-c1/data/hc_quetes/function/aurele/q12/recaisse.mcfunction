# DÉFAUT REMONTÉ EN JEU : « on ne peut pas dépenser l'or du sac à dos (ça marche pour les sorts
# qui consomment des runes dans le sac) ».
#
# Cause mesurée : « items entity @s container.* » ne voit que l'inventaire principal du joueur.
# L'intérieur d'un sac à dos est un composant que le datapack ne sait ni lire ni vider. Les runes
# font exception parce que Spell Engine lit le conteneur en Java, pas en commande — c'est pour ça
# que le joueur voit les deux se comporter différemment, et il a raison.
# Rien dans un datapack ne peut retirer une pièce du fond d'un sac. Ce qui était réparable, et
# qui était le vrai problème, c'est le mur muet : Aurèle répondait « revenez avec dix Martins » à
# un joueur qui les avait sous les yeux, dans son sac. Il le dit maintenant.
#
# Second défaut trouvé dans la même ligne : elle testait la PRÉSENCE d'un Martin, pas dix. Avec
# trois Martins en poche, le test passait, le « clear 10 » n'en retirait que trois, et la caisse
# était livrée pour trois. On compte maintenant pour de vrai : « clear … 0 » compte sans retirer.
execute store result score #or hcm_tick run clear @s capitale_currency:martin_dor 0
execute if score #or hcm_tick matches 10.. run return run function hc_quetes:aurele/q12/recaisse_paye
# Rien du tout en poche : le refus d'origine, inchangé.
execute if score #or hcm_tick matches 0 run return run function hc_quetes:lib/dire {pnj:"Aurèle Bonnaire",t:"Vous avez perdu la caisse. Ça arrive, et ça se paie : j'en ai une autre en réserve, contre dix Martins. Le verre vient de loin et personne ne me le remplace. Revenez avec dix Martins et nous n'en reparlerons plus."}
# Entre 1 et 9 : on dit le compte, et on dit où regarder. C'est la ligne qui répond au défaut.
function hc_quetes:lib/dire {pnj:"Aurèle Bonnaire",t:"Ce n'est pas dix. Je compte ce que vous avez sur vous, pas ce qui dort au fond d'un sac : si votre or est dans votre sac à dos, sortez-le et reposez-le-moi sur le comptoir. Le verre coûte dix Martins, et il les coûte aussi le mardi."}
function hc_quetes:lib/note {t:"Martins comptés dans votre inventaire : il en faut dix. Les Martins rangés dans un sac à dos ne sont pas comptés — sortez-les du sac."}
