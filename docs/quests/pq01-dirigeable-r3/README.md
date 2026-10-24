# PQ01 — Correctif de cadence et service de Perrin — R3-HF8

24 octobre 2026 — Minecraft Fabric 1.21.11, EasyNPC 7.16.0 et Dialogue b10.

Les répliques successives de Géraud, d’Althéon et des autres PNJ avancent dès la fin de la frappe et de la voix. Le délai supplémentaire de lecture et le repos final sont retirés lorsque le texte se révèle progressivement. Le temps de lecture reste disponible lorsque la frappe est désactivée ; les questions du joueur restent manuelles.

Perrin peut commencer son service avant la première progression PQ01. Son trajet vise le bloc exact, afin que des points proches ne soient pas tous considérés comme déjà atteints. Le correctif conserve les points existants, le fonctionnement de Gautier, les questions répétables, la conclusion verte et le filtre des notifications.

**Remplacer uniquement le JAR du mod de quêtes/journal sur le serveur et chaque client. Conserver le core HF7 et `hc_quetes` HF4.** Aucun PNJ ni point à replacer, aucune quête à réinitialiser.

- [Installation et toutes les commandes de pose](docs/INSTALLATION_ET_POSE_PNJ.md)
- [Causes, portée et vérifications](HOTFIX_CADENCE_PERRIN_R3_HF8.md)
- [Relecture et recette](docs/TESTS_EN_JEU.md)
- [Direction humaine en français](direction_acteur/DIRECTION_ACTEUR_FR.md)
- [Prompts Qwen en français](direction_acteur/PROMPTS_QWEN_FR.md)

Le dossier vocal contient six fiches de rôle, 92 directions de prise, les textes exacts et des prompts individuels ou regroupés en JSONL. Aucun fichier audio n’est généré ou remplacé.

Sources et documentation : [PR GitHub nº177](https://github.com/Arthuuuurrr/Indivis/pull/177). Le banc couvre les fonctions, le contrôleur et la navigation réels ; le rendu et le monde utilisateur restent hors du banc.
