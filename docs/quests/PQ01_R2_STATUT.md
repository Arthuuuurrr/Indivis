# Statut PQ01

Version actuelle : **journal R3-HF8**, core **R3-HF7** et datapack hc_quetes **R3-HF4** conservés, administration alpha.3.

HF8 retire l’attente ajoutée entre les répliques automatiques lorsque la frappe du texte est terminée, tout en attendant la voix et le rafraîchissement de la capture. Sans frappe progressive, le budget de lecture est conservé. Les questions restent manuelles.

Perrin commence son activité dès l’étape 0. Son chemin vise le bloc exact, avec une tolérance adaptée aux points proches. Le banc valide aussi l’arrêt pendant sa conversation, la reprise, le retour, les points du quai, la désactivation et le nettoyage. Les coordonnées configurées du navire utilisateur ne figurent pas dans les logs fournis.

Remplacer uniquement le journal sur le serveur et chaque client ; conserver les deux datapacks. Aucun PNJ, point ou liaison à replacer, aucune progression à réinitialiser. Les corrections de Gautier et le filtre des notifications HF7 restent intégrés.

Le dossier de direction d’acteur fournit six fiches de rôle en français et 92 prises avec texte canonique, intention et prompt Qwen. Aucun audio généré ou remplacé.

[Causes et preuves HF8](pq01-dirigeable-r3/HOTFIX_CADENCE_PERRIN_R3_HF8.md) · [installation et pose](pq01-dirigeable-r3/docs/INSTALLATION_ET_POSE_PNJ.md) · [direction et prompts](pq01-dirigeable-r3/direction_acteur/) · [livraison](pq01-dirigeable-r3/README.md).

Contrôles réussis : 15 d’ambiance par le tick et la navigation réels ; 20 de scène Gautier ; 1 042 du graphe/capture ; 24 en monde Fabric ; 191 de capture b10 intacte ; 41 de cadence client. Les deux datapacks sont identiques aux versions conservées. Les 54 adresses de fonctions documentées sont résolues. Caméra/panneau et Nexus doublés ; aucun rendu graphique complet ni parcours SI1 sur la carte réelle. PR #177 en brouillon ; aucun déploiement.
