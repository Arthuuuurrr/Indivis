# PQ01 — Sous pavillon de la Couronne — R3-HF5

23 octobre 2026 — Minecraft Fabric 1.21.11, EasyNPC 7.16.0, Dialogue b10.

HF5 corrige l’interpellation de Géraud dès le spawn et la conversation impossible à rouvrir après fermeture. La proximité attend cinq secondes après disponibilité du personnage, un mouvement du joueur et une distance de trois blocs. Le clic volontaire reste immédiat. Fermer ne provoque pas de nouvelle ouverture automatique ; le clic suivant reprend au checkpoint enregistré. Les refus de caméra libèrent l’état temporaire, y compris un blocage laissé par HF4.

Le rejet supplémentaire du point d’approche de Gautier à plus de quatre blocs est supprimé ; les bornes de navigation restent inchangées. Un interlocuteur hors de la portée de caméra ne provoque plus de remplacement aveugle qui laisse une page vide. Les corrections HF4 (marche, noms, Anselme, questions, conclusion verte et lecture continue) sont conservées.

**Depuis HF4 : remplacer uniquement core et journal.** Le journal va sur serveur et clients. Garder `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4.zip`. La pose des PNJ, leurs liaisons, apparences et points restent valables ; aucune commande de placement ou de reset n’est nécessaire.

[Guide complet de pose](docs/INSTALLATION_ET_POSE_PNJ.md) · [cause, changements et preuves](HOTFIX_REPRISE_R3_HF5.md) · [recette en jeu](docs/TESTS_EN_JEU.md).

Les contrôles de régression du pont utilisent un contexte de monde/caméra doublé ; les trajets et le chargement des fonctions sont aussi contrôlés dans un monde Fabric/EasyNPC isolé réel. Le rendu client et le modpack complet restent à recetter. Aucun monde utilisateur ouvert ni déploiement effectué. Sources, documentation et preuves sur la [PR GitHub nº 177](https://github.com/Arthuuuurrr/Indivis/pull/177), conservée en brouillon.
