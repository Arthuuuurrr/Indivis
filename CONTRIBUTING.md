# Contribuer à Indivis

## Principe

Chaque changement doit être traçable. Une modification importante ne doit pas apparaître directement sur `main` sans issue ni Pull Request.

## Workflow Arthur / Clément

1. **Issue** : décrire le bug ou la tâche.
2. **Branche** : partir de `main`.
3. **Travail** : commits courts et compréhensibles.
4. **Test local / serveur** : vérifier le comportement concerné.
5. **Pull Request** : résumer les changements et les tests.
6. **Review** : corriger les points signalés.
7. **Merge** : fusion dans `main` une fois validé.

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
- l'issue liée, si elle existe.

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

Les nouvelles tâches doivent être suivies par GitHub Issues plutôt que par réactions Discord.
