# Installation, configuration et déplacements des PNJ — PQ01-R3-HF7

**Mise à jour du 23 octobre : remplacer le core et le journal par HF7.** Le journal doit être remplacé sur le serveur **et les clients**. Conserver le datapack `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4.zip`. La pose actuelle, les liaisons, points, apparences et progressions sont réutilisés : aucune commande de liaison, placement ou reset à refaire pour installer ce correctif.

L’archive contient uniquement les deux composants modifiés. Les sections de pose ci-dessous servent à une configuration initiale ou à un déplacement volontaire du décor. [Corrections et preuves HF7](../HOTFIX_SCENE_CAPTURE_R3_HF7.md).

## 1. Les trois opérations à distinguer

| Besoin | Exemple exact | Effet |
| --- | --- | --- |
| Affecter un PNJ existant à son rôle | `/function capitale:pq01/liaison/gautier_large` | Choisit l’EasyNPC le plus proche, sauvegarde puis relie son interaction. Aucun déplacement. |
| Déplacer physiquement ce PNJ en édition | `/function capitale:pq01/pose/placer_gautier_large` | Place le PNJ déjà lié à la position et à l’orientation de l’admin. |
| Définir sa destination de marche | `/function capitale:pq01/pose/gautier_approche` | Enregistre le sol où Gautier doit rejoindre Géraud pendant la scène. |
| Ancien point de journal | `/function capitale:pq01/pose/gautier_large` | Enregistre uniquement un waypoint ; ne lie et ne déplace aucun PNJ. |

**Pendant la quête, Gautier marche réellement vers le point d’approche puis vers son poste.** La commande `placer_…` sert à l’édition du décor ; elle n’est jamais appelée par le trajet de scène. L’acteur temporaire est créé à son poste réel et sa navigation doit rejoindre la destination : aucun TP de scène à travers un mur.

## 2. Remplacer les fichiers

| Emplacement | Fichier à installer |
| --- | --- |
| Datapacks du monde serveur | `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF5.zip` |
| Datapacks du monde serveur | `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4.zip` |
| `mods` serveur et clients | `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF7.jar` |

Le ZIP du core est un datapack complet, avec `pack.mcmeta` à sa racine. Le ZIP extérieur de livraison n’est pas un datapack. Le datapack hc_quetes indiqué dans le tableau est celui déjà installé, conservé sans modification.

1. Fermer les dialogues et attendre la fin d’un amarrage éventuel. Sauvegarder ensemble monde et profils Nexus, puis arrêter le serveur.
2. Remplacer l’ancien ZIP du core par le core HF7 dans les datapacks du monde, et l’ancien JAR du journal par le journal HF7 dans `mods`. Conserver hc_quetes HF4.
3. Remplacer aussi le journal sur chaque client. Garder Nexus `PRE34-SI1`, Dialogue `0.1.0+1.21.11.b10-DATAPACK-COMMAND-HOTFIX`, EasyNPC et son interface `7.16.0`, Fabric et les autres composants existants.
4. Redémarrer puis reprendre au clic droit sur le PNJ. Les checkpoints et les sauvegardes de relecture sont conservés. Aucun reset ni commande de pose n’est nécessaire.

Les commandes d’édition des sections suivantes s’exécutent en jeu, avec un personnage Nexus sélectionné et chargé, uniquement si l’on souhaite modifier la configuration.

Le code de scène fonctionne côté serveur ; le JAR journal contient aussi ses composants clients et mixins. Ce correctif s’ajoute au journal fourni, sans créer un second mod portant le même identifiant.

## 3. Préparer l’édition

Suspendre l’ambiance puis nettoyer les acteurs temporaires avant de lier ou déplacer les originaux :

```mcfunction
/data modify storage capitale:pq01_setup ambient_enabled set value 0
/function capitale:pq01/admin/nettoyer_scenes
```

Ces commandes ne réinitialisent aucune quête. Le nettoyage ferme les scènes PQ01 personnelles en cours ; l’effectuer pendant la configuration sur la copie de test du monde. Les liaisons, points et apparences restent enregistrés.

