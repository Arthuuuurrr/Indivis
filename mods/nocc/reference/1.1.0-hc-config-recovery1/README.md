# NoCC 1.1.0 — Haute Capitale CONFIG RECOVERY1

Issue : #127

## Base

`NoCC-1.1.0.jar`

SHA-256 :
`53e95c452759d52c3dfe9e73238aaa26a3c36699a6dfc9921c7c9ca810264cc2`

## Diagnostic

NoCC n'utilise pas un fichier nommé `nocc.json`.

Le chemin codé dans `NoccConfig` est exactement :

`config/no-command-confirm.json`

Le crash observé vient de `NoccConfig.load()` :
Gson attend un objet JSON mais le fichier local contient une chaîne à la racine.

NoCC 1.1.0 ne capture que les `IOException`, donc une `JsonSyntaxException` tue l'entrypoint client.

## Correctif

Le patch ne change qu'une chose :
les deux handlers externes de `NoccConfig.load()` capturent `Exception` au lieu de seulement `IOException`.

Conséquence :
- config valide : comportement identique ;
- fichier absent : comportement identique ;
- config malformée / ancien schéma : retour aux valeurs par défaut au lieu d'un crash.

## Valeurs par défaut conservées

- mode : `OFF` ;
- confirmation popup désactivée ;
- patterns confirm/bypass inchangés.

Aucun mixin, aucune règle de commande, aucune synchronisation serveur et aucune UI fonctionnelle ne sont modifiés.

## Build

`NoCC-1.1.0-HC-CONFIG-RECOVERY1.jar`

SHA-256 :
`2d4d9717620d64cee16a92d2d555a0807b08d512130304509a2c8e6949645b8a`

## Validation

- archive JAR : intègre ;
- aucune signature JAR ;
- bytecode vérifié : exactement 2 handlers modifiés ;
- `fabric.mod.json` versionné `1.1.0+hc.config-recovery1`.

Runtime Minecraft encore à valider.
