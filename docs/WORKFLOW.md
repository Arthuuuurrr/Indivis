# Workflow Git

## Modèle retenu

Le projet utilise un **GitHub Flow simple** :

```
Issue
  ↓
branche dédiée
  ↓
commits
  ↓
Pull Request
  ↓
tests / review
  ↓
merge vers main
```

Il n'y a pas de branche `develop` permanente afin de garder le processus simple.

## Règles

- `main` = état stable de référence.
- une branche = un sujet principal ;
- une PR = un changement testable ;
- toute régression découverte après merge devient une nouvelle issue ;
- les ZIP envoyés sur Discord ne doivent plus constituer la référence de version.

## Exemple

Issue :

`[P0][BUG] Crash lors du heal avec armure enchantée`

Branche :

`fix/enchanted-armor-heal-crash`

Commits :

`fix(arsenal): guard enchant heal event`

PR :

`fix: prevent enchanted armor heal crash`

## Travail sur plusieurs mods

Une PR peut toucher plusieurs modules uniquement si les changements sont réellement liés. Exemple : intégration CapSkills + Arsenal + Haznstuff.

Sinon, créer des PR séparées pour faciliter le diagnostic et les retours arrière.

## Rollback

Si une PR provoque une régression importante :

1. identifier le merge fautif ;
2. revert proprement ;
3. rouvrir ou créer l'issue ;
4. corriger sur une nouvelle branche ;
5. retester avant nouveau merge.
