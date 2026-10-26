# HF6 — Enchaînements, questions et navigation PQ01

23 octobre 2026. Succède à core/journal HF5 ; conserve hc_quetes HF4.

## Constats et correction

Les captures montrent une réponse de Géraud avec seulement la fermeture disponible, puis l’obligation de rouvrir pour demander autre chose. Le log serveur contient aussi « Trajet Gautier interrompu : chemin devenu inaccessible ». Le serveur annonce HF5 ; l’adaptateur client HF4 et celui de HF5 sont identiques, ce qui ne suffit pas à expliquer le problème par une différence de version.

Le code précédent dépendait d’un choix automatique envoyé une seule fois pour revenir aux questions. Un choix reçu pendant l’attente audio pouvait être ignoré ; le client avait alors déjà effacé sa capture et n’essayait plus. Cette perte est reproduite avec les vraies fonctions Minecraft dans le banc HF6. Les logs fournis ne permettent pas d’attribuer avec certitude le blocage observé à cette seule condition.

HF6 affiche les questions directement sous les réponses concernées : il n’existe plus d’avance automatique intermédiaire pour retrouver ce menu. Cela couvre Géraud au large et au quai, Gautier, Hugues, Anselme, Perrin et les fins des familles A–E du Capitaine. Les sous-questions rédigées restent manuelles. Les répliques linéaires continuent automatiquement après leur lecture et leur voix ; les pages intermédiaires se tournent après révélation du texte. Les délais de lecture sans voix sont raccourcis.

Le serveur conserve un choix reçu pendant une voix, puis le traite à la fin de l’attente. Le client réessaie le même jeton tant qu’aucune nouvelle page ne l’acquitte, au maximum une fois par seconde et soixante fois. Un ancien jeton est ignoré et ne peut pas valider la page suivante. La capture n’est pas vidée lors d’un envoi automatique en attente de confirmation. Une durée audio renseignée sans son associé ne bloque plus la lecture. Le délai de conversation repart à chaque nouveau passage, au lieu d’expirer après trois minutes cumulées d’entretien actif.

Le contrôleur de navigation ne remplace plus un chemin actif à intervalles fixes. Un premier calcul refusé laisse le temps à l’acteur de se poser, puis réessaie ; un recalcul refusé ne fait pas échouer un acteur encore en mouvement. Pour un point à hauteur fractionnaire, le calcul natif peut aussi essayer la hauteur de bloc immédiatement supérieure. Le point sauvegardé reste identique. Les limites de dimension, portée, délai, annulation et repli restent actives.

## Reprise des demandes précédentes sur PQ01

| Demande | État dans HF6 et contrôle |
| --- | --- |
| Temps de spawn et proximité de Géraud | Conservation HF5 : cinq secondes après disponibilité, déplacement de 0,75 bloc, distance de trois blocs ; clic volontaire immédiat. Banc de régression Géraud. |
| Fermer puis rouvrir sans « … » | Reprise au checkpoint et récupération d’état orphelin conservées ; ouverture réelle des fonctions après fermeture contrôlée. |
| Questions successives sans fermeture | Menus directement sous les réponses ; séquences répétées contrôlées avec fonctions et capture b10 réelles. |
| Enchaînement sans « Continuer » inutile | Pages linéaires automatiques, choix rédigés conservés, retransmission bornée et file d’attente audio. |
| Gautier seul dans l’en-tête | Les 94 pages actives contrôlent le nom natif de capture ; Gautier porte uniquement ce nom. La caméra réelle reste à observer dans le modpack complet. |
| Gautier rejoint Géraud, intervient puis repart | Graphe complet et replis contrôlés ; aller/retour EasyNPC réel sur marches et dalles ; originaux immobiles. Caméra et navigation ne sont pas testées ensemble avec un client graphique. |
| Perrin se déplace entre ses points | Aller/retour EasyNPC réel, contrôleur de marche commun corrigé ; points existants conservés. |
| Anselme et Kerrec distincts | Tag réservé PQ01 et exclusion de l’archiviste conservés ; ressource d’interaction identique à HF5. |
| Capitaine au clic et première vraie réplique | Near automatique désactivé et c0 redirigé vers c1, conservés ; introduction et familles A–E traversées. |
| Conclusion du Capitaine verte et explicite | Catégorie native QUETE vérifiée, choix « Terminer l’entretien et lancer l’amarrage. » conservé ; les questions précèdent sa validation. |
| Messages techniques hors paroles de PNJ | Attentes de marche et de voix sans tellraw technique ; diagnostics serveur conservés. Les messages des commandes administrateur restent des retours de commande. |
| Pose, skins, actions et points existants | Aucune nouvelle liaison nécessaire ; création/copie d’acteur, apparence et interaction contrôlées avec EasyNPC réel. Guide complet fourni. |
| Relecture et progression | 95 identifiants stables, sauvegardes de relecture conservées, fin et récompenses historiques inchangées. Aucun rang Visiteur ajouté. |
| Isolation des joueurs | Gardes de contexte, propriété et annulation conservées ; contrôles unitaires existants, dont ports de marche décalés de dix secondes. Pas de recette graphique multijoueur complète. |
| Séquence I, gardes et zones du messager | Datapack hc_quetes identique à HF4 ; core hors PQ01 identique à HF5. Références MQ01–MQ03 et gardes contrôlées. Aucun parcours complet de séquence I sur la carte réelle. |

