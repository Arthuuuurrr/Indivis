# Haute Capitale — Dialogue

Dialogues immersifs pour le serveur Haute Capitale, Fabric 1.21.11 / Java 21.
Mod ID `haute_capitale_dialogue`.

## Ce que c'est

Une **couche de présentation** au-dessus d'Easy NPC, qui reste le cœur des PNJ
(identité, skin, quêtes, dialogues, actions, commerce). Quand un joueur clique
sur un personnage éligible :

1. le serveur ouvre une *session* — « ce joueur parle à ce personnage » ;
2. la caméra glisse vers le personnage et le cadre au visage (état
   `NPC_DIALOGUE` de la caméra RPG) ;
3. le HUD s'efface, le texte du personnage et les réponses s'affichent en bas de
   l'écran ; touches 1–9 ou souris ;
4. chaque réponse repart par les paquets d'Easy NPC, qui la revalide ;
5. à la fin, tout est restauré : caméra, perspective, HUD.

Rien n'est dupliqué : le texte, les boutons, les conditions et les actions sont
ceux d'Easy NPC. Aucun mixin. Aucun identifiant de registre en dehors d'un type
de menu (`haute_capitale_dialogue:dialogue_easy_npc`).

## Livraison 1 (P0 + P1)

- sessions serveur, 3 paquets, configuration, commandes `/dialogue` ;
- état caméra `NPC_DIALOGUE` dans `haute_capitale_rpg` ≥ 0.3.0 (API `CameraFocus`) ;
- écran de dialogue pour les **dialogues Easy NPC** (source A) ;
- verrous : dégâts, interactions, sorts (Spell Engine), élan Better Combat ;
- regard du personnage vers le joueur (objectif Easy NPC temporaire) ;
- masque du HUD, perspective, orientation du joueur, cinématiques prioritaires.

## Livraison 2 (P2) — capture des dialogues de chat

Les quêtes du serveur ne passent pas par les dialogues Easy NPC : le clic lance
une fonction de datapack qui écrit dans le chat (`tellraw @s [Nom] …`, puis des
lignes `[Choix] [Réponse]` cliquables qui posent un `/trigger`). Les panneaux
des ferries et dirigeables font pareil. Ces conversations sont maintenant
**capturées** et présentées dans le même écran, sans rien changer aux datapacks.

Côté serveur (`CaptureOpener`) : au clic droit, dans une phase Fabric ordonnée
**avant** la phase par défaut (donc avant Easy NPC, les ferries, les
dirigeables), le personnage est classé d'après ses actions `ON_INTERACTION` :

| actions du personnage | conséquence |
|---|---|
| ouvre un dialogue Easy NPC | rien ici : le gestionnaire de menu s'en charge (livraison 1) |
| ouvre un commerce | rien |
| lance des **commandes** | session de capture si le personnage est éligible |
| actions d'autres mods, aucune action, pas un Easy NPC | session de capture **seulement** si tag `hc_dialogue` ou surcharge `capture` |

Le paquet de session part avant que les commandes ne s'exécutent ; le clic
n'est jamais consommé.

Côté client (`MessageCapture`, `CaptureDialogueScreen`) : pendant une session
de capture, chaque message de jeu est trié :

- il porte des `click_event` → ce sont des **réponses** (libellé sans ses
  crochets, nuance d'après la couleur du datapack : vert = quête, rouge =
  danger, cyan = transport, bleu = commerce, or = métier ; « Au revoir. »,
  « Partir. » = congé) ;
- il commence par `[Nom du personnage]` (casse et accents indifférents) ou
  `[Choix]` → **réplique**, préfixe retiré, couleurs conservées ;
- il arrive dans la **fenêtre** qui suit l'ouverture ou une réponse
  (`capture_fenetre_ms`, `capture_fenetre_reponse_ms`) → réplique ;
- il commence par un préfixe de `capture_prefixes_ignores` (`[Quête]`…), ou
  c'est le retour d'une commande vanilla (`Triggered [QuestChoix]…`) → reste
  dans le chat ;
