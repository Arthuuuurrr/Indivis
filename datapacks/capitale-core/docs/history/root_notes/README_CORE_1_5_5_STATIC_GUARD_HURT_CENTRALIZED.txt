CAPITALE CORE 1.5.5 — HOTFIX STATIC GUARD HURT CENTRALIZED

Objectif : centraliser la punition directe des attaques contre les gardes statiques dans les fonctions on_hurt_self, afin que l’UI EasyNPC n’ait plus besoin d’une action séparée `damage @s 8 minecraft:explosion`.

Fonctions modifiées :
- capitale:npc/gardecoeur/static_port/on_hurt_self
- capitale:npc/gardeport/static_port/on_hurt_self
- capitale:npc/gardeprofondeurs/static_port/on_hurt_self

Réglage EasyNPC recommandé pour les PNJ concernés :
- garder une seule action On Hurt / attaqué par joueur : `function capitale:npc/<garde>/static_port/on_hurt_self`
- conserver executeAsUser=true
- supprimer l’ancienne action séparée : `damage @s 8 minecraft:explosion`

Remarque : le warning EasyNPC peut encore apparaître si l’événement On Hurt se déclenche sans joueur-source ; en revanche il ne doit plus apparaître deux fois par événement à cause de deux actions séparées.
