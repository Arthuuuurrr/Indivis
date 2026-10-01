# Audit b8 -> b9 — Haute Capitale Dialogue

Base analysée : `haute-capitale-dialogue-0.1.0+1.21.11.b8.jar`
SHA-256 : `428c771ef958e56245628bb145a198f04715e5e7a21d5a855ede673b5c7b25c0`

## Cause du faux choix joueur

`MessageCapture.extraireChoix()` du b8 regroupe tout segment qui possède un `ClickEvent`,
sans vérifier son type. `classer()` retourne ensuite ces choix avant les filtres de locuteur.
Un composant cliquable de nom de joueur peut donc être affiché comme réponse.

Correction b9 : le mixin annule uniquement la finalisation des segments dont le ClickEvent n'est
pas un `ClickEvent.RunCommand`. Les vrais choix à commande restent inchangés.

## Interpellations

La caméra existante n'est pas modifiée. b9 ouvre une session CAPTURE via le
`DialogueManager` b8 existant.

Deux entrées internes :
- `hcd_interpellation nearest_named <nom>`
- `hcd_interpellation auto_guard`

Le ciblage serveur est limité aux `easy_npc:*` vivants à 14 blocs. Pour les portes à deux gardes,
le classement combine distance et direction de regard afin de privilégier le garde devant le joueur.

## Accès

Le Core RC9AY fourni contient les objectifs `ACCESS_*` mais pas les fonctions externes qui
produisent le couple de choix « suivre le garde » / « tenter un arrangement ». b9 ne devine donc
pas à partir d'un simple score ACCESS.

Le client ne déclenche `auto_guard` que si le message reçu contient :
- le texte correspondant à « suivre » + « garde » + « arrangement » ;
- au moins un ClickEvent RunCommand vers `/trigger CapChoix set ...`.

Les lignes de proximité ordinaires ne correspondent pas à ce filtre.

## Quêtes explicitement raccordées dans RC9BA

- Géraud Rivet : démarrage du prologue ;
- Althéon Brumeforge : interception capitaine ;
- Léovic : offre au Port ;
- Aurèle Veyrane : interception sortie d'ascenseur ;
- Roch Vallet : interception vers les Profondeurs.

## Éléments volontairement intouchés

- `DialogueCameraController.class`
- `DialogueManager.class`
- `DialoguePanel.class` et le `...`
- `CaptureDialogueScreen.class`
- `RpgDialogueScreen.class`
- protocole réseau existant
- compat BetterCombat
- logique EasyNPC directe
- logique Creature / Orc
