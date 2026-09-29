# NexusCharacters — BETA 1.0 PRE8

Issue : #138

Base exacte : **BETA 1.0 PRE7** `1.0.0-beta.7+hc.1.21.11`.

## Objet

PRE8 ne change pas la persistance. Il supprime uniquement le bruit console émis par le checkpoint périodique Nexus.

Correctifs ciblés :

- `PuffishSkillsBridge.save` conserve toute la sauvegarde mais n'émet plus le log INFO de succès `Puffish Skills saved...` ;
- `ProfileStateBridge.save` conserve toute la sauvegarde mais n'émet plus le `System.out.println` de succès `Profile scores saved...` ;
- `ProfileStateBridge.runHookIfCorePresent` conserve les hooks `before_save_self` / `after_load_self`, mais exécute leur commande avec `ServerCommandSource.withSilent()` (intermediary `class_2168.method_9217()`).

## Garanties

PRE8 ne modifie pas :

- le checkpoint 100 ticks / 5 s ;
- les appels `PuffishSkillsBridge.save` et `ProfileStateBridge.save` ;
- les données écrites ;
- les chemins/fichiers de sauvegarde ;
- les diagnostics WARN/ERR en cas d'échec ;
- la restauration ;
- les sauvegardes de déconnexion / arrêt ;
- les cosmétiques et le layout PRE7.

Le build valide que seules `ProfileStateBridge.class`, `PuffishSkillsBridge.class` et `fabric.mod.json` diffèrent de PRE7.
