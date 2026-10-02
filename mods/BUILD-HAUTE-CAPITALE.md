# Compilation des modules Haute Capitale

Instructions de reconstruction des modules dont les sources sont versionnées dans
`mods/<module>/`. Lot 1 importé le 2026-09-24 (voir #72).

Ces sources sont une **archive de référence**. Elles ne remplacent aucun JAR déployé :
`mods/*.jar` reste la bibliothèque des versions réellement retenues en production.

## Environnement commun

| | |
|---|---|
| Minecraft | 1.21.11 |
| Yarn | 1.21.11+build.6 |
| Fabric Loader | 0.19.5 |
| Fabric API | 0.141.6+1.21.11 |
| JDK | 21 |
| Gradle | 9.5.1 (wrapper) |

## Modules importés

| Module | Build | Groupe | Suivi |
|---|---|---|---|
| `haute-capitale-dialogue` | `0.1.0+1.21.11.b7` | `net.hautecapitale` | #59, #20, #21, #72 |
| `haute-capitale-spawns` | `0.1.0+1.21.11.b6` | `net.hautecapitale` | #38, #72 |
| `haute-capitale-party` | `0.1.0+1.21.11.b9` | `net.hautecapitale` | #32, #72 |
| `haute-capitale-metiers` | `0.1.0.b12` | `net.hautecapitale` | #72 |
| `haute-capitale-fusils` | `0.1.0+1.21.11.b3` | `net.hautecapitale` | #3, #4, #72 |
| `haute-capitale-rpg` | `0.3.0+1.21.11.b4` | `net.hautecapitale` | #53, #72 |

Le numéro de build exact de chaque module est porté par son `gradle.properties`
(`mod_version`, ou `version` pour `haute-capitale-fusils`).

## Dépendances locales (`libs/`) — non versionnées

Plusieurs modules compilent contre des JAR tiers placés dans un dossier `libs/` à la
racine du module. Ces JAR ne sont **pas** versionnés ici : les `gradle.properties`
d'origine les marquent explicitement « dépendances locales, jamais redistribuées », et
aucun n'est modifié par le projet.

Pour compiler, recréer `mods/<module>/libs/` et y placer les fichiers attendus :

| Module | JAR attendu dans `libs/` | Portée | SHA-256 |
|---|---|---|---|
| dialogue | `easy_npc-fabric-1.21.11-7.8.0-HOTFIX-index-iteration.jar` | `modImplementation` | `a28a2af9d9f164ca80835249fabcbd5a9105587655676f77c886684fc1d10559` |
| dialogue | `haute-capitale-rpg-0.3.0+1.21.11.b1.jar` | `modImplementation` | `65ac9df7ac58a6b1ca7abee3388ddaf6b4e11614ae6f0a3e1ce480a7ce33838d` |
| dialogue | `bettercombat-fabric-3.0.2+1.21.11.jar` | `modCompileOnly` | `fa5591648e954c423f2a856d6ba14c6976fd5efd5e2ccb3a987ca2c0338837d3` |
| spawns | `easy_npc-fabric-1.21.11-7.8.0-HOTFIX-index-iteration.jar` | `modCompileOnly` | *(idem ci-dessus)* |
| spawns | `haute-capitale-party-0.1.0+1.21.11.b6.jar` | `modCompileOnly` | `15a9fd41cf5355093322158197878e405ff5d5ec2bc0745c8e553b63035f571b` |
| party | `haute-capitale-rpg-0.1.0.jar` | `modCompileOnly` | `86d4af3cef6a86d0755b750c5a076792bc9d9a0c7587f4e00bb23bc6fb4a7a34` |
| party | `puffish_skills-0.17.3-1.21.11-fabric.jar` | `modCompileOnly` | `c32ee10b9883e7fdd2ae3a70cb00530a7c230c54a5c07a87a448353390c9066d` |
| metiers | `easy_npc-fabric-1.21.11-7.8.0-HOTFIX-index-iteration.jar` | `modImplementation` | *(idem ci-dessus)* |
| rpg | `puffish_skills-0.17.3-1.21.11-fabric.jar` | `modCompileOnly` | *(idem ci-dessus)* |
| fusils | *(aucune)* | — | — |

`haute-capitale-fusils` tire GeckoLib 5.4.5 depuis un dépôt Maven, sans JAR local.

### Dépendances internes à noter

`dialogue`, `spawns` et `party` compilent contre des builds **antérieurs** d'autres
modules Haute Capitale (`rpg b1`, `rpg 0.1.0`, `party b6`). Une fois tous les modules
versionnés, ces dépendances pourront devenir des dépendances de projet Gradle plutôt que
des JAR figés — à traiter séparément, cela change le graphe de build.

## Wrapper Gradle

Le `gradle-wrapper.jar` n'est **pas** versionné : la CI `check-jar-library` impose que
tout `.jar` sous `mods/` soit directement dans `mods/`, et un wrapper en sous-dossier la
ferait échouer.

`gradle/wrapper/gradle-wrapper.properties`, `gradlew` et `gradlew.bat` sont conservés, ce
qui fige la version de Gradle. Régénérer le binaire manquant avec un Gradle 9.5.1 local :

```
cd mods/<module>
gradle wrapper --gradle-version 9.5.1
```

`haute-capitale-metiers` n'avait pas de wrapper à l'origine : le compiler avec un Gradle
9.5.1 installé.

## Compiler

```
cd mods/<module>
./gradlew build
```

Le JAR est produit dans `build/libs/` (ignoré par Git).

## Bancs de test

Les modules qui en avaient conservent leur dossier `tools/` : scripts PowerShell de
pilotage d'un client de test, capture d'écran, envoi de touches, et un client RCON.
Les binaires compilés (`*.class`, `out/`) ont été exclus.

## Exclusions de l'import

`build/`, `.gradle/`, `out/`, `run/`, `run-client*/`, `logs/`, `*.log`, `testserver*/`,
`packclient/`, `shots/`, `DELIVERY*/`, `solotest/`, `audit/`, `libs/`, `*.class`.

Le dossier `datapack/` de `haute-capitale-rpg` n'a pas été importé : il contient une copie
de `capitale_abilities`, déjà présent sous `datapacks/capitale-abilities`. Les deux
versions seraient à comparer avant de décider laquelle fait référence.