### Baguette d’édition et clic de dialogue

Dans EasyNPC 7.16.0 fourni, le contrôle réseau de l’éditeur accepte le mode créatif ou le propriétaire du PNJ. Il ne donne pas d’exception au statut OP dans cette branche. Pour utiliser la baguette d’édition sur un PNJ dont on n’est pas propriétaire : `/gamemode creative`, effectuer l’édition, puis reprendre son mode de jeu habituel, par exemple `/gamemode adventure`. Les commandes de liaison/pose PQ01 gardent leurs propres permissions administrateur ; elles n’exigent pas de changer le propriétaire EasyNPC.

Pour parler au PNJ, ranger la baguette et les autres outils EasyNPC, puis faire un clic droit de la main principale, sans s’accroupir. Dialogue b10 et EasyNPC réservent le clic avec leurs outils à l’édition. HF4 prépare explicitement la caméra pour l’action native PQ01 après contrôle de la liaison, de la dimension et de la distance. Il respecte les réglages de capture et les refus du gestionnaire de dialogue. Si le personnage est encore en chargement ou à une autre phase, un message neutre apparaît en barre d’action et le détail reste dans le journal serveur ; aucune progression n’est réinitialisée.

## 4. Lier les six PNJ de chaque exemplaire du navire

Se tenir **à moins de trois blocs du PNJ voulu**, plus près de lui que de tout autre EasyNPC d’au moins 0,5 bloc. Exécuter uniquement la commande de son rôle, puis passer au PNJ suivant. Les exemplaires au large et au quai possèdent des liaisons distinctes ; un même UUID ne peut occuper deux rôles.

| PNJ | Exemplaire au large | Exemplaire au quai |
| --- | --- | --- |
| Géraud Rivet | `/function capitale:pq01/liaison/geraud_large` | `/function capitale:pq01/liaison/geraud_quai` |
| Althéon Brumeforge | `/function capitale:pq01/liaison/altheon_large` | `/function capitale:pq01/liaison/altheon_quai` |
| Gautier | `/function capitale:pq01/liaison/gautier_large` | `/function capitale:pq01/liaison/gautier_quai` |
| Hugues | `/function capitale:pq01/liaison/hugues_large` | `/function capitale:pq01/liaison/hugues_quai` |
| Anselme | `/function capitale:pq01/liaison/anselme_large` | `/function capitale:pq01/liaison/anselme_quai` |
| Perrin | `/function capitale:pq01/liaison/perrin_large` | `/function capitale:pq01/liaison/perrin_quai` |

La liaison conserve le type, le modèle, le skin, les textures, l’équipement et les autres propriétés EasyNPC. Elle attribue le nom approuvé au PNJ et un tag de rôle si absent ; avant toute écriture, elle sauvegarde son UUID, son interaction, son éventuel événement near, son ancien nom et le fait que le tag était déjà présent. Les autres événements restent en place.

`ON_INTERACTION` peut être absent ou contenir un unique déclencheur PQ01 reconnu, seul ou accompagné d’autres actions. La liaison remplace seulement ce déclencheur et conserve l’ordre, les conditions et les autres actions. Les anciennes commandes `function capitale:…`, `/function capitale:…` et les chemins `capitale:…` seuls sont reconnus pour les rôles prévus. Une interaction sans déclencheur PQ01 reconnu, ou avec plusieurs déclencheurs, est refusée et décrite sans écriture. Une liaison existante vers un autre PNJ doit être libérée avant réaffectation. Une modification externe des actions après liaison reste protégée lors d’une nouvelle liaison.

