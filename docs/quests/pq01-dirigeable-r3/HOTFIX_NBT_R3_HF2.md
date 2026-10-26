# PQ01 R3-HF2 — copies NBT et liaisons EasyNPC

Correctif du 23 octobre 2026 pour Fabric **1.21.11**, EasyNPC **7.16.0** et Dialogue **b10**.

Le log fourni confirme le démarrage de R3-HF1 et d’Indivis Admin alpha.3. À la liaison de Géraud, `GuardNbt.copy` appelle `method_10553`, méthode spécifique à NbtCompound, sur une NbtList. Les sauvegardes de listes d’actions et de noms réutilisaient incorrectement cet utilitaire. Le log montre aussi un refus de liaison multiple d’Althéon et le rejet par EasyNPC d’un ancien chemin de fonction nu.

## Installation

1. Arrêter le serveur et remplacer uniquement le journal par `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF2.jar` dans les mods **serveur et clients**. Retirer le JAR R3-HF1 de ce même mod.
2. Garder les datapacks core/quêtes **R3** et le mod admin **alpha.3** déjà installés. Redémarrer puis se reconnecter.
3. Fermer tout dialogue actif et reprendre les commandes de liaison qui ont échoué, à moins de trois blocs du PNJ voulu :

```mcfunction
/function capitale:pq01/liaison/geraud_large
/function capitale:pq01/liaison/altheon_large
```

Exécuter chaque commande près du PNJ correspondant. Le log situe l’échec de Géraud pendant la préparation de la sauvegarde, avant la première mutation du PNJ ; la nouvelle liaison réutilise le PNJ existant. Les commandes de pose et de déplacements sont inchangées. [Guide complet](docs/INSTALLATION_ET_POSE_PNJ.md).

## Changement de code

- Copies PQ01 par le contrat public `NbtElement.copy` (`method_10707`), valable pour composés, listes et valeurs. Les helpers historiques des gardes et de la séquence I restent identiques octet pour octet.
- Migration d’un unique déclencheur PQ01 reconnu dans une liste d’actions, y compris un chemin `capitale:…` sans le mot `function`. Les autres actions, leur ordre et leurs conditions restent conservés. Les interactions étrangères sans hook reconnu et les déclencheurs multiples restent refusés avec diagnostic.
- Déclencheurs PQ01 sous forme d’action native EasyNPC `haute_capitale_quetes:pq01`. Le callback n’accepte que les rôles/événements prédéfinis et contrôle le joueur chargé, la liaison, la dimension, la proximité et le propriétaire de l’acteur personnel. Aucun texte de commande libre n’est accepté et aucune liste d’autorisations de commandes n’est modifiée.
- Mutation par `ActionEventDataCapable.setActionEventSet`, avec copie indépendante, sérialisation EasyNPC et relecture. Une modification externe après liaison empêche d’écraser l’ancienne sauvegarde lors d’une nouvelle liaison. Une écriture interrompue peut être reprise si les actions correspondent encore à l’original ou à la version liée.
- Annotations Mixin HF1 conservées à l’identique ; scripts, dialogues, progression et mouvements R3 conservés.

## Contrôles exécutés

- **80 assertions** avec les vraies classes NBT de Minecraft **1.21.11**, les vraies structures d’actions, le parseur, la sérialisation et le registre d’EasyNPC **7.16.0**. L’erreur précédente est reproduite sur une liste et un nom NBT ; le nouveau chemin les copie correctement. Listes multiples, commandes nues, ordre, liaison répétée, écritures interrompues, modifications externes et restauration sont exercés.
- **104 recherches de membres publics** dans les classes réelles Minecraft, EasyNPC et Dialogue : existence, visibilité et arité ; la liste des signatures trouvées est dans `audit/hf2_native_api.log`. Cela ne teste pas le comportement de toutes ces méthodes dans un monde.
- Contrôleur de marche compilé : **17 cas, 26 assertions** avec ports doublés.
- ASM BasicVerifier : **175 classes, 1 725 méthodes** ; CRC du JAR valide.
- Depuis HF1 : trois entrées modifiées (métadonnées, `Pq01Bridge` et son port de déplacement), deux classes ajoutées (`Pq01Nbt`, `Pq01Actions`), aucune entrée supprimée ; **208 entrées identiques**. Tous les mixins, `GuardNbt` et `SequenceBridge` sont identiques. Les deux ZIP R3 sont identiques à ceux déjà livrés.

**Limites : aucun serveur Fabric complet ni copie du monde n’a été démarré ici.** Le banc utilise un conteneur doublé pour le stockage d’entité ; les structures et sérialiseurs EasyNPC et les NBT sont réels. La navigation réelle, la caméra, les paquets et la coexistence avec tout le modpack restent à observer en jeu. Le log utilisateur prouve le démarrage de HF1 ; il ne constitue pas une recette de HF2.

## Artefact

`haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF2.jar` — **465422 octets** — SHA-256 :

```text
320ae69d6dca99bfc2ac4b9f0515a44d01b84e3274e9087115495c52ac235613
```

Sources : `tools/pq01_full/java/net/hautecapitale/quetes/sequence/`. Bancs : `tools/pq01_full/tests/integration/Pq01NbtIntegrationBench.java`, `Pq01NativeApiBench.java`, `Pq01MotionBench.java`. `verify_hf2.py` compile les bancs puis charge le JAR HF2 avec les dépendances de test réelles :

```bash
python3 tools/pq01_full/verify_hf2.py --java21 CHEMIN_JAVA21 --minecraft minecraft-intermediary-1.21.11.jar --libraries BIBLIOTHEQUES_DU_BUNDLE_SERVEUR --easynpc EASY_NPC_7_16_0.jar --dialogue DIALOGUE_B10.jar
```

Le serveur officiel de test provient de `https://piston-data.mojang.com/v1/objects/64bb6d763bed0a9f1d632ec347938594144943ed/server.jar` (SHA-1 `64bb6d763bed0a9f1d632ec347938594144943ed`). Son JAR interne est remappé de `official` vers `intermediary` avec Tiny Remapper 0.12.1 et `net.fabricmc:intermediary:1.21.11:v2`. Aucun fichier du jeu, bibliothèque de test ou classe de contrat n’est redistribué dans le mod.

Références primaires : [NbtElement 1.21.11](https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/nbt/NbtElement.html#copy()), [NbtList 1.21.11](https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/nbt/NbtList.html#copy()). Les API EasyNPC et Dialogue ont été inspectées dans les JAR fournis.
