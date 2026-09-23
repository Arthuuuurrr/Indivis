Capitale Core — beta 1.5.5 STABLE

Socle mécanique validé avant la future phase 1.6.

Versions associées recommandées :
- Core : capitale_core_beta_1_5_5_STABLE_hotfix_easynpc_conversations_only.zip
- Monnaie : capitale_currency_fabric_alpha_0_1_6_default_styled_name_1_21_11.jar
- CapSkills : capitale_skills_COMPLET_DATA_ASSETS_0_8_29e_TRIGGER_DESCRIPTIONS_HOOKS_CLARITY_VALIDATED.zip
- Items CapSkills : capitale_skills_items_fabric_alpha_1_1_6_advanced_hooks_fabric_api_required_1_21_11.jar

Points validés :
- monnaie impériale basée sur l’item brut capitale_currency:martin_dor ;
- nom/lore/couleur de la monnaie portés par le mod, pas par des composants ajoutés par le datapack ;
- récompenses de quêtes et fonctions de give stackables avec /give @s capitale_currency:martin_dor ;
- ancienne monnaie poisonous_potato conservée uniquement dans les fonctions legacy/conversion ;
- Lanternes du Globe intégrées comme vraie quête journalière ;
- menus de patrouilles avec UUID EasyNPC copiables ;
- routes inactives visibles dans les menus ;
- presets de PNJ d’habillage séparés pour Profondeurs intérieures et Profondeurs extérieures ;
- tick patrouilles déjà nettoyé par fusion des wrappers de route ;
- compatibilité maintenue avec les fonctions EasyNPC existantes.

Menus utiles :
- /function capitale:admin/menu_self
- /function capitale:admin/test/socle155_self
- /function capitale:currency/admin/menu_self
- /function capitale:admin/patrol/menu
- /function capitale:admin/test/capskills_mod_items_self

Notes de prudence :
- ne garder qu’un seul datapack capitale_core actif ;
- ne pas garder d’ancien dossier décompressé capitale_core/ en parallèle du zip ;
- ne pas donner les Martins d’Or avec custom_name/lore depuis le core : le rendu vient du mod currency ;
- beaucoup de fonctions apparemment non référencées sont des points d’entrée EasyNPC/manuels et ne doivent pas être supprimées sans audit en jeu.

Voir : docs/CAPITALE_CORE_1_5_5_STABLE_AUDIT.txt


## 1.5.5 stable — hotfix daily cooldowns

Correction des cooldowns journaliers : les quêtes quotidiennes utilisent désormais une horloge serveur `CAP_TIME_SEC` et des échéances `CAP_CD_*_END`. Les cooldowns ne dépendent plus uniquement du temps passé en ligne par le joueur. Limite vanilla : si le serveur est arrêté ou mis en pause lorsqu’il est vide, le datapack ne peut pas connaître le temps réel écoulé hors tick serveur.


## Hotfix Ombre / arrangements — CapSkills 0.8.29e

Le core exploite désormais les tags fournis par CapSkills 0.8.29e :
- `capskills.ombre.bribe.r1` : +5 % aux arrangements/corruption légère.
- `capskills.ombre.bribe.r2` : +10 % total aux arrangements/corruption légère.

Le bonus n'est pas cumulatif entre r1 et r2 et reste volontairement inférieur à ce qui pourra être donné plus tard par une vraie progression de guilde des voleurs. Sur les arrangements judiciaires, le bonus Ombre ne s'applique qu'aux dossiers mineurs (`CAP_CTX_MODE 30`) et jamais aux dossiers graves 40+.


## Hotfix menu CapSkills 0.8.29e

Aucun changement mécanique core : les fonctions core → CapSkills déjà appelées existent toujours en 0.8.29e.
Mise à jour des textes de menus/docs visibles vers :
- CapSkills 0.8.29e ADVANCED_COMBAT_ARTISANAT_VALIDATED ;
- capitale_skills_items 1.1.6 advanced-hooks.

Le core continue d'utiliser les hooks Ombre bribe r1/r2 déjà présents et ne modifie pas la monnaie.

## Hotfix InfoRP triggers/menu

- InfoRP étendu : `/trigger InfoRP set 1..11`.
- Dossier RP rendu plus lisible avec sections en tirets : identité, justice, réputations, quêtes, actions rapides, détails.
- Ajout de pages de détail : identité, justice, réputations, accès, guidage, plans/portails, aide triggers.
- Aucun changement mécanique sur la monnaie, les quêtes, les gardes ou CapSkills.


Hotfix commerce EasyNPC
-----------------------
Les achats directs par /trigger ShopChoix sont désactivés pour les boutiques principales.
Les fonctions marchandes affichent désormais des dialogues/catalogues indicatifs ; les achats réels sont à configurer dans EasyNPC via Open Trading Menu.
Voir docs/EASYNPC_TRADING_CONVERSATIONS_1_5_5.txt.

## Hotfix EasyNPC conversations only

Les boutiques classiques n’affichent plus de catalogue joueur en chat. Les fonctions d’interaction lancent une conversation RP courte avec options : Ouvrir la boutique / Discuter / Partir. L’ouverture réelle de la boutique doit être faite dans EasyNPC par le bouton `Open Trading Menu`. Le libraire reste distinct car il combine boutique et quête Divers « Un exemplaire à remettre ».


## Hotfix profils / vétéran player target
- Ajout d'une entrée EasyNPC conseillée pour le greffier des profils : `capitale:profiles/registrar_interact_self`.
- Ajout d'une entrée joueur pour le vétéran bière : `capitale:quest/journalieres/biere_veteran/interact_player`.
- Documentation : `docs/PNJ_PROFILS_ET_VETERAN_PLAYER_TARGET_1_5_5.txt`.

HOTFIX — VENDEURS DIVERSIFIÉS EASYNPC
Ajout de fonctions de conversation natives pour agriculteur, boucher, marchand d'artefacts CapSkills, apothicaire, quincaillier et cartographe. Les boutiques doivent toujours utiliser l’action EasyNPC Open Trading Menu ; le datapack ne simule pas l’ouverture du menu de commerce.

## 1.5.6 RC5 — nettoyage gardes statiques
- Suppression des fonctions temporaires/obsolètes créées pendant l'itération RC2/RC3 : context_debug_self, create_anchor_self, setup_current_npc_self, clear_all_anchors.
- Suppression des anciens guides RC2/RC3 qui documentaient ces fonctions.
- Conservation du flux RC4 propre : menu admin de tag/ancrage proche + bulk des PNJ tagués + reset actifs.
- Aucune logique mécanique principale retirée.


## 1.5.6 RC8 — optimisation légère serveur

Base : RC7. Optimisations sans changement mécanique : anti-spawn hostile toutes les 5 secondes, préfiltre zone protégée resserré, gardes statiques defend/reset phasés toutes les 5 ticks.


## RC9d — Profile Persistence Policy Sync
Voir README_CORE_1_5_6_RC9D_PROFILE_PERSISTENCE_POLICY.txt et data/capitale/profile_persistence/policy.json.


RC9f — Marchand d'artefacts + retour Aurèle
- Remplace l’ancien libellé court du vendeur CapSkills par 'Marchand d'artefacts'.
- Les chemins internes capitale:npc/artefactier_capskills/* sont conservés pour compatibilité EasyNPC.
- Conserve la correction du retour progressif d’Aurèle après le Cercle marchand.