L’interaction utilise l’action personnalisée native EasyNPC `haute_capitale_quetes:pq01`, enregistrée par le journal HF7. Le rôle, l’événement, la liaison du PNJ, la proximité, le personnage chargé et la propriété d’un acteur personnel sont vérifiés ; la fonction est choisie dans une liste fixe et exécutée dans le contexte du joueur. Les nouveaux déclencheurs PQ01 n’exigent aucun ajout à `executeAsUserCommandAllowList`. Pour Géraud au large, la proximité attend cinq secondes après disponibilité du personnage, un déplacement horizontal d’au moins 0,75 bloc et une distance réelle au PNJ de trois blocs maximum. Le clic volontaire reste disponible immédiatement. L’interpellation ne se relance pas automatiquement après fermeture ; la reprise passe par un clic droit. Le déclencheur PQ01 de proximité du Capitaine devient sans effet : sa conversation commence au clic droit. Un ancien déclencheur PQ01 reconnu est converti et les autres actions sont conservées. Un near personnalisé sans déclencheur reconnu reste intact. La portée et l’activation du near restent celles configurées dans EasyNPC. Le point de waypoint du rôle est aussi enregistré à la position du PNJ. Les ensembles d’actions sont mis à jour par l’API EasyNPC, avec relecture de contrôle ; l’entité complète n’est pas rechargée par une commande de données.

Un PNJ impliqué dans une scène personnelle ou occupé par un autre joueur ne peut pas être lié/libéré/déplacé tant que cette utilisation dure.

## 5. Déplacer les originaux si leur poste doit changer

Après liaison, se placer au poste réel souhaité, pieds sur un sol accessible et regard dans le bon sens, puis exécuter la commande individuelle. Le PNJ doit être chargé dans la même dimension, à 64 blocs maximum. La commande déplace uniquement le PNJ lié, puis actualise son poste sauvegardé et son waypoint. Aucune conversation ne doit être active pour l’admin.

| PNJ | Au large | Au quai |
| --- | --- | --- |
| Géraud Rivet | `/function capitale:pq01/pose/placer_geraud_large` | `/function capitale:pq01/pose/placer_geraud_quai` |
| Althéon Brumeforge | `/function capitale:pq01/pose/placer_altheon_large` | `/function capitale:pq01/pose/placer_altheon_quai` |
| Gautier | `/function capitale:pq01/pose/placer_gautier_large` | `/function capitale:pq01/pose/placer_gautier_quai` |
| Hugues | `/function capitale:pq01/pose/placer_hugues_large` | `/function capitale:pq01/pose/placer_hugues_quai` |
| Anselme | `/function capitale:pq01/pose/placer_anselme_large` | `/function capitale:pq01/pose/placer_anselme_quai` |
| Perrin | `/function capitale:pq01/pose/placer_perrin_large` | `/function capitale:pq01/pose/placer_perrin_quai` |

Exemples de placement : Gautier à son poste de surveillance, Hugues près de la cargaison, Anselme devant les instruments et Perrin au départ de son service. Les modèles et équipements existants restent utilisés ; aucune apparence nouvelle n’est imposée. Ne pas lancer toutes les commandes depuis un même endroit.

## 6. Définir l’aller/retour de Gautier et les occupations au large

Chaque commande ci-dessous enregistre **la position actuelle de l’admin**, sans déplacer immédiatement le PNJ.

| Commande | Se placer à cet endroit |
| --- | --- |
| `/function capitale:pq01/pose/gautier_poste` | Poste de surveillance de Gautier, sur le pont du navire au large. |
| `/function capitale:pq01/pose/gautier_approche` | Point d’arrêt sûr près de Géraud, sur un sol accessible. |
| `/function capitale:pq01/pose/hugues_charge` | Point vers lequel Hugues regarde pendant son inspection de cargaison. |
| `/function capitale:pq01/pose/anselme_instruments` | Instruments ou barre vers lesquels Anselme regarde. |
| `/function capitale:pq01/pose/perrin_a` | Premier point du service léger de Perrin. |
| `/function capitale:pq01/pose/perrin_b` | Second point du service léger de Perrin. |

Gautier : poste et approche dans la même dimension, sur un sol praticable, à 24 blocs maximum l’un de l’autre et proches des PNJ sources. Laisser une voie réelle entre eux, compatible avec les portes et escaliers du décor. Les acteurs n’ouvrent pas les portes ; un trajet fermé produit un repli dont la raison reste dans le journal serveur. Perrin : deux points accessibles dans la même zone, à 24 blocs maximum. Aucun chemin ni coordonnée du décor n’a été inventé.

