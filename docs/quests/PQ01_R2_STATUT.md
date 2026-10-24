# Statut PQ01

Version actuelle : **journal R3-HF10**, core **R3-HF9**, datapack `hc_quetes` **R3-HF4** conservé.

Les scènes utilisent des copies personnelles. HF10 refuse les acteurs des autres joueurs avant les contrôles historiques et regroupe les rafraîchissements de visibilité par joueur/tick. La création de deux acteurs passe de deux parcours natifs en HF9 à un en HF10 dans le même banc. Gautier n’a plus de copie d’ambiance inactive ; ses scènes et sa marche restent disponibles.

Les 48 contrôles multijoueur passent avec deux scènes et deux Perrin simultanés, un observateur et un arrivant tardif : aucun paquet de création d’acteur privé étranger, marche réelle, fermeture indépendante, changement de personnage et déconnexion. Le vrai QuestVisibility, son mixin d’origine, les trackers et paquets Minecraft sont exécutés. Connexions, chunks reçus et profils Nexus synthétiques ; aucun FPS graphique mesuré. La visibilité privée passait aussi dans le banc HF9 de référence : aucune fuite supposée n’est présentée comme une cause prouvée de FPS.

Les régressions de lecture, orientation et doublage passent : 1 161 contrôles de fonctions/capture, 25 de scène, 26 en monde réel, 15 d’activité, 44 de lecture client, 191 de capture b10 intacte. Textes, 95 identifiants, 92 prises, blocs manuels, choix verts, file audio et code client HF9 conservés.

Perrin marche dans le banc, y compris sur un trajet d’un bloc et au quai. L’état de ses points sur la carte utilisateur reste inconnu : aucune ligne PERRIN_DIAG dans les nouveaux logs. Les échecs de ronde et points manquants produisent maintenant un diagnostic automatique sans texte parasite. Le gain de FPS du modpack et l’immobilité propre à cette carte ne sont pas déclarés résolus. Les erreurs NativeLayers23/transition Nexus du client HF5 sont documentées séparément.

Remplacer uniquement le JAR de quêtes/journal sur le serveur et tous les clients. Conserver core HF9 et hc_quetes HF4. Aucun reset, aucune nouvelle liaison ni pose. Les gardes, zones sûres du messager et la séquence I sont conservés ; aucun parcours complet de la carte utilisateur. Aucun Nexus inchangé redistribué.

[Changements HF10](pq01-dirigeable-r3/HOTFIX_MULTIJOUEUR_R3_HF10.md) · [installation et pose](pq01-dirigeable-r3/docs/INSTALLATION_ET_POSE_PNJ.md) · [preuves](pq01-dirigeable-r3/audit/hf10_validation.json).

PR nº177 en brouillon ; aucun merge ni déploiement, aucun log brut utilisateur publié.