- tout le reste → chat.

Choisir une réponse rejoue son `click_event` par le code vanilla, exactement
comme un clic dans le chat : le `/trigger` part et est validé par le serveur
comme aujourd'hui. **L'écran ne se referme jamais de lui-même** : il reste
ouvert, et les répliques qui arrivent ensuite viennent s'y afficher, jusqu'à ce
que le joueur prenne congé — Échap, ou une réponse d'adieu. Seul un clic qui
n'a produit aucun message rend la main tout seul : ce n'était pas une
conversation. Les sorties de secours restent au serveur : distance
(`distance_maintien`), mort, changement de monde, téléportation, personnage
disparu. Pour retrouver l'ancien comportement — le silence vaut congé après
`capture_delai_silence_ms` plus le temps de lecture — mettre
`fermeture_automatique` à `true` côté client (ou `/dialoguec fermeture`), et
`capture_fermeture_inactivite` à `true` côté serveur pour rétablir la borne
`capture_inactivite_max_ticks`.

**Prérequis Easy NPC** : ses commandes « exécuter en tant que joueur » doivent
être autorisées dans `config/easy_npc/security.cfg`
(`executeAsUserCommandAllowList.<niveau>=function,tellraw,scoreboard,…`) —
c'est déjà le cas sur un serveur où les quêtes fonctionnent.

Réglages : serveur `capture_active`, `capture_fermeture_inactivite`,
`capture_inactivite_max_ticks`, surcharge par UUID `capture` ; client
`fermeture_automatique`, `capture_masquer_chat`, `capture_fenetre_ms`,
`capture_fenetre_reponse_ms`, `capture_delai_silence_ms`,
`capture_prefixes_ignores`.

## Livraison 3 (P3) — personnages sur véhicule

Un personnage **porté** — passager d'une charrette, ou épinglé sans IA sur un
pont comme le capitaine d'un ferry ou le pilote d'un dirigeable — se dialogue
comme les autres tant que le véhicule est à quai : la caméra suit sa position
réelle. Quand le véhicule bouge, le serveur le mesure (déplacement par tick,
avec hystérésis : en route dès le premier écart, à l'arrêt après
`vehicule_ticks_arret` ticks calmes) et applique `vehicule_en_mouvement` :

| politique | en route |
|---|---|
| `simplifie` (défaut) | texte seul : la caméra reste celle du gameplay, la perspective et l'orientation du joueur ne sont pas touchées ; l'écran reste ouvert. Le changement se fait **pendant** la conversation, en fondu, dans les deux sens. |
| `refuser` | pas de conversation en route (message au joueur) ; une conversation en cours se ferme (`MOUVEMENT`) dès le départ. |
| `complet` | comme à quai — déconseillé : la caméra suit un point qui bouge. |

Un personnage qui **marche** de lui-même n'est pas un véhicule : dialogue
complet, comme avant. La téléportation est détectée sur la position *relative*
au personnage : deux voyageurs sur le même pont ne se quittent pas.

Les mods de transport peuvent dire mieux que la physique s'ils sont à quai :
`Dialogues.sondeMouvement(entité → Optional<Boolean>)` (vide = « je ne sais
pas »), consulté à l'ouverture et à chaque contrôle.

## Livraison 4 (P4) — retour et reprise

**Retour après un commerce ou un atelier** (`retour_apres_commerce`, vrai par
défaut, surcharge par UUID). Une réponse qui ouvre le commerce d'Easy NPC, un
atelier des Métiers ou tout autre écran : la conversation reste ouverte
derrière, la caméra ne bouge pas, et quand cet écran se ferme le serveur
rouvre le dialogue **au nœud d'où il a été ouvert** (sans recompter une
exécution ni relancer l'événement « dialogue ouvert »). En mode capture, le
client remontre lui-même la conversation. Sans l'option, fermer cet écran
finit la conversation, comme avant.

