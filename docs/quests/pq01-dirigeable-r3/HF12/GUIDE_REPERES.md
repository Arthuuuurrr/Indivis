# Marqueurs personnels du core — QMARK2

Un « ! » jaune apparaît lorsqu'une offre est accessible au personnage Nexus sélectionné. Les 24 quêtes du core et leurs 20 donneurs sont couverts. Les marqueurs existants de la Séquence I sont conservés.

## Actualisation et reconnaissance

Le journal HF12 cherche les EasyNPC publics dans un rayon de 24 blocs, une fois toutes les 20 ticks serveur. Il ne dépend plus du déclenchement de `hc_quetes:runtime/second`. Les types humanoïdes et villageois sont couverts.

Une association manuelle est prioritaire. Sinon les actions d'interaction connues du core sont examinées, puis les anciens tags de rôle et le nom normalisé. Les couleurs Minecraft, espaces et casse du nom sont normalisés. Les actions ne sont pas exécutées pour identifier le donneur. Une action ambiguë n'est pas attribuée automatiquement. La lecture du jeu d'actions est mise en cache dix secondes ; le NBT complet de l'entité n'est pas lu en boucle.

Le marqueur est à 3,05 blocs au-dessus des pieds, avec luminosité propre et texte visible à travers les obstacles, pour éviter de se confondre avec le nom ou l'ombre d'un auvent. Il réutilise son UUID, même après une actualisation ou une migration QMARK1. Il reste privé au compte **et au personnage Nexus** ; les autres joueurs n'en reçoivent pas les apparitions.

Les règles des offres restent celles de QMARK1 : états de migration, rang, délais, quête active et blocage d'escorte lorsque l'acceptation les applique. Le Greffier peut prononcer son accueil alors que l'acceptation est bloquée par une escorte : entendre l'accueil ne suffit donc pas à établir l'éligibilité. Le repère n'accepte ni ne fait avancer de quête.

## Diagnostic en cas d'absence

Avec un personnage sélectionné, près du donneur, lancer comme administrateur :

```mcfunction
/indivispq01 diagnostic_reperes
```

Le diagnostic indique si les fonctions QMARK2 sont chargées, le personnage/verrou de profil, l'état et le délai du Greffier, le blocage d'escorte, puis les trois EasyNPC les plus proches avec leur type, rôle reconnu et disponibilité d'offre. Il écrit aussi une ligne dédiée dans le log serveur. Il ne modifie pas la progression.

## Donneur renommé

Si le donneur a un nom et une action entièrement personnalisés, se placer à moins de quatre blocs et plus près de lui que de tout autre EasyNPC public, puis utiliser sa ligne. Cette commande associe seulement son repère ; elle ne change ni son nom, ni son apparence, ni son dialogue, ni ses points de déplacement.

| Donneur | Quêtes | Association facultative du marqueur |
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

Pour libérer une association manuelle, utiliser le même identifiant après `capitale:quest/markers/liberer/`, par exemple `/function capitale:quest/markers/liberer/greffier`. La reconnaissance automatique par actions, tags ou nom reste active.

## Charge et nettoyage

Les repères immobiles sont réutilisés sans réapparition réseau. Un déplacement du donneur entraîne une mise à jour de position si l'écart dépasse 0,05 bloc. Aucun nouvel appel global de visibilité n'est ajouté. Les repères devenus inutiles ou éloignés sont supprimés dans la dimension courante ; les règles privées historiques traitent le changement de personnage et la déconnexion. Les éventuels repères dans une autre dimension restent réutilisables au retour.

Pour revenir aux versions précédentes, arrêter le serveur puis remettre ensemble HF11 et les deux datapacks QMARK1 (ou les trois composants antérieurs). Les tags automatiques du marqueur sont inertes sans le module. Un admin peut retirer les seuls affichages QMARK dans chaque dimension concernée avec `/kill @e[type=minecraft:text_display,tag=cap_qmark]`.
