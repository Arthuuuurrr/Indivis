# Audit avant développement — issue #167, 19 octobre 2026

Base Git : `9799badc75e8921e543caa18027e5acac0a91486` (`main`). Travail sur
`feat/167-indivis-voices-prologue`. Ni fusion ni déploiement de production.

## Versions vérifiées

| Composant | Preuve actuelle | SHA-256 de l'artefact inspecté |
| --- | --- | --- |
| Minecraft / Java / Loader | 1.21.11 / 21 / 0.19.5 ; métadonnées JAR et log du 19/10 | — |
| Fabric API | 0.141.6+1.21.11 ; log | — |
| EasyNPC Core + Config UI | 7.16.0 ; log + security.cfg généré | Core officiel téléchargé pour audit : be3531fba0d3b0553e64ac8acae1906a882f41b32ac5e64386c3d6c4cbef6f8b ; identité du JAR serveur non vérifiée par hash |
| Dialogue | 0.1.0+1.21.11.b10 ; fabric.mod.json et bytecode du JAR fourni | a2c21f24889f96880c472d168ea55cf65e84ff154ee1bbcc22dbeaa9df1d4057 |
| Core prologue | fichier capitale_core_1.5.7.zip | 9b9c20bbc0a28d85384d7bb5e7d00199fee87af795fc081a24e3f0971c61e9df |
| Journal | 0.1.0+1.21.11.b6 | d7585297d672f22019af68efff67762997fe086cb1da59e0c60b0eb838de2ff5 |
| Quêtes Clément | hc_quetes b21, livres v16.1, sync citoyenneté impériale | d67f3a86b2130f476a5b957261b6e852d14be8227868ed6ea3a887f60f03f5f6 |
| Nexus | 1.0.0-beta.34+indivis.1.21.11 | 24e68bc46405c8c40de030b192372da2a14bae7df1b28cee3f9db04cfe528e90 |
| RPG | 0.3.0+1.21.11.b2.HC.TEST3.RC3-FIREARMS-SPELLBAR ; log et artefact retrouvé | 08f03d4db0c41286851cbd35318b91afcb0e59483a5ce2d7164aa59ebf047e40 |
| Spell Engine HC | 1.10.5.001+1.21.11-HC-TEST3-RC17-RPG-ARMOR-X2 ; version affichée normalisée 1.10.5.1 | 64e31c2239b2e341c5ae514ca522814f36549c819fa88a7e7bf7a5186bc75fbd |
| Spell Power HC | 1.6.1.002+1.21.11.hc.test3.rc9 ; normalisée 1.6.1.2 | d208fd4b8de6711d53108d490ad3991b310acc9aed8af0626ac2c9034959be6b |
| Cloth Config / Player Animation Library | 21.11.153 / 1.1.10+mc.1.21.11 ; log et artefacts officiels | Dépendances du banc de test, pas de Voices |

Le libellé interne de `pack.mcmeta` du core joint indique encore 1.5.6-RC9BC.
Le nom 1.5.7 et le hash ci-dessus identifient la base fournie. Les anciens
manifestes et le datapack sur main ne sont pas une copie de cet environnement.
Ne pas les remplacer implicitement. Le core reste nommé 1.5.7.

Les manifestes des vrais JAR b10, journal b6 et RPG RC3 indiquent **Loom
1.17.20** ; EasyNPC officiel 7.16 indique Loom 1.16.3. Le précédent exemple
daycycle de main utilise 1.13.6, trop ancien pour remapper b10 en développement.
Le build Voices est donc aligné sur **Loom 1.17.20 / Gradle 9.5.1**, comme la
source historique de dialogue PR #104, et **Yarn 1.21.11+build.6 / Java 21**.
Les dépendances de jeu sont déclarées indépendamment, sans réassembler les
anciens JAR. `version.json` du client Mojang 1.21.11 confirme resource 75.0 /
data 94.1 ; le pack vocal utilise le format ressource, pas le format datapack.

## Interfaces constatées, pas supposées

Les sources b7 sont dans PR #104, pas sur main. Les ajouts b9/b10 n'y figurent
pas intégralement. Inspection du bytecode b10 et décompilation locale pour audit
uniquement ; aucun remplacement de b10 ni redistribution de ses classes.

