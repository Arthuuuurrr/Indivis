# Installation, configuration et déplacements des PNJ — journal PQ01-R3-HF10

**Mise à jour du 24 octobre 2026 : remplacer uniquement le mod de quêtes/journal sur le serveur et chaque client par HF10. Conserver le core HF9 déjà installé.** Conserver `hc_quetes` HF4. Liaisons, points, apparences et progressions sont réutilisés. Aucune commande de liaison, pose ou reset n’est requise pour mettre à jour.

Le correctif regroupe les répliques successives d’un même PNJ dans un bloc. Le texte reste affiché jusqu’à la lecture du joueur : Espace, Entrée ou clic dans le dialogue pour révéler/parcourir le texte, puis lire la suite. Les vraies réponses restent des choix. La file des prises audio conserve les hooks existants. [Changements multijoueur et vérifications HF10](../HOTFIX_MULTIJOUEUR_R3_HF10.md).

## 1. Les trois opérations à distinguer

| Besoin | Exemple exact | Effet |
| --- | --- | --- |
| Affecter un PNJ existant à son rôle | `/function capitale:pq01/liaison/gautier_large` | Choisit l’EasyNPC le plus proche, sauvegarde puis relie son interaction. Aucun déplacement. |
| Déplacer physiquement ce PNJ en édition | `/function capitale:pq01/pose/placer_gautier_large` | Place le PNJ déjà lié à la position et à l’orientation de l’admin. |
| Définir sa destination de marche | `/function capitale:pq01/pose/gautier_approche` | Enregistre le sol où Gautier doit rejoindre Géraud pendant la scène. |
| Ancien point de journal | `/function capitale:pq01/pose/gautier_large` | Enregistre uniquement un waypoint ; ne lie et ne déplace aucun PNJ. |

**Pendant la quête, Gautier marche réellement vers le point d’approche puis vers son poste.** La commande `placer_…` sert à l’édition du décor ; elle n’est jamais appelée par le trajet de scène. L’acteur temporaire est créé à son poste réel et sa navigation doit rejoindre la destination : aucun TP de scène à travers un mur.

## 2. Installer le journal HF10 sur le core HF9

| Composant | Action | Fichier |
| --- | --- | --- |
| Core, datapacks du monde serveur | Conserver HF9, inchangé et non inclus | `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9.zip` |
| Mod de quêtes/journal, serveur **et chaque client** | Remplacer le JAR précédent | `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF10.jar` |
| Quêtes, datapacks du monde serveur | Conserver HF4 | `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4.zip` |

1. Fermer les dialogues et attendre la fin d’un amarrage éventuel. Sauvegarder ensemble monde et profils Nexus, puis arrêter le serveur.
2. Remplacer le seul JAR de quêtes/journal par HF10 sur le serveur. Conserver le core HF9 et `hc_quetes` HF4.
3. Remplacer aussi le journal sur chaque client. Conserver Nexus `PRE34-SI1`, Dialogue `0.1.0+1.21.11.b10-DATAPACK-COMMAND-HOTFIX`, EasyNPC `7.16.0`, Fabric API `0.141.6+1.21.11` et les autres composants.
4. Redémarrer et reprendre au clic droit sur le PNJ. Aucun PNJ ou point à replacer ; aucune progression à réinitialiser.

Le ZIP extérieur de livraison contient le guide et les composants. Cette livraison contient uniquement le JAR modifié, à placer dans les mods. Aucun datapack à remplacer pour passer de HF9 à HF10. Les commandes de pose suivantes servent aux modifications volontaires du décor.

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

## HF9 : lecture, orientation et diagnostic de Perrin

- **Lecture :** aucun minuteur ne tourne les pages ou n’efface un bloc. Espace/Entrée/clic laisse d’abord le moteur révéler ou parcourir le texte ; sur la dernière page achevée, la même action demande le bloc suivant. L’indication « Espace ou clic : suite » n’est pas une parole du protagoniste. Les boutons de questions et la fermeture restent ceux du dialogue.
- **Géraud :** son acteur regarde le joueur à l’ouverture et pendant A1/A2. Il ne se tourne vers Gautier que pour A3 à A6, puis revient vers le joueur. « Matelot ! » et la remontrance sont dans un même bloc lisible.
- **Perrin :** la marche reste personnelle, respecte les points enregistrés et l’ambiance désactivée. Une vitesse de base nulle du PNJ décoratif est corrigée seulement sur l’acteur temporaire. L’original conserve ses attributs. La ronde comporte normalement une pause de six secondes entre les trajets.

Si Perrin reste immobile sur le serveur, se tenir près de lui avec le personnage concerné et exécuter :

```mcfunction
/function capitale:pq01/admin/diagnostic_perrin
```

La commande inscrit une ligne `[Indivis PERRIN_DIAG HF10]` dans le log serveur : phase, progression, ambiance, position du joueur/source/acteur, points A/B, vitesse effective et raison actuelle. Elle ne déplace pas le PNJ et ne réinitialise rien. Fournir ensuite cette ligne ou le log serveur permet de distinguer un point absent, une ambiance désactivée, un PNJ non chargé/éloigné, une vitesse, une pause ou un trajet bloqué. Les nouveaux logs ne contiennent aucune ligne PERRIN_DIAG. HF10 écrit aussi ce détail automatiquement, une fois par personnage, phase et cause, si un trajet échoue ou si les points manquent. Les deux Perrin marchent dans le test multijoueur ; cela ne prouve pas que les points de cette carte sont praticables.

Pour le futur doublage, voir [guide de doublage](https://github.com/Arthuuuurrr/Indivis/blob/feat/pq01-dirigeable-r2-20261022/docs/quests/pq01-dirigeable-r3/docs/DOUBLAGE_BLOCS.md) et [correspondance blocs/prises](https://github.com/Arthuuuurrr/Indivis/blob/feat/pq01-dirigeable-r2-20261022/docs/quests/pq01-dirigeable-r3/config/BLOCS_ET_PRISES.json). Les 92 prises individuelles restent valables.

## HF10 : scènes simultanées

La copie de Gautier, celle de Géraud et les autres acteurs PQ01 ne sont envoyés qu’à leur propriétaire, avec le contrôle du personnage Nexus conservé. Un autre joueur voit les PNJ publics ou ses propres acteurs. La fermeture, la déconnexion et le changement de personnage nettoient uniquement les copies du joueur concerné.

Les rafraîchissements PQ01 de visibilité sont regroupés par joueur et par tick. Gautier est créé pour son interruption ou son échange avec Hugues, sans copie d’ambiance inactive. Les textes, blocs manuels, choix verts et hooks de doublage de HF9 restent identiques. Aucun changement de commande de pose.
