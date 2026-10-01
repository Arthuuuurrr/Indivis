# Haute Capitale Dialogue b9 patch

Base binaire conservée : `haute-capitale-dialogue-0.1.0+1.21.11.b8.jar`.

Cette source compile uniquement les ajouts b9 et les fusionne dans une copie du b8. Les classes
existantes de Clément ne sont pas recompilées ou remplacées.

## Corrections

### Faux choix contenant un nom de joueur

Le b8 assimilait tout composant doté d'un `ClickEvent` à une réponse de dialogue. Les noms de
joueurs cliquables pouvaient donc entrer dans le panneau de choix.

b9 conserve uniquement les composants `ClickEvent.RunCommand` dans cette voie. Les commandes
réelles de choix restent inchangées ; les `SuggestCommand`, liens et autres composants
cliquables ne sont plus capturés comme réponses.

### Interpellation caméra

Nouvelle commande interne :

- `hcd_interpellation nearest_named <nom>` pour les interpellations de quête ;
- `hcd_interpellation auto_guard` pour les contrôles d'accès.

Le sélecteur cible uniquement les entités `easy_npc:*` vivantes dans un rayon de 14 blocs.
Pour les portes à deux gardes, le score combine distance et direction du regard du joueur afin de
préférer le garde réellement en face plutôt qu'un choix arbitraire.

Le client déclenche automatiquement `auto_guard` uniquement lorsqu'il voit le flux de refus
d'accès comportant à la fois les choix « suivre le garde » / « tenter un arrangement » et un
`/trigger CapChoix set ...`. Les simples répliques de proximité ne déclenchent rien.

Les interpellations de quête sont ajoutées explicitement dans le Core, uniquement sur les scènes
identifiées comme démarrage/interception de quête.

## Intouché

- `DialogueCameraController`
- `DialogueManager`
- `DialoguePanel`, y compris le comportement `...`
- `CaptureDialogueScreen`
- `RpgDialogueScreen`
- réseau/session existants
- EasyNPC direct
- BetterCombat
- logique Creature/Orcs

Le `fabric.mod.json` final passe en b9 et retire la mention `ARR`, sans ajouter une licence de
remplacement.
