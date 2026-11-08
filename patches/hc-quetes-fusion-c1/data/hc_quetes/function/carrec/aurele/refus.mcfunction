# Le refus laisse une trace : hcq_n = -1 veut dire « offre declinee ». Sans cette ligne, refuser
# n ecrivait aucun score, et le « ! » au-dessus d Aurele revenait pour toujours — c est le defaut
# signale en jeu. L offre reste proposable : les branches du routeur acceptent -1..0.
scoreboard players set @s hcf_cqvcbb01_ref 1
function hc_quetes:lib/dire {pnj:"Aurèle Bonnaire",t:"Alors je compte les jours sans barque. Cinq, demain. Je vous donnerai le chiffre chaque fois que vous passerez devant le comptoir ; c'est un défaut, et il n'y a pas de remède."}
function hc_quetes:lib/dire {pnj:"Aurèle Bonnaire",t:"Ne vous excusez pas, je vous prie. Vous avez rendu un frère à cette maison ; je ne fais pas payer deux fois la même reconnaissance. L'offre reste au comptoir, elle ne s'abîme pas. Allez manger chez Rosalinde — dites-lui que c'est sur mon ardoise, et dites-lui bien que je relirai l'ardoise."}