Gautier part pendant la réplique de Géraud sur le sauvetage. Son interruption peut commencer pendant qu’il marche encore ; il n’est plus nécessaire d’attendre son arrivée pour poursuivre le dialogue. À son arrêt, il reste sur place pendant la remontrance et sa dernière réponse. Après « Bien, quartier-maître ! » et sa durée audio éventuelle, il rejoint réellement son poste, puis Géraud revient vers le joueur. Hugues regarde la cargaison et fait un geste d’inspection périodique sans modifier les inventaires. Anselme regarde ses instruments. Perrin marche entre A et B, avec une pause aux extrémités. Leur occupation se suspend pendant leur propre dialogue ; la scène principale garde la priorité.

## 7. Points d’activité au quai

Les points du navire au large ne sont **jamais réutilisés comme destinations sur le navire amarré**. Sur cet autre exemplaire, on peut enregistrer séparément :

```mcfunction
/function capitale:pq01/pose/gautier_poste_quai
/function capitale:pq01/pose/hugues_charge_quai
/function capitale:pq01/pose/anselme_instruments_quai
/function capitale:pq01/pose/perrin_a_quai
/function capitale:pq01/pose/perrin_b_quai
```

Appliquer les mêmes consignes de position et de portée. Sans ces points facultatifs du quai, les PNJ restent à leur poste source ; Perrin n’entame pas de ronde non configurée. Il n’existe pas de seconde interruption obligatoire de Gautier au quai.

## 8. Réactiver puis contrôler

```mcfunction
/data modify storage capitale:pq01_setup ambient_enabled set value 1
/function capitale:pq01/admin/status
```

Le statut distingue liaison manquante, configurée mais non chargée dans la dimension, ou chargée ; il liste les points enregistrés. Les acteurs d’ambiance apparaissent seulement pour un personnage dont PQ01 est actif, à proximité du navire ; ils sont retirés à la fermeture, à l’éloignement ou à la perte du contexte. Les PNJ originaux restent à leur poste.

Échange Hugues/Gautier facultatif, désactivé par défaut :

```mcfunction
/data modify storage capitale:pq01_config allow_banter set value 1
```

Il est proposé via le menu d’Hugues, au large uniquement. Il exige les deux acteurs personnels à huit blocs maximum, aucun autre joueur à seize blocs et un cooldown expiré de 1 200 ticks en ligne. Il ne déplace aucun original et ne progresse aucune quête. Remettre `allow_banter` à `0` pour le désactiver.

## 9. Libérer une liaison et restaurer les actions sauvegardées

Se tenir à huit blocs maximum du PNJ lié, dans sa dimension, après fermeture des scènes. Choisir la seule commande correspondante :

```mcfunction
/function capitale:pq01/liaison/liberer_geraud_large
/function capitale:pq01/liaison/liberer_altheon_large
/function capitale:pq01/liaison/liberer_gautier_large
/function capitale:pq01/liaison/liberer_hugues_large
/function capitale:pq01/liaison/liberer_anselme_large
/function capitale:pq01/liaison/liberer_perrin_large
/function capitale:pq01/liaison/liberer_geraud_quai
/function capitale:pq01/liaison/liberer_altheon_quai
/function capitale:pq01/liaison/liberer_gautier_quai
/function capitale:pq01/liaison/liberer_hugues_quai
/function capitale:pq01/liaison/liberer_anselme_quai
/function capitale:pq01/liaison/liberer_perrin_quai
```

La restauration concerne seulement les champs modifiés par la liaison. Si une action a été modifiée entre-temps dans EasyNPC, la restauration est refusée pour ne pas écraser ce travail. Les changements cosmétiques ultérieurs sont conservés ; le nom est restauré seulement s’il est encore celui posé par la liaison. Le placement physique d’édition n’est pas annulé par cette commande.

## 10. Paramètres et dépannage ciblé

