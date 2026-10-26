# Points d'exclamation du core — QMARK1

## Comportement

- « ! » jaune : une offre est accessible au personnage sélectionné. Il disparaît après acceptation, quand le délai d'une journalière est actif ou qu'un prérequis n'est plus rempli.
- Les étapes récentes et les offres déjà ouvertes sont évaluées via les mêmes prédicats `hc_quetes:migration/state/...` que les interactions du core. Les nombres d'étape historiques ne sont pas recopiés dans les scoreboards actuels.
- Prise en compte des quêtes précédentes, du rang exigé, du blocage pendant une escorte lorsque l'acceptation l'applique, et de la quête active lorsque le core l'exige.
- Elias et Roland : l'interaction de livraison prioritaire masque la nouvelle offre correspondante. Magistrat : résidence et citoyenneté partagent un seul repère. Maelor : les quatre offres successives partagent aussi un repère.
- Le repère est privé au compte Minecraft **et au personnage Nexus**. Les marqueurs « ! » et « ? » déjà installés pour la séquence I sont conservés.
- La recherche est limitée à 24 blocs autour du joueur et aux entités EasyNPC publiques. Aucun PNJ de décor n'est déplacé ; aucun dialogue, rang, accès ou récompense n'est déclenché par le repère.

## Reconnaissance des PNJ

Les tags de rôle existants sont prioritaires, puis les noms connus du core sont utilisés. Le catalogue JSON joint donne tous les noms et tags reconnus. Les installations utilisant ces noms/tags fonctionnent sans commande supplémentaire.

Pour un PNJ renommé, se placer à moins de **quatre blocs**, plus près de lui que de tout autre EasyNPC public, puis exécuter uniquement la commande de sa ligne. Elle ajoute un tag de repère ; elle ne modifie ni les actions EasyNPC, ni le nom, ni l'apparence, ni les points du prologue. Le mot `liaison` désigne ici seulement l'association du repère au donneur.

| Donneur | Quêtes du core | Commande facultative pour un PNJ renommé |
|---|---|---|
| Géraud Rivet | PQ01 | `/function capitale:quest/markers/liaison/geraud` |
| Léovic | PQ02 | `/function capitale:quest/markers/liaison/leovic` |
| Aurèle Veyrane | PQ03 | `/function capitale:quest/markers/liaison/aurele` |
| Roch Vallet | PQ04 | `/function capitale:quest/markers/liaison/roch` |
| Magistrat des Profondeurs | PQ05, SQHCCI01 | `/function capitale:quest/markers/liaison/magistrat` |
| Mira | SQBAOB01 | `/function capitale:quest/markers/liaison/mira` |
| Elias Ferbois | SQBAOB02 | `/function capitale:quest/markers/liaison/elias` |
| Lysandre | SQBAOB03 | `/function capitale:quest/markers/liaison/lysandre` |
| Roland | SQBAOB04 | `/function capitale:quest/markers/liaison/roland` |
| Odon Varenne | SQHCSO01 | `/function capitale:quest/markers/liaison/odon` |
| Libraire agréé | SQHCSO02 | `/function capitale:quest/markers/liaison/libraire` |
| Aldren le Traqueur | SQHCSO03 | `/function capitale:quest/markers/liaison/aldren` |
| Veilleuse des Profondeurs | RQHC01 | `/function capitale:quest/markers/liaison/veilleuse` |
| Technicienne du Cœur | RQHC02 | `/function capitale:quest/markers/liaison/technicienne` |
| Employé de la Banque | DQHC01 | `/function capitale:quest/markers/liaison/employe_banque` |
| Vétéran de la Garde du Cœur | DQHC02 | `/function capitale:quest/markers/liaison/veteran` |
| Commis du Port | DQHC03 | `/function capitale:quest/markers/liaison/commis_port` |
| Commis des Messageries | DQHC04 | `/function capitale:quest/markers/liaison/commis_messageries` |
| Greffier des Quais | DQHC05 | `/function capitale:quest/markers/liaison/greffier` |
| Maelor Veyne | SQHCVN01, SQHCVN02, SQHCVN03, SQHCVN04 | `/function capitale:quest/markers/liaison/maelor` |

Pour retirer une association facultative, se placer près du même PNJ et employer `/function capitale:quest/markers/liberer/<identifiant>`, par exemple `/function capitale:quest/markers/liberer/leovic`. La résolution automatique par nom ou ancien tag reste disponible. Les commandes administrateur se lancent comme joueur ; elles ne sélectionnent pas un joueur proche via `@p`.

## Charge et nettoyage

Les mises à jour réutilisent le même `text_display` tant que le donneur reste disponible. Sa position n'est actualisée que s'il s'est déplacé de plus de 0,05 bloc. Il n'y a ni copie de PNJ, ni lecture complète du NBT des PNJ en boucle, ni nouveau rafraîchissement global de visibilité. Les autres joueurs ne reçoivent pas ces repères privés.

Les repères devenus inutiles ou éloignés sont supprimés dans la dimension courante. Le nettoyage privé historique traite les changements de personnage et déconnexions ; les éventuels repères d'une autre dimension restent réutilisables au retour. Un personnage possède au plus un repère par donneur et par dimension chargée.

## Recette sur la carte

1. Avec un personnage éligible, approcher un donneur nommé comme dans le catalogue : un « ! » jaune apparaît. Accepter la quête : il disparaît à la mise à jour suivante.
2. Pour une journalière, vérifier l'absence pendant le délai, puis sa disponibilité lorsque l'offre du core redevient accessible.
3. Tester deux joueurs au même PNJ, dont un a déjà accepté la quête : chacun doit voir uniquement ses propres offres.
4. Changer de personnage Nexus : aucune offre du personnage précédent ne doit rester visible. Tester ensuite déconnexion et reconnexion.
5. Pour le prologue, poursuivre les dialogues et déplacements habituels : les fichiers de scène HF9 et le journal HF11 sont inchangés.

## Retour aux datapacks précédents

Avant de retirer QMARK1, un admin peut enlever ses seuls repères visuels dans chaque dimension concernée : `/kill @e[type=minecraft:text_display,tag=cap_qmark]`. Arrêter ensuite le serveur et remettre le core HF9 et `hc_quetes` HF4 précédents. Conserver le journal HF11. Les éventuels tags facultatifs `cap_qgiver_...` sont inertes sans QMARK1.
