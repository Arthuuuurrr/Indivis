# Spell Engine TEST3 RC10 — Cast Safety

RC10 est construit sur RC9 et conserve donc :

- HUD spellbar `x=-185, y=-11` ;
- synchronisation de 216 icônes CapSkills ;
- déduplication ACTIVE ;
- gate/déduplication Arsenal ;
- filtres heal ;
- compatibilité Hazenn movement/summons.

## Correctifs RC10

### 1. CHANNEL

Après un démarrage réussi, SpellHotbar debounce le use-case `START` jusqu’au key-up.

RC9 réutilisait `STOP_released` pour le restart du bloc CASTING/CHANNEL lorsque le process précédent était terminé. RC10 utilise `START_released` pour CHANNEL uniquement.

Conséquence : un CHANNEL effectue un cycle fini par pression physique ; maintenir la touche après la fin ne relance pas immédiatement un nouveau process. Un relâchement puis une nouvelle pression permet un nouveau cast.

### 2. Haste / durée

`SpellParameters.hasteAffectedValue(base, haste)` est protégé contre :

- NaN / Infinity ;
- haste <= 0 ;
- haste positive sous le plancher normalisé de Spell Power (`0.1`) ;
- résultat non fini.

Spell Power définit HASTE avec default=100, min=10, max=1000.

## Audits

`audit_cast_definitions.py` : abilities visibles CapSkills.

Attendu :
- 221 spells visibles ;
- 170 actifs ;
- 33 CHANNEL ;
- 14 CHARGE ;
- 0 erreur.

`audit_all_spells.py` : univers complet des JAR + overrides CapSkills.

Attendu :
- 319 ressources ;
- 191 actives ;
- 33 CHANNEL ;
- 14 CHARGE ;
- 0 erreur.

Il n’existe donc aucun CHANNEL caché supplémentaire hors arbre dans le corpus audité.

## Harness

`TestHaste.java` couvre :
- haste normal ;
- accélération ;
- ralentissement ;
- plancher 0.1 ;
- zéro ;
- négatif ;
- NaN ;
- +/-Infinity ;
- valeur positive subnormale.

## Runtime

Voir `docs/spell-integration/RUNTIME_MATRIX.md` et l’issue #83.
