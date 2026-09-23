Capitale Core 1.5.6 RC9j — identity retry stale ACK fix

Base: RC9i.

Correction ciblée:
- identity_open_mandatory_self retire désormais le tag `capitale.identity.created` avant d'armer le retry du menu identité obligatoire.

Raison:
- `capitale.identity.created` est un tag joueur global ajouté par le HUD après validation d'identité.
- Sur un joueur ayant déjà un personnage validé, la création d'un nouveau slot pouvait hériter temporairement de cet ancien tag.
- RC9i arrêtait le retry dès qu'il voyait ce tag, ce qui pouvait empêcher l'ouverture obligatoire du menu sur un nouveau slot.

Ce qui ne change pas:
- aucune valeur de quête déplacée ou réinitialisée;
- aucun accès/réputation/justice modifié;
- aucun changement PNJ, marchand, patrouille;
- le retry reste temporaire et s'arrête après validation ou timeout.
