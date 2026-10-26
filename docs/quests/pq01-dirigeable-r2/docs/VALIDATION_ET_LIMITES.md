# Validation et limites — PQ01-R2

Résultats propres à cette reconstruction ; aucun résultat du candidat R1 perdu n'est réutilisé.

- 12 cas de contrôle statique réussis sur les archives produites : JSON, dépendances internes, périmètre des modifications, répliques exactes, vrais choix, passages obligatoires de l'amnésie, fins explicites, conservation du corps des récompenses, magistrat inchangé, sauvegardes par compte/personnage, gardes de contexte/jeton/voix/timeout, priorité MQ03 et autres fiches du journal.
- 931 fichiers JSON/pack.mcmeta chargés par le validateur.
- 95 nœuds ; 145 occurrences de dialogues/questions comparées au prompt.
- 0 référence interne obligatoire manquante dans core + quêtes. Les macro-appels dynamiques ne sont pas assimilés à des chemins littéraux ; les tags `required:false` sont traités comme facultatifs. Les références externes CapSkills déjà présentes restent inchangées ; leur liste est dans `audit/validation.json`. Cela ne certifie pas l'installation d'un autre datapack sur le serveur.
- 40 assertions exécutées sur la classe V2QuestPolicy réellement extraite du Nexus fourni : les nouveaux états `hch_`/`hcf_`/`hcd_` sont persistants, les `hcm_` transitoires. Ce banc teste une politique Java, pas une sauvegarde/restauration de personnage en jeu.
- Aucun fichier retiré des deux datapacks. Gardes, magistrat, Léovic, Aurèle, Roch et autres quêtes préservés par comparaison des contenus. Aucun JAR modifié ; aucun ancien JAR redistribué comme correctif.
- SHA-256 des entrées et sorties : `audit/diffs.json` et `SHA256SUMS`. Inventaire de chaque entrée changée/ajoutée : `audit/DIFFS_EXHAUSTIFS.csv`.

## Ce qui n'est pas validé

Pas de serveur Minecraft/Fabric lancé ici et pas de monde/NBT des six PNJ fourni. Le contrôle n'est pas un chargement Brigadier/Minecraft, un test réseau/caméra, une recette à deux joueurs ou une restauration réelle des profils Nexus. Les commandes publiques sont contrôlées comme chemins existants dans le ZIP, pas proclamées jouées en jeu. La compatibilité avec un mod/banque de voix supplémentaire non fourni reste à vérifier.

**Mouvement : absent de R2.** L'architecture de visibilité SI1b sait masquer les entités `hcm_private` aux autres personnages, mais cette visibilité ne résout pas à elle seule les collisions et l'IA d'un double EasyNPC mobile. Faute de reconstruire et tester le correctif de collision/navigation et sans exports du monde, ce lot laisse les PNJ existants en place et utilise une interruption textuelle personnelle. Il n'y a pas de chemin fictif, de téléportation d'un Gautier à travers un mur ou de clone permanent. L'animation individuelle demandée reste à développer et à recetter ; elle n'est pas présentée comme achevée.

La recette complète à effectuer est dans `INSTALLATION_ET_TEST.md`. Les conclusions de sécurité sont limitées aux contrôles ci-dessus ; aucun « zéro bug » n'est garanti.

## Références de format

Les hooks et structures existants proviennent des archives 1.21.11 fournies. Les notes officielles Mojang décrivent `click_event` et son champ `command` depuis 1.21.5 : https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-5 . Les macros et `return run` sont décrits ici : https://www.minecraft.net/en-us/article/minecraft-snapshot-23w31a . Ces références de syntaxe ne constituent pas un essai du candidat dans la version 1.21.11.
