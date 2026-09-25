# Spell Engine TEST3 RC13 — Axe Icon Fallback

RC13 est un hotfix de ressources construit sur RC12. **Aucune classe Java n'est modifiée.**

## Symptôme

La technique d'arme au clic droit des haches de Berserker affiche encore une texture manquante.

## Identification

`WeaponsRegister.berserker_axes(...)` assigne comme spell natif :

`more_rpg_classes:decapitate`

Le nœud CapSkills correspondant est `ability_berserker_more_rpg_classes_decapitate` (« Décapitation ») et utilise :

`more_rpg_classes:textures/spell/decapitate.png`

Le mapping RC12 contient déjà ce couple spell → texture. Le hotfix ne touche donc pas au mapping.

## Correction

Ajout d'un fallback de ressource à ce chemin exact :

`assets/more_rpg_classes/textures/spell/decapitate.png`

L'image est l'asset upstream More RPG Library, branche `1.21.1`, Git blob `6aad6307fe31a29a0eee4081a184bebd813b9c4c`.

SHA-256 des octets utilisés :

`7f972f91fe7de5960550aea89c4cd87c01636c57374f7905f464c1cd8ce1dbf7`

## Invariants RC12 → RC13

- 968 classes : 0 modifiée ;
- aucun spell JSON modifié ;
- aucun cooldown/cast-time modifié ;
- aucun changement à `SpellImpacts` ;
- aucun changement à `ClientCastController` ;
- aucun changement de position HUD ;
- seul `fabric.mod.json` change parmi les entrées partagées ;
- une seule nouvelle entrée : l'icône Décapitation.

Voir le rapport `docs/spell-integration/archive/VALIDATION_SPELL_ENGINE_RC13_AXE_ICON_FALLBACK.txt`.
