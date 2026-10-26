# Indivis Nexus NPC — Vérification PREVIEW1

26 octobre 2026. **Préversion visuelle fonctionnelle en instance isolée ; charge
des poses animées non validée pour une foule.** Aucun déploiement sur la production,
aucune fusion dans `main`, aucun PNJ de quête réel modifié.

## Livrable et environnement

Module `Indivis-Nexus-NPC-0.1.0-PREVIEW1-1.21.11.jar`, 37 236 octets.
SHA-256 : `cc9cfb403e48c21a22d14e1bcc15152d3ae74dc86d5ab9ddae9a926d35836047`.
Reconstruit après récupération du code : empreinte identique au binaire testé
avant nettoyage de la session. Une seconde compilation produit la même empreinte.

Minecraft 1.21.11, Java Temurin 21.0.13+9, Fabric Loader 0.19.5,
Fabric API 0.141.6+1.21.11, EasyNPC 7.16.0 et son interface de configuration.
Base Nexus : PRE35 SI1, SHA-256
`399a4c64a7672e60ebbaaa52044cb547f5c41576039fe5aafe2c42485e2e8a8a`.
Ce JAR de base n'a pas été modifié.

Client Linux en rendu logiciel llvmpipe, sans shader actif ; Sodium 0.8.7,
ImmediatelyFast 1.14.2, 3D Skin Layers 1.10.2, Iris 1.10.7, EntityCulling 1.10.2,
PAL 1.1.10, owo 0.13.0, Cloth Config 21.11.153, Puffish Skills 0.19.0,
HUD OPT1 et couches Accessories de l'instance de référence.

## Vérifications réussies

| Contrôle | Résultat constaté |
| --- | --- |
| Import natif EasyNPC | Les 5 ressources sont découvertes par EasyNPC, chargées comme presets DEFAULT puis importées par son véritable gestionnaire. |
| Véritables PNJ | 5 humanoïdes Nexus créés par le module et 5 autres importés par EasyNPC, pas des joueurs factices utilisés comme PNJ de substitution. |
| Relief | Audit des 6 parties du modèle : maillages `NexusPosed23`, géométrie extérieure présente, couches cosmétiques effectivement appelées. Corps normal et slim. |
| Contrôle visuel | Captures en jeu des cinq silhouettes, de face puis avec la caméra placée derrière les vrais PNJ ; pas d'image de rendu théorique utilisée comme preuve. |
| Créateur Nexus | Clic réel sur « Créer » dans le mode PNJ : recette exportée, nombre de personnages joueurs inchangé. |
| Publication réseau | Envoi C2S réel puis lecture de la recette reçue côté serveur. Contrôle du seuil OP 4 et refus d'identifiants de chemins invalides. |
| Application sur un PNJ existant | UUID, nom, dialogue témoin, tag de quête et données NBT hors apparence conservés. Rotation et échelle d'origine respectées. |
| Applications successives | Les 5 presets appliqués au même modèle ne cumulent pas leurs facteurs d'échelle. |
| Restauration | `reset` rétablit le NBT d'origine, à l'exception de l'horodatage technique EasyNPC de dernière modification. |
| Sauvegarde | Aller-retour NBT d'entités ; fermeture et réouverture réelle du monde. Les 10 UUID sont comparés aux UUID des fichiers sauvegardés, avec conservation des 5 tags témoins. |
| Réutilisation d'état | Passage d'un PNJ Nexus à un PNJ ordinaire : pas de fuite de texture, d'oreilles, de barbe ou du drapeau de profil. |
| Invalidation | Vidage des caches de géométrie puis reconstruction et rendu effectif des 5 profils. Ce n'est pas un essai complet de toutes les variantes de rechargement de packs. |
| Nouveaux assets | Cheveux, barbe classique et tenue ajoutés sous l'ID 930 dans le harnais uniquement : découverte dynamique et rendu sur un vrai EasyNPC, sans changement du code de production. |
| Menus joueurs | 23 captures comparées à la même base sans le module : identiques pixel pour pixel. |
| Géométrie existante | 320 états de géométrie + 91 états de cache de poses comparés : fichiers identiques. Les autres assertions mains/HUD du harnais passent également. |
| Contenu du JAR | 5 recettes, 5 presets SNBT ; aucune classe Minecraft/Nexus/EasyNPC embarquée, aucun asset tiers remplacé, aucune classe du harnais dans le livrable. |

Les résultats frais sont conservés dans `qa/last-*.json`, les synthèses de chaque
essai dans `qa/evidence-*/pass.txt`, la comparaison dans `qa/comparison.json`.

## Raccord de rendu corrigé

Minecraft choisissait son rendu joueur pour les `AvatarRenderState` produits par
EasyNPC. Le test observait le mauvais moteur de rendu, des accessoires non appelés
et des proportions incorrectes. Le module redirige seulement les états portant
son propre profil Nexus vers leur véritable rendu EasyNPC. Les modèles de joueurs
ordinaires et les PNJ sans ce profil ne sont pas redirigés.

## Coût CPU : limite importante

Mesure sur 8 véritables entités clientes EasyNPC, avec 1, 4 puis 8 entités préparées
par passage. Trois échantillons de 16 passages, après échauffement. Les poses de
marche sont pilotées dans leurs états de rendu pour provoquer des poses nouvelles.
Ce ne sont ni des FPS mesurés, ni un profil Spark du serveur, ni un test d'IA en combat.

| Pose | PNJ | Médiane CPU par passage groupé | Allocation médiane par passage |
| --- | ---: | ---: | ---: |
| Statique | 1 | 0,393 ms | 0,010 Mio |
| Statique | 4 | 2,005 ms | 0,040 Mio |
| Statique | 8 | 7,557 ms | 0,081 Mio |
| Marche, nouvelles poses | 1 | 28,610 ms | 11,061 Mio |
| Marche, nouvelles poses | 4 | 101,476 ms | 40,704 Mio |
| Marche, nouvelles poses | 8 | 314,052 ms | 81,538 Mio |

Périmètre : extraction des états et préparation du modèle natif seulement.
GPU, monde, soumission des accessoires et coût total d'une image sont exclus.
Ces chiffres proviennent du processeur de l'environnement de test, avec d'autres
essais en cours ; ils ne prédisent pas les FPS du PC d'Arthur. Ils montrent néanmoins
un coût et une allocation élevés lors des changements de pose. Aucun gain par
rapport au pack complet de production n'est revendiqué. L'optimisation de foule
reste à traiter et à profiler séparément sans supprimer le relief demandé.

## Points non verrouillés

- Serveur dédié Nitroserv et pack de production complet : pas de lancement dans cette session. Tests serveur réalisés avec le serveur intégré d'une instance isolée.
- Framerate de Lion-Port, canalisation de sorts, caméra de dialogue et plusieurs joueurs réels : non mesurés par ce prototype.
- Shaders actifs, armures et animations de tous les autres mods : non couverts exhaustivement.
- Tous les assets possibles et toutes les poses : pas de garantie visuelle universelle. Les nouveaux assets doivent respecter le catalogue et les UV Nexus.
- Coffre joueur Nexus : les comptes synthétiques de test n'ont pas de manifeste propriétaire complet. Les logs incluent l'erreur de snapshot partiel correspondante, ainsi que des avertissements hors ligne et de traduction. La persistance complète des personnages joueurs n'est pas validée ici ; aucun correctif de coffre n'est inclus.

Conclusion : les cinq presets et leur passerelle d'édition/import sont utilisables
pour une vérification visuelle sur une copie. Cette préversion n'est pas une
version « optimisation terminée » à déployer en foule sur le serveur.
