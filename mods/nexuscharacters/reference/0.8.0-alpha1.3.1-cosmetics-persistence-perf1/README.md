# NexusCharacters HC 0.8.0-alpha1.3.1 — Cosmetics Pack 1 + Persistence PERF1

Issue : #125

## Base exacte

`NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.3-COSMETICS-PACK1.jar`

SHA-256 :
`512b92cc9796f22487aedd59a7896716f06a4816adcca312cf31ab7453a80da2`

Version Fabric :
`1.5.1-capitale-port-v0.8.0-alpha1.3-cosmetics-pack1+1.21.11`

## Vérification alpha1.3 avant patch

Les classes de persistance suivantes sont bit-à-bit identiques entre alpha1.2 et alpha1.3 :
- `VaultManager.class`
- `PlayerNbtSanitizerV0622.class`
- `PersistenceQueueV0710.class`
- `PersistenceEngineV0710.class`
- `NexusCharacters.class`

La mise à jour alpha1.3 est donc bien séparée du système de sauvegarde.

## Correctif

`VaultManager.serializePlayerNbtTokenized` ne fait plus :

`player -> NBT -> GZIP -> NBT -> filtrage -> GZIP`

mais :

`player -> NBT -> filtrage -> GZIP`

Le filtrage continue d'appeler `PlayerNbtSanitizerV0622.shouldStripTag`.
Les préfixes filtrés restent :
- `CAP_RUNTIME_`
- `CAP_TMP_`
- `NPC_`
- `ACCESS_`

## Garanties de sauvegarde volontairement préservées

Aucune modification de :
- checkpoint périodique : toujours **100 ticks / 5 secondes** ;
- `PuffishSkillsBridge.save` et `ProfileStateBridge.save` ;
- sauvegarde PlayerManager ;
- position/rotation/dimension du joueur ;
- `PersistenceQueueV0710` ;
- sauvegarde finale de déconnexion ;
- barrière/attente à l'arrêt du serveur ;
- format des vaults ;
- autorité serveur ;
- copie/synchronisation des fichiers de progression.

Le correctif ne réduit donc pas la fréquence de sauvegarde et ne remplace aucun chemin de persistance par un mécanisme moins sûr.

## Cosmétique alpha1.3

Toutes les ressources et classes du Cosmetics Pack 1 sont conservées bit-à-bit :
- 15 tenues ;
- 2 coiffures ;
- 2 styles de pilosité ;
- anciens IDs d'apparence ;
- ressources et compatibilité legacy.

Contrôle effectué : **121 entrées cosmétiques comparées, 0 différence** entre alpha1.3 et alpha1.3.1 PERF1.

## Build candidat

`NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.3.1-COSMETICS-PACK1-PERSISTENCE-PERF1.jar`

SHA-256 :
`2278108e8be245ab8c574e9c41d7c38537069b53d29bca129ac352d18535f53a`

Version Fabric :
`1.5.1-capitale-port-v0.8.0-alpha1.3.1-cosmetics-pack1-persistence-perf1+1.21.11`

Diff par rapport à alpha1.3 :
- modifié : `net/tompsen/nexuscharacters/VaultManager.class`
- modifié : `fabric.mod.json`
- ajouté : `PATCH_NOTES_0_8_0_ALPHA1_3_1_PERSISTENCE_PERF1.md`
- supprimé : aucune entrée.

## Validation statique

- archive alpha1.3 : intègre ;
- archive alpha1.3.1 : intègre ;
- aucune signature JAR ;
- `NexusCharacters.class`, `PersistenceQueueV0710.class`, `PersistenceEngineV0710.class` et `PlayerNbtSanitizerV0622.class` inchangés ;
- checkpoint 100 ticks vérifié dans `NexusCharacters.lambda$onInitialize$8` ;
- la méthode patchée compresse le NBT une seule fois ;
- elle appelle toujours `PlayerNbtSanitizerV0622.shouldStripTag`.

## Validation runtime obligatoire

Avant validation définitive :
1. connexion + sélection d'un personnage existant ;
2. modifier inventaire, XP, santé, position ; attendre >5 s ; déco/reco ;
3. modifier CapSkills ; attendre >5 s ; déco/reco ;
4. changer de dimension ; attendre >5 s ; déco/reco ;
5. arrêt serveur normal puis redémarrage ;
6. déconnexion brutale/crash client puis reconnexion ;
7. tester un personnage avec les nouveaux cosmétiques alpha1.3 ;
8. refaire un Spark comparable.

Une sauvegarde de test/backup du monde et du dossier Nexus avant le premier essai reste recommandée.
