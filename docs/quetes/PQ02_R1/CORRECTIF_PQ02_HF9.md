# Indivis — PQ02 R1 HF9 : pauses, marche et caméras

Date : 1er novembre 2026. Minecraft/Fabric **1.21.11**, Java **21**.

## Installation depuis HF8 + Fusion C1

**Seul le JAR de quêtes change, sur le serveur et sur chaque client. Aucune commande, liaison, pose de PNJ ou réinitialisation à refaire.** Remplacer l’ancien JAR par `haute-capitale-quetes-HF12-PQ02-R1-HF9-1.21.11.jar`, garder les deux datapacks actuels et redémarrer complètement serveur et clients.

`PQ02_HF9.zip` contient `mods/` et la documentation. L’archive complète ajoute les mêmes datapacks HF8/Fusion C1, ainsi que les dossiers `serveur/` et `client/`. Les noms HF8 des datapacks sont intentionnels : les fichiers sont strictement conservés. Un seul mod `haute_capitale_quetes` est livré ; aucun compagnon ni dépendance n’est ajouté.

## Constats et modifications

| Retour | Cause constatée | HF9 |
| --- | --- | --- |
| Absence de pauses de lore | D/H étaient des points de transit sans arrêt narratif ; les questions exigeaient un clic pendant la marche. | Arrêt réel à D puis H, orientation vers le joueur et ouverture des répliques existantes à proximité. |
| Marche lente et arrêts entre repères | Consigne 0,8, arrêt/annulation de vitesse à chaque arrivée, prochain trajet au tick suivant ; relance inactive toutes les 20 ticks. | Consigne 1,2, soit +50 % ; passage au trajet suivant dans le même tick sans annuler l’élan ; relance inactive après 4 ticks au maximum. |
| Cadrage de l’entretien | Ancre commune basse et profil générique avec décalage de visée ; un log signalait une caméra hors de portée. | Ancre centrée entre les deux PNJ à 1,55 bloc au-dessus de leur niveau moyen ; profils b10 propres à l’entretien, visée centrée et décalage latéral réduit ; repli sur un interlocuteur accessible. |

### Pauses de lore

- **D** : réplique T01 sur l’activité du Port, avec les rubriques de questions existantes.
- **H** : réplique T04 sur la Garde du Port et les responsabilités des corps, avec ses questions.
- Les deux entrées proposent directement **« Reprenons notre route. »**. Échap permet aussi de poursuivre. Les sujets facultatifs et leurs conditions ne deviennent pas obligatoires.
- La pause attend le joueur : jusqu’à quatre blocs, dans la limite de la portée configurée, sans remplacer une autre conversation ouverte. Un texte bref au-dessus de la barre d’objets signale l’arrêt. Le joueur en retrait ne provoque pas une ouverture de caméra lointaine.
- Aucun délai ne retire le texte. La pause est privée et ne déplace pas le Léovic public.
- L’attente d’une pause non encore présentée est enregistrée dans le stockage de scène du personnage. Une sauvegarde/reprise la conserve ; après présentation ou départ volontaire, elle n’est pas rejouée.
- Une ancienne escorte reprend à sa position et à son repère enregistrés. Les pauses déjà dépassées ne sont pas imposées rétroactivement.

### Navigation

Le pathfinding reste celui d’EasyNPC/Minecraft, avec ses obstacles réels, ses objectifs A–P et sa suspension pendant les dialogues. Aucun déplacement de l’acteur par téléportation n’est ajouté. Les points ordinaires s’enchaînent ; D, H, M et P gardent leurs arrêts. Les pauses, un joueur éloigné, une erreur de chemin ou un repère indisponible arrêtent explicitement le guide.

La relance plus courte s’applique **seulement à une navigation inactive**, au plus cinq demandes par seconde ; aucun recalcul périodique d’un chemin actif n’est ajouté. Les recherches de PNJ/repères, la visibilité et les marqueurs conservent leur architecture. La consigne +50 % ne constitue pas une mesure de vitesse sur la carte ni un résultat de FPS/MSPT.

### Caméras

L’entretien conserve les plans communs des échanges M01/M05c et les plans sur l’interlocuteur des autres blocs. Le point commun est remonté vers les visages, la visée n’a plus de biais horizontal, le plan commun est élargi selon la séparation des PNJ, les plans individuels réduisent le décalage d’épaule et la transition native reste progressive (450 ms). Les PNJ s’orientent vers leur interlocuteur.

Les profils sont appliqués pendant l’ouverture de la seule session privée, puis l’ancienne configuration est immédiatement restaurée. Aucun réglage global ou fichier de configuration Dialogue n’est écrit. Le contrôleur b10 et son traitement des collisions restent utilisés. Si le point commun dépasse la portée d’ouverture, le cadrage utilise un acteur privé accessible ; si aucun ne l’est, le joueur doit se rapprocher sans perdre sa progression.

**Le rendu n’a pas été inspecté dans un client graphique.** Les paramètres, le centrage, le choix de cible et la restauration de configuration ont été contrôlés ; les obstacles et angles réels doivent être vérifiés sur la carte Indivis.

## Préservation