Les commandes exactes des sentinelles restent `/function hc_quetes:liaison/sentinelle_archives_1` et `/function hc_quetes:liaison/sentinelle_archives_2`. Elles sont présentes dans le datapack conservé ; aucune pose n’est à refaire pour HF6. Les gardes existants ne sont pas recréés par ce correctif.

## Vérifications exécutées

- **1 029 contrôles de transitions** sur serveur Fabric 1.21.11 : fonctions et macros réelles, scoreboard, 671 cadres tellraw réellement émis et 438 commandes trigger. Le banc vérifie le graphe, les questions successives, l’introduction Géraud–Gautier, fermeture/reprise, l’entretien complet du Capitaine A–E et son choix vert, la course audio, les jetons périmés et le renouvellement du délai.
- **23 contrôles en monde réel EasyNPC 7.16.0** : quatre trajets, franchissement observé du palier et des dalles dans un couloir fermé empêchant le contournement, un chemin bloqué avec échec borné, aucune téléportation à travers le mur, originaux immobiles et nettoyage.
- **191 assertions de capture native b10**, sur le JAR Dialogue intact, avec décodage des vrais composants Text Minecraft pour les 94 pages actives ; noms et boutons contrôlés.
- Bancs conservés : 80 assertions NBT/actions, 41 de régression Géraud, 23 d’interactions, 38 de cadence client, trois de dispatcher EasyNPC, marche 20 cas / 32 assertions, 119 recherches d’API publique, 13 contrôles de membres clients et couleur verte native.
- ASM : 180 classes et 1 764 méthodes ; archives contrôlées par CRC. Les commandes de la documentation sont résolues contre les datapacks construits.
- **12 018 références de fonction résolues** dans 10 194 fichiers du périmètre prologue/MQ01–MQ03/gardes ; 931 ressources JSON décodées. Les 10 370 entrées du core hors PQ01 sont identiques à HF5. Les 101 fonctions MQ01–MQ03 de hc_quetes sont inchangées.

Le banc serveur utilise les classes de capture b10 avec uniquement leur annotation d’environnement CLIENT retirée pour ce banc ; le test séparé de capture utilise le JAR intact. Le profil Nexus, le panneau graphique, la caméra et l’émission de visibilité sont doublés. Les corps historiques de téléportation et de récompense sont remplacés par des marqueurs dans le banc de graphe, pour vérifier l’appel au bon stade. Ils ne sont pas remplacés dans la livraison. Aucun client graphique, rendu caméra ni écoute du son réel n’a été exécuté. La géométrie des points du monde utilisateur n’a pas été fournie : les trajets validés sont ceux du monde d’essai.

## Fichiers et installation

L’archive contient uniquement le core complet HF6 et le journal complet HF6. Le journal se remplace sur le serveur et sur chaque client. Garder hc_quetes HF4, Nexus, Dialogue, EasyNPC et les autres mods actuels. Fermer les dialogues, arrêter le serveur, remplacer ces deux composants, redémarrer et reprendre au clic droit. Les points et liaisons existants restent utilisés ; aucune commande de placement ou de reset n’est requise.

| Composant | SHA-256 |
| --- | --- |
| `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF6.zip` | `d84f1165dfbd6259e5c0dd141ee7d720ea0abb93488e76ec5a0c9a2a0f597e34` |
| `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF6.jar` | `44d73b77adf2296cd9b3ba2f8d55cb438a89f5ae1c18f594d3ab8481242d8918` |
| `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4.zip` (conservé) | `71478ff2edf9c0246891429144407e5a8532e16baf5a5361494dfb4b78edd153` |

Diff HF5 → HF6 : 256 entrées existantes du core modifiées, deux fonctions ajoutées, aucune supprimée ; modifications limitées à PQ01 et pack.mcmeta. Journal : sept entrées modifiées, aucun ajout/suppression ; pont, contrôleur de marche, cadence client et métadonnées uniquement. Tous les mixins, helpers historiques, fabrique d’acteurs et classes de séquence I restent identiques à HF5.

[Guide complet](docs/INSTALLATION_ET_POSE_PNJ.md) · [relecture et recette](docs/TESTS_EN_JEU.md) · preuves dans `audit/validation.json`, `audit/diffs.json`, `audit/world_test.txt` et `audit/sequence_i_preservation.json`.

Sources et documentation mises à jour dans la PR #177. Aucun merge ni déploiement sur le serveur utilisateur.
