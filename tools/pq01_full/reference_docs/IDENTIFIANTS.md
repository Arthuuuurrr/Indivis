# Identifiants et compatibilité

Identifiant public inchangé : `pq_01_sous_pavillon_couronne`. Autorité principale inchangée : `hcq_pq01`. Le registre CSV transmis décrivait encore une migration à faire, alors que les archives reçues utilisent déjà les identifiants v2 ; aucune nouvelle migration globale n'est exécutée.

Étapes : 0 avant contact ; 10 Géraud/cabine ; 20 entretien Althéon ; 40 arrivée au quai ; 100 terminé. La fiche du journal marque désormais le capitaine terminé à 40 et le retour au quai comme remise disponible entre 40 et 99. Les 69 autres fiches sont inchangées.

Nouveaux états persistants, reconnus par la politique Nexus SI1 : `hch_pq1node` (reprise de lecture), `hcf_pq1_a` (intro Géraud), `hcf_pq1_c` (intro Althéon), `hcf_pq1_av` (mention Avaleiv par Althéon), `hcf_pq1_gain` (gain normal consommé), `hcf_pq1_test` (relecture), `hcf_pq1_ver` (initialisation locale), `hcd_pq1_banter` (cooldown). Les préfixes `hcm_pq1_*` sont transitoires ; ils ne portent aucune progression durable.

Propriétaires : Géraud 91, Althéon 92, Gautier 93, Hugues 94, Anselme 95, Perrin 96. Le propriétaire reste celui du PNJ qui ouvre l'échange, même lorsque Gautier interrompt Géraud. `QuestChoix` reçoit un nouveau bloc de valeurs par affichage, avec un nonce stocké entre 1 000 000 000 et 1 999 999 900 ; indices 1–7 pour les choix et 15 pour fermer. Les anciens boutons ne correspondent plus au jeton actif. Les mêmes scores ne sont pas utilisés pour deux joueurs.

Les séquences héritées 101–103, 201–251 et 401–403 sont redirigées vers les nouveaux routeurs et ne gardent aucune ancienne attribution automatique. Les séquences 301–303 restent l'amarrage reçu. Les autres séquences sont inchangées. Le relais fixe PQ01 de `QuestChoix=5` est remplacé par le bouton neuf du nœud `c0` ; les relais Léovic/Aurèle/Roch restent en place.

Les personnages existants ne subissent aucun reset de quête/rang : première initialisation locale, un état >=10 est considéré comme ayant déjà rencontré Géraud ; >=40 comme ayant déjà terminé l'introduction d'Althéon ; >=100 comme ayant déjà consommé le gain. Pour rejouer les nouveaux dialogues, utiliser le mode de test avec sauvegarde. La continuité historique de chaque ancien enregistrement doit encore être contrôlée en jeu.

Stockages : `capitale:pq01_runtime` (nonce et ancres par compte, avec vérification du personnage), `capitale:pq01_tests` (sauvegardes par couple compte/personnage), `capitale:pq01_config` (timeout, voix, ambiance). Points de journal : `hc_quetes:lieux pq01_<role>_<large|quai>`. Tag éphémère d'ambiance : `hcm_pq1_viewer`, ajouté et retiré dans la même fonction. Aucun tag durable n'est ajouté à un PNJ.
