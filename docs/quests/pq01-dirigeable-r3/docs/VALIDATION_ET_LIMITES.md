# Validations exécutées et limites

- 12 contrôles des datapacks finaux : JSON, chemins et références internes, périmètre protégé, 145 passages approuvés, choix et amnésie obligatoires, déclenchements de conclusion, récompenses, magistrat, relecture par compte/personnage, jetons/voix/timeout, priorité MQ03 et amarrage avec gardes de contexte.
- 17 cas et 26 assertions exécutés sur le contrôleur `Pq01Motion` réellement compilé : aller/retour, arrivée, attente, chemin refusé/coupé, portée/dimension, annulation, perte de contexte, délais bornés, replanification et deux ports à dix secondes de décalage. Les ports de ce banc ne sont pas des joueurs Minecraft réels.
- 44 assertions sur la vraie classe `V2QuestPolicy` extraite du Nexus fourni : persistance des huit états et caractère transitoire des quatorze scores de runtime. Ce n’est pas un test de changement de personnage dans le jeu.
- PASS ASM BasicVerifier: 173 classes, 1704 methods; no Minecraft API resolution.
- 159 classes originales du journal vérifiées identiques ; une classe de visibilité patchée intentionnellement. Contrats Minecraft/Fabric/Mixin et outils de construction absents du JAR livré.
- Aucune référence interne obligatoire manquante dans core + quêtes. Les 22 appels externes CapSkills préexistants sont inchangés ; les macros et références facultatives ne sont pas confondues avec des fonctions manquantes.
- Aucune entrée supprimée. Autres fiches de quêtes, gardes, Léovic et magistrat conservés. L’inventaire exhaustif et les hash avant/après sont dans les audits.

La scène mobile et les occupations sont **implémentées** dans R3. Leur rendu réel dépend des PNJ liés, des points réellement configurés et du chemin praticable du décor. Un repli explicite est prévu en cas d’indisponibilité ; il n’est pas utilisé comme remplacement permanent de l’animation.

**Aucun démarrage Minecraft/Fabric et aucun test en jeu n’ont été réalisés ici.** Le monde, les exports NBT réels et une session à deux joueurs ne sont pas disponibles dans cet environnement. Sont donc encore à vérifier en jeu : chargement des mixins avec le modpack complet, rendu skins/équipements, navigation sur le pont/escaliers, paquets de visibilité et caméra, interactions near, changement de personnage Nexus, reconnexion et récompenses normales. `TESTS_EN_JEU.md` donne les scénarios et résultats attendus.

Les sorties ne sont pas présentées comme certifiées sans bugs. Elles sont fournies pour recette sur copie du monde, avec rollback ciblé, sans installation en production ni merge.
