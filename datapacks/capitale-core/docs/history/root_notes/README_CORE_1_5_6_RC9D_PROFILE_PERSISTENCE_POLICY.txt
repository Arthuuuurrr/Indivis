# Capitale Core 1.5.6 RC9d — Profile Persistence Policy Sync

## But

Cette version formalise le contrat entre le datapack `capitale_core` et le mod HUD/profils : le mod ne doit plus sauvegarder une liste Java figée de réputations, quêtes ou accès. Il doit sauvegarder les objectifs par convention de nommage, puis appeler le hook datapack qui reconstruit les valeurs dérivées.

## Hooks à appeler par le mod

Avant snapshot du slot actif :

```mcfunction
function capitale:profiles/before_save_self
```

Après restauration complète du slot choisi :

```mcfunction
function capitale:profiles/after_load_self
```

## Politique de sauvegarde côté mod

Le fichier contractuel ajouté est :

```text
data/capitale/profile_persistence/policy.json
```

Le mod doit énumérer dynamiquement les objectifs scoreboard existants et sauvegarder les scores du joueur si l'objectif correspond aux préfixes persistants, en respectant les exclusions.

Préfixes persistants principaux :

```text
REP_
QUEST_
ACCESS_
GUILD_
JOB_
FACTION_
CAPSK_
CAP_
```

Exemples qui seront automatiquement pris en charge si ajoutés plus tard :

```text
REP_GUILDE_MAGES
REP_GUILDE_VOLEURS
QUEST_MAGES_MAIN
QUEST_VOLEURS_INITIATION
ACCESS_GUILDE_VOLEURS
GUILD_MAGES_RANK
JOB_ALCHIMISTE_LEVEL
```

## Valeurs sources à restaurer depuis le slot

Exemples :

```text
CAP_RANGSOCIAL
CAP_CRIME
REP_*
QUEST_*
ACCESS_*
CAP_JCOINS
CAPSK_*
CAP_DAILY_*
CAP_CD_*_END
```

## Valeurs dérivées à recalculer après chargement

Ces valeurs ne doivent pas être la source principale. Le core les reconstruit via `capitale:profiles/after_load_self` :

```text
CAP_RANGCLASSE
CAP_CRIMECLASSE
CAP_REPCLASSE
CAP_DIALOGUE
CAP_TRAITEMENT
CAP_JUSTICE_LEVEL
CAP_AMENDE
CAP_COMPARUTION
tags ACCESS_*
préfixe CustomName
cooldowns restants calculés depuis *_END
```

## Changements datapack

Nouvelles fonctions :

```mcfunction
capitale:profiles/pre_save_runtime_flush_self
capitale:profiles/resync_runtime_after_profile_load_self
capitale:profiles/persistence_contract_self
capitale:justice/state/sync_silent_self
```

Fonctions modifiées :

```mcfunction
capitale:profiles/before_save_self
capitale:profiles/after_load_self
capitale:profiles/resync_after_profile_load_self
capitale:profiles/mod_hooks_self
capitale:profiles/menu_self
```

## Ordre de reconstruction après chargement

```mcfunction
function capitale:player/ensure_runtime_self
function capitale:bounds/rangsocial_self
function capitale:bounds/crime_self
function capitale:bounds/reputation_all_self
function capitale:logic/classify/base_self
function capitale:justice/state/sync_silent_self
function capitale:access/recalculate_by_rank_self
function capitale:access/sync_tags_self
function capitale:quest/journalieres/cooldown/sync_self
function capitale:display/prefix/sync_self
```

## Point important

Le datapack ne peut pas, à lui seul, forcer le mod Java à sauvegarder des objectifs par préfixes. Cette version fournit le contrat et les hooks stables. Le thread mod HUD/profils doit adapter le snapshot Java pour lire `policy.json` ou appliquer la même politique codée côté Java.
