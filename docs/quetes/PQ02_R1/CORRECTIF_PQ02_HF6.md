# Indivis — PQ02 HF6 : sauvegarde Nexus et fenêtre de Léovic

31 octobre 2026 — Minecraft/Fabric 1.21.11, Java 21, Nexus PRE35-SI1-OPT3, Dialogue b10.

## Installation depuis HF1 à HF5

1. Arrêter le serveur et fermer Minecraft sur les clients.
2. Remplacer le JAR de quêtes par `haute-capitale-quetes-HF12-PQ02-R1-HF6-1.21.11.jar`, sur le serveur et chaque client. Garder un seul JAR de quêtes.
3. Redémarrer et reprendre le clic normal sur Léovic.

Aucune commande, liaison, pose, préparation de parcours ou réinitialisation à refaire. Les deux datapacks sont strictement identiques à HF1/R1. Conserver le Nexus OPT3 installé, Dialogue, EasyNPC, Voices et les autres dépendances. Aucun nouveau mod compagnon.

## Cause identifiée et reproduite

Les nouveaux logs confirment HF5 des deux côtés. Les sessions privées 18 et 20 reçoivent leur première page `a01`, puis ferment avec `ECRAN_FERME`, à 11:29:55 et 11:30:00 côté serveur. Les horloges client et serveur diffèrent d'une heure.

Le véritable code Nexus OPT3 appelle `ProfileStateBridge.save` tous les 100 ticks, soit cinq secondes au rythme normal du serveur. Cette sauvegarde appelle `capitale:profiles/before_save_self`. L'intégration PQ02 R1 avait placé `indivispq02 suspend` au début de ce hook. Cette commande fermait la session et supprimait la scène lors d'un simple instantané périodique. Le hook pose aussi un verrou de profil que la scène ne doit pas conserver après un instantané.

Le test reproduit cette fermeture avec le JAR HF5 exact, le véritable `ProfileStateBridge.save` OPT3, les fonctions du core fourni et une session native b10. Le joueur de test est opérateur, comme Arthur ; Nexus exécute le hook avec les permissions du joueur. Les tests précédents de fenêtre avaient simulé un retrait client et omettaient cet appel périodique. Ils ne prouvaient donc pas la correction de ce défaut.

## Correction

HF6 adapte uniquement l'appel périodique de sauvegarde dans Nexus, depuis le mod de quêtes intégré. Le moteur de sauvegarde Nexus, son fichier de profil et le hook core continuent à s'exécuter. Pendant cet appel, une scène PQ02 valide enregistre sa position sans se suspendre ; son verrou temporaire retrouve sa valeur d'entrée. La session, l'acteur privé, le choix et la navigation restent en place.

La même protection conserve un premier clic déjà en attente lorsque l'instantané arrive avant la création de l'acteur. Elle est bornée au joueur, au personnage et au monde de l'appel, puis libérée dans un `finally`. Les sauvegardes finales, déconnexions, changements de personnage et suspensions explicites gardent leur fermeture habituelle. La protection ne s'applique pas à une scène d'un autre personnage ni à un profil déjà verrouillé.

Aucune recherche d'entités ou nouvelle boucle de tick n'est ajoutée. Les classes de dialogue côté client, Gautier, le graphe narratif, les récompenses, les marqueurs et les deux datapacks restent identiques à HF5.

## Vérifications et limites

**43 assertions de reproduction HF5, 398 de sauvegarde/scène, 2 425 narratives serveur, 566 de protocole client et 9 contrôles de démarrage passent.** La garde anti-doublon passe également.

Les preuves actuelles sont dans `verification/hf6.json` et `verification/merged_tests_summary.json` : reproduction sur HF5, instantanés sur les 111 blocs, escorte, deux joueurs, sauvegarde finale, clic en attente, suites narratives et protocole Fabric/b10, démarrage du JAR livré.

Le test de démarrage applique réellement le nouveau mixin à la classe Nexus OPT3 d'origine. Le banc de sauvegarde emploie le vrai rédacteur de profils, sa politique et les fonctions Minecraft ; la sélection des comptes et les connexions restent synthétiques. Les essais client utilisent le vrai protocole avec des surfaces graphiques doublées. Aucun client graphique sur la carte Indivis, cadrage rendu, FPS ou doublage n'a été testé ici. La recette finale sur le modpack réel reste à confirmer.
