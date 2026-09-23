RC9n — Q01 Mira pathfinding scene

Base : RC9m.

Objet : corriger la première quête secondaire des Bas-Anneaux pour prévoir une vraie scène de collision/fuite de Mira avec pathfinding EasyNPC, tout en gardant la logique de quête côté scoreboard.

Changements :
- Ajout de quest/side/bas_anneaux/q01_quartier_oublies/start_collision_self.mcfunction.
- interact_self lance désormais la scène si QUEST_SIDE_BA_Q01 = 0.
- accept_self et offer_self deviennent des alias de compatibilité vers start_collision_self.
- talk_merchant_self accepte aussi QUEST_SIDE_BA_Q01 = 0 comme fallback anti-blocage si Mira est temporairement absente parce qu’elle est encore sur sa route.
- near_self de Mira réagit aussi à l’étape 30.

Configuration EasyNPC recommandée :
- Mira : interaction principale -> function capitale:npc/mira/interact_self.
- Mira : route courte marché -> collision/étal -> ruelle -> retour position initiale.
- Le déplacement doit rester géré par EasyNPC. Le datapack ne valide pas la quête en fonction de l’arrivée physique de Mira.
- Marchand : interaction -> function capitale:quest/side/bas_anneaux/q01_quartier_oublies/talk_merchant_self.

Note multi-joueur :
- Un PNJ EasyNPC mobile est global. Pendant quelques secondes, Mira peut être hors de son point de départ.
- Ce n’est pas bloquant si sa route retourne automatiquement au point initial.
- Le marchand peut amorcer l’étape en fallback si Mira est absente ou si le joueur n’a pas vu la collision.
