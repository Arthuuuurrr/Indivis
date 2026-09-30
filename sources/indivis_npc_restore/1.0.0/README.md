# Indivis NPC Restore 1.0.0

Datapack de récupération pour Minecraft 1.21.11 / Easy NPC 7.12.1.

Il utilise `easy_npc spawn <uuid>` afin de restaurer les données présentes dans l'index EasyNPC actuel. Le code EasyNPC 7.12.x récupère la dimension enregistrée et met à jour une entité déjà chargée portant le même UUID au lieu de créer un doublon. Si l'UUID n'existe plus dans l'index, la commande échoue sans recréer arbitrairement le PNJ.

Modes :
- `restore_story` : PNJ de quête/histoire identifiés ;
- `restore_safe` : PNJ vus dans les logs du 30/09/2026 + histoire ;
- `restore_all_known` : tous les UUID retrouvés dans les backups fournis et les logs récents, à utiliser seulement en dernier recours car un PNJ volontairement *despawned* mais encore indexé peut revenir ;
- `reset` : annule le verrou et les fonctions planifiées.

Les commandes sont découpées en lots de 8 avec un délai de 2 secondes. Rien ne s'exécute automatiquement au chargement du datapack.

Procédure : sauvegarder le monde et les données EasyNPC, conserver `accessories_compat_layer 0.1.13.b1+hc.nullcapguard1`, installer Easy NPC Core + Config UI 7.12.1 côté serveur et client, puis utiliser d'abord `restore_story`, ensuite `restore_safe` si nécessaire.
