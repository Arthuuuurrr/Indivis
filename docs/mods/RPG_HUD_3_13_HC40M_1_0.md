# RPG-HUD 3.13 — Haute Capitale HC40M 1.0

## Cause du bug

RPG-HUD 3.13 lit directement `World#getTimeOfDay()` dans `HudElementClockVanilla#getTime()` et `getClockColor()`.

Avec `HauteCapitale-DayCycle-1.21.11-1.0.0`, le serveur fait avancer le temps autoritaire à **0,5x**. Le client Minecraft continue cependant sa prédiction locale du temps à vitesse vanilla entre deux paquets de synchronisation, puis reçoit une correction serveur vers l'arrière. L'horloge RPG-HUD affichait donc brièvement la minute suivante avant de revenir à la précédente.

## Correction

Un unique mixin client `HauteCapitaleClockMixin` redirige uniquement les lectures de `getTimeOfDay()` effectuées par l'horloge RPG-HUD.

- horloge affichée : **0,5 unité de timeOfDay par gameTime tick** ;
- `gameTime` reste la base monotone, à vitesse vanilla ;
- petites corrections serveur vers l'arrière : ignorées visuellement ;
- `/time`, sommeil et gros sauts réels : resynchronisation immédiate ;
- cycle figé : détecté et respecté ;
- reprise du cycle : réancrage immédiat ;
- couleur de l'horloge : utilise exactement le même temps lissé.

Aucune autre partie de RPG-HUD ni aucun état du monde n'est modifié.

## Validation

Simulation déterministe effectuée avec :
- prédiction client à 1,0x ;
- correction serveur toutes les 20 ticks vers un temps autoritaire à 0,5x ;
- vérification que l'affichage ne diminue jamais ;
- vérification de la cadence 0,5x ;
- test d'un saut `/time` ;
- test gel/reprise du cycle.

Le JAR patché conserve tous les fichiers d'origine byte-for-byte sauf :
- `fabric.mod.json` ;
- `rpg-hud.mixins.json` ;
- ajout de `HauteCapitaleClockMixin.class` ;
- ajout de la note `__haute_capitale_meta/HC40M_CLOCK_PATCH_1.0.txt`.

Base fournie/officielle attendue :
`ab2dec0daf697af037c294c50ba7a8dc5251048294d5f88229bc14d832a02b00`

JAR final :
`RPG-HUD-3.13-HauteCapitale-HC40M-1.0.jar`

SHA-256 :
`a255bfe59ddc0bff9081dfc9219da46c5917a3052e8b6279571872fd09552c02`

Source : `tools/patches/rpghud-hc40m/`.
