# HF5 — proximité de Géraud et reprise après fermeture

23 octobre 2026 — Minecraft Fabric 1.21.11 / EasyNPC 7.16.0 / Dialogue b10.

## Constats et cause examinée

Les nouveaux logs client et serveur confirment HF4 installé. Le personnage devient disponible à 16:05:31, heure serveur ; l’action EasyNPC de proximité ouvre Géraud dès 16:05:32. Le pont HF4 autorisait encore le near jusqu’à 16 blocs. Le log enregistre également le rejet du point d’approche de Gautier au-delà de quatre blocs de Géraud, puis une fermeture de caméra REMPLACEE sans nouvelle ouverture journalisée.

Le code natif de Dialogue b10 ferme l’ancienne session avant de contrôler la distance d’ouverture de la nouvelle ; il peut ensuite retourner null. HF4 ne traitait pas ce retour et pouvait laisser PQ01 actif sans caméra. Une réouverture rencontrait alors le garde « déjà actif » et n’affichait aucune page. Ce scénario est reproduit par les tests de refus ; les logs seuls ne donnent pas la position exacte du locuteur au moment du refus. Un second défaut concernait la première capture ouverte au clic : elle n’était pas toujours rattachée à la vue PQ01 avant son remplacement, donc sa fermeture pouvait manquer le nettoyage.

## Comportement HF5

- La proximité attend au moins 100 ticks après disponibilité du personnage, un déplacement horizontal d’au moins 0,75 bloc et une distance réelle au PNJ de trois blocs maximum. Le suivi est par compte, personnage et monde. Un clic volontaire reste utilisable immédiatement.
- La proximité est observée côté pont même si EasyNPC a déjà déclenché son ancien événement dans un rayon plus large. Les entrées de proximité historiques sont filtrées. Un personnage en chargement ne reçoit aucun message de dialogue technique.
- La première interpellation automatique ne se répète pas après fermeture. Les conversations déjà entamées se reprennent au clic ; le mode de relecture volontaire réarme le near après son TP initial.
- La première capture est rattachée à la vue PQ01. La fermeture libère la vue et les états temporaires. Une session orpheline, y compris laissée par HF4, est récupérée avant le garde d’ouverture. Les checkpoints persistants, récompenses, profils et autres conversations ne sont pas réinitialisés.
- Le changement de caméra vérifie d’abord la portée réelle configurée dans Dialogue. Si le locuteur est trop loin, une caméra valide déjà ouverte est conservée et la trame de dialogue garde le nom du locuteur. Si l’ouverture est refusée ou si aucune caméra n’est valide, PQ01 se ferme proprement pour permettre un nouveau clic.
- Lors d’une reprise pendant l’approche de Gautier, le cadre d’attente est renvoyé après ouverture de caméra, sans points de suspension ni validation automatique. Le dialogue reprend à l’arrivée.
- Le contrôle supplémentaire qui rejetait le point d’approche de Gautier à plus de quatre blocs de Géraud est retiré. Les points existants sont réutilisés. La navigation conserve ses limites de dimension, 24 blocs par trajet et délai borné ; aucun TP de scène n’est ajouté.

Les corrections HF4 de marche, copie d’apparence, Anselme, noms, questions, lecture continue et conclusion verte restent intégrées.

## Installation depuis HF4

Remplacer **core par HF5** et **journal par HF5**, puis redémarrer serveur et clients. Le journal va des deux côtés. Conserver le datapack `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4.zip`, strictement inchangé. Les deux fichiers à remplacer et le [guide complet](docs/INSTALLATION_ET_POSE_PNJ.md) sont dans la livraison.

Aucune liaison à refaire, aucun point ni PNJ à replacer. Le prochain clic sur Géraud récupère un état bloqué laissé par HF4. Aucune commande de reset ou de relecture n’est exécutée par l’installation.

## Validation et portée

- **41 assertions** exécutent le vrai pont du JAR : rayon, délai, immobilité au spawn, déplacement minimum, clic immédiat, ancien near lointain, chargement, autres quêtes, fermeture, réouverture, session orpheline, première capture, refus natif simulé, conservation du checkpoint, fallback caméra, attente de marche et réarmement de relecture. Monde, horloge, effets des fonctions et gestionnaire caméra sont doublés ; ce n’est pas une recette graphique.
- Les bancs existants restent verts : 80 assertions NBT/actions Minecraft/EasyNPC réels ; 3 sur le dispatcher natif ; 23 sur les interactions avec contexte doublé ; 34 sur l’adaptateur client/cadence ; 18 cas et 27 assertions de marche ; 118 recherches de membres publics ; 12 contrôles d’ABI client et classification verte réelle de Dialogue b10.
- Monde isolé réel redémarré avec Minecraft 1.21.11, Loader 0.19.5, Fabric API 0.141.6 et EasyNPC 7.16.0 : **19 contrôles**, **quatre trajets** parcourus, apparence copiée, interaction d’acteur conservée et originaux immobiles. Les fonctions PQ01 sont chargées par le vrai dispatcher. La sélection Nexus et les paquets de visibilité sont doublés ; aucun client connecté.
- 94 pages actives et 95 identifiants stables ; ASM : **180 classes / 1 762 méthodes**. Le datapack quêtes, tous les mixins, les helpers historiques, la fabrique d’acteurs, le contrôleur de marche et l’adaptateur client sont identiques à HF4. Le journal change uniquement le pont, ses classes internes et ses métadonnées ; une classe interne de suivi near est ajoutée. Le core change uniquement les six entrées de dialogue, le near de Géraud, l’attente de reprise, le réarmement explicite de test et ses métadonnées.

Le rendu, la caméra et le son dans le modpack complet restent à observer en jeu. Le test de navigation utilise un sol plat ; il ne valide pas le relief de la carte utilisateur. Aucun monde utilisateur ouvert ni déploiement. La PR #177 reste en brouillon.

## Fichiers

| Composant | Action depuis HF4 | SHA-256 |
| --- | --- | --- |
| `capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF5.zip` | Remplacer | `12cf679a919ccaf0f50aee45ad79e81dc5f034c1f5e9985c29edab02f12a9960` |
| `hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4.zip` | Conserver | `71478ff2edf9c0246891429144407e5a8532e16baf5a5361494dfb4b78edd153` |
| `haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF5.jar` | Remplacer | `4ad2b51ffb6641540be5769203a2169c4a4bb85c765c0f2898ffb5fdb13bc018` |
