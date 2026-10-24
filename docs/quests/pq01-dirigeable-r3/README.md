# PQ01 — Sous pavillon de la Couronne — R3-HF7

24 octobre 2026 — Fabric 1.21.11, EasyNPC 7.16.0 et Dialogue b10.

Gautier part pendant la réplique de Géraud, puis peut commencer son interruption en marchant. L’arrivée ne fait pas sauter les répliques. Il reste sur place pendant l’échange et repart à pied après sa dernière réponse. Un trajet refusé le conserve au dernier point atteint au lieu de supprimer son acteur et réafficher l’original à son poste. La tolérance d’arrivée accepte l’arrêt à environ 1,31 bloc observé dans le log.

Les notifications Minecraft de connexion/déconnexion restent dans le chat et ne sont plus capturées comme paroles de PNJ. Les questions répétables et les enchaînements HF6 restent intégrés.

**Remplacer uniquement le core sur le serveur et le journal sur le serveur et chaque client. Conserver hc_quetes HF4 et Dialogue b10.** Les PNJ, leurs apparences, liaisons et points existants sont réutilisés ; aucune commande de pose ou de reset à refaire pour la mise à jour.

[Installation et commandes de pose](docs/INSTALLATION_ET_POSE_PNJ.md) · [causes et vérifications](HOTFIX_SCENE_CAPTURE_R3_HF7.md) · [relecture et recette](docs/TESTS_EN_JEU.md).

Le nouveau banc associe le vrai contrôleur de scène, les vraies fonctions Minecraft, la marche EasyNPC et la capture native. Le rendu caméra/panneau et l’écoute audio dans le modpack complet restent hors de ce banc. Sources, documentation et preuves : [PR GitHub nº177](https://github.com/Arthuuuurrr/Indivis/pull/177).
