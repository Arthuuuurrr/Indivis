Capitale Core 1.5.6 RC9ad — prologue suite + vétéran dialogue fix

Base : RC9ac fourni par Arthur.

Correctifs ciblés :
- ajoute les entrées manquantes du prologue après Géraud/Althéon : Léovic, Aurèle, Roch, Colin, Magistrat, Aubergiste ;
- raccorde les owners QuestChoix historiques 2 à 7 ;
- ajoute la journalière bière vétéran owner 8 avec choix 100/101 + récit 102/103/104 + remise 105/106 ;
- corrige le cas « Ce choix n’est plus lié à un dialogue actif » pour les choix de la bière ;
- ajoute interact_player et story_listen_player pour éviter les erreurs EasyNPC en @a ;
- migre les joueurs déjà bloqués après QUEST_SPAWN=100 / QUEST_PROLOGUE=20 vers QUEST_GARDEPORT=10 ;
- ajoute des placeholders d’escorte non-opérants mais sûrs pour éviter des appels vers des fonctions absentes dans RC9ac.

Limite :
- les anciennes escortes cinématiques complètes ne sont pas recréées. Cette version privilégie un prologue jouable/stable en dialogues ciblés @s plutôt qu’une reconstruction hasardeuse de pathfinding.
