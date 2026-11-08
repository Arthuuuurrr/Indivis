# PQ03 R1 HF1 — Réputation au moment du dialogue

**8 novembre 2026 — Minecraft Fabric 1.21.11.** Cette mise à jour applique la demande d’Arthur : la réputation doit changer dès la réponse du joueur, avec un indicateur visible. Elle remplace sur ce point la règle de versement différé décrite dans le dossier du 7 novembre et le rapport R1. Les dialogues et les parcours restent ceux de R1.

## Ce qui change

- **PQ03 :** les décisions B04 et C05 modifient immédiatement `REP_GARDECOEUR` et le souvenir `CAP_PQ3_IMPR`. Les choix positifs donnent +1, le mépris final −1 ; les autres choix ne modifient pas la réputation. Le total reste compris entre −1 et +2 hors effet des bornes de faction.
- **PQ02 :** les scores étaient déjà modifiés à chaque décision. Leur calcul, leurs garde-fous et les réponses restent inchangés ; la mise à jour ajoute seulement l’indicateur visible.
- **Affichage :** un bandeau privé de quatre secondes indique par exemple « Garde du Cœur : +1 en réputation », en vert ; une perte est rouge. Il est dessiné dans l’écran de dialogue, séparément des répliques et de leur historique. Aucune réponse système ou fausse phrase du joueur n’est ajoutée à la conversation. Les choix neutres ne produisent aucun bandeau.
- **Bornes :** la notification correspond à la variation réellement appliquée après les limites existantes. Une faction déjà à +100 n’affiche pas un faux +1 ; le souvenir personnel peut néanmoins évoluer selon la réponse.

La réputation change dès que le serveur valide le choix : il ne s’agit pas d’un aperçu annonçant un versement futur. Fermer la conversation après une réponse ne retire pas ce choix ni son effet.

## Conclusion et anti-double gain

Les deux marqueurs persistants `CAP_PQ3_RA` et `CAP_PQ3_RB` indiquent que l’effet de chaque décision a été traité. Ils sont enregistrés par Nexus avec le personnage actif, comme les autres scores `CAP_PQ3_*`.

La conclusion C06 continue à exiger la fermeture réelle de l’au revoir pour terminer PQ03 et attribuer les **175 XP, compétences et prime technique**. Elle ne contient plus d’addition de réputation ni de recalcul du souvenir. Réouvrir une question, recharger un profil, changer de personnage ou répéter la fonction de conclusion ne redonne aucun point.

Le contrat `reputation_ready` et la fonction interne `pq03/reputation/instant_ready_self` empêchent un mélange de versions du mod et du core de terminer silencieusement la quête avec le mauvais mode de versement. Remplacer ensemble le JAR et le core, comme indiqué ci-dessous.

## Migration depuis R1

| État du personnage au passage à HF1 | Traitement |
|---|---|
| Aucune décision prise | Aucun gain ; les prochains choix sont traités immédiatement. |
| R1 en cours avec une décision mémorisée mais non versée | L’effet en attente est appliqué une fois à la reprise/migration, puis marqué comme traité. Les prochains choix restent immédiats. |
| PQ03 déjà terminée sous R1 | La réputation et le souvenir acquis sont conservés ; aucun gain rétroactif. |
| Choix neutre déjà mémorisé | Marqué comme traité, sans point ni notification fictive « +0 ». |

La version interne du personnage passe à 2. Les marqueurs de versement sont distincts des valeurs de choix : une réponse neutre est aussi verrouillée, et ne peut pas être remplacée ensuite par une réponse favorable. Aucun état de parcours, waypoint, dialogue ou récompense externe n’est remis à zéro.

## Fichiers modifiés face à R1

