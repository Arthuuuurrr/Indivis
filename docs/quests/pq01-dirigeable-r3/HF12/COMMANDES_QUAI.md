# Commandes des six PNJ au quai — HF12

Les noms des commandes ne changent pas. La mise à jour corrige leur reconnaissance des anciennes actions EasyNPC.

Se placer à moins de trois blocs du PNJ voulu, plus près de lui que des autres EasyNPC d'au moins 0,5 bloc. Exécuter seulement sa ligne ; une liaison déjà réussie n'est pas à refaire.

| PNJ | Commande à proximité du PNJ voulu |
|---|---|
| Géraud Rivet | `/function capitale:pq01/liaison/geraud_quai` |
| Althéon Brumeforge | `/function capitale:pq01/liaison/altheon_quai` |
| Gautier — matelot 01 | `/function capitale:pq01/liaison/gautier_quai` |
| Hugues — matelot 02 | `/function capitale:pq01/liaison/hugues_quai` |
| Anselme — matelot 03 | `/function capitale:pq01/liaison/anselme_quai` |
| Perrin — matelot 04 | `/function capitale:pq01/liaison/perrin_quai` |

Dans la capture du 26 octobre, les anciennes actions `capitaine_quai/interact_self` et `matelot_01_self` étaient rejetées : HF12 les reconnaît. Une autre tentative d'`altheon_quai` ciblait `matelot_03_quai_self`, qui appartient à **Anselme** : utiliser sa ligne, ou se rapprocher du véritable capitaine. Le refus d'un autre rôle est volontairement conservé.

Les liaisons servent à associer un PNJ ; elles ne le déplacent pas. Les commandes de pose et des points de marche sont détaillées dans [le guide complet](INSTALLATION_ET_POSE_PNJ.md). Les gardes, liaisons de la Séquence I et points déjà enregistrés restent réutilisés.
