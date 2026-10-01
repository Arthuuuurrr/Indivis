# Haute Capitale Dialogue b10 — DATAPACK COMMAND HOTFIX

Date : 2026-10-01

## Constat

Le serveur charge `haute_capitale_dialogue 0.1.0+1.21.11.b9`, mais Minecraft refuse les cinq fonctions du core RC9BA qui commencent par `hcd_interpellation nearest_named ...`.

Fonctions touchées :
- `capitale:quest/spawn_dirigeable_couronne/geraud_large_start_self`
- `capitale:quest/spawn_dirigeable_couronne/capitaine_large/near_intro_self`
- `capitale:quest/le_registre_du_port/leovic/offer_self`
- `capitale:quest/sous_le_regard_du_coeur/aurele/near_intro_self`
- `capitale:quest/au_seuil_des_profondeurs/roch/near_intro_self`

## Cause

Le patch serveur b9 enregistre la racine Brigadier `hcd_interpellation` avec une condition équivalente à :

```java
.requires(source -> source.getEntity() instanceof ServerPlayerEntity)
```

Minecraft parse les fichiers `.mcfunction` avec une source de commande non-joueur. La racine est donc invisible pendant le parsing et chaque fonction qui l'utilise est rejetée à la position 0.

## Correctif b10

Delta binaire minimal sur `B9ServerPatch.class` :
- la condition de visibilité de la racine devient toujours vraie au parsing ;
- les handlers d'exécution conservent leur récupération de joueur et l'ouverture de session existante ;
- aucune logique caméra, capture, EasyNPC, réseau, HUD, session ou cadrage n'est modifiée.

JAR produit :
- `haute-capitale-dialogue-0.1.0+1.21.11.b10-DATAPACK-COMMAND-HOTFIX.jar`
- SHA-256 : `a2c21f24889f96880c472d168ea55cf65e84ff154ee1bbcc22dbeaa9df1d4057`
- taille : 200473 octets
- base : b9 fourni le 01/10/2026

Datapack associé :
- `capitale_core_1.5.6-RC9BB_HCD_B10_DATAPACK_COMMAND_HOTFIX_FULL.zip`
- SHA-256 : `6e593493cfc39ed2a3dee457be03b7e45c2660e4eff42387e14e3265bc9c4457`
- RC9BB ne change aucun fichier sous `data/` par rapport à RC9BA ; seuls `pack.mcmeta` et la validation de traçabilité diffèrent.

## Limite de traçabilité

Les sources historiques b7 sont dans la PR #104. Le code source complet des ajouts b9 (B9ServerPatch/B9ClientPatch/MessageCaptureMixin) n'est pas encore présent dans le dépôt. Ce document trace donc le delta b10 exact sans prétendre reconstituer une source b9 complète.

## Validation à faire en jeu

Après remplacement b9 -> b10 sur client et serveur et remplacement RC9BA -> RC9BB :
1. redémarrage complet du serveur ;
2. vérifier qu'aucun `Failed to load function capitale:quest/...` ne mentionne les cinq fonctions ci-dessus ;
3. personnage au début du prologue : s'approcher de Géraud à moins de 12 blocs ;
4. la caméra d'interpellation doit s'ouvrir et la quête doit passer à `QUEST_SPAWN=10` / `QUEST_PROLOGUE=10` ;
5. vérifier ensuite Althéon, puis au minimum Léovic ou Aurèle pour confirmer que les autres appels `hcd_interpellation` sont reparsés correctement.
