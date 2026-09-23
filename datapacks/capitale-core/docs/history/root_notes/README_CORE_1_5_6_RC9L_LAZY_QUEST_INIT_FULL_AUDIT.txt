Capitale Core 1.5.6 RC9l — lazy QUEST init full audit test

Objectif : tester une architecture où les objectifs QUEST_* restent déclarés globalement au /reload, mais où les scores individuels de quête ne sont plus créés en masse dans l'initialisation joueur.

Changements :
- Retrait des lignes scoreboard players add/set @s QUEST_* 0 dans player/ensure_runtime_self.mcfunction, player/init.mcfunction et player/migrate_v26.mcfunction.
- Conservation des resets explicites d'admin/reset_self.mcfunction.
- Conservation du reset explicite de QUEST_SPAWN et QUEST_PROLOGUE au démarrage volontaire du prologue.
- Ajout de lazy init locale avant les lectures de scores QUEST_* dans les fonctions qui les testent directement.

Points d'entrée couverts :
- player/bootstrap_runtime_v101_self.mcfunction
- quests/spawn/complete.mcfunction
- npc/libraire/interact_self.mcfunction
- npc/libraire/near_self.mcfunction
- npc/libraire/work_offer_self.mcfunction
- player/ensure_runtime_self.mcfunction pour la migration legacy Relais du Cœur

Note : les sélecteurs tick sur scores non-zéro ne nécessitent pas de création du score à 0 ; un score absent ne doit pas matcher une quête en cours.