Les données de configuration sont dans `capitale:pq01_setup` : `bindings`, `points`, `ambient_enabled` (1 par défaut) et `move_timeout_ticks` (300 par défaut, borné à 20–1 200). Pour modifier le délai :

```mcfunction
/data modify storage capitale:pq01_setup move_timeout_ticks set value 300
```

Un acteur absent, un point non configuré, un chemin refusé ou le dépassement du délai inscrit la raison dans le journal serveur et déclenche un repli qui laisse la quête avancer. Aucun diagnostic technique n’est envoyé comme réplique de PNJ. Pour Gautier, un échec du trajet conserve le même acteur au dernier point atteint pendant la conversation. Il ne réaffiche pas l’original à son poste. La fermeture annule la scène et nettoie les acteurs comme auparavant ; aucun PNJ original n’est téléporté pour forcer l’animation. Après correction du chemin, un admin peut réarmer uniquement l’introduction de son personnage :

```mcfunction
/function capitale:pq01/admin/fermer_dialogue
/function capitale:pq01/admin/reparer_scene_gautier
```

Cette réparation est limitée à PQ01 avant l’entretien achevé du capitaine. Pour revoir PQ01 sur un personnage plus avancé, employer `test_debut`/`test_fin` décrits dans `TESTS_EN_JEU.md`.

En cas de « unknown function », les adresses ci-dessus correspondent aux fichiers présents dans les ZIP R3 et sont contrôlées dans la livraison. Le journal R3 fournit la commande native `indivispq01` : son absence ou une erreur de chargement se diagnostique dans le log de démarrage, sans reset de profil ni vérification générique des datapacks.

## 11. Dialogues et questions HF6

Les pages qui n’offraient que « Continuer. » avancent seules après le temps de lecture, la dernière page et la fin de la voix. Les pages intermédiaires se tournent aussi automatiquement une fois leur texte révélé. Le joueur conserve ses vraies réponses, ses questions et la possibilité de fermer. Un menu de questions ne se valide jamais automatiquement. Aucun texte nouveau de réponse n’a été inventé.

L’interruption affiche le nom et la caméra du locuteur : **Gautier** pendant ses lignes, **Géraud Rivet** pendant les siennes. La caméra quitte Gautier avant son retour pour que le verrou de dialogue ne bloque pas sa marche. Les menus sans réplique ne montrent plus une page « … » à valider. La première page vide du Capitaine est sautée.

Le Capitaine attend un clic droit ; il ne déclenche plus l’interpellation automatique PQ01. Dans ses questions, la réponse finale est verte et porte la mention **« Terminer l’entretien et lancer l’amarrage. »**. Les autres questions peuvent être reprises avant de choisir cette réponse. La couleur utilise la catégorie verte native de Dialogue b10.

Le matelot Anselme reçoit un tag réservé PQ01. Les liaisons déjà enregistrées sont mises à jour quand le PNJ est chargé près d’un joueur, sans refaire la liaison. La quête de l’archiviste Anselme Kerrec exclut ce tag ; les autres archivistes gardent leur comportement.

Gautier et Perrin utilisent une création directe d’entité puis une copie contrôlée et une navigation réelle. Le seuil d’arrivée est de 1,5 bloc avec un écart vertical de 0,75 bloc maximum. Il accepte notamment l’arrêt natif à environ 1,31 bloc observé dans le log. Un autre étage n’est pas considéré comme une arrivée proche. Les originaux ne sont pas déplacés par une scène.

## 12. Déclenchement et reprise HF5

Après disponibilité du personnage Nexus, Géraud attend au moins cinq secondes, un mouvement horizontal du joueur d’au moins 0,75 bloc et une proximité réelle de trois blocs. La surveillance de proximité ne dépend plus d’un nouvel événement EasyNPC après l’entrée dans son ancien grand rayon. Un clic droit volontaire reste utilisable immédiatement, sans ce délai.