| Composant | Périmètre |
|---|---|
| Core | Deux fonctions modifiées : `pq03/load` et `pq03/complete_once_self`. Une fonction de contrat ajoutée : `pq03/reputation/instant_ready_self`. Tous les autres fichiers R1 sont identiques. |
| Mod fusionné | Effet/migration PQ03, notification de l’effet PQ02 déjà immédiat, réception et dessin du bandeau dans le client existant. Trois classes de notification ajoutées. |
| Graphe PQ03 | Les 57 blocs et toutes leurs répliques/choix restent identiques ; seule la note technique de versement est actualisée. |
| `hc_quetes` Fusion C1 R1 | Archive complète strictement identique à R1. |
| Nexus, HUD, Dialogue, EasyNPC | Aucun JAR remplacé ou modifié. |

PQ01/Gautier, les routes de Léovic/Aurèle, les marqueurs, le versement automatique des 50 Martins et la séquence I restent ceux de la livraison précédente. La correction de la question de l’ascenseur vers le Cœur est conservée. Le retrait du choix n°6 de Hugues et la chaîne MQ03 capitaine → Archives sont rejoués dans les tests de régression.

## Vérifications exécutées

- **2 113 assertions serveur PASS**, dont deux routes PQ03 simultanées, effet dès B04 et C05, montant réel dans la notification, absence de versement final supplémentaire, reprise Nexus aux cinq phases, migration R1 active/terminée/neutre et borne de faction +100. Les 88 contrôles HF9 et les 105 contrôles Fusion C1 sont rejoués.
- **9 assertions après un véritable redémarrage PASS** : même monde, même JAR, compte non opérateur ; le point N, la page, la réputation immédiate, l’impression et le marqueur de décision traitée sont conservés sans rejeu.
- **302 contrôles du protocole client PQ03 PASS** : les 57 blocs restent fonctionnels. L’événement Fabric reçoit la notification sans l’ajouter aux répliques ou aux choix. Les commandes de dessin du bandeau produisent le texte vert/rouge attendu ; expiration et nettoyage à la fermeture vérifiés.
- **566 contrôles du protocole PQ02 PASS** avec PQ03 et son affichage chargés.
- **98 assertions Java PASS**, dont 16 combinaisons de décisions, correspondance du barème immédiat, politique Nexus OPT3 réelle et messages de notification valides/invalides.
- Conservation des archives et des sources compilées vérifiée dans `static.json`. Vrai chargeur de journal : **70 fiches, 0 erreur** dans la fixture. Aucun substitut de test distribué.

Les nombres et les empreintes sont liés dans `validation.json`. Les tests utilisent les vraies classes serveur Fabric, Dialogue b10, EasyNPC 7.16 et le writer/loader Nexus OPT3. Les connexions et la sélection Nexus sont synthétiques ; monnaie, CapSkills et primes externes sont adaptés dans les seules fixtures. Sept équipements externes ne sont pas exercés.

**Limite :** le dessin a été vérifié par ses appels et sa réception dans le protocole, sans rendu graphique sur la carte Indivis. Aucune capture d’écran réelle, mesure FPS/TPS/MSPT ou vérification audio n’est déclarée réalisée.

## Installation depuis PQ03 R1

1. Arrêter le serveur et fermer le jeu ; conserver la sauvegarde habituelle du monde et des profils.
2. Remplacer le **core** par le ZIP portant `PQ03_R1_HF1`.
3. Remplacer le **JAR de quêtes fusionné sur le serveur et chaque client** par celui portant `PQ03-R1-HF1`.
4. Redémarrer complètement serveur et clients.

**Aucune commande à refaire, aucun PNJ ou waypoint à replacer, aucun reset de quête.** Le journal `hc_quetes` R1, Nexus et le HUD restent en place. Le petit paquet HF1 contient seulement les deux composants changés ; l’archive d’installation complète conserve aussi le journal R1 inchangé pour une première installation.

L’absence de dialogue « … » signalée pendant le premier essai avait été observée avec le JAR PQ03 manquant côté client, ce qu’Arthur a confirmé. HF1 ne remplace pas la nécessité du même JAR de quêtes sur les deux côtés. Le résultat de la recette graphique d’Aurèle après cette installation n’est pas encore connu.

Les empreintes des trois composants figurent dans `SHA256SUMS.txt`. Les éléments R1 archivés conservent la description de l’ancien versement ; ce document HF1 fait foi pour le nouveau comportement.