**Reprise après une cinématique** (`reprise_apres_cinematique`, faux par
défaut, surcharge par UUID, `reprise_delai_max_ticks`). Une cinématique
(Bosses'Rise) qui prend la caméra suspend toujours la conversation ; avec
l'option, le serveur retient où elle en était et, quand le client signale la
fin de la cinématique, la rouvre au même nœud — ou rejoue l'interaction du
personnage pour une conversation capturée — si le personnage est encore
vivant, à portée, et que le délai n'est pas écoulé.

## Livraison 5 (P5) — finitions

- **Styles de réponses.** Sept nuances (normale, quête, commerce, métier,
  transport, danger, congé), déduites des actions du bouton ou de la couleur
  du datapack, et **posées par l'auteur** avec une étiquette en tête du
  libellé : `[quête] Accepter la tâche.`, `[commerce] Voir l'étal.`,
  `[danger] …`, `[adieu] …` — l'étiquette disparaît du texte. Couleur et
  préfixe de chaque nuance se règlent côté client (`styles_reponses`). Une
  réponse verrouillée montre sa raison entre crochets : `[Rang social ≥ 30 requis]`.
- **Historique.** La touche **H** montre les dernières répliques et réponses
  de la conversation au-dessus du bandeau (`historique_taille`, 8 par défaut).
- **Son de réplique.** À chaque nœud d'un dialogue Easy NPC, le son du
  personnage de type `son_replique` (`AMBIENT` par défaut : ses « hmm ») est
  joué au seul joueur en conversation ; `none` pour ne rien jouer. Les
  dialogues de datapack jouent déjà les leurs.
- **Panneau seul.** `panneau_seul` (global) ou la surcharge par personnage :
  le texte et les réponses, sans caméra ni perspective — pour ceux qui ne
  veulent que l'interface.
- **Outils d'administration.** `/dialogue debug` décrit aussi le personnage
  (éligibilité, tags, surcharge, ce que son clic déclenche, ses dialogues et
  le nœud courant, l'état du véhicule) ; `/dialogue pnj voir`, `/dialogue pnj
  set <clé> <valeur>` (clés : actif, exclusif, verrou_joueur, autoriser_esc,
  masquer_hud, forcer_troisieme_personne, reprise_apres_cinematique,
  retour_apres_commerce, capture, panneau_seul, recul, decalage, angle_visee,
  hauteur_cible, decalage_cible_y, elevation, transition_ms ; `défaut` pour
  effacer une clé) et `/dialogue pnj oublier` règlent un personnage en le
  regardant, et écrivent le fichier de configuration. L'overlay client
  (`/dialoguec debug`) montre le nœud courant et chaque réponse avec sa
  nuance et son verrou.
- **Outils Easy NPC préservés.** Un clic accroupi, ou avec un objet d'Easy NPC
  en main (baguette, déplacement, presets), n'ouvre jamais de conversation.

## Dépendances

| mod | rôle |
|---|---|
| `haute_capitale_rpg` ≥ 0.3.0 | module caméra (`CameraFocus`, `CameraOverrideManager`) — **obligatoire** |
| `easy_npc` 7.8.x | source des dialogues — facultatif (le mod démarre sans, mais ne présente rien) |
| `spell_engine`, `bettercombat` | compat conditionnelle, jamais requis |

## Éligibilité d'un personnage

Ordre de décision : tag `hc_dialogue_off` (interdit) → tag `hc_dialogue`
(force) → surcharge par UUID dans la config → `actif_par_defaut` (vrai).

En jeu : `/dialogue pnj on|off|effacer` en regardant le personnage, ou
`/tag @e[...] add hc_dialogue`.

## Configuration

`config/haute_capitale_dialogue.json` (serveur) : distances, drapeaux (verrou du
joueur, Échap, HUD, perspective, reprise après cinématique, exclusivité),
profil de caméra par défaut, et surcharges par UUID. Un personnage `exclusif`
déjà en conversation **refuse** tout autre joueur : message, et aucun écran —
pas même celui d'Easy NPC. Même chose pour un véhicule en route en politique
`refuser`.

```json
"pnj": {
  "29527e21-4d01-46d8-8f8c-f8b01d48ec5d": { "recul": 1.4, "decalage": 1.2, "hauteur_cible": 0.9, "exclusif": true }
}
```

**Cadrage** (bloc `camera`) : un plan d'épaule **ancré sur le joueur**. La
caméra part de ses yeux, recule de `recul_*` blocs, se décale de
`decalage_*` blocs vers l'épaule, monte de `elevation`, puis regarde le
personnage en l'écartant du centre de `angle_visee_*` degrés vers le côté
opposé à l'épaule : le joueur occupe le bord de l'image, le personnage la
zone centrale, à toute distance. Recul, décalage et visée glissent entre la
valeur `_proche` (personnage à moins de `distance_proche`) et la valeur
`_loin` (au-delà de `distance_loin`), et ne bougent plus au-delà : le joueur
ne sort jamais du cadre. Par défaut proche 1,5 / 1,35 / 10° et loin 1,75 /
0,8 / 6°, entre 1,5 et 5 blocs. Un mur du côté de l'épaule fait passer la
caméra à l'autre épaule, puis plus haut et plus près, et en dernier recours
aux yeux du joueur ; le côté retenu est gardé tant qu'il reste praticable.

`config/haute_capitale_dialogue_client.json` (client) : typewriter (suit
Easy NPC par défaut), échelle du texte, opacité, `fermeture_automatique`
(fausse : l'écran attend Échap), réduire les animations, ne pas
changer de perspective, éléments de HUD masqués, libellés des conditions.

## Commandes

- `/dialogue debug` — ma session ;
- `/dialogue sessions` · `/dialogue fermer <joueur>` · `/dialogue recharger` (GM) ;
- `/dialogue pnj on|off|effacer` (GM) ;
- `/dialoguec debug` (client) — overlay d'état ; `/dialoguec fermeture` (client)
  — la conversation se referme-t-elle toute seule quand le personnage se tait ?
  (non par défaut) ; `/dialoguec fin` — sortie de secours.

## Tests

- `./gradlew build` : harnais hors jeu (`DialogueTest`, `CadrageTest`,
  `CaptureTest` — le tri des messages de chat sur des textes construits comme
  les `tellraw` du datapack) ;
- `./gradlew runClient -PdialogueSelfTest` : autotest en jeu dans le monde
  `hcdtest` (personnages posés par commande, clics par le vrai chemin
  d'interaction, mesures de caméra et d'état ; scénarios 7–10 = capture :
  réplique et choix, `/trigger` rejoué, salve suivante, congé, silence (l'écran
  reste ouvert, puis se referme si `fermeture_automatique` est demandée),
  messages étrangers, entité non éligible ; scénarios 11–13 = véhicule :
  personnage porté déplacé d'un pas par tick, bascule simplifié/complet en
  cours de conversation, ouverture en route, politique « refuser » par la
  sonde ; scénarios 14–16 = retour au dialogue après le commerce d'Easy NPC,
  reprise après cinématique au même nœud, reprise d'une conversation
  capturée ; scénarios 17–19 = étiquettes de style, historique, panneau seul,
  `/dialogue pnj set` et `oublier`), verdict dans `run-client/logs/latest.log`. Lancer avec
  `--no-daemon` si une autre compilation tourne sur la machine : un arrêt du
  démon Gradle partagé tue le client de test en plein scénario. La fenêtre du client de
  test est cachée dès le début : visible, elle capturerait la souris et le
  clavier de la personne qui utilise la machine. Le client de test doit
  autoriser `tellraw` et `scoreboard` dans
  `run-client/config/easy_npc/security.cfg` ;
- `bash tools/banc-multi.sh` : banc multijoueur (serveur dédié + deux clients
  détachés pilotés par RCON) — nœuds indépendants, dégâts bloqués,
  exclusivité, kick, et capture d'un dialogue de chat sur vrai réseau.
