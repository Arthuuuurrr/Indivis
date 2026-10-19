# Enchaînement des répliques — 0.2.1

Diagnostic du 19 octobre 2026, à partir des journaux serveur
`latest - 2026-10-19T194248.282.log` et client `latest(20261019-174317).log`.

## Faits et reproduction

Le client 0.2.0 vérifie 20/20 prises à 19:39:36, démarre uniquement
`prologue.geraud.reveil.01` à 19:41:46 puis reçoit l'objectif suivant à
19:41:53. Le serveur annonce lui aussi les 20 prises et une session b10 réelle.
Les journaux ne donnent pas l'empreinte du datapack installé sur le serveur.

Le ZIP core 1.5.7 fourni et déjà audité a été réinspecté :
SHA-256 `9b9c20bbc0a28d85384d7bb5e7d00199fee87af795fc081a24e3f0971c61e9df`.
Son dispatcher est reproduit sans modification dans la fixture de test
`src/test/resources/core-1.5.7-dispatch.mcfunction`.

Son verrou met CAP_FLAG à 1 pour la branche 101. La fonction de deuxième
réplique change CAP_QSEQ en 102 ; les commandes suivantes acceptent toujours
CAP_FLAG=1, donc la troisième réplique puis l'objectif partent dans le même
appel. Le timer 20 posé par chaque réplique n'est pas consulté à l'intérieur
du dispatcher. À la fin du tick, la séquence est déjà terminée : Voices ne peut
plus autoriser les prises 02/03. Le même défaut concerne les autres branches
qui avancent vers une branche située plus bas dans ce fichier.

Le contrôle initial vérifiait la présence de CAP_FLAG, sans exécuter ce cas de
cascade. Cette preuve était insuffisante ; la régression couvre maintenant
le comportement avant et après correction.

## Correction dans le JAR

`CommandFunctionMixin` intervient lors de la compilation des fonctions, pas
pendant chaque tick. Il cible uniquement
`capitale:quest/dialogue/sequence/dispatch_self` avec l'empreinte normalisée
`7c2fa10c295379b9bab0ac4e5157ebe916a42a30d070408d7f57a3859acc6b4a`.
Les 38 appels de branche deviennent `run return run function` : chaque appel
du dispatcher termine après sa première branche. Les fonctions natives restent
appelées normalement et conservent leurs actions et leurs timers.

Empreinte normalisée après garde :
`721693a18c5552af0a372d857b3dcb2ca4389abe0fdd2e4156c8c80171721073`.
Les commentaires, conditions, 38 cibles et autres lignes restent identiques.
Une entrée déjà corrigée ne repasse pas dans la transformation. Une autre
fonction ou révision reste intacte ; une empreinte inconnue émet un diagnostic.
Le contrôle repose sur les lignes brutes, commentaires compris, transmises par
[`CommandFunction.create` de Minecraft 1.21.11](https://maven.fabricmc.net/docs/yarn-1.21.11%2Bbuild.6/net/minecraft/server/function/CommandFunction.html).

Il s'agit d'une correction ciblée du contrôle de flux compilé du core. Aucun
fichier du datapack sur disque ni fonction de quête supplémentaire n'est livré.
Les ID, textes, conditions, choix, récompenses, caméras, interfaces b10 et
banque des 20 prises restent conservés. Retirer Voices retire aussi cette garde ;
le core d'origine retrouverait alors sa cascade jusqu'à sa correction native.

## Vérifications

Sept tests de régression complètent les 22 tests existants : empreintes avant
et après, seules 38 lignes modifiées, refus d'une autre révision, idempotence,
reproduction du défaut Géraud, trois appels distincts jusqu'à l'objectif,
et isolation de plusieurs contextes avec les 38 branches.

`tools/smoke_sequence_guard.py` lance aussi un serveur Fabric de développement
isolé dans `build/smoke-server/`, sans compte ni mod privé. Il charge le vrai
dispatcher audité et des fonctions de test qui comptent leurs invocations.
Il vérifie les 38 branches dans Minecraft, trois appels Géraud distincts et
deux sources d'exécution indépendantes. Ces fonctions factices servent
uniquement au banc de test et ne sont pas dans le JAR livré. Le serveur de test
se ferme automatiquement après ses contrôles.

Les résultats effectifs sont dans les tests et journaux de l'artefact
`indivis-voices-prototype` de la [CI de la PR #171](https://github.com/Arthuuuurrr/Indivis/pull/171/checks).
Ce banc vérifie la compilation et le contrôle de flux Minecraft ; il ne
remplace pas l'écoute client, le monde réel de quêtes ni une recette à deux joueurs.

## Essai sur le serveur de test

1. Remplacer uniquement Voices par `indivis-voices-0.2.1+1.21.11.jar` côté
   serveur et client, puis redémarrer les deux. Garder le core fourni.
2. Dans le log serveur, vérifier `garde anti-cascade du core 1.5.7 active`.
   Si le log indique une empreinte non auditée, conserver ce log et fournir le
   core réellement installé ; ne pas contourner le contrôle d'empreinte.
3. `/ivvoices` doit toujours annoncer 20/20. Les tests locaux
   `/ivvoices test P01-GER-010` et `P01-GER-011` permettent d'écouter les deux prises.
4. Rejouer le premier bloc par le parcours normal : trois démarrages audio
   01 → 02 → 03, puis seulement l'objectif. Vérifier aussi l'introduction et
   les questions d'Althéon, le passage volontaire et ESC.

Aucun déploiement en production ni migration de progression n'est effectué.
