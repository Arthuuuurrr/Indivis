# Vérifications QMARK1 — 26 octobre 2026

**224 contrôles natifs réussis**, sur Fabric 1.21.11 et EasyNPC 7.16.0.

- 24 quêtes du core recensées directement depuis leurs écritures de migration ; aucune n'est absente du catalogue. 20 donneurs reconnus par leur nom sur de vraies entités EasyNPC.
- Offres, étapes acceptées, prérequis, rangs, délais, escorte, conflit de quête active et priorité des livraisons testés avec les vrais prédicats et fonctions Minecraft.
- Le vrai `hc_quetes:runtime/second` appelle le nouveau repère ; ses autres intégrations sont remplacées par des fonctions sans effet dans ce banc uniquement.
- Deux propriétaires et un observateur : contrôle des vrais trackers et paquets d'apparition/destruction avec `QuestVisibility` et `EntityTrackerMixin` natifs. Changement de personnage, verrou de profil, déconnexion et nettoyage d'orphelins vérifiés.
- Conservation du même identifiant sur 20 mises à jour ; absence de nouvelle apparition réseau pour ce repère immobile. Suivi d'un déplacement, suppression hors de portée et liaison d'un PNJ renommé vérifiés.
- NBT des 20 PNJ d'essai et tous les états/dénouements de quête conservés par les mises à jour.
- Toutes les fonctions du core préexistantes restent identiques octet par octet, dont les dialogues, déplacements, gardes, zones protégées et fonctions administrateur. Dans `hc_quetes`, seule une ligne d'appel commentée est ajoutée à la fin de `runtime/second`, avec la description du pack actualisée.
- JAR HF11 strictement identique : SHA-256 `1f63a074ae4871d02daa0c70729032b3985f9b5029548a9539ae5627625a1f83`. Aucun nouveau mod, mixin ou point d'entrée.

Les identités Nexus, connexions et réceptions de chunks sont synthétiques dans le banc. Aucun client graphique n'a été lancé : ni apparence à l'écran ni gain de FPS mesuré. Les noms et la géométrie de la carte utilisateur restent à vérifier lors de la recette. Le correctif n'ajoute pas de marqueurs aux quêtes hors du core ; leurs marqueurs existants restent conservés.

Les résultats détaillés et empreintes sont dans `audit/`. Reconstruction : `python3 work/pq01_full/build_quest_markers.py`, puis `CORE_QUEST_MARKERS=1 python3 work/pq01_full/run_world_test.py` et `python3 work/pq01_full/verify_quest_markers.py`.
