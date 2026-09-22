# Indivis

Dépôt de travail du serveur Minecraft **L'Indivis / Haute Capitale**.

Ce dépôt sert de source de vérité pour le développement : code source, datapacks, documentation, roadmap, bugs, branches de travail et historique des changements.

## Organisation

- `mods/` : sources des mods développés ou maintenus pour le projet.
- `datapacks/` : datapacks du serveur.
- `docs/` : architecture, spécifications, tests et suivi.
- `tools/` : scripts et outils de développement.
- `.github/` : modèles d'issues et de pull requests.

## Règle principale

`main` doit rester **stable et validée**. On évite les modifications directes sur `main`.

Workflow :

1. créer ou sélectionner une issue ;
2. créer une branche courte dédiée ;
3. développer et tester ;
4. ouvrir une Pull Request ;
5. corriger si nécessaire ;
6. fusionner dans `main` après validation.

Voir [CONTRIBUTING.md](CONTRIBUTING.md) et [docs/WORKFLOW.md](docs/WORKFLOW.md).

## Suivi du projet

- [Roadmap](ROADMAP.md)
- [Priorités et statuts](docs/STATUS.md)
- [Bug reports des testeurs](docs/TESTER_REPORTS.md)
- [Historique des éléments résolus](docs/RESOLVED.md)

## Convention rapide des branches

- `fix/<sujet>` : bug
- `feat/<sujet>` : fonctionnalité
- `refactor/<sujet>` : refactor
- `docs/<sujet>` : documentation
- `chore/<sujet>` : maintenance

Exemples : `fix/enchanted-armor-heal-crash`, `feat/per-player-loot`.

## Convention des commits

Format recommandé :

`type(scope): description`

Exemples :

- `fix(skills): prevent spell reset on reconnect`
- `feat(core): add per-player chest loot`
- `docs(roadmap): update beta priorities`

## Versions binaires

Le dépôt doit contenir en priorité les **sources** et les fichiers nécessaires au build. Les JAR distribuables doivent idéalement être publiés via les Releases/artefacts plutôt que servir de source de vérité.
