"""Livraison R3 complète : trois composants changés, documentation et sources."""
from pathlib import Path
import csv,json,shutil,zipfile,hashlib,re
import build
from model import N,IDS,ROLES,PROMPT
ROOT=build.ROOT;HERE=build.HERE;OUT=build.OUT
NAME='Indivis_PQ01_R3_HF1_2026-10-23';DEST=ROOT/'deliverables'/NAME
def put(path,text):
 p=DEST/path;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(text);return p
def copy(src,path):
 p=DEST/path;p.parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(src,p)
def release():
 validation=json.loads((OUT/'validation.json').read_text());assert validation['status']=='PASS'
 if DEST.exists():shutil.rmtree(DEST)
 DEST.mkdir(parents=True)
 for key,name in build.FILES.items():copy(OUT/name,('installation/mods/' if key=='journal' else 'installation/datapacks/')+name)
 put('README.md',f'''# PQ01 — Sous pavillon de la Couronne — R3-HF2

Minecraft Java Fabric 1.21.11 — 23 octobre 2026.

Cette livraison intègre les dialogues approuvés, l’aller/retour individuel de Gautier vers Géraud, les occupations des matelots, les liaisons et placements individuels EasyNPC et la relecture de PQ01 par personnage.

HF2 conserve la correction des annotations Mixin et ajoute les copies NbtElement, la migration des anciens déclencheurs et les actions natives EasyNPC. Les datapacks R3 restent identiques. Le mod admin reste en alpha.3.

**Trois composants à remplacer : core, datapack de quêtes et mod journal.** Le journal R3-HF2 s’installe sur le serveur **et sur les clients**. Les fichiers sont dans `installation/`. Nexus, Dialogue et EasyNPC fournis restent utilisés ; aucun Nexus inchangé n’est redistribué.

Lire **[INSTALLATION_ET_POSE_PNJ.md](docs/INSTALLATION_ET_POSE_PNJ.md)** : liaison, déplacement physique et points de marche sont trois opérations distinctes, avec toutes les commandes exactes. Puis **[TESTS_EN_JEU.md](docs/TESTS_EN_JEU.md)** pour rejouer sans récompense ni lancement de PQ02.

Les {len(N)} nœuds reprennent les 145 passages/questions approuvés. Les choix et sous-questions sont répétables et fermables ; l’amnésie précède la conclusion d’Althéon. L’amarrage historique et les récompenses normales sont conservés. Visiteur reste attribué chez le magistrat.

Validation exécutée : 12 contrôles des datapacks, 17 cas du contrôleur de marche compilé, 44 assertions sur la politique du Nexus fourni, vérification du bytecode. **Aucun serveur Minecraft/Fabric ni monde n’a été lancé ici.** La navigation dans le décor, le rendu des PNJ, la caméra et les scénarios à deux joueurs restent à recetter sur une copie du monde.

Inventaire exact : `audit/DIFFS_DEPUIS_SI1_EXHAUSTIFS.csv`. Preuves et limites : `docs/VALIDATION_ET_LIMITES.md`. Les sources et le prompt sont inclus dans `sources/`. Aucun déploiement ni merge effectué.
''')
 bindings='\n'.join(f'| {ROLES[r]} | `/function capitale:pq01/liaison/{r}_large` | `/function capitale:pq01/liaison/{r}_quai` |' for r in ROLES)
 placements='\n'.join(f'| {ROLES[r]} | `/function capitale:pq01/pose/placer_{r}_large` | `/function capitale:pq01/pose/placer_{r}_quai` |' for r in ROLES)
 unbinds='\n'.join(f'/function capitale:pq01/liaison/liberer_{r}_{phase}' for phase in ('large','quai') for r in ROLES)
 put('docs/INSTALLATION_ET_POSE_PNJ.md',f'''# Installation, configuration et déplacements des PNJ — PQ01-R3-HF2

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
| Datapacks du monde serveur | `{build.FILES['core']}` |
| Datapacks du monde serveur | `{build.FILES['quests']}` |
| `mods` serveur et clients | `{build.FILES['journal']}` |

Les deux ZIP internes sont les datapacks complets, avec `pack.mcmeta` à leur racine. Le ZIP extérieur de livraison n’est pas lui-même un datapack.

1. Fermer les dialogues, attendre la fin d’un amarrage et terminer les relectures actives avec `/function capitale:pq01/admin/test_fin` si R2/R3 est déjà installé. Sauvegarder ensemble le monde et les profils Nexus.
2. Arrêter le serveur. Remplacer les deux datapacks et le JAR journal correspondants par les trois fichiers ci-dessus. Retirer les versions précédentes de ces mêmes composants.
3. Installer aussi le journal R3-HF2 dans les clients. Conserver Nexus `PRE34-SI1`, Dialogue `0.1.0+1.21.11.b10-DATAPACK-COMMAND-HOTFIX`, EasyNPC et son interface `7.16.0`, Fabric et les autres composants du serveur. Aucun remplacement de Nexus ou Dialogue n’est fourni.
4. Redémarrer. Les commandes suivantes s’exécutent en jeu, par un administrateur avec un personnage Nexus sélectionné et chargé.

Le code de scène fonctionne côté serveur ; le JAR journal contient aussi ses composants clients et mixins. Ce correctif s’ajoute au journal fourni, sans créer un second mod portant le même identifiant.

## 3. Préparer l’édition

Suspendre l’ambiance puis nettoyer les acteurs temporaires avant de lier ou déplacer les originaux :

```mcfunction
/data modify storage capitale:pq01_setup ambient_enabled set value 0
/function capitale:pq01/admin/nettoyer_scenes
```

Ces commandes ne réinitialisent aucune quête. Le nettoyage ferme les scènes PQ01 personnelles en cours ; l’effectuer pendant la configuration sur la copie de test du monde. Les liaisons, points et apparences restent enregistrés.

## 4. Lier les six PNJ de chaque exemplaire du navire

Se tenir **à moins de trois blocs du PNJ voulu**, plus près de lui que de tout autre EasyNPC d’au moins 0,5 bloc. Exécuter uniquement la commande de son rôle, puis passer au PNJ suivant. Les exemplaires au large et au quai possèdent des liaisons distinctes ; un même UUID ne peut occuper deux rôles.

| PNJ | Exemplaire au large | Exemplaire au quai |
| --- | --- | --- |
{bindings}

La liaison conserve le type, le modèle, le skin, les textures, l’équipement et les autres propriétés EasyNPC. Elle attribue le nom approuvé au PNJ et un tag de rôle si absent ; avant toute écriture, elle sauvegarde son UUID, son interaction, son éventuel événement near, son ancien nom et le fait que le tag était déjà présent. Les autres événements restent en place.

`ON_INTERACTION` peut être absent ou contenir un unique déclencheur PQ01 reconnu, seul ou accompagné d’autres actions. Seul ce déclencheur est remplacé ; l’ordre, les conditions et les autres actions sont conservés. Les anciennes formes `function capitale:…`, `/function capitale:…` et `capitale:…` sont reconnues pour les rôles prévus. Une interaction sans déclencheur reconnu ou avec plusieurs déclencheurs est refusée sans écriture. Les modifications externes après liaison restent protégées. Une liaison existante vers un autre PNJ doit être libérée avant réaffectation.

L’interaction utilise l’action personnalisée native EasyNPC `haute_capitale_quetes:pq01`, enregistrée par le journal HF2. Le rôle, l’événement, la liaison du PNJ, la proximité, le personnage chargé et la propriété d’un acteur personnel sont vérifiés ; la fonction est choisie dans une liste fixe et exécutée dans le contexte du joueur. Les nouveaux déclencheurs PQ01 n’exigent aucun ajout à `executeAsUserCommandAllowList`. Pour Géraud/Althéon au large, un `ON_DISTANCE_NEAR` absent reçoit le déclencheur prévu ; un ancien déclencheur PQ01 reconnu est converti, ses autres actions sont conservées. Un near personnalisé sans déclencheur reconnu reste intact. La portée et l’activation du near restent celles configurées dans EasyNPC. Le point de waypoint du rôle est aussi enregistré à la position du PNJ. Les ensembles d’actions sont mis à jour par l’API EasyNPC, avec relecture de contrôle ; l’entité complète n’est pas rechargée par une commande de données.

Un PNJ impliqué dans une scène personnelle ou occupé par un autre joueur ne peut pas être lié/libéré/déplacé tant que cette utilisation dure.

## 5. Déplacer les originaux si leur poste doit changer

Après liaison, se placer au poste réel souhaité, pieds sur un sol accessible et regard dans le bon sens, puis exécuter la commande individuelle. Le PNJ doit être chargé dans la même dimension, à 64 blocs maximum. La commande déplace uniquement le PNJ lié, puis actualise son poste sauvegardé et son waypoint. Aucune conversation ne doit être active pour l’admin.

| PNJ | Au large | Au quai |
| --- | --- | --- |
{placements}

Exemples de placement : Gautier à son poste de surveillance, Hugues près de la cargaison, Anselme devant les instruments et Perrin au départ de son service. Les modèles et équipements existants restent utilisés ; aucune apparence nouvelle n’est imposée. Ne pas lancer toutes les commandes depuis un même endroit.

## 6. Définir l’aller/retour de Gautier et les occupations au large

Chaque commande ci-dessous enregistre **la position actuelle de l’admin**, sans déplacer immédiatement le PNJ.

| Commande | Se placer à cet endroit |
| --- | --- |
| `/function capitale:pq01/pose/gautier_poste` | Poste de surveillance de Gautier, sur le pont du navire au large. |
| `/function capitale:pq01/pose/gautier_approche` | Point d’arrêt sûr près de Géraud, à quatre blocs maximum de lui. |
| `/function capitale:pq01/pose/hugues_charge` | Point vers lequel Hugues regarde pendant son inspection de cargaison. |
| `/function capitale:pq01/pose/anselme_instruments` | Instruments ou barre vers lesquels Anselme regarde. |
| `/function capitale:pq01/pose/perrin_a` | Premier point du service léger de Perrin. |
| `/function capitale:pq01/pose/perrin_b` | Second point du service léger de Perrin. |

Gautier : poste et approche dans la même dimension, sur un sol praticable, à 24 blocs maximum l’un de l’autre et proches des PNJ sources. Laisser une voie réelle entre eux, compatible avec les portes et escaliers du décor. Les acteurs n’ouvrent pas les portes ; un trajet fermé produit un repli explicite. Perrin : deux points accessibles dans la même zone, à 24 blocs maximum. Aucun chemin ni coordonnée du décor n’a été inventé.

Gautier attend son arrivée pour prononcer son interruption. Après « Bien, quartier-maître ! » et sa durée audio éventuelle, il rejoint réellement son poste, puis Géraud revient vers le joueur. Hugues regarde la cargaison et fait un geste d’inspection périodique sans modifier les inventaires. Anselme regarde ses instruments. Perrin marche entre A et B, avec une pause aux extrémités. Leur occupation se suspend pendant leur propre dialogue ; la scène principale garde la priorité.

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
{unbinds}
```

La restauration concerne seulement les champs modifiés par la liaison. Si une action a été modifiée entre-temps dans EasyNPC, la restauration est refusée pour ne pas écraser ce travail. Les changements cosmétiques ultérieurs sont conservés ; le nom est restauré seulement s’il est encore celui posé par la liaison. Le placement physique d’édition n’est pas annulé par cette commande.

## 10. Paramètres et dépannage ciblé

Les données de configuration sont dans `capitale:pq01_setup` : `bindings`, `points`, `ambient_enabled` (1 par défaut) et `move_timeout_ticks` (300 par défaut, borné à 20–1 200). Pour modifier le délai :

```mcfunction
/data modify storage capitale:pq01_setup move_timeout_ticks set value 300
```

Un acteur absent, un point non configuré, un chemin refusé ou le dépassement du délai déclenche un message de raison et un repli qui laisse la quête avancer. Le repli supprime l’acteur personnel concerné ; aucun PNJ original n’est téléporté pour forcer l’animation. Après correction du chemin, un admin peut réarmer uniquement l’introduction de son personnage :

```mcfunction
/function capitale:pq01/admin/fermer_dialogue
/function capitale:pq01/admin/reparer_scene_gautier
```

Cette réparation est limitée à PQ01 avant l’entretien achevé du capitaine. Pour revoir PQ01 sur un personnage plus avancé, employer `test_debut`/`test_fin` décrits dans `TESTS_EN_JEU.md`.

En cas de « unknown function », les adresses ci-dessus correspondent aux fichiers présents dans les ZIP R3 et sont contrôlées dans la livraison. Le journal R3 fournit la commande native `indivispq01` : son absence ou une erreur de chargement se diagnostique dans le log de démarrage, sans reset de profil ni vérification générique des datapacks.
''')
 put('docs/TESTS_EN_JEU.md','''# Rejouer PQ01 et effectuer la recette

## Relecture personnelle sans récompenses

Sur une copie du monde et avec le personnage voulu chargé, hors dialogue et hors transition :

```mcfunction
/function capitale:pq01/admin/test_debut
```

L’état antérieur de PQ01, ses drapeaux de dialogue, `QUEST_PROLOGUE`, `CAP_QUETEACTIVE` et la position sont sauvegardés une seule fois pour le couple compte Minecraft/personnage Nexus. Le personnage est replacé au début historique `764 299 1101` dans l’Overworld. La relecture ne réinitialise aucune autre quête, inventaire, réputation ou rang. Elle ne distribue aucune récompense, ne lance pas PQ02 et ne modifie pas le spawnpoint réel. Elle réutilise le TP historique d’amarrage `93 228 82`.

Après fermeture du dialogue et fin d’une éventuelle transition :

```mcfunction
/function capitale:pq01/admin/test_fin
```

Cette commande restaure uniquement la sauvegarde du personnage actif et sa position. Elle ne remet pas les inventaires ou le monde entier dans leur état précédent ; ne pas effectuer d’autres activités pendant une recette dont on souhaite retrouver exactement le contexte. Relancer `test_debut` en mode test ne remplace pas la sauvegarde initiale. Répéter l’opération séparément sur chaque personnage ayant un test ouvert.

## Parcours fonctionnel

1. Nouveau personnage/relecture : near de Géraud, bouton « Je vous écoute » sans second clic PNJ, introduction, Gautier quitte son poste, rejoint Géraud, s’arrête et lui fait face. Fermer puis rouvrir après chacune de ses deux répliques : les répliques déjà affichées ne doivent pas être répétées. Une reprise pendant la remontrance rejoint d’abord la position d’échange si nécessaire.
2. Après sa réponse, Gautier marche à son poste ; Géraud revient vers le joueur. Vérifier le menu répétable de Géraud et l’objectif vers Althéon.
3. Gautier, Hugues, Anselme, Perrin : chaque question et sous-question est cliquable ; retour au menu et fermeture possibles. Observer leurs occupations et leur suspension pendant leur dialogue. Aucun item n’est transféré par les gestes d’Hugues.
4. Althéon : introduction une fois, amnésie canonique explicitement posée avant la conclusion. Toutes les familles A–E et sous-questions restent répétables. Avaleiv se déverrouille après la mission décrite par Althéon, indépendamment des discussions avec les matelots.
5. Une question facultative ne déclenche aucun TP. Seule la conclusion explicite d’Althéon lance l’amarrage hors champ : sons/effets historiques, TP au navire amarré et objectif Géraud. Aucun nouveau dialogue de manœuvre.
6. Géraud au quai : cinq questions réutilisables ; seule la conclusion finale complète PQ01. Les autres membres d’équipage ont leurs réponses d’après-amarrage. Althéon garde la priorité MQ03 si elle est active.

En parcours normal, contrôler séparément sur un personnage de test approprié : Couronne +2, prime et compétences conservées, PQ01 terminé une seule fois, PQ02 disponible et objectif Léovic. Le mode de relecture supprime volontairement ces effets. Aucun rang 10 n’est donné par PQ01 ; au magistrat après Léovic, Visiteur 10 reste attribué. Un personnage de rang plus élevé ne doit pas être rétrogradé. Les fonctions du magistrat sont inchangées.

## Incidents, caméra et multijoueur

| Scénario à jouer | Résultat attendu |
| --- | --- |
| Joueurs A et B, déclenchements à dix secondes d’écart | Deux scènes personnelles ; le Gautier de B ne suit pas la progression de A ; aucun dialogue, choix, marqueur ou récompense croisé. |
| A traverse l’emplacement du Gautier de B | Aucun blocage/poussée par cet acteur invisible ; aucun effet sur une porte, une plaque ou la ronde de B. |
| Deux personnages du même compte | Reprises, drapeaux, récompenses et sauvegardes de relecture distincts. Changer pendant l’aller et pendant le retour. |
| Déconnexion pendant aller/remontrance/retour | Aucun acteur durable ou errant ; retour possible au stade de dialogue enregistré. |
| Fermer la caméra ou le menu pendant la marche | Navigation annulée et acteurs supprimés ; aucune fin de quête ou téléportation déclenchée par la fermeture. |
| Un autre dialogue remplace la caméra | Le nettoyage PQ01 ne ferme pas la nouvelle session appartenant à l’autre quête. |
| Porte fermée, escalier impraticable, chemin coupé | Arrêt/repli avec raison, sans déplacer l’original ni bloquer l’objectif Althéon. |
| Acteur supprimé/chunk déchargé | Annulation ou repli ; aucun clone abandonné. Après redémarrage, acteurs orphelins retirés à leur chargement. |
| Boutons anciens, doubles clics, durée audio active | Jeton/contexte contrôlé ; pas de double transition ni de double récompense. Le bouton fermer reste disponible en marche. |
| Éloignement, timeout, changement de dimension | Nettoyage sans progression involontaire ; reprise au bon stade. |
| Deux rondes de Perrin et échange Hugues/Gautier | Rondes indépendantes ; ambiance refusée près d’un autre joueur ; scène obligatoire prioritaire. |
| Navire amarré | Aucune destination du navire au large réutilisée au quai. Les points suffixés `_quai` sont indépendants. |

Le timeout de dialogue est de 3 600 ticks par défaut, configurable dans `capitale:pq01_config timeout_ticks` entre 200 et 12 000. La distance maximale au point d’ouverture est de dix blocs. La lecture est manuelle, sans cascade automatique toutes les vingt ticks.

## Futurs doublages

Les nœuds sont listés dans `DIALOGUES_ORDRE_JEU.md`, les intentions dans `DIRECTION_ACTEUR.md`, les durées de lecture suggérées dans `config/VOIX_ET_TEMPS.csv`. Aucun son nouveau n’est généré ou livré. Une fois la ressource audio réelle disponible, régler son nœud, par exemple :

```mcfunction
/data modify storage capitale:pq01_config voices.c1 set value {sound:"indivis:voix/altheon/pq01_c1",ticks:180}
```

Remplacer cet identifiant d’exemple par le son réel. La durée `ticks` bloque le choix suivant jusqu’à la fin audio ; fermer reste possible. Sans son configuré, toute la lecture reste manuelle. Les sons d’amarrage historiques sont conservés. Aucun mod audio supplémentaire n’est supposé installé.

## Retour arrière

Tant que R3 est présent, terminer chaque relecture avec `test_fin`, fermer les scènes et exécuter `nettoyer_scenes`. Si l’on souhaite aussi restaurer les hooks/noms ajoutés aux PNJ, libérer les liaisons individuelles avant désinstallation ; les positions d’édition ne sont pas restaurées automatiquement. Arrêter ensuite le serveur et remettre ensemble les deux datapacks et le journal précédents sur serveur/clients. Les stockages supplémentaires peuvent rester pour diagnostic ; aucun reset global Nexus n’est requis. Les récompenses normales déjà obtenues ne sont pas annulées par un retour de fichiers : pour annuler les effets de jeu, utiliser la sauvegarde cohérente monde/profils.

**Cette recette est fournie à exécuter : aucun de ces scénarios n’a été joué ici sur Minecraft/Fabric.**
''')
 dialogue=['# Dialogues PQ01 — ordre technique et choix','', 'Texte du prompt validé ; les contrôles Continuer/Fermer sont techniques. A2 → trajet aller → A3 ; A6 → trajet retour → A7.','']
 for key,n in N.items():
  dialogue+=['## '+key+' — '+ROLES[n['role']],'']+[f'> {s}' for s in n['lines']]+['']+[f'- {c["label"]} → `{c["target"]}`'+(' (après la mission d’Althéon)' if c.get('condition') else '') for c in n['choices']]+['']
 put('docs/DIALOGUES_ORDRE_JEU.md','\n'.join(dialogue)+'\n')
 direction=(HERE/'reference_docs/DIRECTION_ACTEUR.md').read_text()
 direction=direction.replace(' Dans R2, ne pas prendre l\'absence d\'animation comme une nouvelle intention de personnage : c\'est une limite technique du candidat texte.',' Son arrivée précède A3 ; son retour suit A6. Laisser voir la marche avant la reprise de Géraud.')
 put('docs/DIRECTION_ACTEUR.md',direction)
 ids=(HERE/'reference_docs/IDENTIFIANTS.md').read_text().replace(' Aucun tag durable n\'est ajouté à un PNJ.','')
 ids+='''
## Extension native R3

`hcm_pq1_wait` (attente de marche) et `hcm_pq1_camera` (résultat de commande native) sont transitoires. Caméra : 0 indisponible, 1 ouverte/programmée, 2 reprise gérée par le trajet. La valeur 2 évite une seconde impression de la réplique lors d’un échec synchrone de navigation.

Commande native : `indivispq01`, avec sous-commandes bornées générées dans les fonctions publiques de pose/liaison/admin ; les sous-commandes de scène vérifient propriétaire et nœud actif. Les commandes d’édition exigent le contrôle de permission Gamemasters de Minecraft 1.21.11.

Stockage `capitale:pq01_setup` : liaisons globales de décor sauvegardées par rôle/phase, points d’activité distincts au large/quai, délai et activation de l’ambiance. Il ne contient aucune progression de quête partagée. Vues runtime indexées par compte et vérifiées contre le personnage Nexus, la dimension et le verrou de profil.

Acteurs : `hcm_pq1_actor`, `hcm_private`, `hcm_owner_<UUID-compte>`, `hcm_character_<UUID-personnage>`, `indivis_no_hard_despawn`. Le filtrage de visibilité existant est réutilisé et étendu au masquage de l’original uniquement pour le spectateur de sa copie. Les acteurs sont exclus des collisions/poussées et des cibles de projectile, leurs goals EasyNPC sont vidés et protégés, l’inventaire ne se ramasse/ne tombe pas. Équipe technique `indivis_pq01`, collisionRule `never`. Aucun original n’est déplacé par la scène.

La liaison peut ajouter le tag stable `hcq_pnj_<role>` sur un original ; le marqueur de sauvegarde permet de ne le retirer à la libération que s’il a été ajouté par R3. Les skins/modèles/équipements ne sont pas inventés ni remplacés.
'''
 put('docs/IDENTIFIANTS.md',ids)
 put('docs/ARCHITECTURE_ET_COMPATIBILITE.md','''# Architecture et compatibilité R3

## Base réellement utilisée

Datapacks SI1/SI1b et journal b7 SI1b fournis, signatures SHA-256 dans les audits. Le moteur de dialogues R2 est reconstruit sur ces mêmes entrées, puis complété par le pont natif R3. Aucun ancien Nexus n’est substitué ; la classe de politique testée est extraite de `PRE34-SI1` fourni, sans modification.

Seuls la métadonnée Fabric et le début de `QuestVisibility.isVisibleTo` sont modifiés parmi les entrées originales du journal. Les 159 autres classes originales sont identiques octet pour octet, notamment `SequenceBridge`, les liaisons des gardes et le moteur du journal. Les nouveaux fichiers sont le pont PQ01, le contrôleur de marche, le registre d’acteurs, l’initialiseur, les identifiants générés et deux mixins ciblés. Les contrats de compilation et les outils ASM ne sont jamais intégrés au JAR livré.

## Scène individuelle

Les copies EasyNPC partent du NBT du PNJ existant. Identité, actions de proximité/travail, objectifs, commerce, passagers, laisse et anciens tags sont exclus ; les propriétés visuelles et équipements sont conservés. L’action d’interaction de la copie est uniquement celle de son rôle. Les invocations sont bornées aux UUID de liaisons, à la même dimension et à un rayon de 24 blocs autour du source. Aucun scan d’entités du monde n’est ajouté par tick ; seules les vues actives sont suivies, avec recherche par UUID des quatre matelots proches une fois par seconde et par joueur.

La visibilité par personnage réutilise SI1b. Elle est complétée par les protections de collision/poussée, l’équipe sans collision, l’absence de goals/target goals réintroduits, l’interdiction de ramassage/drops/monture/laisse, et les hooks sans effets sur plaques/pas/vibrations. La nav au sol conserve les collisions avec le décor ; les acteurs ne traversent pas les murs. Aucun PNJ original ni son IA ne reçoit ces modifications de scène.

Gautier et Perrin utilisent le navigateur au sol EasyNPC, l’IA de déplacement active et les objectifs de comportement retirés. Replanification tous les quarante ticks ; timeout, acteur disparu, changement de personnage/dimension et fermeture déclenchent une annulation ou un repli. Les acteurs sont supprimés à la fermeture/éloignement/déconnexion, et les orphelins d’un arrêt anormal au chargement de l’entité.

Le retour après fermeture reprend la lecture stockée sans répéter une réplique de Gautier déjà affichée. La caméra suit uniquement la session capturée par PQ01 ; fermer une ancienne vue ne ferme pas un dialogue plus récent d’une autre quête. L’ouverture de caméra est différée d’un tick après création de l’acteur.

## API contrôlées

Les signatures EasyNPC 7.16.0 et Dialogue b10 sont lues par `javap` dans les JAR fournis : `getGroundPathNavigation`, `getEntityGoalSelector`, `getEntityTargetSelector`, attributs immuables de mouvement/interaction/inventaire, `onFermeture(BiConsumer)`, `ouvrir`, `session`, `fermer`, `pnjOccupe`, `sondeMouvement`. Les helpers package-static `SequenceBridge` et `GuardNbt` sont réutilisés tels quels. Le dossier `audit/api` conserve ces lectures.

Mappings Minecraft vérifiés dans les références primaires Yarn 1.21.11+build.4 :

- https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/entity/Entity.html
- https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/entity/mob/MobEntity.html
- https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/entity/ai/goal/GoalSelector.html
- https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/entity/ai/pathing/EntityNavigation.html
- https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/world/World.html
- https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/server/command/CommandManager.html

Ces contrôles de signatures et de mapping ne remplacent pas un démarrage Fabric avec tous les mixins ni une recette du monde. Le bridge utilise la même méthode de réflexion et le même espace intermediary que le journal fourni ; aucun remapping des classes historiques n’est effectué.
''')
 put('docs/VALIDATION_ET_LIMITES.md',f'''# Validations exécutées et limites

- 12 contrôles des datapacks finaux : JSON, chemins et références internes, périmètre protégé, 145 passages approuvés, choix et amnésie obligatoires, déclenchements de conclusion, récompenses, magistrat, relecture par compte/personnage, jetons/voix/timeout, priorité MQ03 et amarrage avec gardes de contexte.
- 17 cas et 26 assertions exécutés sur le contrôleur `Pq01Motion` réellement compilé : aller/retour, arrivée, attente, chemin refusé/coupé, portée/dimension, annulation, perte de contexte, délais bornés, replanification et deux ports à dix secondes de décalage. Les ports de ce banc ne sont pas des joueurs Minecraft réels.
- 44 assertions sur la vraie classe `V2QuestPolicy` extraite du Nexus fourni : persistance des huit états et caractère transitoire des quatorze scores de runtime. Ce n’est pas un test de changement de personnage dans le jeu.
- {validation['bytecode']}
- 159 classes originales du journal vérifiées identiques ; une classe de visibilité patchée intentionnellement. Contrats Minecraft/Fabric/Mixin et outils de construction absents du JAR livré.
- Aucune référence interne obligatoire manquante dans core + quêtes. Les 22 appels externes CapSkills préexistants sont inchangés ; les macros et références facultatives ne sont pas confondues avec des fonctions manquantes.
- Aucune entrée supprimée. Autres fiches de quêtes, gardes, Léovic et magistrat conservés. L’inventaire exhaustif et les hash avant/après sont dans les audits.

La scène mobile et les occupations sont **implémentées** dans R3. Leur rendu réel dépend des PNJ liés, des points réellement configurés et du chemin praticable du décor. Un repli explicite est prévu en cas d’indisponibilité ; il n’est pas utilisé comme remplacement permanent de l’animation.

**Aucun démarrage Minecraft/Fabric et aucun test en jeu n’ont été réalisés ici.** Le monde, les exports NBT réels et une session à deux joueurs ne sont pas disponibles dans cet environnement. Sont donc encore à vérifier en jeu : chargement des mixins avec le modpack complet, rendu skins/équipements, navigation sur le pont/escaliers, paquets de visibilité et caméra, interactions near, changement de personnage Nexus, reconnexion et récompenses normales. `TESTS_EN_JEU.md` donne les scénarios et résultats attendus.

Les sorties ne sont pas présentées comme certifiées sans bugs. Elles sont fournies pour recette sur copie du monde, avec rollback ciblé, sans installation en production ni merge.
''')
 with (DEST/'config/VOIX_ET_TEMPS.csv').open('w',newline='') if (DEST/'config').exists() else _config_file() as out:
  w=csv.writer(out);w.writerow(['node','role','mots','lecture_conseillee_ticks','son_par_defaut','blocage_audio_ticks_par_defaut'])
  for k,n in N.items():
   words=sum(len(s.split()) for s in n['lines']);w.writerow([k,ROLES[n['role']],words,max(40,words*8+20) if words else 0,'',0])
 for p in OUT.iterdir():
  if p.suffix in ('.json','.log','.csv') and p.name!='release_manifest.json':copy(p,'audit/'+p.name)
 for p in (HERE/'api').glob('*.txt'):
  if p.stat().st_size and p.name!='bridge_call.txt':copy(p,'audit/api/'+p.name)
 for folder,base in [('pq01_full',HERE),('pq01_rebuild',ROOT/'work/pq01_rebuild')]:
  for p in base.rglob('*'):
   relative=p.relative_to(base)
   if p.is_file() and (len(relative.parts)==1 and p.suffix=='.py' or relative.parts[0] in ('java','tests','reference_docs') and p.suffix in ('.java','.md')):copy(p,'sources/work/'+folder+'/'+str(relative))
 copy(PROMPT,'sources/upload/PROMPT_WORK_PQ01_DIRIGEABLE.md')
 put('sources/README.md','''# Reconstruction des fichiers livrés

Python 3, Java 17+ avec modules `jdk.compiler`/`jdk.jdeps` pour assembler ; Minecraft/Fabric 1.21.11 s’exécute avec Java 21. Les contrats minimaux utilisés pour compiler les nouveaux mixins et accéder aux helpers originaux sont générés par `build.py` et ne sont pas livrés dans le mod.

Remettre les fichiers d’entrée originaux dans `sources/upload/` : core `capitale_core_1.5.7_V2_SEQUENCE_I_SI1(1).zip`, quêtes `hc_quetes_b22_V2_SEQUENCE_I_SI1b.zip`, journal `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b.jar`, Nexus `NexusCharacters-Indivis-1.21.11-PRE34-SI1.jar`, et Dialogue b10 fourni. Les SHA-256 sont dans les audits ; les fichiers inchangés ne sont pas redistribués.

Depuis `sources/`, reconstruire d’abord la base R2 puis R3 :

```bash
python3 work/pq01_rebuild/build.py
mkdir -p work/pq01_full/r2
cp work/pq01_rebuild/output/*.zip work/pq01_full/r2/
python3 work/pq01_full/build.py
python3 work/pq01_full/validate.py
python3 work/pq01_full/release.py
```

Le modèle R2 extrait le prompt inclus, sans réécriture libre. Le build R3 vérifie le hash du journal SI1b, conserve ses classes sauf le point de visibilité documenté, génère les fonctions et ajoute le pont natif. `validate.py` teste les sorties ; `release.py` exige ce résultat PASS avant d’assembler la livraison. Les exports `javap` proviennent des JAR réellement fournis ; ils ne sont pas des mocks d’API runtime.
''')
 sums=[]
 for p in sorted(DEST.rglob('*')):
  if p.is_file():sums.append(hashlib.sha256(p.read_bytes()).hexdigest()+'  '+str(p.relative_to(DEST)))
 put('SHA256SUMS','\n'.join(sums)+'\n')
 archive=DEST.with_suffix('.zip');temp=archive.with_suffix('.zip.partial')
 with zipfile.ZipFile(temp,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for p in sorted(DEST.rglob('*')):
   if p.is_file():
    info=zipfile.ZipInfo(NAME+'/'+str(p.relative_to(DEST)),(2026,10,23,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,p.read_bytes())
 with zipfile.ZipFile(temp) as z:assert z.testzip() is None
 temp.replace(archive);report={'file':str(archive),'bytes':archive.stat().st_size,'sha256':hashlib.sha256(archive.read_bytes()).hexdigest()}
 (OUT/'release_manifest.json').write_text(json.dumps(report,indent=2)+'\n');print(json.dumps(report,indent=2));return report
def _config_file():
 (DEST/'config').mkdir(exist_ok=True);return (DEST/'config/VOIX_ET_TEMPS.csv').open('w',newline='')
if __name__=='__main__':release()
