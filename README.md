# Indivis

Dépôt de travail du serveur Minecraft **L'Indivis / Haute Capitale**.

Ce dépôt sert de source de vérité pour le développement : code source, datapacks, documentation, roadmap, bugs, branches de travail et historique des changements.

## Organisation

- `mods/` : sources ou références source des mods développés/maintenus pour le projet.
- `datapacks/` : datapacks du serveur.
- `docs/` : architecture, spécifications, tests et suivi.
- `artifacts/` : politique et suivi des artefacts compilés.
- `tools/` : scripts et outils de développement.
- `.github/` : modèles d'issues et de pull requests.
- `server-manifest.yml` : versions et SHA-256 de référence du modpack/datapacks.

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

- [Roadmap stratégique](ROADMAP.md)
- [Roadmap d'exécution](docs/EXECUTION_ROADMAP.md)
- [Registre des modules](docs/MODULES.md)
- [Manifest serveur](server-manifest.yml)
- [Priorités et statuts](docs/STATUS.md)
- [Bug reports des testeurs](docs/TESTER_REPORTS.md)
- [Historique des éléments résolus](docs/RESOLVED.md)

## Convention rapide des branches

- `fix/<issue>-<sujet>` : bug
- `feat/<issue>-<sujet>` : fonctionnalité
- `refactor/<issue>-<sujet>` : refactor
- `docs/<sujet>` : documentation
- `chore/<sujet>` : maintenance

Exemples : `fix/55-capskills-tree`, `feat/52-safe-city-biomes`.

## Convention des commits

Format recommandé :

`type(scope): description`

Exemples :

- `fix(skills): prevent spell reset on reconnect`
- `feat(core): add per-player chest loot`
- `docs(roadmap): update beta priorities`

## Versions binaires

Le dépôt contient en priorité les **sources** et les fichiers nécessaires au build. Les JAR distribuables sont identifiés par SHA-256 dans `server-manifest.yml` et devront à terme être produits/attachés par GitHub Actions et Releases, plutôt que devenir la source de vérité.
