# Typewriter Calendar — Haute Capitale 1.1.0-hc40m

## Base
- Fichier fourni le 30/09/2026 : `vr_typewriter_calendar_counter_annee-mois-jour_365j_v2_lisible_1.21.11 (2).jar`
- SHA-256 base : `e700fdcae5a9d683aa2fba1acc587eb2b1dfd1a4d9bc1b7c17b67a5a5b8bdcf7`
- Mod id conservé : `mr_typewriter_daycounter`

## Compatibilité cycle 40 minutes
Le calendrier ne convertit pas une durée réelle en jour. Il lit directement `time query day`. Avec le nouveau système qui ralentit uniquement `timeOfDay` à 0,5x, la date avance donc naturellement toutes les 40 minutes.

Aucun tickrate, random tick, cooldown, météo ou autre minuterie n'est modifié par ce patch.

Seule adaptation nécessaire : la fenêtre d'initialisation quotidienne `time query daytime = 1..80` devient `1..40`. Puisque chaque unité de `timeOfDay` dure deux ticks serveur, cela conserve la fenêtre réelle d'environ quatre secondes du mod d'origine.

## Calendrier impérial affiché
1. Givre
2. Dégel
3. Semailles
4. Averses
5. Frondaisons
6. Longs-Jours
7. Braises
8. Hautes Lumières
9. Récoltes
10. Cuivres
11. Brumes
12. Renouveau

Le score numérique `month` reste utilisé en interne pour le calcul 365 jours/an. L'actionbar affiche désormais le nom, par exemple : `ANNÉE 7 / HAUTES LUMIÈRES / JOUR 14`.

## Biomes
Audit du JAR : aucune référence de biome n'est présente. Aucun ancien identifiant de biome/ville n'est donc à migrer dans ce module.

## Validation locale
- archive JAR/ZIP valide ;
- tous les JSON parsables ;
- chemins legacy et overlay 1.21+ synchronisés ;
- 12 noms impériaux présents dans l'animation quotidienne et annuelle ;
- plus aucun rendu `/ MOIS <numéro>` ;
- tous les composants JSON des commandes `title ... actionbar` parsables ;
- fenêtre `1..40` présente, ancienne fenêtre `1..80` absente.

Build local livré : `Typewriter-Calendar-HauteCapitale-1.21.11-HC40M-1.1.0.jar`
SHA-256 local : `56b3566bbd3f2678b327de418243c8b1f2b2305edeaf8ebb192184dd174ee4cf`

Source de reconstruction : `tools/patches/patch_typewriter_calendar_hc40m_1_1_0.py`.
