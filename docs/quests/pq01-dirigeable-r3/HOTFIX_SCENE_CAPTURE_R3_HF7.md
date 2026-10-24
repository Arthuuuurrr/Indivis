# HF7 — Gautier en approche, maintien de l’acteur et capture des notifications

24 octobre 2026 — core et journal HF7 ; hc_quetes HF4 conservé.

## Ce que montrent les pièces fournies

La capture contient une notification de connexion d’un autre joueur ajoutée à la réplique de Géraud. Dialogue b10 accepte ce message de jeu dans sa fenêtre de capture, comme une phrase non préfixée.

Le log serveur signale un échec de chemin de Gautier alors que sa position est à environ **1,31 bloc** de la destination. Le seuil HF6 était de 1,25 bloc. Le callback d’échec supprimait ensuite son acteur personnel et rendait à nouveau visible le PNJ original à son poste : cela explique le retour instantané apparent pendant la conversation. Le trajet commençait seulement après la lecture de la réplique de Géraud, puis bloquait le dialogue jusqu’à l’arrivée, ce qui additionnait les deux temps d’attente.

Les logs annoncent journal HF6 côté serveur et HF4 côté client. Le défaut de trajet et la capture de notification ont des corrections propres ; ce constat de versions ne remplace pas leur diagnostic.

## Changements

- **Départ anticipé :** Gautier commence à marcher pendant la réplique de Géraud « À bord d’un dirigeable… ». La fin de cette réplique reste contrôlée par son temps de lecture et sa voix.
- **Interruption pendant l’approche :** sa première réplique peut commencer même si sa marche n’est pas achevée. L’arrivée ne lance aucune réplique à elle seule et ne fait pas sauter le dialogue.
- **Même acteur pendant l’échange :** il reste au dernier point atteint pendant la remontrance et sa dernière réponse, puis repart à pied. Un échec d’approche, de reprise ou de retour arrête la marche et conserve l’acteur sur place, sans réafficher l’original. Le nettoyage à la fermeture reste actif.
- **Arrêt natif accepté :** rayon d’arrivée de 1,5 bloc, avec un écart vertical de 0,75 bloc maximum. La position relative exacte relevée dans le log est couverte par un test. Un autre étage n’est pas assimilé à une arrivée.
- **Notifications hors des paroles :** les événements Minecraft de connexion, connexion avec ancien nom et déconnexion sont filtrés dans la classification de Dialogue b10 et restent visibles dans le chat normal. Le filtre utilise leurs clés de traduction, y compris dans un composant enveloppé ; il ne supprime pas une réplique littérale contenant les mêmes mots. Il s’applique à l’ensemble des conversations capturées. Le JAR Dialogue n’est pas remplacé : le journal HF7 ajoute un mixin uniquement client sur cette classification.

Les questions répétables, l’attente audio sans perte de choix, la proximité de Géraud, la reprise au clic et la conclusion verte du Capitaine restent celles de HF6. Les 95 identifiants, textes approuvés, sauvegardes de relecture, récompenses et transitions historiques sont conservés.

## Vérifications exécutées sur les fichiers HF7

**Nouveau banc de scène couplée : 20 contrôles réussis.** Il exécute le contrôleur `Pq01Bridge` de production, les vraies fonctions PQ01, les composants Text/capture b10 et la navigation EasyNPC dans un monde Fabric 1.21.11 réel. Il observe le départ pendant a2, la réplique a3 pendant la marche, le même acteur près du point pendant a4/a5, la réponse a6 avant le retour, le retour à pied avant a7, les deux chemins bloqués, la conservation de l’acteur et l’absence de saut instantané de position. L’objectif de regard natif Dialogue/EasyNPC est aussi invoqué pendant la marche. Le panneau et la caméra restent doublés.

**Capture native avec le nouveau mixin effectivement appliqué :** 13 contrôles supplémentaires vérifient les trois événements de connexion/déconnexion, leur présence dans le chat, leur absence des répliques, les composants enveloppés et la conservation d’une phrase littérale de PNJ. Pour permettre cette vérification dans le serveur d’essai, le mixin client est déclaré commun uniquement dans le JAR de test et les copies des classes de capture ont leur annotation CLIENT retirée. La livraison garde ce mixin dans la section client ; les anciens mixins sont inchangés.

Les bancs conservés passent également : **1 042 contrôles du graphe**, **24 contrôles du monde et des trajets**, **191 assertions de capture sur le JAR b10 intact**, marche **22 cas / 34 assertions**, 80 NBT/actions, 41 de régression Géraud, 23 d’interactions, 38 de cadence client, trois de dispatcher EasyNPC, 119 recherches de membres natifs et 13 contrôles de membres clients. ASM : **182 classes / 1 774 méthodes**.

Les essais ne lancent pas de client graphique. La caméra/panneau et l’identité Nexus sont doublés ; l’écoute du son et le rendu complet ne sont pas validés. Le graphe remplace les corps historiques de TP/récompenses par des marqueurs dans le banc uniquement. La géométrie de la carte utilisateur n’est pas disponible ; les coordonnées relatives de l’échec observé sont reproduites dans un test et les trajets réels sont effectués dans le monde d’essai.

## Conservation et périmètre

Le core ne change que `pq01/node/a2` et ses métadonnées. Le journal modifie le pont de scène, la tolérance de marche et ses métadonnées, et ajoute le filtre/mixin client. Les anciens mixins, la fabrique d’acteurs, les helpers historiques, le client de lecture HF6 et les classes de séquence I sont identiques à HF6. Le datapack hc_quetes reste identique à HF4.

Les **12 018 références de fonction**, **931 JSON**, **10 370 entrées du core hors PQ01** et **101 fonctions MQ01–MQ03 du datapack quêtes** sont contrôlés. Gardes, biomes et zones du messager sont conservés. Aucun parcours complet de la séquence I n’est revendiqué sur la carte utilisateur.

## Mise à jour

L’archive contient seulement le core complet HF7 et le journal complet HF7, plus les guides et preuves. **Remplacer le journal sur le serveur et chaque client.** Conserver hc_quetes HF4, Dialogue b10, Nexus et EasyNPC actuels. Les liaisons, apparences, points et progressions sont réutilisés ; aucune commande de pose ou de reset n’est requise pour cette mise à jour. Le filtre d’affichage nécessite le journal client HF7.

| Composant | SHA-256 |
| --- | --- |
| `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF7.zip` | `308c246eef6e90e73b48f5705efc9e5c3aca2fa3d06d6baf304e4af5e2f9df94` |
| `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF7.jar` | `443ad08565e8dfbb7260bed7ba89f785c169c344a6e7baca02d0dccde24924af` |
| `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4.zip` (conservé) | `71478ff2edf9c0246891429144407e5a8532e16baf5a5361494dfb4b78edd153` |

[Guide complet](docs/INSTALLATION_ET_POSE_PNJ.md) · [relecture et recette](docs/TESTS_EN_JEU.md) · preuves : `audit/validation.json`, `audit/world_test.txt`, `audit/diffs.json` et `audit/sequence_i_preservation.json`. Sources et documentation mises à jour dans la PR #177, sans merge ni déploiement sur le serveur utilisateur.