Après fermeture, l’interpellation ne rouvre pas la conversation toute seule. Un clic droit reprend au checkpoint existant. La première caméra est prise en compte avant son éventuel remplacement ; un refus de caméra ou une session disparue libère les seuls états temporaires PQ01. Les autres sessions de dialogue et les scores persistants sont conservés. Cette récupération traite aussi l’état bloqué laissé par HF4.

Le contrôle supplémentaire qui rejetait l’approche de Gautier au-delà de quatre blocs de Géraud est retiré. Les points enregistrés sont réutilisés ; les limites de navigation, de dimension et de délai restent actives. Si un locuteur est hors de la portée d’ouverture de Dialogue, la caméra valide déjà ouverte est conservée et son texte continue avec son nom. Si aucune caméra valide n’existe, la conversation se ferme proprement et peut être relancée au clic.

Le mode volontaire `test_debut` réarme la proximité pour une nouvelle recette, sans modifier ses sauvegardes historiques. Aucune de ces commandes de test n’est exécutée lors de l’installation.

## 13. Enchaînements et navigation HF6

Après une réponse de Géraud, Gautier, Hugues, Anselme ou Perrin, les questions sont directement disponibles sous cette réponse. Le Capitaine retrouve aussi ses questions à la fin de chaque famille de réponses ; les sous-questions rédigées restent des choix manuels. Les cinq questions de Géraud au quai restent réutilisables. Il n’est plus nécessaire de fermer puis rouvrir la conversation pour changer de question. La conclusion verte du Capitaine conserve sa fonction de clôture explicite.

Une avance automatique reçue pendant une voix est conservée par le serveur puis traitée quand cette attente se termine. Le client réessaie avec le même jeton jusqu’à la nouvelle page, de façon bornée ; un ancien jeton ne valide pas la page suivante. Une durée audio sans son associé ne provoque plus d’attente. Chaque nouveau passage renouvelle le délai de conversation configuré : le délai de 3 600 ticks ne porte plus sur la durée totale de tout l’entretien.

La marche conserve un chemin natif actif. Elle ne le remplace plus périodiquement par un nouveau calcul susceptible de faire échouer un trajet en cours. Un refus initial bénéficie de nouvelles tentatives pendant la pose au sol ; un point à hauteur fractionnaire peut être proposé au calcul natif à la hauteur de bloc supérieure, sans changer le point sauvegardé ni téléporter l’acteur. Un blocage réel conserve un arrêt/repli borné et son diagnostic serveur.

Les essais exécutés et leurs limites sont décrits dans `HOTFIX_SCENE_CAPTURE_R3_HF7.md` et `audit/validation.json`. La carte réelle et le rendu graphique complet du modpack ne sont pas disponibles dans ce banc.

## 14. Scène et notifications HF7

Les notifications Minecraft de connexion, reconnexion avec ancien nom et déconnexion restent dans le chat normal et n’apparaissent plus comme paroles du PNJ. Le filtre porte sur les clés de traduction de ces notifications, y compris lorsqu’elles sont enveloppées dans un autre composant de texte ; une réplique littérale contenant les mêmes mots reste intacte. Il s’applique aux conversations capturées par Dialogue b10, pas uniquement à PQ01. Le JAR Dialogue reste inchangé.

Gautier commence son approche pendant la deuxième réplique de Géraud. L’arrivée ne déclenche pas à elle seule une nouvelle réplique : le dialogue garde son ordre et le temps de lecture/de voix de Géraud. À la fin de cette réplique, Gautier peut parler en marchant. Une fois arrêté, il garde son acteur, sa position et son apparence pendant l’échange. Après « Bien, quartier-maître ! », il repart à pied. Un échec d’approche, de reprise ou de retour le laisse au dernier point atteint ; aucune suppression/recréation ne simule un retour instantané au poste.

La mise à jour se fait sur le **serveur et chaque client**. Les logs fournis le 24 octobre annoncent journal HF6 côté serveur et HF4 côté client ; le nouveau filtre d’affichage se trouve dans le journal HF7 côté client. Cette observation ne suffit pas à expliquer le problème de trajet, qui a sa correction serveur propre.
