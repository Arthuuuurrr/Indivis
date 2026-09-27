# Contribuer à Indivis

## Principe

Chaque changement doit être traçable. Une modification importante ne doit pas apparaître directement sur `main` sans issue ni Pull Request.

## Workflow Arthur / Clément

1. **Chercher l'Issue existante** : avant toute création, rechercher le sujet avec plusieurs formulations pertinentes.
2. **Issue** :
   - si une issue existe déjà, la compléter / corriger ;
   - créer une nouvelle issue uniquement si aucun ticket existant ne couvre réellement le sujet.
3. **Roadmap** :
   - appliquer une priorité P1/P2/P3/P4 ;
   - appliquer les labels de domaine pertinents ;
   - affecter le **Milestone** correspondant ;
   - assigner le responsable lorsqu'il est connu.
4. **Branche** : partir de `main`.
5. **Travail** : commits courts et compréhensibles.
6. **Test local / serveur** : vérifier le comportement concerné.
7. **Pull Request** : résumer les changements et les tests.
8. **Review** : corriger les points signalés.
9. **Merge** : fusion dans `main` une fois validé.

## Règles de suivi des Issues

### Ne pas créer de doublon

Avant de créer une issue :
- rechercher le nom du mod / module ;
- rechercher le symptôme ;
- rechercher les noms d'items, classes, quêtes ou systèmes concernés ;
- vérifier les issues ouvertes **et fermées** lorsqu'il s'agit d'une régression.

Si une ancienne issue couvre déjà le même problème ou sa régression, **la réouvrir / mettre à jour** plutôt que créer un nouveau ticket.

### Labels obligatoires

Chaque issue opérationnelle doit recevoir :
- une priorité :
  - `Priorité maximale -P1`
  - `Problème prioritaire secondaire -P2`
  - `A régler -P3`
  - `A traiter -P4`
- les labels de domaine utiles : `Gameplay`, `Technique`, `UI / UX`, `Level Design`, `Équilibrage`, `Audio / Musique`, `Quêtes / Dialogues`, `Performance`, etc. ;
- `bug` lorsqu'il s'agit réellement d'un défaut.

### `A tester / Valider`

Ne **pas** appliquer ce label par défaut.

Il est réservé aux cas où :
- un correctif candidat existe réellement ;
- une version / branche / PR / build est disponible ;
- il reste une validation runtime ou fonctionnelle à effectuer.

Une issue simplement signalée, en triage, non traitée ou seulement assignée **ne doit pas** porter `A tester / Valider`.

### Milestones = roadmap active

Le Project V2 / Kanban historique est conservé uniquement à titre historique.

La source de vérité active est :
- Issues ;
- Milestones ;
- labels ;
- assignees.

Chaque issue opérationnelle doit être rattachée au Milestone approprié :
1. `0 — Validation technique`
2. `1 — Progression & combat`
3. `2 — Monde & biomes`
4. `3 — Quêtes & narration`
5. `4 — Donjons & boss`
6. `5 — Contenu bêta & finition`

L'issue #93 est le point d'entrée transversal de la roadmap et n'a pas besoin d'être rattachée à une phase unique.

## Nommage des branches

- `fix/<sujet>`
- `feat/<sujet>`
- `refactor/<sujet>`
- `docs/<sujet>`
- `chore/<sujet>`

Éviter les branches génériques comme `test`, `nouveau` ou `version2`.

## Commits

Format :

`type(scope): description`

Types conseillés : `fix`, `feat`, `refactor`, `docs`, `test`, `chore`.

Un commit doit idéalement représenter un changement logique unique.

## Pull Requests

Une PR doit préciser :

- le problème traité ;
- les fichiers/systèmes touchés ;
- les changements réalisés ;
- les tests effectués ;
- les points restant à vérifier ;
- l'issue liée.

Ne pas mélanger plusieurs sujets indépendants dans une seule PR.

## Priorités

- **P0** : crash, corruption/perte de progression, blocage majeur du serveur.
- **P1** : progression bloquée, système majeur cassé, exploit important.
- **P2** : bug significatif, équilibrage, fonctionnalité importante.
- **P3** : polish, confort, amélioration visuelle ou dette technique non bloquante.

## Statuts

Les réactions Discord historiques sont traduites ainsi :

- ✅ = résolu ;
- ⛏️ = en cours, sans pourcentage supposé ;
- 🔴 = non traité ;
- absence de réaction = à trier / statut non verrouillé.

`status:in-progress` peut être utilisé pour un chantier réellement actif.

Les nouvelles tâches doivent être suivies par GitHub Issues plutôt que par réactions Discord.
