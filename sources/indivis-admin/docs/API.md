# Contrats d'API consultés

Cible exclusive : Minecraft 1.21.11, noms intermédiaires de production Fabric. Cette vérification documentaire et l'examen des JAR fournis ne remplacent pas le lancement du jeu.

| Élément | Méthodes utilisées | Provenance |
|---|---|---|
| CommandFunctionManager (`class_2991`) | `method_29463` Iterable des fonctions ; `method_12905` fonction optionnelle ; `method_12904` exécution native | [Yarn 1.21.11 build 1](https://maven.fabricmc.net/docs/yarn-1.21.11%2Bbuild.1/net/minecraft/server/function/CommandFunctionManager.html) |
| MinecraftServer | `method_3740`, `method_3760`, `method_3845`, `method_3734`, `method_3739` | [Yarn](https://maven.fabricmc.net/docs/yarn-1.21.11%2Bbuild.1/net/minecraft/server/MinecraftServer.html), usages dans les JAR fournis |
| CommandManager | `field_31839`, `method_71774` ; `method_44252` | Journal b6 fourni : QuestCommands, autres adaptateurs fournis |
| ServerPlayerEntity | `method_64396` source de commande fraîche | Nexus fourni : ProfileStateBridge ; [Yarn](https://maven.fabricmc.net/docs/yarn-1.21.11%2Bbuild.1/net/minecraft/server/network/ServerPlayerEntity.html) |
| Joueurs et scores | `method_14571`, `method_14602`, `method_1170`, `method_55430`, `method_55397` | Journal b6 et Nexus PRE34 fournis |
| Entité et texte | `method_5667`, `method_5477` + `getString`, `method_51469`, `method_73189` | JAR fournis, notamment QuestCommands |
| Source de commande | `method_9207`, `method_9232`, `method_9227`, `method_9208` | JAR fournis |
| Nexus | `NexusCharacters.getSelectedCharacter(player)` ; `id()`, `name()` | Nexus PRE34 fourni |
| Fabric | CommandRegistrationCallback et ServerTickEvents/ServerLifecycleEvents | Même API constatée dans le journal fourni |

Les dialogues reposent sur les commandes et formats natifs publiés dans les [notes officielles Minecraft 1.21.6](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-6) : `dialog show`, `dialog clear`, `multi_action`, `confirmation`, corps `plain_message`, actions `run_command`. Ils sont disponibles dans la version cible 1.21.11. Le décodage réel de ces dialogues n'a pas été testé ici.

Le mod n'utilise pas les accès NBT de la Séquence I et n'édite pas les fichiers de profils Nexus. Il comporte un seul mixin de compatibilité Nexus : réécriture de l’adresse du hook avant sauvegarde, avec wrapper de préservation du verrou. Les fichiers des autres mods restent inchangés.

## Correctif alpha.2

La cible `ProfileStateBridge.runHookIfCorePresent(ServerPlayerEntity, Scoreboard, String)` et son unique argument String sont vérifiés dans le JAR `PRE34-SI1` fourni (SHA-256 454bd4206b50737bf386d65eb09f4f0e3ad1294a1fd4e6df426f9fe205cb31d0). Les accesseurs `getSelectedCharacter`, `CharacterDto.id()` et `name()` sont également présents dans ce JAR ; aucune API Nexus de remplacement n’a été supposée.

`@ModifyVariable(at=HEAD,argsOnly=true,ordinal=0,remap=false)` ne change que `function capitale:profiles/before_save_self`. `@Pseudo` rend la cible optionnelle quand Nexus n’est pas installé. Les annotations sont compilées contre des contrats vérifiés dans les sources officielles et ces contrats sont exclus du JAR :

- https://github.com/SpongePowered/Mixin/blob/master/src/main/java/org/spongepowered/asm/mixin/injection/ModifyVariable.java
- https://github.com/SpongePowered/Mixin/blob/master/src/main/java/org/spongepowered/asm/mixin/Pseudo.java

Les commandes d’affichage utilisent `dialog show @s`, `dialog clear @s` et `tellraw @s`, avec `ServerCommandSource.withEntity(administrator)` (`method_9232`) et les permissions de la source serveur. Références :

- https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/server/command/ServerCommandSource.html
- https://maven.fabricmc.net/docs/yarn-1.21.11+build.4/net/minecraft/command/argument/EntityArgumentType.html

La validation du point d’injection et du bytecode n’est pas un test d’application du mixin dans Fabric. Le test de sélection de joueur du banc modélise le refus observé dans le log ; il n’emploie pas le parseur Brigadier réel.