- **249 entrées du JAR HF8 identiques**, 8 entrées modifiées, 1 ajout. La liste exacte figure dans `rapports/hf9_build.json`.
- Tous les mixins, le client, PQ01/Gautier, le journal existant et la séquence I conservent leurs entrées binaires HF8. Aucun autre mod ne change.
- **111 identifiants narratifs stables** ; seules les listes de réponses de T01 et T04 s’enrichissent de navigation. Les répliques du script, les autres nœuds et les décisions ne sont pas réécrits.
- Les **50 Martins automatiques pendant F06**, les réponses, les gains conditionnels, le souvenir global et les gardes de récompense restent ceux de HF8. Aucune nouvelle récompense de lore.
- Le **core HF8** et **hc_quetes Fusion C1** sont conservés **octet pour octet**. Les 70 fiches du journal, les corrections de Clément, Hugues et MQ01–MQ03 ne sont pas réécrits.
- Aucun objectif scoreboard, commande d’installation ou repère n’est ajouté ou renommé. Le nouveau champ d’attente est dans le stockage de scène déjà cloisonné par personnage et propriétaire.

## Résultats des contrôles exécutés

| Banc sur le candidat livré | Résultat |
| --- | --- |
| Compilation Java 21, assemblage du JAR HF8 et conservation | PASS |
| Conservation/statique | **394 contrôles PASS** |
| Règles Java et politique Nexus | **145 assertions PASS**, dont 108 combinaisons de décisions |
| Serveur Fabric réel, JAR de production et mixins, fonctions core et Fusion C1 | **1710 assertions PASS**, dont **88 propres à HF9** et **105 propres à C1** |
| Pipeline de messages et état b10 natifs sur les 111 nœuds | **566 assertions PASS** |
| Registre de quêtes réel | **70 fiches, 0 erreur** |
| Chargement des fonctions incluses dans le banc | **0 erreur de parsing**, 7832 fonctions incluses |
| Exceptions du runtime PQ02 | **0** |

Le parcours chronologique démarre par les interactions de PNJ et rejoue **deux escortes A–P**. Il contrôle les deux pauses chez chaque joueur, six secondes de maintien du dialogue à chaque pause, la reprise explicite et par Échap, une reprise disque Nexus avant l’ouverture d’une pause, les sauvegardes périodiques, les choix, l’inscription et les 50 Martins. Il vérifie aussi les cadrages paramétrés b10 et leur repli à portée, l’identité du guide privé, la stabilité du PNJ public, la suite auprès d’Aurèle, Hugues et le retour MQ03 Althéon → Archives → MQ04.

Full integration core ticks=839, narrative interleavings=251. Les 105 cas C1 rejouent notamment insignes perdus, paiement des dix Martins, refus puis acceptation d’Aurèle, persistance du drapeau et nettoyage des documents de branches à deux joueurs.

### Limites explicites

Le monde est une fixture locale, pas la carte Indivis. Les connexions, l’accusé de réception client et la sélection Nexus sont synthétiques ; le writer/loader OPT3 est réellement exécuté. Les joueurs du test ont les permissions nécessaires aux hooks de profils fournis. Le pipeline client est testé sans moteur graphique. Monnaie remplacée par des pépites d’or, compétences/bonus par compteurs ; identité et mmospawn simulés ; sept récompenses d’équipement de mods absents ne sont pas exercées. **Aucun substitut de test n’est livré.** Le seul message ERROR du serveur de test est une récupération de clé Yggdrasil indisponible dans cet environnement ; aucun échec PQ02 ou de fonction n’est masqué.

Pas de test en jeu sur le serveur utilisateur, de rendu graphique des caméras, de doublage réel, ni de benchmark FPS/MSPT annoncé. Les preuves HF8 antérieures sont historiques et ne sont pas comptées comme de nouveaux tests HF9.

## Recette visuelle restante

1. Sur un personnage qui n’a pas dépassé D/H, laisser Léovic marcher puis vérifier ses deux arrêts, le temps de lecture et les questions. Tester « Reprenons notre route. » et Échap.
2. Rester quelques blocs en retrait pendant un arrêt, puis s’approcher. Vérifier qu’une autre conversation ouverte reste prioritaire.
3. Vérifier la marche entre les repères réels, notamment virages, marches et différences de hauteur.
4. Pendant M01/M05c puis les réponses au magistrat, inspecter les visages, les décors et les collisions de caméra avec le modpack réel.
5. À deux joueurs, vérifier à l’écran les copies privées et les pauses indépendantes ; terminer sans revenir pour recevoir les 50 Martins.

## Empreintes SHA-256

| Fichier | SHA-256 |
| --- | --- |
| `haute-capitale-quetes-HF12-PQ02-R1-HF9-1.21.11.jar` | `bce5ff70728f9e72617db734ecf83f1043fe343687a3806c3c3cd8841bdcae11` |
| `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9_QMARK2_PQ02_R1_HF8.zip` (inchangé) | `6ff671f7b71ff696980b1fdfa4302481dec30cac7db8eca6e0fb2c9bcd4a9aa6` |
| `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4_QMARK2_PQ02_R1_HF8_FUSION_C1.zip` (inchangé) | `d9a0fb0d0aecd0eb4f9bc64f9cb7bae932ddaaa7f67273b042512c22ff9e67aa` |

Les preuves détaillées sont dans `rapports/`. Les instructions de première préparation restent dans `COMMANDES_PQ02.md` ; elles ne sont pas à refaire pour HF9.