* Serveur : `DialogueManager.session(PlayerEntity)` → Optional<DialogueSession> ;
  champs publics `id`, `pnj`, `pnjEntityId`, `monde` ; événements `onFermeture`.
* Client : `DialogueClientState.sessionId()`, `pnjEntityId()`, `pnjUuid()`,
  `captureActive()`, `onMessageCapture(Classification)` ; l'écran de capture
  contient un `DialoguePanel panneau` avec `avancer`, `choixSous`, `typewriter`.
  Les champs `REPLIQUES`, `pageRemise`, `captureVersion`, `fenetreJusquaMs`
  et les méthodes `page`, `pages`, `historiqueVisible` ont été contrôlés dans
  ce même JAR. L'adaptateur ne copie aucune classe de b10.
* Nexus PRE34 : `NexusCharacters.getSelectedCharacter(ServerPlayerEntity)` et
  `CharacterDto.id()`. Le changement d'identité doit annuler, jamais transférer
  une attente de voix à un autre personnage.
* EasyNPC déclenche les fonctions et b10 ouvre la session ; pas d'appel nouveau
  à une API de mouvement non vérifiée. Les fonctions `near_distance`, escortes,
  propriétaires, choix et caméras restent propriétaires de leur logique.

Chaîne réelle : EasyNPC → fonctions `capitale:quest/...` → tellraw privé `@s` +
CAP_QSEQ / CAP_QSEQ_TIMER → `core/tick` →
`quest/dialogue/sequence/dispatch_self`. Ce dispatch a un verrou anti-cascade.
`QuestChoix`, CAP_QDIALOG_OWNER et CAP_QDIALOG_KEEP ne doivent pas être réécrits.
Les fichiers `hc_quetes` de Clément ne participent pas à ce pont.

## Intégration minimale retenue

Le serveur reconnaît uniquement les textes exacts du catalogue, dans une session
b10 réelle et avec le CAP_QSEQ attendu après la fonction. Un client volontaire
ayant vérifié l'asset reçoit une voix privée. Pendant cette seule lecture,
START_SERVER_TICK remet **CAP_QSEQ_TIMER à 2** : le tick natif retire 1, sans
atteindre le dispatch. C'est un bail d'un tick, pas un timer arbitraire de durée.
Fin validée / clic : timer à 0, puis dispatch **natif** au tick suivant. Échec :
restauration du délai texte capturé. Changement de séquence ou personnage :
aucune écriture dans la nouvelle progression. Sans mod le dernier bail expire
en deux ticks. Aucun fichier du core n'est remplacé, aucune commande de quête
n'est exécutée par le protocole vocal.

Pas de nouveau privilège joueur. `security.cfg` n'est pas modifié. Les permissions
élevées observées dans la configuration jointe sont préexistantes, pas des
prérequis ajoutés par Voices.

## Périmètre prouvable et limites

La compilation et les tests isolés ne prouvent pas un parcours en jeu. Les JAR
EasyNPC 7.16 et RPG RC3 ont pu être obtenus pour les essais complémentaires.
Il manque toujours les sources b10 complètes et un monde de test avec les PNJ,
routes, positions et configurations. Le prototype de rencontres ne doit
pas inventer ces positions ni les signatures de navigation EasyNPC.
Les voix de démonstration synthétiques éventuelles sont du matériel technique,
pas un casting approuvé. Les prises humaines restent à fournir par Arthur.
Les validations en jeu #62, #59, #115, #159 et deux joueurs doivent être tracées
séparément ; ne jamais les annoncer acquises après un simple build.

Références techniques : [sources b7 de PR #104](https://github.com/Arthuuuurrr/Indivis/pull/104),
[versions EasyNPC officielles](https://modrinth.com/mod/easy-npc-core/versions),
[Yarn 1.21.11 build.6](https://maven.fabricmc.net/docs/yarn-1.21.11%2Bbuild.6/),
[Fabric docs 1.21.11](https://github.com/FabricMC/fabric-docs/tree/main/versions/1.21.11).
