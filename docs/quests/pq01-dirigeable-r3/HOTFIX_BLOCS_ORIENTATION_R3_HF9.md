# PQ01 R3-HF9 — Blocs lisibles, orientation et préparation du doublage

24 octobre 2026. Référence de départ : core HF7 et journal HF8. Retour étudié : capture du début de Géraud et description des problèmes par Arthur. Aucun nouveau log serveur ni état enregistré des points de Perrin n’a été fourni pour ce retour.

## Changements

### Lecture des blocs

L’avance dès la fin de frappe introduite par HF8 était trop rapide pour laisser lire. HF9 remplace ce mécanisme par une lecture manuelle : aucune page ni aucun bloc ne disparaît sur un minuteur. Espace, Entrée ou clic révèle/parcourt d’abord le texte via le moteur natif ; à la dernière page achevée, l’action demande la suite. Les vraies réponses du protagoniste et les questions restent des choix. Une indication de navigation est affichée dans l’interface ; elle n’est ni une réplique ni une fausse réponse.

Les répliques consécutives d’un même PNJ sont regroupées jusqu’à un vrai choix, un changement d’interlocuteur ou une transition. « Matelot ! » et la remontrance sont donc un même bloc ; l’accueil C1+C2, la question C3+C4, la réflexion C5+C6+menu et la conclusion CX1 à CX4 aussi. Le regroupement ne mélange pas Gautier et Géraud dans un même titre. Les variantes de reprise conservent leurs anciens points d’entrée.

Les commandes d’interface sont limitées à la session PQ01 à laquelle la trame appartient. Une autre conversation ne récupère ni l’ancien nom, ni une demande de suite ou une indication PQ01. Les contrôles utilisent les événements d’écran de Fabric API 0.141.6, déjà présent dans le log client fourni précédemment. Aucun nouveau mixin n’est ajouté.

### Orientation de Géraud

Son acteur regardait encore selon son orientation copiée et l’arrivée de Gautier pouvait le réorienter indépendamment de la réplique active. HF9 le place face au joueur à l’ouverture, le maintient ainsi pendant A1/A2, le tourne vers Gautier pendant A3 à A6 et le ramène vers le joueur pour A7. L’arrivée de Gautier ne provoque plus à elle seule un retournement prématuré.

### Perrin : correction vérifiée et cause restante à établir

Un PNJ décoratif peut avoir une vitesse de base nulle. La copie conservait cet attribut, même après activation de son IA : aucun chemin ne pouvait alors le faire marcher. HF9 remet une vitesse de base positive uniquement sur l’acteur temporaire si elle est nulle, en conservant celle du PNJ original. Le banc vérifie ce cas par une vraie marche.

Cela ne prouve pas que la vitesse nulle soit la cause de l’immobilité du Perrin de la carte. L’état d’ambiance, les liaisons, points A/B, coordonnées et obstacles actuels ne sont pas dans la capture. Pour l’établir, une commande OP ciblée est ajoutée :

```mcfunction
/function capitale:pq01/admin/diagnostic_perrin
```

Elle écrit `[Indivis PERRIN_DIAG HF9]` dans le journal serveur avec la phase, progression, ambiance, positions, points A/B, vitesse effective et raison actuelle. Aucun déplacement, reset ni changement d’attribut de l’original. Fournir cette ligne ou le log après exécution permet de traiter le cas réel. L’ambiance explicitement désactivée reste désactivée.

### Doublages futurs

Les 92 prises gardent leurs textes, identifiants et hooks `voices.<prise>`. Un bloc d’écran peut contenir plusieurs prises individuelles, jouées en file par le serveur. Une prise vide n’ajoute pas de silence fantôme ; un choix reçu pendant la file attend sa fin ; fermer arrête le son courant et annule le reste. L’amarrage attend la validation du dernier bloc et la fin de voix, en gardant la conclusion verte comme entrée volontaire.

Le guide `docs/DOUBLAGE_BLOCS.md` et `config/BLOCS_ET_PRISES.json` donnent la correspondance et la procédure d’intégration ultérieure dans Indivis Voices. Aucun nouveau son ni JAR Indivis Voices n’est fourni.

## Installation

Remplacer le core serveur par **capitale_core_1.5.7_V2_SEQUENCE_I_SI1_PQ01_R3_HF9.zip** et le journal serveur **et clients** par **haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF9.jar**. Conserver **hc_quetes_b22_V2_SEQUENCE_I_SI1b_PQ01_R3_HF4.zip**. Aucun PNJ, point, liaison ou progression à recréer.

Le guide d’installation a été repris pour indiquer uniquement le comportement actuel ; les anciennes consignes de cadence HF8 sont retirées de sa partie opérationnelle.

## Vérifications sur les composants livrés

- 1161 contrôles de fonctions et capture natives : blocs, questions successives, branches A–E du capitaine, conclusion verte et validation finale, jetons périmés, fermeture et file audio de deux prises.
- 25 contrôles de scène couplés : orientation A1/A2 puis A3/A4 et A7, approche parlée de Gautier, maintien pendant l’échange, retour, chemins bloqués et absence de saut de position.
- 26 contrôles en monde Fabric/EasyNPC : copie d’apparence/actions, quatre trajets, escalier, couloir, dalles, chemin bloqué et source Perrin à vitesse nulle, récupérée seulement sur l’acteur.
- 15 contrôles d’activité : démarrage à l’étape 0, trajet d’un bloc, arrêt/reprise pour la conversation, points distincts du quai, désactivation/réactivation et nettoyage lors du changement de personnage.
- 44 assertions client : lecture sans minuterie, frappe/pagination, clavier/souris, boutons de fermeture, durée/chargement/fin de voix, annulation et isolation d’une autre session.
- 191 assertions de capture sur Dialogue b10 intact ; 27 membres et contrats clients contrôlés, dont les événements d’écran de la version Fabric API présente dans les logs.
- PASS ASM BasicVerifier: 182 classes, 1791 methods; no Minecraft API resolution. 12493 références de fonctions résolues ; les commandes du guide sont vérifiées lors de l’assemblage.

## Conservation et limites

Le journal change 6 entrées, sans nouvelle classe ni suppression. Les mixins, helpers historiques, fabrique d’acteurs et intégrations de la séquence I gardent leurs octets HF8. Le core change 222 entrées et ajoute 306 entrées, toutes limitées à PQ01 et à la description du pack. Les fonctions historiques restent présentes. Les 10370 entrées du core hors PQ01 sont identiques ; `hc_quetes` reste strictement HF4, dont gardes, priorités et quêtes principales conservées.

Le banc utilise les vrais contrôleurs, fonctions Minecraft, commandes, macros, captures et navigation EasyNPC. Les profils Nexus, la caméra, le panneau, les entrées et le rendu sont doublés. Le test d’orientation mesure les angles réels des acteurs du monde isolé ; il ne remplace pas l’observation d’une caméra graphique. Aucun client du modpack, écoute de voix définitives ou parcours de la carte utilisateur n’a été effectué. L’immobilité du Perrin de cette carte reste à diagnostiquer avec son état réel ; elle n’est pas déclarée résolue sur la seule foi du banc. La séquence I est conservée et vérifiée par références, sans nouveau parcours complet de sa carte.
