# HF4 — dialogues, interlocuteurs et acteurs PQ01

## Comportement livré

- Les diagnostics de scène vont dans le journal serveur. Les refus de contexte utilisent une barre d’action neutre.
- Les acteurs EasyNPC sont créés via l’EntityType Minecraft, chargés avant insertion, avec UUID propre, skin, modèle et équipement copiés. Leur action de clic est posée par l’API EasyNPC après insertion et relue en test après 20 ticks réels.
- Gautier atteint son approche, parle, puis retourne à son poste. Le seuil d’arrivée passe de 0,8 à 1,25 bloc : le test réel a révélé un arrêt vanilla à 0,972 bloc suivi d’un refus de recalcul. Les quatre trajets réussissent avec HF4, sans TP de scène.
- Chaque page attend l’ouverture de caméra du bon acteur. Le cadre client met à jour le nom avant la capture des lignes ; le nom reste simplement Gautier pendant son interruption. Le retour quitte sa caméra avant la marche.
- Anselme utilise `hcm_pq1_anselme`. L’ancienne étiquette d’archiviste est retirée seulement si PQ01 l’avait ajoutée. L’avancement archiviste exclut ce tag réservé, y compris si une étiquette ancienne avait été posée manuellement. Les sauvegardes de liaison et les autres actions restent protégées.
- Le Capitaine commence au clic droit ; le hook de proximité PQ01 devient sans effet. Sa page initiale vide est redirigée vers sa première vraie ligne.
- Les pages linéaires n’ont plus de bouton Continuer. Le client attend lecture, dernière page et fin de voix, puis envoie le même choix à nonce. Les menus de questions et les réponses du protagoniste restent manuels. Les panneaux sans texte ne montrent plus l’attente « … ».
- La réponse qui termine l’entretien du Capitaine et lance l’amarrage est verte et explicitement marquée. Les questions restent accessibles avant cette sélection.

## Installation et conservation

Remplacer **core HF4 + hc_quetes HF4 + journal HF4**. Le journal va sur serveur et clients. Conserver Dialogue b10, EasyNPC 7.16.0, Nexus et les autres mods présents. L’installation ne lance aucun reset, ne recrée aucun PNJ original et ne change aucune coordonnée. Les 95 identifiants de nœud, les sauvegardes de relecture, la progression et les points restent utilisables. Aucun nouveau mixin n’est ajouté ; les classes de mixin de HF3 sont identiques octet pour octet.

Les points déjà enregistrés dans les logs utilisateur sont réutilisés. Les commandes de liaison et de pose sont inchangées ; voir [le guide complet](docs/INSTALLATION_ET_POSE_PNJ.md). Si l’ambiance avait été suspendue pour l’édition, sa reprise est `/data modify storage capitale:pq01_setup ambient_enabled set value 1`.

## Contrôles exécutés

- Monde isolé réel : Minecraft 1.21.11, Fabric Loader 0.19.5, Fabric API 0.141.6+1.21.11, EasyNPC 7.16.0 et les deux mixins PQ01. 19 contrôles, quatre trajets au sol parcourus, UUID distincts, apparence non standard copiée, action native de clic conservée et originaux immobiles. Tous les scripts PQ01 sont chargés par le vrai dispatcher ; les prédicats requis sont inclus dans ce banc. La sélection Nexus et les paquets de visibilité sont doublés, le joueur est un vrai ServerPlayer construit hors ligne. Aucun client graphique connecté.
- NBT Minecraft et actions EasyNPC réels : 80 assertions ; vrai CustomActionDispatcher : 3 assertions avec initiateur non nul.
- Pont d’interaction du JAR : 23 assertions avec contexte de monde/caméra/Nexus doublé.
- Adaptateur client et cadence du JAR : 34 assertions avec écran, réseau et état vocal doublés, dont refus d’avance pendant une voix active/en chargement/en fin de lecture, page en frappe, menu de questions et fermeture.
- Contrôleur de marche : 18 cas, 27 assertions, dont régression de l’arrêt à 0,972 bloc.
- Vrai Dialogue b10 : la réponse Minecraft GREEN est classée QUETE par son code natif.
- 118 recherches de membres publics, 94 pages actives contrôlées, 95 identifiants stables. ASM : 179 classes / 1 756 méthodes.
- Inventaire strict HF3 → HF4 : journal limité au pont/actions/identifiants/marche, adaptateur client/cadence/fabrique et métadonnées ; core limité à PQ01 et au near du Capitaine ; datapack quêtes limité à l’avancement Anselme et métadonnées. Aucun fichier retiré. Toutes les classes de mixin et les helpers historiques sont identiques.

L’adaptateur vise le Dialogue b10 fourni. Il utilise ses champs de nom/fenêtre et les méthodes publiques du panneau ; ses cadres vides sont interceptés avant la capture de chat. Indivis Voices 0.2.3 est facultatif : l’adaptateur attend ses champs de lecture active/pending/fading, en plus du délai audio configuré. Aucun JAR Dialogue ou Voices n’est remplacé.

## Limites de la preuve

Le monde de test est un sol plat isolé ; il ne valide pas le relief ni les obstacles de la carte utilisateur. Le rendu, les transitions de caméra, le son réel et la coexistence du modpack complet nécessitent une recette graphique. Les tests de champs vocaux sont doublés, pas une lecture OGG réelle. Les captures/logs utilisateur diagnostiquent HF3 et ne prouvent pas HF4 en jeu. Aucun monde utilisateur ouvert, aucun serveur utilisateur démarré, aucun déploiement ni merge.

Les sources et preuves sont dans `tools/pq01_full/` et `audit/hf4_*` sur la PR #177.

## Fichiers

| Composant | Octets | SHA-256 |
| --- | ---: | --- |
| `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF4.zip` | 4946795 | `58fe4586a0b03246a72c93d9df7c6a606626f9688cde16d980b46febd5a16a92` |
| `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4.zip` | 1516080 | `71478ff2edf9c0246891429144407e5a8532e16baf5a5361494dfb4b78edd153` |
| `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF4.jar` | 474323 | `3cbacb3427cbce13f43cd3fcd2afc19d7b8ab90e3ea3dd3b8d00fe92908d5e99` |
