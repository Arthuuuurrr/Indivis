# PQ01 R3-HF8 — Enchaînements et service de Perrin

24 octobre 2026. Fichiers examinés : les deux logs client/serveur fournis le 24 octobre, test de HF7 confirmé des deux côtés. Aucun log brut utilisateur n’est distribué.

## Changements et causes

**Répliques successives.** Le contrôleur attendait la fin de la frappe, puis conservait un budget de lecture global et une seconde de présence sur la dernière page. Pendant ce temps, une page automatique ne proposait que la fermeture. HF8 retire ce second temps lorsque la frappe progressive est active et enchaîne dès que le texte et la voix sont achevés. La page de capture doit avoir été rafraîchie avant validation ; un écran affichant encore l’ancienne page ne peut pas valider la nouvelle. Si la frappe progressive est désactivée, le budget de lecture subsiste pour le texte affiché d’un coup. Les vraies questions restent manuelles. Les durées audio configurées et la lecture réelle d’Indivis Voices continuent de bloquer l’avance tant qu’elles ne sont pas finies.

**Perrin.** Le tick d’ambiance excluait l’étape PQ01 0. De plus, la navigation standard visait une destination avec une distance native de tolérance d’un bloc, et le contrôleur considérait une distance de 1,5 bloc comme une arrivée. Des points proches pouvaient donc ne produire aucun déplacement. HF8 active l’ambiance dès l’étape 0 et donne à Perrin un chemin vers le bloc exact, avec une tolérance de fin adaptée à l’écartement A/B. Le trajet de Gautier garde sa tolérance HF7, qui couvre l’arrêt observé dans le précédent log.

L’échec avant la première progression a été reproduit avec le JAR HF7 dans le nouveau banc. Une simple baisse de la tolérance finale ne suffisait pas : le chemin natif s’arrêtait encore à environ 0,97 bloc sur le trajet long. Le changement définitif porte donc aussi sur la précision du calcul du chemin, puis a été vérifié en navigation réelle sur les trajets longs et sur un trajet d’un bloc.

Les logs utilisateur ne contiennent pas les coordonnées enregistrées ni le réglage d’ambiance. Ils ne permettent pas d’attribuer l’immobilité de son Perrin à un seul de ces cas. Le statut administrateur affiche maintenant l’ambiance, la phase et les coordonnées actives ; un point manquant est signalé dans le log serveur. Les points et les liaisons sont réutilisés, sans en inventer de nouveaux. Le réglage explicite `ambient_enabled=0` est respecté.

## Installation

Remplacer uniquement **haute-capitale-quetes-0.1.0+1.21.11.b7-SEQUENCE-I-SI1b-PQ01-R3-HF8.jar** sur le serveur et tous les clients. Conserver le core **HF7** et le datapack de quêtes **HF4**. Aucun placement, liaison, reset de quête ou changement de Nexus/Dialogue/EasyNPC n’est requis. Voir le guide complet `docs/INSTALLATION_ET_POSE_PNJ.md`.

SHA256 journal : `fd0f45425638693fdd66b221929ddc84b32d037047d1397b4d0ee4a309a2d0f3`.

## Vérifications effectuées

- 15 contrôles du tick d’ambiance réel : démarrage à l’étape 0, trajet d’un bloc, arrêt pour la conversation de Perrin, reprise, retour, points distincts du quai, désactivation/réactivation et nettoyage à la sortie du personnage. Aucun appel direct à `travel` dans ce banc d’ambiance.
- 20 contrôles de la scène de Gautier : départ pendant Géraud, parole en approche, maintien pendant l’échange, retour à pied, chemins bloqués conservant l’acteur et absence de saut de position.
- 24 contrôles de monde Fabric/EasyNPC : copie d’apparence et actions, quatre trajets, escalier, couloir, dalles et destination bloquée.
- 1042 contrôles de fonctions/capture natives : choix successifs, branches du capitaine, fermeture/reprise, jetons et files audio ; les notifications de connexion restent hors du dialogue.
- 41 assertions de cadence client : page périmée refusée, frappe achevée sans pause ajoutée, pagination, voix active/en chargement/en fin, texte instantané, fermeture et réessais bornés.
- 191 assertions de capture sur le JAR Dialogue b10 intact, 120 contrôles d’API natives et 16 membres clients contrôlés.
- PASS ASM BasicVerifier: 182 classes, 1776 methods; no Minecraft API resolution. Les références de fonctions, JSON et commandes documentées sont vérifiées séparément.

## Conservation et limites

Le journal change **6 entrées**, sans ajout ni suppression de classe. Tous les mixins, les helpers historiques, la fabrique d’acteurs et la séquence I gardent les mêmes octets qu’en HF7. Les deux datapacks sont intégralement identiques à leurs versions conservées. Les textes, 95 identifiants, checkpoints, menus, récompenses et hooks audio ne changent pas.

Le banc utilise les vrais contrôleurs, fonctions Minecraft et navigation EasyNPC. Les profils Nexus, l’affichage, la mise en caméra et le rendu sont doublés ; la préparation de caméra de Perrin est appelée pour vérifier l’arrêt de sa navigation. Aucun client graphique du modpack ni écoute audio n’a été effectué, et la géométrie du monde utilisateur n’a pas été fournie. La séquence I est conservée et vérifiée par références, sans nouveau parcours de sa carte.

## Direction d’acteur

Le dossier `direction_acteur` fournit six fiches de rôle, 92 directions en français et les textes canoniques. Les prompts Qwen sont proposés par personnage et par prise, avec `text` et `instruct` séparés. Aucun son n’est généré, remplacé ou activé par ce dossier. Un ZIP vocal autonome est aussi livré.
